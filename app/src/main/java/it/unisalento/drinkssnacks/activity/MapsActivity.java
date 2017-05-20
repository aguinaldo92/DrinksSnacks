package it.unisalento.drinkssnacks.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import it.unisalento.drinkssnacks.R;
import it.unisalento.drinkssnacks.model.CategorieForniteModel;
import it.unisalento.drinkssnacks.model.DistributoreModel;
import it.unisalento.drinkssnacks.singleton.AppSingleton;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static final String EXTRA_MESSAGE = MapsActivity.class.getCanonicalName();
    private static final String TAG = MapsActivity.class.getSimpleName();
    // zoom of camera on the world view
    private static final int DEFAULT_ZOOM = 15;
    // permission access fine location
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    // default location if not available (ROME)
    private final LatLng mDefaultLocation = new LatLng(41.890251, 12.492373);
    private final Double mUserChosenDistance = 100d;
    private final int duration = Toast.LENGTH_LONG;
    private final String mUrl = "http://distributori.ddns.net:8080/distributori-rest/distributori.json";
    private GoogleMap mMap;
    // client to connect with google apis
    private GoogleApiClient mGoogleApiClient;
    private boolean mLocationPermissionGranted;
    private CameraPosition mCameraPosition;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // test url  windows pc
    //private final String mUrl = "http://192.168.1.105:8080/distributori-rest/distributori.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */,
                            this /* OnConnectionFailedListener */)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        MapsActivity.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }


    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Do other setup activities here too, as described elsewhere in this tutorial.

        // check permission for location
        checkPermissionLocation();
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        getDistributoriLocations(mLastKnownLocation);


        //iscrizione a un topic generico del sistema
        FirebaseMessaging.getInstance().subscribeToTopic("DrinksSnacks");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }

    @SuppressWarnings("MissingPermission")
    private void getDeviceLocation() {

        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @SuppressWarnings("MissingPermission")
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }


        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }


    private void getDistributoriLocations(Location mLastKnownLocation) {
        if (mMap == null) {
            return;
        }
        String lat, lon, distanza;
        if (mLastKnownLocation != null) {
            lat = String.valueOf(mLastKnownLocation.getLatitude());
            lon = String.valueOf(mLastKnownLocation.getLongitude());
        } else {
            lat = String.valueOf(mDefaultLocation.latitude);
            lon = String.valueOf(mDefaultLocation.longitude);
        }
        distanza = String.valueOf(mUserChosenDistance);
        String getUrl = mUrl + "?" + "lat=" + lat + "&" + "lon=" + lon + "&" + "distanza=" + distanza;
        final String distributoriArray = "distributori";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, getUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        Gson gson = new Gson();

                        JSONArray jsonArray = response.optJSONArray(distributoriArray);
                        CharSequence text = "Response: " ;
                        if (jsonArray != null) {
                            DistributoreModel[] distributori = gson.fromJson(jsonArray.toString(), DistributoreModel[].class);
                            if (distributori != null && distributori.length > 0) {
                                for (DistributoreModel distributore : distributori) {
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(new LatLng(distributore.getLat(), distributore.getLon()));
                                    String label = distributore.getIndirizzo();
                                    markerOptions.title(label);

                                    mMap.addMarker(markerOptions).setTag(distributore);
                                    Log.i(TAG, "distirbutore indirizzo = " + distributore.getIndirizzo());
                                    DistributoriCustomInfoWindowAdapter distributoriCustomInfoWindowAdapter = new DistributoriCustomInfoWindowAdapter();

                                    mMap.setInfoWindowAdapter(distributoriCustomInfoWindowAdapter);
                                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(Marker marker) {
                                            DistributoreModel distributoreModel = ((DistributoreModel) marker.getTag());
                                            Intent intent = new Intent(MapsActivity.this, ProdottiDistributoreListActivity.class);
                                            intent.putExtra(EXTRA_MESSAGE, distributoreModel.getIdDistributore());
                                            Log.i(TAG, "Cliccato su marker con distributore id = " + distributoreModel.getIdDistributore());
                                            Toast toast = Toast.makeText(getApplicationContext(), "Cliccato su marker con distributore id = " + ((DistributoreModel) marker.getTag()).getIdDistributore(), duration);
                                            toast.show();
                                            startActivity(intent);
                                        }
                                    });

                                    Log.i(TAG, distributore.getPosizioneEdificio());
                                    for (CategorieForniteModel categorieForniteModel : distributore.getListCategorieFornite()) {
                                        Log.i(TAG, categorieForniteModel.getNome());
                                    }

                                }
                            }
                        }


                        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                        toast.show();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Errore", duration);
                        toast.show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        AppSingleton.getInstance(this).addToRequestQueue(jsObjRequest);

    }

    private void checkPermissionLocation() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }


    public class DistributoriCustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            DistributoreModel distributoreModel = (DistributoreModel) marker.getTag();
            View view = getLayoutInflater().inflate(R.layout.marker_info_window, null);
            ListView listView = (ListView) view.findViewById(R.id.info_window_list_view);
            ArrayList<String> categorieForniteNames = new ArrayList<>();
            for (CategorieForniteModel categorieForniteModel : distributoreModel.getListCategorieFornite()) {
                categorieForniteNames.add(categorieForniteModel.getNome());
            }
            ArrayAdapter<String> mCategoriesMarkerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.text_view_only, categorieForniteNames);
            listView.setAdapter(mCategoriesMarkerAdapter);
            Log.d(TAG, "indirizzi: + " + distributoreModel.getIndirizzo());
            Log.d(TAG, "num cunt adapter items: + " + mCategoriesMarkerAdapter.getCount());
            TextView textViewAddress = (TextView) view.findViewById(R.id.info_window_address);
            TextView textViewPosizione = (TextView) view.findViewById(R.id.info_window_posizione);
            textViewAddress.setText(distributoreModel.getIndirizzo());
            textViewPosizione.setText(distributoreModel.getPosizioneEdificio());

            return view;
            // return null;
        }
    }


}
