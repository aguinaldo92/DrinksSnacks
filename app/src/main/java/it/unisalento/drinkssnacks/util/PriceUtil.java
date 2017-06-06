package it.unisalento.drinkssnacks.util;

import android.content.Context;

import java.math.BigDecimal;

import it.unisalento.drinkssnacks.singleton.AppSingleton;

/**
 * Created by andrea on 06/06/2017.
 */

public abstract class PriceUtil {
    public static BigDecimal getPrice(Context context, int quantita, String prezzo, String sconto ) {
        if(AppSingleton.getInstance(context).isTokenSavedValid()) {
            BigDecimal riduzione = new BigDecimal(prezzo).multiply(new BigDecimal(sconto));
            BigDecimal prezzoEffettivoUnitario = new BigDecimal(prezzo).subtract(riduzione);
            return prezzoEffettivoUnitario.multiply(new BigDecimal(quantita));
        } else {
            return new BigDecimal(prezzo).multiply(new BigDecimal(quantita));
        }
    }
}
