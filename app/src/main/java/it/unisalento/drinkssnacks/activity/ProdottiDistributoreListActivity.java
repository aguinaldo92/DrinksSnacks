package it.unisalento.drinkssnacks.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
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


public class ProdottiDistributoreListActivity extends AppCompatActivity {

    private static final String TAG = ProdottiDistributoreListActivity.class.getSimpleName();
    private final String mUrl = "http://distributori.ddns.net:8080/distributori-rest/prodotti_erogati.json";
    private int idDistributore;
    private List<ProdottoDistributoreModel> prodottoDistributoreModels = new ArrayList<>();
    private RowProdottiDistributoreAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_prodotti_distributore);
        adapter = new RowProdottiDistributoreAdapter(this, R.layout.row_activity_list_prodotti_distributore, prodottoDistributoreModels, idDistributore);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        Intent intent = getIntent();
        int idDistributoreSalvato = -1;
        if (savedInstanceState != null) {
            idDistributoreSalvato = savedInstanceState.getInt("idDistributore", -1);
        }
        this.idDistributore = intent.getIntExtra(MapsActivity.EXTRA_MESSAGE, idDistributoreSalvato);
        if(idDistributore == -1) {
            idDistributore =  restoreIdDistributore();
        }
        Toast toast = Toast.makeText(getApplicationContext(), "visualizzo distributore con id = " + idDistributore, Toast.LENGTH_SHORT);
        toast.show();

        String getUrl = mUrl + "?" + "idDistributore=" + idDistributore;
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
                            //Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                            //toast.show();
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

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
        saveIdDistributore();
    }

    // solo per i cambiamenti di orientazione;
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        // savedInstanceState.putBoolean("MyBoolean", true);
        //savedInstanceState.putDouble("myDouble", 1.9);
        savedInstanceState.putInt("idDistributore", idDistributore);
        //savedInstanceState.putString("MyString", "Welcome back to Android");
        // etc.
    }

    private void saveIdDistributore() {
        if (idDistributore != -1) {
            SharedPreferences prefs = AppSingleton.getInstance(this).distributoriPreferences();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("idDistributore", idDistributore);
            editor.commit();
        }
    }

    private int restoreIdDistributore() {
        SharedPreferences prefs = AppSingleton.getInstance(this).distributoriPreferences();
        int idDistributore = -1;
        if (prefs != null) {
            idDistributore = prefs.getInt("idDistributore", -1);

        }
        return idDistributore;
    }


}
