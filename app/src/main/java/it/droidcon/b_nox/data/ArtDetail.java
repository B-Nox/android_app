package it.droidcon.b_nox.data;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import it.droidcon.b_nox.activities.MainActivity;


/**
 * Created by demiurgo on 4/11/15.
 */

public final class ArtDetail {

    public String id;
    public String title;
    public String author;

    public String date;
    public String place;
    public String description;

    public String image;
    public String audio;


    public ArtDetail() {

    }

    public ArtDetail(MainActivity activity, String identifier, String serverAddress) {

        this.id = identifier;
        //String defaultServerAddress = "192.168.23.3";
        String sanitizedMAC = identifier.toLowerCase().replace(":", "-");
        String url = serverAddress + "/api/artworks/" + sanitizedMAC;
        Log.i("JSON", "request on " + url + " identifier " + identifier);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i("JSON", "Response " + response.toString());

                        try {
                            title = response.getString("title");
                            author = response.getString("author");
                            date = response.getString("date");
                            place = response.getString("place");
                            description = response.getString("description");
                            image = response.getString("image");
                            audio = response.getString("audio");

                            if(title!=null && image != null && audio!=null && description != null) {
                                activity.onDetailLoaded();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                }, error -> {
                    Log.i("JSON", "Request FAILED: " + error.toString());
                });

        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }
}
