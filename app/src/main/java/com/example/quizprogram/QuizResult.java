package com.example.quizprogram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class QuizResult extends AppCompatActivity
{
    String playerName = "";
    int numberOfCorrectAnswers = 0;
    int numberOfTotalQuestions = 0;
    TextView playerGreeting;
    TextView quizScore;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        playerGreeting = findViewById(R.id.playerGreetingID);
        quizScore = findViewById(R.id.quizScoreID);

        Intent theIntent = getIntent();
        playerName = theIntent.getStringExtra("playerName");
        numberOfCorrectAnswers = theIntent.getIntExtra("numOfCorrectAnswers", 0);
        numberOfTotalQuestions = theIntent.getIntExtra("numOfQuestions", 0);

        playerGreeting.setText("Greetings " + playerName + ".\nYou have completed the quiz.\nYour score is: ");
        quizScore.setText(numberOfCorrectAnswers + " / " + numberOfTotalQuestions);

    }

    public void returnToMainScreen(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //clears the stack of all the loaded activities
        startActivity(intent);
    }
}