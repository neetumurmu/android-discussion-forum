package com.example.discussionforum.activity;

/**
 * Created by neetu on 14/8/17.
 */
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.example.discussionforum.R;
import com.example.discussionforum.app.AppConfig;
import com.example.discussionforum.app.AppController;
import com.example.discussionforum.sqlite.SQLiteHandler;
import com.example.discussionforum.sqlite.SessionManager;

public class RegisterVerifActivity extends AppCompatActivity {

    private static final String TAG =  RegisterActivityVerif.class.getSimpleName();
    private Button btnRegisterVerif;
    private Button btnLinkToLogin;
    private Button btnLinkToHelp;
    private EditText inputRoll;
    private TextView errorMsg;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_verif);

        inputRoll = (EditText) findViewById(R.id.roll);
        btnRegisterVerif = (Button) findViewById(R.id.btnRegisterVerif);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLogin);
        errorMsg = (TextView) findViewById(R.id.net_err);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //Register Verification Button
        btnRegisterVerif.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String roll = inputRoll.getText().toString().trim();

                if(!roll.isEmpty()){
                    RegisterVerifUser(roll);
                }
                else{
                    errMsg.setVisibility(View.VISIBLE);
                    errMsg.setText("Please enter the credentials!");
                }
            }
        });

        //Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(RegisterVerifActivity.this , LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //User Verification Method
    private void RegisterVerifUser(final String roll){

        String tag_string_req = "req_register_verif";
        pDialog.setMessage("Proceeding...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST , AppConfig.URL_REGISTER_VERIF , new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {

               //hide dialog when request completes
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        Toast.makeText(getApplicationContext(), "Roll number verified successfully. You can register now!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterVerifActivity.this, RegisterActivity.class);

                        //Animation to open next activity sidewise
                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation,R.anim.animation2).toBundle();

                        intent.putExtra("roll", roll);
                        startActivity(intent, bndlanimation);
                        finish();
                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        errMsg.setVisibility(View.VISIBLE);
                        errMsg.setText(errorMsg);

                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

                //hide dialog to show network connection error
                hideDialog();
                errMsg.setVisibility(View.VISIBLE);
                errMsg.setText("Please check you internet connection.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("roll", roll);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //Method to show progress dialog
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    //Method to hide progress dialog
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    //Back button OnClick action
    @Override
    public void onBackPressed() {

        Intent i = new Intent(RegisterVerifActivity.this , LoginActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }
}
