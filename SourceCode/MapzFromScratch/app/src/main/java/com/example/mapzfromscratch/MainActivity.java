package com.example.mapzfromscratch;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.widget.EditText;
import android.view.View;
import android.view.KeyEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import android.view.MenuItem;
import android.widget.PopupMenu;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback , PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "MainActivity";

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private MarkerOptions mMarkerOptions;
    private LatLng mOrigin;
    private LatLng mDestination;
    private Polyline mPolyline;
    private LatLng mDevicelocation;
    private List<LatLng> mDummyDevicelocation;
    private String toastString;
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_main);
        popup.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            EditText edittext = (EditText) findViewById(R.id.editTextTextAddress);
            edittext.setShowSoftInputOnFocus(false);
            edittext.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    try
                    {
                        // If the event is a key-down event on the "enter" button
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                                (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            // Perform action on key press
                            //Toast.makeText(MainActivity.this, edittext.getText(), Toast.LENGTH_SHORT).show();
                            mMap.clear();
                            toastString = edittext.getText().toString();
                            GeocodeConverter geocodeConverter = new GeocodeConverter();
                            geocodeConverter.execute(toastString).get();
                            return true;
                        }
                    }
                    catch (Exception e) {
                        Log.d("ERRRORRR   onKey", e.toString());
                        e.printStackTrace();
                    }

                    return false;
                }
            });
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            Log.d("ERROR -----------  onCreate", e.toString());
            e.printStackTrace();
        }



    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.getUiSettings().setZoomControlsEnabled(true);

            getMyLocation();
//            Bundle extras = getIntent().getExtras();
 //           if (extras != null) {
 //               String value = extras.getString("key");
  //              EditText edittext = (EditText) findViewById(R.id.editTextTextAddress);
   //             edittext.setText(value);
   //             mMap.clear();
   //             //toastString = edittext.getText().toString();
   //             GeocodeConverter geocodeConverter = new GeocodeConverter();
    //            geocodeConverter.execute(value).get();
     //           //The key argument here must match that used in the other activity
     //       }
        }
        catch (Exception e) {
            Log.d("ERRORR-------  onMapReady", e.toString());
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == 100) {
            if (!verifyAllPermissions(grantResults)) {
                Toast.makeText(getApplicationContext(), "No sufficient permissions", Toast.LENGTH_LONG).show();
            } else {
                getMyLocation();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, MainActivity2.class);
                startActivity(intent);
                //Toast.makeText(this, "Item 1 clicked",Toast.LENGTH_SHORT).show();
                //button.setText("item 1");
                return true;
            case R.id.action_ViewFriends:
                Intent intentViewFriends = new Intent(this, SeeFriendsActivity.class);
                startActivity(intentViewFriends);
                //Toast.makeText(this, "Item 1 clicked",Toast.LENGTH_SHORT).show();
                //button.setText("item 1");
                return true;
            default:
                return false;
        }
    }
    public void ZoomToROute(  List<LatLng> lstLatLongRoute) {

        if (  lstLatLongRoute == null || lstLatLongRoute.isEmpty()) return;

        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLongRoute)
            bounds.include(latLngPoint);

        int routePadding = 100;
        LatLngBounds latLngBounds = bounds.build();

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }
    private boolean verifyAllPermissions(int[] grantResults) {

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void GetDummyLocations() {
        List<LatLng> lstDummy = new ArrayList<>();
       // lstDummy.add(mDevicelocation);
        try {
            //new ArrayList<LatLng>(){mdevi};
            for (int i = 0; i < 10; i++) {
                LatLng objLatLnj = GetADummy(mDevicelocation);
                lstDummy.add(objLatLnj);
            }
            Collections.shuffle(lstDummy);
            String strUrlNearestRaods = getNearestRoadsUrl(lstDummy);
            GetNereastRoadConverter objNearestRoad = new GetNereastRoadConverter();
            objNearestRoad.execute(strUrlNearestRaods).get();
        }
        catch(Exception e)
        {
            Log.d("ERRORR-------  GetDummyLocations", e.toString());
            e.printStackTrace();
        }
        //return lstDummy;
    }

    public LatLng GetADummy(LatLng latlngVal) {
        //double randomLat = ThreadLocalRandom.current().nextDouble(RoundInteger(latlngVal.latitude) - 1, RoundInteger(latlngVal.latitude) + 1);
        //double randomLng = ThreadLocalRandom.current().nextDouble(RoundInteger(latlngVal.longitude) - 1, RoundInteger(latlngVal.longitude) + 1);
        double randomLat = ThreadLocalRandom.current().nextDouble(RoundInteger(latlngVal.latitude) , RoundInteger(latlngVal.latitude) + 1);
        double randomLng = ThreadLocalRandom.current().nextDouble(RoundInteger(latlngVal.longitude), RoundInteger(latlngVal.longitude) + 1);

        return new LatLng(randomLat, randomLng);
    }

    public int RoundInteger(double dblValue) {
       // int intVal = (int) Math.round(dblValue);
        int intVal = (int) dblValue;
        return intVal;
    }

    public LatLng GetMyDeviceLocation() {
        // getting GPS status
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location Devicelocation = null;
        Double Devicelatitude;
        Double Devicelongitude;

        if (isNetworkEnabled) {
            //check the network permission
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            }
             mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, mLocationListener);

            Log.d("Network", "Network");
            if (mLocationManager != null) {
                Devicelocation = mLocationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (Devicelocation != null) {
                    Devicelatitude = Devicelocation.getLatitude();
                    Devicelongitude = Devicelocation.getLongitude();
                    return new LatLng(Devicelatitude, Devicelongitude);
                }
            }
        } else if (isGPSEnabled) {
            if (Devicelocation == null) {
                //check the network permission
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                }
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,  0, mLocationListener);

                Log.d("GPS Enabled", "GPS Enabled");
                if (mLocationManager != null) {
                    Devicelocation = mLocationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (Devicelocation != null) {
                        Devicelatitude = Devicelocation.getLatitude();
                        Devicelongitude = Devicelocation.getLongitude();
                        return new LatLng(Devicelatitude, Devicelongitude);
                    }
                }
            }
        }
        return null;

    }

    private void getMyLocation() {
        // Getting LocationManager object from System Service LOCATION_SERVICE
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
               // mDestination = new LatLng(location.getLatitude(), location.getLongitude());
                 mOrigin = new LatLng(location.getLatitude(), location.getLongitude());
                mDevicelocation= mOrigin;
                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigin,12));
                if(mDestination !=null && mOrigin.latitude==mDestination.latitude && mOrigin.longitude==mDestination.longitude)
                {
                    return;
                }
                if (mOrigin != null && mDestination != null)
                    drawRoute();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED) {
                //  mMap.setMyLocationEnabled(true);
                  mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,0,mLocationListener);

                mDevicelocation = GetMyDeviceLocation();
                mOrigin=mDevicelocation;
                mDummyDevicelocation =new ArrayList<LatLng>();
                GetDummyLocations();
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(mDevicelocation, 12);
                mMap.moveCamera(update);
                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        // mOrigin=    new LatLng(Double.parseDouble("30.5082551"), Double.parseDouble("-97.678896"));
                        //  mDestination=  new LatLng(Double.parseDouble("30.513532"),Double.parseDouble("-97.7397313"));
                    //    if (mDestination != null) {
                     //       mOrigin = mDestination;
                      //  }
                        if(mOrigin==null)
                        {
                            mOrigin=mDevicelocation;
                        }
                        mDestination = latLng;
                        mMap.clear();
                        mMarkerOptions = new MarkerOptions().position(mDestination).title("Destination");
                        mMap.addMarker(mMarkerOptions);
                        if (mOrigin != null && mDestination != null)
                            drawRoute();


                    }
                });
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    String value = extras.getString("key");
                    EditText edittext = (EditText) findViewById(R.id.editTextTextAddress);
                    edittext.setText(value);
                    mMap.clear();
                    //toastString = edittext.getText().toString();
                    GeocodeConverter geocodeConverter = new GeocodeConverter();
                    geocodeConverter.execute(value);
                    //The key argument here must match that used in the other activity
                }

            } else {
                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, 100);
            }
        }
    }

    private void drawRoute() {
        try {
            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(mOrigin, mDestination);
            DownloadTask downloadTask = new DownloadTask(mOrigin);
            // Start downloading json data from Google Directions API
            downloadTask.execute(url).get();

            for (int i = 0; i < mDummyDevicelocation.size(); i++) {
                //LatLng mOriginNew=    new LatLng(Double.parseDouble("30.5082551"), Double.parseDouble("-97.678896"));
                //LatLng mDestinationNew=  new LatLng(Double.parseDouble("30.513532"),Double.parseDouble("-97.7397313"));
                // List<LatLng> lstDummyLatlng = new ArrayList<LatLng>();
                // lstDummyLatlng.add(mDummyDevicelocation.get(i));
                String urlNew = getDirectionsUrl((LatLng)mDummyDevicelocation.get(i), mDestination);
                DownloadTask downloadTaskNew = new DownloadTask((LatLng)mDummyDevicelocation.get(i));
                // Start downloading json data from Google Directions API
                downloadTaskNew.execute(urlNew).get();
            }
//        //LatLng mOriginNew=    new LatLng(Double.parseDouble("30.5082551"), Double.parseDouble("-97.678896"));
            //       //LatLng mDestinationNew=  new LatLng(Double.parseDouble("30.513532"),Double.parseDouble("-97.7397313"));
            //     String urlNew = getDirectionsUrl(mDevicelocation, mDestination);
//        DownloadTask downloadTaskNew = new DownloadTask();
//        //Start downloading json data from Google Directions API
            //       downloadTaskNew.execute(urlNew);

            //   GeocodeConverter geocodeConverter=new GeocodeConverter();
            //      geocodeConverter.execute(urlNew);

            //LatLng mOriginNew=    new LatLng(Double.parseDouble("30.5082551"), Double.parseDouble("-97.678896"));
            //LatLng mDestinationNew=  new LatLng(Double.parseDouble("30.513532"),Double.parseDouble("-97.7397313"));
            //String urlNew = getDirectionsUrl(mOriginNew, mDestinationNew);
            //DownloadTask downloadTaskNew = new DownloadTask();
            // Start downloading json data from Google Directions API
            //downloadTaskNew.execute(urlNew);
        }
        catch (Exception e) {
            Log.d("ERRORR-------  drawRoute", e.toString());
            e.printStackTrace();
        }


    }
private String getNearestRoadsUrl(List<LatLng> lstLatLng)
{
    try {
        String strInputPoints="";
        for( int i=0;i<lstLatLng.size();i++)
        {
            strInputPoints+=  lstLatLng.get(i).latitude+",";
            strInputPoints+= lstLatLng.get(i).longitude;
            if(i<lstLatLng.size()-1)
            {
                strInputPoints+="|";
            }
        }
        String str_input = "points=" + URLEncoder.encode(strInputPoints, "UTF-8");

        // Key
        String key = "key=" + getString(R.string.google_maps_key);

        // Building the parameters to the web service
        String parameters = str_input + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://roads.googleapis.com/v1/nearestRoads?" +  parameters;

        return url;
    }
    catch(Exception e)
    {
        Log.d("ERRORR-------  getNearestRoadsUrl", e.toString());
        e.printStackTrace();
    }
    return "";
}
    private String getDirectionsUrlFromAddress(String strAddress) {

        try {
            // Origin of route
            String str_address = "address=" + URLEncoder.encode(strAddress, "UTF-8");

            // Key
            String key = "key=" + getString(R.string.google_maps_key);

            // Building the parameters to the web service
            String parameters = str_address + "&" + key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/geocode/" + output + "?" + parameters;

            return url;
        } catch (Exception e) {
            Log.d("ERRORR-------  getDirectionsUrlFromAddress", e.toString());
            e.printStackTrace();
        }
        return "";

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Key
        String key = "key=" + getString(R.string.google_maps_key);

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("ERRORR-------  downloadUrl", e.toString());
            e.printStackTrace();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to download data from Google Directions URL
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {
        LatLng startLng;

        DownloadTask(LatLng LatLngStart) {
            this.startLng = LatLngStart;
        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask", "DownloadTask : " + data);
            } catch (Exception e) {
                Log.d("ERRORR-------  DownloadTask doInBackground", e.toString());
                e.printStackTrace();
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            try {

                super.onPostExecute(result);

                ParserTask parserTask = new ParserTask(this.startLng);

                // Invokes the thread for parsing the JSON data
                parserTask.execute(result).get();

            } catch (Exception e) {
                Log.d("ERROR DownloadTask  onPostExecute", e.toString());
                e.printStackTrace();
            }
        }
    }

    /**
     * A class to parse the Google Directions in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        LatLng startLng;
        ParserTask(LatLng LatLngStart)
        {
            this.startLng=LatLngStart;
        }
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionJSON parser = new DirectionJSON();
                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                Log.d("ERROR :-------------ParserTask Task", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            if(startLng.latitude !=mDevicelocation.latitude || startLng.longitude!=mDevicelocation.longitude   )
            {
                return;
            }
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                if(startLng.latitude ==mDevicelocation.latitude && startLng.longitude==mDevicelocation.longitude   )
                {
                    lineOptions.width(8);
                    lineOptions.color(Color.RED);
                  }
                else
                {
                    lineOptions.width(5);
                    lineOptions.color(Color.GRAY);
                }


            }
            for (int j = 0; j < mDummyDevicelocation.size(); j++) {
                MarkerOptions mDummyMarkerOptions = new MarkerOptions().position(mDummyDevicelocation.get(j)).title("Dummy Location");
                mMap.addMarker(mDummyMarkerOptions);
            }
            MarkerOptions mDummyMarkerOptions = new MarkerOptions().position(mDevicelocation).title("Actual Location");
            mMap.addMarker(mDummyMarkerOptions);

            if(mDestination!=null)
            {
                MarkerOptions mDummyMarkerOptionsDestination = new MarkerOptions().position(mDestination).title("Destination");
                mDummyMarkerOptionsDestination.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mMap.addMarker(mDummyMarkerOptionsDestination);
            }
            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                // if(mPolyline != null){
                //     mPolyline.remove();
                //  }
                mPolyline = mMap.addPolyline(lineOptions);
                if(mDestination!=null && mDevicelocation!=null)
                {
                    List<LatLng> lst=new ArrayList<LatLng>();
                    lst.add(mDevicelocation);
                    lst.add(mDestination);
                    ZoomToROute(lst);
                }

              //  Toast.makeText(MainActivity.this, toastString, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "No route is found", Toast.LENGTH_LONG).show();
            }
            mDestination=null;
        }
    }

    private class GeocodeConverter extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strAddress) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                //data = downloadUrl(url[0]);
                 String url = getDirectionsUrlFromAddress(strAddress[0]);
                data = downloadUrl(url);
                Log.d("GeocodeConverter", "GeocodeConverter : " + data);
            } catch (Exception e) {
                Log.d("ERROR :-------------GeocodeConverter Task", e.toString());
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("GeocodeConverter", "GeocodeConverter : " + result);
            List<LatLng> lstLatlng = new ArrayList<LatLng>();
            try {
                JSONObject objJSONObject = new JSONObject(result);
                JSONArray jResults = objJSONObject.getJSONArray("results");
                for (int i = 0; i < jResults.length(); i++) {
                    JSONObject objResult = (JSONObject) jResults.get(i);
                    JSONObject objGeometry = (JSONObject) objResult.get("geometry");
                    JSONObject objlocation = (JSONObject) objGeometry.get("location");
                    Double strLat = (Double) objlocation.get("lat");
                    Double strLong = (Double) objlocation.get("lng");
                    Log.d("strLat", "strLat : " + strLat);
                    Log.d("strLong", "strLong : " + strLong);
                    lstLatlng.add(new LatLng(strLat, strLong));
                }
                for (int i = 0; i < lstLatlng.size(); i++) {
                    String urlNew = getDirectionsUrl(mDevicelocation, (LatLng) lstLatlng.get(i));
                    DownloadTask downloadTaskNew = new DownloadTask(mDevicelocation);
                    //Start downloading json data from Google Directions API
                    downloadTaskNew.execute(urlNew).get();
                }
                for (int i = 0; i <  mDummyDevicelocation.size(); i++) {
                    //LatLng mOriginNew=    new LatLng(Double.parseDouble("30.5082551"), Double.parseDouble("-97.678896"));
                    //LatLng mDestinationNew=  new LatLng(Double.parseDouble("30.513532"),Double.parseDouble("-97.7397313"));
                   // List<LatLng> lstDummyLatlng = new ArrayList<LatLng>();
                   // lstDummyLatlng.add(mDummyDevicelocation.get(i));
                   if(mDestination==null) {
                       mDestination = (LatLng) lstLatlng.get(0);
                   }
                    String urlNew = getDirectionsUrl((LatLng)mDummyDevicelocation.get(i),mDestination);// mDestination);
                    DownloadTask downloadTaskNew = new DownloadTask(mDestination);
                    // Start downloading json data from Google Directions API
                    downloadTaskNew.execute(urlNew).get();
                }

                //    var markers = [];//some array
                //    var bounds = new google.maps.LatLngBounds();
                //    for (var i = 0; i < markers.length; i++) {
                //        bounds.extend(markers[i]);
                //    }
                //    mMap.fitBounds([]);
                // String id=objJSONObject.getString("u_name");
            } catch (JSONException e) {
                Log.d("ERROR :-------------GeocodeConverter onPostExecute", e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                Log.d("ERROR :-------------GeocodeConverter onPostExecute", e.toString());
                e.printStackTrace();

            }


        }

    }
    private class GetNereastRoadConverter extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strAddress) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                //data = downloadUrl(url[0]); 
                String url =strAddress[0];// getDirectionsUrlFromAddress(strAddress[0]);
                data = downloadUrl(url);
                Log.d("GetNereastRoadConverter", "url : " + url);
                Log.d("GetNereastRoadConverter", "GetNereastRoadConverter : " + data);
            } catch (Exception e) {
                Log.d("ERROR :-------------GetNereastRoadConverter ", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("GetNereastRoadConverter", "GetNereastRoadConverter onPostExecute: " + result);
            List<LatLng> lstLatlng = new ArrayList<LatLng>();
            try {
                if(result.trim() != "" && !result.equals("{}" )) {
                    JSONObject objJSONObject = new JSONObject(result);
                    JSONArray jResults = objJSONObject.getJSONArray("snappedPoints");
                    for (int i = 0; i < jResults.length(); i++) {
                        JSONObject objResult = (JSONObject) jResults.get(i);
                        //JSONObject objGeometry = (JSONObject) objResult.get("geometry");
                        JSONObject objlocation = (JSONObject) objResult.get("location");
                        Double strLat = (Double) objlocation.get("latitude");
                        Double strLong = (Double) objlocation.get("longitude");
                        Log.d("strLat", "strLat : " + strLat);
                        Log.d("strLong", "strLong : " + strLong);
                        LatLng lngToAdd = new LatLng(strLat, strLong);
                        if (!CheckLatLngInList(lngToAdd) && mDummyDevicelocation.size() < 3) {
                            mDummyDevicelocation.add(lngToAdd);
                            // lstLatlng.add(new LatLng(strLat, strLong));
                        }
                    }
                }
              // mDummyDevicelocation.addAll(lstLatlng);
                if(mDummyDevicelocation.size()<3)
                {
                    GetDummyLocations();
                }
            } catch (JSONException e) {
                Log.d("ERROR :-------------GetNereastRoadConverter onPostExecute", e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                Log.d("ERROR :-------------GetNereastRoadConverter onPostExecute", e.toString());
                e.printStackTrace();

            }


        }

    }
    public boolean CheckLatLngInList(  LatLng objLatLng)
    {

        for(int i=0; i<mDummyDevicelocation.size();i++)
        {
            if(objLatLng.latitude==mDummyDevicelocation.get(i).latitude && objLatLng.longitude==mDummyDevicelocation.get(i).longitude)
            {
                return true;
            }
        }
        return false;
    }
}