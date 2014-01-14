package com.zlab.btcmonitor;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class bm_Preferences extends PreferenceActivity {

    String PIN1="";
    String PIN2="";
    boolean PIN_SHOW;
    CheckBoxPreference prefs_SEC_PIN;


    Context mContext;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            if(bm_Main.prefs_black_theme){
                setTheme(android.R.style.Theme_Holo);
            }
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            bm_Main.SETTINGS_IS_OPENED=true;

            mContext=this;



            final CheckBoxPreference prefs_black_theme = (CheckBoxPreference) getPreferenceManager().findPreference("prefs_black_theme");
            final CheckBoxPreference prefs_black_charts = (CheckBoxPreference) getPreferenceManager().findPreference("prefs_black_charts");

            prefs_SEC_PIN = (CheckBoxPreference) getPreferenceManager().findPreference("prefs_SEC_PIN");

            prefs_black_theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().equals("true")) {
                        prefs_black_charts.setChecked(true);
                    } else {
                        prefs_black_charts.setChecked(false);
                    }
                    return true;
                }
            });

            prefs_SEC_PIN.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    //if(!prefs_SEC_PIN.isChecked()){
                    if((Boolean) newValue){
                        //if(bm_Main.PIN.equals("")){
                        Toast.makeText(mContext, bm_Main.bm_MainState.getResources().getString(R.string.pin_new), Toast.LENGTH_SHORT).show();
                        newPIN1();
                        //} else {}
                    } else {
                        Toast.makeText(mContext, bm_Main.bm_MainState.getResources().getString(R.string.pin_new_off), Toast.LENGTH_SHORT).show();
                        bm_Main.prefs.edit().putString("PIN","").commit();
                    }
                    return true;
                }
            });
        }

    private void newPIN1(){
        AlertDialog.Builder action_dialog = new AlertDialog.Builder(mContext);
        action_dialog.setTitle("PIN-CODE");
        LayoutInflater inflater = bm_Main.bm_MainState.getLayoutInflater();
        View layer = inflater.inflate(R.layout.pin,null);
        PIN1="";
        Button btn_PINOK = (Button) layer.findViewById(R.id.btnPINOK);
        Button btn_PINCancel = (Button) layer.findViewById(R.id.btnPINCancel);
        Button btn_PINUndo = (Button) layer.findViewById(R.id.btnPINUndo);
        final ImageButton btn_PIN = (ImageButton) layer.findViewById(R.id.imgBtnPIN);
        Button btn_PIN0 = (Button) layer.findViewById(R.id.btnPIN0);
        Button btn_PIN1 = (Button) layer.findViewById(R.id.btnPIN1);
        Button btn_PIN2 = (Button) layer.findViewById(R.id.btnPIN2);
        Button btn_PIN3 = (Button) layer.findViewById(R.id.btnPIN3);
        Button btn_PIN4 = (Button) layer.findViewById(R.id.btnPIN4);
        Button btn_PIN5 = (Button) layer.findViewById(R.id.btnPIN5);
        Button btn_PIN6 = (Button) layer.findViewById(R.id.btnPIN6);
        Button btn_PIN7 = (Button) layer.findViewById(R.id.btnPIN7);
        Button btn_PIN8 = (Button) layer.findViewById(R.id.btnPIN8);
        Button btn_PIN9 = (Button) layer.findViewById(R.id.btnPIN9);
        final EditText text_PIN = (EditText) layer.findViewById(R.id.textPIN);

        action_dialog.setCancelable(false);
        action_dialog.setView(layer);
            /*
            action_dialog.setNegativeButton(bm_Main.bm_MainState.getResources().getString(R.string.close),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });       */

        final AlertDialog AboutDialog = action_dialog.create();
        btn_PINOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!PIN1.equals("")){
                    Toast.makeText(bm_Main.bm_MainContext, bm_Main.bm_MainState.getResources().getString(R.string.pin_new_repeat), Toast.LENGTH_SHORT).show();
                    text_PIN.setText("");
                    newPIN2();
                    AboutDialog.dismiss();
                } else {
                    prefs_SEC_PIN.setChecked(false);
                    Toast.makeText(bm_Main.bm_MainContext, bm_Main.bm_MainState.getResources().getString(R.string.pin_new_notset), Toast.LENGTH_SHORT).show();
                    AboutDialog.dismiss();
                }
            }
        });
        btn_PINCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PIN1="";
                if(!bm_Main.PIN.equals("")){prefs_SEC_PIN.setChecked(true);}else{prefs_SEC_PIN.setChecked(false);};
                Toast.makeText(bm_Main.bm_MainContext, bm_Main.bm_MainState.getResources().getString(R.string.pin_new_notset), Toast.LENGTH_SHORT).show();
                AboutDialog.dismiss();
            }
        });
        btn_PINUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN1="";text_PIN.setText("");}
        });

        btn_PIN0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN1 = PIN1+"0"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"0");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN1 = PIN1+"1"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"1");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN1 = PIN1+"2"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"2");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN1 = PIN1+"3"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"3");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN1 = PIN1+"4"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"4");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN1 = PIN1+"5"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"5");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN1 = PIN1+"6"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"6");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN1 = PIN1+"7"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"7");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN1 = PIN1+"8"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"8");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN1 = PIN1+"9"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"9");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });

        btn_PIN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // show interest in events resulting from ACTION_DOWN
                if(event.getAction()==MotionEvent.ACTION_DOWN) return true;
                // don't handle event unless its ACTION_UP so "doSomething()" only runs once.
                if(event.getAction()!=MotionEvent.ACTION_UP) return false;
                if(PIN_SHOW){
                    PIN_SHOW=false;
                    btn_PIN.setPressed(false);
                    String sec="";
                    for(int i=0;i<PIN1.length();i++){
                        sec=sec+"*";
                    }
                    text_PIN.setText(sec);
                } else {
                    PIN_SHOW=true;
                    btn_PIN.setPressed(true);
                    text_PIN.setText(PIN1);
                }
                return true;
            }
        });
        if(PIN_SHOW){btn_PIN.setPressed(true);}

        AboutDialog.show();
    }
    private void newPIN2(){
        AlertDialog.Builder action_dialog = new AlertDialog.Builder(mContext);
        action_dialog.setTitle("PIN-CODE");
        LayoutInflater inflater = bm_Main.bm_MainState.getLayoutInflater();
        View layer = inflater.inflate(R.layout.pin,null);

        PIN2="";

        Button btn_PINOK = (Button) layer.findViewById(R.id.btnPINOK);
        Button btn_PINCancel = (Button) layer.findViewById(R.id.btnPINCancel);
        Button btn_PINUndo = (Button) layer.findViewById(R.id.btnPINUndo);
        final ImageButton btn_PIN = (ImageButton) layer.findViewById(R.id.imgBtnPIN);
        Button btn_PIN0 = (Button) layer.findViewById(R.id.btnPIN0);
        Button btn_PIN1 = (Button) layer.findViewById(R.id.btnPIN1);
        Button btn_PIN2 = (Button) layer.findViewById(R.id.btnPIN2);
        Button btn_PIN3 = (Button) layer.findViewById(R.id.btnPIN3);
        Button btn_PIN4 = (Button) layer.findViewById(R.id.btnPIN4);
        Button btn_PIN5 = (Button) layer.findViewById(R.id.btnPIN5);
        Button btn_PIN6 = (Button) layer.findViewById(R.id.btnPIN6);
        Button btn_PIN7 = (Button) layer.findViewById(R.id.btnPIN7);
        Button btn_PIN8 = (Button) layer.findViewById(R.id.btnPIN8);
        Button btn_PIN9 = (Button) layer.findViewById(R.id.btnPIN9);
        final EditText text_PIN = (EditText) layer.findViewById(R.id.textPIN);

        action_dialog.setCancelable(false);
        action_dialog.setView(layer);
            /*
            action_dialog.setNegativeButton(bm_Main.bm_MainState.getResources().getString(R.string.close),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });       */

        final AlertDialog AboutDialog = action_dialog.create();
        btn_PINOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PIN1.equals(PIN2)){
                    if(!PIN2.equals("")){
                        bm_Main.prefs.edit().putString("PIN",PIN2).commit();
                        prefs_SEC_PIN.setChecked(true);
                        Toast.makeText(bm_Main.bm_MainContext, bm_Main.bm_MainState.getResources().getString(R.string.pin_new_set), Toast.LENGTH_SHORT).show();
                    } else {
                        bm_Main.prefs.edit().putString("",PIN2).commit();
                        prefs_SEC_PIN.setChecked(false);
                        Toast.makeText(bm_Main.bm_MainContext, bm_Main.bm_MainState.getResources().getString(R.string.pin_new_off), Toast.LENGTH_SHORT).show();
                    }
                    AboutDialog.dismiss();
                } else {
                    Toast.makeText(bm_Main.bm_MainContext, bm_Main.bm_MainState.getResources().getString(R.string.pin_new_nottrue), Toast.LENGTH_SHORT).show();

                    //if(!PIN2.equals("")){
                      //  Toast.makeText(bm_Main.bm_MainContext, bm_Main.bm_MainState.getResources().getString(R.string.pin_new_nottrue), Toast.LENGTH_SHORT).show();
                    //} else {
                    /*
                        bm_Main.prefs.edit().putString("PIN",PIN2).commit();
                        prefs_SEC_PIN.setChecked(false);
                        Toast.makeText(bm_Main.bm_MainContext, bm_Main.bm_MainState.getResources().getString(R.string.pin_new_off), Toast.LENGTH_SHORT).show();
                        AboutDialog.dismiss();*/
                    //}
                }
            }
        });
        btn_PINCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PIN2="";
                prefs_SEC_PIN.setChecked(false);
                bm_Main.prefs.edit().putString("PIN",PIN2).commit();
                Toast.makeText(bm_Main.bm_MainContext, bm_Main.bm_MainState.getResources().getString(R.string.pin_new_off), Toast.LENGTH_SHORT).show();
                AboutDialog.dismiss();
            }
        });
        btn_PINUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN2="";text_PIN.setText("");}
        });

        btn_PIN0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN2 = PIN2+"0"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"0");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN2 = PIN2+"1"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"1");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN2 = PIN2+"2"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"2");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN2 = PIN2+"3"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"3");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN2 = PIN2+"4"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"4");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN2 = PIN2+"5"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"5");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN2 = PIN2+"6"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"6");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN2 = PIN2+"7"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"7");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN2 = PIN2+"8"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"8");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });
        btn_PIN9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {PIN2 = PIN2+"9"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"9");}else{text_PIN.setText(text_PIN.getText()+"*");}}
        });

        btn_PIN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // show interest in events resulting from ACTION_DOWN
                if(event.getAction()==MotionEvent.ACTION_DOWN) return true;
                // don't handle event unless its ACTION_UP so "doSomething()" only runs once.
                if(event.getAction()!=MotionEvent.ACTION_UP) return false;
                if(PIN_SHOW){
                    PIN_SHOW=false;
                    btn_PIN.setPressed(false);
                    String sec="";
                    for(int i=0;i<PIN2.length();i++){
                        sec=sec+"*";
                    }
                    text_PIN.setText(sec);
                } else {
                    PIN_SHOW=true;
                    btn_PIN.setPressed(true);
                    text_PIN.setText(PIN2);
                }
                return true;
            }
        });
        if(PIN_SHOW){btn_PIN.setPressed(true);}

        AboutDialog.show();
    }
}

