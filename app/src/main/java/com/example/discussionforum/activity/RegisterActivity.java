package com.example.discussionforum.activity;

/**
 * Created by neetu on 14/8/17.
 */
import android.support.v7.app.AppCompatActivity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import android.view.MenuItem;

public class RegisterActivity extends AppCompatActivity{

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private Button btnLinkToHelp;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private TextView errMsg;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private String jsonResponse;
    private String anon_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        final String roll = bundle.getString("roll");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    registerUser(roll , name, email, password);
                } else {
                    errMsg.setVisibility(View.VISIBLE);
                    errMsg.setText("Please enter the credentials.");
                }
            }
        });
    }


    // User Registration
    private void registerUser(final String roll ,final String name, final String email,
                              final String password) {

        String tag_string_req = "req_register"; // Tag used to cancel the request

        pDialog.setMessage("Signing up ...");   //Setting message to show in dialog
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        // User successfully stored in MySQL, set login session true then store the user in sqlite
                        session.setLogin(true);

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");
                        String permission = user.getString("permission");

                        // Inserting row in users table
                        db.addUser(name, email, permission, anon_name ,created_at );
                        Toast.makeText(getApplicationContext(), "User successfully registered.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation,R.anim.animation2).toBundle();
                        intent.putExtra("fragment_index" , fragment_index);
                        intent.putExtra("roll", roll);
                        startActivity(intent, bndlanimation);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg , Toast.LENGTH_LONG).show();
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
                errMsg.setText("Please check you internet connection.");
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("roll",roll);
                params.put("name", name);
                params.put("email", email);
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

    @Override
    public void onBackPressed() {

        Intent i = new Intent(RegisterActivity.this , RegisterActivityVerif.class);
        startActivity(i); finish();
        super.onBackPressed();
    }


}
