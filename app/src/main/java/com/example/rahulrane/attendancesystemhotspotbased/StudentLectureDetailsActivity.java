package com.example.rahulrane.attendancesystemhotspotbased;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class StudentLectureDetailsActivity extends AppCompatActivity {

    private String[] lecturepieces;
    private boolean attendance = false;
    private int n;
    private int lecturetime;
    private int totaltime;
    private int buffertime;

    private ProgressBar progressBar;

    private DuringLecture duringLecture = new DuringLecture();
    private StudentEnter studentEnter = new StudentEnter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_lecture_details);

        String lecture = getIntent().getStringExtra("lecture");
        lecturepieces = lecture.split("#");

        AutoCompleteTextView teachername = findViewById(R.id.teacher);
        AutoCompleteTextView lecturename = findViewById(R.id.name);
        AutoCompleteTextView classroom = findViewById(R.id.classroom);
        AutoCompleteTextView date = findViewById(R.id.date);
        AutoCompleteTextView mtimeStart = findViewById(R.id.timeStart);
        AutoCompleteTextView mtimeEnd = findViewById(R.id.timeEnd);
        AutoCompleteTextView year = findViewById(R.id.year);
        AutoCompleteTextView branch = findViewById(R.id.branch);

        teachername.setText(lecturepieces[0]);
        lecturename.setText(lecturepieces[1]);
        classroom.setText(lecturepieces[2]);
        date.setText(lecturepieces[3]);
        mtimeStart.setText(lecturepieces[4]);
        mtimeEnd.setText(lecturepieces[5]);
        year.setText(lecturepieces[6]);
        branch.setText(lecturepieces[7]);

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
        progressBar.setProgress(timedone/totaltime*100);

        /*final Button markAttendance = findViewById(R.id.markAttendanceButton);
        markAttendance.setOnClickListener(new View.OnClickListener() {
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
                else {
                    buffertime = ((Integer.parseInt(lecturepieces[4].substring(0,2))-Integer.parseInt(time.substring(0,2)))*60)+(Integer.parseInt(lecturepieces[4].substring(3,5))-Integer.parseInt(time.substring(3,5)))+1;
                    if (buffertime <= 0) {
                        Snackbar.make(v, "Lecture has been Started", Snackbar.LENGTH_LONG).show();
                    }
                    else {

                    }
                }
            }
        });*/
    }

    public class DuringLecture extends AsyncTask<Void, Void, Boolean> {
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

        @Override
        protected void onPreExecute() {
            success = false;
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if(!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }

            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            List<ScanResult> scanResultList = wifiManager.getScanResults();
            for (ScanResult result : scanResultList) {
                if (result.SSID.equals(lecturepieces[0])) {
                    success = true;
                    List<WifiConfiguration> wifiConfigurationList = wifiManager.getConfiguredNetworks();
                    if (wifiConfigurationList != null) {
                        for (WifiConfiguration wificonfig : wifiConfigurationList) {
                            if (wificonfig.SSID.contains(lecturepieces[0])) {
                                wifiManager.disableNetwork(wificonfig.networkId);
                                wifiManager.removeNetwork(wificonfig.networkId);
                            }
                        }
                    }
                    wifiManager.saveConfiguration();
                    wifiConfiguration.SSID = "\"" + lecturepieces[0] + "\"";
                    wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    int res = wifiManager.addNetwork(wifiConfiguration);
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(res, true);
                    wifiManager.reconnect();
                    wifiManager.setWifiEnabled(true);
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

            if (success) {
                attendance = true;
            }
            else {
                onPreExecute();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean done) {
            if (done) {
                wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if(wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                }
                if (/*attendance*/true) {
                    duringLecture.execute();
                }
            }
        }
    }

    public class Ping extends AsyncTask<Void, Void, Boolean> {

        private boolean success;
        private WifiManager wifiManager;
        private WifiConfiguration wifiConfiguration;

        @Override
        protected void onPreExecute() {
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
                    }
                    break;
                }
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

            if (!success) {
                onPreExecute();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean done) {
            if (done) {
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
                if(!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                }
                if(wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                }
                if (lecturetime >= 1) {
                    DuringLecture duringLecture = new DuringLecture();
                    duringLecture.execute();
                }
            }
        }
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
        Intent intent = new Intent(StudentLectureDetailsActivity.this, StudentMainActivity.class);
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
