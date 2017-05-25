package it.unisalento.drinkssnacks.singleton;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.auth0.android.jwt.JWT;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by aguinaldo on 18/04/2017.
 */

public class AppSingleton {
    private final static String SHARED_PREFERENCES_DISTRIBUTORI = "SharedPreferencesDistributori";
    private static AppSingleton mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;


    private AppSingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized AppSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppSingleton(context);
        }
        return mInstance;
    }

    public static String getSharedPreferencesDistributori() {
        return SHARED_PREFERENCES_DISTRIBUTORI;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public SharedPreferences distributoriPreferences() {
        SharedPreferences prefs = null;
        try {
            prefs = mCtx.getSharedPreferences(SHARED_PREFERENCES_DISTRIBUTORI, MODE_PRIVATE);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return prefs;
    }

    public Boolean isTokenValid() {
        SharedPreferences prefs = null;
        String token;
        prefs = distributoriPreferences();
        if (prefs != null) {
            token = prefs.getString("token", null);
            if (token != null) {
                JWT jwt = new JWT(token);
                if (!jwt.isExpired(20))
                    return true;
            }
            prefs.edit().putString("token", null);
            prefs.edit().commit();
        }

        return false;
    }
}
