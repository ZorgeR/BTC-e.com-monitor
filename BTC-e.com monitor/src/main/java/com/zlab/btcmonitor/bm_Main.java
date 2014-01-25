package com.zlab.btcmonitor;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.*;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.google.gson.JsonObject;
import com.zlab.btcmonitor.UI.navDrawer;
import com.zlab.btcmonitor._API.VARs;
import com.zlab.btcmonitor._API.btce_getTicker;
import com.zlab.btcmonitor.adaptors.*;
import com.zlab.btcmonitor.elements.*;
import com.zlab.btcmonitor.workers.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class bm_Main extends Activity
        implements navDrawer.NavigationDrawerCallbacks {

    /** **/
    public static navDrawer mBmNavDrawer;
    public static bm_Main bm_MainState;
    public static Context bm_MainContext;
    public static CharSequence mTitle;

    /** Global **/
    public static int currentApiVersion;
    public static SharedPreferences prefs;
    private static View mDecorView;
    public static int THEME;
    public static int PAGE_ID;
    public static boolean mDecor_isHiden;
    public static boolean mDecor_verifier;
    public static boolean SETTINGS_IS_OPENED=false;
    public static int REFRESH_COUNTER = 0;
    private static final int RGB_MASK = 0x00FFFFFF;
    private static boolean DIAGRAM_IS_BLACK;
    private static long DIAGRAM_LAST_REFRESH=0;
    private static final long DIAGRAM_COOLDOWN=5;
    static String PIN;
    private static String PINCheck="";
    private static boolean PIN_SHOW;
    public static boolean prefs_CHAT=false;
    public static int NOPAIRS_GROUP=3;
    //public static String[] pairs_UI;
    //public static String[] pairs_CODE;

    /** API **/
    public static String API_URL_PRIVATE;
    public static String API_URL_PUBLIC;
    public static String API_KEY;
    public static String API_SECRET;
    public static String API_ZLAB_URL="http://api.z-lab.me/btce/";

    /** Полученные через API данные **/
    public static String getInfo_data;      /** Данные личного кабинета, запрос - > getInfo **/

    /** Настройки **/
    public static boolean prefs_charts_classic =true; /** Charts style **/
    public static boolean prefs_charts_detailed =false; /** Детальный список курсов **/
    public static boolean prefs_show_vector = false; /** Отображать направление движения курса **/
    public static boolean prefs_fullscreen = false; /** Полноэкранный режим **/
    public static boolean prefs_bottom_actionbar = false; /** Кнопки снизу**/
    public static boolean prefs_black_theme = false; /** Темная тема оформления **/
    public static boolean prefs_black_charts = false; /** Темная диаграмма **/
    public static Set<String> prefs_enabled_charts;

    /** === Списки === **/
    /** Курсы **/
    public static ListView chartsList;
    public static bm_ChartsAdaptor chartsAdaptor;
    public static List<bm_ListElementCharts> chartsListElements;
    public static List<bm_ListElementCharts> chartsListElementsToShow;
    public static List<bm_ListElementCharts> chartsListElementsOld;
    //public static List<bm_ChartsListDiffElements> chartsListDiff,chartsListDiffSell,chartsListDiffBuy;
    public static boolean chartsBlocked=false;
    public static boolean[] chartsEnabled=new boolean[VARs.pairs_CODE.length];
    /** Финансы **/
    public static bm_FundsAdaptor fundsAdaptor;
    public static List<bm_ListElementCharts> fundsListElements;
    public static ListView fundsList;
    public static boolean fundsBlocked=false;
    /** Пары **/
    public static ListView pairAskList;
    public static ListView pairBidsList;
    public static Double listPrice;
    public static List<bm_ListElementsDepth>[] pairAskElements;
    public static List<bm_ListElementsDepth>[] pairBidsElements;
    public static bm_DepthAdaptor[] pairAskAdaptor;
    public static bm_DepthAdaptor[] pairBidsAdaptor;
    public static Button btn_Orders,btn_Buy,btn_Sell,btn_History;
    public static ImageView[] imgCharts;
    public static Bitmap[] imgChartsBitmap;
    public static boolean[] imgRefreshIsBlocked = new boolean[VARs.pairs_CODE.length];
    public static TextView[] textLast,textLow,textHigh,textBal;
    public static String[] txtLast = new String[VARs.pairs_CODE.length];
    public static String[] txtLow = new String[VARs.pairs_CODE.length];
    public static String[] txtHigh = new String[VARs.pairs_CODE.length];

    //public static WebView webCharts;
    /** Ордеры **/
    public static ListView orderList;
    public static List<bm_ListElementOrder> orderElements;
    public static bm_OrdersAdaptor orderAdaptor;
    /** История **/
    public static ListView historyList;
    public static List<bm_ListElementsHistory> historyElements;
    public static bm_HistoryAdaptor historyAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Определение версии API **/
        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        /** Обновление массива валют **/
        //pairs_UI=VARs.pairs_UIz;
        //pairs_CODE=VARs.pairs_CODEz;

        /** Инициализация настроек **/
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        getSettigns();
        setSettings();
        if(prefs_bottom_actionbar){getWindow().setUiOptions(1);} /** Кнопки вниз **/
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_main);

        mDecorView = getWindow().getDecorView();
        configUI();

        bm_MainContext = this;
        bm_MainState = ((bm_Main) bm_MainContext);

        mBmNavDrawer = (navDrawer)getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mBmNavDrawer.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        this.invalidateOptionsMenu();

        // PIN CHECK
        PIN = prefs.getString("PIN","");
        if(!PIN.equals("")){checkPIN();}
    }

    public static void checkPIN(){
            //Toast.makeText(bm_MainContext,"TRIAL",Toast.LENGTH_LONG).show();

            AlertDialog.Builder action_dialog = new AlertDialog.Builder(bm_MainContext);
            action_dialog.setTitle("PIN-CODE");
            LayoutInflater inflater = bm_MainState.getLayoutInflater();
            View layer = inflater.inflate(R.layout.pin,null);

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
                    if(PIN.equals(PINCheck)){
                        AboutDialog.dismiss();
                        PINCheck="";text_PIN.setText("");
                    } else {
                        Toast.makeText(bm_MainContext,bm_Main.bm_MainState.getResources().getString(R.string.pin_fail),Toast.LENGTH_LONG).show();
                    }
                }
            });
            btn_PINCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bm_MainState.finish();
                }
            });
            btn_PINUndo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {PINCheck="";text_PIN.setText("");}
            });

            btn_PIN0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {PINCheck = PINCheck+"0"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"0");}else{text_PIN.setText(text_PIN.getText()+"*");}}
            });
            btn_PIN1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {PINCheck = PINCheck+"1"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"1");}else{text_PIN.setText(text_PIN.getText()+"*");}}
            });
            btn_PIN2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {PINCheck = PINCheck+"2"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"2");}else{text_PIN.setText(text_PIN.getText()+"*");}}
            });
            btn_PIN3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {PINCheck = PINCheck+"3"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"3");}else{text_PIN.setText(text_PIN.getText()+"*");}}
            });
            btn_PIN4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {PINCheck = PINCheck+"4"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"4");}else{text_PIN.setText(text_PIN.getText()+"*");}}
            });
            btn_PIN5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {PINCheck = PINCheck+"5"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"5");}else{text_PIN.setText(text_PIN.getText()+"*");}}
            });
            btn_PIN6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {PINCheck = PINCheck+"6"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"6");}else{text_PIN.setText(text_PIN.getText()+"*");}}
            });
            btn_PIN7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {PINCheck = PINCheck+"7"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"7");}else{text_PIN.setText(text_PIN.getText()+"*");}}
            });
            btn_PIN8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {PINCheck = PINCheck+"8"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"8");}else{text_PIN.setText(text_PIN.getText()+"*");}}
            });
            btn_PIN9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {PINCheck = PINCheck+"9"; if(PIN_SHOW){text_PIN.setText(text_PIN.getText()+"9");}else{text_PIN.setText(text_PIN.getText()+"*");}}
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
                        for(int i=0;i<PINCheck.length();i++){
                            sec=sec+"*";
                        }
                        text_PIN.setText(sec);
                    } else {
                        PIN_SHOW=true;
                        btn_PIN.setPressed(true);
                        text_PIN.setText(PINCheck);
                    }
                    return true;
                }
            });

            AboutDialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(SETTINGS_IS_OPENED){
            SETTINGS_IS_OPENED=false;
            getSettigns();
            if(chartsAdaptor!=null){
                //bm_Main.chartsAdaptor.setItems(bm_ChartsAdaptor.hide(bm_Main.chartsListElements));
                reHide();
                //chartsAdaptor = new bm_ChartsAdaptor(bm_MainContext,R.layout.charts_list_item,chartsListElementsToShow);
                //chartsList.setAdapter(chartsAdaptor);
                //chartsList.requestLayout();
                chartsAdaptor.notifyDataSetChanged();
                //chartsAdaptor.notifyDataSetInvalidated();
            }
            if(fundsAdaptor!=null){fundsAdaptor.notifyDataSetChanged();}
            if(imgCharts!=null){
                for(int i=0;i<VARs.pairs_CODE.length;i++)
                {
                        imgChartsBitmap[i]=BitmapFactory.decodeResource(getResources(), R.drawable.charts_loading);
                    try{
                        imgCharts[i].setImageBitmap(imgChartsBitmap[i]);
                    } catch (NullPointerException e){}
                }
            }
            doRefresh();
        }
        if(prefs_fullscreen){hideSystemUI();}else{mDecorView.getRootView().postDelayed(new Runnable() {
            @Override
            public void run() {
                showSystemUI();
            }},250);}
        updateNavDrawer();
    }

    public void doRefresh(){
        if(mTitle.equals(getResources().getString(R.string.title_charts))){
            if(!bm_Main.chartsBlocked){
                bm_Main.chartsBlocked=true;

                //final String[] pairscode = VARs.pairs_CODE.clone();
                //final String[] pairsui = VARs.pairs_UI.clone();

                //bm_Charts.listElementsSizeCheckkkkk(pairscode,pairsui);          /***************************************************************************/
                //bm_Main.chartsAdaptor.setItems(bm_ChartsAdaptor.hide(bm_Main.chartsListElements));
                reHide();
                bm_Main.chartsAdaptor.notifyDataSetChanged();

                Thread thread = new Thread()
                {
                    @Override
                    public void run() {
                        bm_Charts.update_charts(/*pairscode,pairsui*/);
                    }
                };
                thread.start();
            }
        } else if (mTitle.equals(getResources().getString(R.string.title_office))){
            Thread thread = new Thread()
            {
                @Override
                public void run() {
                    bm_Office.updateInfo();
                }
            };
            thread.start();
        } else {
            refresh_pair_page();
        }
    }

    public void updateNavDrawer(){
        ArrayList<String> newlist=new ArrayList<String>();
        for(int i=0;i< VARs.pairs_CODE.length;i++){
            if(bm_Main.chartsEnabled[i]){
                newlist.add(VARs.pairs_UI[i]);
            }
        }
        ArrayList<String> NAV_DRAWER = newlist;

        NAV_DRAWER.add(0, getString(R.string.title_charts));
        NAV_DRAWER.add(1, getString(R.string.title_office));
        /** CHAT **/
        if(bm_Main.prefs_CHAT){
            NAV_DRAWER.add(2, getString(R.string.chat));}

        navDrawer.my_adapter.clear();
        navDrawer.my_adapter.addAll(NAV_DRAWER);
        navDrawer.my_adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop(){
        // Pair+"*"+Last+"*"+Buy+"*"+Sell+"*"+Updated+"*"+High+"*"+Low;
        FileOutputStream fos = null;
        try {
            for(int i=0;i<VARs.pairs_CODE.length;i++){
                fos = openFileOutput("charts_"+VARs.pairs_CODE[i]+".json", Context.MODE_PRIVATE);
                fos.write(chartsListElements.get(i).getAsString().getBytes());
                fos.close();
            }

        } catch (Exception e) {
            //Log.e("ERR", "MSG");
        }
        super.onStop();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commitAllowingStateLoss();
        PAGE_ID=position;
    }
    public void onSectionAttached(int number) {

        if(number==1){
            mTitle = getString(R.string.title_charts);
        } else if(number==2){
            mTitle = getString(R.string.title_office);
        } else if(prefs_CHAT && number==3){
            mTitle = getString(R.string.chat);
        } else {
            mTitle = chartsListElementsToShow.get(number-(NOPAIRS_GROUP+1)).getPair();
        }
    }
    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
            @Override
            public void onMenuVisibilityChanged(boolean isVisible) {
                if(isVisible){mDecor_isHiden=false;}
            }
        });
    }

    @Override
    public void onBackPressed(){
        if(mTitle!=getString(R.string.title_charts)){
            navDrawer.selectItem(0);
            //bm_MainState.onNavigationDrawerItemSelected(position+2);
            mTitle=getString(R.string.title_charts);
            bm_MainState.invalidateOptionsMenu();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mBmNavDrawer.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.bmon_main, menu);
            if(prefs_fullscreen){menu.findItem(R.id.action_fullscreen).setChecked(true);}
            if(prefs_charts_classic){menu.findItem(R.id.action_classic).setChecked(true);}
            if(prefs_charts_detailed){menu.findItem(R.id.action_detailed).setChecked(true);}
            if(prefs_show_vector){menu.findItem(R.id.action_vector).setChecked(true);}
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:{
                Intent settingsActivity = new Intent(getBaseContext(),
                        bm_Preferences.class);
                startActivity(settingsActivity);
                return true;
            }
            case R.id.action_exit:{
                finish();
                //bm_MainState.finish();
                return true;
            }
            case R.id.action_fullscreen:{
                //hideSystemUI();
                if(mDecor_verifier){
                    showSystemUI();
                    mDecor_verifier=false;
                    item.setChecked(false);
                }else{
                    hideSystemUI();
                    mDecor_verifier=true;
                    item.setChecked(true);
                }
                return true;
            }
            case R.id.action_classic:{
                //hideSystemUI();
                if(prefs_charts_classic){
                    prefs_charts_classic=false;prefs.edit().putBoolean("prefs_charts_classic", false).commit();
                    item.setChecked(false);
                } else {
                    prefs_charts_classic=true;prefs.edit().putBoolean("prefs_charts_classic", true).commit();
                    item.setChecked(true);
                }
                getSettigns();
                reHide();
                chartsAdaptor.notifyDataSetInvalidated();
                return true;
            }
            case R.id.action_detailed:{
                //hideSystemUI();
                if(prefs_charts_detailed){
                    prefs_charts_detailed=false;prefs.edit().putBoolean("prefs_charts_detailed", false).commit();
                    item.setChecked(false);
                } else {
                    prefs_charts_detailed=true;prefs.edit().putBoolean("prefs_charts_detailed", true).commit();
                    item.setChecked(true);
                }
                getSettigns();
                reHide();
                chartsAdaptor.notifyDataSetInvalidated();
                return true;
            }
            case R.id.action_vector:{
                //hideSystemUI();
                if(prefs_show_vector){
                    prefs_show_vector=false;prefs.edit().putBoolean("prefs_show_vector", false).commit();
                    item.setChecked(false);
                } else {
                    prefs_show_vector=true;prefs.edit().putBoolean("prefs_show_vector", true).commit();
                    item.setChecked(true);
                }
                getSettigns();
                reHide();
                chartsAdaptor.notifyDataSetInvalidated();
                return true;
            }
            case R.id.action_about:{

                AlertDialog.Builder ab = new AlertDialog.Builder(bm_MainContext);
                ab.setTitle(getResources().getString(R.string.action_about));

                View layer = getLayoutInflater().inflate(R.layout.about,null);
                ab.setView(layer);
                AlertDialog AboutDialog = ab.create();
                AboutDialog.show();

                return true;
            }
            case R.id.action_refresh:{
                doRefresh();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView;

            if(mTitle.equals(getString(R.string.title_office))){
                rootView=pageOffice(container,inflater);
            } else if(mTitle.equals(getString(R.string.title_charts))){
                rootView=pageCharts(container,inflater);
            } else if(mTitle.equals(getString(R.string.chat))){
                rootView=pageChat(container, inflater);
            } else {
                rootView=pagePairs(container,inflater);
            }
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((bm_Main) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        for(int i=0;i<chartsListElementsToShow.size();i++){
                if(mTitle.equals(chartsListElementsToShow.get(i).getPair())){
                    if (navDrawer.mCallbacks != null) {
                        navDrawer.mCallbacks.onNavigationDrawerItemSelected(i+NOPAIRS_GROUP);
                    }
                }
        }
    }

    public static View pageChat(ViewGroup container, LayoutInflater inflater){
        View rootView = inflater.inflate(R.layout.chat, container, false);

        final WebView webView = (WebView) rootView.findViewById(R.id.webChat);
        webView.getSettings().setJavaScriptEnabled(true);

        //final int wvwidth = webView.getWidth();
        //final int wvheight = webView.getHeight();
        /*
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(bm_MainContext, description, Toast.LENGTH_SHORT).show();
            }
        });*/

        final ProgressDialog mProgressDialog = new ProgressDialog(bm_MainContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(bm_MainState.getResources().getString(R.string.chat_warning));

        mProgressDialog.show();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                webView.loadUrl("javascript:(function() { " +
                        //"document.getElementById('PoW_working').style.display='none'"+
                        //"document.getElementById('orp_con').style.display='none'"+
                        "document.getElementById('content').getElementsByTagName('div')[0].style.display='none';"+
                        "document.getElementById('header-logo').style.display='none';"+
                        "document.getElementById('header-ticker').style.display='none';"+
                        "document.getElementById('footer').style.display='none';"+
                        "document.getElementsByClassName('block')[5].style.display='none';"+
                        "document.getElementsByClassName('block')[6].style.display='none';"+


                        //"document.getElementById('content').style.margin='inherit';"+
                        "document.getElementById('content').style.width='90%';"+
                        "document.getElementById('nChatCon').style.width='inherit';"+
                        "document.getElementById('nChat').style.width='inherit';"+
                        "document.getElementById('content').getElementsByTagName('div')[1].style.width='inherit';"+
                        "document.getElementById('content').getElementsByTagName('div')[1].style.float='none';"+

                        "document.getElementsByClassName('menu')[0].style.display='none';" +

                        //"var div = document.getElementById('header');"+
                        "document.getElementById('header').style.height='0px';"+
                        "document.getElementById('header').style.background='none';"+
                        "document.getElementById('header').style.width='0px';" +

                        //"div = document.getElementById('header-content');"+
                        "document.getElementById('header-content').style.height='0px';"+
                        "document.getElementById('header-content').style.width='200px';"+
                        "document.getElementById('header-content').style.margin='initial';" +

                        //"div = document.getElementById('header-profile');"+
                        "document.getElementById('header-profile').style.width='300px';" +
                        //"document.getElementById('header-profile').style.float='left';" +

                        //"document.getElementById('nav-container').style.height='0px';" +

                        //"document.getElementById('content').getElementsByTagName('div')[1].style.width='300px';"+
                        //"document.getElementById('content').getElementsByTagName('div')[1].style.float='left';"+
                        //"document.getElementById('content').getElementsByTagName('div')[1].style.margin-top='10px';"+
                        //"document.getElementById('content').getElementsByTagName('div')[1].style.margin-left='10px';"+

                        "document.getElementById('nav-container').getElementsByTagName('div').style.float='right';" +
                        "document.getElementById('nav-container').getElementsByTagName('div').style.position='absolute';" +
                        //"document.getElementById('nav-container').getElementsByTagName('div').style.top='0px';" +
                        //"document.getElementById('nav-container').getElementsByTagName('div').style.left='95%';" +

                        "document.getElementById('content').getElementsByTagName('div')[0].style.width='0px';"+
                        //"document.getElementById('content').getElementsByTagName('div')[0].style.float='left';"+
                        "document.getElementById('content').getElementsByTagName('div')[0].style.display='none';"+

                        "})()");

                mProgressDialog.dismiss();
            }
        });
        webView.loadUrl("https://btc-e.com/");

        return rootView;
    }
    public static View pageOffice(ViewGroup container, LayoutInflater inflater){
        View rootView = inflater.inflate(R.layout.office, container, false);
        fundsList = (ListView) rootView.findViewById(R.id.FundsList);

        if(fundsListElements!=null){
            //fundsList.setAdapter(fundsAdaptor);
            //}
            //fundsAdaptor.notifyDataSetChanged();
            fundsList.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fundsList.setAdapter(fundsAdaptor);
                }
            },250);
        } else {
            Thread thread = new Thread()
            {
                @Override
                public void run() {
                    bm_Office.updateInfo();
                }
            };
            thread.start();
        }

        TextView textView = (TextView) rootView.findViewById(R.id.textStat);
        TextView txtHead = (TextView) rootView.findViewById(R.id.txtHead);
        if(bm_Main.currentApiVersion >= Build.VERSION_CODES.JELLY_BEAN){
            txtHead.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
            textView.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        }
        if(getInfo_data != null && !getInfo_data.isEmpty()){
            textView.setText(getInfo_data);
        } else {
            textView.setText(mTitle);
        }
        return rootView;
    }
    public static View pageCharts(ViewGroup container, LayoutInflater inflater){
        View rootView = inflater.inflate(R.layout.charts, container, false);
        chartsList = (ListView) rootView.findViewById(R.id.ChartsList);

        chartsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                navDrawer.selectItem(position + bm_Main.NOPAIRS_GROUP);
                //bm_MainState.onNavigationDrawerItemSelected(position+2);
                mTitle = chartsListElementsToShow.get(position).getPair()/*VARs.pairs_UI[position]*/;
                bm_MainState.invalidateOptionsMenu();
            }
        });

        if(chartsListElements==null){chartsArrayBlank();}

        if(chartsAdaptor==null){
            reHide();
            chartsAdaptor = new bm_ChartsAdaptor(bm_MainContext,R.layout.charts_list_item,chartsListElementsToShow);
        }

        chartsList.postDelayed(new Runnable() {
            @Override
            public void run() {
                chartsList.setAdapter(chartsAdaptor);
                chartsList.requestLayout();
                bm_Main.chartsAdaptor.notifyDataSetChanged();
            }
        },250);

        if(!bm_Main.chartsBlocked){
            bm_Main.chartsBlocked=true;

            //final String[] pairscode = VARs.pairs_CODE.clone();
            //final String[] pairsui = VARs.pairs_UI.clone();

            //bm_Charts.listElementsSizeCheckkkkk(pairscode,pairsui);
            //bm_Main.chartsAdaptor.setItems(bm_ChartsAdaptor.hide(bm_Main.chartsListElements));
            reHide();
            bm_Main.chartsAdaptor.notifyDataSetChanged();

            Thread thread = new Thread()
            {
                @Override
                public void run() {
                    bm_Charts.update_charts(/*pairscode,pairsui*/);
                }
            };
            thread.start();
        }

        return rootView;
    }
    public static View pagePairs(ViewGroup container, LayoutInflater inflater){
        View rootView;

        if(bm_MainState.getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT){
            rootView = inflater.inflate(R.layout.pairs, container, false);
        } else {
            rootView = inflater.inflate(R.layout.pairs_landscape, container, false);
        }

        //int pair_code=-1;
        int pair_code=0; /** Fix crash on back button and resume **/
        for(int i=0;i<VARs.pairs_CODE.length;i++)
        {if(VARs.pairs_UI[i].equals(mTitle)){
            pair_code=i;
        }}
        final int PAIR_CODE = pair_code;
        final String PAIR_ID = VARs.pairs_CODE[PAIR_CODE];


        btn_Buy = (Button) rootView.findViewById(R.id.btnBuy);
        btn_Sell = (Button) rootView.findViewById(R.id.btnSell);
        btn_Orders = (Button) rootView.findViewById(R.id.btnOrders);
        btn_History = (Button) rootView.findViewById(R.id.btnHistory);

        if(textLast==null){textLast=new TextView[VARs.pairs_CODE.length];}
        if(textLow==null){textLow=new TextView[VARs.pairs_CODE.length];}
        if(textHigh==null){textHigh=new TextView[VARs.pairs_CODE.length];}
        if(textBal==null){textBal=new TextView[VARs.pairs_CODE.length];}

        textLast[PAIR_CODE] = (TextView) rootView.findViewById(R.id.textLast);
        textLow[PAIR_CODE] = (TextView) rootView.findViewById(R.id.textLow);
        textHigh[PAIR_CODE] = (TextView) rootView.findViewById(R.id.textHigh);

        if(imgCharts==null){imgCharts=new ImageView[VARs.pairs_CODE.length];}
        imgCharts[PAIR_CODE] = (ImageView) rootView.findViewById(R.id.imgCharts);

        //webCharts = (WebView) rootView.findViewById(R.id.webCharts);
        TextView textOrdersLeft = (TextView) rootView.findViewById(R.id.textOrdersLeft);
        TextView textOrdersRight = (TextView) rootView.findViewById(R.id.textOrdersRight);


        if(bm_Main.currentApiVersion >= Build.VERSION_CODES.JELLY_BEAN){
            textLast[PAIR_CODE].setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
            textLow[PAIR_CODE].setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
            textHigh[PAIR_CODE].setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
            textOrdersLeft.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
            textOrdersRight.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
            btn_Buy.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
            btn_Sell.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
            btn_Orders.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
            btn_History.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
        }

        btn_Buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder action_dialog = new AlertDialog.Builder(bm_MainContext);
                action_dialog.setTitle(bm_Main.bm_MainState.getResources().getString(R.string.buy));
                LayoutInflater inflater = bm_MainState.getLayoutInflater();
                View layer = inflater.inflate(R.layout.trade,null);

                final EditText editTradeAmount = (EditText) layer.findViewById(R.id.editTradeAmount);
                final EditText editTradePrice = (EditText) layer.findViewById(R.id.editTradePrice);
                TextView textTradeAmount = (TextView) layer.findViewById(R.id.textTradeAmount);
                TextView textTradePrice = (TextView) layer.findViewById(R.id.textTradePrice);
                final TextView textTradeTotal = (TextView) layer.findViewById(R.id.textTradeTotal);
                final TextView textTradeTax = (TextView) layer.findViewById(R.id.textTradeTax);
                final SeekBar seekBarTrade = (SeekBar) layer.findViewById(R.id.seekBarTrade);
                textTradeTotal.setText("0.0" + " " + mTitle.toString().split(" / ")[1]);
                textTradeTax.setText("0.0" + " " + mTitle.toString().split(" / ")[0]);

                final TextView textBalance = (TextView) layer.findViewById(R.id.textBal);

                Double bl=0.0;
                    for(int i=0;i<fundsListElements.size();i++){
                        if(fundsListElements.get(i).getPair().toUpperCase().equals(mTitle.toString().split(" / ")[1].toUpperCase())){
                            textBalance.setText(new DecimalFormat("#.#####").format(Double.parseDouble(fundsListElements.get(i).getLast()))+" "+mTitle.toString().split(" / ")[1]);
                            bl=Double.parseDouble(fundsListElements.get(i).getLast());
                        }
                    }
                for(int i=0;i<fundsListElements.size();i++){
                    if(fundsListElements.get(i).getPair().toUpperCase().equals(mTitle.toString().split(" / ")[0].toUpperCase())){
                        textBalance.setText(textBalance.getText()+"\n"+new DecimalFormat("#.#####").format(Double.parseDouble(fundsListElements.get(i).getLast()))+" "+mTitle.toString().split(" / ")[0]);
                    }
                }

                final Double dbl=bl;

                seekBarTrade.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        //if(editTradeAmount.getText().toString().equals("")){editTradeAmount.setText("0.00");}
                        //if(editTradePrice.getText().toString().equals("")){editTradePrice.setText("0.00");}

                        Double total=dbl/Double.parseDouble(editTradePrice.getText().toString());
                        NumberFormat formatter = new DecimalFormat("#.#####");

                        editTradeAmount.setText(formatter.format(total/100*progress).replace(",","."));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        if(editTradeAmount.getText().toString().equals("")){editTradeAmount.setText("0");}
                        if(editTradePrice.getText().toString().equals("")){editTradePrice.setText("0");}
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

                editTradeAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            Double total = Double.parseDouble(editTradeAmount.getText().toString()) * Double.parseDouble(editTradePrice.getText().toString());
                            Double tax = Double.parseDouble(editTradeAmount.getText().toString()) * 0.002;
                            if(PAIR_ID.equals("usd_rur")){tax=total * 0.005;}
                            NumberFormat formatter = new DecimalFormat("#.#####");

                            textTradeTotal.setText(formatter.format(total) + " " + mTitle.toString().split(" / ")[1]);
                            textTradeTax.setText(formatter.format(tax) + " " + mTitle.toString().split(" / ")[0]);
                            if(dbl<total){
                                editTradeAmount.setTextColor(Color.RED);
                            } else {
                                editTradeAmount.setTextColor(textTradeTax.getTextColors());
                            }
                        } catch (Exception e ){}
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                editTradePrice.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                        Double total = (Double.parseDouble(editTradeAmount.getText().toString()) * Double.parseDouble(editTradePrice.getText().toString()));
                            Double tax = Double.parseDouble(editTradeAmount.getText().toString()) * 0.002;
                            if(PAIR_ID.equals("usd_rur")){tax=total * 0.005;}

                            NumberFormat formatter = new DecimalFormat("#.########");
                            textTradeTotal.setText(formatter.format(total) + " " + mTitle.toString().split(" / ")[1]);
                            textTradeTax.setText(formatter.format(tax) + " " + mTitle.toString().split(" / ")[0]);
                            if(dbl<total){
                                editTradeAmount.setTextColor(Color.RED);
                            } else {
                                editTradeAmount.setTextColor(textTradeTax.getTextColors());
                            }
                        } catch (Exception e ){}
                    }
                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                for(int i=0;i<VARs.pairs_CODE.length;i++)
                {if(VARs.pairs_UI[i].equals(mTitle)){
                    editTradePrice.setText(pairAskElements[i].get(0).getPrice());
                    textTradeAmount.setText(textTradeAmount.getText()+" "+mTitle.toString().split(" / ")[0]+":");
                    textTradePrice.setText(textTradePrice.getText()+" "+mTitle.toString().split(" / ")[0]+":");
                }}

                action_dialog.setView(layer);
                action_dialog.setNegativeButton(bm_MainState.getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                action_dialog.setPositiveButton(bm_Main.bm_MainState.getResources().getString(R.string.buy)+" "+mTitle.toString().split(" / ")[0],
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Thread thread = new Thread()
                                {
                                    @Override
                                    public void run() {
                                        bm_Trade.doTrade(PAIR_ID, "buy", editTradePrice.getText().toString(), editTradeAmount.getText().toString());
                                    }
                                };
                                thread.start();
                            }
                        });
                AlertDialog AboutDialog = action_dialog.create();
                AboutDialog.show();
            }
        });

        btn_Sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder action_dialog = new AlertDialog.Builder(bm_MainContext);
                action_dialog.setTitle(bm_Main.bm_MainState.getResources().getString(R.string.sell));
                LayoutInflater inflater = bm_MainState.getLayoutInflater();
                View layer = inflater.inflate(R.layout.trade,null);

                final EditText editTradeAmount = (EditText) layer.findViewById(R.id.editTradeAmount);
                final EditText editTradePrice = (EditText) layer.findViewById(R.id.editTradePrice);
                TextView textTradeAmount = (TextView) layer.findViewById(R.id.textTradeAmount);
                TextView textTradePrice = (TextView) layer.findViewById(R.id.textTradePrice);
                final TextView textTradeTotal = (TextView) layer.findViewById(R.id.textTradeTotal);
                final TextView textTradeTax = (TextView) layer.findViewById(R.id.textTradeTax);
                final SeekBar seekBarTrade = (SeekBar) layer.findViewById(R.id.seekBarTrade);
                final TextView textBalance = (TextView) layer.findViewById(R.id.textBal);

                Double bl=0.0;

                for(int i=0;i<fundsListElements.size();i++){
                    if(fundsListElements.get(i).getPair().toUpperCase().equals(mTitle.toString().split(" / ")[0].toUpperCase())){
                        textBalance.setText(new DecimalFormat("#.#####").format(Double.parseDouble(fundsListElements.get(i).getLast()))+" "+mTitle.toString().split(" / ")[0]);
                        bl=Double.parseDouble(fundsListElements.get(i).getLast());
                    }
                }
                for(int i=0;i<fundsListElements.size();i++){
                    if(fundsListElements.get(i).getPair().toUpperCase().equals(mTitle.toString().split(" / ")[1].toUpperCase())){
                        textBalance.setText(textBalance.getText()+"\n"+new DecimalFormat("#.#####").format(Double.parseDouble(fundsListElements.get(i).getLast()))+" "+mTitle.toString().split(" / ")[1]);
                    }
                }

                for(int i=0;i<VARs.pairs_CODE.length;i++)
                {if(VARs.pairs_UI[i].equals(mTitle)){
                    editTradePrice.setText(pairBidsElements[i].get(0).getPrice());
                    textTradeAmount.setText(textTradeAmount.getText()+" "+mTitle.toString().split(" / ")[0]+":");
                    textTradePrice.setText(textTradePrice.getText()+" "+mTitle.toString().split(" / ")[0]+":");
                }}

                textTradeTotal.setText("0.0" + " " + mTitle.toString().split(" / ")[1]);
                textTradeTax.setText("0.0" + " " + mTitle.toString().split(" / ")[1]);

                final Double dbl=bl;

                seekBarTrade.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        NumberFormat formatter = new DecimalFormat("#.########");

                        editTradeAmount.setText(formatter.format(dbl/100*progress).replace(",","."));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }
                });
                editTradeAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                        Double total = Double.parseDouble(editTradeAmount.getText().toString()) * Double.parseDouble(editTradePrice.getText().toString());
                        Double tax = total * 0.002;
                            if(PAIR_ID.equals("usd_rur")){tax=total * 0.005;}
                            NumberFormat formatter = new DecimalFormat("#.#####");
                            textTradeTotal.setText(formatter.format(total) + " " + mTitle.toString().split(" / ")[1]);
                            textTradeTax.setText(formatter.format(tax) + " " + mTitle.toString().split(" / ")[1]);
                            if(dbl<Double.parseDouble(editTradeAmount.getText().toString())){
                                editTradeAmount.setTextColor(Color.RED);
                            } else {
                                editTradeAmount.setTextColor(textTradeTax.getTextColors());
                            }
                        } catch (Exception e ){}
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                editTradePrice.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                        Double total = (Double.parseDouble(editTradeAmount.getText().toString()) * Double.parseDouble(editTradePrice.getText().toString()));
                        Double tax = total * 0.002;
                            if(PAIR_ID.equals("usd_rur")){tax=total * 0.005;}
                            NumberFormat formatter = new DecimalFormat("#.#####");
                            textTradeTotal.setText(formatter.format(total) + " " + mTitle.toString().split(" / ")[1]);
                            textTradeTax.setText(formatter.format(tax) + " " + mTitle.toString().split(" / ")[1]);
                            if(dbl<Double.parseDouble(editTradeAmount.getText().toString())){
                                editTradeAmount.setTextColor(Color.RED);
                            } else {
                                editTradeAmount.setTextColor(textTradeTax.getTextColors());
                            }
                        } catch (Exception e ){}
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                action_dialog.setView(layer);
                action_dialog.setNegativeButton(bm_MainState.getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                action_dialog.setPositiveButton(bm_Main.bm_MainState.getResources().getString(R.string.sell)+" "+mTitle.toString().split(" / ")[0],
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Thread thread = new Thread()
                                {
                                    @Override
                                    public void run() {
                                        bm_Trade.doTrade(PAIR_ID,"sell",editTradePrice.getText().toString(),editTradeAmount.getText().toString());
                                    }
                                };
                                thread.start();
                            }
                        });
                AlertDialog AboutDialog = action_dialog.create();
                AboutDialog.show();
            }
        });

        if(orderList==null){orderList = new ListView(bm_MainContext);}

        btn_Orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder action_dialog = new AlertDialog.Builder(bm_MainContext);
                action_dialog.setTitle(bm_Main.bm_MainState.getResources().getString(R.string.orders));
                LayoutInflater inflater = bm_MainState.getLayoutInflater();
                View layer = inflater.inflate(R.layout.orders_list,null);

                orderList = (ListView) layer.findViewById(R.id.listOrders);
                orderAdaptor = new bm_OrdersAdaptor(bm_MainContext, R.layout.orders_list_item,orderElements);
                orderList.setAdapter(orderAdaptor);

                action_dialog.setView(layer);
                action_dialog.setNegativeButton(bm_Main.bm_MainState.getResources().getString(R.string.close),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog AboutDialog = action_dialog.create();
                AboutDialog.show();
            }
        });
        btn_History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder action_dialog = new AlertDialog.Builder(bm_MainContext);
                action_dialog.setTitle(bm_Main.bm_MainState.getResources().getString(R.string.history));
                LayoutInflater inflater = bm_MainState.getLayoutInflater();
                View layer = inflater.inflate(R.layout.history_list,null);

                historyList = (ListView) layer.findViewById(R.id.listHistory);
                historyAdaptor = new bm_HistoryAdaptor(bm_MainContext, R.layout.history_list_item,historyElements);
                historyList.setAdapter(historyAdaptor);

                action_dialog.setView(layer);
                action_dialog.setNegativeButton(bm_Main.bm_MainState.getResources().getString(R.string.close),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog AboutDialog = action_dialog.create();
                AboutDialog.show();
            }
        });

        pairAskList = (ListView) rootView.findViewById(R.id.listAsk);
        pairBidsList = (ListView) rootView.findViewById(R.id.listBids);

        if(bm_MainState.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
            pairAskList.setOnTouchListener(new ListView.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });

            pairBidsList.setOnTouchListener(new ListView.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });
        }

        pairAskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPrice=Double.parseDouble(pairAskElements[PAIR_CODE].get(position).getPrice());
                //Toast.makeText(bm_MainContext,listPrice.toString(),Toast.LENGTH_SHORT).show();
                AlertDialog.Builder action_dialog = new AlertDialog.Builder(bm_MainContext);
                action_dialog.setTitle(bm_Main.bm_MainState.getResources().getString(R.string.buy));
                LayoutInflater inflater = bm_MainState.getLayoutInflater();
                View layer = inflater.inflate(R.layout.trade,null);

                final EditText editTradeAmount = (EditText) layer.findViewById(R.id.editTradeAmount);
                final EditText editTradePrice = (EditText) layer.findViewById(R.id.editTradePrice);
                TextView textTradeAmount = (TextView) layer.findViewById(R.id.textTradeAmount);
                TextView textTradePrice = (TextView) layer.findViewById(R.id.textTradePrice);
                final TextView textTradeTotal = (TextView) layer.findViewById(R.id.textTradeTotal);
                final TextView textTradeTax = (TextView) layer.findViewById(R.id.textTradeTax);
                final SeekBar seekBarTrade = (SeekBar) layer.findViewById(R.id.seekBarTrade);
                textTradeTotal.setText("0.0" + " " + mTitle.toString().split(" / ")[1]);
                textTradeTax.setText("0.0" + " " + mTitle.toString().split(" / ")[0]);

                final TextView textBalance = (TextView) layer.findViewById(R.id.textBal);

                Double bl=0.0;
                for(int i=0;i<fundsListElements.size();i++){
                    if(fundsListElements.get(i).getPair().toUpperCase().equals(mTitle.toString().split(" / ")[1].toUpperCase())){
                        textBalance.setText(new DecimalFormat("#.#####").format(Double.parseDouble(fundsListElements.get(i).getLast()))+" "+mTitle.toString().split(" / ")[1]);
                        bl=Double.parseDouble(fundsListElements.get(i).getLast());
                    }
                }
                for(int i=0;i<fundsListElements.size();i++){
                    if(fundsListElements.get(i).getPair().toUpperCase().equals(mTitle.toString().split(" / ")[0].toUpperCase())){
                        textBalance.setText(textBalance.getText()+"\n"+new DecimalFormat("#.#####").format(Double.parseDouble(fundsListElements.get(i).getLast()))+" "+mTitle.toString().split(" / ")[0]);
                    }
                }

                final Double dbl=bl;

                seekBarTrade.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        //if(editTradeAmount.getText().toString().equals("")){editTradeAmount.setText("0.00");}
                        //if(editTradePrice.getText().toString().equals("")){editTradePrice.setText("0.00");}

                        Double total=dbl/Double.parseDouble(editTradePrice.getText().toString());
                        NumberFormat formatter = new DecimalFormat("#.#####");

                        editTradeAmount.setText(formatter.format(total/100*progress).replace(",","."));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        if(editTradeAmount.getText().toString().equals("")){editTradeAmount.setText("0");}
                        if(editTradePrice.getText().toString().equals("")){editTradePrice.setText("0");}
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

                editTradeAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            Double total = Double.parseDouble(editTradeAmount.getText().toString()) * Double.parseDouble(editTradePrice.getText().toString());
                            Double tax = Double.parseDouble(editTradeAmount.getText().toString()) * 0.002;
                            if(PAIR_ID.equals("usd_rur")){tax=total * 0.005;}
                            NumberFormat formatter = new DecimalFormat("#.#####");

                            textTradeTotal.setText(formatter.format(total) + " " + mTitle.toString().split(" / ")[1]);
                            textTradeTax.setText(formatter.format(tax) + " " + mTitle.toString().split(" / ")[0]);
                            if(dbl<total){
                                editTradeAmount.setTextColor(Color.RED);
                            } else {
                                editTradeAmount.setTextColor(textTradeTax.getTextColors());
                            }
                        } catch (Exception e ){}
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                editTradePrice.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            Double total = (Double.parseDouble(editTradeAmount.getText().toString()) * Double.parseDouble(editTradePrice.getText().toString()));
                            Double tax = Double.parseDouble(editTradeAmount.getText().toString()) * 0.002;
                            if(PAIR_ID.equals("usd_rur")){tax=total * 0.005;}

                            NumberFormat formatter = new DecimalFormat("#.########");
                            textTradeTotal.setText(formatter.format(total) + " " + mTitle.toString().split(" / ")[1]);
                            textTradeTax.setText(formatter.format(tax) + " " + mTitle.toString().split(" / ")[0]);
                            if(dbl<total){
                                editTradeAmount.setTextColor(Color.RED);
                            } else {
                                editTradeAmount.setTextColor(textTradeTax.getTextColors());
                            }
                        } catch (Exception e ){}
                    }
                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                editTradePrice.setText(listPrice.toString());
                for(int i=0;i<VARs.pairs_CODE.length;i++)
                {if(VARs.pairs_UI[i].equals(mTitle)){
                    textTradeAmount.setText(textTradeAmount.getText()+" "+mTitle.toString().split(" / ")[0]+":");
                    textTradePrice.setText(textTradePrice.getText()+" "+mTitle.toString().split(" / ")[0]+":");
                }}

                action_dialog.setView(layer);
                action_dialog.setNegativeButton(bm_MainState.getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                action_dialog.setPositiveButton(bm_Main.bm_MainState.getResources().getString(R.string.buy)+" "+mTitle.toString().split(" / ")[0],
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Thread thread = new Thread()
                                {
                                    @Override
                                    public void run() {
                                        bm_Trade.doTrade(PAIR_ID, "buy", editTradePrice.getText().toString(), editTradeAmount.getText().toString());
                                    }
                                };
                                thread.start();
                            }
                        });
                AlertDialog AboutDialog = action_dialog.create();
                AboutDialog.show();
            }
        });
        pairBidsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPrice=Double.parseDouble(pairBidsElements[PAIR_CODE].get(position).getPrice());
                //Toast.makeText(bm_MainContext,listPrice.toString(),Toast.LENGTH_SHORT).show();

                AlertDialog.Builder action_dialog = new AlertDialog.Builder(bm_MainContext);
                action_dialog.setTitle(bm_Main.bm_MainState.getResources().getString(R.string.sell));
                LayoutInflater inflater = bm_MainState.getLayoutInflater();
                View layer = inflater.inflate(R.layout.trade,null);

                final EditText editTradeAmount = (EditText) layer.findViewById(R.id.editTradeAmount);
                final EditText editTradePrice = (EditText) layer.findViewById(R.id.editTradePrice);
                TextView textTradeAmount = (TextView) layer.findViewById(R.id.textTradeAmount);
                TextView textTradePrice = (TextView) layer.findViewById(R.id.textTradePrice);
                final TextView textTradeTotal = (TextView) layer.findViewById(R.id.textTradeTotal);
                final TextView textTradeTax = (TextView) layer.findViewById(R.id.textTradeTax);
                final SeekBar seekBarTrade = (SeekBar) layer.findViewById(R.id.seekBarTrade);
                final TextView textBalance = (TextView) layer.findViewById(R.id.textBal);

                Double bl=0.0;

                for(int i=0;i<fundsListElements.size();i++){
                    if(fundsListElements.get(i).getPair().toUpperCase().equals(mTitle.toString().split(" / ")[0].toUpperCase())){
                        textBalance.setText(new DecimalFormat("#.#####").format(Double.parseDouble(fundsListElements.get(i).getLast()))+" "+mTitle.toString().split(" / ")[0]);
                        bl=Double.parseDouble(fundsListElements.get(i).getLast());
                    }
                }
                for(int i=0;i<fundsListElements.size();i++){
                    if(fundsListElements.get(i).getPair().toUpperCase().equals(mTitle.toString().split(" / ")[1].toUpperCase())){
                        textBalance.setText(textBalance.getText()+"\n"+new DecimalFormat("#.#####").format(Double.parseDouble(fundsListElements.get(i).getLast()))+" "+mTitle.toString().split(" / ")[1]);
                    }
                }

                editTradePrice.setText(listPrice.toString());

                for(int i=0;i<VARs.pairs_CODE.length;i++)
                {if(VARs.pairs_UI[i].equals(mTitle)){
                    textTradeAmount.setText(textTradeAmount.getText()+" "+mTitle.toString().split(" / ")[0]+":");
                    textTradePrice.setText(textTradePrice.getText()+" "+mTitle.toString().split(" / ")[0]+":");
                }}

                textTradeTotal.setText("0.0" + " " + mTitle.toString().split(" / ")[1]);
                textTradeTax.setText("0.0" + " " + mTitle.toString().split(" / ")[1]);

                final Double dbl=bl;

                seekBarTrade.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        NumberFormat formatter = new DecimalFormat("#.########");

                        editTradeAmount.setText(formatter.format(dbl/100*progress).replace(",","."));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }
                });
                editTradeAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            Double total = Double.parseDouble(editTradeAmount.getText().toString()) * Double.parseDouble(editTradePrice.getText().toString());
                            Double tax = total * 0.002;
                            if(PAIR_ID.equals("usd_rur")){tax=total * 0.005;}
                            NumberFormat formatter = new DecimalFormat("#.#####");
                            textTradeTotal.setText(formatter.format(total) + " " + mTitle.toString().split(" / ")[1]);
                            textTradeTax.setText(formatter.format(tax) + " " + mTitle.toString().split(" / ")[1]);
                            if(dbl<Double.parseDouble(editTradeAmount.getText().toString())){
                                editTradeAmount.setTextColor(Color.RED);
                            } else {
                                editTradeAmount.setTextColor(textTradeTax.getTextColors());
                            }
                        } catch (Exception e ){}
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                editTradePrice.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            Double total = (Double.parseDouble(editTradeAmount.getText().toString()) * Double.parseDouble(editTradePrice.getText().toString()));
                            Double tax = total * 0.002;
                            if(PAIR_ID.equals("usd_rur")){tax=total * 0.005;}
                            NumberFormat formatter = new DecimalFormat("#.#####");
                            textTradeTotal.setText(formatter.format(total) + " " + mTitle.toString().split(" / ")[1]);
                            textTradeTax.setText(formatter.format(tax) + " " + mTitle.toString().split(" / ")[1]);
                            if(dbl<Double.parseDouble(editTradeAmount.getText().toString())){
                                editTradeAmount.setTextColor(Color.RED);
                            } else {
                                editTradeAmount.setTextColor(textTradeTax.getTextColors());
                            }
                        } catch (Exception e ){}
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                action_dialog.setView(layer);
                action_dialog.setNegativeButton(bm_MainState.getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                action_dialog.setPositiveButton(bm_Main.bm_MainState.getResources().getString(R.string.sell)+" "+mTitle.toString().split(" / ")[0],
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Thread thread = new Thread()
                                {
                                    @Override
                                    public void run() {
                                        bm_Trade.doTrade(PAIR_ID,"sell",editTradePrice.getText().toString(),editTradeAmount.getText().toString());
                                    }
                                };
                                thread.start();
                            }
                        });
                AlertDialog AboutDialog = action_dialog.create();
                AboutDialog.show();
            }
        });

        if(pairAskElements==null){pairAskElements = (List<bm_ListElementsDepth>[]) new List[VARs.pairs_CODE.length];}
        if(pairBidsElements==null){pairBidsElements=(List<bm_ListElementsDepth>[]) new List[VARs.pairs_CODE.length];}

        if(pairAskAdaptor==null){pairAskAdaptor=new bm_DepthAdaptor[VARs.pairs_CODE.length];}
        if(pairBidsAdaptor==null){pairBidsAdaptor=new bm_DepthAdaptor[VARs.pairs_CODE.length];}

        if(pairAskElements[PAIR_CODE]!=null && pairBidsAdaptor!=null){
            //pairAskAdaptor = new bm_DepthAdaptor(bm_MainContext,R.layout.depth_list_item,pairAskElements);
            //pairBidsAdaptor = new bm_DepthAdaptor(bm_MainContext,R.layout.depth_list_item,pairBidsElements);
            pairAskList.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pairAskList.setAdapter(pairAskAdaptor[PAIR_CODE]);
                }
            }, 250);
            pairBidsList.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pairBidsList.setAdapter(pairBidsAdaptor[PAIR_CODE]);
                }
            },250);
        }

        final LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.chartsLayout);


        if(imgChartsBitmap==null){imgChartsBitmap=new Bitmap[VARs.pairs_CODE.length];}

        if(imgChartsBitmap[PAIR_CODE]!=null){imgCharts[PAIR_CODE].setImageBitmap(imgChartsBitmap[PAIR_CODE]);}

        bm_Main.orderElements = new ArrayList<bm_ListElementOrder>();
        Thread thread = new Thread()
        {
            @Override
            public void run() {
                    bm_Office.updateInfo();
                    bm_Depth.update_depth(PAIR_ID);

                bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bm_MainState.setProgressBarIndeterminateVisibility(true);
                    }
                });
                    bm_ActiveOrders.getActiveOrders(PAIR_ID);
                    bm_PairHistory.getHistory(PAIR_ID);

                JsonObject Ticker = btce_getTicker.getTickerObj(PAIR_ID);
                if(Ticker!=null){
                bm_Main.txtLast[PAIR_CODE] = btce_getTicker.get_last(Ticker);
                bm_Main.txtLow[PAIR_CODE] = btce_getTicker.get_low(Ticker);
                bm_Main.txtHigh[PAIR_CODE] = btce_getTicker.get_high(Ticker);}

                if(!imgRefreshIsBlocked[PAIR_CODE]/* && (DIAGRAM_LAST_REFRESH - (System.currentTimeMillis()/1000))>DIAGRAM_COOLDOWN*/){
                    imgRefreshIsBlocked[PAIR_CODE]=true;
                try {

                    if(prefs_black_charts){
                        imgChartsBitmap[PAIR_CODE] = invert(BitmapFactory.decodeStream((InputStream) new URL(API_ZLAB_URL+PAIR_ID+".php").getContent()));
                        DIAGRAM_IS_BLACK=true;
                    } else {
                        imgChartsBitmap[PAIR_CODE] = BitmapFactory.decodeStream((InputStream) new URL(API_ZLAB_URL+PAIR_ID+".php").getContent());
                    }

                    //DIAGRAM_LAST_REFRESH =System.currentTimeMillis()/1000;
                    bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imgCharts[PAIR_CODE].setImageBitmap(imgChartsBitmap[PAIR_CODE]);
                        }
                    });
                } catch (MalformedURLException e) {
                } catch (IOException e) {
                }
                    imgRefreshIsBlocked[PAIR_CODE]=false;
                }

                bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(txtLast[PAIR_CODE]!=null){textLast[PAIR_CODE].setText(bm_MainState.getString(R.string.last)+" "+txtLast[PAIR_CODE]);}
                        if(txtLow[PAIR_CODE]!=null){textLow[PAIR_CODE].setText(bm_MainState.getString(R.string.low)+" "+txtLow[PAIR_CODE]);}
                        if(txtHigh[PAIR_CODE]!=null){textHigh[PAIR_CODE].setText(bm_MainState.getString(R.string.high)+" "+txtHigh[PAIR_CODE]);}
                        bm_MainState.setProgressBarIndeterminateVisibility(false);
                    }
                });
                                  /*
                        //CandleStickChartDemo03View charts = new CandleStickChartDemo03View(bm_MainContext,mTitle.toString());
                        //ll.addView(charts);
                        Document doc = null;
                        try {
                            doc = Jsoup.connect("https://btc-e.com/exchange/" + PAIR_ID).get();
                        } catch (IOException e) {
                            Log.e("ERR", "MSG");
                        }
                        Elements ele = doc.select("div#chart_div");

                        final String html = ele.toString();
                        final String mime = "text/html";
                        final String encoding = "utf-8";
                    bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webCharts.loadData(html, mime, encoding);
                        }
                    });    */

            }
        };
        thread.start();


                /*
                rootView = inflater.inflate(R.layout.fragment_main, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                            */
        //textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

        //textView.setText(mTitle);
        return rootView;
    }
    public static void refresh_pair_page(){
        Thread thread = new Thread()
        {
            @Override
            public void run() {
                for(int i=0;i<VARs.pairs_CODE.length;i++){
                    if(VARs.pairs_UI[i].equals(mTitle)){
                        bm_Depth.update_depth(VARs.pairs_CODE[i]);
                        bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bm_MainState.setProgressBarIndeterminateVisibility(true);
                            }
                        });
                        bm_ActiveOrders.getActiveOrders(VARs.pairs_CODE[i]);
                        bm_PairHistory.getHistory(VARs.pairs_CODE[i]);

                        JsonObject Ticker = btce_getTicker.getTickerObj(VARs.pairs_CODE[i]);
                        if(Ticker!=null){
                        bm_Main.txtLast[i] = btce_getTicker.get_last(Ticker);
                        bm_Main.txtLow[i] = btce_getTicker.get_low(Ticker);
                        bm_Main.txtHigh[i] = btce_getTicker.get_high(Ticker);}

                        if(!imgRefreshIsBlocked[i]/* && (DIAGRAM_LAST_REFRESH - (System.currentTimeMillis()/1000))>DIAGRAM_COOLDOWN*/){

                                imgRefreshIsBlocked[i]=true;
                            try {
                                final int PAIR_CODE=i;
                                if(prefs_black_charts){
                                    imgChartsBitmap[i] = invert(BitmapFactory.decodeStream((InputStream) new URL(API_ZLAB_URL+VARs.pairs_CODE[i]+".php").getContent()));
                                    DIAGRAM_IS_BLACK=true;
                                } else {
                                    imgChartsBitmap[i] = BitmapFactory.decodeStream((InputStream) new URL(API_ZLAB_URL+VARs.pairs_CODE[i]+".php").getContent());
                                }
                                //DIAGRAM_LAST_REFRESH =System.currentTimeMillis()/1000;
                                bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imgCharts[PAIR_CODE].setImageBitmap(imgChartsBitmap[PAIR_CODE]);
                                    }
                                });
                            } catch (MalformedURLException e) {
                            } catch (IOException e) {
                            }
                                imgRefreshIsBlocked[i]=false;
                        }
                        final int pos=i;
                        bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(txtLast[pos]!=null){textLast[pos].setText(bm_MainState.getString(R.string.last)+" "+txtLast[pos]);}
                                if(txtLow[pos]!=null){textLow[pos].setText(bm_MainState.getString(R.string.low)+" "+txtLow[pos]);}
                                if(txtHigh[pos]!=null){textHigh[pos].setText(bm_MainState.getString(R.string.high)+" "+txtHigh[pos]);}
                                bm_MainState.setProgressBarIndeterminateVisibility(false);
                            }
                        });
                    }
                }
            }
        };
        thread.start();
    }

    public void getSettigns(){
        prefs_charts_classic = prefs.getBoolean("prefs_charts_classic",true);
        prefs_charts_detailed = prefs.getBoolean("prefs_charts_detailed",true);
        prefs_show_vector = prefs.getBoolean("prefs_show_vector",true);
        prefs_fullscreen = prefs.getBoolean("prefs_fullscreen",false);if(prefs_fullscreen){mDecor_verifier=true;}
        prefs_bottom_actionbar = prefs.getBoolean("prefs_bottom_actionbar",false);
        prefs_black_theme = prefs.getBoolean("prefs_black_theme",false);
        prefs_black_charts = prefs.getBoolean("prefs_black_charts",false);
        API_KEY = prefs.getString("prefs_API_KEY","GMJ4FH97-TZG5KON4-VUO0XOC2-AM2TLCI9-UL90D0OR");
        API_SECRET = prefs.getString("prefs_API_SECRET","4403412b3793c319bffba269d5862992ca5cd400b5cded0ebe3e0ec3a3a8c4d1");
        API_URL_PRIVATE = prefs.getString("prefs_API_URL_PRIVATE","https://btc-e.com/tapi");
        API_URL_PUBLIC = prefs.getString("prefs_API_URL_PUBLIC","https://btc-e.com/api/2/");
        prefs_CHAT = prefs.getBoolean("prefs_CHAT",false);

        if(prefs_CHAT){NOPAIRS_GROUP=3;}else{NOPAIRS_GROUP=2;}
        /*Set<String> prefs_enabled_charts_selections = prefs.getStringSet("prefs_enabled_charts",
                new HashSet<String>(Arrays.asList(
                        "BTC / USD",
                        "BTC / RUR",
                        "BTC / EUR",
                        "LTC / BTC",
                        "LTC / USD",
                        "LTC / RUR",
                        "LTC / EUR",
                        "NMC / BTC",
                        "NMC / USD",
                        "NVC / BTC",
                        "NVC / USD",
                        "USD / RUR",
                        "EUR / USD",
                        "TRC / BTC",
                        "PPC / BTC",
                        "PPC / USD",
                        "FTC / BTC",
                        "XPM / BTC"))); */

        //prefs_enabled_charts = new HashSet<String>(Arrays.asList(VARs.pairs_UI));

        prefs_enabled_charts = prefs.getStringSet("prefs_enabled_charts",new HashSet<String>(Arrays.asList(VARs.pairs_UI)));


        //int c=0;
        /*
        for(int j=0;j<VARs.pairs_UIz.length;j++){
            if(bm_Main.prefs_enabled_charts.contains(VARs.pairs_UIz[j])){
                c++;
            }
        }
                    */
        //chartsEnabled = new boolean[c];
        //bm_Main.pairs_UI = VARs.pairs_UIz;
        //bm_Main.pairs_CODE = VARs.pairs_CODEz;
        //bm_Main.txtLast = new String[c];
        //bm_Main.txtLow = new String[c];
        //bm_Main.txtHigh = new String[c];
        //c=0;

        for(int j=0;j<VARs.pairs_CODE.length;j++){
            if(bm_Main.prefs_enabled_charts.contains(VARs.pairs_UI[j])){
                chartsEnabled[j]=true;
                //bm_Main.pairs_UI[c]=VARs.pairs_UIz[j];
                //bm_Main.pairs_CODE[c]=VARs.pairs_CODEz[j];

                //c++;
            } else {
                chartsEnabled[j]=false;
            }
        }
        //prefs_enabled_charts[]
    }

    public void configUI(){
        if(prefs_fullscreen){
            hideSystemUI();
        }
         /*
        mDecorView.setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int flags) {
                        if(flags==View.SYSTEM_UI_FLAG_VISIBLE&&prefs_fullscreen){
                            //hideSystemUI();
                            //mDecor_isHiden=false;
                        }
                    }
                });   */
    }
    public void setSettings(){
        if(prefs_black_theme){
            setTheme(android.R.style.Theme_Holo);
            if(bm_MainState!=null){bm_MainState.setTheme(android.R.style.Theme_Holo);}
            THEME=android.R.style.Theme_Holo;
        }
        else {
            if(bm_MainState!=null){bm_MainState.setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);}
            setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
            THEME=android.R.style.Theme_Holo_Light_DarkActionBar;
        }
        //this.getTheme();
    }

    private void hideSystemUI() {
        if(currentApiVersion>=19){
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView.setSystemUiVisibility(
                /*View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | */View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        //| View.SYSTEM_UI_FLAG_IMMERSIVE);
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mDecor_isHiden=true;
        prefs_fullscreen=true;prefs.edit().putBoolean("prefs_fullscreen",true).commit();
        } else {
            if(bm_MainContext!=null){ /** Fix NULL when start app on 4.0 **/
                Toast.makeText(bm_MainContext,getResources().getText(R.string.only_kitkat),Toast.LENGTH_LONG).show();}
        }
    }
    private void showSystemUI() {
        mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE
                /*View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN*/);
        mDecor_isHiden=false;
        prefs_fullscreen=false;prefs.edit().putBoolean("prefs_fullscreen", false).commit();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && prefs_fullscreen && !mDecor_isHiden) {
            if(currentApiVersion>=19){
            mDecorView.setSystemUiVisibility(/*View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                  |*/ View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    //| View.SYSTEM_UI_FLAG_IMMERSIVE);
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            mDecor_isHiden=true;
            } else {
                if(bm_MainContext!=null){ /** Fix NULL when start app on 4.0 **/
                Toast.makeText(bm_MainContext,getResources().getText(R.string.only_kitkat),Toast.LENGTH_LONG).show();}
            }
        } else if (!hasFocus) {
            mDecor_isHiden=false;
        }
    }

    public static Bitmap invert(Bitmap original) {
        // Create mutable Bitmap to invert, argument true makes it mutable

        Bitmap inversion = original.copy(Bitmap.Config.ARGB_8888, true);

        // Get info about Bitmap
        int width = inversion.getWidth();
        int height = inversion.getHeight();
        int pixels = width * height;

        // Get original pixels
        int[] pixel = new int[pixels];
        inversion.getPixels(pixel, 0, width, 0, 0, width, height);

        // Modify pixels
        for (int i = 0; i < pixels; i++){
            String color = Integer.toHexString(pixel[i]);
            String r = color.substring(2,4);
            String g = color.substring(4,6);
            String b = color.substring(6,8);
            if(r.equals(g) && g.equals(b)){
                pixel[i] ^= RGB_MASK;
            }
        }
        inversion.setPixels(pixel, 0, width, 0, 0, width, height);
        // Return inverted Bitmap
        return inversion;
    }

    private static void chartsArrayBlank(){
        chartsListElements=new ArrayList<bm_ListElementCharts>();
        // Pair+"*"+Last+"*"+Buy+"*"+Sell+"*"+Updated+"*"+High+"*"+Low;
        FileInputStream fis = null;
        for(int i=0;i<VARs.pairs_CODE.length;i++){
            try {
                    fis = bm_MainState.openFileInput("charts_"+VARs.pairs_CODE[i]+".json");

                    StringBuffer fileContent = new StringBuffer("");
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) != -1) {
                        fileContent.append(new String(buffer));
                    }

                    String[] chartsElementsArray=fileContent.toString().split("&");

                    /** Проверка на out of bound **/
            try{
                    chartsListElements.remove(i);
            } catch (IndexOutOfBoundsException e){}

                    chartsListElements.add(i,new bm_ListElementCharts(
                            chartsElementsArray[0],
                            chartsElementsArray[1],
                            chartsElementsArray[2],
                            chartsElementsArray[3],
                            chartsElementsArray[4],
                            chartsElementsArray[5],
                            chartsElementsArray[6],
                            chartsElementsArray[7],
                            chartsElementsArray[8],
                            chartsElementsArray[9],
                            chartsElementsArray[10]));
                REFRESH_COUNTER++;
            } catch (Exception e) {
                //Log.e("ERR", "MSG");
                chartsListElements.add(new bm_ListElementCharts(VARs.pairs_UI[i],VARs.pairs_CODE[i],"0.00","0.00","0.00","","","","0.00","0.00","0.00"));
            }
        }
    }
    public static void reHide(){
        if(chartsListElementsToShow==null){
            chartsListElementsToShow=bm_ChartsAdaptor.hide(chartsListElements);
        } else {
            chartsListElementsToShow.removeAll(chartsListElementsToShow);
            chartsListElementsToShow.addAll(bm_ChartsAdaptor.hide(chartsListElements));
        }
    }
}
