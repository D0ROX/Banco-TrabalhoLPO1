package banco.model;

public class ContaInvestimentoModel extends ContaModel {
    private double montanteMinimo;
    private double depositoMinimo;

    public ContaInvestimentoModel(ClienteModel dono, double depositoInicial,
                                  double montanteMinimo, double depositoMinimo) {
        super(dono, depositoInicial);
        this.montanteMinimo = montanteMinimo;
        this.depositoMinimo = depositoMinimo;
    }

    public double getMontanteMinimo() { return montanteMinimo; }
    public double getDepositoMinimo() { return depositoMinimo; }

    @Override
    public boolean deposita(double valor) {
        if (valor >= depositoMinimo) {
            return super.deposita(valor);
        }
        return false;
    }

    @Override
    public boolean saca(double valor) {
        if (valor <= 0) return false;
        if (saldo - valor >= montanteMinimo) {
            saldo -= valor;
            return true;
        }
        return false;
    }

    @Override
    public void remunera() {
        saldo = saldo * 1.02;
    }

    @Override
    public String toString() {
        return "Conta Investimento - Número: " + numero +
                " - Saldo: R$ " + String.format("%.2f", saldo) +
                " <br> Montante Mínimo: R$ " + String.format("%.2f", montanteMinimo);
    }
}
