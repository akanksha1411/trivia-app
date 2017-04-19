package toi.com.trivia.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.ads.doubleclick.PublisherAdView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import toi.com.trivia.R;
import toi.com.trivia.databases.DBController;
import toi.com.trivia.fragments.CategoryPage;
import toi.com.trivia.fragments.QuizScreen;
import toi.com.trivia.model.AnswersPojo;
import toi.com.trivia.model.NewGame;
import toi.com.trivia.model.RandamisedPojo;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

public class StartQuiz extends AppCompatActivity implements QuizScreen.OnFragmentInteractionListener, CategoryPage.OnFragmentInteractionListener, TriviaConstants {

    public static List<RandamisedPojo> randamized_ques = new ArrayList<>();

    public static NewGame.GameProperties properties;

    List<NewGame.Options> options = new ArrayList<>();
    int qid, set_id, isBonus;
    public static AnswersPojo answersPojo;
    ReadPref readPref;
    SavePref savePref;
    int mCurrentPosition = 0;
    public static List<AnswersPojo.Answers> answersList;
    public static DBController dbController;
    public static AppCompatActivity activity;
    String uid;

    @Override
    protected void onStart() {

        Log.d("onStart -quiz", "###########################");
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_quiz);
        Log.d("onCreate -quiz", "###########################");
        dbController = new DBController(getApplicationContext());
        activity = this;
        readPref = new ReadPref(getApplicationContext());
        uid = readPref.getUID();
        savePref = new SavePref(getApplicationContext());
        final String sponsorName = readPref.getSponsorName();
        //set background for the screen
        CommonUtility.initBackground(getWindow().getDecorView().getRootView(), getApplicationContext(), TriviaConstants.START_QUIZ_ACTIVITY, sponsorName);
        mCurrentPosition = readPref.getCurrentPosition();

        Bundle bundle = getIntent().getExtras();
        int pageCalled = 0;
        if (bundle != null) {
            pageCalled = bundle.getInt("pageCalled");
        }
        try {
            properties = dbController.findGameProperties();
            if (properties.getQuesAlgo().equals(QUESTION_ALGO_RANDOM)) {
                randamized_ques = dbController.findRandamizedQuestions("");
            } else {
                randamized_ques = dbController.findFixedQuestions("");
            }
            List<RandamisedPojo> randamized_ques1 = dbController.findRandamizedQuestionsForBonus("");
            randamized_ques.addAll(randamized_ques1);

            qid = randamized_ques.get(mCurrentPosition).getQ_id();
            set_id = randamized_ques.get(mCurrentPosition).getSet_id();
            isBonus = randamized_ques.get(mCurrentPosition).getIs_bonus();

            //pojo to save answers and set on Server
            answersPojo = new AnswersPojo();
            answersList = new ArrayList<>();
            if (pageCalled != GAME_ARCHIVE) {
                //if the request is made to current game
                answersPojo.setSetId(String.valueOf(readPref.getCurrentGameId()));
                answersPojo.setGameId(Integer.parseInt(readPref.getCurrentGameId()));
            } else {
                //if request for game is from archieve page then archieve game id will be passed
                answersPojo.setSetId(String.valueOf(readPref.getArchiveGameId()));
                answersPojo.setGameId(Integer.parseInt(readPref.getArchiveGameId()));
            }
            answersPojo.setUid(Integer.parseInt(readPref.getUID()));//uid of the drupal user saved from registration
            answersPojo.setDeviceType(DEVICE_TYPE);
            answersPojo.setIsLoggedIn(DEFAULT_ZERO);
            answersPojo.setUqid(CommonUtility.getIMEI(getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        final PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);

        CommonUtility.initADs(mPublisherAdView);

        if (savedInstanceState == null) {
            replaceFragmentWithoutHistory(new CategoryPage(pageCalled), TriviaConstants.SCREEN_TYPE, TriviaConstants.CATEGORY_PAGE, true, this);
        }

    }


    /**
     * Adds 1st fragment to activity without being added to the history stack
     *
     * @param fragment
     * @param bundleParameterName
     * @param bundleValue
     * @param isUsedBundle
     */
    public static void replaceFragmentWithoutHistory(Fragment fragment, String bundleParameterName, int bundleValue,
                                                     boolean isUsedBundle, AppCompatActivity context) {
        if (isUsedBundle) {
            Bundle args = new Bundle();
            args.putInt(bundleParameterName, bundleValue);
            fragment.setArguments(args);
        }
        if (context != null) {
            try {
                FragmentTransaction transaction =
                        context.getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                transaction.replace(R.id.quiz_container, fragment);
                transaction.commitAllowingStateLoss();

            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds 1st fragment to activity without being added to the history stack
     *
     * @param fragment
     * @param bundleParameterName
     * @param bundleValue
     * @param isUsedBundle
     */
    public static void replaceFragmentHistory(Fragment fragment, String bundleParameterName, int bundleValue,
                                              boolean isUsedBundle, AppCompatActivity context) {
        if (isUsedBundle) {
            Bundle args = new Bundle();
            args.putInt(bundleParameterName, bundleValue);
            fragment.setArguments(args);
        }
        if (context != null) {
            try {
                FragmentTransaction transaction =
                        context.getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                transaction.replace(R.id.quiz_container, fragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();

            } catch (IllegalStateException e) {
                e.printStackTrace();
                context.finish();
            }
        }
    }


    @Override
    public void onBackPressed() {
        Log.d("onbackpressed", "---onbackpressed");
        //Back press show alert to continue to quit
        new AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle("Alert")
                .setMessage(R.string.quit_msg)
                .setPositiveButton(R.string.continue_, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();

                    }
                }).setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

               /* AnswersPojo pojo = StartQuiz.answersPojo;
                savePref.saveCurrentPosition(0);
                savePref.isReadyShown(false);

                QuizScreen.submitAnswers(pojo);*/
               /* if (readPref.getCurrentPosition() == 0) {
                    //
                    savePref.saveCurrentPosition(0);
                    savePref.isReadyShown(false);
                    finish();

                } else {*/
                if (StartQuiz.answersPojo != null) {
                    AnswersPojo pojo = StartQuiz.answersPojo;
                    savePref.saveCurrentPosition(0);
                    savePref.isReadyShown(false);

                    QuizScreen.submitAnswers(pojo);
                }
                //}


            }
        }).show();


    }

    @Override
    protected void onDestroy() {
        Log.d("onDestroy quiz", "#################");
        savePref.saveCurrentPosition(0);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("ACTION_HOME_RESUMED"));
        //TODAY
       /* if (TriviaLoginView.progressLayout != null) {
            TriviaLoginView.progressLayout.showProgress();
        }*/
        //TriviaLoginView.fetchDashboard(Integer.parseInt(readPref.getUID()));
        //  new LoginView(getApplicationContext());
       /* File mediaStorageDir = new File(Environment
                .getExternalStorageDirectory().getPath() + "/TOI_TRIVIA/QuizImages"
        );*/
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/TOI_TRIVIA", "QuizImages");


        deleteDirectory(mediaStorageDir);
        if (QuizScreen.myCountDownTimer != null) {
            QuizScreen.myCountDownTimer.cancel();
        }

        CommonUtility.fetchArchive(getApplicationContext(), String.valueOf(uid), TriviaConstants.GAME_END);

        super.onDestroy();
    }

    public static NewGame.GameProperties returnGameProperties() {
        return properties;
    }

    public static List<RandamisedPojo> returnRandamizedQues() {
        return randamized_ques;
    }

    public static NewGame.Questions returnQuestions(String q_id) {
        return dbController.findQuestionsForQID(q_id);
    }


    @Override
    public void onQuizSCreenFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCategoryFragmentInteraction(String uri) {

    }


    /*public static void deleteDirectory() {

        try {
            Log.d("deleteDirectory", "--called");
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                    .getPath()
                    + "/Android/data/" + activity.getPackageName() + "/files/Trivia/QuizImages"
            );
            if (mediaStorageDir.exists()) {

                mediaStorageDir.delete();
                Log.d("QuizImages folder", "--deleted");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/
    public static boolean deleteDirectory(File path) {
        // TODO Auto-generated method stub
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }
}
