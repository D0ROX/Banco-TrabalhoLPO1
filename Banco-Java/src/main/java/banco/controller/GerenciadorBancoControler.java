package banco.controller;

import banco.dao.ClienteDAO;
import banco.dao.ClienteDAOImpl;
import banco.dao.ContaDAO;
import banco.dao.ContaDAOImpl;
import banco.model.ClienteModel;
import banco.model.ContaCorrenteModel;
import banco.model.ContaInvestimentoModel;
import banco.model.ContaModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GerenciadorBancoControler {
    private final ClienteDAO clienteDAO;
    private final ContaDAO contaDAO;
    private List<ClienteModel> clientes;
    private Map<String, ContaModel> contas;

    public GerenciadorBancoControler() {
        this(new ClienteDAOImpl(), new ContaDAOImpl());
    }

    public GerenciadorBancoControler(ClienteDAO clienteDAO, ContaDAO contaDAO) {
        this.clienteDAO = clienteDAO;
        this.contaDAO = contaDAO;
        carregarDados();
    }

    private void carregarDados() {
        try {
            clientes = new ArrayList<>(clienteDAO.listarTodos());
            contas = new HashMap<>(contaDAO.listarTodasPorCpf());
        } catch (SQLException e) {
            throw erroBanco(e);
        }
    }

    private RuntimeException erroBanco(SQLException e) {
        return new IllegalStateException("Erro ao acessar o banco de dados.", e);
    }

    private String normalizar(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase();
    }

    public boolean existeCpf(String cpf) {
        String alvo = normalizar(cpf);
        return clientes.stream().anyMatch(cliente -> normalizar(cliente.getCpf()).equals(alvo));
    }

    public boolean existeRg(String rg) {
        String alvo = normalizar(rg);
        return clientes.stream().anyMatch(cliente -> normalizar(cliente.getRg()).equals(alvo));
    }

    public boolean existeNomeSobrenome(String nome, String sobrenome) {
        String nomeNormalizado = normalizar(nome);
        String sobrenomeNormalizado = normalizar(sobrenome);
        return clientes.stream().anyMatch(cliente ->
                normalizar(cliente.getNome()).equals(nomeNormalizado)
                        && normalizar(cliente.getSobrenome()).equals(sobrenomeNormalizado));
    }

    public boolean existeCpfEmOutro(String cpf, ClienteModel ignorar) {
        String alvo = normalizar(cpf);
        return clientes.stream().anyMatch(cliente ->
                cliente != ignorar && normalizar(cliente.getCpf()).equals(alvo));
    }

    public boolean existeRgEmOutro(String rg, ClienteModel ignorar) {
        String alvo = normalizar(rg);
        return clientes.stream().anyMatch(cliente ->
                cliente != ignorar && normalizar(cliente.getRg()).equals(alvo));
    }

    public boolean existeNomeSobrenomeEmOutro(String nome, String sobrenome, ClienteModel ignorar) {
        String nomeNormalizado = normalizar(nome);
        String sobrenomeNormalizado = normalizar(sobrenome);
        return clientes.stream().anyMatch(cliente ->
                cliente != ignorar
                        && normalizar(cliente.getNome()).equals(nomeNormalizado)
                        && normalizar(cliente.getSobrenome()).equals(sobrenomeNormalizado));
    }

    public void adicionarCliente(ClienteModel cliente) {
        try {
            if (clienteDAO.salvar(cliente)) {
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            throw erroBanco(e);
        }
    }

    public void removerCliente(ClienteModel cliente) {
        try {
            if (clienteDAO.excluirPorCpf(cliente.getCpf())) {
                clientes.remove(cliente);
                contas.remove(cliente.getCpf());
            }
        } catch (SQLException e) {
            throw erroBanco(e);
        }
    }

    public void atualizarCliente(ClienteModel clienteAntigo, ClienteModel clienteNovo) {
        try {
            if (!clienteDAO.atualizar(clienteAntigo.getCpf(), clienteNovo)) {
                return;
            }

            int indice = clientes.indexOf(clienteAntigo);
            if (indice >= 0) {
                clientes.set(indice, clienteNovo);
            }

            ContaModel conta = contas.remove(clienteAntigo.getCpf());
            if (conta != null) {
                conta.setDono(clienteNovo);
                contas.put(clienteNovo.getCpf(), conta);
            }
        } catch (SQLException e) {
            throw erroBanco(e);
        }
    }

    public List<ClienteModel> getClientes() {
        return clientes;
    }

    public List<ClienteModel> buscarPorNome(String nome) {
        try {
            return clienteDAO.buscarPorNome(nome);
        } catch (SQLException e) {
            throw erroBanco(e);
        }
    }

    public List<ClienteModel> buscarPorSobrenome(String sobrenome) {
        try {
            return clienteDAO.buscarPorSobrenome(sobrenome);
        } catch (SQLException e) {
            throw erroBanco(e);
        }
    }

    public ClienteModel buscarPorRg(String rg) {
        try {
            return clienteDAO.buscarPorRg(rg).orElse(null);
        } catch (SQLException e) {
            throw erroBanco(e);
        }
    }

    public ClienteModel buscarPorCpf(String cpf) {
        try {
            return clienteDAO.buscarPorCpf(cpf).orElse(null);
        } catch (SQLException e) {
            throw erroBanco(e);
        }
    }

    public List<ClienteModel> ordenarPorNome() {
        List<ClienteModel> ordenados = new ArrayList<>(clientes);
        Collections.sort(ordenados);
        return ordenados;
    }

    public List<ClienteModel> ordenarPorSobrenome() {
        List<ClienteModel> ordenados = new ArrayList<>(clientes);
        ordenados.sort(Comparator.comparing(ClienteModel::getSobrenome, String.CASE_INSENSITIVE_ORDER));
        return ordenados;
    }

    public List<ClienteModel> ordenarPorSaldo() {
        List<ClienteModel> ordenados = new ArrayList<>(clientes);
        ordenados.sort((c1, c2) -> Double.compare(saldoCliente(c2), saldoCliente(c1)));
        return ordenados;
    }

    private double saldoCliente(ClienteModel cliente) {
        ContaModel conta = contas.get(cliente.getCpf());
        return conta == null ? 0.0 : conta.getSaldo();
    }

    private void adicionarConta(String cpf, ContaModel conta) {
        try {
            if (contaDAO.salvar(conta)) {
                contas.put(cpf, conta);
            }
        } catch (SQLException e) {
            throw erroBanco(e);
        }
    }

    public ContaModel criarContaCorrente(ClienteModel cliente, double depositoInicial, double limite) {
        if (depositoInicial <= 0 || limite < 0) {
            throw new IllegalArgumentException("Informe depósito inicial positivo e limite maior ou igual a zero.");
        }

        ContaModel conta = new ContaCorrenteModel(cliente, depositoInicial, limite);
        adicionarConta(cliente.getCpf(), conta);
        return conta;
    }

    public ContaModel criarContaInvestimento(ClienteModel cliente, double depositoInicial,
                                             double montanteMinimo, double depositoMinimo) {
        if (montanteMinimo < 0 || depositoMinimo <= 0 || depositoInicial < depositoMinimo) {
            throw new IllegalArgumentException(
                    "Informe montante mínimo válido e depósito inicial maior ou igual ao depósito mínimo.");
        }

        ContaModel conta = new ContaInvestimentoModel(
                cliente,
                depositoInicial,
                montanteMinimo,
                depositoMinimo
        );
        adicionarConta(cliente.getCpf(), conta);
        return conta;
    }

    public ContaModel buscarConta(String cpf) {
        return contas.get(cpf);
    }

    public boolean clienteTemConta(String cpf) {
        return contas.containsKey(cpf);
    }

    public boolean depositar(String cpf, double valor) {
        ContaModel conta = contas.get(cpf);
        if (conta == null || !conta.deposita(valor)) {
            return false;
        }
        atualizarConta(conta);
        return true;
    }

    public boolean sacar(String cpf, double valor) {
        ContaModel conta = contas.get(cpf);
        if (conta == null || !conta.saca(valor)) {
            return false;
        }
        atualizarConta(conta);
        return true;
    }

    public boolean remunerar(String cpf) {
        ContaModel conta = contas.get(cpf);
        if (conta == null) {
            return false;
        }
        conta.remunera();
        atualizarConta(conta);
        return true;
    }

    private void atualizarConta(ContaModel conta) {
        try {
            contaDAO.atualizar(conta);
        } catch (SQLException e) {
            throw erroBanco(e);
        }
    }
}
