package toi.com.trivia.utility;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;
/*
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
*/

import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import pl.droidsonroids.gif.GifDrawable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import toi.com.trivia.R;
import toi.com.trivia.activities.Leaderboard;
import toi.com.trivia.activities.PrizesActivity;
import toi.com.trivia.activities.ResultScreen;
import toi.com.trivia.activities.StartQuiz;
import toi.com.trivia.api.APICalls;
import toi.com.trivia.api.APIService;
import toi.com.trivia.api.ApiRetroFit;
import toi.com.trivia.fragments.GameEnd_New;
import toi.com.trivia.fragments.MonthlyLeaderboard;
import toi.com.trivia.fragments.QuizScreen;
import toi.com.trivia.fragments.WeekLeaderboard;
import toi.com.trivia.model.AnswersPojo;
import toi.com.trivia.model.HomeItems;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;

/**
 * Created by yashpal on 9/8/16.
 */
public class CommonUtility implements TriviaConstants {

    public static ProgressDialog dialog;
    public static HomeItems homeItems = new HomeItems();
    private static APIService apiService;
    static Call<HomeItems> registrationItemsCall;
    private static ApiRetroFit apiRetroFit;

    /**
     * Adds 1st fragment to activity being added to the history stack
     *
     * @param fragment
     * @param bundleParameterName
     * @param bundleValue
     * @param isUsedBundle
     */
    public static void replaceFragment(Fragment fragment, String bundleParameterName, int bundleValue,
                                       boolean isUsedBundle, Context context) {
        if (isUsedBundle) {
            Bundle args = new Bundle();
            args.putInt(bundleParameterName, bundleValue);
            fragment.setArguments(args);
        }
        try {
            FragmentTransaction transaction =
                    ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds 1st fragment to activity being added to the history stack
     *
     * @param fragment
     * @param bundleParameterName
     * @param bundleValue
     * @param isUsedBundle
     */
    public static void addFragment(Fragment fragment, String bundleParameterName, int bundleValue,
                                   boolean isUsedBundle, Context context) {
        if (isUsedBundle) {
            Bundle args = new Bundle();
            args.putInt(bundleParameterName, bundleValue);
            fragment.setArguments(args);
        }
        try {
            FragmentTransaction transaction =
                    ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        } catch (IllegalStateException e) {
            e.printStackTrace();
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
        try {
            FragmentTransaction transaction =
                    context.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.commit();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start new activity
     *
     * @param context
     * @param bundleParameterName
     * @param bundleValue
     * @param isUsedBundle
     * @param intent
     */
    public static void showActivity(Context context, String bundleParameterName, int bundleValue,
                                    boolean isUsedBundle, Intent intent, Bundle bundle) {

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (isUsedBundle) {
            Bundle args = new Bundle();
            args.putInt(bundleParameterName, bundleValue);
            intent.putExtras(args);
        }

        if (isUsedBundle) {
            if (bundle != null) {
                intent.putExtras(bundle);
            }
        }
        try {
            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(
                    context, R.anim.slide_in_left,
                    R.anim.slide_out_right).toBundle();
            context.startActivity(intent, bndlanimation);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param bitmap
     * @param pixels
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * Set background for App as default or from URL
     */
    public static void initBackground(View view, Context context, int type, String sponsorName) {

        if (!sponsorName.toLowerCase().equals(TOI_SPONSOR_NAME) && !sponsorName.equals("")) {


            File sponsorFile = new File(Environment.getExternalStorageDirectory()
                    .getPath()
                    + "/Android/data/" + context.getPackageName() + "/files/Trivia/Sponsor/"
                    + sponsorName + ".jpg");
            if (sponsorFile != null) {
                if (sponsorFile.exists()) {
                    try {

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        Bitmap bitmap = BitmapFactory.decodeFile(sponsorFile.getAbsolutePath(), options);
                        //Bitmap bitmap = getBitmapFromURL(sponsorImg);
                        Drawable sponsor = new BitmapDrawable(context.getResources(), bitmap);

                        //   BitmapDrawable sponsor = new BitmapDrawable(context.getResources(), b);
                        switch (type) {
                            case TriviaConstants.HOST_ACTIVITY:
                                LinearLayout main_layout = (LinearLayout) view.findViewById(R.id.main_layout);
                                main_layout.setBackground(sponsor);
                                break;
                            case TriviaConstants.ANSWERS_ACTIVITY:
                                LinearLayout answers_layout = (LinearLayout) view.findViewById(R.id.answers_layout);
                                answers_layout.setBackground(sponsor);
                                break;
                            case TriviaConstants.RESULT_SCREEN:
                                RelativeLayout result_layout = (RelativeLayout) view.findViewById(R.id.result_layout);
                                result_layout.setBackground(sponsor);
                                break;
                            case TriviaConstants.GAME_ARCHIVE:
                                LinearLayout game_archive_layout = (LinearLayout) view.findViewById(R.id.game_archive_layout);
                                game_archive_layout.setBackground(sponsor);
                                break;
                            case TriviaConstants.LEADERBOARD:
                                LinearLayout leaderboard_layout = (LinearLayout) view.findViewById(R.id.leaderboard_layout);
                                leaderboard_layout.setBackground(sponsor);
                                break;

                            case TriviaConstants.START_QUIZ_ACTIVITY:
                                RelativeLayout quiz_layout = (RelativeLayout) view.findViewById(R.id.quiz_layout);
                                quiz_layout.setBackground(sponsor);
                                //    quiz_layout.setBackground(gifFromResource);
                                break;
                            case TriviaConstants.LOGIN_SCREEN:
                                LinearLayout login_layout = (LinearLayout) view.findViewById(R.id.login_layout);
                                login_layout.setBackground(sponsor);
                                break;
                            case TriviaConstants.GAME_END:
                                RelativeLayout game_end = (RelativeLayout) view.findViewById(R.id.game_end_result);
                                game_end.setBackground(sponsor);
                                break;
                            case TriviaConstants.DASHBOARD_SCREEN:
                                FrameLayout dashboard_layout = (FrameLayout) view.findViewById(R.id.dashboard_layout);
                                dashboard_layout.setBackground(sponsor);
                                break;
                            case TriviaConstants.GAME_LOADER:
                                LinearLayout loader_layout = (LinearLayout) view.findViewById(R.id.loader_layout);
                                loader_layout.setBackground(sponsor);
                                break;
                        }
                    } catch (Exception e) {
                        setDefaultBackground(view, context, type);
                        e.printStackTrace();
                    }

                } else {
                    setDefaultBackground(view, context, type);
                }
            } else {
                setDefaultBackground(view, context, type);

            }
        } else {
            setDefaultBackground(view, context, type);

        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setDefaultBackground(View view, Context context, int type) {
        GifDrawable gifFromResource = null;
        try {

            gifFromResource = new GifDrawable(context.getResources(), R.drawable.home_animation);

            switch (type) {
                case TriviaConstants.HOST_ACTIVITY:
                    LinearLayout main_layout = (LinearLayout) view.findViewById(R.id.main_layout);
                    main_layout.setBackground(gifFromResource);
                    break;
                case TriviaConstants.ANSWERS_ACTIVITY:
                    LinearLayout answers_layout = (LinearLayout) view.findViewById(R.id.answers_layout);
                    answers_layout.setBackground(gifFromResource);
                    break;
                case TriviaConstants.RESULT_SCREEN:

                    /*File fireFile = new File(Environment.getExternalStorageDirectory()
                            .getPath()
                            + "/Android/data/" + context.getPackageName() + "/files/Trivia/Others/"
                            + FIREWORKS_IMAGE + ".gif");
                    if (fireFile.exists()) {
                        //FileDescriptor
                        FileDescriptor fd = new RandomAccessFile(fireFile, "r").getFD();
                        gifFromResource = new GifDrawable(fd);
                    }*/
                    RelativeLayout result_layout = (RelativeLayout) view.findViewById(R.id.result_layout);
                    result_layout.setBackground(gifFromResource);
                    break;
                case TriviaConstants.GAME_ARCHIVE:
                    LinearLayout game_archive_layout = (LinearLayout) view.findViewById(R.id.game_archive_layout);
                    game_archive_layout.setBackground(gifFromResource);
                    break;
                case TriviaConstants.LEADERBOARD:
                    LinearLayout leaderboard_layout = (LinearLayout) view.findViewById(R.id.leaderboard_layout);
                    leaderboard_layout.setBackground(gifFromResource);
                    break;

                case TriviaConstants.START_QUIZ_ACTIVITY:
                    RelativeLayout quiz_layout = (RelativeLayout) view.findViewById(R.id.quiz_layout);
                    quiz_layout.setBackground(context.getResources().getDrawable(R.drawable.bg));
                    //    quiz_layout.setBackground(gifFromResource);
                    break;
                case TriviaConstants.LOGIN_SCREEN:
                    LinearLayout login_layout = (LinearLayout) view.findViewById(R.id.login_layout);
                    login_layout.setBackground(gifFromResource);
                    break;
                case TriviaConstants.GAME_END:
                    RelativeLayout game_end = (RelativeLayout) view.findViewById(R.id.game_end_result);
                    game_end.setBackground(gifFromResource);
                    break;
                case TriviaConstants.DASHBOARD_SCREEN:
                    FrameLayout dashboard_layout = (FrameLayout) view.findViewById(R.id.dashboard_layout);
                    dashboard_layout.setBackground(gifFromResource);
                    break;
                case TriviaConstants.GAME_LOADER:
                    LinearLayout loader_layout = (LinearLayout) view.findViewById(R.id.loader_layout);
                    loader_layout.setBackground(gifFromResource);
                    break;
              /*  Bitmap myBitmap = BitmapFactory.decodeFile(profilePicture.getAbsolutePath());
                quiz_image.setImageBitmap(myBitmap);*/

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * get device country iso code from configurations
     *
     * @param context
     */
    public static String getDeviceCountry(Context context) {
        String locale = context.getResources().getConfiguration().locale.getDisplayCountry();

        return locale;

    }

    /**
     * Get IMEI no for the device to be sent as UQID for trivia
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        String imei = "";
        try {
            TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            imei = mngr.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return /*imei*/"358978060746919";
    }

    /***
     * convert comma separeated string to staring list
     *
     * @param commaSeparedString
     * @return
     */
    public static List<String> convertCommaStringToList(String commaSeparedString) {
        List<String> items =
                new ArrayList<String>(Arrays.asList(commaSeparedString.split(",")));

        return items;
    }

    /**
     * Alert to be shown when api calls fails or some error occurs
     *
     * @param context
     */
    public static void showErrorAlert(final Context context, final String message) {

        try {
          /*  new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle("Alert")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            if (!message.equals(TriviaConstants.TRY_LATER)) {
                                //finish the activity opened on okk click
                                ((AppCompatActivity) context).finish();
                            } else {
                                dialog.dismiss();
                            }

                        }
                    }).show();*/

           /* SnackbarManager.show(
                    com.nispok.snackbar.Snackbar.with(context) // context
                            .text(message) // text to display
                            .actionLabel("Ok")
                            .duration(10000)
                            .color(context.getResources().getColor(R.color.black_transparent))
                            .textColor(context.getResources().getColor(R.color.white))// action button label
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                    snackbar.dismiss();
                                }
                            }) // action button's ActionClickListener
            );*/
            View rootView = ((AppCompatActivity) context).getWindow().getDecorView().findViewById(android.R.id.content);

            Snackbar.make(rootView.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Alert to be shown when api calls fails or some error occurs
     *
     * @param context
     */
    public static void showRetryErrorAlert(final AppCompatActivity context, final String message) {
        final SavePref savePref = new SavePref(context);
        try {
          /*  new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle("Alert")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            if (!message.equals(TriviaConstants.TRY_LATER)) {
                                //finish the activity opened on okk click
                                ((AppCompatActivity) context).finish();
                            } else {
                                dialog.dismiss();
                            }

                        }
                    }).show();*/

           /* SnackbarManager.show(
                    com.nispok.snackbar.Snackbar.with(context) // context
                            .text(message) // text to display
                            .actionLabel("Ok")
                            .duration(10000)
                            .color(context.getResources().getColor(R.color.black_transparent))
                            .textColor(context.getResources().getColor(R.color.white))// action button label
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                    snackbar.dismiss();
                                }
                            }) // action button's ActionClickListener
            );*/
            View rootView = ((AppCompatActivity) context).getWindow().getDecorView().findViewById(android.R.id.content);

            // Snackbar.make(rootView.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();

            Snackbar snackbar = Snackbar
                    .make(rootView.findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)

                   /* .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (StartQuiz.answersPojo != null) {
                                AnswersPojo pojo = StartQuiz.answersPojo;
                                savePref.saveCurrentPosition(0);
                                savePref.isReadyShown(false);

                                QuizScreen.submitAnswers(pojo);
                            }
                        }
                    });*/
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (ResultScreen.context != null) {
                                ResultScreen.context.finish();
                            }
                            if (context != null) {
                                context.finish();
                            }
                            /*if (StartQuiz.answersPojo != null) {
                                AnswersPojo pojo = StartQuiz.answersPojo;
                                savePref.saveCurrentPosition(0);
                                savePref.isReadyShown(false);

                                QuizScreen.submitAnswers(pojo);
                            }*/
                        }
                    });

// Changing message text color
            snackbar.setActionTextColor(Color.RED);

// Changing action button text color

            snackbar.show();

           /* SnackbarManager.show(
                    Snackbar.with(context) // context
                            .text(message) // text to display
                            .actionLabel("RETRY") // action button label
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                    if (StartQuiz.answersPojo != null) {
                                        AnswersPojo pojo = StartQuiz.answersPojo;
                                        savePref.saveCurrentPosition(0);
                                        savePref.isReadyShown(false);

                                        QuizScreen.submitAnswers(pojo);
                                    }
                                }
                            }) // action button's ActionClickListener
                    , ((AppCompatActivity) context));*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Alert to be shown when api calls fails or some error occurs
     *
     * @param context
     */
    public static void showMessageAlert(final Context context, String message) {

        try {

            View rootView = ((AppCompatActivity) context).getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar.make(rootView.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * replace with blank if data not present
     *
     * @param text
     * @return
     */
    public static String checkNull(String text) {
        if (text != null) {
            if (text == null || text.length() == 0 || text.equals("null")) {
                text = "";
            }
        } else

        {
            text = "";
        }

        return text;
    }

    /**
     * replace with blank if data not present
     *
     * @param text
     * @return
     */
    public static String getDefaultZero(String text) {
        if (text != null) {
            if (text == null || text.length() == 0 || text.equals("null")) {
                text = "0";
            }
        } else

        {
            text = "0";
        }

        return text;
    }


    /**
     * replace with Anonymous if data not present
     *
     * @param text
     * @return
     */
    public static String checkName(String text) {

        if (text.length() == 0 || text.equals("null") || text == null) {
            text = TriviaConstants.DEFAULT_NAME;
        }

        return text;
    }

    /**
     * Get Comma separated String from list
     *
     * @param selected
     * @return
     */
    public static String commasSeparatedArray(List<String> selected) {
        // The string builder used to construct the string
        StringBuilder commaSepValueBuilder = new StringBuilder();

        // Looping through the list
        for (int i = 0; i < selected.size(); i++) {
            // append the value into the builder
            commaSepValueBuilder.append(selected.get(i));

            // if the value is not the last element of the list
            // then append the comma(,) as well
            if (i != selected.size() - 1) {
                commaSepValueBuilder.append(", ");
            }
        }

        return commaSepValueBuilder.toString();
    }

    /**
     * Show ad in the ad container passed
     *
     * @param mPublisherAdView
     */
    public static void initADs(final PublisherAdView mPublisherAdView) {


        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);
        mPublisherAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mPublisherAdView.setVisibility(View.GONE);

                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                mPublisherAdView.setVisibility(View.GONE);

                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                mPublisherAdView.setVisibility(View.VISIBLE);
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                mPublisherAdView.setVisibility(View.VISIBLE);

                super.onAdLoaded();
            }
        });


    }

    /**
     * @param str
     * @return date format of timestamp
     */
    public static String formatTime(long str) {
        DateFormat df = new SimpleDateFormat("h a");
        Long ltym = Long.valueOf(str);
        Date date = new Date(ltym * 1000);
        TimeZone t = TimeZone.getTimeZone("Asia/Calcutta");
        df.setTimeZone(t);
        // String formattedDate = new
        // SimpleDateFormat("mm/dd/yy hh:mm:ss").format(date);
        String reportDate = df.format(date);

        System.out.println("json:" + str + "  , reportDate:" + reportDate);
        return reportDate;
    }


    /**
     * @param str
     * @return date format of timestamp
     */
    public static String formatDate(long str) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Long ltym = Long.valueOf(str);
        Date date = new Date(ltym * 1000);
        TimeZone t = TimeZone.getTimeZone("Asia/Calcutta");
        df.setTimeZone(t);
        // String formattedDate = new
        // SimpleDateFormat("mm/dd/yy hh:mm:ss").format(date);
        String reportDate = df.format(date);

        System.out.println("json:" + str + "  , reportDate:" + reportDate);
        return reportDate;
    }

    /**
     * String to time stamp
     *
     * @param str_date
     * @return
     */
    public static Long getTimestamp(String str_date) {

        Timestamp timestamp = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date parsedDate = dateFormat.parse(str_date);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
            Long d = timestamp.getTime();
            Long d1 = timestamp.getTime();
        } catch (Exception e) {//this generic but you can control another types of exception
            e.printStackTrace();
        }

        return timestamp.getTime();
    }

    /**
     * Convert string to char array
     *
     * @param s
     * @return
     */
    public static Character[] toCharacterArray(String s) {

        if (s == null) {
            return null;
        }

        int len = s.length();
        Character[] array = new Character[len];
        for (int i = 0; i < len; i++) {
            array[i] = new Character(s.charAt(i));
        }

        return array;
    }

    /**
     * Check network connection available or not
     *
     * @param context
     * @return
     */
    public static boolean haveNetworkConnection(Context context) {
        /*boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;*/

        ConnectivityManager mCM = null;
        if (mCM == null) {
            mCM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo netInfo = mCM.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();


    }


    /**
     * This builds the notification section on top of the screen
     * and text are set as per the notification and type set
     *
     * @param applicationContext
     * @param notificationType
     * @param resultGameId
     */
    public static void initNotification(final Context applicationContext, View view, final String uid, int notificationType, final String resultGameId) {

        final SavePref savePref = new SavePref(applicationContext);
        switch (notificationType) {
            case 1:
                //result notification
                ImageView hide_notification = (ImageView) view.findViewById(R.id.hide_notification);
                TextView notification_title = (TextView) view.findViewById(R.id.notification_title);
                notification_title.setText(R.string.notification_title);
                TextView notification_action = (TextView) view.findViewById(R.id.notification_action);

                final View result_noti_layout = (View) view.findViewById(R.id.result_noti_layout);
                final View network_noti_layout = (View) view.findViewById(R.id.network_noti_layout);

                result_noti_layout.setVisibility(View.VISIBLE);
                network_noti_layout.setVisibility(View.GONE);
                //todo set text to textviews wrt notification type
                notification_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtility.updateAnalyticGtmEvent(applicationContext, GA_PREFIX + "Dashboard", "See Results", TriviaConstants.CLICK);

                        savePref.saveLastResultGameId(resultGameId);
                        //action
                        fetchResult(applicationContext, uid, resultGameId, TriviaConstants.NOTIFICATION);

                        result_noti_layout.setVisibility(View.GONE);
                    }
                });

                //todo set text to textviews wrt notification type
                result_noti_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        savePref.saveLastResultGameId(resultGameId);
                        //action
                        fetchResult(applicationContext, uid, resultGameId, TriviaConstants.NOTIFICATION);

                        result_noti_layout.setVisibility(View.GONE);
                    }
                });
                hide_notification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        result_noti_layout.setVisibility(View.GONE);
                        savePref.saveLastResultGameId(resultGameId);
                    }
                });
                break;
            case 2:
                //network notification
                TextView noti_title_network = (TextView) view.findViewById(R.id.noti_title_network);
                noti_title_network.setText(TriviaConstants.No_INTERNET);
                TextView noti_network_button = (TextView) view.findViewById(R.id.noti_network_button);
                final View network_noti_layout1 = (View) view.findViewById(R.id.network_noti_layout);
                final View result_noti_layout1 = (View) view.findViewById(R.id.result_noti_layout);

                network_noti_layout1.setVisibility(View.VISIBLE);
                result_noti_layout1.setVisibility(View.GONE);

                //todo set text to textviews wrt notification type
                noti_network_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        network_noti_layout1.setVisibility(View.GONE);
                    }
                });
                break;
        }


    }


    /**
     * Make call to fetch new game data
     *
     * @param mCurrentGameId
     */
    public static void fetchPlayedGame(int mCurrentGameId, Context context, String UID) {
        ReadPref readPref = new ReadPref(context);

        HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_GAME_ID, String.valueOf(mCurrentGameId)); // current game id stored in preference from home Api
        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(context));
        map.put(PARAM_UID, String.valueOf(UID));
        map.put(PARAM_ISLOGGEDIN, "1");
        map.put(PARAM_LOGIN_TYPE, DEFAULT_LOGIN_TYPE);
        map.put(PARAM_LOGIN_ID, readPref.getLoginId());
        APICalls.fetchPlayedGame(context, map);

    }

    /**
     * fetch result for the game id passed
     *
     * @param context
     * @param UID
     * @param resultGameId
     * @param openScreen
     */
    public static void fetchResult(final Context context, final String UID, final String resultGameId, int openScreen) {

        Intent intent = new Intent(context, ResultScreen.class);
        CommonUtility.showActivity(context, TriviaConstants.SCREEN_TYPE, TriviaConstants.GAME_ARCHIVE, true, intent, null);

        ReadPref readPref = new ReadPref(context);
        //Interstitial ads integration
     /*   final PublisherInterstitialAd mInterstitialAd = new PublisherInterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.interstitial_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }

            @Override
            public void onAdClosed() {
                //on ads closed the game will start
                openResult();
            }

            private void openResult() {

                Intent intent = new Intent(context, ResultScreen.class);
                CommonUtility.showActivity(context, SCREEN_TYPE, RESULT_SCREEN, true, intent, null);

                HashMap<String, String> map = new HashMap<>();
                map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
                map.put(PARAM_UQID, CommonUtility.getIMEI(context));
                map.put(PARAM_UID, String.valueOf(UID));
                map.put(PARAM_ISLOGGEDIN, "1");
                map.put(PARAM_LOGIN_TYPE, "f");
                map.put(PARAM_LOGIN_ID, "133441");
                map.put(PARAM_LOGIN_TOKEN, "85385");
                map.put(PARAM_GAME_ID, resultGameId);

                APICalls.userResult(context, map);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                openResult();
            }
        });
        requestNewInterstitial(mInterstitialAd, context);*/


        HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(context));
        map.put(PARAM_UID, String.valueOf(UID));
        map.put(PARAM_ISLOGGEDIN, "1");
        map.put(PARAM_LOGIN_TYPE, DEFAULT_LOGIN_TYPE);
        map.put(PARAM_LOGIN_ID, readPref.getLoginId());
        map.put(PARAM_GAME_ID, resultGameId);

        APICalls.userResult(context, map);

    }

    public static void requestNewInterstitial(PublisherInterstitialAd mInterstitialAd, Context context) {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                .addTestDevice(CommonUtility.getIMEI(context))
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    /**
     * Add post parameters for fetching prizes from api
     */
    public static void fetchPrizes(Context context, String gameId, int screenType) {

        Intent intent = new Intent(context, PrizesActivity.class);
        CommonUtility.showActivity(context, SCREEN_TYPE, screenType, true, intent, null);

     /*   HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_GAME_ID, gameId); // A FOR ANDROID
        map.put(PARAM_MODE, "D"); // A FOR ANDROID
        APICalls.triviaPrizes(context, map);*/

    }

    /**
     * Make call to fetch new game data
     *
     * @param mCurrentGameId
     * @param pageCalled
     */
    public static void fetchNewGame(final Context context, final int mCurrentGameId, final int pageCalled) {

      /*  //Interstitial ads integration
        final PublisherInterstitialAd mInterstitialAd = new PublisherInterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.interstitial_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }

            @Override
            public void onAdClosed() {
                //on ads closed the game will start
                openGame();
            }

            private void openGame() {

                createProgressDialog(context);
                HashMap<String, String> map = new HashMap<>();
                map.put(PARAM_GAME_ID, String.valueOf(mCurrentGameId)); // current game id stored in preference from home Api
                APICalls.fetchNewGame(context, map, pageCalled);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                openGame();
            }
        });
        requestNewInterstitial(mInterstitialAd, context);
*/

        createProgressDialog(context);
        HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_GAME_ID, String.valueOf(mCurrentGameId)); // current game id stored in preference from home Api
        APICalls.fetchNewGame(context, map, pageCalled);
    }


    public static ProgressDialog createProgressDialog(Context mContext) {
        try {
            dialog = new ProgressDialog(mContext);

            dialog.show();

            dialog.setCancelable(false);
           /* SpannableString ss2 = new SpannableString("Loading...");
            ss2.setSpan(new RelativeSizeSpan(1f), 0, ss2.length(), 0);

            ss2.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)), 0, ss2.length(), 0);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD200")));*/

            dialog.setMessage("Loading...");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }


    /**
     * Fetch leaderboard daily data
     *
     * @param context
     * @param uid
     * @param isrefreshaction
     * @param mode
     * @param screenType
     */
    public static void fetchLeaderBoard(Context context, String uid, int isrefreshaction, String mode, int screenType) {
        ReadPref readPref = new ReadPref(context);
        HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(context));
        map.put(PARAM_UID, String.valueOf(uid));
        map.put(PARAM_ISLOGGEDIN, "1");
        map.put(PARAM_LOGIN_TYPE, DEFAULT_LOGIN_TYPE);
        map.put(PARAM_LOGIN_ID, readPref.getLoginId());

        map.put(PARAM_MODE, mode);//Mode of the leaderboard to be displayed. Daily/Weekly/Monthly/Single Quiz (D/W/M/Q)
        map.put(PARAM_START_DATE, String.valueOf(System.currentTimeMillis() / 1000));
        map.put(PARAM_END_DATE, String.valueOf(System.currentTimeMillis() / 1000));

        // APICalls.fetchLeaderBoard(context, map, isrefreshaction, mode);

        Intent intent = new Intent(context, Leaderboard.class);
        CommonUtility.showActivity(context, SCREEN_TYPE, screenType, true, intent, null);

    }

    public static void fetchArchive(Context context, String uid, int screenType) {
        ReadPref readPref = new ReadPref(context);


        HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(context));
        map.put(PARAM_UID, String.valueOf(uid));
        map.put(PARAM_ISLOGGEDIN, "1");
        map.put(PARAM_LOGIN_TYPE, TriviaConstants.DEFAULT_LOGIN_TYPE);
        map.put(PARAM_LOGIN_ID, readPref.getLoginId());

        map.put(PARAM_FROM_INDEX, "1");
        map.put(PARAM_COUNT, "32");

        APICalls.fetchGameArchive(context, map);


    }

    /**
     * Add post parameters for register and call api for data
     */
    public static void registerUser(Context context, HashMap<String, String> fbData, String regStatus, String isLoggedIn, String uid, int type) {
        HashMap<String, String> map = new HashMap<>();
        if (fbData.size() != 0) {
            map.putAll(fbData);
        }

        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(context));
        map.put(PARAM_REG_STATUS, regStatus);
        map.put(PARAM_ISLOGGEDIN, isLoggedIn);
        if (!uid.equals("0")) {
            map.put(PARAM_UID, uid);
        }

        APICalls.registerUser(context, map, type);

    }


    /**
     * reference uids
     *
     * @return
     */
    public static Map<String, String> getLoggedInReferences() {
        Map<String, String> map = new HashMap<>();
        map.put("1001", "A_218325482282679");
        map.put("1002", "A_365748863369875");
        map.put("1003", "A_517164593862456");
        map.put("1004", "A_362190292030111");
        map.put("1005", "A_214189993431624");

        return map;


    }

    /**
     * print hashmap values sent in api
     *
     * @param map
     */
    public static void printHashmap(Map<String, String> map) {
        try {
            for (String name : map.keySet()) {

                String key = name.toString();
                String value = map.get(name).toString();
                System.out.println(key + " " + value);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get start and end date from calender
     *
     * @param enterWeek
     * @param enterYear
     */
    public static String getStartWeek(int enterWeek, int enterYear) {
        //enterWeek is week number
        //enterYear is year
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, enterWeek);
        calendar.set(Calendar.YEAR, enterYear);

        SimpleDateFormat formatter = new SimpleDateFormat("ddMMM yyyy"); // PST`
        Date startDate = calendar.getTime();
        String startDateInStr = formatter.format(startDate);
        System.out.println("...date..." + startDateInStr);
        long start = startDate.getTime() / 1000;


        // calendar.add(Calendar.DATE, 6);
      /*  Date enddate = calendar.getTime();
        String endDaString = formatter.format(enddate);
        System.out.println("...date..." + endDaString);*/

        return String.valueOf(start);
    }

    /**
     * Get start and end date from calender
     *
     * @param enterWeek
     * @param enterYear
     */
    public static String getEndOFWeek(int enterWeek, int enterYear) {
        //enterWeek is week number
        //enterYear is year
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, enterWeek);
        calendar.set(Calendar.YEAR, enterYear);

        SimpleDateFormat formatter = new SimpleDateFormat("ddMMM yyyy"); // PST`
     /*   Date startDate = calendar.getTime();
        String startDateInStr = formatter.format(startDate);
        System.out.println("...date..." + startDateInStr);*/

        calendar.add(Calendar.DATE, 6);
        Date enddate = calendar.getTime();
        String endDaString = formatter.format(enddate);
        System.out.println("...date..." + endDaString);
        long end = enddate.getTime() / 1000;
        return String.valueOf(end);
    }


    /**
     * validation in edit text to check space null and enter
     *
     * @param text
     * @return
     */
    public static Boolean chkString(String text) {

        if (TextUtils.isEmpty(text) || text.length() == 0 || text.equals(" ")
                || text == null || text.equals("\n") || text.equals("null")) {

            return false;
        } else if (text.length() > 0 && text.startsWith(" ")) {
            return false;

        } else {
            return true;
        }

    }


    /**
     * Updates analytics GTM events
     *
     * @param eventName
     * @param eventAction
     * @param eventLabel
     */
    public static void updateAnalyticGtmEvent(final Context mContext, final String eventName, final String eventAction, final String eventLabel) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("GTM_TRIVIA--", eventName + "+" + eventAction + "+" + eventLabel);
                DataLayer mDataLayer = TagManager.getInstance(mContext).getDataLayer();
                mDataLayer.pushEvent(eventName, DataLayer.mapOf("EventAction", eventAction, "EventLabel", eventLabel));
            }
        });
        thread.start();

    }

    public static String getCurrentTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    public static void registerUserapi(final Context context, HashMap<String, String> map, String name, String userImage, String toiSsoId, final int pageCalled) {
        Log.d("registerUser called", "-----------------");
        final SavePref savePref = new SavePref(context);
        final ReadPref readPref = new ReadPref(context);

        apiRetroFit = new ApiRetroFit();
        apiService = apiRetroFit.getApiService();
        //Initialize map with default param
        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(context));
        boolean callRegister = false;

        //TODO working here
        /**
         *
         * 0. Get TOI login status, if user object is there then user is logged in else logged out
         * 1. Check if UID is there
         * 2. If Yes; user is already there either logged in or anon browsing no need to update in user pref
         * 2.a. If TOI login status is yes and reg_status = 0 then call register API with user data - update user data
         * 2.b. If TOI login is yes and reg_status =1 , no action
         * 2.c. If TOI login is no and reg_status =0 , no action
         * 2.d. If TOI login is no and reg_status =1 , Expected logout scenario, update reg_status and uid and call register and update data
         * 3. If No; user is not there and we need to invoke register and save in user pref
         * 3.a. If TOI login is yes, then call register with user information and update data
         * 3.b If TOI login id no, then call anon register and update data
         */
        // User user1 = SSOManager.getInstance().getCurrentUser();
        // User user1 = SSOManager.getInstance().getCurrentUser();


        boolean toiIsLogin = false;
        String toiLoginId = "";
        if (CommonUtility.chkString(toiSsoId)) {
            toiIsLogin = true;
            toiLoginId = toiSsoId;

        }
        String triviaLoginId = readPref.getLoginId();
        if (!triviaLoginId.equals(toiLoginId)) {//if this case occurs that means user has logout from setting and again logged in from setting without comming back to dashboard page
            savePref.logoutClearData();
        }
        String triviaUid = readPref.getUID();
        String triviaRegStatus = readPref.getRegStatus();


        //Case 3  user is not there and we need to invoke register and save in user pref
        if (Integer.parseInt(triviaUid) == DEFAULT_ZERO) {

            if (toiIsLogin) {//case 3.a
                map.put(PARAM_REG_STATUS, String.valueOf(DEFAULT_ONE));
                map.put(PARAM_LOGIN_ID, toiLoginId);
                map.put(PARAM_LOGIN_TYPE, "s");
                map.put(PARAM_UID, String.valueOf(DEFAULT_ZERO));
                map.put(PARAM_NAME, name);
                map.put("profile_img", userImage);
                callRegister = true;
            } else {//case 3.bR
                map.put(PARAM_REG_STATUS, String.valueOf(DEFAULT_ZERO));
                callRegister = true;
            }

        } else { //Case 2 user is already there either logged in or anon browsing no need to update in user pref

            if (toiIsLogin && Integer.parseInt(triviaRegStatus) == DEFAULT_ZERO) {//case 2.a
                //call register API with user data - update user data
                map.put(PARAM_REG_STATUS, String.valueOf(DEFAULT_ONE));
                map.put(PARAM_LOGIN_ID, toiLoginId);
                map.put(PARAM_LOGIN_TYPE, "s");
                map.put(PARAM_UID, triviaUid);
                map.put(PARAM_NAME, name);
                map.put("profile_img", userImage);
                callRegister = true;
            } else if (toiIsLogin && Integer.parseInt(triviaRegStatus) == DEFAULT_ONE) {//case 2.b
                //no action
                //2.b. If TOI login is yes and reg_status =1 , no action
                //Do redirect action
                callRegister = false;
            } else if (!toiIsLogin && Integer.parseInt(triviaRegStatus) == DEFAULT_ZERO) {//case 2.c
                //2.c. If TOI login is no and reg_status =0 , no action
                // no action
                callRegister = false;
            } else if (!toiIsLogin && Integer.parseInt(triviaRegStatus) == DEFAULT_ONE) { //case 2.d
                //2.d. If TOI login is no and reg_status =1 , Expected logout scenario, update reg_status and uid and call register and update data
                // Expected logout scenario
                map.put(PARAM_REG_STATUS, String.valueOf(DEFAULT_ZERO));
                callRegister = true;
            }

        }

        Log.d("Call register--------", String.valueOf(callRegister));
        if (callRegister) {
            registrationItemsCall = apiService.registerUser(map); //Attach parameter
            CommonUtility.printHashmap(map);
            registrationItemsCall.enqueue(new Callback<HomeItems>() { //Call API
                                              @Override
                                              public void onResponse(Call<HomeItems> call, Response<HomeItems> response) {

                                                  Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                                                  Log.d("response message", response.message());
                                                  if (response.isSuccessful()) {
                                                      try {
                                                          HomeItems items = response.body();
                                                          if (items.getStatus() == SUCCESS_RESPONSE) {
                                                              //api call is successful
                                                              homeItems = items;
                                                              HomeItems.User user = items.getUser();
                                                              HomeItems.Game game = items.getGame();
                                                              HomeItems.Data data = items.getData();
                                                              HomeItems.Sponsor sponsor = items.getSponsor();

                                                              String data_fire = data.getData_fire();
                                                              int uid = items.getUid();
                                                              int game_count = user.getGame_count();
                                                              int currentGameId = game.getCurrentGameId();
                                                              int nextGameId = game.getNextGameId();


                                                              //save data into local preferences
                                                              //if do not get ssoid from api set reg status as zero else 1
                                                              if (items.getSsoId().equals(String.valueOf(DEFAULT_ZERO))) {
                                                                  savePref.saveRegStatus(String.valueOf(DEFAULT_ZERO));
                                                                  savePref.saveLoginId("");//save ssoid in local preference i.e login id for further API calls
                                                                  savePref.isLoggedIn(String.valueOf(DEFAULT_ZERO));
                                                              } else {
                                                                  savePref.saveRegStatus(String.valueOf(DEFAULT_ONE));
                                                                  savePref.saveLoginId(items.getSsoId());//save ssoid in local preference i.e login id for further API calls
                                                                  savePref.isLoggedIn(String.valueOf(DEFAULT_ONE));
                                                              }
                                                              Log.d("SSO-------------", items.getSsoId().toString());
                                                              savePref.saveUID(String.valueOf(uid));

                                                              savePref.saveUserName(CommonUtility.checkName(user.getName()));
                                                              savePref.saveUserImage(user.getProfile_img());
                                                              savePref.saveScreenBackground(sponsor.getImg_url());
                                                              savePref.saveSponsorName(sponsor.getName());
                                                              savePref.saveUserGameCount(String.valueOf(game_count));
                                                              if (game_count == 0) {
                                                                  savePref.isFirstTime(true);
                                                              } else {
                                                                  savePref.isFirstTime(false);
                                                              }
                                                              if (currentGameId == 0) {
                                                                  //no active game
                                                                  savePref.saveCurrentGameId(String.valueOf(currentGameId));


                                                              } else {
                                                                  //current game is active

                                                                  savePref.saveCurrentGameId(String.valueOf(currentGameId));//save game id to preference
                                                                  savePref.saveNextGameId(String.valueOf(nextGameId));//save next game id to preference
                                                              }

                                                              if (pageCalled == LEADERBOARD) {
                                                                  if (Leaderboard.PlaceholderFragment.lock_leaderboard != null) {
                                                                      Leaderboard.PlaceholderFragment.lock_leaderboard.setVisibility(View.GONE);
                                                                  }
                                                                  if (WeekLeaderboard.lock_leaderboard != null) {
                                                                      WeekLeaderboard.lock_leaderboard.setVisibility(View.GONE);
                                                                  }
                                                                  if (MonthlyLeaderboard.lock_leaderboard != null) {
                                                                      MonthlyLeaderboard.lock_leaderboard.setVisibility(View.GONE);
                                                                  }
                                                              } else if (pageCalled == GAME_END) {
                                                                  if (GameEnd_New.non_logged_in != null) {
                                                                      GameEnd_New.non_logged_in.setVisibility(View.GONE);
                                                                  }
                                                                  if (GameEnd_New.logged_in != null) {
                                                                      GameEnd_New.logged_in.setVisibility(View.VISIBLE);
                                                                  }
                                                                  if (GameEnd_New.user_name != null) {
                                                                      GameEnd_New.user_name.setText(CommonUtility.checkName(readPref.getUserName()));
                                                                  }
                                                                  if (GameEnd_New.profile_img != null) {
                                                                      GameEnd_New.openImage(GameEnd_New.profile_img, readPref.getUserImage());
                                                                  }

                                                              }

                                                              /*//downloading process for sponsor and fireworks
                                                              try {
                                                                  //sponsor image
                                                                  if (!sponsor.getName().toLowerCase().equals(TOI_SPONSOR_NAME)) {
                                                                      File imgFile = new File(Environment.getExternalStorageDirectory()
                                                                              .getPath()
                                                                              + "/Android/data/" + context.getPackageName() + "/files/Trivia/Sponsor/"
                                                                              + sponsor.getName() + ".jpg");


                                                                      if (!imgFile.exists()) {
                                                                          downloadSponsorPicture(sponsor.getImg_url(), sponsor.getName(), 0);
                                                                      }
                                                                  }
                                                              } catch (Exception e) {
                                                                  e.printStackTrace();
                                                              }

                                                              try {//for fireworks
                                                                  if (CommonUtility.chkString(data_fire)) {
                                                                      File imgFile = new File(Environment.getExternalStorageDirectory()
                                                                              .getPath()
                                                                              + "/Android/data/" + context.getPackageName() + "/files/Trivia/Others/"
                                                                              + FIREWORKS_IMAGE + ".gif");
                                                                      if (!imgFile.exists()) {
                                                                          downloadSponsorPicture(data_fire, FIREWORKS_IMAGE, 1);
                                                                      }
                                                                  }
                                                              } catch (Exception e) {
                                                                  e.printStackTrace();
                                                              }*/
                                                          }
                                                      } catch (Exception e) {
                                                          e.printStackTrace();
                                                      }
                                                  }


                                              }

                                              @Override
                                              public void onFailure(Call<HomeItems> call, Throwable t) {
                                                  t.printStackTrace();
                                                  CommonUtility.showErrorAlert(context, TRY_LATER);
                                              }
                                          }

            );

        } else {

            if (pageCalled == LEADERBOARD) {
                if (Leaderboard.PlaceholderFragment.lock_leaderboard != null) {
                    Leaderboard.PlaceholderFragment.lock_leaderboard.setVisibility(View.GONE);
                }
                if (WeekLeaderboard.lock_leaderboard != null) {
                    WeekLeaderboard.lock_leaderboard.setVisibility(View.GONE);
                }
                if (MonthlyLeaderboard.lock_leaderboard != null) {
                    MonthlyLeaderboard.lock_leaderboard.setVisibility(View.GONE);
                }
            } else if (pageCalled == GAME_END) {
                if (GameEnd_New.non_logged_in != null) {
                    GameEnd_New.non_logged_in.setVisibility(View.GONE);
                }
                if (GameEnd_New.logged_in != null) {
                    GameEnd_New.logged_in.setVisibility(View.VISIBLE);
                }
                if (GameEnd_New.user_name != null) {
                    GameEnd_New.user_name.setText(CommonUtility.checkName(readPref.getUserName()));
                }
                if (GameEnd_New.profile_img != null) {
                    GameEnd_New.openImage(GameEnd_New.profile_img, readPref.getUserImage());
                }

            }

        }
    }

    public static HashMap<String, String> loadMap(String mapString) {
        HashMap<String, String> outputMap = new HashMap<String, String>();
        try {

            JSONObject jsonObject = null;

            jsonObject = new JSONObject(mapString);

            Iterator<String> keysItr = jsonObject.keys();
            while (keysItr.hasNext()) {
                String k = keysItr.next();
                String v = (String) jsonObject.get(k);
                outputMap.put(k, v);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputMap;
    }



}

