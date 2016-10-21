package com.laxmisoft.datadudu.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laxmisoft.datadudu.ApplicationData;
import com.laxmisoft.datadudu.ConnectionDetector;
import com.laxmisoft.datadudu.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by abc on 30-04-2016.
 */
public class SignUpActivity extends Activity {


    TextView txtLogin, txtAlready;
    Button btnSignUp;
    //String[] TimeZone = {"TimeZone 1 ", "TimeZone 2", "TimeZone 3", "TimeZone"};
    Typeface bold, regular, black;
    EditText edtUsername, edtEmail, edtPassword, edtConfirmPassword;
    Spinner spTimezone;
    ArrayAdapter<String> idAdapter;
    String getTimeZone = "";

    ProgressDialog mProgressDialog;
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        cd = new ConnectionDetector(getApplicationContext());
        mProgressDialog = new ProgressDialog(SignUpActivity.this);

        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
        spTimezone = (Spinner) findViewById(R.id.spTimeZoon);
        txtAlready = (TextView) findViewById(R.id.txtAlready);
        txtLogin = (TextView) findViewById(R.id.txtLogin);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        bold = Typeface.createFromAsset(SignUpActivity.this.getAssets(), "Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(SignUpActivity.this.getAssets(), "Roboto-Regular.ttf");
        black = Typeface.createFromAsset(SignUpActivity.this.getAssets(), "Roboto-Black.ttf");

        edtUsername.setTypeface(regular);
        edtEmail.setTypeface(regular);
        edtPassword.setTypeface(regular);
        edtConfirmPassword.setTypeface(regular);
        txtAlready.setTypeface(regular);
        btnSignUp.setTypeface(regular);
        txtLogin.setTypeface(bold);
        String tz = String.valueOf(R.string.str_TimeZone);
        String[] idArray = TimeZone.getAvailableIDs();
        idAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, R.id.textview, idArray);
        spTimezone.setPrompt(tz);
        spTimezone.setAdapter(idAdapter);

        spTimezone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedId = (String) (parent.getItemAtPosition(position));
                TimeZone timezone = TimeZone.getTimeZone(selectedId);
                getTimeZone = timezone.getID();
                Log.e("Get Time Zone", " : " + timezone.getID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet()) {
                    if (edtUsername.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please Enter Username", Toast.LENGTH_SHORT).show();
                    } else if (edtEmail.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please Enter Email Id", Toast.LENGTH_SHORT).show();
                    } else if (edtPassword.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                    } else if (edtConfirmPassword.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please Enter Confirm Password", Toast.LENGTH_SHORT).show();
                    } else if (!edtConfirmPassword.getText().toString().equals(edtPassword.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Password Does Not Match", Toast.LENGTH_SHORT).show();
                    } else if (!isValidEmail(edtEmail.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Enter Email Id Not Valid ", Toast.LENGTH_SHORT).show();
                    } else {
                        getSignUpDetail();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean isValidEmail(String email) {
        boolean isValidEmail = false;
        System.out.println(email);
        String emailExpression = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(emailExpression,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValidEmail = true;
        }
        return isValidEmail;
    }

    public void getSignUpDetail() {

        final String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "create";
        Log.e("url", url + "");
        mProgressDialog.setMessage("Please Wait");
        mProgressDialog.show();

        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("username", edtUsername.getText().toString());
        jsonParams.put("password", edtPassword.getText().toString());
        jsonParams.put("email", edtEmail.getText().toString());
        jsonParams.put("timezone", getTimeZone.toString());

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Sign Up", " " + response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject jsob = new JSONObject(response.toString());
                            String msg = jsob.getString("result");
                            Log.e("msg", "" + msg);
                            if (msg.equalsIgnoreCase("Success")) {
                                JSONObject object = jsob.getJSONObject("account");

                                String user_id = object.getString("user_id");
                                String username = object.getString("username");
                                String email = object.getString("email");
                                String timezone = object.getString("timezone");
                                String account_key = object.getString("account_key");
                                String email_status = object.getString("email_status");
                                String updated_at = object.getString("updated_at");
                                String bio = object.getString("bio");
                                String website = object.getString("website");
                                String status = object.getString("status");
                                String usage = object.getString("usage");
                                String created_at = object.getString("created_at");
                                String public_flag = object.getString("public_flag");

                                SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("USER_ID", "1");
                                editor.putString("ACCOUNT_KEY", object.getString("account_key"));
                                editor.putString("EMAIL_ID", object.getString("email"));
                                editor.putString("TIMEZONE", object.getString("timezone"));
                                editor.putString("USERNAME", object.getString("username"));
                                editor.putString("USER_LOGIN_ID", object.getString("user_id"));
                                editor.commit();

                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();

                            } else if (msg.equalsIgnoreCase("error")) {
                                if (jsob.getString("desp").equalsIgnoreCase("user name already exist")) {
                                    Toast.makeText(SignUpActivity.this, "User Name Already Exist", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressDialog.dismiss();
                        VolleyLog.e("Sign Up", "Error: " + error.getMessage());
                        Toast.makeText(SignUpActivity.this, "User Name Already Exist", Toast.LENGTH_LONG).show();
                        error.getCause();
                        error.printStackTrace();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(postRequest, tag_json_obj);
    }
}
