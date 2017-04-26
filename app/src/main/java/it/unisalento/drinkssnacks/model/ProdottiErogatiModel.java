package it.unisalento.drinkssnacks.model;

/**
 * Created by aguinaldo on 25/04/2017.
 */

public class ProdottiErogatiModel {
    private int idProdotto;
    private String nome;
    private int quantita;

    public int getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(int idProdotto) {
        this.idProdotto = idProdotto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }
}
