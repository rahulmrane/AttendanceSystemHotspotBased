package com.example.rahulrane.attendancesystemhotspotbased;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        final EditText mPrepassword = findViewById(R.id.prepassword);
        final EditText mPassword = findViewById(R.id.password);
        final EditText mRepassword = findViewById(R.id.repassword);

        Button mChange = findViewById(R.id.change);
        mChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    boolean cancel = false;
                    String prepassword = mPrepassword.getText().toString();
                    String password = mPassword.getText().toString();
                    String repassword = mRepassword.getText().toString();

                    Database db = Database.getInstance();
                    String[] userpieces = db.User.split("/");

                    if (!userpieces[3].equals(prepassword)) {
                        mPrepassword.setError("Passwords Don't Match");
                        mPrepassword.requestFocus();
                        cancel = true;
                    }
                    if(!password.equals(repassword)) {
                        mPassword.setError("Passwords Don't Match");
                        mPassword.requestFocus();
                        mRepassword.setError("Passwords Don't Match");
                        mRepassword.requestFocus();
                        cancel = true;
                    }
                    if(!cancel) {
                        if (userpieces[0].equals("Student")) {
                            for (String data : db.Student_Data) {
                                String[] pieces = data.split("/");
                                if(userpieces[1].equals(pieces[0])) {
                                    Intent serviceintent = new Intent(ChangePasswordActivity.this, BackgroundService.class);
                                    stopService(serviceintent);
                                    db.removeDataFireStudentData(pieces[0], pieces[1], password, pieces[3], pieces[4], pieces[5]);
                                    db.Student_Data.remove(data);
                                    db.putDataFireStudentData(pieces[0], pieces[1], password, pieces[3], pieces[4], pieces[5]);
                                    db.addStudentData(pieces[0], pieces[1], password, pieces[3], pieces[4], pieces[5]);
                                    startService(serviceintent);
                                    Intent intent = new Intent(ChangePasswordActivity.this, StudentMainActivity.class);
                                    finish();
                                    startActivity(intent);
                                }
                            }
                        }
                        else if (userpieces[0].equals("Teacher")) {
                            for (String data : db.Teacher_Data) {
                                String[] pieces = data.split("/");
                                if(userpieces[1].equals(pieces[0])) {
                                    Intent serviceintent = new Intent(ChangePasswordActivity.this, BackgroundService.class);
                                    stopService(serviceintent);
                                    db.removeDataFireTeacherData(pieces[0], pieces[1], password, pieces[3], pieces[4]);
                                    db.Teacher_Data.remove(data);
                                    db.putDataFireTeacherData(pieces[0], pieces[1], password, pieces[3], pieces[4]);
                                    db.addTeacherData(pieces[0], pieces[1], password, pieces[3], pieces[4]);
                                    startService(serviceintent);
                                    Intent intent = new Intent(ChangePasswordActivity.this, StudentMainActivity.class);
                                    finish();
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                }
                else {
                    Toast.makeText(ChangePasswordActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Database db = Database.getInstance();
        String[] userpieces = db.User.split("/");
        if (userpieces[0].equals("Student")) {
            Intent intent = new Intent(ChangePasswordActivity.this, StudentMainActivity.class);
            finish();
            startActivity(intent);
        }
        else if (userpieces[0].equals("Teacher")) {
            Intent intent = new Intent(ChangePasswordActivity.this, TeacherMainActivity.class);
            finish();
            startActivity(intent);
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
