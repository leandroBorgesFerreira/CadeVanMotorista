package br.com.simplepass.cadevanmotorista.location;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.domain.Van;
import br.com.simplepass.cadevanmotorista.domain_realm.EntityInsidePlace;
import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.domain_realm.Place;
import br.com.simplepass.cadevanmotorista.utils.Constants;
import br.com.simplepass.cadevanmotorista.utils.Utils;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * This class is responsable to manage the location services of the app
 */
public class Locator implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        LocateManager{

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private Activity mActivity;
    private Van mVan;

    private DistanceToDestinationRequester mDistanceToDestinationRequester;
    private static Locator ourInstance = new Locator();
    public boolean isDelivering;
    private PathManager mPathManager;

    private boolean show;

    private static final int UPDATE_INTERVAL = 7500; //7.5 segundos

    private Locator() {}

    public static Locator getInstance() {
        return ourInstance;
    }

    public PathManager getPathManager() {
        return mPathManager;
    }

    public DistanceToDestinationRequester getDistanceToDestinationRequester() {
        return mDistanceToDestinationRequester;
    }

    /**
     * Start sending position to the server
     *
     * @param activity activity that called this Class
     */
    @Override
    public void start(Activity activity){
        mActivity = activity;

        AccountManager accountManager = AccountManager.get(activity);
        Account account = new Account(Constants.ARG_ACCOUNT_NAME, Constants.ACCOUNT_TYPE);

        try {
            mVan = new Van(Integer.parseInt(accountManager.getUserData(account, "driverId")),
                    accountManager.getUserData(account, "name"),
                    0,
                    0,
                    "",
                    "");

            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        } catch (NumberFormatException e){
            Toast.makeText(activity, "Não foi possível começar a enviar a localização", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Start delivering a path
     *
     * @param path the current path hat is being covered
     */
    @Override
    public void startDelivering(Path path) {
        mPathManager = new PathManager(mActivity, path, mDistanceToDestinationRequester);
        isDelivering = true;
        mDistanceToDestinationRequester =
                new DistanceToDestinationRequester(mActivity, mPathManager.getPath(),
                        mVan, mPathManager.getCurrentPlace());
        mDistanceToDestinationRequester.startMonitoring();
    }


    /**
     * Stops delivering
     */
    @Override
    public void stopDelivering(){
        mPathManager = null;
        mVan.setDirection("");
        isDelivering = false;
        if(mDistanceToDestinationRequester != null) {
            mDistanceToDestinationRequester.stopMonitoriong();
            mDistanceToDestinationRequester = null;
        }
    }

    public boolean isShowing(){return show;}

    /**
     * Connection callback. Caled when the class is connected to GoogleApiClient
     *
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(mActivity,
                ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    /**
     * Method called when a new location is received by the sistem. When a new location is received,
     * this class and many others must be updated to conform with the new location.
     *
     * @param location the most recent location received
     */
    @Override
    public void onLocationChanged(Location location){
        mLastLocation = location;

        mVan.setLatitude(location.getLatitude());
        mVan.setLongitude(location.getLongitude());

        if(isDelivering) {
            StringBuilder stringBuilder = new StringBuilder();

            for(EntityInsidePlace entity : mPathManager.getCurrentPlace().getEntitysInsidePlace()){
                stringBuilder.append(entity.getName()).append(", ");
            }

            mVan.setDirection(stringBuilder.toString());

            for (int i = 0; i < mPathManager.getAllPlaces().size(); i++) {
                Place place = mPathManager.getPlace(i);

                if (location.distanceTo(Utils.getLocationOfPlace(place)) < 150){
                    if(mPathManager.getInsidePlace() == null) {
                        mPathManager.setInsidePlace(mPathManager.getCurrentPlace());
                        mDistanceToDestinationRequester.stopMonitoriong();
                        mVan.setTimeToArrive(mActivity.getString(R.string.very_close));

                        if (!mDistanceToDestinationRequester.mmTimeToArriveSender.evaluateIfSent(
                                mPathManager.getCurrentPlace(),
                                TimeToArriveSender.ARRIVED)) {
                            mDistanceToDestinationRequester.mmTimeToArriveSender.sendArrived(
                                    mPathManager.getPath(),
                                    mPathManager.getCurrentPlace(),
                                    mPathManager.getPath().getDirection());
                        }
                    } else if(!mPathManager.getInsidePlace().equals(mPathManager.getCurrentPlace())){
                        mPathManager.setInsidePlace(mPathManager.getCurrentPlace());
                        mDistanceToDestinationRequester.stopMonitoriong();
                        mVan.setTimeToArrive(mActivity.getString(R.string.very_close));

                        if (!mDistanceToDestinationRequester.mmTimeToArriveSender.evaluateIfSent(
                                mPathManager.getCurrentPlace(),
                                TimeToArriveSender.ARRIVED)) {
                            mDistanceToDestinationRequester.mmTimeToArriveSender.sendArrived(
                                    mPathManager.getPath(),
                                    mPathManager.getCurrentPlace(),
                                    mPathManager.getPath().getDirection());
                        }
                    }
                }
            }

            if(mPathManager.getInsidePlace() != null){
                if(location.distanceTo(Utils.getLocationOfPlace(mPathManager.getCurrentPlace())) > 400){//modificar
                    mPathManager.setInsidePlace(null);
                    mPathManager.nextPlace();

                    if(!mDistanceToDestinationRequester.isMonitoring()){
                        mDistanceToDestinationRequester.startMonitoring();
                    } else{
                        mDistanceToDestinationRequester.restartMonitoring();
                    }
                }
            }

        }

        LocationSender.sendLocation(mVan);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    private void startLocationUpdates(){
        if(mGoogleApiClient.isConnected()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mActivity.checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(
                            mGoogleApiClient, createLocationRequest(), this);

                }
            } else{
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, createLocationRequest(), this);
            }
            show = true;
        } else{
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    startLocationUpdates();
                }
            };

            handler.postDelayed(runnable, 8000);
        }
    }

    @Override
    public void stop(){
        if(isShowing()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }

            mGoogleApiClient = null;
            show = false;
        }
    }

    @Override
    public Location getLastLocation(){
        if(mLastLocation != null){
            return mLastLocation;
        }

        if(mGoogleApiClient != null) {
            if(mGoogleApiClient.isConnected()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (mActivity.checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        return LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient);
                    }
                } else{
                    return LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);
                }
            }
        }

        return null;
    }

}
