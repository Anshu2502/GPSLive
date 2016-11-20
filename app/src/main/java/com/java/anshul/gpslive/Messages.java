package com.java.anshul.gpslive;

/**
 * Created by Anshul on 6/20/2016.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Messages extends ActionBarActivity {

    String myJSON, session_id, username, id, ids, ss, tt, check;
    Button send;
    String result=null;
    String line=null;
    int code;
    InputStream is=null;
    EditText sendtext;
    ListAdapter adapter;
    TextView di;

    private static final String TAG_RESULTS="result";
    private static final String TAG_ID = "mess";
    private static final String TAG_MESSAGE = "message";

    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList;

    ListView list;
    ProgressBar Pbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_list);
        getSupportActionBar().hide();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        SharedPreferences prefs = getSharedPreferences("ids", MODE_PRIVATE);
        session_id = prefs.getString("id", null);

        di = (TextView) findViewById(R.id.id);


        username = prefs.getString("name", null);
        Pbar = (ProgressBar)findViewById(R.id.progressBar1);
        Pbar.setVisibility(View.VISIBLE);
        send = (Button) findViewById(R.id.btnSend);
        sendtext = (EditText) findViewById(R.id.inputMsg);
        list = (ListView) findViewById(R.id.listView);
        personList = new ArrayList<HashMap<String,String>>();
        getData();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insert();
                reload();
            }
        });
    }
    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<peoples.length();i++){
                JSONObject c = peoples.getJSONObject(i);
                HashMap<String,String> persons = new HashMap<String,String>();
                id = c.getString("sender_id");
                ids = c.getString("rec_id");
                if(id.equals(session_id)&&ids.equals("admin")) {

                    ss = c.getString("messages");
                    persons.put(TAG_MESSAGE, ss);
                    //persons.put("usern", "");
                    personList.add(persons);
                  }
                    if(id.equals("admin")&&ids.equals(session_id)) {

                    tt = c.getString("messages");
                        persons.put(TAG_ID, tt);
                       // persons.put("admin","");
                        personList.add(persons);
                      }



                adapter = new SimpleAdapter(Messages.this, personList,
                        R.layout.activity_message,
                        new String[]{TAG_ID,  TAG_MESSAGE },
                        new int[]{ R.id.id, R.id.messag});

                list.setAdapter(adapter);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getData(){
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                InputStream inputStream = null;
                String result = null;
                try {

                    String postReceiverUrl = "http://gps.trackeron.in/gpsapp/emp_message.php"; // Get All Messages
                    //"http://progresscard.progresscard.in/progress_card/messages/get_messages.php";
                    // HttpClient
                    HttpClient httpClient = new DefaultHttpClient();

                    // post header
                    HttpPost httpPost = new HttpPost(postReceiverUrl);

                    // add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("user_id", session_id));
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpClient.execute(httpPost);
                    HttpEntity resEntity = response.getEntity();

                    inputStream = resEntity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    Log.i("tagconvertstr", "[" + result + "]");
                    System.out.println(e);
                }
                finally {
                    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                myJSON = result;
                showList();
                Pbar.setVisibility(View.GONE);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }


    public void insert()
    {
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());*/
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("user_id",session_id));
        nameValuePairs.add(new BasicNameValuePair("admin_id","admin"));
        nameValuePairs.add(new BasicNameValuePair("send_text",sendtext.getText().toString().trim()));
        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://gps.trackeron.in/gpsapp/message_reply.php"); // Send Messages
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.e("pass 1", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 1", e.toString());
            Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    Toast.LENGTH_LONG).show();
        }

        try
        {
            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            Log.e("pass 2", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 2", e.toString());
        }

        try
        {
            JSONObject json_data = new JSONObject(result);
            code=(json_data.getInt("code"));

            if(code==1)
            {
                // Toast.makeText(getBaseContext(), "Inserted Successfully",
                //       Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getBaseContext(), "Sorry, Try Again",
                        Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e)
        {
            Log.e("Fail 3", e.toString());
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(Messages.this, MainActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}
