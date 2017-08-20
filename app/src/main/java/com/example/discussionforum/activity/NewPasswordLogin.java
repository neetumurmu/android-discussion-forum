package com.example.discussionforum.activity;

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
import com.example.discussionforum.sqlite.SQLiteHandler;
import com.example.discussionforum.sqlite.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by neetu on 8/17/17.
 */

public class NewPasswordLogin extends AppCompatActivity {

    private Button btnLogin;
    private Button btnResetPassword;
    private EditText inputRoll;
    private TextView view_net_err;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private String anon_name;
    private String fragment_index = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_conf);

        inputRoll = (EditText) findViewById(R.id.roll);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnResetPassword = (Button) findViewById(R.id.resetpassword);
        view_net_err = (TextView) findViewById(R.id.net_err);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String roll = inputRoll.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!roll.isEmpty() && !password.isEmpty()) {

                    checkLogin(roll, password);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ResetPassword.class);
                startActivity(i);
                finish();
            }
        });

    }


    @Override
    public void onBackPressed() {

        Intent i = new Intent(getApplicationContext() , LoginActivity.class);
        startActivity(i); finish();
        super.onBackPressed();
    }


    private void checkLogin(final String roll, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //  Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String roll = user.getString("roll");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");
                        String permission = user.getString("permission");
                        String anon_name = user.getString("anon_name");

                        // Inserting row in users table
                        db.addUser(name, email, permission, anon_name ,created_at);

                        // Launch main activity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                        intent.putExtra("fragment_index" , fragment_index);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e(TAG, "Login Error: " + error.getMessage());
                hideDialog();
                String net_err = "Can't connect to the Internet";
                view_net_err.setVisibility(View.VISIBLE);
                view_net_err.setText(net_err);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("roll", roll);
                params.put("password", password);

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
