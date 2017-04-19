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
import toi.com.trivia.adapters.PrizesExpandableListAdapter3;
import toi.com.trivia.api.APICalls;
import toi.com.trivia.model.PrizesItems;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.utility.CommonUtility;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PrizesMonth.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PrizesMonth#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrizesMonth extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static ExpandableListView expListView;
    public static PrizesExpandableListAdapter prizesExpandableListAdapter, listAdapter;
    private static List<String> listDataHeader = new ArrayList<>();
    public static HashMap<String, List<PrizesItems.Prizesss>> listDataChild = new HashMap<>();
    public static Context context;
    ReadPref readPref;
    private String UID;
    private static ProgressLayout progressLayout;

    public PrizesMonth() {
        // Required empty public constructor
    }

    public static PrizesMonth newInstance(int sectionNumber) {
        PrizesMonth fragment = new PrizesMonth();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readPref = new ReadPref(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity();
        readPref = new ReadPref(context);
        UID = readPref.getUID();

        View view = inflater.inflate(R.layout.fragment_prizesdaily, container, false);
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        progressLayout = (ProgressLayout) view.findViewById(R.id.progress_layout);
        progressLayout.showProgress();

        final String currrentGameId = readPref.getCurrentGameId();
        HashMap<String, String> map = new HashMap<>();
        // map.put(PARAM_GAME_ID, currrentGameId);// A FOR ANDROID
        map.put("mode", "M");
        map.put("prize_time", CommonUtility.getCurrentTimeStamp());
        APICalls.triviaPrizes(getActivity(), map);
        //initUI();
        return view;


    }


    public static void initUI() {

        PrizesItems prizesItems = new PrizesItems();
        prizesItems = APICalls.getPrizesItemsMonthy();
        PrizesItems items = prizesItems;
        try {
            System.out.println("CALLED !!");
            // listDataChild = new ArrayList<>();

            listDataHeader = new ArrayList<String>();
            listDataChild = new HashMap<>();
            //adding the values in headers list and childs hashmap

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
            }
            listAdapter = new PrizesExpandableListAdapter(context, listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
            expListView.expandGroup(0);

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

        }


    }


    @Override
    public void onClick(View view) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

   /* public void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, String[]>();

        // Adding child data
        listDataHeader.add("December 2016");
        listDataHeader.add("November 2016");
        listDataHeader.add("October 2016");

        listDataChild.put(listDataHeader.get(0), getResources().getStringArray(R.array.prizes_array)); // Header, Child data
        listDataChild.put(listDataHeader.get(1), getResources().getStringArray(R.array.prizes_array));
        listDataChild.put(listDataHeader.get(2), getResources().getStringArray(R.array.prizes_array));// Header, Child data

    }*/
}
