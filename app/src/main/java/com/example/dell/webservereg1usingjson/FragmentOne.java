package com.example.dell.webservereg1usingjson;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentOne extends Fragment {
    Button button;
    ListView listView;
    MyAdapter myAdapter;
    MyTask myTask;
    ArrayList<Contact>arrayList;
//step7 : create an inner class for ASYNCTASK
    public class MyTask extends AsyncTask<String,Void,String>
    {
        URL url;
        HttpURLConnection connection;
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        BufferedReader bufferdreader;
        String line;
        StringBuilder result;

        @Override
        protected String doInBackground(String... string) {
            //12.a write logic for connecting to server ant get JSON data
            try {
                url = new URL(string[0]);
                connection = (HttpURLConnection) url.openConnection();
                inputStream = connection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                bufferdreader = new BufferedReader(inputStreamReader);
                line = bufferdreader.readLine();
                result = new StringBuilder();
                while (line!=null)
                {
                    result.append(line);
                    line = bufferdreader.readLine();
                }
                return  result.toString();// return
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("B_34","URL is improper");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("B_34","Network Problem");
            }
            return "Something went wrong";
        }

        @Override
        protected void onPostExecute(String s) {
            //12.b reverse JSON parsing
            try {
                JSONObject j = new JSONObject(s);
                JSONArray k = j.getJSONArray("contacts");
                for (int i = 0;i<k.length();i++)
                {
                    JSONObject m = k.getJSONObject(i);
                    String name = m.getString("name");
                    String email = m.getString("email");
                    JSONObject phone = m.getJSONObject("phone");
                    String mobile = phone.getString("mobile");
                    // lets put above data to arraylist<Contacts>
                    Contact c = new Contact();
                    c.setName(name);
                    c.setEmail(email);
                    c.setMobile(mobile);
                    c.setSno(i+1);
                    // now push contact object to arraylist
                    arrayList.add(c);
                }
                myAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("B_34","JSON PARSING Error");
            }
            super.onPostExecute(s);
        }
    }

    //step7b: create an inner class for Custom Adapter
    public class MyAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           //read data from arralist based position
            Contact c = arrayList.get(position);

            View v = getActivity().getLayoutInflater().inflate(R.layout.row,null);
            TextView tv1 = (TextView) v.findViewById(R.id.textView10);
            TextView tv2 = (TextView) v.findViewById(R.id.textView20);
            TextView tv3 = (TextView) v.findViewById(R.id.textView30);
            TextView tv4 = (TextView) v.findViewById(R.id.textView40);
            // fill data into above views
            tv1.setText(""+c.getSno());
            tv2.setText(c.getName());
            tv3.setText(c.getEmail());
            tv4.setText(c.getMobile());
            //return row.xml
                    return v;
        }
    }

    public FragmentOne() {
        // Required empty public constructor
    }
    public boolean checkIntrenetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if((networkInfo==null)||(networkInfo.isConnected()==false))
        {
            return false;
        }
        return true;

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_fragment_one, container, false);
        button = (Button) v.findViewById(R.id.button);
        listView = (ListView) v.findViewById(R.id.listview);
        myAdapter =  new MyAdapter();
        myTask = new MyTask();
        arrayList = new ArrayList<Contact>();
        listView.setAdapter(myAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check internet, if available start asynctask
                if (checkIntrenetConnection())
                {
                    myTask.execute("http://api.androidhive.info/contacts/");
                }
                else Toast.makeText(getActivity(), "Internet is not available", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

}
