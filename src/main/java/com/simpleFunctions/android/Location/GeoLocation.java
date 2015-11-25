package com.simpleFunctions.android.Location;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.*;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;
import com.simpleFunctions.android.R;

import java.util.List;
import java.util.Locale;

/**
 * Created on 20-05-14.
 * Provides a Location service.
 */
public class GeoLocation extends Service implements LocationListener {

    private final Context myContext;
    private final Activity myActivity;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    //flag for asking location services
    boolean askedBefore = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GeoLocation(Activity activity) {

        Context myContext1;
        this.myActivity = activity;

        try {
            myContext1 = activity.getApplicationContext();
        }
        catch(NullPointerException e){
            myContext1 = activity.getBaseContext();
        }

        this.myContext = myContext1;
        getLocation();
    }

    public GeoLocation(Activity activity, boolean stopMessages) {
        Context myContext1;
        this.myActivity = activity;

        try {
            myContext1 = activity.getApplicationContext();
        }
        catch(NullPointerException e){
            myContext1 = activity;
        }

        this.myContext = myContext1;
        setAskedBefore(stopMessages);
        getLocation();
    }

    public GeoLocation(Context context) {

        this.myContext = context;
        myActivity = null;
        getLocation();
    }


    /**
     * Deactivates/Activates the automatic settings dialog call.
     * @param bol True for not asking next time, false for asking.
     */
    public void setAskedBefore(boolean bol){
        askedBefore = bol;
    }

    public boolean isEnabled() {

        locationManager = (LocationManager) myContext
                .getSystemService(LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isGPSEnabled && !isNetworkEnabled) return false;
        else return true;

    }

    public Location getLocation() {
        try {
            if(!isEnabled() && !askedBefore && isActivityValid(myActivity)) showSettingsAlert();

            if(isEnabled())
            {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Allows to get the locality where the device is, based in location data.
     * @return a String
     */
    public String getLocality() {

        Geocoder geocoder = new Geocoder(myContext, Locale.getDefault());
        try {
            List<Address> place = geocoder.getFromLocation(getLatitude(), getLongitude(), 1);
            if (place.size() > 0) {
                String locality = place.get(0).getLocality();
                return locality;
            }
            return "Here";
        } catch (java.io.IOException | NullPointerException e) {
            return "";
        }
    }

    /**
     * Allows to get the locality where the device is, based in location data.
     * @return a String
     */
    public String getLocalityByLatlng(Double lat, Double lng) {

        Geocoder geocoder = new Geocoder(myContext, Locale.getDefault());
        try {
            List<Address> place = geocoder.getFromLocation(lat, lng, 1);
            if (place.size() > 0) {
                String locality = place.get(0).getLocality();
                return locality;
            }
            return "Here";
        } catch (java.io.IOException | NullPointerException e) {
            return "";
        }
    }

    /**
     * Allows to get the address where the device is, based in location data.
     * @return a String
     */
    public String getAddress() {

        Geocoder geocoder = new Geocoder(myContext, Locale.getDefault());
        try {
            List<Address> place = geocoder.getFromLocation(getLatitude(), getLongitude(), 1);
            if (place.size() > 0) {
                String referencePoint = place.get(0).getCountryName() + " " +
                        place.get(0).getLocality() + " " + place.get(0).getAddressLine(0);
                return referencePoint;
            }
            return "Here";
        } catch (java.io.IOException | NullPointerException e) {
            return "";
        }
    }

    /**
     * Allows to get the address where the device is, based in location data.
     * @return a String
     */
    public String getAddress(Double latitude, Double longitude) {

        Geocoder geocoder = new Geocoder(myContext, Locale.getDefault());
        try {
            List<Address> place = geocoder.getFromLocation(latitude, longitude, 1);
            if (place.size() > 0) {
                String referencePoint = place.get(0).getCountryName() + " " +
                        place.get(0).getLocality() + " " + place.get(0).getAddressLine(0);
                return referencePoint;
            }
            return "Here";
        } catch (java.io.IOException | NullPointerException e) {
            return "";
        }
    }


    /**
     * Testing method. Determines if the conduct of the getAddress() Method is callable.
     * @return
     */
    public int ShowAddress() {

        Geocoder geocoder = new Geocoder(myContext, Locale.getDefault());
        try {
            List<Address> place = geocoder.getFromLocation(getLatitude(), getLongitude(), 1);
            if (place.size() > 0) {
                String referencePoint = place.get(0).getCountryName() + " " +
                        place.get(0).getLocality() + " " + place.get(0).getAddressLine(0);
                Toast.makeText(myContext, referencePoint, Toast.LENGTH_LONG);                
                return 2;
            }
            return 1;
        } catch (java.io.IOException | NullPointerException e) {
            return 0;
        }
    }

    /**
     * Shows a google map windows with the current location.
     * @param activity a subclass of Activity to provide access to the services.
     * @param <T> any subtype of activity.
     */
    public <T extends Activity> void showLocation(T activity) {

        Intent googleMaps = new Intent(Intent.ACTION_VIEW,
                android.net.Uri.parse("http://maps.google.com/maps?q=loc:" + getLatitude() + "," + getLongitude() + "(" + location + ")"));
        googleMaps.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(googleMaps);
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GeoLocation.this);
        }
    }

    /**
     * Allows to get the current latitude. The getLocation() method must be called at least once before.
     * @return a double representing the latitude.
     */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    public void setLatitude(Double Latitude){
        latitude = Latitude;
    }

    /**
     * Allows to get the current longitude. The getLocation() method must be called at least once before.
     * @return a double representing the longitude.
     */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    public void setLongitude(Double Longitude){
        longitude = Longitude;
    }

    public screenPoints getScreenPoints(MapView mapView){

        float pisteX;
        float pisteY;

        Projection projection = mapView.getProjection();

        Point pt = new Point();
        GeoPoint gie = new GeoPoint((int)latitude, (int)longitude);
        projection.toPixels(gie, pt);
        pisteX = pt.x;
        pisteY = pt.y;

        screenPoints screen = new screenPoints();
        screen.setX(pisteX);
        screen.setY(pisteY);

        return screen;
    }

    /**
     * Checks if GPS/wifi are enabled.
     * @return a boolean representing the possibility to get location data.
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Shows the settings alert dialog to enable wifi/GPS. When pressed,
     * Settings button will launch the Settings Options.
     * */
    public void showSettingsAlert(){
        if(isActivityValid(myActivity)){
            askedBefore = true;
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(myActivity);

            // Setting Dialog Title
            alertDialog.setTitle(R.string.setting_location);

            // Setting Dialog Message
            alertDialog.setMessage(R.string.ask_location);

            // On pressing Settings button
            alertDialog.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myActivity.startActivity(intent);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }
    }

    private boolean isActivityValid(Activity activity){
        boolean result = true;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){

            if(activity == null || activity.isFinishing() || activity.isDestroyed() ){
                result = false;
            }
        }
        else{
            if(activity == null || activity.isFinishing()){
                result = false;
            }
        }
        return result;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
