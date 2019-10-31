package com.example.rahulrane.attendancesystemhotspotbased;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.content.Context;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.content.res.Resources.Theme;

import android.widget.TextView;
import android.widget.Toast;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import javax.crypto.Mac;

public class SignupActivity extends AppCompatActivity {


    protected static SignupActivity signupActivity;
    private UserSignupTask mAuthTask = null;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mSignupFormView;
    private AutoCompleteTextView mNameView;
    private EditText mRepasswordView;
    private AutoCompleteTextView MacAddView;
    private static int sectionno;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    private String name;
    private String email;
    private String password;
    private String repassword;
    private String mac_addr;
    private String year;
    private String branch;
    private String department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupActivity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new MyAdapter(
                toolbar.getContext(),
                new String[]{
                        "Student Sign Up",
                        "Teacher Sign Up",
                }));

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When the given dropdown item is selected, show its contents in the
                // container view.
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                        .commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void attemptSignup() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mRepasswordView.setError(null);

        // Store values at the time of the signup attempt.
        name = mNameView.getText().toString();
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();
        repassword = mRepasswordView.getText().toString();
        mac_addr = MacAddView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("This field is required");
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(repassword)) {
            mRepasswordView.setError("This field is required");
            focusView = mRepasswordView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(SignupActivity.signupActivity.getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(repassword) && !isPasswordValid(repassword)) {
            mRepasswordView.setError(SignupActivity.signupActivity.getString(R.string.error_invalid_password));
            focusView = mRepasswordView;
            cancel = true;
        }
        else if (!password.equals(repassword)) {
            mPasswordView.setError("Both Passwords don't Match");
            focusView = mPasswordView;
            mRepasswordView.setError("Both Passwords don't Match");
            focusView = mRepasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("This field is required");
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(SignupActivity.signupActivity.getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(name)) {
            mNameView.setError("This field is required");
            focusView = mNameView;
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

            mAuthTask = new UserSignupTask();
            mAuthTask.execute((Void) null);
        }
    }

    private void saveData() {
        if(sectionno == 1) {
            Database db = Database.getInstance();
            db.putDataFireStudentData(name, email, password, mac_addr, year, branch);
            db.addStudentData(name, email, password, mac_addr, year, branch);
        }
        else if(sectionno == 2) {
            Database db = Database.getInstance();
            db.putDataFireTeacherData(name, email, password, mac_addr, department);
            db.addTeacherData(name, email, password, mac_addr, department);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = SignupActivity.signupActivity.getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSignupFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignupFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignupFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mSignupFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserSignupTask extends AsyncTask<Void, Void, Boolean> {

        private String check="";

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
            for (String data : db.Student_Data) {
                String[] pieces = data.split("/");
                if(pieces[0].equals(name)) {
                    check = "Name";
                }
                else if(pieces[1].equals(email)) {
                    check = "Email";
                }
                else if(pieces[3].equals(mac_addr)) {
                    check = "MacAdd";
                }
            }
            for (String data : db.Teacher_Data) {
                String[] pieces = data.split("/");
                if(pieces[0].equals(name)) {
                    check = "Name";
                    return false;
                }
                else if(pieces[1].equals(email)) {
                    check = "Email";
                    return false;
                }
                else if(pieces[3].equals(mac_addr)) {
                    check = "MacAdd";
                    return false;
                }
            }
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                saveData();
                Toast.makeText(SignupActivity.signupActivity, "Sign Up Successful", Toast.LENGTH_LONG).show();
                SignupActivity.signupActivity.finish();
            }
            else if(check.equals("Name")) {
                mNameView.setError("Name Exists");
                mNameView.requestFocus();
            }
            else if(check.equals("Email")) {
                mEmailView.setError("Email Exists");
                mEmailView.requestFocus();
            }
            else if(check.equals("MacAdd")) {
                MacAddView.setError("WiFi Mac Address Exists");
                MacAddView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final SignupActivity sup = new SignupActivity();
            if(getArguments() != null) {
                sectionno = getArguments().getInt(ARG_SECTION_NUMBER);
            }
            View rootView = inflater.inflate(R.layout.fragment_student_signup, container, false);
            if(sectionno == 1) {
                rootView = inflater.inflate(R.layout.fragment_student_signup, container, false);

                sup.mSignupFormView = rootView.findViewById(R.id.signup_form);
                sup.mProgressView = rootView.findViewById(R.id.signup_progress);


                sup.MacAddView = rootView.findViewById(R.id.MacAdd);
                String mac_addr = getMacAdd();
                sup.MacAddView.setText(mac_addr);

                sup.mNameView =  rootView.findViewById(R.id.name);

                sup.mEmailView = rootView.findViewById(R.id.email);

                sup.mPasswordView = rootView.findViewById(R.id.password);

                sup.mRepasswordView = rootView.findViewById(R.id.repassword);


                final View rv = rootView;
                Button mEmailSignupButton = rootView.findViewById(R.id.email_sign_up_button);
                mEmailSignupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean cancel = false;
                        sup.radioGroup = rv.findViewById(R.id.year_group);
                        int radioId = sup.radioGroup.getCheckedRadioButtonId();
                        if(rv.findViewById(radioId) != null) {
                            sup.radioButton = rv.findViewById(radioId);
                            sup.year = sup.radioButton.getText().toString();
                        }
                        else {
                            TextView yr = rv.findViewById(R.id.yearView);
                            yr.setError("Select Year");
                            yr.requestFocus();
                            cancel = true;
                        }
                        sup.radioGroup = rv.findViewById(R.id.branch_group);
                        radioId = sup.radioGroup.getCheckedRadioButtonId();
                        if(rv.findViewById(radioId) != null) {
                            sup.radioButton = rv.findViewById(radioId);
                            sup.branch = sup.radioButton.getText().toString();
                        }
                        else {
                            TextView brn = rv.findViewById(R.id.branchView);
                            brn.setError("Select Branch");
                            brn.requestFocus();
                            cancel = true;
                        }
                        if(!cancel) {
                            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                sup.attemptSignup();
                            }
                            else {
                                Toast.makeText(SignupActivity.signupActivity, "No Internet Connection", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                rootView = rv;

                return rootView;
            }
            else if(sectionno == 2) {
                rootView = inflater.inflate(R.layout.fragment_teacher_signup, container, false);

                sup.mSignupFormView = rootView.findViewById(R.id.signup_form);
                sup.mProgressView = rootView.findViewById(R.id.signup_progress);

                sup.MacAddView = rootView.findViewById(R.id.MacAdd);
                String mac_addr = getMacAdd();
                sup.MacAddView.setText(mac_addr);

                sup.mNameView = rootView.findViewById(R.id.name);

                sup.mEmailView = rootView.findViewById(R.id.email);

                sup.mPasswordView = rootView.findViewById(R.id.password);

                sup.mRepasswordView = rootView.findViewById(R.id.repassword);

                final View rv = rootView;
                Button mEmailSignupButton = rootView.findViewById(R.id.email_sign_up_button);
                mEmailSignupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean cancel = false;
                        sup.radioGroup = rv.findViewById(R.id.department_group);
                        int radioId = sup.radioGroup.getCheckedRadioButtonId();
                        if(rv.findViewById(radioId) != null) {
                            sup.radioButton = rv.findViewById(radioId);
                            sup.department = sup.radioButton.getText().toString();
                        }
                        else {
                            TextView yr = rv.findViewById(R.id.departmentView);
                            yr.setError("Select Department");
                            yr.requestFocus();
                            cancel = true;
                        }
                        if(!cancel) {
                            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                sup.attemptSignup();
                            }
                            else {
                                Toast.makeText(SignupActivity.signupActivity, "No Internet Connection", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                rootView = rv;
                return rootView;
            }
            return rootView;
        }
    }
}
