package toi.com.trivia;

/**
 * Created by Akanksha on 25/05/17.
 */

public class TriviaConfiguration {
    private TriviaDataProvider triviaDataProvider;
    private TriviaCommandListener triviaCommandListener;

    public TriviaConfiguration(TriviaDataProvider triviaDataProvider, TriviaCommandListener triviaCommandListener) {
        this.triviaDataProvider = triviaDataProvider;
        this.triviaCommandListener = triviaCommandListener;
    }

    public TriviaCommandListener getTriviaCommandListener() {
        return triviaCommandListener;
    }

    public TriviaDataProvider getTriviaDataProvider() {
        return triviaDataProvider;
    }
}
