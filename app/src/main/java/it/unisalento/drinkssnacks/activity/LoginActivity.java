package it.unisalento.drinkssnacks.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import it.unisalento.drinkssnacks.R;
import it.unisalento.drinkssnacks.singleton.AppSingleton;
import it.unisalento.drinkssnacks.util.PasswordUtils;
import it.unisalento.drinkssnacks.volley.JsonObjectResponseWithHeadersRequest;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppBasicActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */

    //////   andrea.aguinaldo.licastro@gmail.com
    //////        admin
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private final String mLoginUrl = "http://distributori.ddns.net:8080/distributori-rest/login.json";
    private final String mRegistrationUrl = "http://distributori.ddns.net:8080/distributori-rest/registrazione.json";
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mEmailSignInButton;
    private Button mEmailSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            JSONObject params = new JSONObject();
            try {
                params.put("email", mEmailView.getText().toString());
                params.put("password", PasswordUtils.getSha256(mPasswordView.getText().toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectResponseWithHeadersRequest jsObjRequest = new JsonObjectResponseWithHeadersRequest(Request.Method.POST, mLoginUrl, params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        String result = "result";
                        CharSequence text = "Response: " + response.toString();
                        Boolean isResultOK = response.optBoolean(result, false);
                        if (isResultOK) { // get token -> save token in shared preference whit editor and private context
                            JSONObject headers = response.optJSONObject("headers");
                            int idPersona = response.optInt("idUtente", -1);
                            String bearer = headers.optString("Authorization", null);
                            String[] parts = bearer.split(" ");
                            String token = parts[2];
                            if (AppSingleton.getInstance(getApplicationContext()).isTokenValid(token) && idPersona != -1) {
                                SharedPreferences.Editor editor = getSharedPreferences(AppSingleton.getSharedPreferencesDistributori(), MODE_PRIVATE).edit();
                                editor.putString("token", token);
                                editor.putInt("idPersona", idPersona);
                                editor.commit();

                                //imposto il risultato per l'activity chiamante
                                returnResultToCallerActivity(Activity.RESULT_OK);
                                showProgress(false);
                                finish();
                            } else { // TODO: gestione credenziali errate
                                showProgress(false);
                                text = getString(R.string.error_generic);
                            }
                        }

                        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                        toast.show();


                    } catch (Exception e) {
                        Log.i(TAG, "Impossibile processare il token : " + e.getStackTrace());
                    }


                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    mEmailView.setError(getString(R.string.error_generic));
                    mPasswordView.setError(getString(R.string.error_generic));
                    Toast toast = Toast.makeText(getApplicationContext(), "Errore", Toast.LENGTH_SHORT);
                    toast.show();

                }
            });

            // Access the RequestQueue through your singleton class.
            AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);

        }
/*
        mAuthTask = new UserLoginTask(email, password);
        mAuthTask.execute((Void) null);
        */

    }

    private void attemptRegistration() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            JSONObject params = new JSONObject();
            try {
                params.put("email", mEmailView.getText().toString());
                params.put("password", PasswordUtils.getSha256(mPasswordView.getText().toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, mRegistrationUrl, params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {

                    String result = "result";
                    CharSequence text = "Response: " + response.toString();
                    Boolean isLogged = response.optBoolean(result, false);
                    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                    toast.show();
                    if (isLogged) {
                        String token = response.optString("token", "token non valido");
                        int idUtente = response.optInt("idUtente", -1);
                        SharedPreferences.Editor editor = getSharedPreferences(AppSingleton.getSharedPreferencesDistributori(), MODE_PRIVATE).edit();
                        editor.putString("token", token);
                        editor.putInt("idUtente", idUtente);
                        editor.commit();
                        showProgress(false);
                        returnResultToCallerActivity(Activity.RESULT_OK);

                        finish();

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
            AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
        }
    }

    // ?? a che serve TODO: implementare o cancellare
    private void returnError() {

    }

    private void returnResultToCallerActivity(int ActivityResult) {
        Intent intentReceived = getIntent();
        String activityCallerName = intentReceived.getStringExtra("ClassCanonicalName");
        try {
            Class<?> activityCallerClass = Class.forName(activityCallerName);

            Intent returnIntent = new Intent(getApplicationContext(), activityCallerClass);
            setResult(Activity.RESULT_OK, returnIntent);
        } catch (ClassNotFoundException e) {
            Log.i(TAG, "Impossibile trovare la classe Chiamante. : " + e.getStackTrace());
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

    }

}

