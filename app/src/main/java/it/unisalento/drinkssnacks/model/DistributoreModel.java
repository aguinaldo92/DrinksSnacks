package it.unisalento.drinkssnacks.model;

import java.util.List;

/**
 * Created by aguinaldo on 20/04/2017.
 */

public class DistributoreModel {

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
}


