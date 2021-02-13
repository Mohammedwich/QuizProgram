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
import java.util.Arrays;

public class QuizQuestions extends AppCompatActivity
{
    boolean fragNull = true;
    String textForAnswerValidity = "...";

    Serializable serializedQuiz;
    Quiz selectedQuiz;
    ArrayList<String[]> allQuestions;
    String [] currentQuestion;

    int currentlySelectedChoice = 0;
    boolean isQuizComplete = false; //make true when questions run out so result screen is displayed

    TextView question;
    TextView choice1;
    TextView choice2;
    TextView choice3;
    TextView choice4;
    String correctChoice;

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

        allQuestions = selectedQuiz.getListOfQuestions();
        currentQuestion = allQuestions.get(0);

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
            currentlySelectedChoice = 1;
            choice1.setBackgroundColor(Color.parseColor("#1cd9ff")); //set blue
            choice2.setBackgroundColor(Color.parseColor("#ffffff")); //set white
            choice3.setBackgroundColor(Color.parseColor("#ffffff"));
            choice4.setBackgroundColor(Color.parseColor("#ffffff"));

            checkAnswerAndGenerateResponse((TextView)view, correctChoice);
        }
        else if (view == choice2)
        {
            currentlySelectedChoice = 2;
            choice1.setBackgroundColor(Color.parseColor("#ffffff"));
            choice2.setBackgroundColor(Color.parseColor("#1cd9ff"));
            choice3.setBackgroundColor(Color.parseColor("#ffffff"));
            choice4.setBackgroundColor(Color.parseColor("#ffffff"));

            checkAnswerAndGenerateResponse((TextView)view, correctChoice);
        }
        else if (view == choice3)
        {
            currentlySelectedChoice = 3;
            choice1.setBackgroundColor(Color.parseColor("#ffffff"));
            choice2.setBackgroundColor(Color.parseColor("#ffffff"));
            choice3.setBackgroundColor(Color.parseColor("#1cd9ff"));
            choice4.setBackgroundColor(Color.parseColor("#ffffff"));

            checkAnswerAndGenerateResponse((TextView)view, correctChoice);
        }
        else if (view == choice4)
        {
            currentlySelectedChoice = 4;
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

    public void spawnAnswerButtonFrag()
    {
        /* //This section of code causes a crash. Replacing works fine without delete then replace/add
        if(fragNull == false)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            // Check to see if the validity fragment is already showing
            AnswerValidityFrag answerValidityFrag = (AnswerValidityFrag) fragmentManager.findFragmentById(R.id.AnswerValidity);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(answerValidityFrag).commit();
        }*/
        AnswerButtonFrag answerButtonFrag = AnswerButtonFrag.newInstance("", "");
        //answerButtonFrag.setValue(answerButtonText);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.FragmentFrameID, answerButtonFrag).addToBackStack(null).commit();
        //TODO: should I use the ID of the fragment in the Quiz activity xml?

        fragNull = false;
    }

    public void spawnAnswerValidityFrag()
    {
        /* //This section of code causes a crash. Replacing works fine without delete then replace/add
        if(fragNull == false)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            // Check to see if the Answer button fragment is already showing
            AnswerButtonFrag answerButtonFrag = (AnswerButtonFrag) fragmentManager.findFragmentById(R.id.AnswerButton);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(answerButtonFrag).commit();
        }*/
        AnswerValidityFrag answerValidityFrag = AnswerValidityFrag.newInstance("", "");
        answerValidityFrag.setValue(textForAnswerValidity);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.FragmentFrameID, answerValidityFrag).addToBackStack(null).commit();

        fragNull = false;
    }

    public void runTest2ndScreen(View view)
    {
        for (String[] x : allQuestions)
        {
            System.out.println(Arrays.toString(x));
        }
    }


}