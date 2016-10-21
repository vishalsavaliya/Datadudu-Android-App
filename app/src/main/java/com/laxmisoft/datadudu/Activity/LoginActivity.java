package com.laxmisoft.datadudu.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.laxmisoft.datadudu.ApplicationData;
import com.laxmisoft.datadudu.ConnectionDetector;
import com.laxmisoft.datadudu.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button btnSignUp, btnLogin;
    Typeface bold, regular, black;
    EditText edtUsername, edtPassword;
    TextView txtForgotPassword;

    public static int screenWidth, screenHeight;
    ConnectionDetector cd;
    ProgressDialog mProgressDialog;
    String Check_Username = "";
    SharedPreferences preferences_login_check, preferences_login;
    SharedPreferences.Editor editor_login_check, editor_login;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        /*DateFormat df = new SimpleDateFormat("yyyyMMdd  HH:mm");
        try {
            Date date = new SimpleDateFormat("yyyymmddhhmmss", Locale.ENGLISH).parse("20130526160000");
            Log.e("Gate String  time", " :1 " + date);
            String sdt = df.format(date);
            Log.e("Gate String  time", " : " + sdt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String source = "2016-05-06T10:22:30Z";
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date formatted = null;
        try {
            formatted = formatter.parse(source);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

      /*  String st = getDate(Long.parseLong("20130526160000"));
        Log.e("Gate String  time", "1 : " + st);*/


        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);

        cd = new ConnectionDetector(getApplicationContext());
        mProgressDialog = new ProgressDialog(LoginActivity.this);
        preferences_login_check = getSharedPreferences("CHECK_LOGIN", 0);
        preferences_login = getSharedPreferences("LOGIN", 0);

        bold = Typeface.createFromAsset(LoginActivity.this.getAssets(), "Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(LoginActivity.this.getAssets(), "Roboto-Regular.ttf");
        black = Typeface.createFromAsset(LoginActivity.this.getAssets(), "Roboto-Black.ttf");

        edtUsername.setTypeface(regular);
        edtPassword.setTypeface(regular);
        txtForgotPassword.setTypeface(regular);
        btnLogin.setTypeface(regular);
        btnSignUp.setTypeface(regular);

        try {
            db = openOrCreateDatabase("DuDuDB", Context.MODE_PRIVATE, null);
            db.execSQL("DELETE FROM dudu");
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet()) {
                    if (edtUsername.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please Enter Username", Toast.LENGTH_SHORT).show();
                    } else if (edtPassword.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                    } else {
                        getLoginDetail();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getLoginDetail() {

        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "login";
        Log.e("url", url + "");
        mProgressDialog.setMessage("Please Wait");
        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Login ", " " + response.toString());
                        //Toast.makeText(getApplicationContext(), " Login Response : " + response.toString(), Toast.LENGTH_LONG).show();
                        try {
                            mProgressDialog.dismiss();
                            JSONObject jsob = new JSONObject(response);
                            String msg = jsob.getString("result");
                            Log.e("result", "" + msg);
                            if (msg.equalsIgnoreCase("Success")) {
                                String token_id = jsob.getString("token_id");
                                String expire_time = jsob.getString("expire_time");
                                JSONObject object = jsob.getJSONObject("account");
                                try {
                                    Check_Username = preferences_login.getString("USERNAME", "");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (Check_Username.equalsIgnoreCase(object.getString("username"))) {

                                    editor_login_check = preferences_login_check.edit();
                                    editor_login_check.putString("USER_ID", "1");
                                    editor_login_check.commit();

                                    editor_login = preferences_login.edit().clear();
                                    editor_login.apply();
                                    editor_login_check.commit();

                                    editor_login = preferences_login.edit();
                                    editor_login.putString("USER_ID", "1");
                                    editor_login.putString("USERNAME", object.getString("username"));
                                    editor_login.putString("EMAIL_ID", object.getString("email"));
                                    editor_login.putString("TIMEZONE", object.getString("timezone"));
                                    editor_login.putString("ACCOUNT_KEY", object.getString("account_key"));
                                    editor_login.putString("TOKEN_ID", jsob.getString("token_id"));
                                    editor_login.commit();
                                    Log.e("Get Token Id", " : " + jsob.getString("token_id"));
                                } else {
                                    editor_login_check = preferences_login_check.edit();
                                    editor_login_check.putString("USER_ID", "1");
                                    editor_login_check.commit();

                                    editor_login = preferences_login.edit().clear();
                                    editor_login.apply();
                                    editor_login_check.commit();

                                    editor_login = preferences_login.edit();
                                    editor_login.putString("USER_ID", "1");
                                    editor_login.putString("USERNAME", object.getString("username"));
                                    editor_login.putString("EMAIL_ID", object.getString("email"));
                                    editor_login.putString("TIMEZONE", object.getString("timezone"));
                                    editor_login.putString("ACCOUNT_KEY", object.getString("account_key"));
                                    editor_login.putString("TOKEN_ID", jsob.getString("token_id"));
                                    editor_login.commit();
                                }
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this, "Wrong Username or Password", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("Login ", "Error: " + error.getMessage());
                Toast.makeText(LoginActivity.this, "Wrong Username or Password", Toast.LENGTH_LONG).show();
                error.getCause();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", edtUsername.getText().toString());
                params.put("password", edtPassword.getText().toString());
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public void onBackPressed() {
        //System.exit(0);
    }
}
