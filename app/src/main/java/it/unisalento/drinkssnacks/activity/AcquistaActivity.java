package it.unisalento.drinkssnacks.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import it.unisalento.drinkssnacks.R;
import it.unisalento.drinkssnacks.adapter.RowProdottiDistributoreAdapter;
import it.unisalento.drinkssnacks.model.AcquistoModel;
import it.unisalento.drinkssnacks.model.ProdottoDistributoreModel;
import it.unisalento.drinkssnacks.singleton.AppSingleton;

public class AcquistaActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE_RESULT = AcquistaActivity.class.getCanonicalName() + "_result";
    public static final String EXTRA_MESSAGE_CONTENT = AcquistaActivity.class.getCanonicalName() + "_content";
    private static final String TAG = AcquistaActivity.class.getSimpleName();
    private final String baseUrlImage = "http://distributori.ddns.net:8080/distributori/";
    private final String mUrl = "http://distributori.ddns.net:8080/distributori-rest/acquista.json";
    private int mQuantitaScelta = 1;
    private int maxQuantitaAcquistabile = 5;
    private ProdottoDistributoreModel prodottoDistributoreModel;
    private int idDistributore = -1;
    private Button btnAcquista;
    private TextView textViewQuantitaScelta;
    private ImageLoader imageLoader;
    private Intent intent = new Intent();
    private int idUtente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acquista);
        imageLoader = AppSingleton.getInstance(getApplicationContext()).getImageLoader();
        Intent intentReceived = getIntent();
        prodottoDistributoreModel = intentReceived.getParcelableExtra(RowProdottiDistributoreAdapter.EXTRA_PRODOTTODISTRIBUTOREMODEL);
        idDistributore = intentReceived.getIntExtra(RowProdottiDistributoreAdapter.EXTRA_IDDISTRIBUTORE, idDistributore);
        if (prodottoDistributoreModel.getQuantita() < 5) {
            maxQuantitaAcquistabile = prodottoDistributoreModel.getQuantita();
        }
        TextView textViewNomeProdotto = (TextView) findViewById(R.id.acquista_activity_text_nome_prodotto);
        TextView textViewNomeProduttore = (TextView) findViewById(R.id.acquista_activity_text_nome_produttore);
        NetworkImageView imageProdottoView = (NetworkImageView) findViewById(R.id.acquista_activity_img_prodotto);
        imageProdottoView.setImageUrl(baseUrlImage + prodottoDistributoreModel.getFoto256(), imageLoader);
        btnAcquista = (Button) findViewById(R.id.acquista_activity_btn_acquista);
        SeekBar seekBarQuantita = (SeekBar) findViewById(R.id.acquista_activity_seekbar_quantita);
        textViewNomeProdotto.setText(prodottoDistributoreModel.getNome());
        textViewNomeProduttore.setText(prodottoDistributoreModel.getProduttore());
        textViewQuantitaScelta = (TextView) findViewById(R.id.acquista_activity_text_quantita_scelta);
        seekBarQuantita.setMax(maxQuantitaAcquistabile);

        seekBarQuantita.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int actualProgress = progress + 1;
                setmQuantitaScelta(actualProgress);
                textViewQuantitaScelta.setText(String.valueOf(actualProgress));
                btnAcquista.setText(getString(R.string.acquista_activity_btn_acquista,String.valueOf(getPrice(actualProgress, prodottoDistributoreModel.getPrezzo()))));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                textViewQuantitaScelta.setTypeface(null, Typeface.BOLD);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewQuantitaScelta.setTypeface(null, Typeface.NORMAL);
            }
        });
        textViewNomeProdotto.setText(prodottoDistributoreModel.getNome());

        btnAcquista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //chiamo il rest service per effettuare l'acquisto riutilizzando il distributore model
                // ma CAMBIANDO il significato di quantita, da "quantita disponibile" a "quantita da acquistare".
                JSONObject params = fetchParams();
                if(params != null) {
                    JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, mUrl, params, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(@NonNull JSONObject response) {

                            String result = "result";
                            CharSequence text = "Response: " + response.toString();

                            Boolean acquistoEffettuato = response.optBoolean(result, false);
                            intent.putExtra(EXTRA_MESSAGE_RESULT, acquistoEffettuato);
                            if (acquistoEffettuato) {
                                intent.putExtra(EXTRA_MESSAGE_CONTENT, prodottoDistributoreModel);
                            }
                            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                            toast.show();
                            startActivity(intent);

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Errore", Toast.LENGTH_SHORT);
                            toast.show();

                        }
                    });

                    // Access the RequestQueue through your singleton class.
                    AppSingleton.getInstance(AcquistaActivity.this.getApplicationContext()).addToRequestQueue(jsObjRequest);

                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Errore", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }

    private JSONObject fetchParams(){
        AcquistoModel acquistoModel = new AcquistoModel();
        acquistoModel.setIdDistributore(idDistributore);
        acquistoModel.setIdPersona(fetchIdUser()); // settare l'ID del cliente che fa l'acquisto
        acquistoModel.setIdProdotto(prodottoDistributoreModel.getIdProdotto());
        acquistoModel.setIdProdottoErogato(prodottoDistributoreModel.getIdProdottoErogato());
        acquistoModel.setQuantita(mQuantitaScelta);
        acquistoModel.setTotale_spesa(getPrice(mQuantitaScelta, prodottoDistributoreModel.getPrezzo()));
        Gson gson = new Gson();
        String jsonString = gson.toJson(acquistoModel);
        JSONObject params = null;
        try {
            params = new JSONObject(jsonString);
            return params;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    private int fetchIdUser() {
        SharedPreferences prefs = null;
        int idUtente = -1;
        try {
            prefs = getApplicationContext().getSharedPreferences(AppSingleton.getSharedPreferencesDistributori(), MODE_PRIVATE);
            idUtente = prefs.getInt("idUtente", -1);
            return idUtente;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public void setmQuantitaScelta(int mQuantitaScelta) {
        this.mQuantitaScelta = mQuantitaScelta;
    }

    private BigDecimal getPrice(int quantita, String prezzo) {
        return new BigDecimal(prezzo).multiply(new BigDecimal(quantita));
    }
}
