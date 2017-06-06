package it.unisalento.drinkssnacks.model;

import java.util.List;

/**
 * Created by andrea on 30/05/2017.
 */

public class SottoscrizioniModel {
    List<Integer> listDistributori;
    List<String> listTopics;

    public List<Integer> getListDistributori() {
        return listDistributori;
    }

    public void setListDistributori(List<Integer> listDistributori) {
        this.listDistributori = listDistributori;
    }

    public List<String> getListTopics() {
        return listTopics;
    }

    public void setListTopics(List<String> listTopics) {
        this.listTopics = listTopics;
    }
}
