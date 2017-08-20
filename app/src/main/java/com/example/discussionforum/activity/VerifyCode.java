package com.example.discussionforum.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.discussionforum.R;

/**
 * Created by neetu on 16/5/17.
 */

public class VerifyCode extends AppCompatActivity {

    private static final String TAG = "Code_Verification";
    private Button btnLogin;
    private Button btnCodeVerif;
    private EditText inputCode;
    private ProgressDialog pDialog;
    private String code_server, code_user;
    private String email;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_code);

        inputCode = (EditText) findViewById(R.id.code);
        btnLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        btnCodeVerif = (Button) findViewById(R.id.codeVerif);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);

        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");
        code_server = bundle.getString("code_server");

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext() , LoginActivity.class);
                startActivity(i);
                finish();
            }

        });

        btnCodeVerif.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                code_user = inputCode.getText().toString().trim();

                if(code_user.equals(code_server)){
                    Intent i = new Intent(getApplicationContext() , NewPassword.class);
                    i.putExtra("email", email);
                    startActivity(i);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "Code doesn't match! Please try again.", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(getApplicationContext() , ResetPassword.class);
        startActivity(i); finish();
        super.onBackPressed();
    }
}
