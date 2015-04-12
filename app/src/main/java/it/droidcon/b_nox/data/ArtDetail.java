package it.droidcon.b_nox.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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

    public String[][] images;
    public String[][] videos;
    public String[][] audios;

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

							JSONArray array = response.getJSONArray("images");
							images = new String[array.length()][2];
							for(int i=0; i < array.length(); i++) {
								images[i][0] = array.getJSONObject(i).getString("path");
								images[i][1] = array.getJSONObject(i).getString("description");
							}

							array = response.getJSONArray("audios");
							audios = new String[array.length()][2];
							for(int i=0; i < array.length(); i++) {
								audios[i][0] = array.getJSONObject(i).getString("path");
								audios[i][1] = array.getJSONObject(i).getString("description");
							}

							array = response.getJSONArray("videos");
							videos = new String[array.length()][2];
							for(int i=0; i < array.length(); i++) {
								videos[i][0] = array.getJSONObject(i).getString("path");
								videos[i][1] = array.getJSONObject(i).getString("description");
							}

							activity.onDetailLoaded();


						} catch (JSONException e) {
							e.printStackTrace();
						}

					

					}

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
						Log.i("JSON", "Request FAILED: " + error.toString());

                    }
                });

		AppController.getInstance().addToRequestQueue(jsObjRequest);

	}
}
