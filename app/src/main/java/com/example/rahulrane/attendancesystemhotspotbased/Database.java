package com.example.rahulrane.attendancesystemhotspotbased;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.crypto.Mac;

public class Database {
    private static final Database ourInstance = new Database();
    protected static ArrayList<String> Student_Data = new ArrayList<>();
    protected static ArrayList<String> Teacher_Data = new ArrayList<>();
    protected static ArrayList<String> Lectures = new ArrayList<>();
    protected static ArrayList<String> AttendanceRegister = new ArrayList<>();
    protected static ArrayList<String> temp = new ArrayList<>();
    protected static String User = "";
    public static Database getInstance() {
        return ourInstance;
    }

    private Database() {
    }

    public void addStudentData(String name, String email, String password, String mac_addr, String year, String branch) {
        Student_Data.add(name+"/"+email+"/"+password+"/"+mac_addr+"/"+year+"/"+branch);
    }

    public void addTeacherData(String name, String email, String password, String mac_addr, String department) {
        Teacher_Data.add(name+"/"+email+"/"+password+"/"+mac_addr+"/"+department);
    }

    public void addLectures(String teacherName, String lectrureName, String Classroom, String Date, String timeStart, String timeEnd, String year, ArrayList<String> lectureBranches) {
        ArrayList<String> temp = new ArrayList<>();
        Lectures.add(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches);
        for (String data : Lectures) {
            String[] pieces = data.split("#");
            temp.add(pieces[3] + "#" + pieces[4] + "#" + pieces[1] + "#" + pieces[0] + "#" + pieces[2] + "#" + pieces[5] + "#" + pieces[6] + "#" + pieces[7]);
        }
        for (String data : temp) {
            String[] pieces = data.split("#");
            Lectures.remove(pieces[3] + "#" + pieces[2] + "#" + pieces[4] + "#" + pieces[0] + "#" + pieces[1] + "#" + pieces[5] + "#" + pieces[6] + "#" + pieces[7]);
        }
        Collections.sort(temp);
        for (String data : temp) {
            String[] pieces = data.split("#");
            Lectures.add(pieces[3] + "#" + pieces[2] + "#" + pieces[4] + "#" + pieces[0] + "#" + pieces[1] + "#" + pieces[5] + "#" + pieces[6] + "#" + pieces[7]);
        }
    }

    public void getDataFire() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("attendance-system-hotspotbased");
        DatabaseReference studentData = databaseReference.child("Student Data");
        DatabaseReference teacherData = databaseReference.child("Teacher Data");
        DatabaseReference lectures = databaseReference.child("Lectures");
        DatabaseReference attendanceRegister = databaseReference.child("Attendance Register");
        studentData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Student_Data.clear();
                String data;
                int k=6;
                if (dataSnapshot.getValue() != null) {
                    for (int i = 6; i < dataSnapshot.getValue().toString().length(); i++) {
                        if (dataSnapshot.getValue().toString().charAt(i) == '=') {
                            k=i;
                        }
                    }
                    for (int i = k; i < dataSnapshot.getValue().toString().length(); i++) {
                        if (dataSnapshot.getValue().toString().charAt(i) == '{') {
                            k = i + 1;
                        }
                        if (dataSnapshot.getValue().toString().charAt(i) == '}') {
                            data = dataSnapshot.getValue().toString().substring(k, i);
                            Student_Data.add(data);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        teacherData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Teacher_Data.clear();
                String data;
                int k=6;
                if (dataSnapshot.getValue() != null) {
                    for (int i = 6; i < dataSnapshot.getValue().toString().length(); i++) {
                        if (dataSnapshot.getValue().toString().charAt(i) == '=') {
                            k=i;
                        }
                    }
                    for (int i = k; i < dataSnapshot.getValue().toString().length(); i++) {
                        if (dataSnapshot.getValue().toString().charAt(i) == '{') {
                            k = i + 1;
                        }
                        if (dataSnapshot.getValue().toString().charAt(i) == '}') {
                            data = dataSnapshot.getValue().toString().substring(k, i);
                            Teacher_Data.add(data);
                    }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        lectures.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Lectures.clear();
                String data;
                int k=6;
                if (dataSnapshot.getValue() != null) {
                    for (int i = 6; i < dataSnapshot.getValue().toString().length(); i++) {
                        if (dataSnapshot.getValue().toString().charAt(i) == '=') {
                            k=i;
                        }
                    }
                    for (int i = k; i < dataSnapshot.getValue().toString().length(); i++) {
                        if (dataSnapshot.getValue().toString().charAt(i) == '{') {
                            k = i + 1;
                        }
                        if (dataSnapshot.getValue().toString().charAt(i) == '}') {
                            data = dataSnapshot.getValue().toString().substring(k, i);
                            Lectures.add(data);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        attendanceRegister.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AttendanceRegister.clear();
                String data;
                int k=6;
                if (dataSnapshot.getValue() != null) {
                    for (int i = 6; i < dataSnapshot.getValue().toString().length(); i++) {
                        if (dataSnapshot.getValue().toString().charAt(i) == '=') {
                            k=i;
                        }
                    }
                    for (int i = k; i < dataSnapshot.getValue().toString().length(); i++) {
                        if (dataSnapshot.getValue().toString().charAt(i) == '{') {
                            k = i + 1;
                        }
                        if (dataSnapshot.getValue().toString().charAt(i) == '}') {
                            data = dataSnapshot.getValue().toString().substring(k, i);
                            AttendanceRegister.add(data);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void putDataFireLecture(String teacherName, String lectrureName, String Classroom, String Date, String timeStart, String timeEnd, String year, ArrayList<String> lectureBranches) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("attendance-system-hotspotbased");
        databaseReference.child("Lectures").child(teacherName+","+lectrureName+","+Classroom+","+Date.substring(0,2)+Date.substring(3,5)+Date.substring(6,10)+","+timeStart.substring(0,2)+timeStart.substring(3,5)+","+timeEnd.substring(0,2)+timeEnd.substring(3,5)).setValue(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches);
        //databaseReference.child("Lectures").child(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches).child("Teacher Name").setValue(lectrureName);
        //databaseReference.child("Lectures").child(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches).child("Teacher Name").setValue(teacherName);
        //databaseReference.child("Lectures").child(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches).child("Classroom").setValue(Classroom);
        //databaseReference.child("Lectures").child(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches).child("Date").setValue(Date);
        //databaseReference.child("Lectures").child(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches).child("Start Time").setValue(timeStart);
        //databaseReference.child("Lectures").child(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches).child("End Time").setValue(timeEnd);
        //databaseReference.child("Lectures").child(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches).child("Year").setValue(year);
        //databaseReference.child("Lectures").child(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches).child("Branches").setValue(lectureBranches);
    }
    public void putDataFireAttendanceRegister(String teacherName, String lectrureName, String Classroom, String Date, String timeStart, String timeEnd, String year, String lectureBranches, String Present) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("attendance-system-hotspotbased");
        databaseReference.child("Attendance Register").child(teacherName+","+lectrureName+","+Classroom+","+Date.substring(0,2)+Date.substring(3,5)+Date.substring(6,10)+","+timeStart.substring(0,2)+timeStart.substring(3,5)+","+timeEnd.substring(0,2)+timeEnd.substring(3,5)).setValue(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches+"#"+Present);
        //databaseReference.child("Attendance Register").child(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches).child("Teacher Name").setValue(lectrureName);
        //databaseReference.child("Attendance Register").child(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches).child("Teacher Name").setValue(teacherName);
        //databaseReference.child("Attendance Register").child(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches).child("Classroom").setValue(Classroom);
        //databaseReference.child("Attendance Register").child(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches).child("Date").setValue(Date);
        //databaseReference.child("Attendance Register").child(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches).child("Start Time").setValue(timeStart);
        //databaseReference.child("Attendance Register").child(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches).child("End Time").setValue(timeEnd);
        //databaseReference.child("Attendance Register").child(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches).child("Year").setValue(year);
        //databaseReference.child("Attendance Register").child(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches).child("Branches").setValue(lectureBranches);
        //databaseReference.child("Attendance Register").child(teacherName+"#"+lectrureName+"#"+Classroom+"#"+Date+"#"+timeStart+"#"+timeEnd+"#"+year+"#"+lectureBranches).child("Present Students").setValue(Present);
    }

    public void putDataFireStudentData(String Name, String Email, String Password, String Mac_Address, String Year, String Branch) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("attendance-system-hotspotbased");
        databaseReference.child("Student Data").child(Name+","+Mac_Address+","+Branch).setValue(Name+"/"+Email+"/"+Password+"/"+Mac_Address+"/"+Year+"/"+Branch);
        //databaseReference.child("Student Data").child(Name).child("Email").setValue(Email);
        //databaseReference.child("Student Data").child(Name).child("Password").setValue(Password);
        //databaseReference.child("Student Data").child(Name).child("Mac_Address").setValue(Mac_Address);
        //databaseReference.child("Student Data").child(Name).child("Year").setValue(Year);
        //databaseReference.child("Student Data").child(Name).child("Branch").setValue(Branch);
    }

    public void putDataFireTeacherData(String Name, String Email, String Password, String Mac_Address, String Department) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("attendance-system-hotspotbased");
        databaseReference.child("Teacher Data").child(Name+","+Mac_Address+","+Department).setValue(Name+"/"+Email+"/"+Password+"/"+Mac_Address+"/"+Department);
        //databaseReference.child("Teacher Data").child(Name).child("Email").setValue(Email);
        //databaseReference.child("Teacher Data").child(Name).child("Password").setValue(Password);
        //databaseReference.child("Teacher Data").child(Name).child("Mac_Address").setValue(Mac_Address);
        //databaseReference.child("Teacher Data").child(Name).child("Department").setValue(Department);
    }

    public void removeDataFireStudentData(String Name, String Email, String Password, String Mac_Address, String Year, String Branch) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("attendance-system-hotspotbased");
        databaseReference.child("Student Data").child(Name+","+Mac_Address+","+Branch).removeValue();
    }

    public void removeDataFireTeacherData(String Name, String Email, String Password, String Mac_Address, String Department) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("attendance-system-hotspotbased");
        databaseReference.child("Teacher Data").child(Name+","+Mac_Address+","+Department).removeValue();
    }

    public void removeLecture(String data) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("attendance-system-hotspotbased");
        String[] pieces = data.split("#");
        databaseReference.child("Lectures").child(pieces[0]+","+pieces[1]+","+pieces[2]+","+pieces[3].substring(0,2)+pieces[3].substring(3,5)+pieces[3].substring(6,10)+","+pieces[4].substring(0,2)+pieces[4].substring(3,5)+","+pieces[5].substring(0,2)+pieces[5].substring(3,5)).removeValue();
    }

    public void removeAttendance(String data) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("attendance-system-hotspotbased");
        String[] pieces = data.split("#");
        databaseReference.child("Attendance Register").child(pieces[0]+","+pieces[1]+","+pieces[2]+","+pieces[3].substring(0,2)+pieces[3].substring(3,5)+pieces[3].substring(6,10)+","+pieces[4].substring(0,2)+pieces[4].substring(3,5)+","+pieces[5].substring(0,2)+pieces[5].substring(3,5)).removeValue();
    }
}
