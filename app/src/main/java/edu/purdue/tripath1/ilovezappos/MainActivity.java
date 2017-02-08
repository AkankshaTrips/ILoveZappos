package edu.purdue.tripath1.ilovezappos;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import android.view.Menu;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends ActionBarActivity {
    EditText productSearch;
    Button buttonSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productSearch = (EditText) findViewById(R.id.editSearch);
        buttonSearch = (Button) findViewById(R.id.searchButton);

        buttonSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AsyncTaskRunner runner = new AsyncTaskRunner();
                String value = productSearch.getText().toString();
                runner.execute(value);
            }
        });
    }

    public void convertToJson(String jsonString) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(jsonString);
            org.json.JSONObject jsonObject = new org.json.JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            org.json.JSONObject iterator = jsonArray.getJSONObject(0);

            brandName(iterator);
            thumbnailImageUrl(iterator);
            originalPrice(iterator);
            price(iterator);
            percentOff(iterator);
            productName(iterator);

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void brandName(org.json.JSONObject iterator) {
        try {
            String brandName = (String)iterator.get("brandName");
            TextView brandView = (TextView) findViewById(R.id.brandName);
            brandView.setText(brandName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void thumbnailImageUrl(org.json.JSONObject iterator) {
        try {
            String thumbnailImageUrl = (String) iterator.get("thumbnailImageUrl");
            ImageView imageView = (ImageView) findViewById(R.id.thumbnailImageUrl);
            Picasso.with(this).load(thumbnailImageUrl).into(imageView);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void originalPrice(org.json.JSONObject iterator) {
        try {
            String originalPrice = (String)iterator.get("originalPrice");
            TextView origPriceView = (TextView) findViewById(R.id.originalPrice);
            origPriceView.setText("ORIGINAL PRICE\n" + originalPrice);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void price(org.json.JSONObject iterator) {
        try {
            String price = (String)iterator.get("price");
            TextView priceView = (TextView) findViewById(R.id.price);
            priceView.setText("AFTER SALE\n" + price);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void productName(org.json.JSONObject iterator) {
        try {
            String productName = (String)iterator.get("productName");
            TextView productView = (TextView) findViewById(R.id.productName);
            productView.setText(productName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void percentOff(org.json.JSONObject iterator) {
        try {
            String percentOff = (String)iterator.get("percentOff");
            TextView offView = (TextView) findViewById(R.id.percentOff);
            offView.setText(percentOff + " OFF");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void animations (View view) {
        ImageView imageView = (ImageView) findViewById(R.id.thumbnailImageUrl);
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anime);
        imageView.startAnimation(animation1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        String search = productSearch.getText().toString();
        StringBuilder stringBuilder = new StringBuilder();

        @Override
        protected String doInBackground(String... params) {
            try {
                //calling the API
                URL url = new URL("https://api.zappos.com/Search?term=" + search + "&key=b743e26728e16b81da139182bb2094357c31d331");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");

                InputStreamReader isr = new InputStreamReader(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(isr);

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                urlConnection.disconnect();
                return stringBuilder.toString();

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {
            convertToJson(jsonString);
        }
    }
}