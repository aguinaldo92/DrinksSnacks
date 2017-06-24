package it.unisalento.drinkssnacks.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;

import org.json.JSONObject;

import it.unisalento.drinkssnacks.R;
import it.unisalento.drinkssnacks.adapter.RowProdottiDistributoreAdapter;
import it.unisalento.drinkssnacks.model.ProdottoDetailModel;
import it.unisalento.drinkssnacks.model.ProdottoDistributoreModel;
import it.unisalento.drinkssnacks.model.StabilimentoModel;
import it.unisalento.drinkssnacks.singleton.AppSingleton;
import it.unisalento.drinkssnacks.volley.JsonObjectProtectedRequest;


public class DettagliProdottoActivity extends AppCompatActivity {
    public static final String EXTRA_PRODOTTODISTRIBUTOREMODEL = "prodottoDistributoreModel";
    public static final String EXTRA_IDDISTRIBUTORE = "idDistributore";
    private final static int REQUEST_CODE_NEW_ACTIVITY_LOGIN = 100;
    // nome canonico di questa classe tale che la login sa a chi deve riferire il risultato.
    private final static String TAG = DettagliProdottoActivity.class.getCanonicalName();
    private final String mUrl = "http://distributori.ddns.net:8080/distributori-rest/prodotto_detail.json";
    private final String baseUrlImage = "http://distributori.ddns.net:8080/distributori/";
    // idDIstributore passato come intent dall'adapter
    private int idDistributore;
    // prodottoDistributoreModel passato come intent dall'adapter
    private ProdottoDistributoreModel prodottoDistributoreModel;
    // dettagli del prodotto da ottenere tramite chiamata a mUrl
    private ProdottoDetailModel prodottoDetailModel;


    // views
    private Button btnAcquista;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.idDistributore = getIntent().getIntExtra(RowProdottiDistributoreAdapter.EXTRA_IDDISTRIBUTORE, -1);
        this.prodottoDistributoreModel = getIntent().getParcelableExtra(RowProdottiDistributoreAdapter.EXTRA_PRODOTTODISTRIBUTOREMODEL);
        if (!AppSingleton.getInstance(getApplicationContext()).isTokenSavedValid()) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra("ClassCanonicalName", TAG);
            startActivityForResult(intent, REQUEST_CODE_NEW_ACTIVITY_LOGIN);
        } else if (prodottoDistributoreModel != null) {
            fetchProdottoDetails(prodottoDistributoreModel);
        } else {
            raiseError();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_NEW_ACTIVITY_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                //String result = data.getStringExtra("result");
                fetchProdottoDetails(prodottoDistributoreModel);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.error_login_needed_detailed_info_products), Toast.LENGTH_LONG);
                toast.show();
                Intent intent = new Intent(getApplicationContext(), ProdottiDistributoreListActivity.class);
                intent.putExtra(RowProdottiDistributoreAdapter.EXTRA_IDDISTRIBUTORE, idDistributore);
                startActivity(intent);
            }
        }
    }

    private void fetchProdottoDetails(ProdottoDistributoreModel prodottoDistributoreModel) {
        if (prodottoDistributoreModel.getIdProdotto() < 0) {
            raiseError();
            return;
        }
        String urlGetProdottoDetails = mUrl + "?" + "idProdotto=" + prodottoDistributoreModel.getIdProdotto();
        String token = AppSingleton.getInstance(this).fetchToken();
        JsonObjectProtectedRequest jsObjRequest = new JsonObjectProtectedRequest(Request.Method.GET, urlGetProdottoDetails, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(@NonNull JSONObject response) {
                Gson gson = new Gson();
                JSONObject jsonObject = response.optJSONObject("prodottoDetail");
                //CharSequence text = "Response: " + response.toString();
                if (jsonObject != null) {
                    prodottoDetailModel = gson.fromJson(jsonObject.toString(), ProdottoDetailModel.class);
                    if (prodottoDetailModel != null) {
                        composeView();
                    }
                   /* Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                    toast.show();*/
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getApplicationContext(), "Errore", Toast.LENGTH_SHORT);
                toast.show();

            }
        }, token);
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);

    }

    private void composeView() {
        setContentView(R.layout.activity_dettagli_prodotto);
        imageLoader = AppSingleton.getInstance(getApplicationContext()).getImageLoader();
        // un utente può comprare al massimo 5 oggetti dello stesso tipo per volta
        // in ordine di visualizzazione nella pagina
        TextView textViewNomeProdotto = (TextView) findViewById(R.id.dettagli_activity_nomeProdotto);
        NetworkImageView imageProdottoView = (NetworkImageView) findViewById(R.id.dettagli_activity_img_prodotto);
        TextView textViewDescrizione = (TextView) findViewById(R.id.dettagli_activity_descrizioneProdotto);
        TextView textViewNomeProduttore = (TextView) findViewById(R.id.dettagli_activity_nomeProduttore);
        TextView textViewStabilimento = (TextView) findViewById(R.id.dettagli_activity_stabilimento);
        TextView textViewPrezzoUnitario = (TextView) findViewById(R.id.dettagli_activity_prezzoUnitario);
        TextView textViewQuantitaDisponibile = (TextView) findViewById(R.id.dettagli_activity_quantitaDisponibile);
        TextView textViewSconto = (TextView) findViewById(R.id.dettagli_activity_sconto);

        btnAcquista = (Button) findViewById(R.id.dettagli_activity_btn_acquista);
        TextView textViewIngredienti = (TextView) findViewById(R.id.dettagli_activity_ingredienti);
        TextView textViewPreparazione = (TextView) findViewById(R.id.dettagli_activity_preparazione);

        // set values
        textViewNomeProdotto.setText(prodottoDetailModel.getNome());
        imageProdottoView.setImageUrl(baseUrlImage + prodottoDetailModel.getFoto256(), imageLoader);
        textViewDescrizione.setText(prodottoDetailModel.getDescrizione());
        textViewNomeProduttore.setText(prodottoDetailModel.getProduttore().getNome());
        StabilimentoModel stabilimentoModel = prodottoDetailModel.getProduttore().getStabilimento();
        String stabilimentoText = stabilimentoModel.getCitta() + " " + "(" + stabilimentoModel.getProvincia() + ")";
        textViewStabilimento.setText(stabilimentoText);
        textViewPrezzoUnitario.setText(getString(R.string.prezzo_in_euro,prodottoDetailModel.getPrezzo()));
        String quantitaDisponibile = String.valueOf(prodottoDistributoreModel.getQuantita());
        textViewQuantitaDisponibile.setText(quantitaDisponibile);
        Float scontoFloat = Float.parseFloat(prodottoDistributoreModel.getSconto()) * 100f;
        Integer scontoInteger = Math.round(scontoFloat);
        textViewSconto.setText(getString(R.string.sconto, String.valueOf(scontoInteger)));
        textViewIngredienti.setText(prodottoDetailModel.getIngredienti());
        textViewPreparazione.setText(prodottoDetailModel.getPreparazione());
        if (prodottoDistributoreModel.getQuantita() > 0) {
           btnAcquista.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentAcquista = new Intent(getApplicationContext(), AcquistaActivity.class);
                    intentAcquista.putExtra(EXTRA_PRODOTTODISTRIBUTOREMODEL, prodottoDistributoreModel);
                    intentAcquista.putExtra(EXTRA_IDDISTRIBUTORE, idDistributore);
                    startActivity(intentAcquista);
                }
            });
        } else {
            btnAcquista.setClickable(false);
            btnAcquista.setText("Prodotto non disponibile");
        }

    }


    private void raiseError() {
        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.error_generic), Toast.LENGTH_LONG);
        toast.show();
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

}
