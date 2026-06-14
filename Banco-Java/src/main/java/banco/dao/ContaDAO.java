package banco.dao;

import banco.model.ContaModel;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public interface ContaDAO {
    boolean salvar(ContaModel conta) throws SQLException;

    boolean atualizar(ContaModel conta) throws SQLException;

    boolean excluirPorCpfCliente(String cpf) throws SQLException;

    Optional<ContaModel> buscarPorCpfCliente(String cpf) throws SQLException;

    Map<String, ContaModel> listarTodasPorCpf() throws SQLException;
}
