package com.example.rahulrane.attendancesystemhotspotbased;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.NetworkInterface;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CHANGE_NETWORK_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_SETTINGS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int ACTION_MANAGE_WRITE_SETTINGS = 0;
    private static final int ACTION_MANAGE_PERMISSIONS = 1;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private AutoCompleteTextView MacAddView;

    private String mac_addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        int permission = PermissionChecker.checkCallingOrSelfPermission(getApplicationContext(), Manifest.permission.WRITE_SETTINGS);
        if (permission == PermissionChecker.PERMISSION_DENIED) {
            mayRequestWriteSettings();
        }

        mayRequestPermissions();

        Database db = Database.getInstance();

        /*SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Saved Data", MODE_PRIVATE);
        if (!db.User.equals("Logged Out")) {
            if (sharedPreferences.getString("User", null) != null) {
                db.User = sharedPreferences.getString("User", null);
            }
        }
        int i=0;
        String data;
        db.Student_Data.clear();
        while (sharedPreferences.getString("Student_Data/" + i, null) != null) {
            data = sharedPreferences.getString("Student_Data/" + i, null);
            db.Student_Data.add(data);
            i++;
        }
        i=0;
        db.Teacher_Data.clear();
        while (sharedPreferences.getString("Teacher_Data/" + i, null) != null) {
            data = sharedPreferences.getString("Teacher_Data/" + i, null);
            db.Teacher_Data.add(data);
            i++;
        }
        i=0;
        db.Lectures.clear();
        while (sharedPreferences.getString("Lectures/" + i, null) != null) {
            data = sharedPreferences.getString("Lectures/" + i, null);
            db.Lectures.add(data);
            i++;
        }
        i=0;
        db.AttendanceRegister.clear();
        while (sharedPreferences.getString("AttendanceRegister/" + i, null) != null) {
            data = sharedPreferences.getString("AttendanceRegister/" + i, null);
            db.AttendanceRegister.add(data);
            i++;
        }*/

        BackgroundService bs = new BackgroundService();
        if (!db.User.equals("")) {
            bs.getData();
        }

        if(!db.User.equals("")) {
            String[] userPieces = db.User.split("/");
            if(userPieces[0].equals("Student")) {
                Intent intent = new Intent(this, StudentMainActivity.class);
                finish();
                startActivity(intent);
            }
            else if(userPieces[0].equals("Teacher")) {
                Intent intent = new Intent(this, TeacherMainActivity.class);
                finish();
                startActivity(intent);
            }
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            db.getDataFire();
        }
        else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }

        MacAddView = (AutoCompleteTextView) findViewById(R.id.MacAdd);
        mac_addr = getMacAdd();
        MacAddView.setText(mac_addr);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        attemptLogin();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    attemptLogin();
                }
                else {
                    Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private static String getMacAdd() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for(NetworkInterface nif : all) {
                if(!nif.getName().equalsIgnoreCase("wlan0"))
                    continue;
                byte[] macbytes = nif.getHardwareAddress();
                if(macbytes == null) {
                    return "";
                }
                StringBuilder sb = new StringBuilder();
                for(byte b : macbytes) {
                    sb.append(String.format("%02X:",b));
                }
                if(sb.length() > 0 ) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                return sb.toString();
            }
        } catch(Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    private boolean mayRequestWriteSettings() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!Settings.System.canWrite(getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 200);
        }

        if (checkSelfPermission(WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(WRITE_SETTINGS)) {
            Snackbar.make(mEmailView, "Modify Settings Permission Needed for Wifi Hotspot", Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{WRITE_SETTINGS}, ACTION_MANAGE_WRITE_SETTINGS);
                        }
                    }).show();
        } else {
            requestPermissions(new String[]{WRITE_SETTINGS}, ACTION_MANAGE_WRITE_SETTINGS);
        }
        return false;
    }

    private boolean mayRequestPermissions() {
        //Access Coarse Location
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)) {
            Toast.makeText(this, "Location Permission Needed for Wifi", Toast.LENGTH_LONG).show();
        } else {
            requestPermissions(new String[]{ACCESS_COARSE_LOCATION}, ACTION_MANAGE_PERMISSIONS);
        }

        //Access Fine Location
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Location Permission Needed for Wifi", Toast.LENGTH_LONG).show();
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, ACTION_MANAGE_PERMISSIONS);
        }

        //Read Phone State
        if (checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
            Toast.makeText(this, "Permission Needed", Toast.LENGTH_LONG).show();
        } else {
            requestPermissions(new String[]{READ_PHONE_STATE}, ACTION_MANAGE_PERMISSIONS);
        }
        return false;
    }
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
            Database db = Database.getInstance();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private int sectionno;
        private int c;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            Database db = Database.getInstance();
            c=0;
            for (String credential : db.Student_Data) {
                c++;
            }
            for (String data : db.Student_Data) {
                String[] pieces = data.split("/");
                if (pieces[1].equals(mEmail)) {
                    if(pieces[2].equals(mPassword)) {
                        // Account exists, return true if the password matches.
                        if (pieces[3].equals(mac_addr)) {
                            sectionno = 1;
                            db.User = "Student/" + data;
                            return true;
                        }
                        else {
                            c=-1;
                            return false;
                        }
                    }
                    else {
                        c=-2;
                        return false;
                    }
                }
                else {
                    c--;
                }
            }

            c=0;
            for (String credential : db.Teacher_Data) {
                c++;
            }
            for (String data : db.Teacher_Data) {
                String[] pieces = data.split("/");
                if (pieces[1].equals(mEmail)) {
                    if(pieces[2].equals(mPassword)) {
                        // Account exists, return true if the password matches.
                        if (pieces[3].equals(mac_addr)) {
                            sectionno = 2;
                            db.User = "Teacher/" + data;
                            return true;
                        }
                        else {
                            c=-1;
                            return false;
                        }
                    }
                    else {
                        c=-2;
                        return false;
                    }
                }
                else {
                    c--;
                }
            }

            // TODO: register the new account here.
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                Intent serviceintent = new Intent(LoginActivity.this, BackgroundService.class);
                startService(serviceintent);
                if (sectionno == 1) {
                    Intent intent = new Intent(LoginActivity.this, StudentMainActivity.class);
                    startActivity(intent);
                } else if (sectionno == 2) {
                    Intent intent = new Intent(LoginActivity.this, TeacherMainActivity.class);
                    startActivity(intent);
                }
            }
            else if(c == 0) {
                mEmailView.setError(getString(R.string.error_invalid_email));
                mEmailView.requestFocus();
            }
            else if(c == -1) {
                MacAddView.setError("WiFi Mac Address doesnot Match");
                MacAddView.requestFocus();
            }
            else if (c==-2) {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    @Override
    public void onStop () {
        /*Database db = Database.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("Saved Data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        editor.putString("User", db.User);
        int i=0;
        for (String data : db.Student_Data) {
            editor.putString("Student_Data/"+i, data);
            i++;
        }
        i=0;
        for (String data : db.Teacher_Data) {
            editor.putString("Teacher_Data/"+i, data);
            i++;
        }
        i=0;
        for (String data : db.Lectures) {
            editor.putString("Lectures/"+i, data);
            i++;
        }
        i=0;
        for (String data : db.AttendanceRegister) {
            editor.putString("Attendance_Register/"+i, data);
            i++;
        }
        editor.commit();*/
        super.onStop();
    }
}

