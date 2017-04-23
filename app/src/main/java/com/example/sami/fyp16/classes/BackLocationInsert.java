package com.example.sami.fyp16.classes;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by sami on 14/04/17.
 */

public class BackLocationInsert extends AsyncTask<String,Void,String> {

    Context context;
    AlertDialog alertDialog;
    public BackLocationInsert(Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d("doIn","yes");
        String login_url = "http://192.168.1.104/fyp/coordinatesup.php";
        try{

            Log.d("Try","start");
            String latitude = params[0];
            String longitude = params[1];
            String user_name_sec = params[2];
            String time_stamp = params[3];

            Log.d("BacK_latitude",latitude);
            Log.d("Back_longtitude",longitude);

            URL url = new URL(login_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            Log.d("send_latitude",latitude);
            Log.d("send_longitude",latitude);
            Log.d("send_user_name_sec",user_name_sec);
            String post_data = URLEncoder.encode("latitude", "UTF-8")+"="+URLEncoder.encode(latitude,"UTF-8")
                    +"&"+URLEncoder.encode("longitude","UTF-8")+"="+URLEncoder.encode(longitude,"UTF-8")
                    +"&"+URLEncoder.encode("user_name_sec","UTF-8")+"="+URLEncoder.encode(user_name_sec,"UTF-8")
                    +"&"+URLEncoder.encode("time_stamp","UTF-8")+"="+URLEncoder.encode(time_stamp,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String result="";
            String line="";
            while((line = bufferedReader.readLine())!= null) {
                result += line;
                Log.d("In results",result);
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Location Status");
    }
    @Override
    protected void onPostExecute(String result) {
        alertDialog.setMessage(result);
        alertDialog.show();
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
