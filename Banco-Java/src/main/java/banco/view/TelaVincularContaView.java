package banco.view;

import banco.controller.GerenciadorBancoControler;
import banco.model.ClienteModel;
import banco.model.ContaModel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class TelaVincularContaView extends JFrame {
    private final GerenciadorBancoControler banco;
    private JComboBox<ClienteModel> cbClientes;
    private JComboBox<String> cbTipoConta;
    private JPanel painelCamposConta;
    private JTextField txtDepositoInicial;
    private JTextField txtLimite;
    private JTextField txtMontanteMinimo;
    private JTextField txtDepositoMinimo;

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
        for (ClienteModel cliente : banco.getClientes()) {
            cbClientes.addItem(cliente);
        }
    }

    private void atualizarCamposConta() {
        painelCamposConta.removeAll();

        String tipoConta = (String) cbTipoConta.getSelectedItem();

        painelCamposConta.add(new JLabel("Deposito Inicial (R$):"));
        txtDepositoInicial = new JTextField();
        painelCamposConta.add(txtDepositoInicial);

        if ("Conta Corrente".equals(tipoConta)) {
            painelCamposConta.add(new JLabel("Limite (R$):"));
            txtLimite = new JTextField();
            painelCamposConta.add(txtLimite);
        } else {
            painelCamposConta.add(new JLabel("Montante Minimo (R$):"));
            txtMontanteMinimo = new JTextField();
            painelCamposConta.add(txtMontanteMinimo);

            painelCamposConta.add(new JLabel("Deposito Minimo (R$):"));
            txtDepositoMinimo = new JTextField();
            painelCamposConta.add(txtDepositoMinimo);
        }

        painelCamposConta.revalidate();
        painelCamposConta.repaint();
    }

    private double parseDouble(String valor) {
        return Double.parseDouble(valor.trim().replace(",", "."));
    }

    private void criarConta() {
        ClienteModel clienteSelecionado = (ClienteModel) cbClientes.getSelectedItem();

        if (clienteSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Nenhum cliente disponivel!");
            return;
        }

        if (banco.clienteTemConta(clienteSelecionado.getCpf())) {
            JOptionPane.showMessageDialog(this, "Este cliente ja possui uma conta vinculada!");
            return;
        }

        try {
            ContaModel novaConta = criarContaSelecionada(clienteSelecionado);
            JOptionPane.showMessageDialog(this,
                    "Conta criada com sucesso!\nNumero da conta: " + novaConta.getNumero());
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro: digite valores numericos validos. Use ponto ou virgula.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao acessar o banco de dados.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private ContaModel criarContaSelecionada(ClienteModel clienteSelecionado) {
        String tipoConta = (String) cbTipoConta.getSelectedItem();
        double depositoInicial = parseDouble(txtDepositoInicial.getText());

        if ("Conta Corrente".equals(tipoConta)) {
            double limite = parseDouble(txtLimite.getText());
            return banco.criarContaCorrente(clienteSelecionado, depositoInicial, limite);
        }

        double montanteMinimo = parseDouble(txtMontanteMinimo.getText());
        double depositoMinimo = parseDouble(txtDepositoMinimo.getText());
        return banco.criarContaInvestimento(
                clienteSelecionado,
                depositoInicial,
                montanteMinimo,
                depositoMinimo
        );
    }
}
