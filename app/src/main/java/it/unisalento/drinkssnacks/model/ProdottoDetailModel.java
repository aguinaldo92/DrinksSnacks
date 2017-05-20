package it.unisalento.drinkssnacks.model;

/**
 * Created by andrea on 19/05/2017.
 */

public class ProdottoDetailModel {
    private int idProdotto;
    private String foto256;
    private String nome;
    private String descrizione;
    private String prezzo;
    private String sconto;
    private String ingredienti;
    private String preparazione;
    private ProduttoreModel produttoreModel;
    private StabilimentoModel stabilimentoModel;

    public int getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(int idProdotto) {
        this.idProdotto = idProdotto;
    }

    public String getFoto256() {
        return foto256;
    }

    public void setFoto256(String foto256) {
        this.foto256 = foto256;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(String prezzo) {
        this.prezzo = prezzo;
    }

    public String getSconto() {
        return sconto;
    }

    public void setSconto(String sconto) {
        this.sconto = sconto;
    }

    public String getIngredienti() {
        return ingredienti;
    }

    public void setIngredienti(String ingredienti) {
        this.ingredienti = ingredienti;
    }

    public String getPreparazione() {
        return preparazione;
    }

    public void setPreparazione(String preparazione) {
        this.preparazione = preparazione;
    }

    public ProduttoreModel getProduttoreModel() {
        return produttoreModel;
    }

    public void setProduttoreModel(ProduttoreModel produttoreModel) {
        this.produttoreModel = produttoreModel;
    }

    public StabilimentoModel getStabilimentoModel() {
        return stabilimentoModel;
    }

    public void setStabilimentoModel(StabilimentoModel stabilimentoModel) {
        this.stabilimentoModel = stabilimentoModel;
    }
}
