package com.example.dbkats.calc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.content.Intent;
import android.widget.EditText;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class MyActivity extends Activity {

    public static String EXTRA_MSG = "hello";
    private Intent intent;
    private TextView textView;

    public void addNumbers (View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);

        EditText editText1 = (EditText) findViewById(R.id.num1);
        String message1 = editText1.getText().toString();

        EditText editText2 = (EditText) findViewById(R.id.num2);
        String message2 = editText2.getText().toString();

        int a = Integer.parseInt(message1);
        int b = Integer.parseInt(message2);
        int c = a + b;

        intent.putExtra(EXTRA_MSG, "" + c);
        startActivity(intent);
    }

    public void sendMessage (View view) {
        this.intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.msg);
        String message = editText.getText().toString();
        Log.d("upload_msg", "trying to send message " + message);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new UploadWebpageTask().execute(message);
        } else {
            intent.putExtra(EXTRA_MSG, "trouble connecting to server");
            startActivity(intent);
        }
    }

    public void getMessages (View view) {
//        this.intent = new Intent(this, Activity.class);
        this.textView = (TextView) findViewById(R.id.message_container);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute("");

        } else {
            intent.putExtra(EXTRA_MSG, "trouble connecting to server");
            startActivity(intent);
        }
    }

    private class UploadWebpageTask extends AsyncTask<String, Void, String> {
        protected String doInBackground (String... msg) {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://boompig.herokuapp.com/droid");

                String json = "";
                JSONObject obj = new JSONObject();
                obj.accumulate("nickname", "Anonymous Android Phone");
                obj.accumulate("msg", msg[0]);
                json = obj.toString();

                StringEntity se = new StringEntity(json);
                post.setEntity(se);

                post.setHeader("Accept", "application/json");
                post.setHeader("Content-Type", "application/json");

                HttpResponse response = client.execute(post);
                InputStream is = response.getEntity().getContent();

                BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = buf.readLine()) != null) {
                    sb.append(line);
                }

                String content = sb.toString();
                is.close();

                return content;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "I failed :(";
        }

        protected void onPostExecute(String content) {
            intent.putExtra(EXTRA_MSG, content);
            startActivity(intent);
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        protected String doInBackground (String... link) {
            try {
                URL url = new URL("http://boompig.herokuapp.com/droid");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 10s
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("URL request", "response is " + response);
                InputStream is = conn.getInputStream();

                BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = buf.readLine()) != null) {
                    sb.append(line);
                }

                String content = sb.toString();
                is.close();

                return content;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "I failed :(";
        }

        protected void onPostExecute (String content) {
            // parse the content
            String output = "";

            try {
                JSONArray arr = new JSONArray(content);
                for (int i = arr.length() - 1; i >= 0; i--) {
                    JSONObject obj = arr.getJSONObject(i);
                    output += obj.get("nickname") + ": " + obj.get("msg") + "\n";
                }

                textView.setText(output);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
