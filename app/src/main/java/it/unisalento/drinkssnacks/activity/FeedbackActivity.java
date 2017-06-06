package it.unisalento.drinkssnacks.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;

import it.unisalento.drinkssnacks.R;
import it.unisalento.drinkssnacks.singleton.AppSingleton;
import it.unisalento.drinkssnacks.volley.StringProtectedRequest;

public class FeedbackActivity extends AppBasicActivity {
    // necessario a discernere quale activity viene chiamata
    private final static int REQUEST_CODE_NEW_ACTIVITY_LOGIN = 101;
    private final static String TAG = DettagliProdottoActivity.class.getCanonicalName();
    private final String mUrl = "http://distributori.ddns.net:8080/distributori-rest/feedback.json";
    // views
    EditText textViewTextFeedback;
    Button btnInvia;
    Button btnAnnulla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AppSingleton.getInstance(getApplicationContext()).isTokenSavedValid()) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra("ClassCanonicalName", TAG);
            startActivityForResult(intent, REQUEST_CODE_NEW_ACTIVITY_LOGIN);
        } else {

            setContentView(R.layout.activity_feedback);
            textViewTextFeedback = (EditText) findViewById(R.id.activity_feedback_text);
            btnInvia = (Button) findViewById(R.id.activity_feedback_btn_invia);
            btnAnnulla = (Button) findViewById(R.id.activity_feedback_btn_annulla);

            btnInvia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String token = AppSingleton.getInstance(getApplicationContext()).fetchToken();
                    int idPersona = AppSingleton.getInstance(getApplicationContext()).fetchIdPersona();
                    if (token != null && idPersona != -1) {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("idPersona", String.valueOf(idPersona));
                        params.put("testo", String.valueOf(textViewTextFeedback.getText()).trim());
                        StringProtectedRequest jsObjRequest = new StringProtectedRequest(token, params, Request.Method.POST, mUrl, new Response.Listener<String>() {

                            @Override
                            public void onResponse(@NonNull String responseString) {
                                String result = "result";
                                try {
                                    JSONObject response = new JSONObject(responseString);
                                    Boolean isResultOK = response.optBoolean(result, false);
                                    CharSequence text = "Response: " + response.toString();
                                    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                                    toast.show();
                                    if (isResultOK) {
                                        text = "Feedback consegnato";
                                        toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                                        toast.show();
                                        finish();
                                    }
                                } catch (Exception e) {

                                    e.printStackTrace();
                                    Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                                    Log.e(TAG, e.getLocalizedMessage() + e.getStackTrace());
                                    toast.show();
                                }
                            }
                        }
                                , new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast toast = Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
                                Log.e(TAG, error.getLocalizedMessage() + error.getStackTrace());
                                toast.show();
                            }
                        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_NEW_ACTIVITY_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                //String result = data.getStringExtra("result");
                this.recreate();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.error_login_needed_detailed_info_products), Toast.LENGTH_LONG);
                toast.show();
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);

            }
        }
    }


}
