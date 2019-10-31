package com.example.rahulrane.attendancesystemhotspotbased;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class TeacherLectureDetailsActivity extends AppCompatActivity {

    private String lecture;
    private String[] lecturepieces;
    private int n;

    private ProgressBar progressBar;

    private ArrayList<String> Present = new ArrayList<>();

    //private DuringLecture duringLecture = new DuringLecture();
    //private StudentEnter studentEnter = new StudentEnter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_lecture_details);

        lecture = getIntent().getStringExtra("lecture");
        lecturepieces = lecture.split("#");

        AutoCompleteTextView teachername = findViewById(R.id.teacher);
        AutoCompleteTextView lecturename = findViewById(R.id.name);
        AutoCompleteTextView classroom = findViewById(R.id.classroom);
        AutoCompleteTextView date = findViewById(R.id.date);
        AutoCompleteTextView mtimeStart = findViewById(R.id.timeStart);
        AutoCompleteTextView mtimeEnd = findViewById(R.id.timeEnd);
        AutoCompleteTextView year = findViewById(R.id.year);
        AutoCompleteTextView mBranch = findViewById(R.id.branch);

        teachername.setText(lecturepieces[0]);
        lecturename.setText(lecturepieces[1]);
        classroom.setText(lecturepieces[2]);
        date.setText(lecturepieces[3]);
        mtimeStart.setText(lecturepieces[4]);
        mtimeEnd.setText(lecturepieces[5]);
        year.setText(lecturepieces[6]);
        mBranch.setText(lecturepieces[7]);

        progressBar = findViewById(R.id.progressBar);
        final Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);
        int ampm = cal.get(Calendar.AM_PM);
        if(hour <= 12 && ampm == 1) {
            hour = hour + 12;
        }
        String time;
        if(hour < 10) {
            String shour = "0"+String.valueOf(hour);
            if(min < 10) {
                String smin = "0"+String.valueOf(min);
                time = (shour + ":" + smin);
            } else {
                time = (shour + ":" + String.valueOf(min));
            }
        } else {
            if(min < 10) {
                String smin = "0"+String.valueOf(min);
                time = (String.valueOf(hour) + ":" + smin);
            } else {
                time = (String.valueOf(hour) + ":" + String.valueOf(min));
            }
        }
        int timedone = ((Integer.parseInt(time.substring(0, 2)) - Integer.parseInt(lecturepieces[4].substring(0, 2))) * 60) + (Integer.parseInt(time.substring(3, 5)) - Integer.parseInt(lecturepieces[4].substring(3, 5)));
        int totaltime = ((Integer.parseInt(lecturepieces[5].substring(0, 2)) - Integer.parseInt(lecturepieces[4].substring(0, 2))) * 60) + (Integer.parseInt(lecturepieces[5].substring(3, 5)) - Integer.parseInt(lecturepieces[4].substring(3, 5)));
        progressBar.setMax(100);
        int percent = timedone/totaltime*100;
        progressBar.setProgress(percent);

        if (Integer.parseInt(time.substring(0, 2)) >= Integer.parseInt(lecturepieces[5].substring(0, 2)) && (Integer.parseInt(time.substring(3, 5)) >= (Integer.parseInt(lecturepieces[5].substring(3, 5)) - 1))) {
            TextView attendanceList = findViewById(R.id.attendanceList);
            attendanceList.setVisibility(View.VISIBLE);
            final Database db = Database.getInstance();
            LinearLayout lL = (LinearLayout) findViewById(R.id.linearLayout);
            lL.setVisibility(View.VISIBLE);
            lL.removeAllViews();
            for (String student : db.Student_Data) {
                final String[] studentpieces = student.split("/");
                if (studentpieces[4].equals(lecturepieces[6])) {
                    String branch;
                    int k = 1;
                    for (int i = 0; i < lecturepieces[7].length(); i++) {
                        if (lecturepieces[7].charAt(i) == ',' || lecturepieces[7].charAt(i) == ']') {
                            branch = lecturepieces[7].substring(k, i);
                            k = i + 2;
                            if (studentpieces[5].equals(branch)) {
                                for (final String data : db.AttendanceRegister) {
                                    final String[] pieces = lecture.split("#");
                                    if (lecturepieces[0].equals(pieces[0]) && lecturepieces[1].equals(pieces[1]) && lecturepieces[2].equals(pieces[2]) && lecturepieces[3].equals(pieces[3]) && lecturepieces[4].equals(pieces[4]) && lecturepieces[5].equals(pieces[5]) && lecturepieces[6].equals(pieces[6]) && lecturepieces[7].equals(pieces[7])) {
                                        lL = (LinearLayout) findViewById(R.id.linearLayout);
                                        LinearLayout lLM = new LinearLayout(this);
                                        LinearLayout.LayoutParams lLMPrams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        );
                                        lLM.setOrientation(LinearLayout.HORIZONTAL);
                                        lLMPrams.setMargins(10, 10, 10, 10);
                                        lLM.setPadding(10, 10, 10, 10);
                                        lLM.setLayoutParams(lLMPrams);
                                        if (pieces[8].contains(studentpieces[0])) {
                                            lLM.setBackgroundColor(Color.parseColor("#00ff00"));
                                        } else if (!pieces[8].contains(studentpieces[0])) {
                                            lLM.setBackgroundColor(Color.parseColor("#ff0000"));
                                        }
                                        lL.addView(lLM);
                                        LinearLayout linearLayout = new LinearLayout(this);
                                        LinearLayout.LayoutParams linearLayoutPrams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        );
                                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                                        linearLayoutPrams.setMargins(10, 10, 10, 10);
                                        linearLayout.setPadding(10, 10, 10, 10);
                                        linearLayout.setLayoutParams(linearLayoutPrams);
                                        lLM.addView(linearLayout);
                                        TextView textView = new TextView(this);
                                        LinearLayout.LayoutParams textViewPrams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        );
                                        textView.setTextSize(18);
                                        textView.setText(studentpieces[0]);
                                        textView.setLayoutParams(textViewPrams);
                                        linearLayout.addView(textView);
                                        textView = new TextView(this);
                                        textView.setTextSize(18);
                                        textView.setText(studentpieces[5]);
                                        textView.setLayoutParams(textViewPrams);
                                        linearLayout.addView(textView);
                                        Button button = new Button(this);
                                        LinearLayout.LayoutParams buttonPrams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        );
                                        button.setTextSize(16);
                                        if (data.contains(studentpieces[0])) {
                                            button.setText("Mark Absent");
                                            button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                                        String Lecture = "";
                                                        String t = pieces[0];
                                                        Present.clear();
                                                        String name;
                                                        int h = 1;
                                                        for (int j = 0; j < pieces[8].length(); j++) {
                                                            if (pieces[8].charAt(j) == ',' || pieces[8].charAt(j) == ']') {
                                                                name = pieces[8].substring(h, j);
                                                                h = j + 2;
                                                                if (!name.equals("")) {
                                                                    Present.add(name);
                                                                }
                                                            }
                                                        }
                                                        Lecture = pieces[0] + "#" + pieces[1] + "#" + pieces[2] + "#" + pieces[3] + "#" + pieces[4] + "#" + pieces[5] + "#" + pieces[6] + "#" + pieces[7];
                                                        Intent serviceintent = new Intent(TeacherLectureDetailsActivity.this, BackgroundService.class);
                                                        stopService(serviceintent);
                                                        db.removeAttendance(Lecture);
                                                        db.AttendanceRegister.remove(data);
                                                        Present.remove(studentpieces[0]);
                                                        db.AttendanceRegister.add(Lecture + "#" + Present);
                                                        db.temp.add(Lecture + "#" + Present);
                                                        startService(serviceintent);
                                                        finish();
                                                        Intent intent = new Intent(TeacherLectureDetailsActivity.this, TeacherLectureDetailsActivity.class);
                                                        intent.putExtra("lecture", lecture);
                                                        startActivity(intent);
                                                    }
                                                    else {
                                                        Toast.makeText(TeacherLectureDetailsActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        } else if (!data.contains(studentpieces[0])) {
                                            button.setText("Mark Present");
                                            button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                                        String Lecture = "";
                                                        Present.clear();
                                                        String name;
                                                        int h = 1;
                                                        for (int j = 0; j < pieces[8].length(); j++) {
                                                            if (pieces[8].charAt(j) == ',' || pieces[8].charAt(j) == ']') {
                                                                name = pieces[8].substring(h, j);
                                                                h = j + 2;
                                                                if (!name.equals("")) {
                                                                    Present.add(name);
                                                                }
                                                            }
                                                        }
                                                        Lecture = pieces[0] + "#" + pieces[1] + "#" + pieces[2] + "#" + pieces[3] + "#" + pieces[4] + "#" + pieces[5] + "#" + pieces[6] + "#" + pieces[7];
                                                        Intent serviceintent = new Intent(TeacherLectureDetailsActivity.this, BackgroundService.class);
                                                        stopService(serviceintent);
                                                        db.removeAttendance(Lecture);
                                                        db.AttendanceRegister.remove(data);
                                                        Present.add(studentpieces[0]);
                                                        db.AttendanceRegister.add(Lecture + "#" + Present);
                                                        db.temp.add(Lecture + "#" + Present);
                                                        startService(serviceintent);
                                                        finish();
                                                        Intent intent = new Intent(TeacherLectureDetailsActivity.this, TeacherLectureDetailsActivity.class);
                                                        intent.putExtra("lecture", lecture);
                                                        startActivity(intent);
                                                    }
                                                    else {
                                                        Toast.makeText(TeacherLectureDetailsActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                        button.setLayoutParams(buttonPrams);
                                        lLM.addView(button);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /*Button takeAttendance = findViewById(R.id.takeAttendanceButton);
        takeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR);
                int min = cal.get(Calendar.MINUTE);
                int ampm = cal.get(Calendar.AM_PM);
                if(hour <= 12 && ampm == 1) {
                    hour = hour + 12;
                }
                String time;
                if(hour < 10) {
                    String shour = "0"+String.valueOf(hour);
                    if(min < 10) {
                        String smin = "0"+String.valueOf(min);
                        time = (shour + ":" + smin);
                    } else {
                        time = (shour + ":" + String.valueOf(min));
                    }
                } else {
                    if(min < 10) {
                        String smin = "0"+String.valueOf(min);
                        time = (String.valueOf(hour) + ":" + smin);
                    } else {
                        time = (String.valueOf(hour) + ":" + String.valueOf(min));
                    }
                }
                String Date;
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

                if (!Date.equals(lecturepieces[3])) {
                    Snackbar.make(v, "Lecture Yet to Begin", Snackbar.LENGTH_LONG).show();
                }
                else if((Integer.parseInt(time.substring(0,2))<=Integer.parseInt(lecturepieces[4].substring(0,2)) && (Integer.parseInt(time.substring(3,5))<Integer.parseInt(lecturepieces[4].substring(3,5))))) {
                    Snackbar.make(v, "Lecture Yet to Begin", Snackbar.LENGTH_LONG).show();
                }
                else if((Integer.parseInt(time.substring(0,2))>=Integer.parseInt(lecturepieces[5].substring(0,2)) && (Integer.parseInt(time.substring(3,5))>Integer.parseInt(lecturepieces[5].substring(3,5))))) {
                    Snackbar.make(v, "Lecture has been Completed", Snackbar.LENGTH_LONG).show();
                }
                else {*/

                    //Get Clients connected to hotspot
                    /*ArrayList<String> macList = new ArrayList<>();
                    int macCount = 0;
                    BufferedReader br = null;
                    try {
                        br = new BufferedReader(new FileReader("/proc/net/arp"));
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] splitted = line.split(" +");
                            if (splitted != null) {
                                String mac = splitted[3];
                                if (mac.matches("..:..:..:..:..:..")) {
                                    macCount++;
                                    macList.add(mac);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Exception in getting clients", Toast.LENGTH_SHORT).show();
                    }*//*
                }
            }
        });*/
    }

    /*public class DuringLecture extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            Random rand = new Random();
            n = rand.nextInt(lecturetime)+1;
            if (n>(lecturetime-1)) {
                DuringLecture duringLecture = new DuringLecture();
                duringLecture.execute();
            }
            lecturetime = lecturetime - n;
            Toast.makeText(getApplicationContext(), String.valueOf(lecturetime), Toast.LENGTH_LONG).show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                Thread.sleep(n*60*1000);
            } catch (InterruptedException e) {
                return false;
            }

            final Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR);
            int min = cal.get(Calendar.MINUTE);
            int ampm = cal.get(Calendar.AM_PM);
            if(hour <= 12 && ampm == 1) {
                hour = hour + 12;
            }
            String time;
            if(hour < 10) {
                String shour = "0"+String.valueOf(hour);
                if(min < 10) {
                    String smin = "0"+String.valueOf(min);
                    time = (shour + ":" + smin);
                } else {
                    time = (shour + ":" + String.valueOf(min));
                }
            } else {
                if(min < 10) {
                    String smin = "0"+String.valueOf(min);
                    time = (String.valueOf(hour) + ":" + smin);
                } else {
                    time = (String.valueOf(hour) + ":" + String.valueOf(min));
                }
            }
            int timeProgress = ((Integer.parseInt(time.substring(0,2))-Integer.parseInt(lecturepieces[4].substring(0,2)))*60)+(Integer.parseInt(time.substring(3,5))-Integer.parseInt(lecturepieces[4].substring(3,5)));
            progressBar.setProgress(timeProgress/totaltime*100);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean done) {
            if (done) {
                Ping ping = new Ping();
                ping.execute();
            }
        }
    }

    public class StudentEnter extends AsyncTask<Void, Void, Boolean> {

        private boolean success;
        private WifiManager wifiManager;
        private WifiConfiguration wifiConfiguration;

        @Override
        protected void onPreExecute() {
            success = true;
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
            wifiConfiguration = new WifiConfiguration();
            Method[] mMethods = wifiManager.getClass().getDeclaredMethods();
            for (Method mMethod : mMethods) {
                if (mMethod.getName().equals("setWifiApEnabled")) {
                    wifiConfiguration.SSID = lecturepieces[0];
                    wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    wifiManager.addNetwork(wifiConfiguration);
                    wifiManager.saveConfiguration();
                    try {
                        Object o = mMethod.invoke(wifiManager, wifiConfiguration, true);
                        if (o == null) {
                            success = false;
                            Toast.makeText(getApplicationContext(), "Returned NULL", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    } catch (Exception e) {
                        success = false;
                        Toast.makeText(getApplicationContext(), "1/"+e.getMessage()+"/"+e.getStackTrace().toString()+"/"+e.getCause(), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    try {
                        Object o = mMethod.invoke(wifiManager, null, true);
                        if (o == null) {
                            success = false;
                            Toast.makeText(getApplicationContext(), "Returned NULL", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    } catch (Exception e) {
                        success = false;
                        Toast.makeText(getApplicationContext(), "2/"+e.getMessage()+"/"+e.getStackTrace().toString()+"/"+e.getCause(), Toast.LENGTH_SHORT).show();
                        break;
                    }*/
                    /*try {
                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        Method method = connectivityManager.getClass().getMethod("setMobileDataEnabled", boolean.class);
                        Object o = method.invoke(connectivityManager, false);
                        if (o == null) {
                            success = false;
                            Toast.makeText(getApplicationContext(), "Returned NULL", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    } catch (Exception e) {
                        success = false;
                        Toast.makeText(getApplicationContext(), "2/"+e.getMessage()+"/"+e.getStackTrace().toString()+"/"+e.getCause(), Toast.LENGTH_LONG).show();
                        break;
                    }*/
                    /*break;
                }
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                Thread.sleep(buffertime*60*1000);
            } catch (InterruptedException e) {
                return false;
            }

            final Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR);
            int min = cal.get(Calendar.MINUTE);
            int ampm = cal.get(Calendar.AM_PM);
            if(hour <= 12 && ampm == 1) {
                hour = hour + 12;
            }
            String time;
            if(hour < 10) {
                String shour = "0"+String.valueOf(hour);
                if(min < 10) {
                    String smin = "0"+String.valueOf(min);
                    time = (shour + ":" + smin);
                } else {
                    time = (shour + ":" + String.valueOf(min));
                }
            } else {
                if(min < 10) {
                    String smin = "0"+String.valueOf(min);
                    time = (String.valueOf(hour) + ":" + smin);
                } else {
                    time = (String.valueOf(hour) + ":" + String.valueOf(min));
                }
            }
            int timeProgress = ((Integer.parseInt(time.substring(0,2))-Integer.parseInt(lecturepieces[4].substring(0,2)))*60)+(Integer.parseInt(time.substring(3,5))-Integer.parseInt(lecturepieces[4].substring(3,5)));
            progressBar.setProgress(timeProgress/totaltime*100);

            if (!success) {
                onPreExecute();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                Method[] mMethods = wifiManager.getClass().getDeclaredMethods();
                for (Method mMethod : mMethods) {
                    if (mMethod.getName().equals("setWifiApEnabled")) {
                        try {
                            Object o = mMethod.invoke(wifiManager, wifiConfiguration, false);
                            if (o == null) {
                                Toast.makeText(getApplicationContext(), "Returned NULL", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "1/" + e.getMessage() + "/" + e.getStackTrace().toString() + "/" + e.getCause(), Toast.LENGTH_SHORT).show();
                            break;
                        }
                        try {
                            Object o = mMethod.invoke(wifiManager, null, false);
                            if (o == null) {
                                Toast.makeText(getApplicationContext(), "Returned NULL", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "2/" + e.getMessage() + "/" + e.getStackTrace().toString() + "/" + e.getCause(), Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                }
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                }

                duringLecture.execute();
            }
        }
    }

    public class Ping extends AsyncTask<Void, Void, Boolean> {

        private WifiManager wifiManager;

        @Override
        protected void onPreExecute() {
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                return false;
            }

            final Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR);
            int min = cal.get(Calendar.MINUTE);
            int ampm = cal.get(Calendar.AM_PM);
            if(hour <= 12 && ampm == 1) {
                hour = hour + 12;
            }
            String time;
            if(hour < 10) {
                String shour = "0"+String.valueOf(hour);
                if(min < 10) {
                    String smin = "0"+String.valueOf(min);
                    time = (shour + ":" + smin);
                } else {
                    time = (shour + ":" + String.valueOf(min));
                }
            } else {
                if(min < 10) {
                    String smin = "0"+String.valueOf(min);
                    time = (String.valueOf(hour) + ":" + smin);
                } else {
                    time = (String.valueOf(hour) + ":" + String.valueOf(min));
                }
            }
            int timeProgress = ((Integer.parseInt(time.substring(0,2))-Integer.parseInt(lecturepieces[4].substring(0,2)))*60)+(Integer.parseInt(time.substring(3,5))-Integer.parseInt(lecturepieces[4].substring(3,5)));
            progressBar.setProgress(timeProgress/totaltime*100);

            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> scanResultList = wifiManager.getScanResults();
            Database db = Database.getInstance();
            for (ScanResult result : scanResultList) {
                for (String data : db.Student_Data) {
                    String[] pieces = data.split("/");
                    String branch;
                    int k=1;
                    for(int i=0;i<lecturepieces[7].length();i++) {
                        if (lecturepieces[7].charAt(i) == ',' || lecturepieces[7].charAt(i) == ']') {
                            branch = lecturepieces[7].substring(k, i);
                            k = i + 2;
                            if (pieces[5].equals(branch)) {
                                if (result.SSID.equals(pieces[0])) {
                                    if (!Present.contains(pieces[0]) && !Absent.contains(pieces[0])) {
                                        Present.add(pieces[0]);
                                    }
                                }
                                else {
                                    if (Present.contains(pieces[0])) {
                                        Present.remove(pieces[0]);
                                        Absent.add(pieces[0]);
                                    }
                                    else {
                                        Absent.add(pieces[0]);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean done) {
            if (done) {
                wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                }
                if (lecturetime >= 1) {
                    DuringLecture duringLecture = new DuringLecture();
                    duringLecture.execute();
                }
                else {
                    Database db = Database.getInstance();
                    db.AttendanceRegister.add(lecture + "#" + Present + "#" + Absent);
                }
            }
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:
                Database db = Database.getInstance();
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    Intent serviceintent = new Intent(this, BackgroundService.class);
                    stopService(serviceintent);
                    String data = "";
                    db.Lectures.remove(lecture);
                    db.removeLecture(lecture);
                    for (String d : db.AttendanceRegister) {
                        String[] pieces = d.split("#");
                        if (lecture.equals(pieces[0] + "#" + pieces[1] + "#" + pieces[2] + "#" + pieces[3] + "#" + pieces[4] + "#" + pieces[5] + "#" + pieces[6] + "#" + pieces[7])) {
                            data = d;
                        }
                    }
                    db.removeAttendance(lecture);
                    db.AttendanceRegister.remove(data);
                    startService(serviceintent);
                    onBackPressed();
                }
                else {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teacher_lecture_details, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TeacherLectureDetailsActivity.this, TeacherMainActivity.class);
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
