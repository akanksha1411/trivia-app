package toi.com.trivia.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import toi.com.trivia.R;
import toi.com.trivia.activities.ResultScreen;
import toi.com.trivia.databases.DBController;
import toi.com.trivia.model.ArchiveItems;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

/**
 * Created by akshat on 21/1/16.
 */
public class ArchiveGridAdapter extends BaseAdapter implements TriviaConstants {
    AppCompatActivity baseContext;
    static List<ArchiveItems.Result> feedsList = new ArrayList<>();
    int stepCount;
    int qId, UID;
    SavePref savePref;
    public static DBController dbController;
    ReadPref readPref;

    public ArchiveGridAdapter(AppCompatActivity baseContext, List<ArchiveItems.Result> feedsList, String UID) {
        this.baseContext = baseContext;
        this.feedsList = feedsList;
        this.stepCount = stepCount;
        this.UID = Integer.parseInt(UID);
    }


    public static void deleteItem(int position) {
        feedsList.remove(position);

    }

    public static void addItem(ArchiveItems.Result result) {
        feedsList.add(result);

    }

    @Override
    public int getCount() {
        return feedsList.size();
    }

    @Override
    public Object getItem(int position) {
        return feedsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        RecordHolder holder = null;
        dbController = new DBController(baseContext);

        savePref = new SavePref(baseContext);
        readPref = new ReadPref(baseContext);
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.game_child_items, parent, false);
            holder = new RecordHolder();
            holder.quiz_no = (TextView) row.findViewById(R.id.quiz_no);
            holder.quiz_qsn_count = (TextView) row.findViewById(R.id.quiz_qsn_count);
            holder.button_text = (TextView) row.findViewById(R.id.button_text);
            holder.green_tick = (ImageView) row.findViewById(R.id.green_tick);

            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        final ArchiveItems.Result mDataset = (ArchiveItems.Result) getItem(position);

        try {

            holder.quiz_no.setText("QUIZ " + String.valueOf(position + 1));
            holder.quiz_qsn_count.setText(TriviaConstants.Archive_Text_Qcount + " " + mDataset.getQues());
            final RecordHolder finalHolder = holder;

            if (mDataset.getPlayStatus().equals("1")) {
                //means user has played this game so result will be shown on the button
                playedAnswer(finalHolder, row);
            } else {
                notPlayedAnswer(finalHolder, row);
            }

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CommonUtility.haveNetworkConnection(baseContext)) {
                        if (mDataset.getPlayStatus().equals("0")) {
                            //play new game
                            savePref.saveArchieveGameId(mDataset.getGameId());
                            savePref.saveCurrentPosition(0);
                            savePref.saveResultGameId(String.valueOf(mDataset.getGameId()));
                            dbController.clearDatabase(baseContext, mDataset.getGameId());
                            //GA ANALYTICS
                            CommonUtility.updateAnalyticGtmEvent(baseContext, GA_PREFIX + "Game Archive", "Play", TriviaConstants.CLICK);
                        } else {
                            //show result page
                            savePref.saveResultGameId(String.valueOf(mDataset.getGameId()));
                            //see result

                            CommonUtility.fetchResult(baseContext, String.valueOf(UID), mDataset.getGameId(), TriviaConstants.GAME_ARCHIVE);


                            //GA click ANALYTICS
                            CommonUtility.updateAnalyticGtmEvent(baseContext, GA_PREFIX + "Game Archive", "Result", TriviaConstants.CLICK);

                        }
                    } else {
                        CommonUtility.showMessageAlert(baseContext, TriviaConstants.No_INTERNET);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }


    private void notPlayedAnswer(RecordHolder finalHolder, View v) {

        finalHolder.button_text.setBackgroundColor(v.getResources().getColor(R.color.yellow));
        finalHolder.button_text.setTypeface(Typeface.DEFAULT_BOLD);
        finalHolder.button_text.setText(R.string.play);
        finalHolder.green_tick.setVisibility(View.GONE);
        finalHolder.button_text.setTextColor(v.getResources().getColor(R.color.black));
    }

    private void playedAnswer(RecordHolder finalHolder, View v) {

        finalHolder.button_text.setBackgroundColor(v.getResources().getColor(R.color.grey_background));
        finalHolder.button_text.setTypeface(Typeface.DEFAULT_BOLD);
        finalHolder.button_text.setText(R.string.result);
        finalHolder.green_tick.setVisibility(View.VISIBLE);
        finalHolder.button_text.setTextColor(v.getResources().getColor(R.color.white));
    }

    static class RecordHolder {
        TextView quiz_no, quiz_qsn_count, button_text;
        ImageView green_tick;

    }

}
