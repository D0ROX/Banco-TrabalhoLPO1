package banco.controller;

import banco.model.ClienteModel;
import banco.model.ContaModel;

import java.util.*;

public class GerenciadorBancoControler {
    private List<ClienteModel> clientes;
    private Map<String, ContaModel> contas;

    public GerenciadorBancoControler() {
        clientes = new ArrayList<>();
        contas = new HashMap<>();
    }

    private String normalizar(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    public boolean existeCpf(String cpf) {
        String alvo = normalizar(cpf);
        for (ClienteModel c : clientes) {
            if (normalizar(c.getCpf()).equals(alvo)) return true;
        }
        return false;
    }

    public boolean existeRg(String rg) {
        String alvo = normalizar(rg);
        for (ClienteModel c : clientes) {
            if (normalizar(c.getRg()).equals(alvo)) return true;
        }
        return false;
    }

    public boolean existeNomeSobrenome(String nome, String sobrenome) {
        String n = normalizar(nome);
        String s = normalizar(sobrenome);
        for (ClienteModel c : clientes) {
            if (normalizar(c.getNome()).equals(n) && normalizar(c.getSobrenome()).equals(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean existeCpfEmOutro(String cpf, ClienteModel ignorar) {
        String alvo = normalizar(cpf);
        for (ClienteModel c : clientes) {
            if (c != ignorar && normalizar(c.getCpf()).equals(alvo)) return true;
        }
        return false;
    }

    public boolean existeRgEmOutro(String rg, ClienteModel ignorar) {
        String alvo = normalizar(rg);
        for (ClienteModel c : clientes) {
            if (c != ignorar && normalizar(c.getRg()).equals(alvo)) return true;
        }
        return false;
    }

    public boolean existeNomeSobrenomeEmOutro(String nome, String sobrenome, ClienteModel ignorar) {
        String n = normalizar(nome);
        String s = normalizar(sobrenome);
        for (ClienteModel c : clientes) {
            if (c != ignorar && normalizar(c.getNome()).equals(n) && normalizar(c.getSobrenome()).equals(s)) {
                return true;
            }
        }
        return false;
    }

    public void adicionarCliente(ClienteModel cliente) {
        clientes.add(cliente);
    }

    public void removerCliente(ClienteModel cliente) {
        clientes.remove(cliente);
        contas.remove(cliente.getCpf());
    }

    public void atualizarCliente(ClienteModel clienteAntigo, ClienteModel clienteNovo) {
        int indice = clientes.indexOf(clienteAntigo);
        if (indice >= 0) {
            clientes.set(indice, clienteNovo);
            if (contas.containsKey(clienteAntigo.getCpf())) {
                ContaModel conta = contas.remove(clienteAntigo.getCpf());
                contas.put(clienteNovo.getCpf(), conta);
            }
        }
    }

    public List<ClienteModel> getClientes() {
        return clientes;
    }

    public List<ClienteModel> buscarPorNome(String nome) {
        List<ClienteModel> resultado = new ArrayList<>();
        for (ClienteModel c : clientes) {
            if (c.getNome().toLowerCase().contains(nome.toLowerCase())) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public List<ClienteModel> buscarPorSobrenome(String sobrenome) {
        List<ClienteModel> resultado = new ArrayList<>();
        for (ClienteModel c : clientes) {
            if (c.getSobrenome().toLowerCase().contains(sobrenome.toLowerCase())) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public ClienteModel buscarPorRg(String rg) {
        for (ClienteModel c : clientes) {
            if (c.getRg().equals(rg)) {
                return c;
            }
        }
        return null;
    }

    public ClienteModel buscarPorCpf(String cpf) {
        for (ClienteModel c : clientes) {
            if (c.getCpf().equals(cpf)) {
                return c;
            }
        }
        return null;
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

    public void adicionarConta(String cpf, ContaModel conta) {
        contas.put(cpf, conta);
    }

    public ContaModel buscarConta(String cpf) {
        return contas.get(cpf);
    }

    public boolean clienteTemConta(String cpf) {
        return contas.containsKey(cpf);
    }
}
