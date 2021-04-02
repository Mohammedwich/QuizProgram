package com.example.quizprogram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

public class CreateEditQuiz extends AppCompatActivity
{
    EditText quizFileNameField;
    EditText quizTitleField;
    EditText questionField;
    RadioGroup answersRadioGroup;
    RadioButton answer1Radio;
    EditText answer1Text;
    RadioButton answer2Radio;
    EditText answer2Text;
    RadioButton answer3Radio;
    EditText answer3Text;
    RadioButton answer4Radio;
    EditText answer4Text;

    String mode; //will be "create" or "edit" and used to determine functionality in this file
    Serializable serializedQuiz;
    Quiz selectedQuiz; //used when mode is edit

    //each element should be an array of 6 items: question, 4 answers, correct answer
    ArrayList<String[]> listOfQuestions = new ArrayList<>(5);
    //Will be given to an adapter so it can display them in a recyclerView
    ArrayList<String> listOfQuestionTitles = new ArrayList<>(5);

    RecyclerView questionTitlesRecyclerView;
    //While the adapter was made for quiz topics initially, it can work to display any arraylist of strings
    // so here it will be used to display a list of question titles
    QuizTopicAdapter questionTitlesAdapter;

    int oldSelectedQuestionPosition = -1; //will be used to notify adapter of change
    View oldSelectedQuestionView = null; //will be used to un-highlight a previously selected quiz.
    String selectedQuestion = "";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_quiz);

        questionTitlesRecyclerView = findViewById(R.id.rvCreateQuizQuestionTitlesID);
        quizFileNameField = findViewById(R.id.createQuizFileNameID);
        quizTitleField = findViewById(R.id.createQuizTitleID);
        questionField = findViewById(R.id.createQuizQuestionID);
        //answersRadioGroup = findViewById(R.id.createQuizAnsRadGrpID);
        answer1Radio = findViewById(R.id.createQuizAns1RadioID);
        answer1Text = findViewById(R.id.createQuizAns1TextID);
        answer2Radio = findViewById(R.id.createQuizAns2RadioID);
        answer2Text = findViewById(R.id.createQuizAns2TextID);
        answer3Radio = findViewById(R.id.createQuizAns3RadioID);
        answer3Text = findViewById(R.id.createQuizAns3TextID);
        answer4Radio = findViewById(R.id.createQuizAns4RadioID);
        answer4Text = findViewById(R.id.createQuizAns4TextID);

        Intent theIntent = getIntent();
        mode = theIntent.getStringExtra("mode");

        //initializations if create mode
        if(mode.equalsIgnoreCase("create") == true)
        {
            quizFileNameField.setText("Quiz");

            //create and set adapter for the recyclerView
            questionTitlesAdapter = new QuizTopicAdapter(listOfQuestionTitles, this);
            questionTitlesRecyclerView.setAdapter(questionTitlesAdapter);
            questionTitlesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }


        //initializations if edit mode
        if(mode.equalsIgnoreCase("edit") == true)
        {
            serializedQuiz = theIntent.getSerializableExtra("chosenQuiz");
            selectedQuiz = (Quiz) serializedQuiz;

            listOfQuestions = selectedQuiz.getListOfQuestions();

            for(int currentQuestion = 0; currentQuestion < listOfQuestions.size(); currentQuestion++)
            {
                //get the actual question from each set of Question-answers
                listOfQuestionTitles.add(listOfQuestions.get(currentQuestion)[0]);
            }

            //create and set adapter for the recyclerView
            questionTitlesAdapter = new QuizTopicAdapter(listOfQuestionTitles, this);
            questionTitlesRecyclerView.setAdapter(questionTitlesAdapter);
            questionTitlesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            quizFileNameField.setText("xxxxxxx");
            quizTitleField.setText(selectedQuiz.getQuizTitle());
            questionField.setText("select a question below");
        }




    }

    public void selectQuestion(View view, int clickedItemPosition)
    {
        String questionTitle = ((TextView)view).getText().toString();

        //Un-highlight old selections
        if(oldSelectedQuestionView != null && questionTitle.equals(selectedQuestion) != true)
        {
            oldSelectedQuestionView.setBackgroundColor(Color.parseColor("#ffffff")); //make old selected white

            if(oldSelectedQuestionPosition != -1 && questionTitle.equals(selectedQuestion) != true)
            {
                questionTitlesAdapter.notifyItemChanged(oldSelectedQuestionPosition);
            }
        }

        //selected question used as both current value and also checked as a previous value next click before it is reassigned in the block above
        selectedQuestion = questionTitle;
        //Record current values so next click can reference them as the previous values.
        oldSelectedQuestionView = view;
        oldSelectedQuestionPosition = clickedItemPosition;

        //Update the currently selected quiz to be highlighted blue
        view.setBackgroundColor(Color.parseColor("#1cd9ff"));
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

    public void runtest(View view)
    {
    }
}