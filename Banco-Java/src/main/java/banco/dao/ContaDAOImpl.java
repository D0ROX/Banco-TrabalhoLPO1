package banco.dao;

import banco.model.ClienteModel;
import banco.model.ContaCorrenteModel;
import banco.model.ContaInvestimentoModel;
import banco.model.ContaModel;
import banco.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ContaDAOImpl implements ContaDAO {
    private static final String TIPO_CORRENTE = "CORRENTE";
    private static final String TIPO_INVESTIMENTO = "INVESTIMENTO";

    private final ConnectionFactory connectionFactory;

    public ContaDAOImpl() {
        this(ConnectionFactory.getInstance());
    }

    public ContaDAOImpl(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public boolean salvar(ContaModel conta) throws SQLException {
        String sql = """
                INSERT INTO conta (
                    cliente_cpf, tipo, saldo, limite, montante_minimo, deposito_minimo
                ) VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherConta(statement, conta);
            int linhasAfetadas = statement.executeUpdate();
            if (linhasAfetadas == 0) {
                return false;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    conta.setNumero(generatedKeys.getInt(1));
                }
            }
            return true;
        }
    }

    @Override
    public boolean atualizar(ContaModel conta) throws SQLException {
        String sql = """
                UPDATE conta
                SET cliente_cpf = ?, tipo = ?, saldo = ?, limite = ?, montante_minimo = ?, deposito_minimo = ?
                WHERE numero = ?
                """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            preencherConta(statement, conta);
            statement.setInt(7, conta.getNumero());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean excluirPorCpfCliente(String cpf) throws SQLException {
        String sql = "DELETE FROM conta WHERE cliente_cpf = ?";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, cpf);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<ContaModel> buscarPorCpfCliente(String cpf) throws SQLException {
        String sql = """
                SELECT conta.numero, conta.tipo, conta.saldo, conta.limite,
                       conta.montante_minimo, conta.deposito_minimo,
                       cliente.cpf, cliente.nome, cliente.sobrenome, cliente.rg, cliente.endereco
                FROM conta
                INNER JOIN cliente ON cliente.cpf = conta.cliente_cpf
                WHERE conta.cliente_cpf = ?
                """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, cpf);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapearConta(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public Map<String, ContaModel> listarTodasPorCpf() throws SQLException {
        String sql = """
                SELECT conta.numero, conta.tipo, conta.saldo, conta.limite,
                       conta.montante_minimo, conta.deposito_minimo,
                       cliente.cpf, cliente.nome, cliente.sobrenome, cliente.rg, cliente.endereco
                FROM conta
                INNER JOIN cliente ON cliente.cpf = conta.cliente_cpf
                """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            Map<String, ContaModel> contas = new HashMap<>();
            while (resultSet.next()) {
                ContaModel conta = mapearConta(resultSet);
                contas.put(conta.getDono().getCpf(), conta);
            }
            return contas;
        }
    }

    private void preencherConta(PreparedStatement statement, ContaModel conta) throws SQLException {
        statement.setString(1, conta.getDono().getCpf());
        statement.setString(2, tipoConta(conta));
        statement.setDouble(3, conta.getSaldo());

        if (conta instanceof ContaCorrenteModel contaCorrente) {
            statement.setDouble(4, contaCorrente.getLimite());
            statement.setNull(5, Types.DECIMAL);
            statement.setNull(6, Types.DECIMAL);
            return;
        }

        ContaInvestimentoModel contaInvestimento = (ContaInvestimentoModel) conta;
        statement.setNull(4, Types.DECIMAL);
        statement.setDouble(5, contaInvestimento.getMontanteMinimo());
        statement.setDouble(6, contaInvestimento.getDepositoMinimo());
    }

    private String tipoConta(ContaModel conta) {
        if (conta instanceof ContaCorrenteModel) {
            return TIPO_CORRENTE;
        }
        return TIPO_INVESTIMENTO;
    }

    private ContaModel mapearConta(ResultSet resultSet) throws SQLException {
        ClienteModel cliente = new ClienteModel(
                resultSet.getString("nome"),
                resultSet.getString("sobrenome"),
                resultSet.getString("rg"),
                resultSet.getString("cpf"),
                resultSet.getString("endereco")
        );

        int numero = resultSet.getInt("numero");
        double saldo = resultSet.getDouble("saldo");
        String tipo = resultSet.getString("tipo");

        if (TIPO_CORRENTE.equals(tipo)) {
            return new ContaCorrenteModel(
                    numero,
                    cliente,
                    saldo,
                    resultSet.getDouble("limite")
            );
        }

        return new ContaInvestimentoModel(
                numero,
                cliente,
                saldo,
                resultSet.getDouble("montante_minimo"),
                resultSet.getDouble("deposito_minimo")
        );
    }
}
