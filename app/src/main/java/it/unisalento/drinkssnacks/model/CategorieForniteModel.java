package it.unisalento.drinkssnacks.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aguinaldo on 20/04/2017.
 */

public class CategorieForniteModel implements Parcelable {
    private int idCategoria;
    private String nome;

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    protected CategorieForniteModel(Parcel in) {
        idCategoria = in.readInt();
        nome = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idCategoria);
        dest.writeString(nome);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CategorieForniteModel> CREATOR = new Parcelable.Creator<CategorieForniteModel>() {
        @Override
        public CategorieForniteModel createFromParcel(Parcel in) {
            return new CategorieForniteModel(in);
        }

        @Override
        public CategorieForniteModel[] newArray(int size) {
            return new CategorieForniteModel[size];
        }
    };
}