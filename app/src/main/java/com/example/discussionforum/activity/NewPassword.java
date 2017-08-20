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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by neetu on 17/5/17.
 */

public class NewPassword extends AppCompatActivity {
    private static final String TAG = "New_Password";
    private Button btnResetPassword;
    private Button btnLogin;
    private EditText inputPassword;
    private TextView view_net_err;
    private ProgressDialog pDialog;
    private String password, email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_password);

        inputPassword = (EditText) findViewById(R.id.password);
        btnResetPassword = (Button) findViewById(R.id.resetpassword);
        view_net_err = (TextView) findViewById(R.id.net_err);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);

        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");

        btnResetPassword.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String password = inputPassword.getText().toString().trim();

                if (!password.isEmpty()) {

                    resetPassword(email, password);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter the new password!", Toast.LENGTH_LONG)
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

    private void resetPassword(final String email, final String password) {

        String tag_string_req = "req_login";
        pDialog.setMessage("Please Wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_NEW_PASSWORD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
             //   Log.d(TAG, "Resetting Password Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        Intent intent = new Intent(getApplicationContext(), NewPasswordLogin.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
             //   Log.e(TAG, "Login Error: " + error.getMessage());
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
                params.put("password", password);

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
