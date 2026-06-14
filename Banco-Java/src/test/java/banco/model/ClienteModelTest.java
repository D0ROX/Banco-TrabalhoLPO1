package banco.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClienteModelTest {
    @Test
    void deveOrdenarPorNomeESobrenome() {
        ClienteModel bruno = new ClienteModel("Bruno", "Costa", "1", "111", "Rua A");
        ClienteModel anaSouza = new ClienteModel("Ana", "Souza", "2", "222", "Rua B");
        ClienteModel anaLima = new ClienteModel("Ana", "Lima", "3", "333", "Rua C");

        List<ClienteModel> clientes = new ArrayList<>(List.of(bruno, anaSouza, anaLima));
        Collections.sort(clientes);

        assertEquals(List.of(anaLima, anaSouza, bruno), clientes);
    }
}
