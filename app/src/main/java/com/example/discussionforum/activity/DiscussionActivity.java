package com.example.discussionforum.activity;

/**
 * Created by neetu on 15/8/17.
 */
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.example.discussionforum.app.AppController;
import com.example.discussionforum.R;
import com.example.discussionforum.adapter.DividerItemDecoration;
import com.example.discussionforum.adapter.Thread;
import com.example.discussionforum.adapter.ThreadAdapter;
import com.example.discussionforum.app.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.ActivityOptions;

public class DiscussionActivity extends AppCompatActivity{

    private List<Thread> threadList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ThreadAdapter mAdapter;
    private ProgressDialog pDialog;
    private FloatingActionButton mSubmitButton;
    private TextView errMsg;
    private String que_id , topic , body , date , author;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fetch_answer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        que_id = bundle.getString("que_id");
        topic = bundle.getString("topic");
        body = bundle.getString("body");
        date = bundle.getString("date");
        author = bundle.getString("author");

        mSubmitButton = (FloatingActionButton) findViewById(R.id.write_answer);
        errMsg = (TextView) findViewById(R.id.net_err);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new ThreadAdapter(threadList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        fetchAnswer(que_id );

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiscussionActivity.this, PostAnswer.class);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation,R.anim.animation2).toBundle();

                intent.putExtra("que_id" , que_id);
                intent.putExtra("topic" , topic);
                intent.putExtra("body" , body);
                intent.putExtra("author" , author);
                intent.putExtra("date" , date);
                startActivity(intent, bndlanimation);
                //  overridePendingTransition(0, 0);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.comp, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Intent intent = new Intent(this, DiscussionActivity.class);
            intent.putExtra("que_id" , que_id);
            intent.putExtra("topic" , topic);
            intent.putExtra("body" , body);
            intent.putExtra("author" , author);
            intent.putExtra("date" , date);

            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchAnswer (final String que_id ){

        String tag_string_req = "req_loading";
        pDialog.setMessage("Loading ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FETCH_ANSWER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                prepareThread(que_id ,"QUESTION :  "+ topic , body , author , date);

                try {
                    JSONArray mArray = new JSONArray(response);

                    if(mArray.length() > 1) {
                        for (int i = 0; i < mArray.length() - 1; i++) {

                            JSONObject object = mArray.getJSONObject(i);

                            String id = object.getString("reply_id");
                            String body = object.getString("reply");
                            String author = object.getString("created_by");
                            String date = object.getString("created_at");

                            prepareThread(id, "", body, author, "answered  " + date);
                        }
                    }

                    if(mArray.length()==1) {
                        String body = mArray.getString(mArray.length() - 1);
                        prepareThread("", "", body, "", "");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                errMsg.setVisibility(View.VISIBLE);
                errMsg.setText("Can't connect to the Internet");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("que_id", que_id);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    private void prepareThread(final String id , final String topic , final String body , final String author , final String date) {

        Thread thread = new Thread(id ,topic, body , author , date);
        threadList.add(thread);

        mAdapter.notifyDataSetChanged();
    }

}
