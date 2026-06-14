package banco.view;

import banco.controller.GerenciadorBancoControler;
import banco.model.ClienteModel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class TelaClientesView extends JFrame {
    private final GerenciadorBancoControler banco;

    private JTable tabela;
    private ModeloTabelaClienteView modelo;
    private JTextField txtNome;
    private JTextField txtSobrenome;
    private JTextField txtRg;
    private JTextField txtCpf;
    private JTextField txtEndereco;
    private JTextField txtBusca;
    private JComboBox<String> cbTipoBusca;
    private JComboBox<String> cbOrdenacao;

    public TelaClientesView(GerenciadorBancoControler banco) {
        this.banco = banco;

        setTitle("Gerenciar Clientes");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        painelPrincipal.add(criarPainelFormulario(), BorderLayout.NORTH);
        painelPrincipal.add(criarPainelTabela(), BorderLayout.CENTER);
        painelPrincipal.add(criarPainelBusca(), BorderLayout.SOUTH);

        setContentPane(painelPrincipal);
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Dados do Cliente"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtNome = new JTextField(15);
        txtSobrenome = new JTextField(15);
        txtRg = new JTextField(15);
        txtCpf = new JTextField(15);
        txtEndereco = new JTextField(30);

        adicionarCampo(painel, gbc, "Nome:", txtNome, 0, 0, 1);
        adicionarCampo(painel, gbc, "Sobrenome:", txtSobrenome, 2, 0, 1);
        adicionarCampo(painel, gbc, "RG:", txtRg, 0, 1, 1);
        adicionarCampo(painel, gbc, "CPF:", txtCpf, 2, 1, 1);
        adicionarCampo(painel, gbc, "Endereco:", txtEndereco, 0, 2, 3);

        gbc.gridwidth = 1;
        gbc.gridy = 3;

        JButton btnAdicionar = new JButton("Adicionar");
        btnAdicionar.addActionListener(e -> adicionarCliente());
        gbc.gridx = 0;
        painel.add(btnAdicionar, gbc);

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> atualizarCliente());
        gbc.gridx = 1;
        painel.add(btnAtualizar, gbc);

        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirCliente());
        gbc.gridx = 2;
        painel.add(btnExcluir, gbc);

        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.addActionListener(e -> limparCampos());
        gbc.gridx = 3;
        painel.add(btnLimpar, gbc);

        return painel;
    }

    private void adicionarCampo(JPanel painel, GridBagConstraints gbc, String rotulo,
                                JTextField campo, int coluna, int linha, int larguraCampo) {
        gbc.gridx = coluna;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        painel.add(new JLabel(rotulo), gbc);

        gbc.gridx = coluna + 1;
        gbc.gridwidth = larguraCampo;
        painel.add(campo, gbc);
    }

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Lista de Clientes"));

        modelo = new ModeloTabelaClienteView(banco.getClientes());
        tabela = new JTable(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() >= 0) {
                preencherFormulario(tabela.getSelectedRow());
            }
        });

        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelBusca() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painel.setBorder(BorderFactory.createTitledBorder("Busca e Ordenacao"));

        painel.add(new JLabel("Buscar por:"));
        cbTipoBusca = new JComboBox<>(new String[]{"Nome", "Sobrenome", "RG", "CPF"});
        painel.add(cbTipoBusca);

        txtBusca = new JTextField(20);
        painel.add(txtBusca);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarClientes());
        painel.add(btnBuscar);

        JButton btnMostrarTodos = new JButton("Mostrar Todos");
        btnMostrarTodos.addActionListener(e -> atualizarTabela(banco.getClientes()));
        painel.add(btnMostrarTodos);

        painel.add(new JLabel(" | Ordenar por:"));
        cbOrdenacao = new JComboBox<>(new String[]{"Nome", "Sobrenome", "Saldo"});
        painel.add(cbOrdenacao);

        JButton btnOrdenar = new JButton("Ordenar");
        btnOrdenar.addActionListener(e -> ordenarClientes());
        painel.add(btnOrdenar);

        return painel;
    }

    private void adicionarCliente() {
        if (!validarCampos()) {
            return;
        }

        String nome = txtNome.getText().trim();
        String sobrenome = txtSobrenome.getText().trim();
        String rg = txtRg.getText().trim();
        String cpf = txtCpf.getText().trim();
        String endereco = txtEndereco.getText().trim();

        if (banco.existeNomeSobrenome(nome, sobrenome)) {
            JOptionPane.showMessageDialog(this, "Ja existe cliente com o mesmo nome e sobrenome.");
            return;
        }
        if (banco.existeRg(rg)) {
            JOptionPane.showMessageDialog(this, "Ja existe cliente com o mesmo RG.");
            return;
        }
        if (banco.existeCpf(cpf)) {
            JOptionPane.showMessageDialog(this, "Ja existe cliente com o mesmo CPF.");
            return;
        }

        try {
            banco.adicionarCliente(new ClienteModel(nome, sobrenome, rg, cpf, endereco));
            atualizarTabela(banco.getClientes());
            limparCampos();
            JOptionPane.showMessageDialog(this, "Cliente adicionado com sucesso!");
        } catch (IllegalStateException ex) {
            mostrarErroBanco();
        }
    }

    private void atualizarCliente() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para atualizar!");
            return;
        }
        if (!validarCampos()) {
            return;
        }

        ClienteModel clienteAntigo = modelo.getCliente(linha);
        String nome = txtNome.getText().trim();
        String sobrenome = txtSobrenome.getText().trim();
        String rg = txtRg.getText().trim();
        String cpf = txtCpf.getText().trim();
        String endereco = txtEndereco.getText().trim();

        if (banco.existeNomeSobrenomeEmOutro(nome, sobrenome, clienteAntigo)) {
            JOptionPane.showMessageDialog(this, "Ja existe outro cliente com este nome.");
            return;
        }
        if (banco.existeRgEmOutro(rg, clienteAntigo)) {
            JOptionPane.showMessageDialog(this, "Ja existe outro cliente com este RG.");
            return;
        }
        if (banco.existeCpfEmOutro(cpf, clienteAntigo)) {
            JOptionPane.showMessageDialog(this, "Ja existe outro cliente com este CPF.");
            return;
        }

        try {
            ClienteModel clienteNovo = new ClienteModel(nome, sobrenome, rg, cpf, endereco);
            banco.atualizarCliente(clienteAntigo, clienteNovo);
            atualizarTabela(banco.getClientes());
            limparCampos();
            JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
        } catch (IllegalStateException ex) {
            mostrarErroBanco();
        }
    }

    private void excluirCliente() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir!");
            return;
        }

        ClienteModel cliente = modelo.getCliente(linha);
        int resposta = JOptionPane.showConfirmDialog(this,
                "Excluir este cliente?\nTodas as contas vinculadas serao apagadas.",
                "Confirmar Exclusao",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            banco.removerCliente(cliente);
            atualizarTabela(banco.getClientes());
            limparCampos();
            JOptionPane.showMessageDialog(this, "Cliente excluido com sucesso!");
        } catch (IllegalStateException ex) {
            mostrarErroBanco();
        }
    }

    private void buscarClientes() {
        String textoBusca = txtBusca.getText().trim();
        if (textoBusca.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite algo para buscar!");
            return;
        }

        try {
            List<ClienteModel> resultado = switch ((String) cbTipoBusca.getSelectedItem()) {
                case "Nome" -> banco.buscarPorNome(textoBusca);
                case "Sobrenome" -> banco.buscarPorSobrenome(textoBusca);
                case "RG" -> {
                    ClienteModel cliente = banco.buscarPorRg(textoBusca);
                    yield cliente == null ? List.of() : List.of(cliente);
                }
                case "CPF" -> {
                    ClienteModel cliente = banco.buscarPorCpf(textoBusca);
                    yield cliente == null ? List.of() : List.of(cliente);
                }
                default -> banco.getClientes();
            };

            if (resultado.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum cliente encontrado!");
            }
            atualizarTabela(resultado);
        } catch (IllegalStateException ex) {
            mostrarErroBanco();
        }
    }

    private void ordenarClientes() {
        List<ClienteModel> ordenados = switch ((String) cbOrdenacao.getSelectedItem()) {
            case "Nome" -> banco.ordenarPorNome();
            case "Sobrenome" -> banco.ordenarPorSobrenome();
            default -> banco.ordenarPorSaldo();
        };
        atualizarTabela(ordenados);
    }

    private void preencherFormulario(int linha) {
        ClienteModel cliente = modelo.getCliente(linha);
        txtNome.setText(cliente.getNome());
        txtSobrenome.setText(cliente.getSobrenome());
        txtRg.setText(cliente.getRg());
        txtCpf.setText(cliente.getCpf());
        txtEndereco.setText(cliente.getEndereco());
    }

    private void limparCampos() {
        txtNome.setText("");
        txtSobrenome.setText("");
        txtRg.setText("");
        txtCpf.setText("");
        txtEndereco.setText("");
        tabela.clearSelection();
    }

    private boolean validarCampos() {
        if (txtNome.getText().trim().isEmpty()
                || txtSobrenome.getText().trim().isEmpty()
                || txtRg.getText().trim().isEmpty()
                || txtCpf.getText().trim().isEmpty()
                || txtEndereco.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos!");
            return false;
        }
        return true;
    }

    private void atualizarTabela(List<ClienteModel> clientes) {
        modelo.setListaClientes(clientes);
    }

    private void mostrarErroBanco() {
        JOptionPane.showMessageDialog(this,
                "Erro ao acessar o banco de dados.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
    }
}
