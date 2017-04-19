package toi.com.trivia.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import toi.com.trivia.R;
import toi.com.trivia.activities.StartQuiz;
import toi.com.trivia.fragments.QuizScreen;
import toi.com.trivia.model.AnswersPojo;
import toi.com.trivia.model.NewGame;
import toi.com.trivia.model.PlayedGame;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

/**
 * Created by akshat on 21/1/16.
 */
public class AnswersListAdapter extends BaseAdapter {
    Context baseContext;
    static List<PlayedGame.Options> feedsList = new ArrayList<>();
    int stepCount;
    int qId, isBonus;
    SavePref savePref;
    ReadPref readPref;
    String pos1 = "", pos2 = "";
    public static List<String> optSeq = new ArrayList<>();
    int mCurrentPosition;


    public AnswersListAdapter(Context baseContext, List<PlayedGame.Options> feedsList, int qid) {
        this.baseContext = baseContext;
        this.feedsList = feedsList;
        this.isBonus = isBonus;
        this.qId = qid;
    }

    public AnswersListAdapter(Context activity, List<String> options) {

        this.baseContext = activity;
        this.feedsList = feedsList;
    }


    public static void deleteItem(int position) {
        feedsList.remove(position);

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
        savePref = new SavePref(baseContext);
        readPref = new ReadPref(baseContext);
        mCurrentPosition = readPref.getCurrentPosition();
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.answers_list, parent, false);
            holder = new RecordHolder();
            holder.option_text = (TextView) row.findViewById(R.id.option_text);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        final PlayedGame.Options mDataset = feedsList.get(position);

        try {

          /*  if (mDataset.getName().length() > 25) {
                holder.option_text.setTextSize(14);
            } else {
                holder.option_text.setTextSize(16);
            }*/
            holder.option_text.setText(mDataset.getName());
            //optSeq.add(String.valueOf(mDataset.getOptId()));



            final RecordHolder finalHolder = holder;
            /*row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QuizScreen.SelectedOPtionId = 0;
                    QuizScreen.SelectedOPtionId = mDataset.getOptId();
                    selectedAnswer(finalHolder, v, position);
                    notifyDataSetChanged();
                    //  setPoints(mDataset, position);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            QuizScreen.myCountDownTimer.cancel();
                            QuizScreen.myCountDownTimer.onFinish();


                        }
                    }, 1000);


                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }



    private void selectedAnswer(RecordHolder finalHolder, View v, int position) {


        finalHolder.option_text.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_yellow));
        finalHolder.option_text.setTypeface(Typeface.DEFAULT_BOLD);
        finalHolder.option_text.setTextColor(v.getResources().getColor(R.color.black));
    }


    static class RecordHolder {
        TextView option_text;

    }
}
