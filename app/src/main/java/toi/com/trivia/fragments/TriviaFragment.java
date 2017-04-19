package toi.com.trivia.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import toi.com.trivia.utility.ui.TriviaLoginView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TriviaFragment extends Fragment {


    public TriviaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = new TriviaLoginView(getActivity().getApplicationContext());
        return view;
    }


    public static Fragment newInstance() {
        Fragment frg = new TriviaFragment();
        return frg;
    }
}
