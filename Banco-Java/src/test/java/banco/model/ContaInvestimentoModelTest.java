package banco.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContaInvestimentoModelTest {
    private final ClienteModel cliente = new ClienteModel(
            "Bruno",
            "Oliveira",
            "456",
            "55566677788",
            "Rua B"
    );

    @Test
    void deveRespeitarDepositoMinimo() {
        ContaInvestimentoModel conta = new ContaInvestimentoModel(cliente, 200.0, 100.0, 50.0);

        assertFalse(conta.deposita(49.99));
        assertEquals(200.0, conta.getSaldo(), 0.001);
        assertTrue(conta.deposita(50.0));
        assertEquals(250.0, conta.getSaldo(), 0.001);
    }

    @Test
    void deveRespeitarMontanteMinimoNoSaque() {
        ContaInvestimentoModel conta = new ContaInvestimentoModel(cliente, 200.0, 100.0, 50.0);

        assertTrue(conta.saca(80.0));
        assertEquals(120.0, conta.getSaldo(), 0.001);
        assertFalse(conta.saca(30.0));
        assertEquals(120.0, conta.getSaldo(), 0.001);
    }

    @Test
    void deveRemunerarSaldoEmDoisPorCento() {
        ContaInvestimentoModel conta = new ContaInvestimentoModel(cliente, 200.0, 100.0, 50.0);

        conta.remunera();

        assertEquals(204.0, conta.getSaldo(), 0.001);
    }
}
