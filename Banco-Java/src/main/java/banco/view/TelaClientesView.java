package banco.view;

import banco.controller.GerenciadorBancoControler;
import banco.model.ClienteModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TelaClientesView extends JFrame {
    private final GerenciadorBancoControler banco;
    private JTable tabela;
    private ModeloTabelaClienteView modelo;
    private JTextField txtNome, txtSobrenome, txtRg, txtCpf, txtEndereco;
    private JTextField txtBusca;
    private JComboBox<String> cbTipoBusca, cbOrdenacao;

    private JButton btnAdicionar, btnAtualizar, btnExcluir, btnLimpar;
    private JButton btnBuscar, btnMostrarTodos, btnOrdenar;

    private JPanel painelFormulario;
    private java.awt.event.AWTEventListener listenerGlobal;

    public TelaClientesView(GerenciadorBancoControler banco) {
        this.banco = banco;

        setTitle("Gerenciar Clientes");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        painelFormulario = criarPainelFormulario();
        JPanel painelTabela = criarPainelTabela();
        JPanel painelBusca = criarPainelBusca();

        painelPrincipal.add(painelFormulario, BorderLayout.NORTH);
        painelPrincipal.add(painelTabela, BorderLayout.CENTER);
        painelPrincipal.add(painelBusca, BorderLayout.SOUTH);

        setContentPane(painelPrincipal);

        listenerGlobal = evento -> {
            if (!(evento instanceof java.awt.event.MouseEvent)) return;
            java.awt.event.MouseEvent me = (java.awt.event.MouseEvent) evento;
            if (me.getID() != java.awt.event.MouseEvent.MOUSE_PRESSED) return;

            Window janelaDoEvento = SwingUtilities.getWindowAncestor((Component) me.getSource());
            if (janelaDoEvento != this) return;

            Component origem = (Component) me.getSource();

            if (SwingUtilities.isDescendingFrom(origem, tabela)) return;

            if (SwingUtilities.isDescendingFrom(origem, painelFormulario)) return;

            if (origem instanceof JButton) return;
            Component atual = origem;
            while (atual != null) {
                if (atual instanceof JButton) return;
                atual = atual.getParent();
            }

            limparSelecaoTabela();
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(listenerGlobal, AWTEvent.MOUSE_EVENT_MASK);
    }

    @Override
    public void dispose() {
        if (listenerGlobal != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(listenerGlobal);
            listenerGlobal = null;
        }
        super.dispose();
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

        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        painel.add(txtNome, gbc);

        gbc.gridx = 2;
        painel.add(new JLabel("Sobrenome:"), gbc);
        gbc.gridx = 3;
        painel.add(txtSobrenome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(new JLabel("RG:"), gbc);
        gbc.gridx = 1;
        painel.add(txtRg, gbc);

        gbc.gridx = 2;
        painel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 3;
        painel.add(txtCpf, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        painel.add(new JLabel("Endereço:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        painel.add(txtEndereco, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        btnAdicionar = new JButton("Adicionar");
        btnAdicionar.addActionListener(e -> adicionarCliente());
        painel.add(btnAdicionar, gbc);

        gbc.gridx = 1;
        btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> atualizarCliente());
        painel.add(btnAtualizar, gbc);

        gbc.gridx = 2;
        btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirCliente());
        painel.add(btnExcluir, gbc);

        gbc.gridx = 3;
        btnLimpar = new JButton("Limpar");
        btnLimpar.addActionListener(e -> limparCampos());
        painel.add(btnLimpar, gbc);

        return painel;
    }

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Lista de Clientes"));

        modelo = new ModeloTabelaClienteView(banco.getClientes());
        tabela = new JTable(modelo);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int linha = tabela.getSelectedRow();
                if (linha != -1) {
                    preencherFormulario(linha);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabela);
        painel.add(scrollPane, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelBusca() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painel.setBorder(BorderFactory.createTitledBorder("Busca e Ordenação"));

        painel.add(new JLabel("Buscar por:"));
        cbTipoBusca = new JComboBox<>(new String[]{"Nome", "Sobrenome", "RG", "CPF"});
        painel.add(cbTipoBusca);

        txtBusca = new JTextField(20);
        painel.add(txtBusca);

        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarClientes());
        painel.add(btnBuscar);

        btnMostrarTodos = new JButton("Mostrar Todos");
        btnMostrarTodos.addActionListener(e -> atualizarTabela(banco.getClientes()));
        painel.add(btnMostrarTodos);

        painel.add(new JLabel(" | Ordenar por:"));
        cbOrdenacao = new JComboBox<>(new String[]{"Nome", "Sobrenome"});
        painel.add(cbOrdenacao);

        btnOrdenar = new JButton("Ordenar");
        btnOrdenar.addActionListener(e -> ordenarClientes());
        painel.add(btnOrdenar);

        return painel;
    }

    private void limparSelecaoTabela() {
        if (tabela != null) {
            tabela.clearSelection();
        }
    }

    private void adicionarCliente() {
        if (!validarCampos()) return;

        String nome = txtNome.getText();
        String sobrenome = txtSobrenome.getText();
        String rg = txtRg.getText();
        String cpf = txtCpf.getText();
        String endereco = txtEndereco.getText();

        if (banco.existeNomeSobrenome(nome, sobrenome)) {
            JOptionPane.showMessageDialog(this, "Já existe cliente com o mesmo Nome e Sobrenome.");
            return;
        }
        if (banco.existeRg(rg)) {
            JOptionPane.showMessageDialog(this, "Já existe cliente com o mesmo RG.");
            return;
        }
        if (banco.existeCpf(cpf)) {
            JOptionPane.showMessageDialog(this, "Já existe cliente com o mesmo CPF.");
            return;
        }

        ClienteModel cliente = new ClienteModel(nome, sobrenome, rg, cpf, endereco);
        banco.adicionarCliente(cliente);
        atualizarTabela(banco.getClientes());
        limparCampos();
        JOptionPane.showMessageDialog(this, "Cliente adicionado com sucesso!");
    }

    private void atualizarCliente() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para atualizar!");
            return;
        }
        if (!validarCampos()) return;

        ClienteModel clienteAntigo = modelo.getCliente(linha);

        String nome = txtNome.getText();
        String sobrenome = txtSobrenome.getText();
        String rg = txtRg.getText();
        String cpf = txtCpf.getText();
        String endereco = txtEndereco.getText();

        if (banco.existeNomeSobrenomeEmOutro(nome, sobrenome, clienteAntigo)) {
            JOptionPane.showMessageDialog(this, "Já existe outro cliente com este nome.");
            return;
        }
        if (banco.existeRgEmOutro(rg, clienteAntigo)) {
            JOptionPane.showMessageDialog(this, "Já existe outro cliente com este RG.");
            return;
        }
        if (banco.existeCpfEmOutro(cpf, clienteAntigo)) {
            JOptionPane.showMessageDialog(this, "Já existe outro cliente com este CPF.");
            return;
        }

        ClienteModel clienteNovo = new ClienteModel(nome, sobrenome, rg, cpf, endereco);
        banco.atualizarCliente(clienteAntigo, clienteNovo);
        atualizarTabela(banco.getClientes());
        limparCampos();
        JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
    }

    private void excluirCliente() {
        int[] linhas = tabela.getSelectedRows();
        if (linhas == null || linhas.length == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um ou mais clientes para excluir!");
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(this,
                "Excluir " + linhas.length + " cliente(s)?\nContas vinculadas serão removidas.",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (resposta != JOptionPane.YES_OPTION) return;

        List<ClienteModel> aRemover = new ArrayList<>();
        for (int linha : linhas) {
            aRemover.add(modelo.getCliente(linha));
        }
        for (ClienteModel c : aRemover) {
            banco.removerCliente(c);
        }

        atualizarTabela(banco.getClientes());
        limparCampos();
        JOptionPane.showMessageDialog(this, "Exclusão concluída!");
    }

    private void buscarClientes() {
        String textoBusca = txtBusca.getText().trim();
        if (textoBusca.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite algo para buscar!");
            return;
        }

        String tipoBusca = (String) cbTipoBusca.getSelectedItem();
        List<ClienteModel> resultado;

        switch (tipoBusca) {
            case "Nome":
                resultado = banco.buscarPorNome(textoBusca);
                break;
            case "Sobrenome":
                resultado = banco.buscarPorSobrenome(textoBusca);
                break;
            case "RG":
                ClienteModel c1 = banco.buscarPorRg(textoBusca);
                resultado = c1 != null ? List.of(c1) : List.of();
                break;
            case "CPF":
                ClienteModel c2 = banco.buscarPorCpf(textoBusca);
                resultado = c2 != null ? List.of(c2) : List.of();
                break;
            default:
                resultado = banco.getClientes();
        }

        if (resultado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum cliente encontrado!");
        }
        atualizarTabela(resultado);
    }

    private void ordenarClientes() {
        String tipoOrdenacao = (String) cbOrdenacao.getSelectedItem();
        List<ClienteModel> ordenados =
                "Nome".equals(tipoOrdenacao)
                        ? banco.ordenarPorNome()
                        : banco.ordenarPorSobrenome();
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
        if (tabela != null) {
            tabela.clearSelection();
        }
    }

    private boolean validarCampos() {
        if (txtNome.getText().trim().isEmpty() ||
                txtSobrenome.getText().trim().isEmpty() ||
                txtRg.getText().trim().isEmpty() ||
                txtCpf.getText().trim().isEmpty() ||
                txtEndereco.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos!");
            return false;
        }
        return true;
    }

    private void atualizarTabela(List<ClienteModel> clientes) {
        modelo.setListaClientes(clientes);
    }
}
