# 🏦 Sistema Bancário - Trabalho de POO

### 🎯 Objetivo
Desenvolver um sistema bancário funcional utilizando a linguagem **Java**, aplicando conceitos de Orientação a Objetos (Herança, Polimorfismo e Interfaces) e interface gráfica com **Swing**.

### 📝 Descrição
O projeto simula as operações de um banco real. O sistema permite o **cadastro completo de clientes**, a **vinculação de contas** (Corrente ou Investimento) e a realização de transações financeiras como **saques, depósitos e consulta de saldo**. Todas as regras de negócio, como limites de saque e rendimentos, foram implementadas seguindo as especificações do trabalho.

### Instruções para Compilar e Executar

1. Limpar e Compilar: mvn -f Banco-Java clean compile
2. executar: mvn -f Banco-Java exec:java -Dexec.mainClass="banco.view.TelaPrincipalView"
3. Empacotar o projeto: mvn -f Banco-Java clean package
4. Executar o JAR: java -cp Banco-Java/target/Banco-Java-1.0-SNAPSHOT.jar banco.view.TelaPrincipalView


### 👥 Autores
* Gabriel Silva Costa
* Danniel Eduardo Dorox
* Reinaldo Antonio Castellano
* Murilo Santos