package it.unisalento.drinkssnacks.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrea on 30/05/2017.
 */

public class StringProtectedRequest extends StringRequest {
    String token;
    HashMap<String, String> params;

    public StringProtectedRequest(String token, HashMap<String, String> params, int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.token = token;
        this.params = params;
    }

    public StringProtectedRequest(String token, HashMap<String, String> params, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        this.token = token;
        this.params = params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        if (headers == null || headers.isEmpty()) {
            headers = new HashMap<>();
        }
        //params.put("Content-Type", "application/json");
        //headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer : " + token);
        //headers.putAll(params);
        //..add other headers
        return headers;
    }

    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
        Map<String, String> fields = new HashMap<String, String>();
        fields.putAll(params);
        return params;
    }

    @Override
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=UTF-8";
    }
}


