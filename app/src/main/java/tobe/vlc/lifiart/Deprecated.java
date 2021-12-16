package tobe.vlc.lifiart;


public class Deprecated {

    /*
    public static void updateLocationsFromWeb(RequestQueue queue) {

        String drive_doc_id = "1EGO5YSew8Lo6rDBzYX4sidC9Lbcpo9ruqncmh6vaRlU";

        String url1 = "https://spreadsheets.google.com/feeds/cells/" + drive_doc_id + "/"+1+"/public/values?alt=json";
        String url2 = "https://spreadsheets.google.com/feeds/list/" + drive_doc_id + "/"+1+"/public/values?alt=json";

        JSONArray newKeys = new JSONArray();
        JSONArray newData = new JSONArray();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url1, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response){
                        System.out.println("***********Response: " + response.toString());
                        try {
                            for(int i = 0; i < response.getJSONObject("feed").getJSONArray("entry").length(); i++){
                                JSONObject item = response.getJSONObject("feed").getJSONArray("entry").getJSONObject(i);
                                if(item.getJSONObject("gs$cell").getString("row").equals("1")){
                                    newKeys.put(item.getJSONObject("content").getString("$t"));
                                }
                            }

                            System.out.println("NK"+newKeys);

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                    (Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {

                                        @Override
                                        public void onResponse(JSONObject response){
                                            System.out.println("***********Response: " + response.toString());
                                            try {
                                                for(int i = 0; i < response.getJSONObject("feed").getJSONArray("entry").length(); i++) {
                                                    JSONObject entry = response.getJSONObject("feed").getJSONArray("entry").getJSONObject(i);
                                                    JSONObject row_data = new JSONObject();
                                                    for (int k = 0; k < newKeys.length(); k++) {
                                                        String key = newKeys.getString(k);
                                                        row_data.put(key, entry.getJSONObject("gsx$" + key).getString("$t"));

                                                    }
                                                    newData.put(row_data);
                                                }

                                                for (int i = 0; i < newData.length(); i++){
                                                    locations.put(newData.getJSONObject(i).getString("location-id"),newData.get(i));
                                                }

                                                LocationsManager.getInstance().saveLocations();



                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }


                                        }
                                    }, new Response.ErrorListener() {

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // TODO: Handle error
                                            System.out.println("ERROR");

                                        }
                                    });

                            queue.add(jsonObjectRequest);



                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        System.out.println("ERROR1");
                    }
                });

        queue.add(jsonObjectRequest);


    }
public static void updateZonesFromWeb(RequestQueue queue) {

        String drive_doc_id = "1EGO5YSew8Lo6rDBzYX4sidC9Lbcpo9ruqncmh6vaRlU";

        String url1 = "https://spreadsheets.google.com/feeds/cells/" + drive_doc_id + "/"+2+"/public/values?alt=json";
        String url2 = "https://spreadsheets.google.com/feeds/list/" + drive_doc_id + "/"+2+"/public/values?alt=json";

        JSONArray newKeys = new JSONArray();
        JSONArray newData = new JSONArray();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url1, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response){
                        System.out.println("***********Response: " + response.toString());
                        try {
                            for(int i = 0; i < response.getJSONObject("feed").getJSONArray("entry").length(); i++){
                                JSONObject item = response.getJSONObject("feed").getJSONArray("entry").getJSONObject(i);
                                if(item.getJSONObject("gs$cell").getString("row").equals("1")){
                                    newKeys.put(item.getJSONObject("content").getString("$t"));
                                }
                            }

                            System.out.println("NK"+newKeys);

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                    (Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {

                                        @Override
                                        public void onResponse(JSONObject response){
                                            System.out.println("***********Response: " + response.toString());
                                            try {
                                                for(int i = 0; i < response.getJSONObject("feed").getJSONArray("entry").length(); i++) {
                                                    JSONObject entry = response.getJSONObject("feed").getJSONArray("entry").getJSONObject(i);
                                                    JSONObject row_data = new JSONObject();
                                                    for (int k = 0; k < newKeys.length(); k++) {
                                                        String key = newKeys.getString(k);
                                                        row_data.put(key, entry.getJSONObject("gsx$" + key).getString("$t"));

                                                    }
                                                    newData.put(row_data);
                                                }

                                                for (int i = 0; i < newData.length(); i++){
                                                    zones.put(newData.get(i));
                                                }

                                                LocationsManager.getInstance().saveZones();



                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }


                                        }
                                    }, new Response.ErrorListener() {

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // TODO: Handle error
                                            System.out.println("ERROR");

                                        }
                                    });

                            queue.add(jsonObjectRequest);



                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        System.out.println("ERROR1");
                    }
                });

        queue.add(jsonObjectRequest);


    }
     */
}
