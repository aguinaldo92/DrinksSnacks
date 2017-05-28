package it.unisalento.drinkssnacks.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import it.unisalento.drinkssnacks.R;
import it.unisalento.drinkssnacks.model.ProdottoDetailModel;
import it.unisalento.drinkssnacks.singleton.AppSingleton;
import it.unisalento.drinkssnacks.util.PasswordUtils;
import it.unisalento.drinkssnacks.volley.JsonObjectProtectedRequest;

public class FeedbackActivity extends AppCompatActivity {

    private final String mUrl = "http://distributori.ddns.net:8080/distributori-rest/feedback.json";
    // views
    TextView textViewTextFeedback;
    Button btnInvia;
    Button btnAnnulla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        textViewTextFeedback = (TextView) findViewById(R.id.activity_feedback_text);
        btnInvia = (Button) findViewById(R.id.activity_feedback_btn_invia);
        btnAnnulla = (Button) findViewById(R.id.activity_feedback_btn_annulla);

        btnInvia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = AppSingleton.getInstance(getApplicationContext()).fetchToken();
                int idPersona = AppSingleton.getInstance(getApplicationContext()).fetchIdPersona();
                if(token != null && idPersona != -1){
                JSONObject params = new JSONObject();
                try {
                    params.put("idPersona", idPersona );
                    params.put("testo", textViewTextFeedback.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectProtectedRequest jsObjRequest = new JsonObjectProtectedRequest(Request.Method.POST, mUrl, params, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        String result = "result";
                        Boolean isResultOK = response.optBoolean(result, false);
                        CharSequence text = "Response: " + response.toString();
                        if (isResultOK) {
                            text = "Feedback consegnato";
                            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                            toast.show();
                            finish();
                        }
                        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Errore", Toast.LENGTH_SHORT);
                        toast.show();

                    }
                },token );
                AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);

            }
            }
        });

        btnAnnulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
