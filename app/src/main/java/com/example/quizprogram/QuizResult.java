/*Written by Mohammed Ahmed, msa190000
For CS4301.002, assignment 3, started Mar 6 2021.
Description:
The app will load up some quizzes out of text files and then offer the choices to the user in a
RecyclerView. They enter their name and pick a quiz and click next and take the quiz on the next
screen. Select an answer and click the answer button which will display the result. Click the
button again to load the next question. When done they are greeted and told their result on a
third screen. From the third screen they can return to the first.
The app also supports fetching quizzes from a website and taking them instead of local quizzes
*/

package com.example.quizprogram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

//This class is for third screen
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