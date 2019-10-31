package com.example.rahulrane.attendancesystemhotspotbased;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class LectureAddActivity extends AppCompatActivity {

    private AutoCompleteTextView lecturename;
    private AutoCompleteTextView classroom;
    private CalendarView calendar;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private CheckBox civil;
    private CheckBox mechanical;
    private CheckBox electrical;
    private CheckBox production;
    private CheckBox textile;
    private CheckBox electronics;
    private CheckBox computer;
    private CheckBox it;
    private CheckBox extc;
    private ArrayList<String> lectureBranches = new ArrayList<>();

    private String lectrureName;
    private String Classroom;
    private String year;
    private String Date;
    private String teacherName;
    private boolean cancel;
    private int c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_add);

        Database db = Database.getInstance();
        String[] userpieces = db.User.split("/");
        teacherName = userpieces[1];
        AutoCompleteTextView teachername = findViewById(R.id.teacher);
        teachername.setText(teacherName);

        final Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);
        int ampm = cal.get(Calendar.AM_PM);
        if(hour <= 12 && ampm == 1) {
            hour = hour + 12;
        }
        final AutoCompleteTextView mtimeStart = (AutoCompleteTextView) findViewById(R.id.timeStart);
        final AutoCompleteTextView mtimeEnd = (AutoCompleteTextView) findViewById(R.id.timeEnd);
        if(hour < 10) {
            String shour = "0"+String.valueOf(hour);
            if(min < 10) {
                String smin = "0"+String.valueOf(min);
                mtimeStart.setText(shour + ":" + smin);
            } else {
                mtimeStart.setText(shour + ":" + String.valueOf(min));
            }
            shour = "0"+String.valueOf(hour+1);
            if(min < 10) {
                String smin = "0"+String.valueOf(min);
                mtimeEnd.setText(shour + ":" + smin);
            } else {
                mtimeEnd.setText(shour + ":" + String.valueOf(min));
            }
        } else {
            if(min < 10) {
                String smin = "0"+String.valueOf(min);
                mtimeStart.setText(String.valueOf(hour) + ":" + smin);
                mtimeEnd.setText(String.valueOf(hour+1) + ":" + smin);
            } else {
                mtimeStart.setText(String.valueOf(hour) + ":" + String.valueOf(min));
                mtimeEnd.setText(String.valueOf(hour+1) + ":" + String.valueOf(min));
            }
        }

        if (cal.get(Calendar.DAY_OF_MONTH)<10 && cal.get(Calendar.MONTH)+1<10) {
            Date = "0" + cal.get(Calendar.DAY_OF_MONTH) + "/0" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.YEAR);
        }
        else if (cal.get(Calendar.DAY_OF_MONTH)<10) {
            Date = "0" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.YEAR);
        }
        else  if (cal.get(Calendar.MONTH)+1<10){
            Date = cal.get(Calendar.DAY_OF_MONTH) + "/0" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.YEAR);
        }
        else {
            Date = cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.YEAR);
        }
        calendar = (CalendarView) findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month = month + 1;
                if (dayOfMonth<10 && month<10) {
                    Date = "0" + dayOfMonth + "/0" + month + "/" + year;
                }
                else if (dayOfMonth<10) {
                    Date = "0" + dayOfMonth + "/" + month + "/" + year;
                }
                else  if (month<10){
                    Date = dayOfMonth + "/0" + month + "/" + year;
                }
                else {
                    Date = dayOfMonth + "/" + month + "/" + year;
                }
            }
        });

        Button mLectureAdd = (Button) findViewById(R.id.lecture_add_button);
        mLectureAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    cancel =false;

                    lecturename = (AutoCompleteTextView) findViewById(R.id.name);
                    lectrureName = lecturename.getText().toString();
                    if(lectrureName.equals("")) {
                        lecturename.setError("Enter Name of Lecture");
                        lecturename.requestFocus();
                        cancel = true;
                    }
                    classroom = (AutoCompleteTextView) findViewById(R.id.classroom);
                    Classroom = classroom.getText().toString();
                    if(Classroom.equals("")) {
                        classroom.setError("Enter Classroom");
                        classroom.requestFocus();
                        cancel = true;
                    }

                    String timeStart = mtimeStart.getText().toString();
                    String timeStart_shour = timeStart.substring(0,2);
                    int timeStart_hour = Integer.parseInt(timeStart_shour);
                    String timeStart_smin = timeStart.substring(3,5);
                    int timeStart_min = Integer.parseInt(timeStart_smin);
                    String timeEnd = mtimeEnd.getText().toString();
                    String timeEnd_shour = timeEnd.substring(0,2);
                    int timeEnd_hour = Integer.parseInt(timeEnd_shour);
                    String timeEnd_smin = timeEnd.substring(3,5);
                    int timeEnd_min = Integer.parseInt(timeEnd_smin);
                    if(!timeStart.contains(":") || !timeStart.substring(2,3).equals(":")) {
                        mtimeStart.setError("Enter Proper Time (HH:mm)");
                        mtimeStart.requestFocus();
                        cancel = true;
                    }

                    if(!timeEnd.contains(":") || !timeStart.substring(2,3).equals(":")) {
                        mtimeEnd.setError("Enter Proper Time (HH:mm)");
                        mtimeEnd.requestFocus();
                        cancel = true;
                    }

                    if(timeStart_min>=60) {
                        mtimeStart.setError("Enter Proper Time (HH:mm)");
                        mtimeStart.requestFocus();
                        cancel = true;
                    }

                    if(timeEnd_min>=60) {
                        mtimeEnd.setError("Enter Proper Time (HH:mm)");
                        mtimeEnd.requestFocus();
                        cancel = true;
                    }

                    if(timeStart_hour>timeEnd_hour) {
                        mtimeStart.setError("Enter Proper Time (HH:mm)");
                        mtimeStart.requestFocus();
                        mtimeEnd.setError("Enter Proper Time (HH:mm)");
                        mtimeEnd.requestFocus();
                        cancel = true;
                    }

                    if(timeStart_hour == timeEnd_hour && timeStart_min>timeEnd_min) {
                        mtimeStart.setError("Enter Proper Time (HH:mm)");
                        mtimeStart.requestFocus();
                        mtimeEnd.setError("Enter Proper Time (HH:mm)");
                        mtimeEnd.requestFocus();
                        cancel = true;
                    }

                    radioGroup = (RadioGroup) findViewById(R.id.year_group);
                    int radioId = radioGroup.getCheckedRadioButtonId();
                    if(findViewById(radioId) != null) {
                        radioButton = (RadioButton) findViewById(radioId);
                        year = radioButton.getText().toString();
                    }
                    else {
                        TextView yr = (TextView) findViewById(R.id.yearView);
                        yr.setError("Select Year");
                        yr.requestFocus();
                        cancel = true;
                    }

                    civil = findViewById(R.id.civil_button);
                    mechanical = findViewById(R.id.mechanical_button);
                    electrical = findViewById(R.id.electrical_button);
                    production = findViewById(R.id.production_button);
                    textile = findViewById(R.id.textile_button);
                    electronics = findViewById(R.id.electronics_button);
                    computer = findViewById(R.id.computer_button);
                    it = findViewById(R.id.it_button);
                    extc = findViewById(R.id.extc_button);

                    if(civil.isChecked()) {
                        lectureBranches.add(getString(R.string.civil));
                    }
                    if(mechanical.isChecked()) {
                        lectureBranches.add(getString(R.string.mechanical));
                    }
                    if(electrical.isChecked()) {
                        lectureBranches.add(getString(R.string.electrical));
                    }
                    if(production.isChecked()) {
                        lectureBranches.add(getString(R.string.production));
                    }
                    if(textile.isChecked()) {
                        lectureBranches.add(getString(R.string.textile));
                    }
                    if(electronics.isChecked()) {
                        lectureBranches.add(getString(R.string.electronics));
                    }
                    if(computer.isChecked()) {
                        lectureBranches.add(getString(R.string.computer));
                    }
                    if(it.isChecked()) {
                        lectureBranches.add(getString(R.string.it));
                    }
                    if(extc.isChecked()) {
                        lectureBranches.add(getString(R.string.extc));
                    }
                    if(!civil.isChecked() && !mechanical.isChecked() && !electrical.isChecked() && !production.isChecked() && !textile.isChecked() && !electronics.isChecked() && !computer.isChecked() && !it.isChecked() && !extc.isChecked()) {
                        TextView brn = (TextView) findViewById(R.id.branchView);
                        brn.setError("Select Atleast One Branch");
                        brn.requestFocus();
                        cancel = true;
                    }
                    if(!cancel) {
                        Database db = Database.getInstance();
                        Intent serviceintent = new Intent(LectureAddActivity.this, BackgroundService.class);
                        stopService(serviceintent);
                        db.putDataFireLecture(teacherName, lectrureName, Classroom, Date, timeStart, timeEnd, year, lectureBranches);
                        db.addLectures(teacherName, lectrureName, Classroom, Date, timeStart, timeEnd, year, lectureBranches);
                        startService(serviceintent);
                        Intent intent = new Intent(LectureAddActivity.this, TeacherMainActivity.class);
                        finish();
                        startActivity(intent);
                    }
                }
                else {
                    Toast.makeText(LectureAddActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
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
        Intent intent = new Intent(LectureAddActivity.this, TeacherMainActivity.class);
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