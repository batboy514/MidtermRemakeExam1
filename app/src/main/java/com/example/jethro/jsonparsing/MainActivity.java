package com.example.jethro.jsonparsing;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity {

    private ProgressDialog pDialog;

    private static String url = "http://josenian.herokuapp.com/api/books";


    private static final String TAG_TITLE = "title";
    private static final String TAG_ID = "id";
    private static final String TAG_GENRE = "genre";
    private static final String TAG_AUTHOR = "author";
    private static final String TAG_READ = "read";


    JSONArray title = null;

    ArrayList<HashMap<String,String>>contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        contactList= new ArrayList<HashMap<String, String>>();

        ListView lv= getListView();

       lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


               String name=((TextView) view.findViewById(R.id.title)).getText().toString();


           }
       });

        new GetTitle().execute();

    }

    private class GetTitle extends AsyncTask<Void,Void,Void>
    {
        private final String GET =null ;

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler sh= new ServiceHandler();

            String jsonStr = sh.getResponse(url,GET);

            Log.d("Response:", ">" + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    title = jsonObj.getJSONArray(TAG_TITLE);

                    // looping through All Contacts
                    for (int i = 0; i < title.length(); i++) {
                        JSONObject c = title.getJSONObject(i);

                        String id = c.getString(TAG_ID);
                        String title = c.getString(TAG_READ);

                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        contact.put(TAG_ID, id);
                        contact.put(TAG_TITLE,title);


                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactList,
                    R.layout.list_item, new String[] { TAG_TITLE
                     }, new int[] { R.id.title});

            setListAdapter(adapter);
        }
    }
}
