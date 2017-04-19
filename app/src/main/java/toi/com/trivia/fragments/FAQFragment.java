package toi.com.trivia.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import toi.com.trivia.R;
import toi.com.trivia.adapters.ExpandableListAdapter;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

public class FAQFragment extends Fragment implements TriviaConstants {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    Context mContext;
    HashMap<String, String> listDataChild;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_faq, container, false);

        // get the listview
        expListView = (ExpandableListView) v.findViewById(R.id.faq_list);

        // preparing list data
        prepareListData();


        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.expandGroup(0);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {

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
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {


            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                /*Toast.makeText(
                        getApplicationContext(),
						listDataHeader.get(groupPosition)
								+ " : "
								+ listDataChild.get(
										listDataHeader.get(groupPosition)).get(
										childPosition), Toast.LENGTH_SHORT)
						.show();*/
                return false;
            }
        });


        return v;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + EXIT + "FAQ", "Back Press", TriviaConstants.CLICK);
    }

    /*
         * Preparing the list data
         */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, String>();

        // Adding child data
        listDataHeader.add("What does TOI Trivia mean?");
        listDataHeader.add("How many games can I play in a day ?");
        listDataHeader.add("How long do I get to answer a question ?");
        listDataHeader.add("Can I pause the timer ?");
        listDataHeader.add("How much time does it take to complete one quiz?");
        listDataHeader.add("How will you inform me when a new game starts?");
        listDataHeader.add("How can I move higher in the leaderboard?");
        listDataHeader.add("Can I choose my own topics for the quiz or is it randomised?");
        listDataHeader.add("How do you draw your prize winners ?");
        listDataHeader.add("How will you inform me if I win ?");
        listDataHeader.add("What do you do with my details ?");
        listDataHeader.add("How do I report a bug, glitch or other concern ?");
        listDataHeader.add("What is the eligibility to win daily rewards ?");
        listDataHeader.add("Can I play all the previous games present in the archive ?");
        listDataHeader.add("Is it mandatory to login to play the quiz ?");
        listDataHeader.add("If I miss a daily quiz will I be eligible for monthly or weekly prizes ?");


        listDataChild.put(listDataHeader.get(0), getResources().getString(R.string.faq1).toString()); // Header, Child data
        listDataChild.put(listDataHeader.get(1), getResources().getString(R.string.faq2).toString());
        listDataChild.put(listDataHeader.get(2), getResources().getString(R.string.faq3).toString());
        listDataChild.put(listDataHeader.get(3), getResources().getString(R.string.faq4).toString()); // Header, Child data
        listDataChild.put(listDataHeader.get(4), getResources().getString(R.string.faq5).toString());
        listDataChild.put(listDataHeader.get(5), getResources().getString(R.string.faq6).toString());
        listDataChild.put(listDataHeader.get(6), getResources().getString(R.string.faq7).toString()); // Header, Child data
        listDataChild.put(listDataHeader.get(7), getResources().getString(R.string.faq8).toString());
        listDataChild.put(listDataHeader.get(8), getResources().getString(R.string.faq9).toString());
        listDataChild.put(listDataHeader.get(9), getResources().getString(R.string.faq10).toString()); // Header, Child data
        listDataChild.put(listDataHeader.get(10), getResources().getString(R.string.faq11).toString());
        listDataChild.put(listDataHeader.get(11), getResources().getString(R.string.faq12).toString());
        listDataChild.put(listDataHeader.get(12), getResources().getString(R.string.faq13).toString()); // Header, Child data
        listDataChild.put(listDataHeader.get(13), getResources().getString(R.string.faq14).toString());
        listDataChild.put(listDataHeader.get(14), getResources().getString(R.string.faq15).toString());
        listDataChild.put(listDataHeader.get(15), getResources().getString(R.string.faq16).toString());

    }
}
