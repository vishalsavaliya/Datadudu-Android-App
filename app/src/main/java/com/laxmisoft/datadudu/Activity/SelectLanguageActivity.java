package com.laxmisoft.datadudu.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.laxmisoft.datadudu.R;

import java.util.Locale;

/**
 * Created by abc on 04-05-2016.
 */
public class SelectLanguageActivity extends Activity {

    ImageView imgBack;
    TextView txtselectlanguage;
    Button btnEnglish;
    Button btnChinese;
    Button btnFrench;
    Button btnSpanish;
    Button btnItalian;

    SharedPreferences preferences_lang;
    SharedPreferences.Editor editor_lang;

    Typeface bold, regular, black;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        txtselectlanguage = (TextView) findViewById(R.id.txtselectlanguage);
        btnEnglish = (Button) findViewById(R.id.btnEnglish);
        btnChinese = (Button) findViewById(R.id.btnChinese);
        btnFrench = (Button) findViewById(R.id.btnFrench);
        btnSpanish = (Button) findViewById(R.id.btnSpanish);
        btnItalian = (Button) findViewById(R.id.btnItalian);

        bold = Typeface.createFromAsset(SelectLanguageActivity.this.getAssets(), "Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(SelectLanguageActivity.this.getAssets(), "Roboto-Regular.ttf");
        black = Typeface.createFromAsset(SelectLanguageActivity.this.getAssets(), "Roboto-Black.ttf");

        txtselectlanguage.setTypeface(bold);
        btnEnglish.setTypeface(regular);
        btnChinese.setTypeface(regular);
        btnFrench.setTypeface(regular);
        btnSpanish.setTypeface(regular);
        btnItalian.setTypeface(regular);

        preferences_lang = getApplicationContext().getSharedPreferences("LANG_DETAIL", 0);

        if (preferences_lang.getString("LANG", "English").equalsIgnoreCase("English")) {
            btnEnglish.setBackgroundResource(R.drawable.button_stroke_gl);
            btnChinese.setBackgroundResource(R.drawable.button_stroke_bl);
            btnFrench.setBackgroundResource(R.drawable.button_stroke_bl);
            btnSpanish.setBackgroundResource(R.drawable.button_stroke_bl);
            btnItalian.setBackgroundResource(R.drawable.button_stroke_bl);

        } else if (preferences_lang.getString("LANG", "Chinese").equalsIgnoreCase("Chinese")) {
            btnEnglish.setBackgroundResource(R.drawable.button_stroke_bl);
            btnChinese.setBackgroundResource(R.drawable.button_stroke_gl);
            btnFrench.setBackgroundResource(R.drawable.button_stroke_bl);
            btnSpanish.setBackgroundResource(R.drawable.button_stroke_bl);
            btnItalian.setBackgroundResource(R.drawable.button_stroke_bl);
        } else if (preferences_lang.getString("LANG", "French").equalsIgnoreCase("French")) {
            btnEnglish.setBackgroundResource(R.drawable.button_stroke_bl);
            btnChinese.setBackgroundResource(R.drawable.button_stroke_bl);
            btnFrench.setBackgroundResource(R.drawable.button_stroke_gl);
            btnSpanish.setBackgroundResource(R.drawable.button_stroke_bl);
            btnItalian.setBackgroundResource(R.drawable.button_stroke_bl);
        } else if (preferences_lang.getString("LANG", "Spanish").equalsIgnoreCase("Spanish")) {
            btnEnglish.setBackgroundResource(R.drawable.button_stroke_bl);
            btnChinese.setBackgroundResource(R.drawable.button_stroke_bl);
            btnFrench.setBackgroundResource(R.drawable.button_stroke_bl);
            btnSpanish.setBackgroundResource(R.drawable.button_stroke_gl);
            btnItalian.setBackgroundResource(R.drawable.button_stroke_bl);
        } else if (preferences_lang.getString("LANG", "Italian").equalsIgnoreCase("Italian")) {
            btnEnglish.setBackgroundResource(R.drawable.button_stroke_bl);
            btnChinese.setBackgroundResource(R.drawable.button_stroke_bl);
            btnFrench.setBackgroundResource(R.drawable.button_stroke_bl);
            btnSpanish.setBackgroundResource(R.drawable.button_stroke_bl);
            btnItalian.setBackgroundResource(R.drawable.button_stroke_gl);
        } else {
            btnEnglish.setBackgroundResource(R.drawable.button_stroke_gl);
            btnChinese.setBackgroundResource(R.drawable.button_stroke_bl);
            btnFrench.setBackgroundResource(R.drawable.button_stroke_bl);
            btnSpanish.setBackgroundResource(R.drawable.button_stroke_bl);
            btnItalian.setBackgroundResource(R.drawable.button_stroke_bl);
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectLanguageActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectLanguageActivity.this);
                alertDialogBuilder.setMessage("Are You Sure You Want Select English Language ?");
                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        editor_lang = preferences_lang.edit();
                        editor_lang.putString("LANG", "English");
                        editor_lang.commit();
                        Locale locale = new Locale("en_US");
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getApplicationContext().getResources().updateConfiguration(config, null);
                        Intent intent = new Intent(SelectLanguageActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        btnChinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectLanguageActivity.this);
                alertDialogBuilder.setMessage("Are You Sure You Want Select Chinese Language ?");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        editor_lang = preferences_lang.edit();
                        editor_lang.putString("LANG", "Chinese");
                        editor_lang.commit();
                        Locale locale = new Locale("zh");
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getApplicationContext().getResources().updateConfiguration(config, null);
                        Intent intent = new Intent(SelectLanguageActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        btnFrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectLanguageActivity.this);
                alertDialogBuilder.setMessage("Are You Sure You Want Select French Language ?");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        editor_lang = preferences_lang.edit();
                        editor_lang.putString("LANG", "French");
                        editor_lang.commit();
                        Locale locale = new Locale("fr");
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getApplicationContext().getResources().updateConfiguration(config, null);
                        Intent intent = new Intent(SelectLanguageActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        btnItalian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectLanguageActivity.this);
                alertDialogBuilder.setMessage("Are You Sure You Want Select Italian Language ?");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        editor_lang = preferences_lang.edit();
                        editor_lang.putString("LANG", "Italian");
                        editor_lang.commit();
                        Locale locale = new Locale("it");
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getApplicationContext().getResources().updateConfiguration(config, null);
                        Intent intent = new Intent(SelectLanguageActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        btnSpanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectLanguageActivity.this);
                alertDialogBuilder.setMessage("Are You Sure You Want Select Spanish Language ?");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        editor_lang = preferences_lang.edit();
                        editor_lang.putString("LANG", "Spanish");
                        editor_lang.commit();
                        Locale locale = new Locale("es");
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getApplicationContext().getResources().updateConfiguration(config, null);
                        Intent intent = new Intent(SelectLanguageActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SelectLanguageActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
