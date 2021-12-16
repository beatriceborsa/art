package tobe.vlc.lifiart;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class LocationsManager {

    private static LocationsManager locationsManager = null;
    private Context context = null;


    public static LocationsManager getInstance(){
        if (locationsManager == null){
            locationsManager = new LocationsManager();
        }
        return locationsManager;
    }

    private void saveLocations(){
        SharedPreferences mPrefs = context.getSharedPreferences("locations",MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("locations-json", locations.toString());
        prefsEditor.commit();
    }

    private void saveZones(){
        SharedPreferences mPrefs = context.getSharedPreferences("zones",MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("zones-json", locations.toString());
        prefsEditor.apply();
    }

    private static JSONObject locations = new JSONObject();
    private static JSONObject zones = new JSONObject();

    public JSONObject getLocations(){
        return locations;
    }
    public JSONArray getZones(){
        JSONArray to_ret = new JSONArray();
        for (Iterator<String> it = zones.keys(); it.hasNext(); ) {
            String key = it.next();
            try {
                to_ret.put(zones.getJSONObject(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return to_ret;
    }

    public void initLocations(Context context){

        if(locationsManager == null){
            locationsManager = new LocationsManager();
        }

        this.context = context;
        SharedPreferences mPrefs = context.getSharedPreferences("locations",MODE_PRIVATE);
        String location_string  = mPrefs.getString("locations-json","{}");
        try {
            JSONObject new_locations = new JSONObject(location_string);
            for (Iterator<String> it = new_locations.keys(); it.hasNext(); ) {
                String key = it.next();
                locations.put(key, new_locations.getJSONObject(key));
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        mPrefs = context.getSharedPreferences("zones",MODE_PRIVATE);
        String zones_string  = mPrefs.getString("zones-json","{}");
        try {
            JSONObject new_zones = new JSONObject(zones_string);
            for (Iterator<String> it = new_zones.keys(); it.hasNext(); ) {
                String key = it.next();
                zones.put(key, new_zones.getJSONObject(key));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void updateLocationsFromDatabase(){




        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://lifizone-2-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        DatabaseReference drb = mDatabase.child("1EGO5YSew8Lo6rDBzYX4sidC9Lbcpo9ruqncmh6vaRlU").child("Location");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                System.out.println(dataSnapshot);
                Map<String, String> value = (Map<String, String>) dataSnapshot.getValue();
                JSONObject jo = new JSONObject(value);
                //System.out.println("****"+jo);

                for (Iterator<String> it = jo.keys(); it.hasNext(); ) {
                    String k = it.next();
                    try {
                        locations.put(k, jo.getJSONObject(k));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                LocationsManager.getInstance().saveLocations();
                System.out.println("**** UPDATE");
                Toast.makeText(context, "Database Updated",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
               System.out.println(databaseError);
            }
        };
        drb.addValueEventListener(postListener);
    }

    public void updateZonesFromDatabase(){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://lifizone-2-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        DatabaseReference drb = mDatabase.child("1EGO5YSew8Lo6rDBzYX4sidC9Lbcpo9ruqncmh6vaRlU").child("Zones");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                System.out.println(dataSnapshot);
                Map<String, String> value = (Map<String, String>) dataSnapshot.getValue();
                JSONObject jo = new JSONObject(value);
                System.out.println("****"+jo);

                for (Iterator<String> it = jo.keys(); it.hasNext(); ) {
                    String k = it.next();
                    try {
                        zones.put(k, jo.getJSONObject(k));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                LocationsManager.getInstance().saveZones();
                System.out.println("**** UPDATE");
                Toast.makeText(context, "Database Updated",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                System.out.println(databaseError);
            }
        };
        drb.addValueEventListener(postListener);
    }


}
