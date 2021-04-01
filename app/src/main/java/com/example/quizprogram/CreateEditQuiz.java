package com.example.quizprogram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class CreateEditQuiz extends AppCompatActivity
{
    ArrayList<String> listOfQuestionTitles = new ArrayList<>(5);

    RecyclerView questionTitlesRecyclerView;
    //While the adapter was made for quiz topics initially, it can work to display any arraylist of strings
    // so here it will be used to display a list of question titles
    QuizTopicAdapter questionTitlesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_quiz);

        questionTitlesRecyclerView = findViewById(R.id.rvCreateQuizQuestionTitlesID);

        //TODO: debug code, remove
        listOfQuestionTitles.add("TestQuestion 1");
        listOfQuestionTitles.add("TestQuestion 2");
        listOfQuestionTitles.add("TestQuestion 3");
        listOfQuestionTitles.add("TestQuestion 4");
        listOfQuestionTitles.add("TestQuestion 5");

        //create and set adapter for the recyclerView
        questionTitlesAdapter = new QuizTopicAdapter(listOfQuestionTitles, this);
        questionTitlesRecyclerView.setAdapter(questionTitlesAdapter);
        questionTitlesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void designateAsCorrectAnswer(View view)
    {
    }

    public void saveQuiz(View view)
    {
    }

    public void deleteQuiz(View view)
    {
    }

    public void clearQuiz(View view)
    {
    }

    public void doneQuiz(View view)
    {
    }
}