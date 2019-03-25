package com.example.androidlabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {
    ProgressBar pBar;
    ImageView currentW;
    TextView currentT;
    TextView minT;
    TextView maxT;
    TextView uvRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        ForecastQuery fq = new ForecastQuery();
        fq.execute("http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric");
        currentW = findViewById(R.id.currentWeather);
        currentT = findViewById(R.id.currentTemp);
        minT = findViewById(R.id.minTemp);
        maxT = findViewById(R.id.maxTemp);
        uvRate = findViewById(R.id.uvRate);
        pBar = findViewById(R.id.pBar);
        pBar.setVisibility(View.VISIBLE);


    }

    private class ForecastQuery extends AsyncTask<String, Integer, String>{

        String windSpeed="";
        String minTemp="";
        String maxTemp="";
        String currentTemp="";
        Bitmap currentWeather;
        String iconName="";
        String uvR="";
        @Override
        protected String doInBackground(String... strings) {
            publishProgress(0);
            try {
                String urlString = "http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric";

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream inStream = conn.getInputStream();

                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                //conn.setDoInput(true);
                //create a pull parser:
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( inStream  , "UTF-8");  //inStream comes from line 46
                // Starts the query
                //conn.connect();



                while(xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    if(xpp.getEventType() == XmlPullParser.START_TAG){
                        String tagName = xpp.getName();

                        if(tagName.equals("temperature")) {
                            currentTemp = xpp.getAttributeValue(null,"value");
                            minTemp = xpp.getAttributeValue(null,"min");
                            maxTemp = xpp.getAttributeValue(null,"max");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            publishProgress(25);
                        }else if(tagName.equals("speed")){
                            windSpeed = xpp.getAttributeValue(null,"value");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            publishProgress(50);
                        }else if(tagName.equals("weather")){
                            iconName = xpp.getAttributeValue(null,"icon");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            publishProgress(75);
                        }

                    }
                    xpp.next();
                }

                currentWeather = null;
                URL iconUrl = new URL("http://openweathermap.org/img/w/" + iconName + ".png");
                HttpURLConnection iconConn = (HttpURLConnection) iconUrl.openConnection();
                iconConn.connect();
                int responseCode = iconConn.getResponseCode();
                if (responseCode == 200) {
                    currentWeather = BitmapFactory.decodeStream(iconConn.getInputStream());
                    Log.d("icon location:", "downloading it ");
                }

                //currentWeather  = HTTPUtils.getImage(iconUrl));
                FileOutputStream outputStream = openFileOutput( iconName + ".png", Context.MODE_PRIVATE);
                currentWeather.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                outputStream.flush();
                outputStream.close();

                FileInputStream fis = null;
                try {
                    fis = openFileInput(iconName + ".png");
                    Log.d("icon location:", "locally ");
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                currentWeather = BitmapFactory.decodeStream(fis);



                URL UVurl = new URL("http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389");
                HttpURLConnection UVConnection = (HttpURLConnection) UVurl.openConnection();
                inStream = UVConnection.getInputStream();

                //create a JSON object from the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                //now a JSON table:
                JSONObject jObject = new JSONObject(result);
                double aDouble = jObject.getDouble("value");
                uvR = jObject.getString("value");
                Log.i("UV is:", ""+ aDouble);


            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "Finished";

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pBar.setVisibility(View.VISIBLE);
            //Log.i("AsyncTaskExample", "update:" + values[0]);
            //messageBox.setText("At step:" + values[0]);
            pBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            //the parameter String s will be "Finished task" from line 27

            currentT.append(currentTemp);
            minT.append(minTemp);
            maxT.append(maxTemp);
            uvRate.append(uvR);
            currentW.setImageBitmap(currentWeather);

            pBar.setVisibility(View.INVISIBLE);
        }
    }
}
