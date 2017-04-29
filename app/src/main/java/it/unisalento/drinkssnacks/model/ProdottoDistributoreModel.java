package it.unisalento.drinkssnacks.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aguinaldo on 28/04/2017.
 */
public class ProdottoDistributoreModel implements Parcelable {
    public static final Parcelable.Creator<ProdottoDistributoreModel> CREATOR = new Parcelable.Creator<ProdottoDistributoreModel>() {
        @Override
        public ProdottoDistributoreModel createFromParcel(Parcel in) {
            return new ProdottoDistributoreModel(in);
        }

        @Override
        public ProdottoDistributoreModel[] newArray(int size) {
            return new ProdottoDistributoreModel[size];
        }
    };
    private int idProdottoErogato;
    private int idProdotto;
    private int scaffale;
    private int posto;
    private int quantita;
    private String nome;
    private String produttore;
    private String foto64;
    private String foto256;
    private double prezzo;
    private double sconto;

    protected ProdottoDistributoreModel(Parcel in) {
        idProdottoErogato = in.readInt();
        idProdotto = in.readInt();
        scaffale = in.readInt();
        posto = in.readInt();
        quantita = in.readInt();
        nome = in.readString();
        produttore = in.readString();
        foto64 = in.readString();
        foto256 = in.readString();
        prezzo = in.readDouble();
        sconto = in.readDouble();
    }

    public int getIdProdottoErogato() {
        return idProdottoErogato;
    }

    public void setIdProdottoErogato(int idProdottoErogato) {
        this.idProdottoErogato = idProdottoErogato;
    }

    public int getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(int idProdotto) {
        this.idProdotto = idProdotto;
    }

    public int getScaffale() {
        return scaffale;
    }

    public void setScaffale(int scaffale) {
        this.scaffale = scaffale;
    }

    public int getPosto() {
        return posto;
    }

    public void setPosto(int posto) {
        this.posto = posto;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getProduttore() {
        return produttore;
    }

    public void setProduttore(String produttore) {
        this.produttore = produttore;
    }

    public String getFoto64() {
        return foto64;
    }

    public void setFoto64(String foto64) {
        this.foto64 = foto64;
    }

    public String getFoto256() {
        return foto256;
    }

    public void setFoto256(String foto256) {
        this.foto256 = foto256;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public double getSconto() {
        return sconto;
    }

    public void setSconto(double sconto) {
        this.sconto = sconto;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idProdottoErogato);
        dest.writeInt(idProdotto);
        dest.writeInt(scaffale);
        dest.writeInt(posto);
        dest.writeInt(quantita);
        dest.writeString(nome);
        dest.writeString(produttore);
        dest.writeString(foto64);
        dest.writeString(foto256);
        dest.writeDouble(prezzo);
        dest.writeDouble(sconto);
    }
}
