package it.unisalento.drinkssnacks.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aguinaldo on 20/04/2017.
 */

public class DistributoreModel implements Parcelable {

    private Integer idDistributore;
    private Double lat;
    private Double lon;
    private String posizioneEdificio;
    private String indirizzo;
    private List<CategorieForniteModel> listCategorieFornite;
    private List<ProdottiErogatiModel> listProdottiErogati;


    public Integer getIdDistributore() {
        return idDistributore;
    }

    public void setIdDistributore(Integer idDistributore) {
        this.idDistributore = idDistributore;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getPosizioneEdificio() {
        return posizioneEdificio;
    }

    public void setPosizioneEdificio(String posizioneEdificio) {
        this.posizioneEdificio = posizioneEdificio;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public List<CategorieForniteModel> getListCategorieFornite() {
        return listCategorieFornite;
    }

    public void setListCategorieFornite(List<CategorieForniteModel> listCategorieFornite) {
        this.listCategorieFornite = listCategorieFornite;
    }

    public List<ProdottiErogatiModel> getListProdottiErogati() {
        return listProdottiErogati;
    }

    public void setListProdottiErogati(List<ProdottiErogatiModel> listProdottiErogati) {
        this.listProdottiErogati = listProdottiErogati;
    }

    protected DistributoreModel(Parcel in) {
        idDistributore = in.readByte() == 0x00 ? null : in.readInt();
        lat = in.readByte() == 0x00 ? null : in.readDouble();
        lon = in.readByte() == 0x00 ? null : in.readDouble();
        posizioneEdificio = in.readString();
        indirizzo = in.readString();
        if (in.readByte() == 0x01) {
            listCategorieFornite = new ArrayList<CategorieForniteModel>();
            in.readList(listCategorieFornite, CategorieForniteModel.class.getClassLoader());
        } else {
            listCategorieFornite = null;
        }
        if (in.readByte() == 0x01) {
            listProdottiErogati = new ArrayList<ProdottiErogatiModel>();
            in.readList(listProdottiErogati, ProdottiErogatiModel.class.getClassLoader());
        } else {
            listProdottiErogati = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (idDistributore == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(idDistributore);
        }
        if (lat == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(lat);
        }
        if (lon == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(lon);
        }
        dest.writeString(posizioneEdificio);
        dest.writeString(indirizzo);
        if (listCategorieFornite == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(listCategorieFornite);
        }
        if (listProdottiErogati == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(listProdottiErogati);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DistributoreModel> CREATOR = new Parcelable.Creator<DistributoreModel>() {
        @Override
        public DistributoreModel createFromParcel(Parcel in) {
            return new DistributoreModel(in);
        }

        @Override
        public DistributoreModel[] newArray(int size) {
            return new DistributoreModel[size];
        }
    };
}