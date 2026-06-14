package banco.dao;

import banco.model.ClienteModel;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ClienteDAO {
    boolean salvar(ClienteModel cliente) throws SQLException;

    boolean atualizar(String cpfOriginal, ClienteModel cliente) throws SQLException;

    boolean excluirPorCpf(String cpf) throws SQLException;

    List<ClienteModel> listarTodos() throws SQLException;

    List<ClienteModel> buscarPorNome(String nome) throws SQLException;

    List<ClienteModel> buscarPorSobrenome(String sobrenome) throws SQLException;

    Optional<ClienteModel> buscarPorRg(String rg) throws SQLException;

    Optional<ClienteModel> buscarPorCpf(String cpf) throws SQLException;
}
