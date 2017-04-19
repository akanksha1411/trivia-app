package toi.com.trivia.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.github.androidprogresslayout.ProgressLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import toi.com.trivia.R;
import toi.com.trivia.adapters.PrizesExpandableListAdapter;
import toi.com.trivia.api.APICalls;
import toi.com.trivia.model.PrizesItems;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.utility.CommonUtility;

public class PrizesDaily extends Fragment implements View.OnClickListener {

    private static final String PARAM_GAME_ID = "gameid";
    static ExpandableListView expListView;
    public static PrizesExpandableListAdapter expandableListAdapter, listAdapter;
    static List<String> listDataHeader = new ArrayList<>();
    static Context context;
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static HashMap<String, List<PrizesItems.Prizesss>> listDataChild = new HashMap<>();
    public static PrizesItems.Prizes prizes;
    //  public static HashMap<String,List<PrizesItems.Prizes>> listDataChild = new HashMap<>();
    ReadPref readPref;
    static String UID;
    public static ProgressLayout progressLayout;


    public PrizesDaily() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PrizesDaily newInstance(int sectionNumber) {
        PrizesDaily fragment = new PrizesDaily();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_prizesdaily, container, false);
        context = getActivity();
        readPref = new ReadPref(context);
        UID = readPref.getUID();
        progressLayout = (ProgressLayout) v.findViewById(R.id.progress_layout);
        progressLayout.showProgress();
        PrizesItems items = new PrizesItems();
        expListView = (ExpandableListView) v.findViewById(R.id.lvExp);

        final String currrentGameId = readPref.getCurrentGameId();
        HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_GAME_ID, currrentGameId); // A FOR ANDROID
        map.put("mode", "D");
        map.put("prize_time", CommonUtility.getCurrentTimeStamp());
        APICalls.triviaPrizes(getActivity(), map);

        //  initUI();
        return v;


    }


    public static void initUI() {

        PrizesItems prizeItems = new PrizesItems();
        prizeItems = APICalls.getPrizesItems();
        PrizesItems items = prizeItems;
        try {
            System.out.println("CALLED !!");
            // listDataChild = new ArrayList<>();

            //adding the values in headers list and childs hashmap
            listDataHeader = new ArrayList<String>();
            listDataChild = new HashMap<>();


            if (items.getPrizes().size() != 0) {
                for (int i = 0; i < items.getPrizes().size(); i++) {
                    PrizesItems.Prizes list = items.getPrizes().get(i);
                    String header = list.getTitle().toString();
                    listDataHeader.add(header);

                    //putting haspmap with child quizes to be shown in gridview
                    for (int j = 0; j < list.getPrizesss().size(); j++) {
                        listDataChild.put(header, list.getPrizesss());
                    }
                }
                listAdapter = new PrizesExpandableListAdapter(context, listDataHeader, listDataChild);
                expListView.setAdapter(listAdapter);
                expListView.expandGroup(0);
            }


            expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {

                    // Toast.makeText(getApplicationContext(),
                    // "Group Clicked " + listDataHeader.get(groupPosition),
                    // Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            // Listview Group expanded listener
            expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                @Override
                public void onGroupExpand(int groupPosition) {

                    expListView.setSelectedGroup(groupPosition);
                }
            });

            // Listview Group collasped listener
            expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                @Override
                public void onGroupCollapse(int groupPosition) {

                }
            });

            // Listview on child click listener
            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {


         /*
           Toast.makeText(
          getClass(),
                            listDataHeader.get(groupPosition)
                                    + " : "
                                    + listDataChild.get(
                                    listDataHeader.get(groupPosition)).get(
                                    childPosition), Toast.LENGTH_SHORT).show();
*/

                    return false;
                }
            });

            expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

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


    @Override
    public void onClick(View view) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    //* Preparing the list data


   /* public void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, String[]>();
        // listDataChild = new HashMap<String, String>();

        // Adding child data
        listDataHeader.add("9/12/2016");
        listDataHeader.add("9/12/2016  |   Quiz3");
        listDataHeader.add("9/12/2016  |   Quiz2");
        listDataHeader.add("9/12/2016  |   Quiz1");
        listDataHeader.add("8/12/2016  |   Quiz3");
        listDataHeader.add("8/12/2016  |   Quiz2");
        listDataHeader.add("8/12/2016  |   Quiz1");
        listDataHeader.add("7/12/2016  |   Quiz3");
        listDataHeader.add("7/12/2016  |   Quiz2");
        listDataHeader.add("7/12/2016  |   Quiz1");
        listDataHeader.add("6/12/2016  |   Quiz3");
        listDataHeader.add("6/12/2016  |   Quiz2");
        listDataHeader.add("6/12/2016  |   Quiz1");

        listDataChild.put(listDataHeader.get(0),getResources().getStringArray(R.array.prizes_array)); // Header, Child data
        listDataChild.put(listDataHeader.get(1),getResources().getStringArray(R.array.prizes_array));
        listDataChild.put(listDataHeader.get(2),getResources().getStringArray(R.array.prizes_array));
        listDataChild.put(listDataHeader.get(3), getResources().getStringArray(R.array.prizes_array));
        listDataChild.put(listDataHeader.get(4), getResources().getStringArray(R.array.prizes_array));
        listDataChild.put(listDataHeader.get(5), getResources().getStringArray(R.array.prizes_array));
        listDataChild.put(listDataHeader.get(6), getResources().getStringArray(R.array.prizes_array));
        listDataChild.put(listDataHeader.get(7), getResources().getStringArray(R.array.prizes_array));
        listDataChild.put(listDataHeader.get(8), getResources().getStringArray(R.array.prizes_array));
        listDataChild.put(listDataHeader.get(9), getResources().getStringArray(R.array.prizes_array));
        listDataChild.put(listDataHeader.get(10),getResources().getStringArray(R.array.prizes_array));
        listDataChild.put(listDataHeader.get(11),getResources().getStringArray(R.array.prizes_array));
        listDataChild.put(listDataHeader.get(12),getResources().getStringArray(R.array.prizes_array));

    }
*/

}
