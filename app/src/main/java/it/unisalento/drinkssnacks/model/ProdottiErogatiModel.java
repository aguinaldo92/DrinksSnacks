package it.unisalento.drinkssnacks.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aguinaldo on 25/04/2017.
 */

public class ProdottiErogatiModel implements Parcelable {
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

    protected ProdottiErogatiModel(Parcel in) {
        idProdotto = in.readInt();
        nome = in.readString();
        quantita = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idProdotto);
        dest.writeString(nome);
        dest.writeInt(quantita);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ProdottiErogatiModel> CREATOR = new Parcelable.Creator<ProdottiErogatiModel>() {
        @Override
        public ProdottiErogatiModel createFromParcel(Parcel in) {
            return new ProdottiErogatiModel(in);
        }

        @Override
        public ProdottiErogatiModel[] newArray(int size) {
            return new ProdottiErogatiModel[size];
        }
    };
}