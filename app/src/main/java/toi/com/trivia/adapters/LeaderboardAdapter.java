package toi.com.trivia.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import toi.com.trivia.R;
import toi.com.trivia.activities.Leaderboard;
import toi.com.trivia.model.LeaderboardItems;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;

/**
 * Created by akshat on 21/1/16.
 */
public class LeaderboardAdapter extends BaseAdapter {
    static Context baseContext;
    List<LeaderboardItems.Rankings> feedsList = new ArrayList<>();
    int stepCount;
    int qId;
    SavePref savePref;
    Boolean flag = true;
    int uid;
    ReadPref readPref;

    public LeaderboardAdapter(Context baseContext, List<LeaderboardItems.Rankings> feedsList, String UID) {
        this.baseContext = baseContext;
        this.feedsList = feedsList;
        this.uid = Integer.parseInt(UID);
    }


    public void updateItem(List<LeaderboardItems.Rankings> items) {
        feedsList = new ArrayList<>();
        feedsList = items;
        notifyDataSetChanged();

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
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.leaderboard_items, parent, false);

            holder = new RecordHolder();
            holder.rank_no = (TextView) row.findViewById(R.id.rank_no);
            holder.user_score = (TextView) row.findViewById(R.id.user_score);
            holder.user_name = (TextView) row.findViewById(R.id.user_name);
            holder.user_image = (ImageView) row.findViewById(R.id.user_image);
            holder.trophy = (ImageView) row.findViewById(R.id.trophy);

            /*holder.winner_prize_name = (TextView) row.findViewById(R.id.winner_prize_name);
            holder.winner_name = (TextView) row.findViewById(R.id.winner_name);
            holder.winner_prize_img = (ImageView) row.findViewById(R.id.winner_prize_img);*/
            row.setTag(holder);


        } else {
            holder = (RecordHolder) row.getTag();
        }
        final LeaderboardItems.Rankings mDataset = feedsList.get(position);

        try {
            holder.user_name.setText(mDataset.getName());
            int user_rank = 0;
            try {
                user_rank = Integer.parseInt(mDataset.getRank());

            } catch (Exception e) {

            }

            //final int user_score=Integer.parseInt(mDataset.getScore());
            final int is_won = (mDataset.getIswon());

            holder.rank_no.setText(String.valueOf(mDataset.getRank()));
            holder.user_score.setText(String.valueOf(mDataset.getScore()));
            //loadImage(baseContext, "http://keenthemes.com/preview/metronic/theme/assets/global/plugins/jcrop/demos/demo_files/image1.jpg");

           /* Bitmap icon = BitmapFactory.decodeResource(baseContext.getResources(),
                    R.drawable.default_avatar);
            holder.user_image.setImageBitmap(icon);*/
            openImage(holder.user_image, mDataset.getImgurl(), String.valueOf(mDataset.getUid()), R.drawable.default_avatar);
            if ((position % 2) == 0) {
                row.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.archive_grey));
            } else {
                row.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.grey));
            }


            System.out.println("Is Won" + is_won);
            if (is_won == 1) {
                switch (user_rank) {
                    case 1:
                        holder.trophy.setImageResource(R.drawable.trophy_gold);
                        break;
                    case 2:
                        holder.trophy.setImageResource(R.drawable.silver_trophy);
                        break;
                    case 3:
                        holder.trophy.setImageResource(R.drawable.bronze_trophy);
                        break;
                    case 4:
                        holder.trophy.setImageResource(R.drawable.trophy_gen);
                        break;
                    case 5:
                        holder.trophy.setImageResource(R.drawable.trophy_gen);
                        break;
                }
            } else {
                holder.trophy.setImageResource(R.drawable.trophy_trans);
            }


            if (position == 5) {
                row.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.yellow));
                holder.user_name.setTextColor(baseContext.getResources().getColor(R.color.black));
                holder.user_score.setTextColor(baseContext.getResources().getColor(R.color.black));
                holder.user_name.setText(readPref.getUserName());
                openImage(holder.user_image, readPref.getUserImage(), String.valueOf(mDataset.getUid()), R.drawable.default_avatar);
            } else {
                holder.user_name.setTextColor(baseContext.getResources().getColor(R.color.white));
                holder.user_score.setTextColor(baseContext.getResources().getColor(R.color.white));


            }


            if (uid == mDataset.getUid()) {
                row.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.yellow));
                holder.user_name.setTextColor(baseContext.getResources().getColor(R.color.black));
                holder.user_score.setTextColor(baseContext.getResources().getColor(R.color.black));

            }


            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (is_won == 1) {
                        Leaderboard.openPrizesView(mDataset.getUid(), mDataset.getPname(), mDataset.getName(), mDataset.getPimgurl(), mDataset.getRank(), baseContext);
                    } else {

                    }


                }


            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }

    public static void loadImageWithCallback(Context baseContext, String url, ImageView winner_prize_img) {
        openImage(winner_prize_img, url, "", R.drawable.default_post_img);

    }


    static class RecordHolder {
        TextView rank_no, user_name, user_score, winner_name, winner_prize_name;
        ImageView user_image, trophy, winner_prize_img;

    }


    /**
     * set image to imageview for user
     *
     * @param quiz_image
     * @param url
     * @param default_avatar
     */
    public static void openImage(final ImageView quiz_image, String url, String uid, int default_avatar) {
        try {
            File profilePicture = new File(Environment.getExternalStorageDirectory()
                    .getPath()
                    + "/Android/data/" + baseContext.getPackageName() + "/files/Trivia/"
                    + "TriviaProfilePicture" + "_" + uid + ".jpg");
            if (profilePicture.exists()) {
                Log.d("file image", "called");
                Bitmap myBitmap = BitmapFactory.decodeFile(profilePicture.getAbsolutePath());
                quiz_image.setImageBitmap(myBitmap);
            } else {
                if (CommonUtility.chkString(url)) {
                    Log.d("url image", "called");
                    Glide.with(baseContext)
                            .load(CommonUtility.checkNull(CommonUtility.checkNull(url)))
                            .asBitmap()
                            .placeholder(default_avatar)
                            .error(default_avatar)
                            .into(new BitmapImageViewTarget(quiz_image) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(baseContext.getResources(),
                                            Bitmap.createScaledBitmap(resource, 50, 50, false));
                                    drawable.setCircular(true);
                                    quiz_image.setImageDrawable(drawable);
                                }
                            });
                } else {
                    Bitmap icon = BitmapFactory.decodeResource(baseContext.getResources(),
                            default_avatar);
                    Bitmap bitmap = decodeSampledBitmapFromResource(baseContext.getResources(), default_avatar, 100, 100);
                    if (bitmap != null) {
                        quiz_image.setImageBitmap(bitmap);
                    }
                    //quiz_image.setImageBitmap(icon);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(res, resId, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


}
