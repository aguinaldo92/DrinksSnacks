package it.unisalento.drinkssnacks.subcriber;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import it.unisalento.drinkssnacks.model.SottoscrizioniModel;
import it.unisalento.drinkssnacks.singleton.AppSingleton;
import it.unisalento.drinkssnacks.volley.JsonObjectProtectedRequest;

/**
 * Created by andrea on 01/06/2017.
 */

public class SubscriptionManager {
    private static String TAG = SubscriptionManager.class.getCanonicalName();
    private String mUrl = "http://distributori.ddns.net:8080/distributori-rest/sottoscrizioni.json";
    private Context mContext;

    public SubscriptionManager(Context context) {
        this.mContext = context;
    }

    public void subscribeAll() {

        try {
            int idPersona = AppSingleton.getInstance(mContext).fetchIdPersona();
            String modality = "get";
            String urlGetSubscriptions = mUrl + "?" + "idPersona=" + idPersona + "&" + "modality=" + modality;
            String token = AppSingleton.getInstance(mContext).fetchToken();
            FirebaseMessaging.getInstance().subscribeToTopic("DrinksSnacks");
            JsonObjectProtectedRequest jsonObjectProtectedRequest = new JsonObjectProtectedRequest(Request.Method.GET, urlGetSubscriptions, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    String result = "result";
                    String sottoscrizioni = "sottoscrizioni";
                    Gson gson = new Gson();
                    Boolean isResultTrue = response.optBoolean(result);
                    CharSequence text = "Response: " + response.toString();

                    if (isResultTrue) {
                        JSONObject jsonObject = response.optJSONObject(sottoscrizioni);
                        SottoscrizioniModel sottoscrizioniModel = gson.fromJson(jsonObject.toString(), SottoscrizioniModel.class);
                        for (String topicSottoscritto : sottoscrizioniModel.getListTopics()) {
                            FirebaseMessaging.getInstance().subscribeToTopic(topicSottoscritto);
                        }
                        AppSingleton.getInstance(mContext).saveSubscriptions(sottoscrizioniModel);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }, token);
            AppSingleton.getInstance(mContext).addToRequestQueue(jsonObjectProtectedRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void unsubscribeAll() {
        SottoscrizioniModel subscriptionsSaved = AppSingleton.getInstance(mContext).fetchSubscriptionsSaved();
        for (String topicSottoscritto : subscriptionsSaved.getListTopics()) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topicSottoscritto);
            unsubscribe(topicSottoscritto, false);
        }
        AppSingleton.getInstance(mContext).saveSubscriptions(new SottoscrizioniModel());
    }


    public void unsubscribe(final String topic, final Boolean update) {

        try {
            int idPersona = AppSingleton.getInstance(mContext).fetchIdPersona();
            String[] parts = topic.split("_");
            String idDistributore = parts[1];
            final String modality = "del";
            String urlGetSubscriptions = mUrl + "?" + "idPersona=" + idPersona + "&" + "idDistributore=" + idDistributore + "&" + "modality=" + modality;
            String token = AppSingleton.getInstance(mContext).fetchToken();

            JsonObjectProtectedRequest jsonObjectProtectedRequest = new JsonObjectProtectedRequest(Request.Method.GET, urlGetSubscriptions, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    String result = "result";
                    String sottoscrizioni = "sottoscrizioni";
                    Gson gson = new Gson();
                    Boolean isResultTrue = response.optBoolean(result);
                    CharSequence text = "Response: " + response.toString();
                    if (isResultTrue && update) {
                        JSONObject jsonObject = response.optJSONObject(sottoscrizioni);
                        SottoscrizioniModel sottoscrizioniModel = gson.fromJson(jsonObject.toString(), SottoscrizioniModel.class);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
                        AppSingleton.getInstance(mContext).saveSubscriptions(sottoscrizioniModel);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }, token);
            AppSingleton.getInstance(mContext).addToRequestQueue(jsonObjectProtectedRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void subscribe(String topic) {
        try {
            String[] parts = topic.split("_");
            String idDistributore = parts[1];
            int idPersona = AppSingleton.getInstance(mContext).fetchIdPersona();
            String modality = "set";
            String urlSetSubscription = mUrl + "?" + "idPersona=" + idPersona + "&" + "idDistributore=" + idDistributore + "&" + "modality=" + modality;
            String token = AppSingleton.getInstance(mContext).fetchToken();
            JsonObjectProtectedRequest jsonObjectProtectedRequest = new JsonObjectProtectedRequest(Request.Method.GET, urlSetSubscription, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    String result = "result";
                    String sottoscrizioni = "sottoscrizioni";
                    String idSottoscrizioniString = "id";
                    int idSottoscrizione;
                    Gson gson = new Gson();
                    Boolean isResultTrue = response.optBoolean(result);
                    CharSequence text = "Response: " + response.toString();

                    if (isResultTrue) {
                        idSottoscrizione = response.optInt(idSottoscrizioniString);
                        String topicSottoscritto = "distributore_" + String.valueOf(idSottoscrizione);
                        FirebaseMessaging.getInstance().subscribeToTopic(topicSottoscritto);
                        JSONObject jsonObject = response.optJSONObject(sottoscrizioni);
                        SottoscrizioniModel sottoscrizioniModel = gson.fromJson(jsonObject.toString(), SottoscrizioniModel.class);
                        AppSingleton.getInstance(mContext).saveSubscriptions(sottoscrizioniModel);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error.getMessage());
                }
            }, token);
            AppSingleton.getInstance(mContext).addToRequestQueue(jsonObjectProtectedRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean contains(int idDistributore) {
        SottoscrizioniModel sottoscrizioniModel = AppSingleton.getInstance(mContext).fetchSubscriptionsSaved();
        ArrayList<Integer> listIdsDistributoriInSubscriptions = (ArrayList<Integer>) sottoscrizioniModel.getListDistributori();
        return listIdsDistributoriInSubscriptions != null && listIdsDistributoriInSubscriptions.contains(idDistributore);
    }
}
