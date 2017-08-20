package com.example.discussionforum.activity;

/**
 * Created by neetu on 16/5/17.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.discussionforum.R;
import com.example.discussionforum.app.AppConfig;
import com.example.discussionforum.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPassword extends AppCompatActivity {

    public static final String TAG = "ResetPassword";
    private String email;
    private Button btnLogin;
    private Button btnProceed;
    private EditText inputEmail;
    private TextView view_net_err;
    private ProgressDialog pDialog;
    private String code_server;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        inputEmail = (EditText) findViewById(R.id.email);
        btnProceed = (Button) findViewById(R.id.emailVerif);
        btnLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        view_net_err = (TextView) findViewById(R.id.net_err);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);

        //Check here ..put it inside onclick

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(ResetPassword.this , LoginActivity.class);
                startActivity(i);
                finish();
            }

        });

        btnProceed.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                email = inputEmail.getText().toString().trim();

                if (!email.isEmpty()) {

                    verifyEmail(email);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter the email address!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(getApplicationContext() , LoginActivity.class);
        startActivity(i); finish();
        super.onBackPressed();
    }


    private void verifyEmail(final String email){

        String tag_string_req = "req_login";
        pDialog.setMessage("Verifying ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_VERIFY_EMAIL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
              //  Log.d(TAG, "Verifying Email Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        code_server = jObj.getString("code_server");
                        // Launch code verification activity
                        Intent intent = new Intent(ResetPassword.this, VerifyCode.class);
                        intent.putExtra("email", email);
                        intent.putExtra("code_server", code_server);
                        startActivity(intent);
                        finish();
                    } else {

                        // Email not registered
                        String errorMsg = jObj.getString("error_msg");
                        hideDialog();
                        view_net_err.setVisibility(View.VISIBLE);
                        view_net_err.setText(errorMsg);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
              //  Log.e(TAG, "Email Verification Error: " + error.getMessage());
                hideDialog();
                String net_err = "Can't connect to the Internet";
                view_net_err.setVisibility(View.VISIBLE);
                view_net_err.setText(net_err);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);

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

}

