package toi.com.trivia.activities;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import toi.com.trivia.R;
import toi.com.trivia.databases.DBController;
import toi.com.trivia.model.NewGame;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

public class TriviaLoader extends AppCompatActivity {


    ReadPref readPref;
    int UID;
    private DownloadManager dm;
    private long enqueue;
    BroadcastReceiver receiver;
    File quizImage;
    List<NewGame.Questions> questions = new ArrayList<>();
    Timer timer1 = new Timer();
    static int qListPos = 0;
    static int qCatPos = 0;
    public static DBController dbController;
    AnimatorSet set;
    int typeFlag = 0;
    int totalCount = 0;
    PublisherInterstitialAd mInterstitialAd;
    int pageCalled;
    final List<String> imageUrls = new ArrayList<>();
    final List<String> imageQids = new ArrayList<>();
    final List<String> categoryImages = new ArrayList<>();
    final List<String> categoryName = new ArrayList<>();

    @Override
    protected void onStart() {
        Log.d("onstart-loader", "###########################");
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_loader);
        Log.d("onCreate -loader", "###########################");
        readPref = new ReadPref(getApplicationContext());
        UID = Integer.parseInt(readPref.getUID());
        String sponsorName = readPref.getSponsorName();
        //set background
        CommonUtility.initBackground(getWindow().getDecorView().getRootView(), getApplicationContext(), TriviaConstants.GAME_LOADER, sponsorName);
        try {
       /* //Interstitial ads integration
        mInterstitialAd = new PublisherInterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                //on ads closed the game will start

            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }
        });
        requestNewInterstitial();
*/

            dbController = new DBController(getApplicationContext());

            final ImageView trivia_logo = (ImageView) findViewById(R.id.loading_logo);

            questions = dbController.findALLQuestions();

            Bundle bundle = getIntent().getExtras();
            pageCalled = bundle.getInt("pageCalled");

            startFlipAnimation(trivia_logo);

            timer1.schedule(new TimerTask() {

                @Override
                public void run() {

                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                startFlipAnimation(trivia_logo);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 1000);


            try {
                for (int i = 0; i < questions.size(); i++) {
                    if (questions.get(i).getqImage() != null) {
                        if (questions.get(i).getqImage().length() != 0) {
                            Log.d("download quiz url--", questions.get(i).getqImage());
                            imageUrls.add(questions.get(i).getqImage());
                            imageQids.add(String.valueOf(questions.get(i).getQuesId()));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < questions.size(); i++) {
                if (questions.get(i).getCatImage().length() != 0) {
                    Log.d("download category url--", questions.get(i).getCatImage());
                    File imgFile = new File(Environment.getExternalStorageDirectory()
                            .getPath()
                            + "/Android/data/" + getApplicationContext().getPackageName() + "/files/CatImages/"
                            + questions.get(i).getCatName() + ".jpg");
                    if (!imgFile.exists()) {

                        if (!categoryImages.contains(questions.get(i).getCatImage())) {
                            categoryImages.add(questions.get(i).getCatImage());
                            categoryName.add(questions.get(i).getCatName());
                        }
                    }
                }
            }
            totalCount = imageUrls.size() + categoryImages.size();
            Log.d("download totalCount--", String.valueOf(totalCount));
            if (imageUrls.size() != 0) {
                new DownloadImages(imageUrls.get(qListPos), imageQids.get(qListPos)).execute();
            } else {
                set.cancel();
                //when images are downloaded - get the game started
                Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, null);

            }
    /*        receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    try {
                        String action = intent.getAction();
                        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                            long downloadId = intent.getLongExtra(
                                    DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                            DownloadManager.Query query = new DownloadManager.Query();
                            query.setFilterById(enqueue);

                            if (dm != null) {
                                Cursor c = dm.query(query);
                                if (c.moveToFirst()) {
                                    int columnIndex = c
                                            .getColumnIndex(DownloadManager.COLUMN_STATUS);
                                    if (DownloadManager.STATUS_SUCCESSFUL == c
                                            .getInt(columnIndex)) {

                                        try {
                                            if (qListPos + qCatPos == totalCount) {
                                                set.cancel();
                                                //when images are downloaded - get the game started
                                                Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                                                CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, null);

                                            } else {

                                                if (imageUrls.size() > qListPos) {
                                                    qListPos++;
                                                    typeFlag = 0;

                                                    new DownloadImages(imageUrls.get(qListPos - 1), imageQids.get(qListPos - 1)).execute();
                                                } else {
                                                    typeFlag = 1;
                                                    if (categoryImages.size() > qCatPos) {
                                                        qCatPos++;

                                                        new DownloadImages(categoryImages.get(qCatPos - 1), categoryName.get(qCatPos - 1)).execute();
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            set.cancel();
                                            //when images are downloaded - get the game started
                                            Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                                            CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, null);
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                c.close();
                            }

                        }
                    } catch (Exception e) {
                        set.cancel();
                        //when images are downloaded - get the game started
                        Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                        CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, null);

                        e.printStackTrace();

                    }
                }
            };*/

          /*  registerReceiver(receiver,
                    new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));*/

        } catch (Exception ex) {
            ex.printStackTrace();
            set.cancel();
            //when images are downloaded - get the game started
            Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
            CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, null);

        }


    }




    private void requestNewInterstitial() {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                .addTestDevice(CommonUtility.getIMEI(getApplicationContext()))

                .build();

        mInterstitialAd.loadAd(adRequest);


        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }


    /**
     * Starts animation 360 degree i.e verticall flipping to logo
     *
     * @param trivia_logo
     */

    private void startFlipAnimation(ImageView trivia_logo) {

        try {
            set = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.flipping_animation);
            set.setTarget(trivia_logo);
            set.cancel();
            set.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();


        //unregisterReceiver(receiver);
    }


/*
    public void downloadQuizImages(String Url, String imageName) {
        try {
            String file_name = "";
            file_name = imageName + ".jpg";

            dm = (DownloadManager) getSystemService(
                    getApplicationContext().DOWNLOAD_SERVICE);
            Uri Download_Uri = Uri.parse(Url);
            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

            // Restrict the types of networks over
            // which this download may
            // proceed.
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                    | DownloadManager.Request.NETWORK_MOBILE);
            // Set whether this download may proceed
            // over a roaming connection.
            request.setAllowedOverRoaming(false);
            //request.setVisibleInDownloadsUi(false);
            // Set the title of this download, to be
            // displayed in notifications
            // (if enabled).
            request.setTitle(file_name);
            // Set a description of this download,
            // to be displayed in
            // notifications (if enabled)
            request.setDescription("Quiz images getting downloaded...");

            Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

            if (isSDPresent) {
                File mediaStorageDir = null;
                if (typeFlag == TriviaConstants.DEFAULT_ZERO) {
                    mediaStorageDir = new File(Environment
                            .getExternalStorageDirectory(), "Trivia/QuizImages");
                } else {
                    mediaStorageDir = new File(Environment
                            .getExternalStorageDirectory(), "CatImages");
                }


                if (!mediaStorageDir.exists()) {
                    mediaStorageDir.mkdirs();
                }
                if (typeFlag == TriviaConstants.DEFAULT_ZERO) {
                    request.setDestinationInExternalFilesDir(getApplicationContext(),
                            "/Trivia/.QuizImages", file_name);
                } else {
                    // if (!mediaStorageDir.exists()) {
                    request.setDestinationInExternalFilesDir(getApplicationContext(),
                            "/.CatImages", file_name);
                    // }
                }
                enqueue = dm.enqueue(request);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    /**
     * background task that downloads the quiz and category images and starts the quiz after downloaded.
     */
    private class DownloadImages extends AsyncTask<String, Void, String> {
        String path;
        String text;

        public DownloadImages(String path, String text) {
            this.path = path;
            this.text = text;
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                String file_name = "trivia";
                if (typeFlag == TriviaConstants.DEFAULT_ZERO) {

                    file_name = String.valueOf(readPref.getCurrentGameId() + "_" + text) + ".jpg";
                    ;
                } else {
                    file_name = text + ".jpg";

                }

                Log.d("download url--path", path);
                try {
                   /* URL url = new URL(path);
                    URLConnection conection = url.openConnection();
                    conection.connect();
                    // getting file length
                    int lenghtOfFile = conection.getContentLength();
                    Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
                    File mediaStorageDir = null;
                    if (isSDPresent) {

                        if (typeFlag == TriviaConstants.DEFAULT_ZERO) {
                            mediaStorageDir = new File(Environment
                                    .getExternalStorageDirectory(), "Trivia/QuizImages");
                        } else {
                            mediaStorageDir = new File(Environment
                                    .getExternalStorageDirectory(), "CatImages");
                        }


                        if (!mediaStorageDir.exists()) {
                            mediaStorageDir.mkdirs();
                        }

                    }

                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);

                    // Output stream to write file
                    OutputStream output = new FileOutputStream(mediaStorageDir.getPath() + "/" + file_name);

                    byte data[] = new byte[1024];
                    int len;
                    while((len=input.read(data))>0){
                        output.write(data,0,len);
                    }
                    // writing data to file
                   // output.write(data);


                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();*/


                    String filepath = "";
                    try {
                        URL url = new URL(path);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setDoOutput(true);
                        urlConnection.connect();
                        File SDCardRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();

                        String filename = file_name;
                        Log.i("Local filename:", "" + filename);
                        /*File sponsorFile = new File(Environment.getExternalStorageDirectory()
                                .getPath()
                                + "/Android/data/" + getActivity().getPackageName() + "/files/.CatImages/";*/
                        File headFolder = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName(), "TOI_TRIVIA");
                        if (!headFolder.exists()) {
                            headFolder.mkdir();
                        }
                       /* if (typeFlag == TriviaConstants.DEFAULT_ZERO) {
                            mediaStorageDir = new File(Environment
                                    .getExternalStorageDirectory()+ "/Trivia/QuizImages");
                        } else {
                            mediaStorageDir = new File(Environment
                                    .getExternalStorageDirectory() + "/Trivia/CatImages");
                        }*/
                        File mediaStorageDir = null;
                        if (typeFlag == TriviaConstants.DEFAULT_ZERO) {
                            mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/TOI_TRIVIA", "QuizImages");
                        } else {
                            mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/TOI_TRIVIA", "CatImages");
                        }
                        if (!mediaStorageDir.exists()) {
                            mediaStorageDir.mkdir();
                        }
                        File file = new File(mediaStorageDir, filename);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileOutputStream fileOutput = new FileOutputStream(file);
                        InputStream inputStream = urlConnection.getInputStream();
                        int totalSize = urlConnection.getContentLength();
                        int downloadedSize = 0;
                        byte[] buffer = new byte[1024];
                        int bufferLength = 0;
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            fileOutput.write(buffer, 0, bufferLength);
                            downloadedSize += bufferLength;
                            Log.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
                        }
                        fileOutput.close();
                        if (downloadedSize == totalSize) filepath = file.getPath();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        filepath = null;
                        e.printStackTrace();
                    }
                    Log.i("filepath:", " " + filepath);
                    return filepath;

                } catch (Exception e) {
                    set.cancel();
                    e.printStackTrace();
                    //when images are downloaded - get the game started
                    Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                    CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, null);

                }


            } catch (Exception e) {
                e.printStackTrace();
                set.cancel();
                //when images are downloaded - get the game started
                Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, null);

            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (qListPos + qCatPos == totalCount) {
                    set.cancel();
                    //when images are downloaded - get the game started
                    Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                    CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, null);

                } else {

                    if (imageUrls.size() > qListPos) {
                        qListPos++;
                        typeFlag = 0;

                        new DownloadImages(imageUrls.get(qListPos - 1), imageQids.get(qListPos - 1)).execute();
                    } else {
                        typeFlag = 1;
                        if (categoryImages.size() > qCatPos) {
                            qCatPos++;

                            new DownloadImages(categoryImages.get(qCatPos - 1), categoryName.get(qCatPos - 1)).execute();
                        }
                    }
                }
            } catch (Exception e) {
                set.cancel();
                //when images are downloaded - get the game started
                Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, null);
                e.printStackTrace();
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qListPos = 0;
        Log.d("onDestroy", "###########################");
    }


    public class SaveImageTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("");
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream("/sdcard/downloadedfile.jpg");

                byte data[] = new byte[1024];

                // writing data to file
                output.write(data);


                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
