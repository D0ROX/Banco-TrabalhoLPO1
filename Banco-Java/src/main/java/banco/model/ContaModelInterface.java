package banco.model;

public interface ContaModelInterface {
    boolean deposita(double valor);
    boolean saca(double valor);
    ClienteModel getDono();
    int getNumero();
    double getSaldo();
    void remunera();
}
