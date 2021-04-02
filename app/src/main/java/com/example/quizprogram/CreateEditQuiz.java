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
import android.widget.Toast;

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
    Quiz newQuizObject; //will be used to handle adding, deleting questions and writing to file

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
    String selectedQuestionTitle = "";
    int currentSelectedQuestionPosition = -1; //this var might be useful for overwriting a question

    String currentlyMarkedCorrectAnswerString = "";



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
            newQuizObject = new Quiz();

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

            newQuizObject = selectedQuiz;

            listOfQuestions = selectedQuiz.getListOfQuestions();

            fillListOfQuestionTitles();

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
        if(oldSelectedQuestionView != null && questionTitle.equals(selectedQuestionTitle) != true)
        {
            oldSelectedQuestionView.setBackgroundColor(Color.parseColor("#ffffff")); //make old selected white

            if(oldSelectedQuestionPosition != -1 && questionTitle.equals(selectedQuestionTitle) != true)
            {
                questionTitlesAdapter.notifyItemChanged(oldSelectedQuestionPosition);
            }
        }

        //selected question used as both current value and also checked as a previous value next click before it is reassigned in the block above
        selectedQuestionTitle = questionTitle;
        currentSelectedQuestionPosition = clickedItemPosition;
        //Record current values so next click can reference them as the previous values.
        oldSelectedQuestionView = view;
        oldSelectedQuestionPosition = clickedItemPosition;

        //Update the currently selected quiz to be highlighted blue
        view.setBackgroundColor(Color.parseColor("#1cd9ff"));

        //can be used to refer to position when looking for a full question object
        currentSelectedQuestionPosition = clickedItemPosition;


        String theQuestion = listOfQuestions.get(clickedItemPosition)[0];
        String theAnswer1 = listOfQuestions.get(clickedItemPosition)[1];
        String theAnswer2 = listOfQuestions.get(clickedItemPosition)[2];
        String theAnswer3 = listOfQuestions.get(clickedItemPosition)[3];
        String theAnswer4 = listOfQuestions.get(clickedItemPosition)[4];
        String theCorrectAnswer = listOfQuestions.get(clickedItemPosition)[5];

        questionField.setText(theQuestion);
        answer1Text.setText(theAnswer1);
        answer2Text.setText(theAnswer2);
        answer3Text.setText(theAnswer3);
        answer4Text.setText(theAnswer4);

        if(theCorrectAnswer.equals(theAnswer1) == true) {
            answer1Radio.toggle();
        }
        else if(theCorrectAnswer.equals(theAnswer2) == true) {
            answer2Radio.toggle();
        }
        else if(theCorrectAnswer.equals(theAnswer3) == true) {
            answer3Radio.toggle();
        }
        else if(theCorrectAnswer.equals(theAnswer4) == true) {
            answer4Radio.toggle();
        }
        else {
            System.out.println("Error: No right answer matched so no radio toggled.");
        }

    }

    public void fillListOfQuestionTitles()
    {
        for(int currentQuestion = 0; currentQuestion < listOfQuestions.size(); currentQuestion++)
        {
            //get the actual question from each set of Question-answers
            listOfQuestionTitles.add(listOfQuestions.get(currentQuestion)[0]);
        }
    }

    public void designateAsCorrectAnswer(View view)
    {
        if(view == answer1Radio)
        {
            currentlyMarkedCorrectAnswerString = answer1Text.getText().toString();
            ((RadioButton)view).toggle();
        }
        else if(view == answer2Radio) {
            currentlyMarkedCorrectAnswerString = answer2Text.getText().toString();
            ((RadioButton)view).toggle();
        }
        else if(view == answer2Radio) {
            currentlyMarkedCorrectAnswerString = answer3Text.getText().toString();
            ((RadioButton)view).toggle();
        }
        else if(view == answer2Radio) {
            currentlyMarkedCorrectAnswerString = answer4Text.getText().toString();
            ((RadioButton)view).toggle();
        }
        else {
            System.out.println("Error: The selected radio button view does not match any of the 4 radio buttons.");
        }
    }

    public void saveQuiz(View view)
    {
        if(mode.equalsIgnoreCase("create") == true)
        {
            String theQuestion = questionField.getText().toString();
            String theAnswer1 = answer1Text.getText().toString();
            String theAnswer2 = answer2Text.getText().toString();
            String theAnswer3 = answer3Text.getText().toString();
            String theAnswer4 = answer4Text.getText().toString();
            String theCorrectAnswer = currentlyMarkedCorrectAnswerString;

            String [] theNewQuestion = {theQuestion, theAnswer1, theAnswer2, theAnswer3, theAnswer4, theCorrectAnswer};

            //make sure no field is blank before adding question to quiz object
            if(theQuestion.equals("") || theAnswer1.equals("") || theAnswer2.equals("")
                    || theAnswer3.equals("") || theAnswer4.equals("") || theCorrectAnswer.equals(""))
            {
                //Tell user they can't have empty fields
                Toast.makeText(CreateEditQuiz.this, "Question, answers, and correct answer must be set", Toast.LENGTH_LONG).show();
            }
            else {
                listOfQuestions.add(theNewQuestion);

                listOfQuestionTitles.add(theQuestion);
                //make recyclerView refresh by telling adapter about the new item
                //using index size-1 because the new question added at the end
                questionTitlesAdapter.notifyItemInserted(listOfQuestionTitles.size()-1);

                newQuizObject.addQuestion(theQuestion, theAnswer1, theAnswer2, theAnswer3, theAnswer4, theCorrectAnswer);
                saveQuizToFile();
                clearFields();

                //reset this after quiz saved so we never end up with a nonexistent correct answer and so can check if empty
                currentlyMarkedCorrectAnswerString = "";
            }
        }

        if(mode.equalsIgnoreCase("edit") == true)
        {

        }
    }

    public void deleteQuestion(View view)
    {
        int deletedQuestionPosition = currentSelectedQuestionPosition;
        boolean questionDeleteResult = newQuizObject.deleteQuestion(selectedQuestionTitle);
        System.out.println("Question delete result is" + questionDeleteResult + "for: " + selectedQuestionTitle);

        //renew all data to account for the deletion
        listOfQuestionTitles.clear();
        fillListOfQuestionTitles();

        listOfQuestions.clear();
        listOfQuestions = newQuizObject.getListOfQuestions();

        questionTitlesAdapter.notifyItemRemoved(deletedQuestionPosition);
        questionTitlesAdapter.notifyItemRangeChanged(deletedQuestionPosition, listOfQuestionTitles.size());

        //reset invalidated things caused by delete
        currentSelectedQuestionPosition = -1;
        oldSelectedQuestionView = null;

        clearFields();
    }

    public void clearFields()
    {
        questionField.setText("");
        answer1Text.setText("");
        answer2Text.setText("");
        answer3Text.setText("");
        answer4Text.setText("");
        answersRadioGroup.clearCheck();
    }

    public void clearQuiz(View view)
    {
        clearFields();
    }

    public void saveQuizToFile()
    {

    }

    public void doneQuiz(View view)
    {
    }

    public void runtest(View view)
    {
    }
}