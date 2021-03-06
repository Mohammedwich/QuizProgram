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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

// This class is for second screen
public class QuizQuestions extends AppCompatActivity
{
    String textForAnswerValidity = "...";

    Serializable serializedQuiz;
    Quiz selectedQuiz;
    ArrayList<String[]> allQuestions;
    String [] currentQuestion;
    int currentQuestionIndex = 0;

    String currentlySelectedChoice = "...";
    int questionsAnswered = 0;
    int questionsAnsweredCorrectly = 0;
    boolean quizComplete = false; //make true when questions run out so result screen is displayed

    TextView question;
    TextView choice1;
    TextView choice2;
    TextView choice3;
    TextView choice4;
    String correctChoice;

    String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_questions);

        question=findViewById(R.id.Question);
        choice1 = findViewById(R.id.Choice1);
        choice2 = findViewById(R.id.Choice2);
        choice3 = findViewById(R.id.Choice3);
        choice4 = findViewById(R.id.Choice4);

        Intent theIntent = getIntent();
        serializedQuiz = theIntent.getSerializableExtra("chosenQuiz");
        selectedQuiz = (Quiz) serializedQuiz;

        playerName = theIntent.getStringExtra("playerName");

        allQuestions = selectedQuiz.getListOfQuestions();
        currentQuestion = allQuestions.get(currentQuestionIndex);

        question.setText(currentQuestion[0]);
        choice1.setText(currentQuestion[1]);
        choice2.setText(currentQuestion[2]);
        choice3.setText(currentQuestion[3]);
        choice4.setText(currentQuestion[4]);
        correctChoice = currentQuestion[5];

        spawnAnswerButtonFrag();
    }


    //update currentlySelectedChoice variable and change the view's background color
    public void highlightChoice(View view)
    {
        if (view == choice1)
        {
            currentlySelectedChoice = choice1.getText().toString();
            choice1.setBackgroundColor(Color.parseColor("#1cd9ff")); //set blue
            choice2.setBackgroundColor(Color.parseColor("#ffffff")); //set white
            choice3.setBackgroundColor(Color.parseColor("#ffffff"));
            choice4.setBackgroundColor(Color.parseColor("#ffffff"));

            checkAnswerAndGenerateResponse((TextView)view, correctChoice);
        }
        else if (view == choice2)
        {
            currentlySelectedChoice = choice2.getText().toString();
            choice1.setBackgroundColor(Color.parseColor("#ffffff"));
            choice2.setBackgroundColor(Color.parseColor("#1cd9ff"));
            choice3.setBackgroundColor(Color.parseColor("#ffffff"));
            choice4.setBackgroundColor(Color.parseColor("#ffffff"));

            checkAnswerAndGenerateResponse((TextView)view, correctChoice);
        }
        else if (view == choice3)
        {
            currentlySelectedChoice = choice3.getText().toString();
            choice1.setBackgroundColor(Color.parseColor("#ffffff"));
            choice2.setBackgroundColor(Color.parseColor("#ffffff"));
            choice3.setBackgroundColor(Color.parseColor("#1cd9ff"));
            choice4.setBackgroundColor(Color.parseColor("#ffffff"));

            checkAnswerAndGenerateResponse((TextView)view, correctChoice);
        }
        else if (view == choice4)
        {
            currentlySelectedChoice = choice4.getText().toString();
            choice1.setBackgroundColor(Color.parseColor("#ffffff"));
            choice2.setBackgroundColor(Color.parseColor("#ffffff"));
            choice3.setBackgroundColor(Color.parseColor("#ffffff"));
            choice4.setBackgroundColor(Color.parseColor("#1cd9ff"));

            checkAnswerAndGenerateResponse((TextView)view, correctChoice);
        }
        else
        {
            System.out.println("Error: Clicked view is not equivalent to the choice variables");
        }
    }

    //Clicking the answer validation fragment will call this
    public void incrementQuestionsAnswered()
    {
        ++questionsAnswered;

        if(questionsAnswered == allQuestions.size())
        {
            quizComplete = true;
        }
    }

    //Clicking the answer validation fragment will call this
    public boolean isQuizComplete()
    {
        return quizComplete;
    }

    //Clicking the answer validation fragment might call this
    public void incrementCorrectAnswerCount()
    {
        ++questionsAnsweredCorrectly;
    }

    //Clicking the answer validation fragment will call this
    public boolean wasTheChoiceCorrect()
    {
        if(currentlySelectedChoice.equals(correctChoice) == true)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /*Clicking the answer validation fragment will call this
      Set the next question to be displayed after the current one is answered */
    public void setNextQuestions()
    {
        ++currentQuestionIndex;
        currentQuestion = allQuestions.get(currentQuestionIndex);

        question.setText(currentQuestion[0]);
        choice1.setText(currentQuestion[1]);
        choice2.setText(currentQuestion[2]);
        choice3.setText(currentQuestion[3]);
        choice4.setText(currentQuestion[4]);
        correctChoice = currentQuestion[5];

        //make them all white so the previous question's selected answer view doesn't stay blue
        choice1.setBackgroundColor(Color.parseColor("#ffffff"));
        choice2.setBackgroundColor(Color.parseColor("#ffffff"));
        choice3.setBackgroundColor(Color.parseColor("#ffffff"));
        choice4.setBackgroundColor(Color.parseColor("#ffffff"));
    }

    public void setTextForAnswerValidity(String str)
    {
        textForAnswerValidity = str;
    }

    //sets the text that will be used for FragmentAnswerValidity
    public void checkAnswerAndGenerateResponse(TextView view, String correctAnswer)
    {
        if(view.getText().equals(correctAnswer) == true)
        {
            setTextForAnswerValidity("Correct");
        }
        else
        {
            setTextForAnswerValidity("Incorrect.\nThe correct answer is: " + correctChoice);
        }
    }

    public void goToResultScreen()
    {
        Intent intentObject = new Intent(this, QuizResult.class);

        //Pass these values so name and score can be displayed on result screen
        intentObject.putExtra("playerName", playerName);
        intentObject.putExtra("numOfCorrectAnswers", questionsAnsweredCorrectly);
        intentObject.putExtra("numOfQuestions", allQuestions.size());

        startActivity(intentObject); //Go to next screen
    }

    public void spawnAnswerButtonFrag()
    {
        //This section of code causes a crash. Replacing works fine without delete then replace/add
        // Left here for note purposes. Code from slides.
        /*
        if(fragNull == false)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            // Check to see if the validity fragment is already showing
            AnswerValidityFrag answerValidityFrag = (AnswerValidityFrag) fragmentManager.findFragmentById(R.id.AnswerValidity);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(answerValidityFrag).commit();
        }*/
        AnswerButtonFrag answerButtonFrag = AnswerButtonFrag.newInstance("", "");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.FragmentFrameID, answerButtonFrag).addToBackStack(null).commit();
    }

    public void spawnAnswerValidityFrag()
    {
        // Note: Had similar commented out code block like the one in spawnAnswerbuttonFrag

        AnswerValidityFrag answerValidityFrag = AnswerValidityFrag.newInstance("", "");
        answerValidityFrag.setValue(textForAnswerValidity);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.FragmentFrameID, answerValidityFrag).addToBackStack(null).commit();
    }

}