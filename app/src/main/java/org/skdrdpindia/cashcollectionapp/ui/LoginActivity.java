package org.skdrdpindia.cashcollectionapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import org.skdrdpindia.cashcollectionapp.R;
import org.skdrdpindia.cashcollectionapp.ui.MainActivity;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.

        txtPassword = (EditText) findViewById(R.id.password);
        Button btnSignin = (Button) findViewById(R.id.SignIn_Button);
        
        // Start signin process once button is clicked.
        btnSignin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        // Reset errors.
        txtPassword.setError(null);

        // Store values at the time of the login attempt.
        String password = txtPassword.getText().toString();

        boolean isSigninCancelled = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            txtPassword.setError(getString(R.string.error_invalid_password));
            focusView = txtPassword;
            isSigninCancelled = true;
        }

        if (isSigninCancelled) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Start the login procedings. Read the IMEI number
            TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            String CREDENTIALS = imei.substring(imei.length() - 4, imei.length());
            if (CREDENTIALS.equals(password)) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
            else {
                //Password wrong, ask user to sign in again.
                txtPassword.setError("Wrong password! try again.");
                txtPassword.requestFocus();
            }
        }
    }

    private boolean isPasswordValid(String password) {
        //check whether entred password is 4 char long.
        return password.length() == 4;
    }

}


