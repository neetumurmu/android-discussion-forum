package com.example.discussionforum.activity;

/**
 * Created by neetu on 15/8/17.
 */
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

public class AskQuestion extends AppCompatActivity{

    private Button mSubmitButton;
    private EditText inputTopic;
    private EditText inputDescrp;
    private TextView errMsg;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private String name;
    private static final String TAG = "AskQuestion";
    private static final String REQUIRED = "Required";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_question);

        inputTopic = (EditText) findViewById(R.id.topic);
        inputDescrp = (EditText) findViewById(R.id.description);
        mSubmitButton = (Button) findViewById(R.id.submit_post);
        errMsg = (TextView) findViewById(R.id.net_err);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        name = user.get("name");

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = inputTopic.getText().toString().trim();
                String description = inputDescrp.getText().toString().trim();

                if (!topic.isEmpty() && !description.isEmpty()) {
                    submitPost(topic , description , name);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter the details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();
    }

    private void submitPost(final String topic , final String description , final String name){

        pDialog.setMessage("Submitting ...");
        showDialog();
        String tag_string_req = "req_submit";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ASK_QUE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {


                        Toast.makeText(getApplicationContext(), "Submitted successfully!", Toast.LENGTH_LONG).show();

                        // LAUNCH MAIN ACTIVITY
                        Intent intent = new Intent(AskQuestion.this, MainActivity.class);
                        startActivity(intent);
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
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("topic", topic);
                params.put("description", description);
                params.put("name", name);

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
