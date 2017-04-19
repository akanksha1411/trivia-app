package toi.com.trivia.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import toi.com.trivia.R;
import toi.com.trivia.activities.GameArchive;
import toi.com.trivia.activities.ResultScreen;
import toi.com.trivia.model.ResultItems;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

/**
 * Created by Akanksha on 15/11/16.
 */
public class GameEnd_New extends Fragment implements View.OnClickListener, TriviaConstants {
    static ReadPref readPref;
    static String UID = "";
    SavePref savePref;
    public static Context mContext;
    ResultItems data = new ResultItems();
    public static LinearLayout non_logged_in, logged_in, timer_layout;
    public TextView hours1, hours2, minutes1, minutes2, seconds1, seconds2,
            meanwhile_text, game_end_time, total_score, game_score, game_bonus, time_bonus;
    Animation slideUpAnimation;
    public static TextView user_name;
    public static ImageView profile_img;
    Button login_register;
    static PieChart pieChart;
    RelativeLayout game_end_result;

    public GameEnd_New(ResultItems data) {
        this.data = data;
    }

    public GameEnd_New() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.game_end_new, container, false);
        savePref = new SavePref(getActivity().getApplicationContext());
        mContext = getActivity();
        readPref = new ReadPref(getActivity().getApplicationContext());
        UID = readPref.getUID();
        final String sponsorName = readPref.getSponsorName();
        //set background for the screen
        CommonUtility.initBackground(view, getActivity().getApplicationContext(), TriviaConstants.GAME_END, sponsorName);

        initUI(view);

        return view;
    }

    public void initUI(View view) {
        ResultScreen.progressLayout.showContent();
        non_logged_in = (LinearLayout) view.findViewById(R.id.non_logged_in);
        logged_in = (LinearLayout) view.findViewById(R.id.logged_in);
        timer_layout = (LinearLayout) view.findViewById(R.id.timer_layout);
        game_end_time = (TextView) view.findViewById(R.id.game_end_time);
        meanwhile_text = (TextView) view.findViewById(R.id.non_logged_in_text_archive);
        login_register = (Button) view.findViewById(R.id.login_register);
        login_register.setOnClickListener(this);
        game_end_result = (RelativeLayout) view.findViewById(R.id.game_end_result);
        RelativeLayout trivia_home = (RelativeLayout) view.findViewById(R.id.home_box);
        trivia_home.setOnClickListener(this);
        RelativeLayout game_archive = (RelativeLayout) view.findViewById(R.id.archive_box);
        game_archive.setOnClickListener(this);
        pieChart = (PieChart) view.findViewById(R.id.chart);
        user_name = (TextView) view.findViewById(R.id.user_name);
        total_score = (TextView) view.findViewById(R.id.total_score);
        game_score = (TextView) view.findViewById(R.id.game_score);
        game_bonus = (TextView) view.findViewById(R.id.game_bonus);
        time_bonus = (TextView) view.findViewById(R.id.time_bonus);
        profile_img = (ImageView) view.findViewById(R.id.user_image);
        hours1 = (TextView) view.findViewById(R.id.hours1);
        minutes1 = (TextView) view.findViewById(R.id.minutes1);
        seconds1 = (TextView) view.findViewById(R.id.seconds1);
        hours2 = (TextView) view.findViewById(R.id.hours2);
        minutes2 = (TextView) view.findViewById(R.id.minutes2);
        seconds2 = (TextView) view.findViewById(R.id.seconds2);
        slideUpAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_up);

        if (readPref.getRegStatus().equals("1")) {
            onlineLayout(view);
        } else {
            offlineLayout();
        }

    }

    /**
     * offine layout i.e user is opening this page anoymously from app. thus non_logged in UI shown
     */
    public void offlineLayout() {
        non_logged_in.setVisibility(View.VISIBLE);
        logged_in.setVisibility(View.GONE);
        game_end_result.setBackground(getResources().getDrawable(R.drawable.result_back));
        showData(data);
    }

    /**
     * online layout i.e user is opening this page after loginned in TOI from app. thus score section will be shown in UI shown
     */
    public void onlineLayout(View view) {
        non_logged_in.setVisibility(View.GONE);
        logged_in.setVisibility(View.VISIBLE);

        showData(data);

    }

    private void showData(ResultItems data) {


        if (null == data) {
            //data is not present timer wont be shown instead meanwhile text shown
            meanwhile_text.setVisibility(View.VISIBLE);
            timer_layout.setVisibility(View.GONE);
        } else {

            int is_game_live = data.getIs_game_live();
            int is_played_live = data.getIs_played_live();

            user_name.setText(CommonUtility.checkName(readPref.getUserName()));
            openImage(profile_img, CommonUtility.checkName(readPref.getUserImage()));
            game_end_time.setText(" " + CommonUtility.formatTime(Long.valueOf(CommonUtility.checkNull(data.getGame().getResultGameTime()))));
            ArrayList<Entry> entries = new ArrayList<>();


            entries.add(new Entry(Float.valueOf(CommonUtility.getDefaultZero(data.getGameScore())), 0));
            entries.add(new Entry(Float.valueOf(CommonUtility.getDefaultZero(data.getGameBonus())), 1));
            entries.add(new Entry(Float.valueOf(CommonUtility.getDefaultZero(data.getTimeBonus())), 2));

            PieDataSet dataset = new PieDataSet(entries, "# of Calls");

            ArrayList<String> labels = new ArrayList<String>();
            labels.add("");
            labels.add("");
            labels.add("");

            PieData pieData = new PieData(labels, dataset);

            dataset.setColors(new int[]{R.color.score_red, R.color.score_blue, R.color.score_green}, mContext);
            pieChart.setHoleColor(Color.BLACK);
            pieChart.setData(pieData);
            pieChart.setDrawSliceText(false);
            pieChart.getData().setDrawValues(false);
            pieChart.getLegend().setEnabled(false);
            pieChart.setDescription("");


            game_score.setText(String.valueOf(CommonUtility.getDefaultZero(data.getGameScore())));
            game_bonus.setText(String.valueOf(CommonUtility.getDefaultZero(data.getGameBonus())));
            time_bonus.setText(String.valueOf(CommonUtility.getDefaultZero(data.getTimeBonus())));

            //total_score.setText(String.valueOf(Integer.parseInt(CommonUtility.getDefaultZero(data.getGame().getGameScore())) + Integer.parseInt(CommonUtility.getDefaultZero(data.getGame().getGameBonus())) + Math.round(Float.parseFloat(CommonUtility.getDefaultZero(data.getGame().getTimeBonus())))));

            total_score.setText(String.valueOf(Integer.parseInt(CommonUtility.getDefaultZero(data.getGameScore())) + Integer.parseInt(CommonUtility.getDefaultZero(data.getGameBonus())) + Math.round(Float.parseFloat(CommonUtility.getDefaultZero(data.getTimeBonus())))));
            if (is_played_live == DEFAULT_ONE && is_game_live == DEFAULT_ONE) {
                meanwhile_text.setVisibility(View.GONE);
                if(!data.getGame().getNextGameTime().equals("0")) {
                    timer_layout.setVisibility(View.VISIBLE);
                    long server_time = Long.parseLong(data.getGame().getServer_time());
                    long offset = server_time - (System.currentTimeMillis() / 1000);
                    long counter_time = Long.valueOf(data.getGame().getNextGameTime()) - offset;
                    Long nextGameTimeLeft = (counter_time * 1000) - System.currentTimeMillis();
                    Log.d("timer---", "current--" + String.valueOf(System.currentTimeMillis() / 1000) + "server--" + server_time + "offset--" + offset + "result time" + data.getGame().getResultGameTime() + "difference--" + counter_time + "nextgametime" + nextGameTimeLeft);

                    final CounterClass timer = new CounterClass(nextGameTimeLeft, 1000);
                    timer.start();
                }else{
                    meanwhile_text.setVisibility(View.VISIBLE);
                    timer_layout.setVisibility(View.GONE);
                }
            } else {
                meanwhile_text.setVisibility(View.VISIBLE);
                timer_layout.setVisibility(View.GONE);

            }
        }
    }

    /**
     * set image to imageview for user
     *
     * @param imageView
     * @param url
     */
    public static void openImage(final ImageView imageView, String url) {
        try {

            if (CommonUtility.chkString(url)) {
                Log.d("url image", "called");

                Glide.with(mContext)
                        .load(CommonUtility.checkNull(url))
                        .asBitmap()
                        .placeholder(R.drawable.default_post_img)
                        .error(R.drawable.default_post_img)
                        .into(new BitmapImageViewTarget(imageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(mContext.getResources(),
                                        Bitmap.createScaledBitmap(resource, 50, 50, false));
                                drawable.setCircular(true);
                                imageView.setImageDrawable(drawable);
                            }
                        });

            } else {
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.default_avatar));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * counter class for countdown for result declaration
     */
    public class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // game_start_time_left.setText("Completed.");
            try {
                CommonUtility.fetchResult(getActivity().getApplicationContext(), UID, readPref.getNextGameId(), TriviaConstants.GAME_END);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;

            displayTimerTikker(millis);

        }

    }

    /**
     * Display tikker timer left for result display with animation
     *
     * @param millis
     */
    public void displayTimerTikker(long millis) {

        String hours_text = String.format("%02d", TimeUnit.MILLISECONDS.toHours(millis));
        Character[] charhoursArray = CommonUtility.toCharacterArray(hours_text);

        if (!hours1.getText().toString().equals(charhoursArray[0].toString())) {
            hours1.startAnimation(slideUpAnimation);
        } else {
            hours1.clearAnimation();
        }

        if (!hours2.getText().toString().equals(charhoursArray[1].toString())) {
            hours2.startAnimation(slideUpAnimation);
        } else {
            hours2.clearAnimation();
        }
        hours1.setText(charhoursArray[0].toString());
        hours2.setText(charhoursArray[1].toString());

        String mins_text = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
        Character[] charMinsArray = CommonUtility.toCharacterArray(mins_text);
        if (!minutes1.getText().toString().equals(charMinsArray[0].toString())) {
            minutes1.startAnimation(slideUpAnimation);
        } else {
            minutes1.clearAnimation();
        }

        if (!minutes2.getText().toString().equals(charMinsArray[1].toString())) {
            minutes2.startAnimation(slideUpAnimation);
        } else {
            minutes2.clearAnimation();
        }

        minutes1.setText(charMinsArray[0].toString());
        minutes2.setText(charMinsArray[1].toString());

        String sec = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        Character[] charObjectArray = CommonUtility.toCharacterArray(sec);
        if (!seconds1.getText().toString().equals(charObjectArray[0].toString())) {
            seconds1.startAnimation(slideUpAnimation);
        } else {
            seconds1.clearAnimation();
        }

        if (!seconds2.getText().toString().equals(charObjectArray[1].toString())) {
            seconds2.startAnimation(slideUpAnimation);
        } else {
            seconds2.clearAnimation();
        }
        seconds1.setText(charObjectArray[0].toString());
        seconds2.setText(charObjectArray[1].toString());
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.home_box) {

            //call leaderboard api and open page
            CommonUtility.fetchLeaderBoard((AppCompatActivity) getContext(), String.valueOf(UID), 0, MODE_DAILY, DASHBOARD_SCREEN);
            //GA ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Result Game End", "Leaderboard", TriviaConstants.CLICK);

        } else if (id == R.id.archive_box) {
            //open archive
            Intent intent = new Intent(getActivity(), GameArchive.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            CommonUtility.showActivity(getActivity(), SCREEN_TYPE, TriviaConstants.GAME_END, true, intent, null);

            //GA ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Result Game End", "Game Archive", TriviaConstants.CLICK);

        } else if (id == R.id.login_register) {

            //todo
            if (readPref.getIsLiveCode()) {
                // Open Toi Login page
                try {
                    final Intent login = new Intent(android.content.Intent.ACTION_VIEW);
                    String implClassName = getResources().getString(R.string.login_class);
                    login.setClassName(getActivity(), implClassName);

                    //CommonUtility.showActivity(context, "", 0, false, login, null);
                    startActivityForResult(login, RESPONSE_LOGIN_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //GA ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Result Game End", "Login", TriviaConstants.CLICK);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        CommonUtility.fetchArchive(mContext, String.valueOf(UID), TriviaConstants.GAME_END);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + EXIT + "Result Game End", "Back Press", CLICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.d("activity result called", "leaderboard");

        //if (requestCode == RESPONSE_LOGIN_CODE) {
        if (resultCode == LOGIN_REQUEST_CODE) {
            if (data != null) {

                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    String name = bundle.getString("name");
                    String user_image = bundle.getString("user_image");
                    String ssoid = bundle.getString("ssoid");

                    Log.d("name", name);
                    CommonUtility.registerUserapi(getActivity(), new HashMap<String, String>(), name, user_image, ssoid, GAME_END);
                }

            }
        }

        //}

    }
}
