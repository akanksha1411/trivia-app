package toi.com.trivia;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Akanksha on 25/05/17.
 */

public interface TriviaCommandListener {
    void login(Activity context, int requestCode);

    void configureTriviaNotification(Context context, String tag);
}
