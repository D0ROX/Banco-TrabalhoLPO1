package banco.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContaCorrenteModelTest {
    private final ClienteModel cliente = new ClienteModel(
            "Ana",
            "Silva",
            "123",
            "11122233344",
            "Rua A"
    );

    @Test
    void deveDepositarApenasValorPositivo() {
        ContaCorrenteModel conta = new ContaCorrenteModel(cliente, 100.0, 50.0);

        assertFalse(conta.deposita(0.0));
        assertFalse(conta.deposita(-10.0));
        assertTrue(conta.deposita(25.0));
        assertEquals(125.0, conta.getSaldo(), 0.001);
    }

    @Test
    void devePermitirSaqueAteOLimite() {
        ContaCorrenteModel conta = new ContaCorrenteModel(cliente, 100.0, 50.0);

        assertTrue(conta.saca(140.0));
        assertEquals(-40.0, conta.getSaldo(), 0.001);
        assertFalse(conta.saca(20.0));
        assertEquals(-40.0, conta.getSaldo(), 0.001);
    }

    @Test
    void deveRemunerarSaldoEmUmPorCento() {
        ContaCorrenteModel conta = new ContaCorrenteModel(cliente, 100.0, 50.0);

        conta.remunera();

        assertEquals(101.0, conta.getSaldo(), 0.001);
    }
}
