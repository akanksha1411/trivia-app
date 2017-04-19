package toi.com.trivia.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import toi.com.trivia.R;
import toi.com.trivia.activities.StartQuiz;
import toi.com.trivia.api.APICalls;
import toi.com.trivia.databases.DBController;
import toi.com.trivia.model.AnswersPojo;
import toi.com.trivia.model.NewGame;
import toi.com.trivia.model.RandamisedPojo;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuizScreen.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuizScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizScreen extends Fragment implements TriviaConstants {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    ProgressBar progressBar;
    Thread thread;
    public static MyCountDownTimer myCountDownTimer;
    TextView time_left, cat_name;
    public long maxTime = 10000;
    int max = 10;
    public long mTimeLeft = 0;
    static ReadPref readPref;
    private int progressStatus = 1000;
    SavePref savePref;
    int mCurrentPosition = 0;
    NewGame.Questions questions = new NewGame.Questions();
    NewGame.GameProperties gameProperties = new NewGame.GameProperties();
    List<NewGame.Options> options = new ArrayList<>();
    int qid, set_id, isBonus, pageCalled;
    public static AnswersPojo answersPojo;
    public static int SelectedOPtionId = 0;
    String presentedTime = "";
    public static List<AnswersPojo.Answers> answersList;
    private Handler handler = new Handler();
    public static DBController dbController;
    public static ProgressDialog pd;
    public static String loginID;
    static Context context;
    private FragmentActivity activity;

    public QuizScreen() {
        // Required empty public constructor
    }

    public QuizScreen(int pageCalled) {
        this.pageCalled = pageCalled;
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuizScreen.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizScreen newInstance(String param1, String param2) {
        QuizScreen fragment = new QuizScreen();
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

        presentedTime = String.valueOf(System.currentTimeMillis() / 1000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz_screen, container, false);

        readPref = new ReadPref(getActivity().getApplicationContext());
        savePref = new SavePref(getActivity().getApplicationContext());
        loginID = readPref.getLoginId();
        dbController = new DBController(getActivity().getApplicationContext());
        context = getActivity();
        mCurrentPosition = readPref.getCurrentPosition();
        List<RandamisedPojo> question_list = StartQuiz.returnRandamizedQues();

        qid = question_list.get(mCurrentPosition).getQ_id();
        set_id = question_list.get(mCurrentPosition).getSet_id();
        isBonus = question_list.get(mCurrentPosition).getIs_bonus();
        questions = StartQuiz.returnQuestions(String.valueOf(qid));
        List<NewGame.Options> options2 = dbController.findALLOptions(String.valueOf(qid));
        List<NewGame.Options> newList = dbController.findCorrectOption(String.valueOf(qid));
        if (newList != null) {
            options2.addAll(newList);
            Collections.shuffle(options2);
            options.addAll(options2);
        } else {
            Collections.shuffle(options2);
            options.addAll(options2);
        }

        //maxTime = String.valueOf(questions.getQuesTime());//todo - Max time will be comming from backend

        initUI(view);

        return view;
    }

    private void initUI(View view) {

        cat_name = (TextView) view.findViewById(R.id.cat_name);
        time_left = (TextView) view.findViewById(R.id.time_left);
        TextView ques_no = (TextView) view.findViewById(R.id.ques_no);
        TextView quiz_question = (TextView) view.findViewById(R.id.quiz_question);
        ImageView quiz_image = (ImageView) view.findViewById(R.id.quiz_image);
        progressBar = (ProgressBar) view.findViewById(R.id.timer_bar);

        progressBar.getProgressDrawable().setColorFilter(
                getResources().getColor(R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN);

        // Start long running operation in a background thread
        thread = new Thread(new Runnable() {
            public void run() {
                while (!(progressStatus < 0)) {
                    progressStatus -= 5;

                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);

                        }
                    });
                    try {
                        // Sleep for 50 milliseconds.
                        //Just to display the progress slowly
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        LinearLayout options_list = (LinearLayout) view.findViewById(R.id.list_option_layout);//List view for option

        LinearLayout options_grid = (LinearLayout) view.findViewById(R.id.grid_option_layout);//grid view for option

        //start timer for progress bar and time left display
        myCountDownTimer = new MyCountDownTimer(maxTime, 1000);
        myCountDownTimer.start();

        //assign values to UI
        cat_name.setText(CommonUtility.checkNull(questions.getCatName()));
        ques_no.setText(CommonUtility.checkNull(String.valueOf((mCurrentPosition + 1))));//set question number
        quiz_question.setText(CommonUtility.checkNull(questions.getTitle().toString()));//set question title

        //if image is present show grid options and if without image then show list options
        //if long question without image - Grid option should come
        //set adapter to listview or gridview to display options for each question
        if (questions.getqImage() != null) {
            if (questions.getqImage().length() != 0) {
                //image visible if not null- image present
                if (questions.getTitle().length() >= 90) {
                    quiz_question.setTextSize(18);
                }
                quiz_image.setVisibility(View.VISIBLE);
                openImage(quiz_image);

                if (options != null) {
                    setGridOptionUI(view, options);
                    options_list.setVisibility(View.GONE);
                    options_grid.setVisibility(View.VISIBLE);
                }
            } else {
                //image not present -
                if (questions.getTitle().length() >= 90) {
                    //more than 90 characters
                    quiz_question.setTextSize(20);
                    quiz_question.setPadding(0, 0, 0, 20);
                } else {
                    quiz_question.setTextSize(24);
                    quiz_question.setPadding(0, 0, 0, 15);
                }
                if (options != null) {
                    setListOptionUI(view, options);
                    options_list.setVisibility(View.VISIBLE);
                    options_grid.setVisibility(View.GONE);
                }
                //image hidden as image is not present
                quiz_image.setVisibility(View.GONE);

            }
        } else {
            if (questions.getTitle().length() >= 90) {
                //more than 90 characters
                quiz_question.setTextSize(20);

            } else {
                quiz_question.setTextSize(24);

            }
            if (options != null) {

                setListOptionUI(view, options);
                options_list.setVisibility(View.VISIBLE);
                options_grid.setVisibility(View.GONE);
            }
            //image hidden as image is not present
            quiz_image.setVisibility(View.GONE);

        }


    }

    private void openImage(final ImageView quiz_image) {
        try {
            String fileName = readPref.getCurrentGameId() + "_" + qid;
            File quizImage = new File(Environment
                    .getExternalStorageDirectory().getPath() + "/Android/data/" + getActivity().getPackageName() + "/TOI_TRIVIA/QuizImages/"
                    + fileName + ".jpg");
            if (quizImage.exists()) {
                Log.d("file image", "called");
                Bitmap myBitmap = BitmapFactory.decodeFile(quizImage.getAbsolutePath());
                quiz_image.setImageBitmap(myBitmap);
            } else {
                Log.d("url image", "called");
            /*    Picasso.with(getActivity()).load(CommonUtility.checkNull(questions.getqImage())).transform(new CommonUtility.RoundedTransformation(10, 0))
                        .placeholder(R.drawable.default_post_img)
                        .error(R.drawable.default_post_img)
                        .into(quiz_image);*/
                Glide.with(getActivity())
                        .load(CommonUtility.checkNull(questions.getqImage()))
                        .asBitmap()
                        .placeholder(R.drawable.default_post_img)
                        .error(R.drawable.default_post_img)
                        .into(quiz_image);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Set the options values for grid and handle clicks
     *
     * @param view
     * @param options
     */
    public void setGridOptionUI(View view, final List<NewGame.Options> options) {

        final TextView opt1 = (TextView) view.findViewById(R.id.option_grid1);
        final TextView opt2 = (TextView) view.findViewById(R.id.option_grid2);
        final TextView opt3 = (TextView) view.findViewById(R.id.option_grid3);
        final TextView opt4 = (TextView) view.findViewById(R.id.option_grid4);


        opt1.setText(CommonUtility.checkNull(options.get(0).getName()));
        opt2.setText(CommonUtility.checkNull(options.get(1).getName()));
        opt3.setText(CommonUtility.checkNull(options.get(2).getName()));
        opt4.setText(CommonUtility.checkNull(options.get(3).getName()));

        opt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectedOPtionId == 0) {
                    SelectedOPtionId = options.get(0).getOptId();
                    opt1.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_yellow_top_left));
                    opt1.setTypeface(Typeface.DEFAULT_BOLD);
                    opt1.setTextColor(v.getResources().getColor(R.color.black));
                    QuizScreen.myCountDownTimer.cancel();
                    thread.interrupt();
                    progressStatus = 0;
                    switchNext();
                }
            }
        });

        opt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectedOPtionId == 0) {
                    SelectedOPtionId = options.get(1).getOptId();
                    opt2.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_yellow_top_right));
                    opt2.setTypeface(Typeface.DEFAULT_BOLD);
                    opt2.setTextColor(v.getResources().getColor(R.color.black));
                    QuizScreen.myCountDownTimer.cancel();
                    thread.interrupt();
                    progressStatus = 0;
                    switchNext();

                }
            }
        });
        opt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectedOPtionId == 0) {
                    SelectedOPtionId = options.get(2).getOptId();
                    opt3.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_yellow_bottom_left));
                    opt3.setTypeface(Typeface.DEFAULT_BOLD);
                    opt3.setTextColor(v.getResources().getColor(R.color.black));
                    QuizScreen.myCountDownTimer.cancel();
                    thread.interrupt();
                    progressStatus = 0;
                    switchNext();
                }
            }
        });
        opt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectedOPtionId == 0) {
                    SelectedOPtionId = options.get(3).getOptId();
                    opt4.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_yellow_bottom_right));
                    opt4.setTypeface(Typeface.DEFAULT_BOLD);
                    opt4.setTextColor(v.getResources().getColor(R.color.black));
                    QuizScreen.myCountDownTimer.cancel();
                    thread.interrupt();
                    progressStatus = 0;
                    switchNext();
                }
            }
        });

    }

    private void switchNext() {
        QuizScreen.myCountDownTimer.onFinish();
        //this code addeded a delay of 1sec in shofting to next question
      /*  Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                QuizScreen.myCountDownTimer.onFinish();

            }
        }, 1000);*/
    }

    /**
     * Set the options values for list and handle clicks
     *
     * @param view
     * @param options
     */
    public void setListOptionUI(View view, final List<NewGame.Options> options) {

        final TextView opt1 = (TextView) view.findViewById(R.id.option_text1);
        final TextView opt2 = (TextView) view.findViewById(R.id.option_text2);
        final TextView opt3 = (TextView) view.findViewById(R.id.option_text3);
        final TextView opt4 = (TextView) view.findViewById(R.id.option_text4);


        opt1.setText(CommonUtility.checkNull(options.get(0).getName()));
        opt2.setText(CommonUtility.checkNull(options.get(1).getName()));
        opt3.setText(CommonUtility.checkNull(options.get(2).getName()));
        opt4.setText(CommonUtility.checkNull(options.get(3).getName()));

        opt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectedOPtionId == 0) {
                    SelectedOPtionId = options.get(0).getOptId();
                    selectedAnswer(opt1, v);
                    QuizScreen.myCountDownTimer.cancel();
                    thread.interrupt();
                    progressStatus = 0;
                    switchNext();
                }
            }
        });

        opt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectedOPtionId == 0) {
                    SelectedOPtionId = options.get(1).getOptId();
                    selectedAnswer(opt2, v);
                    QuizScreen.myCountDownTimer.cancel();
                    thread.interrupt();
                    progressStatus = 0;
                    switchNext();
                }
            }
        });
        opt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectedOPtionId == 0) {
                    SelectedOPtionId = options.get(2).getOptId();
                    selectedAnswer(opt3, v);
                    QuizScreen.myCountDownTimer.cancel();
                    thread.interrupt();
                    progressStatus = 0;
                    switchNext();
                }
            }
        });
        opt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectedOPtionId == 0) {
                    SelectedOPtionId = options.get(3).getOptId();
                    selectedAnswer(opt4, v);
                    QuizScreen.myCountDownTimer.cancel();
                    thread.interrupt();
                    progressStatus = 0;
                    switchNext();
                }
            }
        });


    }

    /**
     * Change UI of the option selected
     *
     * @param option_text
     * @param v
     */
    private void selectedAnswer(TextView option_text, View v) {
        option_text.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_yellow));
        option_text.setTypeface(Typeface.DEFAULT_BOLD);
        option_text.setTextColor(v.getResources().getColor(R.color.black));
    }

    /**
     * timer class for progress bar
     */
    public class MyCountDownTimer extends CountDownTimer {
        int i = Integer.parseInt(Long.toString(maxTime));

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            max--;
            i--;
            mTimeLeft = millisUntilFinished;
            time_left.setText(String.valueOf(max));

        }

        @Override
        public void onFinish() {
            i--;

            setPoints(questions.getQuesId());
            int count = StartQuiz.randamized_ques.size();
            if (count != (mCurrentPosition + 1)) {
                int n = mCurrentPosition + 1;
                savePref.saveCurrentPosition(mCurrentPosition + 1);
                StartQuiz.replaceFragmentWithoutHistory(new CategoryPage(), TriviaConstants.SCREEN_TYPE, TriviaConstants.CATEGORY_PAGE, true, (StartQuiz) getContext());
            } else {

                AnswersPojo pojo = StartQuiz.answersPojo;
                savePref.saveCurrentPosition(0);
                savePref.isReadyShown(false);

                Log.d("pojo", pojo.toString());
                if (pageCalled == GAME_ARCHIVE) {
                    StartQuiz.activity.finish();
                } else {
                    submitAnswers(pojo);
                }


            }
        }
    }

    /**
     * Submit answer to server
     *
     * @param data
     */
    public static void submitAnswers(AnswersPojo data) {
        ReadPref readPref = new ReadPref(StartQuiz.activity);
        try {
            pd = new ProgressDialog(StartQuiz.activity);
            pd.setCancelable(false);
            pd.setMessage("Answer submission \nin progress...");
            pd.show();

            HashMap<String, String> map = new HashMap<>();
            map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
            map.put(PARAM_UQID, CommonUtility.getIMEI(StartQuiz.activity));
            map.put(PARAM_UID, readPref.getUID()); // A FOR ANDROID
            map.put(PARAM_ISLOGGEDIN, String.valueOf(DEFAULT_ONE));
            map.put(PARAM_LOGIN_TYPE, DEFAULT_LOGIN_TYPE);
            map.put(PARAM_GAME_ID, String.valueOf(data.getGameId())); // A FOR ANDROID
            map.put(PARAM_SUBMIT_TIME, String.valueOf((System.currentTimeMillis() / 1000)));
            map.put(PARAM_LOGIN_ID, String.valueOf(readPref.getLoginId()));

            JSONArray answers = new JSONArray();
            if (data.getAnswers_list() != null) {
                for (int i = 0; i < data.getAnswers_list().size(); i++) {
                    JSONObject jobj = new JSONObject();
                    AnswersPojo.Answers ans = data.getAnswers_list().get(i);
                    try {
                        jobj.put(PARAM_QUES_ID, ans.getQuesId());
                        jobj.put(PARAM_TIME_TAKEN, ans.getTimeTaken());
                        jobj.put(PARAM_OPTION_ID, ans.getUserOptId());
                        jobj.put(PARAM_PRESENTED_TIME, ans.getPresentedTime());
                        jobj.put(PARAM_USER_OPT_SEQ, ans.getUserOptSeq());
                        jobj.put(PARAM_QUES_STATE, ans.getqState());
                        jobj.put(PARAM_QUES_SEQ, ans.getUserQuesSeq());
                        answers.put(i, jobj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            map.put(PARAM_ANSWERS, String.valueOf(answers));

            Log.d("answers--", String.valueOf(answers));

            APICalls.SubmitAnswers(StartQuiz.activity, map,1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onQuizSCreenFragmentInteraction(uri);
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
        SelectedOPtionId = 0;
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

        public void onQuizSCreenFragmentInteraction(Uri uri);
    }

    /**
     * here the answer feed is set for sending in api
     *
     * @param quesId
     */
    private void setPoints(int quesId) {
        AnswersPojo.Answers answers = new AnswersPojo().getAnswers();
        //qstate set to 2 as the aswer is not marked
        long mTimeTaken = maxTime - mTimeLeft;
        answers.setTimeTaken(mTimeTaken);//set time taken to answer this question
        answers.setQuesId(quesId);
        answers.setPresentedTime(presentedTime);

        List<String> optseq = new ArrayList<>();
        for (int i = 0; i < options.size(); i++) {
            optseq.add(String.valueOf(options.get(i).getOptId()));
        }
        answers.setUserOptSeq(CommonUtility.commasSeparatedArray(optseq));
        answers.setUserQuesSeq(String.valueOf(mCurrentPosition));

        if (SelectedOPtionId == 0) {
            answers.setUserOptId(DEFAULT_ZERO);
            answers.setqState(TriviaConstants.PRESENTED_STATE);
        } else {
            answers.setUserOptId(SelectedOPtionId);
            answers.setqState(TriviaConstants.ANSWERED_STATE);
        }
        StartQuiz.answersList.add(answers);


        StartQuiz.answersPojo.setAnswers_list(StartQuiz.answersList);

    }


}
