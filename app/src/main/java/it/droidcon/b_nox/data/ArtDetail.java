package it.droidcon.b_nox.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;


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

    public ArtDetail(String identifier) {
		this.id = identifier;
        String defaultServerAddress = "192.168.23.3";
        String sanitizedMAC = identifier.toLowerCase().replace(":", "-");
        String url = defaultServerAddress + "/api/artworks/" + sanitizedMAC;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
						try {
							title = response.getString("title");
							author = response.getString("author");
							date = response.getString("date");
							place = response.getString("place");
							description = response.getString("description");

							JSONArray array = response.getJSONArray("images");
							for(int i=0; i < array.length(); i++) {
								images[i][0] = array.getJSONObject(i).getString("path");
								images[i][1] = array.getJSONObject(i).getString("description");
							}

							array = response.getJSONArray("audios");
							for(int i=0; i < array.length(); i++) {
								audios[i][0] = array.getJSONObject(i).getString("path");
								audios[i][1] = array.getJSONObject(i).getString("description");
							}

							array = response.getJSONArray("videos");
							for(int i=0; i < array.length(); i++) {
								videos[i][0] = array.getJSONObject(i).getString("path");
								videos[i][1] = array.getJSONObject(i).getString("description");
							}


						} catch (JSONException e) {
							e.printStackTrace();
						}

					

					}

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                });
    }
}
