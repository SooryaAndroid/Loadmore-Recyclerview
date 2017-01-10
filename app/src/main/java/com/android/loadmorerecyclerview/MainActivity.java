package com.android.loadmorerecyclerview;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    RecyclerView recyclerView;

    private List<List1>namelist = new ArrayList<>();
    private ListAdapter mAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    LinearLayoutManager linearLayoutManager;



    int i=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView)findViewById(R.id.recycler);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        //--------------scroll--------------------------
scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
    @Override
    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                load_more();
            }
        }, 500);


    }
};
        recyclerView.addOnScrollListener(scrollListener);





        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Loading");
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://api.androidhive.info/json/movies.json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET  , url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        Log.d("response",response.toString());

                        try{

                            JSONArray jsonArray = new JSONArray(response);
                            Toast.makeText(MainActivity.this, ""+jsonArray.length(), Toast.LENGTH_SHORT).show();
                            for(int i =0; i<jsonArray.length(); i++){

                                List1 detail = new List1();


                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String name = jsonObject.getString("title");
                                String year = jsonObject.getString("releaseYear");

                                detail.setName(name);
                                detail.setYear(year);

                                namelist.add(detail);
                            }

                            mAdapter = new ListAdapter(namelist);
//                            RecyclerView.LayoutManager mlayoutmanager = new LinearLayoutManager(getApplicationContext());
//                            recyclerView.setLayoutManager(mlayoutmanager);
                            recyclerView.setAdapter(mAdapter);





                        }catch (Exception e){
                            Log.d("exception",e.toString());
                        }

                    }


                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "error" + error, Toast.LENGTH_SHORT).show();


            }
        });
        queue.add(stringRequest);
    }

public void load_more(){


    i++;


    pDialog = new ProgressDialog(MainActivity.this);
    pDialog.setMessage("Loading");
    pDialog.show();
    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
    String url = "http://api.androidhive.info/json/movies.json?page="+i;
    StringRequest stringRequest = new StringRequest(Request.Method.GET  , url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    pDialog.dismiss();
                    Log.d("response",response.toString());

                    try{

                        JSONArray jsonArray = new JSONArray(response);


                        if(jsonArray.length() > 0){

                            mAdapter.notifyItemInserted(namelist.size() - 1);
                            namelist.remove(namelist.size() - 1);
                            mAdapter.notifyItemRemoved(namelist.size());

                            int start = namelist.size();
                            int end = start + 15;



                            for(int i =0; i<jsonArray.length(); i++){

                                List1 detail = new List1();


                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String name = jsonObject.getString("title");
                                String year = jsonObject.getString("releaseYear");

                                detail.setName(name);
                                detail.setYear(year);

                                namelist.add(detail);
                            }
                            mAdapter.notifyItemInserted(namelist.size());
                        }









                    }catch (Exception e){
                        Log.d("exception",e.toString());
                    }

                }


            }, new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), "error" + error, Toast.LENGTH_SHORT).show();


        }
    });
    queue.add(stringRequest);

}

}
