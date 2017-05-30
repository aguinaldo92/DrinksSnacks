package it.unisalento.drinkssnacks.model;

import java.util.List;

/**
 * Created by andrea on 30/05/2017.
 */

public class SottoscrizioniModel {
    List<Integer> listIdDistributori;
    List<String> listTopics;

    public List<Integer> getListIdDistributori() {
        return listIdDistributori;
    }

    public void setListIdDistributori(List<Integer> listIdDistributori) {
        this.listIdDistributori = listIdDistributori;
    }

    public List<String> getListTopics() {
        return listTopics;
    }

    public void setListTopics(List<String> listTopics) {
        this.listTopics = listTopics;
    }
}
