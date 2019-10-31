package com.example.rahulrane.attendancesystemhotspotbased;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;

public class TeacherUserDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_user_details);

        Database db = Database.getInstance();
        String[] userPieces = db.User.split("/");

        AutoCompleteTextView mNameView = findViewById(R.id.name);
        AutoCompleteTextView mEmailView = findViewById(R.id.email);
        AutoCompleteTextView mMacAddView = findViewById(R.id.MacAdd);
        AutoCompleteTextView mDepartmentView = findViewById(R.id.department);

        mNameView.setText(userPieces[1]);
        mEmailView.setText(userPieces[2]);
        mMacAddView.setText(userPieces[4]);
        mDepartmentView.setText(userPieces[5]);
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
        Intent intent = new Intent(TeacherUserDetailsActivity.this, TeacherMainActivity.class);
        finish();
        startActivity(intent);
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
