package banco.view;

import banco.controller.GerenciadorBancoControler;
import banco.model.ClienteModel;
import banco.model.ContaCorrenteModel;
import banco.model.ContaInvestimentoModel;
import banco.model.ContaModel;

import javax.swing.*;
import java.awt.*;

public class TelaVincularContaView extends JFrame {
    private GerenciadorBancoControler banco;
    private JComboBox<ClienteModel> cbClientes;
    private JComboBox<String> cbTipoConta;
    private JPanel painelCamposConta;
    private JTextField txtDepositoInicial, txtLimite, txtMontanteMinimo, txtDepositoMinimo;

    public TelaVincularContaView(GerenciadorBancoControler banco) {
        this.banco = banco;

        setTitle("Vincular Conta a Cliente");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel painelSelecao = new JPanel(new GridLayout(2, 2, 10, 10));

        painelSelecao.add(new JLabel("Cliente:"));
        cbClientes = new JComboBox<>();
        atualizarComboClientes();
        painelSelecao.add(cbClientes);

        painelSelecao.add(new JLabel("Tipo de Conta:"));
        cbTipoConta = new JComboBox<>(new String[]{"Conta Corrente", "Conta Investimento"});
        cbTipoConta.addActionListener(e -> atualizarCamposConta());
        painelSelecao.add(cbTipoConta);

        painelCamposConta = new JPanel(new GridLayout(3, 2, 10, 10));
        atualizarCamposConta();

        JPanel painelBotoes = new JPanel(new FlowLayout());
        JButton btnCriar = new JButton("Criar Conta");
        btnCriar.addActionListener(e -> criarConta());
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        painelBotoes.add(btnCriar);
        painelBotoes.add(btnCancelar);

        painelPrincipal.add(painelSelecao, BorderLayout.NORTH);
        painelPrincipal.add(painelCamposConta, BorderLayout.CENTER);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        setContentPane(painelPrincipal);
    }

    private void atualizarComboClientes() {
        cbClientes.removeAllItems();
        for (ClienteModel c : banco.getClientes()) {
            cbClientes.addItem(c);
        }
    }

    private void atualizarCamposConta() {
        painelCamposConta.removeAll();

        String tipoConta = (String) cbTipoConta.getSelectedItem();

        if ("Conta Corrente".equals(tipoConta)) {
            painelCamposConta.add(new JLabel("Depósito Inicial (R$):"));
            txtDepositoInicial = new JTextField();
            painelCamposConta.add(txtDepositoInicial);

            painelCamposConta.add(new JLabel("Limite (R$):"));
            txtLimite = new JTextField();
            painelCamposConta.add(txtLimite);
        } else {
            painelCamposConta.add(new JLabel("Depósito Inicial (R$):"));
            txtDepositoInicial = new JTextField();
            painelCamposConta.add(txtDepositoInicial);

            painelCamposConta.add(new JLabel("Montante Mínimo (R$):"));
            txtMontanteMinimo = new JTextField();
            painelCamposConta.add(txtMontanteMinimo);

            painelCamposConta.add(new JLabel("Depósito Mínimo (R$):"));
            txtDepositoMinimo = new JTextField();
            painelCamposConta.add(txtDepositoMinimo);
        }

        painelCamposConta.revalidate();
        painelCamposConta.repaint();
    }

    private double parseDouble(String valor) throws NumberFormatException {
        return Double.parseDouble(valor.trim().replace(",", "."));
    }

    private void criarConta() {
        ClienteModel clienteSelecionado = (ClienteModel) cbClientes.getSelectedItem();

        if (clienteSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Nenhum cliente disponível!");
            return;
        }

        if (banco.clienteTemConta(clienteSelecionado.getCpf())) {
            JOptionPane.showMessageDialog(this,
                    "Este cliente já possui uma conta vinculada!");
            return;
        }

        try {
            String tipoConta = (String) cbTipoConta.getSelectedItem();
            ContaModel novaConta;

            double depositoInicial = parseDouble(txtDepositoInicial.getText());

            if ("Conta Corrente".equals(tipoConta)) {
                double limite = parseDouble(txtLimite.getText());
                novaConta = new ContaCorrenteModel(clienteSelecionado, depositoInicial, limite);
            } else {
                double montanteMinimo = parseDouble(txtMontanteMinimo.getText());
                double depositoMinimo = parseDouble(txtDepositoMinimo.getText());
                novaConta = new ContaInvestimentoModel(clienteSelecionado, depositoInicial,
                        montanteMinimo, depositoMinimo);
            }

            banco.adicionarConta(clienteSelecionado.getCpf(), novaConta);
            JOptionPane.showMessageDialog(this,
                    "Conta criada com sucesso!\nNúmero da conta: " + novaConta.getNumero());
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro: Digite valores numéricos válidos! Use ponto ou vírgula.");
        }
    }
}
