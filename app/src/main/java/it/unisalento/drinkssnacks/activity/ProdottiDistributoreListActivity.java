package it.unisalento.drinkssnacks.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import it.unisalento.drinkssnacks.R;
import it.unisalento.drinkssnacks.adapter.RowProdottiDistributoreAdapter;
import it.unisalento.drinkssnacks.model.ProdottoDistributoreModel;
import it.unisalento.drinkssnacks.singleton.AppSingleton;


public class ProdottiDistributoreListActivity extends ListActivity {

    private static final String TAG = ProdottiDistributoreListActivity.class.getSimpleName();
    private final String mUrl = "http://distributori.ddns.net:8080/distributori-rest/prodotti_erogati.json";
    private int idDistributore;
    private List<ProdottoDistributoreModel> prodottoDistributoreModels = new ArrayList<>();
    private RowProdottiDistributoreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_prodotti_distributore);
        adapter = new RowProdottiDistributoreAdapter(this, R.layout.row_activity_list_prodotti_distributore, prodottoDistributoreModels, idDistributore);
        setListAdapter(adapter);

        Intent intent = getIntent();
        this.idDistributore = intent.getIntExtra(MapsActivity.EXTRA_MESSAGE, -1);
        Toast toast = Toast.makeText(getApplicationContext(), "visualizzo distributore con id = " + idDistributore, Toast.LENGTH_SHORT);
        toast.show();

        final String getUrl = mUrl + "?" + "idDistributore=" + idDistributore;
        final String prodottiDistributoreArray = "prodottiErogati";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, getUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        Gson gson = new Gson();

                        JSONArray jsonArray = response.optJSONArray(prodottiDistributoreArray);
                        CharSequence text = "Response: " + response.toString();
                        if (jsonArray != null) {
                            Type listType = new TypeToken<ArrayList<ProdottoDistributoreModel>>() {
                            }.getType();
                            prodottoDistributoreModels = gson.fromJson(jsonArray.toString(), listType);
                            if (!prodottoDistributoreModels.isEmpty()) {

                                adapter.addAll(prodottoDistributoreModels);
                                adapter.notifyDataSetChanged();
                            }
                            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                            toast.show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Errore", Toast.LENGTH_SHORT);
                        toast.show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        AppSingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }


}
