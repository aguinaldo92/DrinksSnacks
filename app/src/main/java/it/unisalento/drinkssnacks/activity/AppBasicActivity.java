package it.unisalento.drinkssnacks.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import it.unisalento.drinkssnacks.R;
import it.unisalento.drinkssnacks.singleton.AppSingleton;
import it.unisalento.drinkssnacks.subcriber.SubscriptionManager;

/**
 * Created by andrea on 30/05/2017.
 */

public class AppBasicActivity extends AppCompatActivity {


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (AppSingleton.getInstance(getApplicationContext()).isTokenSavedValid()) {
            menu.findItem(R.id.action_feedback).setVisible(true);
            menu.findItem(R.id.action_login).setVisible(false);
            menu.findItem(R.id.action_logout).setVisible(true);
        } else {
            menu.findItem(R.id.action_feedback).setVisible(false);
            menu.findItem(R.id.action_login).setVisible(true);
            menu.findItem(R.id.action_logout).setVisible(false);
        }
        menu.findItem(R.id.action_notification).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        SubscriptionManager subscriptionManager;
        switch (item.getItemId()) {
            case R.id.action_feedback:
                intent = new Intent(this, FeedbackActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_login:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_goto_map:
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_logout:
                AppSingleton.getInstance(getApplicationContext()).invalidateToken();
                subscriptionManager = new SubscriptionManager(getApplicationContext());
                subscriptionManager.unsubscribeAll();
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
