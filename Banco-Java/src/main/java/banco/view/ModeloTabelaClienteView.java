package banco.view;

import banco.model.ClienteModel;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ModeloTabelaClienteView extends AbstractTableModel {
    private List<ClienteModel> listaClientes;
    private String[] nomesColunas = {"Nome", "Sobrenome", "RG", "CPF", "Endereço"};

    public ModeloTabelaClienteView(List<ClienteModel> listaClientes) {
        this.listaClientes = listaClientes;
    }

    @Override
    public int getRowCount() {
        return listaClientes.size();
    }

    @Override
    public int getColumnCount() {
        return nomesColunas.length;
    }

    @Override
    public String getColumnName(int indiceColuna) {
        return nomesColunas[indiceColuna];
    }

    @Override
    public Object getValueAt(int indiceLinha, int indiceColuna) {
        ClienteModel cliente = listaClientes.get(indiceLinha);
        switch (indiceColuna) {
            case 0: return cliente.getNome();
            case 1: return cliente.getSobrenome();
            case 2: return cliente.getRg();
            case 3: return cliente.getCpf();
            case 4: return cliente.getEndereco();
            default: return null;
        }
    }

    public ClienteModel getCliente(int indiceLinha) {
        return listaClientes.get(indiceLinha);
    }

    public void setListaClientes(List<ClienteModel> novaLista) {
        this.listaClientes = novaLista;
        fireTableDataChanged();
    }
}
