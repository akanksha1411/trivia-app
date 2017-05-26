package toi.com.trivia;

import android.content.Context;

import toi.com.trivia.utility.ui.TriviaLoginView;

/**
 * Created by Akanksha on 25/05/17.
 */

public class Trivia {
    private static Trivia instance;
    private TriviaConfiguration triviaConfiguration;

    public static Trivia getInstance() {
        if (instance == null) {
            instance = new Trivia();
        }
        return instance;
    }

    private Trivia() {
    }

    public void init(TriviaConfiguration triviaConfiguration) {
        this.triviaConfiguration = triviaConfiguration;
    }

    public TriviaConfiguration getTriviaConfiguration() {
        return triviaConfiguration;
    }

    public TriviaLoginView getTriviaView(Context context) {
        return new TriviaLoginView(context);
    }

    public boolean isInitialized() {
        return triviaConfiguration != null;
    }

}
