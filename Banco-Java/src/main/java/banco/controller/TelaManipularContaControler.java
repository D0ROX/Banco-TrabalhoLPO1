package banco.controller;

import banco.model.ContaCorrenteModel;
import banco.model.ContaModel;

import javax.swing.*;
import java.awt.*;

public class TelaManipularContaControler extends JFrame {
    private GerenciadorBancoControler banco;
    private JTextField txtCpf, txtValor;
    private JLabel lblInfoConta;
    private ContaModel contaAtual;

    public TelaManipularContaControler(GerenciadorBancoControler banco) {
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
        painelInfo.setBorder(BorderFactory.createTitledBorder("Informações da Conta"));
        lblInfoConta = new JLabel("Nenhuma conta selecionada", SwingConstants.CENTER);
        lblInfoConta.setFont(new Font("Arial", Font.PLAIN, 12));
        lblInfoConta.setOpaque(true);

        JScrollPane scrollPane = new JScrollPane(lblInfoConta);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false); //
        painelInfo.add(scrollPane, BorderLayout.CENTER);


        JPanel painelOperacoes = new JPanel(new GridLayout(5, 1, 10, 10));
        painelOperacoes.setBorder(BorderFactory.createTitledBorder("Operações"));

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

        if (contaAtual == null) {
            JOptionPane.showMessageDialog(this, "Nenhuma conta encontrada para este CPF!\nVerifique se o CPF está correto.");
            lblInfoConta.setText("Nenhuma conta selecionada");
            lblInfoConta.setForeground(Color.RED);
        } else {
            atualizarInfoConta();
            lblInfoConta.setForeground(new Color(0, 100, 0));
            JOptionPane.showMessageDialog(this, "Conta selecionada com sucesso!");
        }
    }

    private void atualizarInfoConta() {
        if (contaAtual != null) {
            String info = "<html><center>" +
                    "Cliente: " + contaAtual.getDono().getNome() + " " +
                    contaAtual.getDono().getSobrenome() + "<br>" +
                    contaAtual.toString() + "</center></html>";
            lblInfoConta.setText(info);
        }
    }

    private double lerValor() throws NumberFormatException {
        String texto = txtValor.getText().trim().replace(",", ".");
        if (texto.isEmpty()) throw new NumberFormatException();
        return Double.parseDouble(texto);
    }

    private void realizarDeposito() {
        if (contaAtual == null) {
            JOptionPane.showMessageDialog(this, "ERRO: Selecione uma conta primeiro!");
            return;
        }

        try {
            double valor = lerValor();

            if (contaAtual.deposita(valor)) {
                JOptionPane.showMessageDialog(this, "Depósito realizado com sucesso!");
                atualizarInfoConta();
                txtValor.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro: Verifique as regras de depósito desta conta.\n" +
                                "Para Conta Investimento, o valor deve ser maior ou igual ao depósito mínimo.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido! Digite apenas números.");
        }
    }

    private void realizarSaque() {
        if (contaAtual == null) {
            JOptionPane.showMessageDialog(this, "ERRO: Selecione uma conta primeiro!");
            return;
        }

        try {
            double valor = lerValor();

            if (!contaAtual.saca(valor)) {
                String mensagem = "Erro: Saque não permitido.\n";
                if (contaAtual instanceof ContaCorrenteModel) {
                    mensagem += "Saldo insuficiente ou excede o limite.";
                } else {
                    mensagem += "Saldo insuficiente ou viola o montante mínimo.";
                }
                JOptionPane.showMessageDialog(this, mensagem);
                return;
            }
            JOptionPane.showMessageDialog(this, "Saque realizado com sucesso!");
            atualizarInfoConta();
            txtValor.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido! Digite apenas números.");
        }
    }

    private void verSaldo() {
        if (contaAtual == null) {
            JOptionPane.showMessageDialog(this, "ERRO: Selecione uma conta primeiro!");
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Saldo atual: R$ " + String.format("%.2f", contaAtual.getSaldo()));
    }

    private void remunerar() {
        if (contaAtual == null) {
            JOptionPane.showMessageDialog(this, "ERRO: Selecione uma conta primeiro!");
            return;
        }

        double saldoAnterior = contaAtual.getSaldo();
        contaAtual.remunera();
        double saldoNovo = contaAtual.getSaldo();
        double rendimento = saldoNovo - saldoAnterior;

        JOptionPane.showMessageDialog(this,
                "Remuneração aplicada com sucesso!\n" +
                        "Rendimento: R$ " + String.format("%.2f", rendimento) + "\n" +
                        "Novo saldo: R$ " + String.format("%.2f", saldoNovo));

        atualizarInfoConta();
    }
}
