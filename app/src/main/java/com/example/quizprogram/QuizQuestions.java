package com.example.quizprogram;

import androidx.appcompat.app.AppCompatActivity;

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
        }
        else if (view == choice2)
        {
            currentlySelectedChoice = 2;
            choice1.setBackgroundColor(Color.parseColor("#ffffff"));
            choice2.setBackgroundColor(Color.parseColor("#1cd9ff"));
            choice3.setBackgroundColor(Color.parseColor("#ffffff"));
            choice4.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        else if (view == choice3)
        {
            currentlySelectedChoice = 3;
            choice1.setBackgroundColor(Color.parseColor("#ffffff"));
            choice2.setBackgroundColor(Color.parseColor("#ffffff"));
            choice3.setBackgroundColor(Color.parseColor("#1cd9ff"));
            choice4.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        else if (view == choice4)
        {
            currentlySelectedChoice = 4;
            choice1.setBackgroundColor(Color.parseColor("#ffffff"));
            choice2.setBackgroundColor(Color.parseColor("#ffffff"));
            choice3.setBackgroundColor(Color.parseColor("#ffffff"));
            choice4.setBackgroundColor(Color.parseColor("#1cd9ff"));
        }
        else
        {
            System.out.println("Error: Clicked view is not equivalent to the choice variables");
        }
    }

    public void runTest2ndScreen(View view)
    {
        for (String[] x : allQuestions)
        {
            System.out.println(Arrays.toString(x));
        }
    }
}