package com.example.discussionforum.activity;

import android.support.v7.app.AppCompatActivity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.discussionforum.R;
import com.example.discussionforum.app.AppConfig;
import com.example.discussionforum.app.AppController;
import com.example.discussionforum.sqlite.SQLiteHandler;
import com.example.discussionforum.sqlite.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by neetu on 15/8/17.
 */

public class PostAnswer extends AppCompatActivity{

    private Button mSubmitButton;
    private EditText inputAnswer;
    private EditText inputDescrp;
    private TextView errMsg;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private static final String TAG = "PostAnswer";
    private static final String REQUIRED = "Required";
    private String name ;
    private String que_id , topic , body , date , author;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_answer);

        Bundle bundle = getIntent().getExtras();
        que_id = bundle.getString("que_id");
        topic = bundle.getString("topic");
        body = bundle.getString("body");
        date = bundle.getString("date");
        author = bundle.getString("author");

        inputAnswer = (EditText) findViewById(R.id.answer);
        mSubmitButton = (Button) findViewById(R.id.submit_post);
        errMsg = (TextView) findViewById(R.id.net_err);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        name = user.get("name");

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer = inputAnswer.getText().toString().trim();

                if (!answer.isEmpty()) {
                    submitPost(que_id , answer , name);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please type your answer!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(PostAnswer.this , DiscussionActivity.class);
        intent.putExtra("que_id" , que_id);
        intent.putExtra("topic" , topic);
        intent.putExtra("body" , body);
        intent.putExtra("author" , author);
        intent.putExtra("date" , date);
        startActivity(intent); finish();
        super.onBackPressed();
    }

    private void submitPost(final String que_id , final String answer , final String name ){

        pDialog.setMessage("Submitting ...");
        showDialog();
        String tag_string_req = "req_answer";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_POST_ANSWER, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        
                        Toast.makeText(getApplicationContext(), "Answer Submitted successfully! ", Toast.LENGTH_LONG).show();
                        // Launch login activity
                        Intent intent = new Intent(PostAnswer.this, DiscussionActivity.class);
                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation2,R.anim.animation).toBundle();
                        intent.putExtra("que_id" , que_id);
                        intent.putExtra("topic" , topic);
                        intent.putExtra("body" , body);
                        intent.putExtra("author" , author);
                        intent.putExtra("date" , date);
                        startActivity(intent, bndlanimation);
                        //overridePendingTransition(0, 0);
                        finish();
                    } else {

                        String errorMsg = jObj.getString("error_msg");
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
                params.put("que_id" , que_id);
                params.put("answer", answer);
                params.put("author", name);

                return params;
            }

        };

        // Adding request to request queue
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

}
