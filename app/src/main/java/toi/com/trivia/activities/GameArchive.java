package toi.com.trivia.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.github.androidprogresslayout.ProgressLayout;

import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import toi.com.trivia.R;
import toi.com.trivia.adapters.ArchiveListAdapter;
import toi.com.trivia.api.APICalls;
import toi.com.trivia.model.ArchiveItems;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

public class GameArchive extends AppCompatActivity implements TriviaConstants {
    private static final String TAG = "GameArchive";
    static ArchiveListAdapter listAdapter;
    static ExpandableListView expListView;
    static List<String> listDataHeader = new ArrayList<>();
    static HashMap<String, List<ArchiveItems.Result>> listDataChild = new HashMap<>();
    ReadPref readPref;
    static String UID;
    public static ProgressLayout progressLayout;
    public static AppCompatActivity activity;

    public static Context context;
    BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!CommonUtility.haveNetworkConnection(context)) {
                Snackbar.make(getWindow().getDecorView(), TriviaConstants.No_INTERNET, Snackbar.LENGTH_SHORT).show();
            } else {


                ReadPref readPref = new ReadPref(context);
                SavePref savePref=new SavePref(context);
                String mapString = readPref.getUserAnswer();
                // use properties to restore the map
                HashMap<String, String> map2 = new HashMap<String, String>();

                if (mapString.length() != 0) {
                    Properties props = new Properties();
                    try {
                        props.load(new StringReader(mapString.substring(1, mapString.length() - 1).replace(", ", "\n")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (Map.Entry<Object, Object> e : props.entrySet()) {
                        map2.put((String) e.getKey(), (String) e.getValue());
                    }
                    map2 = CommonUtility.loadMap(mapString);

                    savePref.saveUserAnswer("");

                    APICalls.SubmitAnswers((AppCompatActivity) context, map2, 0);//flag=0 for checking request
                }

            }
        }


    };




    @Override
    protected void onStart() {
        super.onStart();

        Bundle b = getIntent().getExtras();
        int screenType = b.getInt(SCREEN_TYPE);

        if (screenType == GAME_END) {
            //GA entry ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + ENTRY + "Game Archive", "Game End Page", CLICK);
        } else if (screenType == RESULT_OUT_SCREEN) {
            //GA entry ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + ENTRY + "Game Archive", "Result Announced Page", CLICK);
        } else if (screenType == DASHBOARD_SCREEN) {
            //GA entry ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + ENTRY + "Game Archive", "Dashboard", CLICK);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_archive);
        readPref = new ReadPref(getApplicationContext());
        UID = readPref.getUID();
        activity = this;
        CommonUtility.fetchArchive(getApplicationContext(), String.valueOf(UID), TriviaConstants.GAME_END);
        //register reciever for network connection change
        initNetworkStateReciever();
        progressLayout = (ProgressLayout) findViewById(R.id.progress_layout);
        progressLayout.showProgress();
        context = getApplicationContext();
        final String sponsorName = readPref.getSponsorName();
        initToolbar();
        CommonUtility.initBackground(getWindow().getDecorView().getRootView(), getApplicationContext(), GAME_ARCHIVE, sponsorName);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

    }

    /**
     * Gets the Content from api and called from the api success response
     * sets the value to respective sections
     *
     * @param items
     */
    public static void initUI(ArchiveItems items) {
        ArchiveItems archiveItems = new ArchiveItems();
        archiveItems = APICalls.getArchiveItems();
        items = archiveItems;
        try {
            listDataHeader = new ArrayList<String>();
            listDataChild = new HashMap<>();

            //adding the values in headers list and childs hashmap
            for (int i = 0; i < items.getGames().size(); i++) {
                ArchiveItems.Games list = items.getGames().get(i);
                String header = list.getDate().toString() + " " + " | " + "PLAYED:" + (list.getResult().size() - list.getGameRemain()) + " " + "| GAME REMAINING: " + String.valueOf(list.getGameRemain());
                listDataHeader.add(header);

                //putting haspmap with child quizes to be shown in gridview
                for (int j = 0; j < list.getResult().size(); j++) {
                    listDataChild.put(header, list.getResult());
                }
            }
            listAdapter = new ArchiveListAdapter(activity, listDataHeader, listDataChild, UID);
            // setting list adapter
            expListView.setAdapter(listAdapter);
            //added so that 0 position is always open when you open archive page
            expListView.expandGroup(0);

            // Listview Group click listener
            expListView.setOnGroupClickListener(new OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {

                    TextView groupIndicator = (TextView) v.findViewById(R.id.archive_text);
                    if (parent.isGroupExpanded(groupPosition)) {
                        parent.collapseGroup(groupPosition);
                        groupIndicator.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow_24, 0);
                    } else {
                        parent.expandGroup(groupPosition);
                        groupIndicator.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up_arrow_24, 0);
                    }

                    return true;
                }
            });

            progressLayout.showContent();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * set up toolbar for game archive page
     */
    public void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitle(getResources().getString(R.string.game_archive));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //GA entry ANALYTICS
                CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + EXIT + "Trivia Game Archive", BACK_ARROW, TriviaConstants.CLICK);
            }
        });
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arow);
    }

    /**
     * init receiver for network connction change
     */
    protected void initNetworkStateReciever() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(networkStateReceiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //destroy the receiver for network connection
        unregisterReceiver(networkStateReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //GA entry ANALYTICS
        CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + EXIT + "Trivia Game Archive", DEVICE_BACK, TriviaConstants.CLICK);
    }
}