package banco.dao;

import banco.model.ClienteModel;
import banco.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteDAOImpl implements ClienteDAO {
    private final ConnectionFactory connectionFactory;

    public ClienteDAOImpl() {
        this(ConnectionFactory.getInstance());
    }

    public ClienteDAOImpl(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public boolean salvar(ClienteModel cliente) throws SQLException {
        String sql = """
                INSERT INTO cliente (cpf, nome, sobrenome, rg, endereco)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            preencherCliente(statement, cliente);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean atualizar(String cpfOriginal, ClienteModel cliente) throws SQLException {
        String sql = """
                UPDATE cliente
                SET cpf = ?, nome = ?, sobrenome = ?, rg = ?, endereco = ?
                WHERE cpf = ?
                """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            preencherCliente(statement, cliente);
            statement.setString(6, cpfOriginal);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean excluirPorCpf(String cpf) throws SQLException {
        String sql = "DELETE FROM cliente WHERE cpf = ?";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, cpf);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public List<ClienteModel> listarTodos() throws SQLException {
        String sql = """
                SELECT cpf, nome, sobrenome, rg, endereco
                FROM cliente
                ORDER BY nome, sobrenome
                """;
        return listarPorSql(sql);
    }

    @Override
    public List<ClienteModel> buscarPorNome(String nome) throws SQLException {
        String sql = """
                SELECT cpf, nome, sobrenome, rg, endereco
                FROM cliente
                WHERE LOWER(nome) LIKE LOWER(?)
                ORDER BY nome, sobrenome
                """;
        return listarPorParametro(sql, "%" + nome + "%");
    }

    @Override
    public List<ClienteModel> buscarPorSobrenome(String sobrenome) throws SQLException {
        String sql = """
                SELECT cpf, nome, sobrenome, rg, endereco
                FROM cliente
                WHERE LOWER(sobrenome) LIKE LOWER(?)
                ORDER BY sobrenome, nome
                """;
        return listarPorParametro(sql, "%" + sobrenome + "%");
    }

    @Override
    public Optional<ClienteModel> buscarPorRg(String rg) throws SQLException {
        String sql = """
                SELECT cpf, nome, sobrenome, rg, endereco
                FROM cliente
                WHERE rg = ?
                """;
        return buscarUnico(sql, rg);
    }

    @Override
    public Optional<ClienteModel> buscarPorCpf(String cpf) throws SQLException {
        String sql = """
                SELECT cpf, nome, sobrenome, rg, endereco
                FROM cliente
                WHERE cpf = ?
                """;
        return buscarUnico(sql, cpf);
    }

    private void preencherCliente(PreparedStatement statement, ClienteModel cliente) throws SQLException {
        statement.setString(1, cliente.getCpf());
        statement.setString(2, cliente.getNome());
        statement.setString(3, cliente.getSobrenome());
        statement.setString(4, cliente.getRg());
        statement.setString(5, cliente.getEndereco());
    }

    private List<ClienteModel> listarPorSql(String sql) throws SQLException {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            return mapearClientes(resultSet);
        }
    }

    private List<ClienteModel> listarPorParametro(String sql, String parametro) throws SQLException {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, parametro);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapearClientes(resultSet);
            }
        }
    }

    private Optional<ClienteModel> buscarUnico(String sql, String parametro) throws SQLException {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, parametro);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapearCliente(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    private List<ClienteModel> mapearClientes(ResultSet resultSet) throws SQLException {
        List<ClienteModel> clientes = new ArrayList<>();
        while (resultSet.next()) {
            clientes.add(mapearCliente(resultSet));
        }
        return clientes;
    }

    private ClienteModel mapearCliente(ResultSet resultSet) throws SQLException {
        return new ClienteModel(
                resultSet.getString("nome"),
                resultSet.getString("sobrenome"),
                resultSet.getString("rg"),
                resultSet.getString("cpf"),
                resultSet.getString("endereco")
        );
    }
}
