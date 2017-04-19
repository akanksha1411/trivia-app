package toi.com.trivia.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import toi.com.trivia.R;
import toi.com.trivia.model.PlayedGame;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.TriviaConstants;

/**
 * Created by akshat on 21/1/16.
 */
public class AnswersGridAdapter extends BaseAdapter {
    Context baseContext;
    static List<PlayedGame.Options> feedsList = new ArrayList<>();
    int stepCount;
    int qId, isBonus;
    SavePref savePref;
    ReadPref readPref;
    String pos1 = "", pos2 = "";
    public static List<String> optSeq = new ArrayList<>();
    int mCurrentPosition;
    int selectedOption;

    public AnswersGridAdapter(Context baseContext, List<PlayedGame.Options> feedsList, int qid, String selectedOption) {
        this.baseContext = baseContext;
        this.feedsList = feedsList;
        this.selectedOption = Integer.parseInt(selectedOption);
        this.qId = qid;
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
            row = inflater.inflate(R.layout.answers_grid, parent, false);
            holder = new RecordHolder();
            holder.option_text = (TextView) row.findViewById(R.id.option_text);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        final PlayedGame.Options mDataset = feedsList.get(position);

        try {

            if (mDataset.getName().length() > 25) {
                holder.option_text.setTextSize(14);
            } else {
                holder.option_text.setTextSize(16);
            }
            holder.option_text.setText(mDataset.getName());
            //optSeq.add(String.valueOf(mDataset.getOptId()));


            selectedAnswer(holder, row, position, mDataset);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }


    static class RecordHolder {
        TextView option_text;

    }

    public void selectedAnswer(RecordHolder finalHolder, View v, int position, PlayedGame.Options mDataset) {
        try {
            if (position == 0) {

                if (mDataset.getOptId() == selectedOption && mDataset.getIsCorrect() == TriviaConstants.DEFAULT_ONE) {
                    finalHolder.option_text.setBackground(ContextCompat.getDrawable(baseContext, R.drawable.rounded_button_green_top_left));
                } else if (mDataset.getOptId() == selectedOption && mDataset.getIsCorrect() == TriviaConstants.DEFAULT_ZERO) {
                    finalHolder.option_text.setBackground(ContextCompat.getDrawable(baseContext, R.drawable.rounded_button_red_top_left));
                } else {
                    finalHolder.option_text.setBackground(ContextCompat.getDrawable(baseContext, R.drawable.rounded_button_white_top_left));
                }
            } else if (position == 1) {
                if (mDataset.getOptId() == selectedOption && mDataset.getIsCorrect() == TriviaConstants.DEFAULT_ONE) {
                    finalHolder.option_text.setBackground(ContextCompat.getDrawable(baseContext, R.drawable.rounded_button_green_top_right));
                } else if (mDataset.getOptId() == selectedOption && mDataset.getIsCorrect() == TriviaConstants.DEFAULT_ZERO) {
                    finalHolder.option_text.setBackground(ContextCompat.getDrawable(baseContext, R.drawable.rounded_button_red_top_right));
                } else {
                    finalHolder.option_text.setBackground(ContextCompat.getDrawable(baseContext, R.drawable.rounded_button_white_top_right));
                }
            } else if (position == 2) {
                if (mDataset.getOptId() == selectedOption && mDataset.getIsCorrect() == TriviaConstants.DEFAULT_ONE) {
                    finalHolder.option_text.setBackground(ContextCompat.getDrawable(baseContext, R.drawable.rounded_button_green_bottom_left));
                } else if (mDataset.getOptId() == selectedOption && mDataset.getIsCorrect() == TriviaConstants.DEFAULT_ZERO) {
                    finalHolder.option_text.setBackground(ContextCompat.getDrawable(baseContext, R.drawable.rounded_button_red_bottom_left));
                } else {
                    finalHolder.option_text.setBackground(ContextCompat.getDrawable(baseContext, R.drawable.rounded_button_white_bottom_left));
                }
            } else if (position == 3) {
                if (mDataset.getOptId() == selectedOption && mDataset.getIsCorrect() == TriviaConstants.DEFAULT_ONE) {
                    finalHolder.option_text.setBackground(ContextCompat.getDrawable(baseContext, R.drawable.rounded_button_green_bottom_right));
                } else if (mDataset.getOptId() == selectedOption && mDataset.getIsCorrect() == TriviaConstants.DEFAULT_ZERO) {
                    finalHolder.option_text.setBackground(ContextCompat.getDrawable(baseContext, R.drawable.rounded_button_red_bottom_right));
                } else {
                    finalHolder.option_text.setBackground(ContextCompat.getDrawable(baseContext, R.drawable.rounded_button_white_bottom_right));
                }
            }

            //  finalHolder.option_text.setBackgroundDrawable(baseContext.getResources().getDrawable(R.drawable.rounded_button_yellow));
            finalHolder.option_text.setTypeface(Typeface.DEFAULT_BOLD);
            finalHolder.option_text.setTextColor(v.getResources().getColor(R.color.black));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
