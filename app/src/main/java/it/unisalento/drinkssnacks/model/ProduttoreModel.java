package it.unisalento.drinkssnacks.model;

/**
 * Created by andrea on 19/05/2017.
 */

public class ProduttoreModel {
    private String nome;
    private String sede;
    private StabilimentoModel stabilimento;

    public StabilimentoModel getStabilimento() {
        return stabilimento;
    }

    public void setStabilimento(StabilimentoModel stabilimento) {
        this.stabilimento = stabilimento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }
}
