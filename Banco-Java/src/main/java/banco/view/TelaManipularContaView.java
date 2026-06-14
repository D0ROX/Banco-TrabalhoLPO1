package banco.view;

import banco.controller.GerenciadorBancoControler;
import banco.model.ContaCorrenteModel;
import banco.model.ContaModel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class TelaManipularContaView extends JFrame {
    private final GerenciadorBancoControler banco;
    private JTextField txtCpf;
    private JTextField txtValor;
    private JLabel lblInfoConta;
    private ContaModel contaAtual;
    private String cpfContaAtual;

    public TelaManipularContaView(GerenciadorBancoControler banco) {
        this.banco = banco;

        setTitle("Manipular Conta");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel painelBusca = new JPanel(new FlowLayout());
        painelBusca.add(new JLabel("CPF do Cliente:"));
        txtCpf = new JTextField(15);
        painelBusca.add(txtCpf);

        JButton btnBuscar = new JButton("Buscar Conta");
        btnBuscar.addActionListener(e -> buscarConta());
        painelBusca.add(btnBuscar);

        JPanel painelInfo = new JPanel(new BorderLayout());
        painelInfo.setBorder(BorderFactory.createTitledBorder("Informacoes da Conta"));
        lblInfoConta = new JLabel("Nenhuma conta selecionada", SwingConstants.CENTER);
        lblInfoConta.setFont(new Font("Arial", Font.PLAIN, 12));
        lblInfoConta.setOpaque(true);

        JScrollPane scrollPane = new JScrollPane(lblInfoConta);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        painelInfo.add(scrollPane, BorderLayout.CENTER);

        JPanel painelOperacoes = new JPanel(new GridLayout(5, 1, 10, 10));
        painelOperacoes.setBorder(BorderFactory.createTitledBorder("Operacoes"));

        JPanel painelValor = new JPanel(new FlowLayout());
        painelValor.add(new JLabel("Valor (R$):"));
        txtValor = new JTextField(10);
        painelValor.add(txtValor);
        painelOperacoes.add(painelValor);

        JButton btnDeposito = new JButton("Depositar");
        btnDeposito.addActionListener(e -> realizarDeposito());
        painelOperacoes.add(btnDeposito);

        JButton btnSaque = new JButton("Sacar");
        btnSaque.addActionListener(e -> realizarSaque());
        painelOperacoes.add(btnSaque);

        JButton btnVerSaldo = new JButton("Ver Saldo");
        btnVerSaldo.addActionListener(e -> verSaldo());
        painelOperacoes.add(btnVerSaldo);

        JButton btnRemunerar = new JButton("Remunerar");
        btnRemunerar.addActionListener(e -> remunerar());
        painelOperacoes.add(btnRemunerar);

        painelPrincipal.add(painelBusca, BorderLayout.NORTH);
        painelPrincipal.add(painelInfo, BorderLayout.CENTER);
        painelPrincipal.add(painelOperacoes, BorderLayout.SOUTH);

        setContentPane(painelPrincipal);
    }

    private void buscarConta() {
        String cpf = txtCpf.getText().trim();
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um CPF!");
            return;
        }

        contaAtual = banco.buscarConta(cpf);
        cpfContaAtual = cpf;

        if (contaAtual == null) {
            JOptionPane.showMessageDialog(this,
                    "Nenhuma conta encontrada para este CPF.\nVerifique se o CPF esta correto.");
            lblInfoConta.setText("Nenhuma conta selecionada");
            lblInfoConta.setForeground(Color.RED);
            return;
        }

        atualizarInfoConta();
        lblInfoConta.setForeground(new Color(0, 100, 0));
        JOptionPane.showMessageDialog(this, "Conta selecionada com sucesso!");
    }

    private void atualizarInfoConta() {
        if (contaAtual == null) {
            return;
        }

        String info = "<html><center>" +
                "Cliente: " + contaAtual.getDono().getNome() + " " +
                contaAtual.getDono().getSobrenome() + "<br>" +
                contaAtual + "</center></html>";
        lblInfoConta.setText(info);
    }

    private double lerValor() {
        String texto = txtValor.getText().trim().replace(",", ".");
        if (texto.isEmpty()) {
            throw new NumberFormatException();
        }
        return Double.parseDouble(texto);
    }

    private void realizarDeposito() {
        if (!existeContaSelecionada()) {
            return;
        }

        try {
            double valor = lerValor();

            if (banco.depositar(cpfContaAtual, valor)) {
                atualizarContaSelecionada();
                JOptionPane.showMessageDialog(this, "Deposito realizado com sucesso!");
                txtValor.setText("");
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Erro: verifique as regras de deposito desta conta.\n" +
                            "Para Conta Investimento, o valor deve ser maior ou igual ao deposito minimo.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor invalido! Digite apenas numeros.");
        } catch (IllegalStateException ex) {
            mostrarErroBanco();
        }
    }

    private void realizarSaque() {
        if (!existeContaSelecionada()) {
            return;
        }

        try {
            double valor = lerValor();

            if (banco.sacar(cpfContaAtual, valor)) {
                atualizarContaSelecionada();
                JOptionPane.showMessageDialog(this, "Saque realizado com sucesso!");
                txtValor.setText("");
                return;
            }

            String mensagem = "Erro: saque nao permitido.\n";
            if (contaAtual instanceof ContaCorrenteModel) {
                mensagem += "Saldo insuficiente ou excede o limite.";
            } else {
                mensagem += "Saldo insuficiente ou viola o montante minimo.";
            }
            JOptionPane.showMessageDialog(this, mensagem);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor invalido! Digite apenas numeros.");
        } catch (IllegalStateException ex) {
            mostrarErroBanco();
        }
    }

    private void verSaldo() {
        if (!existeContaSelecionada()) {
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Saldo atual: R$ " + String.format("%.2f", contaAtual.getSaldo()));
    }

    private void remunerar() {
        if (!existeContaSelecionada()) {
            return;
        }

        try {
            double saldoAnterior = contaAtual.getSaldo();
            if (!banco.remunerar(cpfContaAtual)) {
                JOptionPane.showMessageDialog(this, "Nao foi possivel remunerar esta conta.");
                return;
            }

            atualizarContaSelecionada();
            double rendimento = contaAtual.getSaldo() - saldoAnterior;

            JOptionPane.showMessageDialog(this,
                    "Remuneracao aplicada com sucesso!\n" +
                            "Rendimento: R$ " + String.format("%.2f", rendimento) + "\n" +
                            "Novo saldo: R$ " + String.format("%.2f", contaAtual.getSaldo()));
        } catch (IllegalStateException ex) {
            mostrarErroBanco();
        }
    }

    private boolean existeContaSelecionada() {
        if (contaAtual != null) {
            return true;
        }

        JOptionPane.showMessageDialog(this, "ERRO: Selecione uma conta primeiro!");
        return false;
    }

    private void atualizarContaSelecionada() {
        contaAtual = banco.buscarConta(cpfContaAtual);
        atualizarInfoConta();
    }

    private void mostrarErroBanco() {
        JOptionPane.showMessageDialog(this,
                "Erro ao acessar o banco de dados.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
    }
}
