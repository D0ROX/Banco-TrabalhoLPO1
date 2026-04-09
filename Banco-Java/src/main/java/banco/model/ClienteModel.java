package banco.model;

import java.util.Objects;

public class ClienteModel implements Comparable<ClienteModel> {
    private String nome;
    private String sobrenome;
    private String rg;
    private String cpf;
    private String endereco;

    public ClienteModel(String nome, String sobrenome, String rg, String cpf, String endereco) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.rg = rg;
        this.cpf = cpf;
        this.endereco = endereco;
    }

    public String getNome() { return nome; }
    public String getSobrenome() { return sobrenome; }
    public String getRg() { return rg; }
    public String getCpf() { return cpf; }
    public String getEndereco() { return endereco; }

    public void setNome(String nome) { this.nome = nome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }
    public void setRg(String rg) { this.rg = rg; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    @Override
    public int compareTo(ClienteModel outro) {
        int cmp = this.nome.compareToIgnoreCase(outro.nome);
        if (cmp != 0) return cmp;
        return this.sobrenome.compareToIgnoreCase(outro.sobrenome);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClienteModel)) return false;
        ClienteModel cliente = (ClienteModel) o;
        return Objects.equals(cpf, cliente.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }

    @Override
    public String toString() {
        return nome + " " + sobrenome + " - CPF: " + cpf;
    }
}
