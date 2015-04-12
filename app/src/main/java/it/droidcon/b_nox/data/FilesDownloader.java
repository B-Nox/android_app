package it.droidcon.b_nox.data;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import it.droidcon.b_nox.activities.MainActivity;

/**
 * Created by valerio on 11/04/15.
 */

public class FilesDownloader extends AsyncTask<String, Integer, String> {

    private Context context;
    private MainActivity activity;
    private String type;
    private String localFile;

    // fileType: "image", "audio", "video"
    public FilesDownloader(Context context, MainActivity callingActivity, String fileName, String fileType) {
        this.context = context;
        this.activity = callingActivity;
        this.type = fileType;
        this.localFile = fileName;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        //String absolutePath = context.getFilesDir().getPath() + "/" + this.localFile;
        String absolutePath = "/sdcard/b_nox/" + this.localFile;
		try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

			File dir = new File("/sdcard/b_nox/images");
			dir.mkdirs();
			dir = new File("/sdcard/b_nox/audios");
			dir.mkdirs();
			dir = new File("/sdcard/b_nox/videos");
			dir.mkdirs();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(absolutePath);

            byte data[] = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1)
                output.write(data, 0, count);

        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }

        return absolutePath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected void onPostExecute(String result) {

        switch (this.type) {
            case "audio":
                activity.onAudioDownload(result);
				break;
			default: return;
		}

    }

}