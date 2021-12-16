package tobe.vlc.lifiart;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.luciom.vlc.VlcDecoder;
import com.luciom.vlc.VlcDecoderFactory;
import com.philips.indoorpositioning.library.IndoorPositioning;
import com.philips.indoorpositioning.library.IndoorPositioning.IndoorPositioningHeadingOrientation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

import tobe.vlc.lifiart.Constants.Localization_System;
import tobe.vlc.lifiart.R;


public class MainActivity extends AppCompatActivity {


    private VlcDecoder mVlcDecoder = null;
    private IndoorPositioning indoorPositioning = null;
    private Handler handler = new Handler(Looper.myLooper());
    private Double current_latitude = 0.0;
    private Double current_longitude = 0.0;
    private boolean is_first_update = true;
    private String closer_location_id = "";
    private JSONObject closer_location;
    private JSONObject current_zone = null;
    private Localization_System localization_system = Localization_System.S;
    private WebView myWebView;
    private String HOMEPAGE_URL = "file:///android_asset/pages/home-enjoy.html";

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("LiFi Art");
        setContentView(R.layout.activity_main);

        myWebView = new WebView(this.getApplicationContext());
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;


            }
       });
        setContentView(myWebView);
        myWebView.loadUrl(HOMEPAGE_URL);
        myWebView.addJavascriptInterface(new WebAppInterface(this,this), "Android");
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

       // getSupportActionBar().hide();




        //Update Locations

        //Controllo shared preferences: Se Vuote carico local-locations.json
        LocationsManager.getInstance().initLocations(this);
        LocationsManager.getInstance().updateLocationsFromDatabase();
        LocationsManager.getInstance().updateZonesFromDatabase();


        //Aggiorno shared preferences


        //Check Localization permissions
        checkLocationPermission();

        //Get Position with Sig SDK
        initSigSDK();
        startSigSDK();


        //Get Closer location
        //Start correct sdk
        //Viene fatto nell 'handler di sign.



    }

    @Override
    public void onBackPressed() {
        myWebView.loadUrl(HOMEPAGE_URL);
    }

    public void setContentByTextId(String locationId, String zoneId){

        boolean zone_found = false;
        for(int i = 0; i < LocationsManager.getInstance().getZones().length(); i++){
            JSONObject zone = null;
            try {
                zone = LocationsManager.getInstance().getZones().getJSONObject(i);
                if(zone.getString("offline-location-code").equalsIgnoreCase(locationId) && zone.getString("offline-zone-code").equalsIgnoreCase(zoneId)){
                    String url  = zone.getString("zone-content");
                    zone_found = true;
                    Toast.makeText(getBaseContext(), "Caricamento",
                            Toast.LENGTH_LONG).show();
                    myWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            myWebView.loadUrl(url);
                        }
                    });
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(!zone_found)
            Toast.makeText(getBaseContext(), "Codici non riconosciuti",
                    Toast.LENGTH_LONG).show();
    }

    private JSONObject getCloserLocation() {

        Double min_distance = 10000.0; //10KM
        JSONObject cli = null;

        if (LocationsManager.getInstance().getLocations().length() < 1) {
            System.out.println("Ancora nessun update :(");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Iterator<String> it = LocationsManager.getInstance().getLocations().keys(); it.hasNext(); ) {
            String key = it.next();
            try {

                JSONObject current_location = LocationsManager.getInstance().getLocations().getJSONObject(key);
                Double current_distance = calcolaDistanzaInMetri(
                        current_latitude,
                        current_longitude,
                        Double.parseDouble(current_location.getString("location-lat-s").replace(",", ".")),
                        Double.parseDouble(current_location.getString("location-long-s").replace(",", ".")));
                if (current_distance < min_distance) {
                    if (Double.parseDouble(current_location.getString("location-precision")) > current_distance) {
                        min_distance = current_distance;
                        cli = current_location;
                        cli.put("current-distance", current_distance);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return cli;

    }

    private JSONObject getCloserZone() {

        Double min_distance = 10000.0; //10KM
        JSONObject closer_zone = null;
        for (int i = 0; i < LocationsManager.getInstance().getZones().length(); i++) {

            try {
                JSONObject zone = LocationsManager.getInstance().getZones().getJSONObject(i);

                //System.out.println(zone);

                Double current_distance = calcolaDistanzaInMetri(
                        current_latitude,
                        current_longitude,
                        Double.parseDouble(zone.getString("zone-lat").replace(",", ".")),
                        Double.parseDouble(zone.getString("zone-long").replace(",", ".")));
                        System.out.println(zone.getString("id") +" a "+current_distance);

                if (current_distance < min_distance) {
                    if (Double.parseDouble(zone.getString("zone-precision")) > current_distance) {
                        min_distance = current_distance;
                        closer_zone = zone;
                        closer_zone.put("current-distance", current_distance);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        try {
            System.out.println("Sei stato localizzato vicino a " +
                    closer_zone.getString("zone-id") +
                    " a " + min_distance.intValue() + " metri.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return closer_zone;

    }

    private Double calcolaDistanzaInMetri(Double lat1, Double long1, Double lat2, Double long2) {
        Double distXinMT = (lat1 - lat2) * 111000;
        Double distYinMT = (long1 - long2) * 111000;
        return Math.sqrt(Math.pow(distXinMT, 2) + Math.pow(distYinMT, 2));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (localization_system == Localization_System.S) {
            initSigSDK();
            startSigSDK();
        } else if (localization_system == Localization_System.Z1) {
            startZero1Vlc();
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        if (localization_system == Localization_System.S) {
            indoorPositioning.stop();
        } else if (localization_system == Localization_System.Z1) {
            stopZero1Vlc();
        }

    }

    //Initialize the Signifiy SDK
    private void initSigSDK() {
        indoorPositioning = new IndoorPositioning(this.getApplicationContext());
        indoorPositioning.setConfiguration(Constants.tobetest);
        indoorPositioning.setHeadingOrientation(IndoorPositioningHeadingOrientation.PORTRAIT);
    }

    private void startSigSDK() {
        indoorPositioning.register(indoorPositioningListener, handler);
        indoorPositioning.start();
    }


    private IndoorPositioning.Listener indoorPositioningListener = new IndoorPositioning.Listener() {
        @Override
        public void didUpdateHeading(Map<String, Object> heading) {
            // headingDegrees = heading[IndoorPositioning.Listener.HEADING_DEGREES] as? Float,
            // headingAccuracyValue = heading[IndoorPositioning.Listener.HEADING_ACCURACY] as? Float,
            // headingArbitraryNorthDegrees = heading[IndoorPositioning.Listener.HEADING_ARBITRARY_NORTH_DEGREES] as? Float

        }

        @Override
        public void didUpdateLocation(Map<String, Object> location) {

            //System.out.println("*******"+location.get(IndoorPositioning.Listener.LOCATION_LATITUDE));
            //System.out.println("*******"+location.get(IndoorPositioning.Listener.LOCATION_LONGITUDE));

            Double local_lat  = Double.parseDouble(location.get(IndoorPositioning.Listener.LOCATION_LATITUDE).toString());
            Double local_long = Double.parseDouble(location.get(IndoorPositioning.Listener.LOCATION_LONGITUDE).toString());

            if(local_lat.equals(current_latitude) && local_long.equals(current_longitude)){
                return;
            }

            current_latitude = local_lat;
            current_longitude = local_long;
            int current_precision = Integer.parseInt(location.get(IndoorPositioning.Listener.LOCATION_EXPECTED_ACCURACY_LEVEL).toString());

            //Nel primo update possono essere presenti dati sporchi.
            if (is_first_update) {
                is_first_update = false;
                return;
            }

                JSONObject new_closer_location = getCloserLocation();
                try {
                if (new_closer_location!= null && !closer_location_id.equals(new_closer_location.getString("location-id"))) {
                    closer_location_id = new_closer_location.getString("location-id");


                        closer_location = LocationsManager.getInstance().getLocations().getJSONObject(closer_location_id);
                        setTitle("LiFi Zone - "+closer_location.getString("location-name"));
                        if (closer_location.getString("location-protocol").equals("z1")) {
                            indoorPositioning.stop();
                            localization_system = Localization_System.Z1;
                            Toast.makeText(getApplicationContext(), "Attivo Z1",
                                    Toast.LENGTH_LONG).show();
                            startZero1Vlc();
                        }





//Add 16 nov 2021
                        else{
                            indoorPositioning.stop();
                            indoorPositioning.setConfiguration(closer_location.getString("sig-auth-code"));
                            indoorPositioning.setHeadingOrientation(IndoorPositioningHeadingOrientation.PORTRAIT);
                            indoorPositioning.register(indoorPositioningListener, handler);
                            indoorPositioning.start();
                        }




                        Double distance = Double.parseDouble(closer_location.getString("current-distance"));
                        showAlert("Sei stato localizzato vicino a " +
                                closer_location.getString("location-name") +
                                " a " +
                                distance.intValue()
                                + " metri.");



                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            if (current_precision > 1) {

                try {
                    JSONObject closer_zone = getCloserZone();

                    if(closer_zone == null){
                        return;
                    }

                    if( current_zone == null || (
                            !closer_zone.getString("id").equals(current_zone.getString("id")))){
                        current_zone = closer_zone;
                        System.out.println(closer_zone.getString("zone-content"));
                        String url = closer_zone.getString("zone-content");
                        myWebView.post(new Runnable() {
                            @Override
                            public void run() {

                                    myWebView.loadUrl(url);

                            }
                        });
                        Toast.makeText(getApplicationContext(), "Caricamento",
                                Toast.LENGTH_LONG).show();
                        //myWebView.loadUrl(closer_zone.getString("zone-content"));
                        System.out.println("NEWZOONE");
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
  System.out.println(current_zone);
            //handleNewZone();

        }

        @Override
        public void didFailWithError(Error error) {
            System.out.println("********" + error.toString());
            System.out.println("********" + error.name());
            //In caso di mancata autorizzazione alla posizione
            if (error.name().equals("LOCATION_ACCESS_ERROR")) {
                checkLocationPermission();
            }

        }

    };

    private void handleNewZone() {



    }



    public void showAlert(String text){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Location")
                .setMessage(text)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Prompt the user once explanation has been shown

                    }
                })
                .create()
                .show();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            System.out.println("*****Permesso Negato");

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                System.out.println("*****Permesso Negato1");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("No LiFi")
                        .setMessage("Without Localization, LiFi can not work.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        99);
                            }
                        })
                        .create()
                        .show();


            } else {
                System.out.println("*****Permesso Negato2");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        99);

                new AlertDialog.Builder(this)
                        .setTitle("No LiFi")
                        .setMessage("Without Localization, LiFi can not work. Please reinstall the app in order to enable localization.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        99);
                            }
                        })
                        .create()
                        .show();
            }
            return false;
        } else {
            return true;
        }
    }


    public void startZero1Vlc() {
        mVlcDecoder = VlcDecoderFactory.getDecoder(VlcDecoder.VlcDecoderSource.CAMERA_ANY, getApplicationContext(), zero1vlcCallback);
        if (mVlcDecoder != null) {
            mVlcDecoder.start();
        }
    }

    public void stopZero1Vlc() {
        if (mVlcDecoder != null) {
            mVlcDecoder.stop();
        }
    }

    private final Handler.Callback zero1vlcCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(final Message msg) {
            if (msg.what == VlcDecoder.MsgWhat.UID.value && msg.obj instanceof byte[]) {
                final byte[] filtered_uid = (byte[]) msg.obj;
                final StringBuilder sb_newUid = new StringBuilder("");
                for (int i = 0; i < filtered_uid.length; i++) {
                    sb_newUid.append(String.format("%02X", filtered_uid[i]));
                }
                String id = sb_newUid.toString();
                String content = getContentFromZ1ID(id);
                if (content != null) {
                    myWebView.loadUrl(content);
                    System.out.println("NEWZOONE");
                    Toast.makeText(getApplicationContext(), "Caricamento",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), id,
                            Toast.LENGTH_LONG).show();
                }
            }
            return true;
        }

    };
    private String getContentFromZ1ID(String id) {

        String to_return = null;

        for (int i = 0; i < LocationsManager.getInstance().getZones().length(); i++){
            try{
                JSONObject zone = LocationsManager.getInstance().getZones().getJSONObject(i);
                if(zone.getString("zone-z1-id").equalsIgnoreCase(id)){
                    to_return = zone.getString("zone-content");
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return to_return;

    }

}

