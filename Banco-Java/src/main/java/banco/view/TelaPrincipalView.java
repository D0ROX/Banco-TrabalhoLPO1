package banco.view;

import banco.controller.GerenciadorBancoControler;
import banco.controller.TelaManipularContaControler;

import javax.swing.*;
import java.awt.*;

public class TelaPrincipalView extends JFrame {
    private GerenciadorBancoControler banco;

    public TelaPrincipalView() {
        banco = new GerenciadorBancoControler();

        setTitle("Sistema Bancário");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new GridLayout(4, 1, 10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnClientes = new JButton("Gerenciar Clientes");
        btnClientes.setFont(new Font("Arial", Font.PLAIN, 16));
        btnClientes.addActionListener(e -> abrirTelaClientes());

        JButton btnVincularConta = new JButton("Vincular Conta a Cliente");
        btnVincularConta.setFont(new Font("Arial", Font.PLAIN, 16));
        btnVincularConta.addActionListener(e -> abrirTelaVincularConta());

        JButton btnManipularConta = new JButton("Manipular Conta");
        btnManipularConta.setFont(new Font("Arial", Font.PLAIN, 16));
        btnManipularConta.addActionListener(e -> abrirTelaManipularConta());

        JButton btnSair = new JButton("Sair");
        btnSair.setFont(new Font("Arial", Font.PLAIN, 16));
        btnSair.addActionListener(e -> System.exit(0));

        painelPrincipal.add(btnClientes);
        painelPrincipal.add(btnVincularConta);
        painelPrincipal.add(btnManipularConta);
        painelPrincipal.add(btnSair);

        add(painelPrincipal);


    }

    private void abrirTelaClientes() {
        TelaClientesView tela = new TelaClientesView(banco);
        tela.setVisible(true);
    }

    private void abrirTelaVincularConta() {
        TelaVincularContaView tela = new TelaVincularContaView(banco);
        tela.setVisible(true);
    }

    private void abrirTelaManipularConta() {
        TelaManipularContaControler tela = new TelaManipularContaControler(banco);
        tela.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaPrincipalView tela = new TelaPrincipalView();
            tela.setVisible(true);
        });
    }
}

