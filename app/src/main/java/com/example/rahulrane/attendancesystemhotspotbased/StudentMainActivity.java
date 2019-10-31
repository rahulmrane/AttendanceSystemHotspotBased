package com.example.rahulrane.attendancesystemhotspotbased;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class StudentMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView date;
    private CalendarView calendar;
    private String Date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Database db = Database.getInstance();
        final String[] userPieces = db.User.split("/");

        final Calendar cal = Calendar.getInstance();
        date = (TextView) findViewById(R.id.textViewDate);
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
                Date = dayOfMonth + "/" + month + "/" + year;
                if(dayOfMonth == cal.get(Calendar.DAY_OF_MONTH) && month == cal.get(Calendar.MONTH)+1 && year == cal.get(Calendar.YEAR))
                {
                    Date = "Today";
                    date.setText(Date);
                }
                else if(dayOfMonth == cal.get(Calendar.DAY_OF_MONTH)+1 && month == cal.get(Calendar.MONTH)+1 && year == cal.get(Calendar.YEAR))
                {
                    Date = "Tomorrow";
                    date.setText(Date);
                }
                else if(dayOfMonth == cal.get(Calendar.DAY_OF_MONTH)-1 && month == cal.get(Calendar.MONTH)+1 && year == cal.get(Calendar.YEAR))
                {
                    Date = "Yesterday";
                    date.setText(Date);
                }
                else
                    date.setText(Date);
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

                LinearLayout lL = (LinearLayout) findViewById(R.id.linearLayout);
                lL.removeAllViews();

                Database db = Database.getInstance();
                for (final String credential : db.Lectures) {
                    String[] pieces = credential.split("#");
                    if (pieces[3].equals(Date)) {
                        if(userPieces[5].equals(pieces[6])) {
                            String branch;
                            int k=1;
                            for(int i=0;i<pieces[7].length();i++) {
                                if (pieces[7].charAt(i) == ',' || pieces[7].charAt(i) == ']') {
                                    branch = pieces[7].substring(k, i);
                                    k = i + 2;
                                    if (userPieces[6].equals(branch)) {
                                        lL = (LinearLayout) findViewById(R.id.linearLayout);
                                        LinearLayout linearLayout = new LinearLayout(StudentMainActivity.this);
                                        LinearLayout.LayoutParams linearLayoutPrams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        );
                                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                                        linearLayout.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(StudentMainActivity.this, StudentLectureDetailsActivity.class);
                                                intent.putExtra("lecture", credential);
                                                finish();
                                                startActivity(intent);
                                            }
                                        });
                                        linearLayoutPrams.setMargins(10, 10, 10, 10);
                                        linearLayout.setBackgroundColor(Color.parseColor("#ffffbb33"));
                                        for (String Attenddance : db.AttendanceRegister) {
                                            String[] attendancePieces = Attenddance.split("#");
                                            if (Attenddance.contains(userPieces[1])) {
                                                linearLayout.setBackgroundColor(Color.parseColor("#00ff00"));
                                                break;
                                            }
                                            else if (!Attenddance.contains(userPieces[1])) {
                                                linearLayout.setBackgroundColor(Color.parseColor("#ff0000"));
                                                break;
                                            }
                                        }
                                        linearLayout.setPadding(10,10,10,10);
                                        linearLayout.setLayoutParams(linearLayoutPrams);
                                        lL.addView(linearLayout);
                                        TextView textView = new TextView(StudentMainActivity.this);
                                        LinearLayout.LayoutParams textViewPrams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        );
                                        textView.setTextSize(25);
                                        textView.setText(pieces[1]);
                                        textView.setLayoutParams(textViewPrams);
                                        linearLayout.addView(textView);
                                        textView = new TextView(StudentMainActivity.this);
                                        textView.setTextSize(20);
                                        textViewPrams.setMargins(10, 0, 0, 0);
                                        textView.setText(pieces[4] + " - " + pieces[5]);
                                        textView.setLayoutParams(textViewPrams);
                                        linearLayout.addView(textView);
                                        textView = new TextView(StudentMainActivity.this);
                                        textView.setTextSize(15);
                                        textViewPrams.setMargins(10, 0, 0, 0);
                                        textView.setText(pieces[2]);
                                        textView.setLayoutParams(textViewPrams);
                                        linearLayout.addView(textView);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

        });

        db = Database.getInstance();
        if(db.Lectures != null) {
            for (final String credential : db.Lectures) {
                String[] pieces = credential.split("#");
                if (pieces[3].equals(Date)) {
                    if(userPieces[5].equals(pieces[6])) {
                        String branch;
                        int k=1;
                        for(int i=0;i<pieces[7].length();i++) {
                            if (pieces[7].charAt(i) == ',' || pieces[7].charAt(i) == ']') {
                                branch = pieces[7].substring(k, i);
                                k = i + 2;
                                if (userPieces[6].equals(branch)) {
                                    LinearLayout lL = (LinearLayout) findViewById(R.id.linearLayout);
                                    LinearLayout linearLayout = new LinearLayout(this);
                                    LinearLayout.LayoutParams linearLayoutPrams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                                    linearLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(StudentMainActivity.this, StudentLectureDetailsActivity.class);
                                            intent.putExtra("lecture", credential);
                                            finish();
                                            startActivity(intent);
                                        }
                                    });
                                    linearLayoutPrams.setMargins(10, 10, 10, 10);
                                    linearLayout.setBackgroundColor(Color.parseColor("#ffffbb33"));
                                    for (String Attenddance : db.AttendanceRegister) {
                                        String[] attendancePieces = Attenddance.split("#");
                                        if (Attenddance.contains(userPieces[1])) {
                                            linearLayout.setBackgroundColor(Color.parseColor("#00ff00"));
                                            break;
                                        }
                                        else if (!Attenddance.contains(userPieces[1])) {
                                            linearLayout.setBackgroundColor(Color.parseColor("#ff0000"));
                                            break;
                                        }
                                    }
                                    linearLayout.setPadding(10,10,10,10);
                                    linearLayout.setLayoutParams(linearLayoutPrams);
                                    lL.addView(linearLayout);
                                    TextView textView = new TextView(this);
                                    LinearLayout.LayoutParams textViewPrams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    textView.setTextSize(25);
                                    textView.setText(pieces[1]);
                                    textView.setLayoutParams(textViewPrams);
                                    linearLayout.addView(textView);
                                    textView = new TextView(this);
                                    textView.setTextSize(20);
                                    textViewPrams.setMargins(10, 0, 0, 0);
                                    textView.setText(pieces[4] + " - " + pieces[5]);
                                    textView.setLayoutParams(textViewPrams);
                                    linearLayout.addView(textView);
                                    textView = new TextView(this);
                                    textView.setTextSize(15);
                                    textViewPrams.setMargins(10, 0, 0, 0);
                                    textView.setText(pieces[2]);
                                    textView.setLayoutParams(textViewPrams);
                                    linearLayout.addView(textView);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView textViewName = header.findViewById(R.id.textViewName);
        textViewName.setText(userPieces[1]);
        TextView textViewEmail = header.findViewById(R.id.textViewEmail);
        textViewEmail.setText(userPieces[2]);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.student_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Database db = Database.getInstance();
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                db.getDataFire();
            }
            else {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_details) {
            Intent intent = new Intent(this, StudentUserDetailsActivity.class);
            finish();
            startActivity(intent);
        } else if (id == R.id.nav_attendance_register) {
            Intent intent = new Intent(this, StudentAttendanceRegisterActivity.class);
            finish();
            startActivity(intent);
        } else if (id == R.id.nav_change_password) {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            finish();
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Database db = Database.getInstance();
            Intent serviceintent = new Intent(this, BackgroundService.class);
            stopService(serviceintent);
            db.User = "";
            Intent intent = new Intent(this, LoginActivity.class);
            finish();
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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