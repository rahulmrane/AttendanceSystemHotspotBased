package com.example.rahulrane.attendancesystemhotspotbased;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class StudentAttendanceRegisterActivity extends AppCompatActivity {

    private boolean cancel = false;
    private String StartDate;
    private String EndDate;
    private CalendarView mStartDate;
    private CalendarView mEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance_register);

        final AutoCompleteTextView mSubject = findViewById(R.id.subject);

        StartDate = "";
        EndDate = "";

        Button showAttendance = findViewById(R.id.show_attendance);
        showAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Subject = mSubject.getText().toString();
                if (Subject.equals("")) {
                    mSubject.setError("Enter Subject Name");
                    mSubject.requestFocus();
                    cancel = true;
                }

                final Calendar cal = Calendar.getInstance();
                mStartDate = (CalendarView) findViewById(R.id.startDate);
                mEndDate = (CalendarView) findViewById(R.id.endDate);
                mStartDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        if (dayOfMonth < 10 && month < 10) {
                            StartDate = "0" + dayOfMonth + "/0" + month + "/" + year;
                        } else if (dayOfMonth < 10) {
                            StartDate = "0" + dayOfMonth + "/" + month + "/" + year;
                        } else if (month < 10) {
                            StartDate = dayOfMonth + "/0" + month + "/" + year;
                        } else {
                            StartDate = dayOfMonth + "/" + month + "/" + year;
                        }
                    }
                });
                mEndDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        if (dayOfMonth < 10 && month < 10) {
                            EndDate = "0" + dayOfMonth + "/0" + month + "/" + year;
                        } else if (dayOfMonth < 10) {
                            EndDate = "0" + dayOfMonth + "/" + month + "/" + year;
                        } else if (month < 10) {
                            EndDate = dayOfMonth + "/0" + month + "/" + year;
                        } else {
                            EndDate = dayOfMonth + "/" + month + "/" + year;
                        }
                    }
                });
                if (StartDate.equals("")) {
                    if (cal.get(Calendar.DAY_OF_MONTH) < 10 && cal.get(Calendar.MONTH) + 1 < 10) {
                        StartDate = "0" + cal.get(Calendar.DAY_OF_MONTH) + "/0" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
                    } else if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                        StartDate = "0" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
                    } else if (cal.get(Calendar.MONTH) + 1 < 10) {
                        StartDate = cal.get(Calendar.DAY_OF_MONTH) + "/0" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
                    } else {
                        StartDate = cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
                    }
                }
                if (EndDate.equals("")) {
                    if (cal.get(Calendar.DAY_OF_MONTH) < 10 && cal.get(Calendar.MONTH) + 1 < 10) {
                        EndDate = "0" + cal.get(Calendar.DAY_OF_MONTH) + "/0" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
                    } else if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                        EndDate = "0" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
                    } else if (cal.get(Calendar.MONTH) + 1 < 10) {
                        EndDate = cal.get(Calendar.DAY_OF_MONTH) + "/0" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
                    } else {
                        EndDate = cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
                    }
                }
                if (!cancel) {
                    String check = "";
                    LinearLayout lL = (LinearLayout) findViewById(R.id.linearLayout);
                    lL.removeAllViews();
                    Database db = Database.getInstance();
                    String[] userPieces = db.User.split("/");
                    for (String lecture : db.Lectures) {
                        String[] pieces = lecture.split("#");
                        if (!pieces[1].equals(Subject)) {
                            if (!check.equals("Found")) {
                                check = "Subject";
                            }
                        }
                        else if (pieces[1].equals(Subject)){
                            check = "Found";
                            if (Integer.parseInt(pieces[3].substring(0,2))>=Integer.parseInt(StartDate.substring(0,2)) && Integer.parseInt(pieces[3].substring(0,2))<=Integer.parseInt(EndDate.substring(0,2)) && Integer.parseInt(pieces[3].substring(3,5))>=Integer.parseInt(StartDate.substring(3,5)) && Integer.parseInt(pieces[3].substring(3,5))<=Integer.parseInt(EndDate.substring(3,5)) && Integer.parseInt(pieces[3].substring(6,10))>=Integer.parseInt(StartDate.substring(6,10)) && Integer.parseInt(pieces[3].substring(6,10))<=Integer.parseInt(EndDate.substring(6,10))) {
                                if (userPieces[5].equals(pieces[6])) {
                                    String branch;
                                    int k = 1;
                                    for (int i = 0; i < pieces[7].length(); i++) {
                                        if (pieces[7].charAt(i) == ',' || pieces[7].charAt(i) == ']') {
                                            branch = pieces[7].substring(k, i);
                                            k = i + 2;
                                            if (userPieces[6].equals(branch)) {
                                                lL = (LinearLayout) findViewById(R.id.linearLayout);
                                                LinearLayout linearLayout = new LinearLayout(StudentAttendanceRegisterActivity.this);
                                                LinearLayout.LayoutParams linearLayoutPrams = new LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                                );
                                                linearLayout.setOrientation(LinearLayout.VERTICAL);
                                                linearLayoutPrams.setMargins(10, 10, 10, 10);
                                                linearLayout.setBackgroundColor(Color.parseColor("#ffffbb33"));
                                                for (String Attendance : db.AttendanceRegister) {
                                                    String[] attendancePieces = Attendance.split("#");
                                                    if (Attendance.contains(lecture) && attendancePieces[8].contains(userPieces[1])) {
                                                        linearLayout.setBackgroundColor(Color.parseColor("#00ff00"));
                                                        break;
                                                    }
                                                    else if (Attendance.contains(lecture) && !attendancePieces[8].contains(userPieces[1])) {
                                                        linearLayout.setBackgroundColor(Color.parseColor("#ff0000"));
                                                        break;
                                                    }
                                                }
                                                linearLayout.setPadding(10, 10, 10, 10);
                                                linearLayout.setLayoutParams(linearLayoutPrams);
                                                lL.addView(linearLayout);
                                                TextView textView = new TextView(StudentAttendanceRegisterActivity.this);
                                                LinearLayout.LayoutParams textViewPrams = new LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                                );
                                                textView.setTextSize(25);
                                                textView.setText(pieces[1]);
                                                textView.setLayoutParams(textViewPrams);
                                                linearLayout.addView(textView);
                                                textView = new TextView(StudentAttendanceRegisterActivity.this);
                                                textView.setTextSize(20);
                                                textViewPrams.setMargins(10, 0, 0, 0);
                                                textView.setText(pieces[3]);
                                                textView.setLayoutParams(textViewPrams);
                                                linearLayout.addView(textView);
                                                textView = new TextView(StudentAttendanceRegisterActivity.this);
                                                textView.setTextSize(20);
                                                textViewPrams.setMargins(10, 0, 0, 0);
                                                textView.setText(pieces[4] + " - " + pieces[5]);
                                                textView.setLayoutParams(textViewPrams);
                                                linearLayout.addView(textView);
                                                textView = new TextView(StudentAttendanceRegisterActivity.this);
                                                textView.setTextSize(15);
                                                textViewPrams.setMargins(10, 0, 0, 0);
                                                textView.setText(pieces[2]);
                                                textView.setLayoutParams(textViewPrams);
                                                linearLayout.addView(textView);
                                                textView = new TextView(StudentAttendanceRegisterActivity.this);
                                                textView.setTextSize(15);
                                                textViewPrams.setMargins(10, 0, 0, 0);
                                                textView.setText(pieces[0]);
                                                textView.setLayoutParams(textViewPrams);
                                                linearLayout.addView(textView);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (check.equals("Subject")) {
                        mSubject.setError("Subject Not Found");
                        mSubject.requestFocus();
                    }
                }
                StartDate = "";
                EndDate = "";
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
        Intent intent = new Intent(StudentAttendanceRegisterActivity.this, StudentMainActivity.class);
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
