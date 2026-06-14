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

    public ContaModel(int numero, ClienteModel dono, double saldo) {
        this.numero = numero;
        this.dono = dono;
        this.saldo = saldo;
        if (numero >= proximoNumero) {
            proximoNumero = numero + 1;
        }
    }

    @Override
    public boolean deposita(double valor) {
        if (valor <= 0) {
            return false;
        }
        saldo += valor;
        return true;
    }

    @Override
    public boolean saca(double valor) {
        if (valor <= 0 || valor > saldo) {
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

    public void setNumero(int numero) {
        this.numero = numero;
        if (numero >= proximoNumero) {
            proximoNumero = numero + 1;
        }
    }

    public void setDono(ClienteModel dono) {
        this.dono = dono;
    }

    @Override
    public abstract void remunera();
}
