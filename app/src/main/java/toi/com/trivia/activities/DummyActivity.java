package toi.com.trivia.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import toi.com.trivia.utility.ui.TriviaLoginView;

public class DummyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new TriviaLoginView(this));
    }
}
