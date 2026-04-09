package banco.model;

public abstract class ContaModel implements ContaModelInterface {
    protected int numero;
    protected double saldo;
    protected ClienteModel dono;
    protected static int proximoNumero = 1000;

    public ContaModel(ClienteModel dono, double depositoInicial) {
        this.numero = proximoNumero++;
        this.dono = dono;
        this.saldo = depositoInicial;
    }

    @Override
    public boolean deposita(double valor) {
        if (valor < 0) {
            return false;
        }
        saldo += valor;
        return true;
    }

    @Override
    public boolean saca(double valor) {
        if(valor <= 0 && saldo <= valor){
            return false;
        }

        saldo -= valor;
        return true;
    }

    @Override
    public ClienteModel getDono() { return dono; }
    @Override
    public int getNumero() { return numero; }
    @Override
    public double getSaldo() { return saldo; }

    @Override
    public abstract void remunera();
}
