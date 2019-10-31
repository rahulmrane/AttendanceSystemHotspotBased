package com.example.rahulrane.attendancesystemhotspotbased;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;
import java.util.Random;

public class BackgroundService extends Service {

    private int lecturetime;
    private int skiptimemin;
    private int skiptimehour;
    private int endtimemin;
    private int endtimehour;
    private static boolean attendance = false;
    private static boolean studentEnter = false;
    private static boolean Scan = false;
    private static boolean ping = true;
    private static boolean pingoff = false;

    private static ArrayList<String> Present = new ArrayList<>();
    private static ArrayList<String> Absent = new ArrayList<>();

    private WifiManager wifiManager;
    private WifiConfiguration wifiConfiguration = new WifiConfiguration();

    private static String User;
    private static ArrayList<String> Student_Data = new ArrayList<>();
    private static ArrayList<String> Teacher_Data = new ArrayList<>();
    private static ArrayList<String> Lectures = new ArrayList<>();
    private static ArrayList<String> AttendanceRegister = new ArrayList<>();

    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public void getData() {
        Database db = Database.getInstance();
        db.User = User;
        db.Student_Data = Student_Data;
        db.Teacher_Data = Teacher_Data;
        db.Lectures = Lectures;
        db.AttendanceRegister = AttendanceRegister;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Intent notificationintent = new Intent(this, BackgroundService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationintent, 0);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel("NOTIFICATION_CHANNEL_ID", "NOTIFICATION_CHANNEL", importance);
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationbuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(BackgroundService.this, "NOTIFICATION_CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContentTitle("Attendance System (Hotspot Based)")
                .setContentText("Service Running");
        startForeground(1, notificationbuilder.build());

        final Database db = Database.getInstance();
        User = db.User;
        Student_Data = db.Student_Data;
        Teacher_Data = db.Teacher_Data;
        Lectures = db.Lectures;
        AttendanceRegister = db.AttendanceRegister;

        new Thread(new Runnable() {
            @Override
            public void run() {

                wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                while (true) {

                    PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                    PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLock");
                    wakeLock.acquire();

                    final Calendar cal = Calendar.getInstance();
                    int hour = cal.get(Calendar.HOUR);
                    int min = cal.get(Calendar.MINUTE);
                    int ampm = cal.get(Calendar.AM_PM);
                    int sec = cal.get(Calendar.SECOND);
                    if (hour <= 12 && ampm == 1) {
                        hour = hour + 12;
                    }
                    String time;
                    if (hour < 10) {
                        String shour = "0" + String.valueOf(hour);
                        if (min < 10) {
                            String smin = "0" + String.valueOf(min);
                            time = (shour + ":" + smin);
                        } else {
                            time = (shour + ":" + String.valueOf(min));
                        }
                    } else {
                        if (min < 10) {
                            String smin = "0" + String.valueOf(min);
                            time = (String.valueOf(hour) + ":" + smin);
                        } else {
                            time = (String.valueOf(hour) + ":" + String.valueOf(min));
                        }
                    }
                    String Date;
                    if (cal.get(Calendar.DAY_OF_MONTH) < 10 && cal.get(Calendar.MONTH) + 1 < 10) {
                        Date = "0" + cal.get(Calendar.DAY_OF_MONTH) + "/0" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
                    } else if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                        Date = "0" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
                    } else if (cal.get(Calendar.MONTH) + 1 < 10) {
                        Date = cal.get(Calendar.DAY_OF_MONTH) + "/0" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
                    } else {
                        Date = cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
                    }
                    String[] userPieces = User.split("/");
                    if (userPieces[0].equals("Student")) {
                        for (final String lecture : Lectures) {
                            String[] pieces = lecture.split("#");
                            String mac_addr = "";
                            for (String data : Teacher_Data) {
                                String[] teacherPieces = data.split("/");
                                if (teacherPieces[0].equals(pieces[0])) {
                                    mac_addr = teacherPieces[3];
                                    break;
                                }
                            }
                            if (pieces[3].equals(Date)) {
                                if (userPieces[5].equals(pieces[6])) {
                                    String branch;
                                    int k = 1;
                                    for (int i = 0; i < pieces[7].length(); i++) {
                                        if (pieces[7].charAt(i) == ',' || pieces[7].charAt(i) == ']') {
                                            branch = pieces[7].substring(k, i);
                                            k = i + 2;
                                            if (userPieces[6].equals(branch)) {
                                                if (Integer.parseInt(time.substring(0, 2)) == Integer.parseInt(pieces[4].substring(0, 2)) && (Integer.parseInt(time.substring(3, 5)) == Integer.parseInt(pieces[4].substring(3, 5))) && sec == 0) {
                                                    try {
                                                        Thread.sleep(1000);
                                                    } catch (Exception e) {
                                                    }
                                                    lecturetime = ((Integer.parseInt(pieces[5].substring(0, 2)) - Integer.parseInt(pieces[4].substring(0, 2))) * 60) + (Integer.parseInt(pieces[5].substring(3, 5)) - Integer.parseInt(pieces[4].substring(3, 5)));
                                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        int importance = NotificationManager.IMPORTANCE_LOW;
                                                        NotificationChannel notificationChannel = new NotificationChannel("NOTIFICATION_CHANNEL_ID", "NOTIFICATION_CHANNEL", importance);
                                                        notificationChannel.enableLights(true);
                                                        notificationManager.createNotificationChannel(notificationChannel);
                                                    }
                                                    NotificationCompat.Builder notificationbuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(BackgroundService.this, "NOTIFICATION_CHANNEL_ID")
                                                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                                                            .setSmallIcon(R.mipmap.ic_launcher)
                                                            .setContentTitle(pieces[1] + " Lecture is Starting")
                                                            .setContentText(pieces[3] + " " + pieces[2] + " " + pieces[0]);
                                                    notificationManager.notify(Integer.parseInt(pieces[4].substring(0, 2) + pieces[4].substring(3, 5)), notificationbuilder.build());
                                                    if (!wifiManager.isWifiEnabled()) {
                                                        wifiManager.setWifiEnabled(true);
                                                    }
                                                    skiptimemin = 0;
                                                    skiptimehour = 0;
                                                    skiptimemin = skiptimemin + 9;
                                                    while ((Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin) >= 60) {
                                                        skiptimehour++;
                                                        skiptimemin = skiptimemin - 60;
                                                    }
                                                    Scan = true;
                                                }
                                                else if (Scan && Integer.parseInt(time.substring(0, 2)) == (Integer.parseInt(pieces[4].substring(0, 2)) + skiptimehour) && (Integer.parseInt(time.substring(3, 5)) == (Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin)) && sec == 30) {
                                                    wifiManager.startScan();
                                                    wifiConfiguration = new WifiConfiguration();
                                                    List<ScanResult> scanResultList = wifiManager.getScanResults();
                                                    for (ScanResult result : scanResultList) {
                                                        if (result.BSSID.equals(mac_addr)) {//todo
                                                            List<WifiConfiguration> wifiConfigurationList = wifiManager.getConfiguredNetworks();
                                                            if (wifiConfigurationList != null) {
                                                                for (WifiConfiguration wificonfig : wifiConfigurationList) {
                                                                    if (wificonfig.BSSID.contains(mac_addr)) {//todo
                                                                        wifiManager.disableNetwork(wificonfig.networkId);
                                                                        wifiManager.removeNetwork(wificonfig.networkId);
                                                                    }
                                                                }
                                                            }
                                                            wifiManager.saveConfiguration();
                                                            wifiConfiguration.SSID = pieces[0];//todo
                                                            wifiConfiguration.BSSID = mac_addr;
                                                            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                                                            int res = wifiManager.addNetwork(wifiConfiguration);
                                                            wifiManager.disconnect();
                                                            wifiManager.enableNetwork(res, true);
                                                            wifiManager.reconnect();
                                                            wifiManager.setWifiEnabled(true);
                                                            attendance = true;
                                                            if (wifiManager.isWifiEnabled()) {
                                                                wifiManager.setWifiEnabled(false);
                                                            }
                                                        }
                                                    }
                                                    skiptimemin = skiptimemin + 1;
                                                    while ((Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin) >= 60) {
                                                        skiptimehour++;
                                                        skiptimemin = skiptimemin - 60;
                                                    }
                                                    Scan = false;
                                                    studentEnter = true;
                                                }
                                                else if (studentEnter && Integer.parseInt(time.substring(0, 2)) == (Integer.parseInt(pieces[4].substring(0, 2)) + skiptimehour) && (Integer.parseInt(time.substring(3, 5)) == (Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin)) && sec == 0) {
                                                    try {
                                                        Thread.sleep(1000);
                                                    } catch (Exception e) {
                                                    }
                                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        int importance = NotificationManager.IMPORTANCE_LOW;
                                                        NotificationChannel notificationChannel = new NotificationChannel("NOTIFICATION_CHANNEL_ID", "NOTIFICATION_CHANNEL", importance);
                                                        notificationChannel.enableLights(true);
                                                        notificationManager.createNotificationChannel(notificationChannel);
                                                    }
                                                    NotificationCompat.Builder notificationbuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(BackgroundService.this, "NOTIFICATION_CHANNEL_ID")
                                                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                                                            .setSmallIcon(R.mipmap.ic_launcher)
                                                            .setContentTitle(pieces[1]+" Lecture has been Started")
                                                            .setContentText(pieces[3] + " " + pieces[2] + " " + pieces[0]);
                                                    notificationManager.notify(Integer.parseInt(pieces[4].substring(0,2)+pieces[4].substring(3,5)), notificationbuilder.build());
                                                    if (!attendance) {
                                                        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                            int importance = NotificationManager.IMPORTANCE_LOW;
                                                            NotificationChannel notificationChannel = new NotificationChannel("NOTIFICATION_CHANNEL_ID", "NOTIFICATION_CHANNEL", importance);
                                                            notificationChannel.enableLights(true);
                                                            notificationManager.createNotificationChannel(notificationChannel);
                                                        }
                                                        notificationbuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(BackgroundService.this, "NOTIFICATION_CHANNEL_ID")
                                                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                                .setContentTitle(pieces[1]+" Lecture Attendance : Absent")
                                                                .setContentText(pieces[3] + " " + pieces[2] + " " + pieces[0]);
                                                        notificationManager.notify(Integer.parseInt(pieces[4].substring(0,2)+pieces[4].substring(3,5)), notificationbuilder.build());
                                                    }

                                                    if (wifiManager.isWifiEnabled()) {
                                                        wifiManager.setWifiEnabled(false);
                                                    }
                                                    if ((lecturetime-2) > 0) {
                                                        lecturetime = lecturetime - 12;
                                                        skiptimemin = skiptimemin + 10;
                                                        while ((Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin) >= 60) {
                                                            skiptimehour++;
                                                            skiptimemin = skiptimemin - 60;
                                                        }
                                                    }
                                                    studentEnter = false;
                                                    ping = true;
                                                }
                                                else if (ping && attendance && Integer.parseInt(time.substring(0, 2)) == (Integer.parseInt(pieces[4].substring(0, 2)) + skiptimehour) && (Integer.parseInt(time.substring(3, 5)) == (Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin)) && sec == 0) {
                                                    try {
                                                        Thread.sleep(1000);
                                                    } catch (Exception e) {
                                                    }
                                                    wifiConfiguration = new WifiConfiguration();
                                                    Method[] mMethods = wifiManager.getClass().getDeclaredMethods();
                                                    for (Method mMethod : mMethods) {
                                                        if (mMethod.getName().equals("setWifiApEnabled")) {
                                                            wifiConfiguration.SSID = userPieces[1];
                                                            wifiConfiguration.BSSID = userPieces[4];//todo
                                                            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                                                            wifiManager.addNetwork(wifiConfiguration);
                                                            wifiManager.saveConfiguration();
                                                            try {
                                                                Object o = mMethod.invoke(wifiManager, wifiConfiguration, true);
                                                                if (o == null) {
                                                                    Toast.makeText(getApplicationContext(), "Returned NULL", Toast.LENGTH_SHORT).show();
                                                                    break;
                                                                }
                                                            } catch (Exception e) {
                                                                Toast.makeText(getApplicationContext(), "1/" + e.getMessage() + "/" + e.getStackTrace().toString() + "/" + e.getCause(), Toast.LENGTH_SHORT).show();
                                                                break;
                                                            }
                                                            try {
                                                                Object o = mMethod.invoke(wifiManager, null, true);
                                                                if (o == null) {
                                                                    Toast.makeText(getApplicationContext(), "Returned NULL", Toast.LENGTH_SHORT).show();
                                                                    break;
                                                                }
                                                            } catch (Exception e) {
                                                                Toast.makeText(getApplicationContext(), "2/" + e.getMessage() + "/" + e.getStackTrace().toString() + "/" + e.getCause(), Toast.LENGTH_SHORT).show();
                                                                break;
                                                            }
                                                            break;
                                                        }
                                                    }
                                                    skiptimemin = skiptimemin + 2;
                                                    while ((Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin) >= 60) {
                                                        skiptimehour++;
                                                        skiptimemin = skiptimemin - 60;
                                                    }
                                                    ping = false;
                                                }
                                                else if (attendance && Integer.parseInt(time.substring(0, 2)) == (Integer.parseInt(pieces[4].substring(0, 2)) + skiptimehour) && (Integer.parseInt(time.substring(3, 5)) == (Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin)) && sec == 0) {
                                                    wifiConfiguration = new WifiConfiguration();
                                                    Method[] mMethods = wifiManager.getClass().getDeclaredMethods();
                                                    for (Method mMethod : mMethods) {
                                                        if (mMethod.getName().equals("setWifiApEnabled")) {
                                                            wifiConfiguration.SSID = userPieces[1];
                                                            wifiConfiguration.BSSID = userPieces[4];//todo
                                                            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                                                            wifiManager.addNetwork(wifiConfiguration);
                                                            wifiManager.saveConfiguration();
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
                                                            break;
                                                        }
                                                    }
                                                    try {
                                                        Thread.sleep(1000);
                                                    } catch (Exception e) {
                                                    }
                                                    if ((lecturetime-2) > 0) {
                                                        lecturetime = lecturetime - 12;
                                                        skiptimemin = skiptimemin + 10;
                                                        while ((Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin) >= 60) {
                                                            skiptimehour++;
                                                            skiptimemin = skiptimemin - 60;
                                                        }
                                                    }
                                                    ping = true;
                                                }
                                                endtimemin = 0;
                                                endtimehour = 0;
                                                while ((Integer.parseInt(pieces[5].substring(3, 5)) - 5) < 0) {
                                                    endtimehour++;
                                                    endtimemin = 60 - 5 - Integer.parseInt(pieces[5].substring(3, 5));
                                                }
                                                if ((Integer.parseInt(pieces[5].substring(3, 5)) - 5) >= 0) {
                                                    endtimemin = Integer.parseInt(pieces[5].substring(3, 5));
                                                }
                                                if (Integer.parseInt(time.substring(0, 2)) == (Integer.parseInt(pieces[5].substring(0, 2)) - endtimehour) && Integer.parseInt(time.substring(3, 5)) == endtimemin && sec == 0) {
                                                    try {
                                                        Thread.sleep(1000);
                                                    } catch (Exception e) {
                                                    }
                                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        int importance = NotificationManager.IMPORTANCE_LOW;
                                                        NotificationChannel notificationChannel = new NotificationChannel("NOTIFICATION_CHANNEL_ID", "NOTIFICATION_CHANNEL", importance);
                                                        notificationChannel.enableLights(true);
                                                        notificationManager.createNotificationChannel(notificationChannel);
                                                    }
                                                    NotificationCompat.Builder notificationbuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(BackgroundService.this, "NOTIFICATION_CHANNEL_ID")
                                                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                                                            .setSmallIcon(R.mipmap.ic_launcher)
                                                            .setContentTitle(pieces[1]+" Lecture Attendance Marked")
                                                            .setContentText(pieces[3] + " " + pieces[2] + " " + pieces[0]);
                                                    notificationManager.notify(Integer.parseInt(pieces[4].substring(0,2)+pieces[4].substring(3,5)), notificationbuilder.build());
                                                    attendance = false;
                                                    skiptimemin = 0;
                                                    skiptimehour = 0;
                                                    Scan = false;
                                                    ping = false;
                                                    pingoff = false;
                                                    studentEnter = false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (userPieces[0].equals("Teacher")) {
                        for (final String lecture : Lectures) {
                            String[] pieces = lecture.split("#");
                            if (pieces[3].equals(Date)) {
                                if (pieces[0].equals(userPieces[1])) {
                                    if (Integer.parseInt(time.substring(0, 2)) == Integer.parseInt(pieces[4].substring(0, 2)) && (Integer.parseInt(time.substring(3, 5)) == Integer.parseInt(pieces[4].substring(3, 5))) && sec == 0) {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (Exception e) {
                                        }
                                        lecturetime = ((Integer.parseInt(pieces[5].substring(0, 2)) - Integer.parseInt(pieces[4].substring(0, 2))) * 60) + (Integer.parseInt(pieces[5].substring(3, 5)) - Integer.parseInt(pieces[4].substring(3, 5)));
                                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            int importance = NotificationManager.IMPORTANCE_LOW;
                                            NotificationChannel notificationChannel = new NotificationChannel("NOTIFICATION_CHANNEL_ID", "NOTIFICATION_CHANNEL", importance);
                                            notificationChannel.enableLights(true);
                                            notificationManager.createNotificationChannel(notificationChannel);
                                        }
                                        NotificationCompat.Builder notificationbuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(BackgroundService.this, "NOTIFICATION_CHANNEL_ID")
                                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                .setContentTitle(pieces[1]+" Lecture is Starting")
                                                .setContentText(pieces[3] + " " + pieces[2] + " " + pieces[7]);
                                        notificationManager.notify(Integer.parseInt(pieces[4].substring(0,2)+pieces[4].substring(3,5)), notificationbuilder.build());
                                        if (wifiManager.isWifiEnabled()) {
                                            wifiManager.setWifiEnabled(false);
                                        }
                                        wifiConfiguration = new WifiConfiguration();
                                        Method[] mMethods = wifiManager.getClass().getDeclaredMethods();
                                        for (Method mMethod : mMethods) {
                                            if (mMethod.getName().equals("setWifiApEnabled")) {
                                                wifiConfiguration.SSID = userPieces[1];
                                                wifiConfiguration.BSSID = userPieces[4];//todo
                                                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                                                wifiManager.addNetwork(wifiConfiguration);
                                                wifiManager.saveConfiguration();
                                                try {
                                                    Object o = mMethod.invoke(wifiManager, wifiConfiguration, true);
                                                    if (o == null) {
                                                        Toast.makeText(getApplicationContext(), "Returned NULL", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    }
                                                } catch (Exception e) {
                                                    Toast.makeText(getApplicationContext(), "1/" + e.getMessage() + "/" + e.getStackTrace().toString() + "/" + e.getCause(), Toast.LENGTH_SHORT).show();
                                                    break;
                                                }
                                                try {
                                                    Object o = mMethod.invoke(wifiManager, null, true);
                                                    if (o == null) {
                                                        Toast.makeText(getApplicationContext(), "Returned NULL", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    }
                                                } catch (Exception e) {
                                                    Toast.makeText(getApplicationContext(), "2/" + e.getMessage() + "/" + e.getStackTrace().toString() + "/" + e.getCause(), Toast.LENGTH_SHORT).show();
                                                    break;
                                                }
                                                break;
                                            }
                                        }
                                        skiptimemin = 0;
                                        skiptimehour = 0;
                                        skiptimemin = skiptimemin + 10;
                                        while ((Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin) >= 60) {
                                            skiptimehour++;
                                            skiptimemin = skiptimemin - 60;
                                        }
                                        studentEnter = true;
                                    }
                                    else if (studentEnter && Integer.parseInt(time.substring(0, 2)) == (Integer.parseInt(pieces[4].substring(0, 2)) + skiptimehour) && (Integer.parseInt(time.substring(3, 5)) == (Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin)) && sec == 0) {
                                        wifiConfiguration = new WifiConfiguration();
                                        Method[] mMethods = wifiManager.getClass().getDeclaredMethods();
                                        for (Method mMethod : mMethods) {
                                            if (mMethod.getName().equals("setWifiApEnabled")) {
                                                wifiConfiguration.SSID = userPieces[0];
                                                wifiConfiguration.BSSID = userPieces[4];//todo
                                                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                                                wifiManager.addNetwork(wifiConfiguration);
                                                wifiManager.saveConfiguration();
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
                                        try {
                                            Thread.sleep(1000);
                                        } catch (Exception e) {
                                        }
                                        if (wifiManager.isWifiEnabled()) {
                                            wifiManager.setWifiEnabled(false);
                                        }
                                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            int importance = NotificationManager.IMPORTANCE_LOW;
                                            NotificationChannel notificationChannel = new NotificationChannel("NOTIFICATION_CHANNEL_ID", "NOTIFICATION_CHANNEL", importance);
                                            notificationChannel.enableLights(true);
                                            notificationManager.createNotificationChannel(notificationChannel);
                                        }
                                        NotificationCompat.Builder notificationbuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(BackgroundService.this, "NOTIFICATION_CHANNEL_ID")
                                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                .setContentTitle(pieces[1]+" Lecture has been Started")
                                                .setContentText(pieces[3] + " " + pieces[2] + " " + pieces[7]);
                                        notificationManager.notify(Integer.parseInt(pieces[4].substring(0,2)+pieces[4].substring(3,5)), notificationbuilder.build());
                                        if ((lecturetime-2) > 0) {
                                            lecturetime = lecturetime - 12;
                                            skiptimemin = skiptimemin + 10;
                                            while ((Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin) >= 60) {
                                                skiptimehour++;
                                                skiptimemin = skiptimemin - 60;
                                            }
                                        }
                                        studentEnter = false;
                                        ping = true;
                                    }
                                    else if (ping && Integer.parseInt(time.substring(0, 2)) == (Integer.parseInt(pieces[4].substring(0, 2)) + skiptimehour) && (Integer.parseInt(time.substring(3, 5)) == (Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin)) && sec == 0) {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (Exception e) {
                                        }
                                        if (!wifiManager.isWifiEnabled()) {
                                            wifiManager.setWifiEnabled(true);
                                        }
                                        Scan = true;
                                        ping = false;
                                        skiptimemin = skiptimemin + 1;
                                        while ((Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin) >= 60) {
                                            skiptimehour++;
                                            skiptimemin = skiptimemin - 60;
                                        }
                                    }
                                    else if (Scan && Integer.parseInt(time.substring(0, 2)) == (Integer.parseInt(pieces[4].substring(0, 2)) + skiptimehour) && (Integer.parseInt(time.substring(3, 5)) == (Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin)) && sec == 30) {
                                        ArrayList<String> temp = new ArrayList<>();
                                        wifiManager.startScan();
                                        List<ScanResult> scanResultList = wifiManager.getScanResults();
                                        for (ScanResult result : scanResultList) {
                                            for (String data : Student_Data) {
                                                String[] studentPieces = data.split("/");
                                                String branch;
                                                int k = 1;
                                                for (int i = 0; i < pieces[7].length(); i++) {
                                                    if (pieces[7].charAt(i) == ',' || pieces[7].charAt(i) == ']') {
                                                        branch = pieces[7].substring(k, i);
                                                        k = i + 2;
                                                        if (studentPieces[5].equals(branch)) {
                                                            if (result.BSSID.equals(studentPieces[3])) {//todo
                                                                if (!Absent.contains(studentPieces[0])) {
                                                                    temp.add(studentPieces[0]);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (Present != null) {
                                            for (String data : Present) {
                                                if (!temp.contains(data)) {
                                                    Absent.add(data);
                                                }
                                            }
                                        }
                                        Present = temp;
                                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            int importance = NotificationManager.IMPORTANCE_LOW;
                                            NotificationChannel notificationChannel = new NotificationChannel("NOTIFICATION_CHANNEL_ID", "NOTIFICATION_CHANNEL", importance);
                                            notificationChannel.enableLights(true);
                                            notificationManager.createNotificationChannel(notificationChannel);
                                        }
                                        NotificationCompat.Builder notificationbuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(BackgroundService.this, "NOTIFICATION_CHANNEL_ID")
                                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                .setContentTitle("Present")
                                                .setContentText(Present.toString());
                                        notificationManager.notify(20, notificationbuilder.build());
                                        notificationbuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(BackgroundService.this, "NOTIFICATION_CHANNEL_ID")
                                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                .setContentTitle("Absent")
                                                .setContentText(Absent.toString());
                                        notificationManager.notify(10, notificationbuilder.build());
                                        skiptimemin = skiptimemin + 1;
                                        while ((Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin) >= 60) {
                                            skiptimehour++;
                                            skiptimemin = skiptimemin - 60;
                                        }
                                        Scan = false;
                                        pingoff = true;
                                    }
                                    else if (pingoff &&Integer.parseInt(time.substring(0, 2)) == (Integer.parseInt(pieces[4].substring(0, 2)) + skiptimehour) && (Integer.parseInt(time.substring(3, 5)) == (Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin)) && sec == 0) {
                                        ping = true;
                                        try {
                                            Thread.sleep(1000);
                                        } catch (Exception e) {
                                        }
                                        if (wifiManager.isWifiEnabled()) {
                                            wifiManager.setWifiEnabled(false);
                                        }
                                        if ((lecturetime-2) > 0) {
                                            lecturetime = lecturetime - 12;
                                            skiptimemin = skiptimemin + 10;
                                            while ((Integer.parseInt(pieces[4].substring(3, 5)) + skiptimemin) >= 60) {
                                                skiptimehour++;
                                                skiptimemin = skiptimemin - 60;
                                            }
                                        }
                                    }
                                    endtimemin = 0;
                                    endtimehour = 0;
                                    while ((Integer.parseInt(pieces[5].substring(3, 5)) - 5) < 0) {
                                        endtimehour++;
                                        endtimemin = 60 - 5 - Integer.parseInt(pieces[5].substring(3, 5));
                                    }
                                    if ((Integer.parseInt(pieces[5].substring(3, 5)) - 5) >= 0) {
                                        endtimemin = Integer.parseInt(pieces[5].substring(3, 5));
                                    }
                                    if (Integer.parseInt(time.substring(0, 2)) == (Integer.parseInt(pieces[5].substring(0, 2)) - endtimehour) && Integer.parseInt(time.substring(3, 5)) == endtimemin && sec == 0) {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (Exception e) {
                                        }
                                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            int importance = NotificationManager.IMPORTANCE_LOW;
                                            NotificationChannel notificationChannel = new NotificationChannel("NOTIFICATION_CHANNEL_ID", "NOTIFICATION_CHANNEL", importance);
                                            notificationChannel.enableLights(true);
                                            notificationManager.createNotificationChannel(notificationChannel);
                                        }
                                        NotificationCompat.Builder notificationbuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(BackgroundService.this, "NOTIFICATION_CHANNEL_ID")
                                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                .setContentTitle(pieces[1]+" Lecture Attendance Recorded")
                                                .setContentText(pieces[3] + " " + pieces[2] + " " + pieces[7]);
                                        notificationManager.notify(Integer.parseInt(pieces[4].substring(0,2)+pieces[4].substring(3,5)), notificationbuilder.build());
                                        if (!db.AttendanceRegister.contains(lecture + "#" + Present)) {
                                            db.AttendanceRegister.add(lecture + "#" + Present);
                                        }
                                        if (!db.temp.contains(lecture + "#" + Present)) {
                                            db.temp.add(lecture + "#" + Present);
                                        }
                                        if (!AttendanceRegister.contains(lecture + "#" + Present)) {
                                            AttendanceRegister.add(lecture + "#" + Present);
                                        }
                                        Present.clear();
                                        Absent.clear();
                                        skiptimemin = 0;
                                        skiptimehour = 0;
                                        Scan = false;
                                        ping = false;
                                        pingoff = false;
                                        studentEnter = false;
                                    }
                                }
                            }
                        }
                    }
                    wakeLock.release();
                }
            }
        }).start();
        return START_STICKY;
    }
}
