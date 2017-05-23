package it.unisalento.drinkssnacks.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrea on 23/05/2017.
 * Questa classe permette di inserire il token nella richiesta al fine di superare i controlli dell'interceptor.
 */

public class JsonObjectProtectedRequest extends JsonObjectRequest {
    String token;
    public JsonObjectProtectedRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String token) {
        super(method, url, jsonRequest, listener, errorListener);
        this.token = token;
    }

    public JsonObjectProtectedRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String token) {
        super(url, jsonRequest, listener, errorListener);
        this.token = token;
    }


        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> params =  super.getHeaders();
        if ( params == null ) {
            params = new HashMap<>();
            params.put("Content-Type","application/json");
        }
        params.put("Authorization","Bearer " + token);
        //..add other headers
        return params;
    }

}
