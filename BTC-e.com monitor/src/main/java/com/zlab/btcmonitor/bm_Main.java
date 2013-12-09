package com.zlab.btcmonitor;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.*;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.zlab.btcmonitor.UI.navDrawer;
import com.zlab.btcmonitor._API.VARs;
import com.zlab.btcmonitor.adaptors.*;
import com.zlab.btcmonitor.elements.bm_ListElementCharts;
import com.zlab.btcmonitor.elements.bm_ListElementOrder;
import com.zlab.btcmonitor.elements.bm_ListElementsDepth;
import com.zlab.btcmonitor.elements.bm_ListElementsHistory;
import com.zlab.btcmonitor.workers.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class bm_Main extends Activity
        implements navDrawer.NavigationDrawerCallbacks {

    public static navDrawer mBmNavDrawer;
    public static bm_Main bm_MainState;
    public static Context bm_MainContext;
    private static CharSequence mTitle;

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

    /** === Списки === **/
    /** Курсы **/
    public static ListView chartsList;
    public static bm_ChartsAdaptor chartsAdaptor;
    public static List<bm_ListElementCharts> chartsListElements;
    public static List<bm_ListElementCharts> chartsListElementsOld;
    public static List<String> chartsListDiff,chartsListDiffSell,chartsListDiffBuy;
    public static boolean chartsBlocked=false;
    /** Финансы **/
    public static bm_FundsAdaptor fundsAdaptor;
    public static List<bm_ListElementCharts> fundsListElements;
    public static ListView fundsList;
    public static boolean fundsBlocked=false;
    /** Пары **/
    public static ListView pairAskList;
    public static ListView pairBidsList;
    public static List<bm_ListElementsDepth>[] pairAskElements;
    public static List<bm_ListElementsDepth>[] pairBidsElements;
    public static bm_DepthAdaptor[] pairAskAdaptor;
    public static bm_DepthAdaptor[] pairBidsAdaptor;
    public static Button btn_Orders,btn_Buy,btn_Sell,btn_History;
    public static ImageView imgCharts;
    public static Bitmap[] imgChartsBitmap;
    public static boolean imgRefreshIsBlocked;
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SETTINGS_IS_OPENED){
            SETTINGS_IS_OPENED=false;
            getSettigns();
            if(chartsAdaptor!=null){chartsAdaptor.notifyDataSetChanged();}
            if(fundsAdaptor!=null){fundsAdaptor.notifyDataSetChanged();}
            /*
            if(prefs_fullscreen){hideSystemUI();}else{mDecorView.getRootView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showSystemUI();
            }},250);}*/
            if(imgCharts!=null){
                for(int i=0;i<VARs.pairs_UI.length;i++)
                {
                        imgChartsBitmap[i]=BitmapFactory.decodeResource(getResources(), R.drawable.charts_loading);
                        imgCharts.setImageBitmap(imgChartsBitmap[i]);
                }
            }
            refresh_pair_page();
        }
        if(prefs_fullscreen){hideSystemUI();}else{mDecorView.getRootView().postDelayed(new Runnable() {
            @Override
            public void run() {
                showSystemUI();
            }},250);}
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
    public void onSectionAttached(int number) { /** WTF is this?  Idiot! **/ /** No, seriously! WHAT IS THIS SHIT? **/
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_charts);
                break;
            case 2:
                mTitle = getString(R.string.title_office);
                break;
            case 3:
                mTitle = VARs.pairs_UI[0];
                break;
            case 4:
                mTitle = VARs.pairs_UI[1];
                break;
            case 5:
                mTitle = VARs.pairs_UI[2];
                break;
            case 6:
                mTitle = VARs.pairs_UI[3];
                break;
            case 7:
                mTitle = VARs.pairs_UI[4];
                break;
            case 8:
                mTitle = VARs.pairs_UI[5];
                break;
            case 9:
                mTitle = VARs.pairs_UI[6];
                break;
            case 10:
                mTitle = VARs.pairs_UI[7];
                break;
            case 11:
                mTitle = VARs.pairs_UI[8];
                break;
            case 12:
                mTitle = VARs.pairs_UI[9];
                break;
            case 13:
                mTitle = VARs.pairs_UI[10];
                break;
            case 14:
                mTitle = VARs.pairs_UI[11];
                break;
            case 15:
                mTitle = VARs.pairs_UI[12];
                break;
            case 16:
                mTitle = VARs.pairs_UI[13];
                break;
            case 17:
                mTitle = VARs.pairs_UI[14];
                break;
            case 18:
                mTitle = VARs.pairs_UI[15];
                break;
            case 19:
                mTitle = VARs.pairs_UI[16];
                break;
            case 20:
                mTitle = VARs.pairs_UI[17];
                break;
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

                /*
                View layer = getLayoutInflater().inflate(R.layout.about,null);
                PopupWindow pw = new PopupWindow(bm_MainContext);
                pw.setContentView(layer);
                pw.showAtLocation(mDecorView.getRootView(),Gravity.CENTER,0,0);*/
                return true;
            }
            case R.id.action_refresh:{
                if(mTitle.equals(getResources().getString(R.string.title_charts))){
                    Thread thread = new Thread()
                    {
                        @Override
                        public void run() {
                            bm_Charts.update_charts();
                        }
                    };
                    thread.start();
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

    public static View pageOffice(ViewGroup container, LayoutInflater inflater){
        View rootView = inflater.inflate(R.layout.office, container, false);
        if(fundsListElements!=null){
            //fundsAdaptor = new bm_ChartsAdaptor(bm_MainContext,R.layout.charts_list_item, fundsListElements);
            //fundsList.setAdapter(fundsAdaptor);
                    /*if(fundsList==null){*/fundsList = (ListView) rootView.findViewById(R.id.FundsList);//}
            //if(fundsAdaptor==null){
            fundsAdaptor = new bm_FundsAdaptor(bm_MainContext,R.layout.charts_list_item,fundsListElements);
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
                navDrawer.selectItem(position+2);
                //bm_MainState.onNavigationDrawerItemSelected(position+2);
                mTitle=VARs.pairs_UI[position];
                bm_MainState.invalidateOptionsMenu();
            }
        });

        if(chartsListElements==null){chartsArrayBlank();}
        if(chartsAdaptor==null){
            chartsAdaptor = new bm_ChartsAdaptor(bm_MainContext,R.layout.charts_list_item,chartsListElements);
        }

        chartsList.postDelayed(new Runnable() {
            @Override
            public void run() {
                chartsList.setAdapter(chartsAdaptor);
            }
        },250);

        Thread thread = new Thread()
        {
            @Override
            public void run() {
                    bm_Charts.update_charts();
            }
        };
        thread.start();
        return rootView;
    }
    public static View pagePairs(ViewGroup container, LayoutInflater inflater){
        View rootView = inflater.inflate(R.layout.pairs, container, false);

        btn_Buy = (Button) rootView.findViewById(R.id.btnBuy);
        btn_Sell = (Button) rootView.findViewById(R.id.btnSell);
        btn_Orders = (Button) rootView.findViewById(R.id.btnOrders);
        btn_History = (Button) rootView.findViewById(R.id.btnHistory);
        imgCharts = (ImageView) rootView.findViewById(R.id.imgCharts);
        //webCharts = (WebView) rootView.findViewById(R.id.webCharts);

        //int pair_code=-1;
        int pair_code=0; /** Fix crash on back button and resume **/
        for(int i=0;i<VARs.pairs_UI.length;i++)
        {if(VARs.pairs_UI[i].equals(mTitle)){
            pair_code=i;
        }}
        final int PAIR_CODE = pair_code;
        final String PAIR_ID = VARs.pairs_CODE[PAIR_CODE];

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
                textTradeTotal.setText("0.0" + " " + mTitle.toString().split(" / ")[1]);
                textTradeTax.setText("0.0" + " " + mTitle.toString().split(" / ")[0]);

                editTradeAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            Double total = Double.parseDouble(editTradeAmount.getText().toString()) * Double.parseDouble(editTradePrice.getText().toString());
                            Double tax = Double.parseDouble(editTradeAmount.getText().toString()) * 0.002;
                            textTradeTotal.setText(total.toString() + " " + mTitle.toString().split(" / ")[1]);
                            textTradeTax.setText(tax.toString() + " " + mTitle.toString().split(" / ")[0]);
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
                            textTradeTotal.setText(total.toString() + " " + mTitle.toString().split(" / ")[1]);
                            textTradeTax.setText(tax.toString() + " " + mTitle.toString().split(" / ")[0]);
                        } catch (Exception e ){}
                    }
                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                for(int i=0;i<VARs.pairs_UI.length;i++)
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

                for(int i=0;i<VARs.pairs_UI.length;i++)
                {if(VARs.pairs_UI[i].equals(mTitle)){
                    editTradePrice.setText(pairAskElements[i].get(0).getPrice());
                    textTradeAmount.setText(textTradeAmount.getText()+" "+mTitle.toString().split(" / ")[0]+":");
                    textTradePrice.setText(textTradePrice.getText()+" "+mTitle.toString().split(" / ")[0]+":");
                }}

                textTradeTotal.setText("0.0" + " " + mTitle.toString().split(" / ")[1]);
                textTradeTax.setText("0.0" + " " + mTitle.toString().split(" / ")[1]);

                editTradeAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                        Double total = Double.parseDouble(editTradeAmount.getText().toString()) * Double.parseDouble(editTradePrice.getText().toString());
                        Double tax = total * 0.002;
                            textTradeTotal.setText(total.toString() + " " + mTitle.toString().split(" / ")[1]);
                            textTradeTax.setText(tax.toString() + " " + mTitle.toString().split(" / ")[1]);
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
                            textTradeTotal.setText(total.toString() + " " + mTitle.toString().split(" / ")[1]);
                            textTradeTax.setText(tax.toString() + " " + mTitle.toString().split(" / ")[1]);
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

        if(imgChartsBitmap[PAIR_CODE]!=null){imgCharts.setImageBitmap(imgChartsBitmap[PAIR_CODE]);}

        bm_Main.orderElements = new ArrayList<bm_ListElementOrder>();
        Thread thread = new Thread()
        {
            @Override
            public void run() {
                    bm_Depth.update_depth(PAIR_ID);

                bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bm_MainState.setProgressBarIndeterminateVisibility(true);
                    }
                });
                    bm_ActiveOrders.getActiveOrders(PAIR_ID);
                    bm_PairHistory.getHistory(PAIR_ID);

                if(!imgRefreshIsBlocked/* && (DIAGRAM_LAST_REFRESH - (System.currentTimeMillis()/1000))>DIAGRAM_COOLDOWN*/){
                    imgRefreshIsBlocked=true;
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
                            imgCharts.setImageBitmap(imgChartsBitmap[PAIR_CODE]);
                        }
                    });
                } catch (MalformedURLException e) {
                } catch (IOException e) {
                }
                    imgRefreshIsBlocked=false;
                }
                bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                for(int i=0;i<VARs.pairs_UI.length;i++){
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

                        if(!imgRefreshIsBlocked/* && (DIAGRAM_LAST_REFRESH - (System.currentTimeMillis()/1000))>DIAGRAM_COOLDOWN*/){

                                imgRefreshIsBlocked=true;
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
                                        imgCharts.setImageBitmap(imgChartsBitmap[PAIR_CODE]);
                                    }
                                });
                            } catch (MalformedURLException e) {
                            } catch (IOException e) {
                            }
                                imgRefreshIsBlocked=false;
                        }
                        bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
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
        API_KEY = prefs.getString("prefs_API_KEY","UTGST9S5-KJU056A3-OLRLAPNL-DWQCIC05-UJT5UASQ");
        API_SECRET = prefs.getString("prefs_API_SECRET","93356012c426dfa50528757c0ffc9707db13477c81b34f332e9350a2f81adc1c");
        API_URL_PRIVATE = prefs.getString("prefs_API_URL_PRIVATE","https://btc-e.com/tapi");
        API_URL_PUBLIC = prefs.getString("prefs_API_URL_PUBLIC","https://btc-e.com/api/2/");
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
        chartsListElements.add(new bm_ListElementCharts("BTC / USD","0.00","0.00","0.00","","",""));
        chartsListElements.add(new bm_ListElementCharts("BTC / RUR","0.00","0.00","0.00","","",""));
        chartsListElements.add(new bm_ListElementCharts("BTC / EUR","0.00","0.00","0.00","","",""));
        chartsListElements.add(new bm_ListElementCharts("LTC / BTC","0.00","0.00","0.00","","",""));
        chartsListElements.add(new bm_ListElementCharts("LTC / USD","0.00","0.00","0.00","","",""));
        chartsListElements.add(new bm_ListElementCharts("LTC / RUR","0.00","0.00","0.00","","",""));
        chartsListElements.add(new bm_ListElementCharts("LTC / EUR","0.00","0.00","0.00","","",""));
        chartsListElements.add(new bm_ListElementCharts("NMC / BTC","0.00","0.00","0.00","","",""));
        chartsListElements.add(new bm_ListElementCharts("NMC / USD","0.00","0.00","0.00","","",""));
        chartsListElements.add(new bm_ListElementCharts("NVC / BTC","0.00","0.00","0.00","","",""));
        chartsListElements.add(new bm_ListElementCharts("NVC / USD","0.00","0.00","0.00","","",""));
        chartsListElements.add(new bm_ListElementCharts("USD / RUR","0.00","0.00","0.00","","",""));
        chartsListElements.add(new bm_ListElementCharts("EUR / USD","0.00","0.00","0.00","","",""));
        chartsListElements.add(new bm_ListElementCharts("TRC / BTC","0.00","0.00","0.00","","",""));
        chartsListElements.add(new bm_ListElementCharts("PPC / BTC","0.00","0.00","0.00","","",""));
        chartsListElements.add(new bm_ListElementCharts("PPC / USD","0.00","0.00","0.00","","",""));
        chartsListElements.add(new bm_ListElementCharts("FTC / BTC","0.00","0.00","0.00","","",""));
        chartsListElements.add(new bm_ListElementCharts("XPM / BTC","0.00","0.00","0.00","","",""));

        // Pair+"*"+Last+"*"+Buy+"*"+Sell+"*"+Updated+"*"+High+"*"+Low;
        FileInputStream fis = null;
        try {
            for(int i=0;i<VARs.pairs_CODE.length;i++){
                fis = bm_MainState.openFileInput("charts_"+VARs.pairs_CODE[i]+".json");

                StringBuffer fileContent = new StringBuffer("");
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) != -1) {
                    fileContent.append(new String(buffer));
                }

                String[] chartsElementsArray=fileContent.toString().split("&");

                chartsListElements.remove(i);
                chartsListElements.add(i,new bm_ListElementCharts(
                        chartsElementsArray[0],
                        chartsElementsArray[1],
                        chartsElementsArray[2],
                        chartsElementsArray[3],
                        chartsElementsArray[4],
                        chartsElementsArray[5],
                        chartsElementsArray[6]));
            }
            REFRESH_COUNTER++;
        } catch (Exception e) {
            //Log.e("ERR", "MSG");
        }


    }
}