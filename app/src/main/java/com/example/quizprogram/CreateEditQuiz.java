package com.example.quizprogram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class CreateEditQuiz extends AppCompatActivity
{
    EditText quizFileNameField;
    EditText quizTitleField;
    EditText questionField;
    RadioGroup answersRadioGroup; //useless because it doesn't recognize nested RadioButtons as its children
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

    File theFileDir; //will write new quizzes to a file in this directory if we are in create mode
    String theFileToEditPath; //will overwrite this file if we are in edit mode

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
        answersRadioGroup = findViewById(R.id.createQuizAnsRadGrpID);
        answer1Radio = findViewById(R.id.createQuizAns1RadioID);
        answer1Text = findViewById(R.id.createQuizAns1TextID);
        answer2Radio = findViewById(R.id.createQuizAns2RadioID);
        answer2Text = findViewById(R.id.createQuizAns2TextID);
        answer3Radio = findViewById(R.id.createQuizAns3RadioID);
        answer3Text = findViewById(R.id.createQuizAns3TextID);
        answer4Radio = findViewById(R.id.createQuizAns4RadioID);
        answer4Text = findViewById(R.id.createQuizAns4TextID);

        //This is so the watcher can clear selected radios if text is edited so they must select again
        answer1Text.addTextChangedListener(new AnswerTextWatcher());
        answer2Text.addTextChangedListener(new AnswerTextWatcher());
        answer3Text.addTextChangedListener(new AnswerTextWatcher());
        answer4Text.addTextChangedListener(new AnswerTextWatcher());

        theFileDir = getFilesDir();

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

            questionField.setHint("Question title here");
        }


        //initializations if edit mode
        if(mode.equalsIgnoreCase("edit") == true)
        {
            theFileToEditPath = theIntent.getStringExtra("fileNamePath");

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
            questionField.setHint("Select a question below");
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

        clearCheckAllAnswerRadios(); //uncheck all to wipe any previous toggles so a new toggle can be set

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

    //The radio group is not working because each radio button is nested inside a LinearLayout
    // so the radio grouop does not recognize the buttons as its children. This func unchecks them all
    public void clearCheckAllAnswerRadios()
    {
        answer1Radio.setChecked(false);
        answer2Radio.setChecked(false);
        answer3Radio.setChecked(false);
        answer4Radio.setChecked(false);
    }

    public void designateAsCorrectAnswer(View view)
    {

        clearCheckAllAnswerRadios(); //uncheck all

        if(view == answer1Radio)
        {
            currentlyMarkedCorrectAnswerString = answer1Text.getText().toString();
            ((RadioButton)view).toggle(); //check the right one
        }
        else if(view == answer2Radio) {
            currentlyMarkedCorrectAnswerString = answer2Text.getText().toString();
            ((RadioButton)view).toggle(); //check the right one
        }
        else if(view == answer3Radio) {
            currentlyMarkedCorrectAnswerString = answer3Text.getText().toString();
            ((RadioButton)view).toggle(); //check the right one
        }
        else if(view == answer4Radio) {
            currentlyMarkedCorrectAnswerString = answer4Text.getText().toString();
            ((RadioButton)view).toggle(); //check the right one
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
            if(quizTitleField.getText().toString().equals("")
                    || theQuestion.equals("") || theAnswer1.equals("") || theAnswer2.equals("")
                    || theAnswer3.equals("") || theAnswer4.equals("") || theCorrectAnswer.equals(""))
            {
                //Tell user they can't have empty fields
                Toast.makeText(CreateEditQuiz.this, "Title, Question, answers, and correct answer must be set", Toast.LENGTH_LONG).show();
            }
            //make sure file name is valid
            else if(theFileToEditPath.startsWith("Quiz") == false)
            {
                //Tell user they need a valid file name
                Toast.makeText(CreateEditQuiz.this, "File name must begin with \"Quiz\"", Toast.LENGTH_LONG).show();
            }
            //make sure an answer's radio is marked
            else if(answer1Radio.isChecked() == false && answer2Radio.isChecked() == false
                    && answer3Radio.isChecked() == false &&answer4Radio.isChecked() == false)
            {
                //Tell user they need to check a radio button
                Toast.makeText(CreateEditQuiz.this, "check one of the radio buttons", Toast.LENGTH_LONG).show();
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
            String theQuestion = questionField.getText().toString();
            String theAnswer1 = answer1Text.getText().toString();
            String theAnswer2 = answer2Text.getText().toString();
            String theAnswer3 = answer3Text.getText().toString();
            String theAnswer4 = answer4Text.getText().toString();
            String theCorrectAnswer = currentlyMarkedCorrectAnswerString;

            String [] theNewQuestionObject = {theQuestion, theAnswer1, theAnswer2, theAnswer3, theAnswer4, theCorrectAnswer};

            listOfQuestions.set(currentSelectedQuestionPosition, theNewQuestionObject);
            listOfQuestionTitles.set(currentSelectedQuestionPosition, theQuestion);
            newQuizObject.setQuestion(currentSelectedQuestionPosition, theQuestion, theAnswer1, theAnswer2, theAnswer3, theAnswer4, theCorrectAnswer);

            questionTitlesAdapter.notifyItemChanged(currentSelectedQuestionPosition);

            saveQuizToFile();
            clearFields();
        }
    }

    public void deleteQuestion(View view)
    {
        //TODO: Consider invalidating the screen so it rerenders properly after deleting if issues pop up
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
        resetSelectedTrackers();

        clearFields();
    }

    public void clearFields()
    {
        questionField.setText("");
        answer1Text.setText("");
        answer2Text.setText("");
        answer3Text.setText("");
        answer4Text.setText("");
        clearCheckAllAnswerRadios();
    }

    public void clearQuiz(View view)
    {
        oldSelectedQuestionView.setBackgroundColor(Color.parseColor("#ffffff"));
        clearFields();
        resetSelectedTrackers();
    }

    //will be used when we clear the screen so weird stuff doesn't happen because adapter updates so nothing looks selected but in reality stuff is selected
    public void resetSelectedTrackers()
    {
        int oldSelectedQuestionPosition = -1;
        View oldSelectedQuestionView = null;
        String selectedQuestionTitle = "";
        int currentSelectedQuestionPosition = -1;

        String currentlyMarkedCorrectAnswerString = "";
    }

    public void saveQuizToFile()
    {
        //new quiz object should be created and filled with data if this function is called so just write it to file

        //The newQuizObject should maintain its state since it does not get re-initialized to
        // nothing since the screen is not reset at any point, so we can overwrite this new file
        // with everything in the newQuizObject including the newly added question every time save
        // is clicked with valid info and quizFileName is not changed

        //create new file here using the provided file name. Can overwrite it when updating its quiz
        if(mode.equalsIgnoreCase("create") == true)
        {
            File newFile = new File(quizFileNameField.getText().toString());

            try
            {
                newQuizObject.writeToFile(newFile);
            } catch (IOException e)
            {
                e.printStackTrace();
                System.out.println("Failed to write the newly created quiz to a file");
            }
        }

        //do not use provided file name if accessible. Overwrite quiz in the chosen quiz's file with modified quiz.
        if(mode.equalsIgnoreCase("edit") == true)
        {
            File theChosenQuizsFile = new File(theFileToEditPath);

            try
            {
                newQuizObject.writeToFile(theChosenQuizsFile);
            } catch (IOException e)
            {
                e.printStackTrace();
                System.out.println("Failed to write the edited quiz's file");
            }
        }
    }

    public void doneQuiz(View view)
    {
        //stack cleared, all valid changes should have been saved, re-create the
        // main screen so it gets updated and go to it
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //clears the stack of all the loaded activities
        startActivity(intent);
    }

    public void runtest(View view)
    {
        answer1Radio.setChecked(false);
        answer2Radio.setChecked(false);
        answer3Radio.setChecked(false);
        answer4Radio.setChecked(false);

    }

    //will be used so the EditText answers can uncheck the radio after text changes
    //This is done because if text is changed after a radio was clicked then the string radio recorded
    // when clicked and the new string will not match. Unselecting radios makes user select again after edit
    private class AnswerTextWatcher implements TextWatcher
    {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }

        @Override
        public void afterTextChanged(Editable s)
        {
            clearCheckAllAnswerRadios();
        }
    }
}

