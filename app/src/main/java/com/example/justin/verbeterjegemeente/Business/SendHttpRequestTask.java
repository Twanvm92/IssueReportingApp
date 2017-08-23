package com.example.justin.verbeterjegemeente.Business;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by twanv on 23-8-2017.
 */

public class SendHttpRequestTask extends AsyncTask<String, Void, Bitmap> {
    private ImageButton srImage;

    public SendHttpRequestTask(ImageButton srImage) {
        this.srImage = srImage;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            URL url = new URL("http://xxx.xxx.xxx/image.jpg");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        }catch (Exception e){
            Log.d("SendHTTPRequestTask: ",e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        srImage.setImageBitmap(result);
    }
}