package com.example.dell.webservereg1usingjson;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTwo extends Fragment {

    Button button;
   // ListView listView;
   // MyAdapter myAdapter;
MyRecyclerViewAdapter myRecyclerViewAdapter;
    RecyclerView recyclerView;
    MyTask myTask;
    ArrayList<Contact> arrayList;
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
             //  notify  to your recycler view Adapter
                myRecyclerViewAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("B_34","JSON PARSING Error");
            }
            super.onPostExecute(s);
        }
    }

    //step7b: create an inner class for Custom Adapter
   public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>
    {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.row,parent,false);
            ViewHolder viewHolder = new ViewHolder(v);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //get data from arraylist based on position
            Contact c = arrayList.get(position);
            //apply data on viewholder-using setters
            holder.tv1.setText(""+c.getSno());
            holder.tv2.setText(c.getName());
            holder.tv3.setText(c.getEmail());
            holder.tv4.setText(c.getMobile());


        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder // equal to row.xml
        {
            public TextView tv1,tv2,tv3,tv4;
            public ViewHolder(View itemView) {
                super(itemView);
                tv1 = (TextView) itemView.findViewById(R.id.textView10);
                tv2 = (TextView) itemView.findViewById(R.id.textView20);
                tv3 = (TextView) itemView.findViewById(R.id.textView30);
                tv4 = (TextView) itemView.findViewById(R.id.textView40);
            }
        }
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
        View v= inflater.inflate(R.layout.fragment_fragment_two, container, false);
        button = (Button) v.findViewById(R.id.button);
        //listView = (ListView) v.findViewById(R.id.listview);
        //myAdapter =  new MyAdapter();
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        myRecyclerViewAdapter = new MyRecyclerViewAdapter();
        recyclerView.setAdapter(myRecyclerViewAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        myTask = new MyTask();
        arrayList = new ArrayList<Contact>();
       // listView.setAdapter(myAdapter);

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
