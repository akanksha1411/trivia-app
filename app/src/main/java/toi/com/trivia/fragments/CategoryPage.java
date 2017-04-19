package toi.com.trivia.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import toi.com.trivia.R;
import toi.com.trivia.activities.StartQuiz;
import toi.com.trivia.model.NewGame;
import toi.com.trivia.model.RandamisedPojo;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategoryPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CategoryPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryPage extends Fragment implements View.OnClickListener, TriviaConstants {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ReadPref readPref;
    SavePref savePref;
    private OnFragmentInteractionListener mListener;
    int mCurrentPosition = 0;
    NewGame.Questions questions;
    int isBonus = 0, pageCalled;
    Context mContext;

    public CategoryPage() {
        // Required empty public constructor
    }

    public CategoryPage(int pageCalled) {
        this.pageCalled = pageCalled;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryPage.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryPage newInstance(String param1, String param2) {
        CategoryPage fragment = new CategoryPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_page, container, false);
        try {
            // Inflate the layout for this fragment
            mContext = getActivity();
            readPref = new ReadPref(getActivity().getApplicationContext());
            savePref = new SavePref(getActivity().getApplicationContext());

            mCurrentPosition = readPref.getCurrentPosition();
            List<RandamisedPojo> question_list = StartQuiz.returnRandamizedQues();

            int qid = question_list.get(mCurrentPosition).getQ_id();
            isBonus = question_list.get(mCurrentPosition).getIs_bonus();

            questions = StartQuiz.returnQuestions(String.valueOf(qid));

            initUI(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void initUI(View view) {

        TextView question_text = (TextView) view.findViewById(R.id.question_no);
        //  TextView category_name = (TextView) view.findViewById(R.id.category_name);
        ImageView category_image = (ImageView) view.findViewById(R.id.category_image);
        Button ready_button = (Button) view.findViewById(R.id.ready_button);
        ready_button.setOnClickListener(this);
        //will set it true if the ready button is shown for the 1st time and clicked
        if (!readPref.getIsReadyShown()) {
            ready_button.setVisibility(View.VISIBLE);
        } else {
            ready_button.setVisibility(View.GONE);
        }

        //  category_name.setShadowLayer(30, 0, 0, Color.YELLOW);
        if (isBonus == 0) {
            category_image.setImageDrawable(getIcon(questions.getCatName()));
        } else {
            category_image.setImageDrawable(getResources().getDrawable(R.drawable.bonus));
        }
        question_text.setText(CATEGORY_PAGE_TITLE + (mCurrentPosition + 1));
        //  category_name.setText(questions.g);

        if (readPref.getIsReadyShown() == true) {
            //Open question after 1 second
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    try {
                        if (!CommonUtility.chkString(questions.getqVideo())) {
                            StartQuiz.replaceFragmentWithoutHistory(new QuizScreen(pageCalled), TriviaConstants.SCREEN_TYPE, TriviaConstants.QUESTION_SCREEN, true, StartQuiz.activity);

                        } else {
                            StartQuiz.replaceFragmentWithoutHistory(new VideoFragment(pageCalled), TriviaConstants.SCREEN_TYPE, TriviaConstants.QUESTION_SCREEN, true, StartQuiz.activity);
                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                        StartQuiz.replaceFragmentWithoutHistory(new QuizScreen(pageCalled), TriviaConstants.SCREEN_TYPE, TriviaConstants.QUESTION_SCREEN, true, StartQuiz.activity);

                    }
                }
            }, 1000);

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String uri) {
        if (mListener != null) {
            mListener.onCategoryFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ready_button) {
            savePref.isReadyShown(true);

            if (!CommonUtility.chkString(questions.getqVideo())) {
                StartQuiz.replaceFragmentWithoutHistory(new QuizScreen(pageCalled), TriviaConstants.SCREEN_TYPE, TriviaConstants.QUESTION_SCREEN, true, (StartQuiz) getActivity());

            } else {
                StartQuiz.replaceFragmentWithoutHistory(new VideoFragment(pageCalled), TriviaConstants.SCREEN_TYPE, TriviaConstants.QUESTION_SCREEN, true, (StartQuiz) getActivity());
            }
            //GA ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Category", "Ready", TriviaConstants.CLICK);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onCategoryFragmentInteraction(String uri);
    }


    private Drawable getIcon(String cat_name) {
       /* File sponsorFile = new File(Environment.getExternalStorageDirectory()
                .getPath()
                + "/Android/data/" + getActivity().getPackageName() + "/files/.CatImages/"
                + cat_name + ".jpg");*/
        File sponsorFile = new File(Environment
                .getExternalStorageDirectory().getPath() + "/Android/data/" + getActivity().getPackageName() + "/TOI_TRIVIA/CatImages/"
                + cat_name + ".jpg");
        Drawable sponsor = null;
        if (sponsorFile != null) {
            if (sponsorFile.exists()) {
                try {

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(sponsorFile.getAbsolutePath(), options);

                    sponsor = new BitmapDrawable(getActivity().getResources(), bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        if (sponsor == null) {
            sponsor = getResources().getDrawable(R.drawable.default_category);
        }
        return sponsor;
    }

}
