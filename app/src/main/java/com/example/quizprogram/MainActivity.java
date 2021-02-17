//Written by Mohammed Ahmed, msa190000
//For CS4301.002, assignment 2, started Feb 7 2021.
//Description:
//The app will load up some quizzes out of text files and then offer the choices to the user in a
// RecyclerView. They enter their name and pick a quiz and click next and take the quiz on the next
// screen. Select an answer and click the answer button which will display the result. Click the
// button again to load the next question. When done they are greeted and told their result on a
// third screen. From the third screen they can return to the first.

package com.example.quizprogram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Scanner;


// This class is for first screen
public class MainActivity extends AppCompatActivity
{
    EditText playerNameEditText;
    String selectedQuiz = "";
    File theFileDir;
    File[] arrayOfQuizFiles; //Hold the quiz files after filtering directory
    ArrayList<Quiz> listOfQuizzes = new ArrayList<Quiz>();
    ArrayList<String> quizTitles = new ArrayList<String>();

    RecyclerView aRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerNameEditText = findViewById(R.id.PlayerName);
        aRecyclerView = findViewById(R.id.rvQuizTitlesID);

        theFileDir = getFilesDir();

        //create a filter that filters out files with names beginning in "Quiz" and ending in "txt"
        FilenameFilter theFilter = new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                if(name.startsWith("Quiz") && name.endsWith("txt"))
                {   return true;    }
                else
                {   return false;   }
            }
        };

        //make the fileDir we got list its files, that pass the filter, in the array
        arrayOfQuizFiles = theFileDir.listFiles(theFilter);

        //If there are no files, it will say so in the console.
        if(arrayOfQuizFiles.length == 0)
        {
            System.out.println("No files found");
        }

        //fill the list of quizzes with one quiz per file in arrayOfQuizFiles
        for(int indexOfCurrentFile = 0; indexOfCurrentFile < arrayOfQuizFiles.length; indexOfCurrentFile++)
        {
            try
            {
                listOfQuizzes.add(extractQuiz(arrayOfQuizFiles[indexOfCurrentFile]));
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        //Fill the list of quiz titles we have
        for(int indexOfCurrentQuiz = 0; indexOfCurrentQuiz < listOfQuizzes.size(); indexOfCurrentQuiz++)
        {
            quizTitles.add(listOfQuizzes.get(indexOfCurrentQuiz).getQuizTitle());
        }

        //RecyclerView uses an adapter, which uses a holder class to display each view.
        // View in holder, adapter spawns holders, Recyclerview displays them all.
        QuizTopicAdapter quizTopicAdapter = new QuizTopicAdapter(quizTitles, this);

        aRecyclerView.setAdapter(quizTopicAdapter);

        //layout managers lets us set a layout and control how it works like when to recycle and other stuff
        aRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    } // onCreate end

    //Takes a txt quiz file and returns a quiz object that has a title and contains question objects
    public Quiz extractQuiz(File quizFile) throws FileNotFoundException
    {
        Quiz theQuiz = new Quiz();
        Scanner fileReader = new Scanner(quizFile);
        String quizTitle = fileReader.nextLine(); //First line should hold quiz title
        theQuiz.setQuizTitle(quizTitle);

        //reader cursor will be at the start of each question after each loop.
        //Assumes text file is structured properly.
        while(fileReader.hasNextLine())
        {
            String theQuestion = "";
            String choice1 = "";
            String choice2 = "";
            String choice3 = "";
            String choice4 = "";
            String correctChoice = "";

            //Read 5 lines: A question and its 4 potential answers
            for(int i = 0; i < 5; i++)
            {
                //if any line reads end up having no line, the loop will be broken to prevent an exception
                // Breaking will also make the current question not be added to the quiz.
                if(fileReader.hasNextLine())
                {
                    theQuestion = fileReader.nextLine();
                }
                else
                {
                    break;
                }

                if(fileReader.hasNextLine()) {
                    choice1 = fileReader.nextLine();
                }
                else{
                    break;
                }

                if(fileReader.hasNextLine()) {
                    choice2 = fileReader.nextLine();
                }
                else{
                    break;
                }

                if(fileReader.hasNextLine()) {
                    choice3 = fileReader.nextLine();
                }
                else{
                    break;
                }

                if(fileReader.hasNextLine()) {
                    choice4 = fileReader.nextLine();
                }
                else{
                    break;
                }


                //set which answer is the correct one
                if(choice1.startsWith("*"))
                {
                    choice1 = choice1.replace("*", ""); //erase asterisk
                    correctChoice = choice1;
                }
                else if (choice2.startsWith("*"))
                {
                    choice2 = choice2.replace("*", "");
                    correctChoice = choice2;
                }
                else if (choice3.startsWith("*"))
                {
                    choice3 = choice3.replace("*", "");
                    correctChoice = choice3;
                }
                else if (choice4.startsWith("*"))
                {
                    choice4 = choice4.replace("*", "");
                    correctChoice = choice4;
                }
                else
                {
                    System.out.println("Error: None of the choices are marked correct with an asterisk at the beginning");
                }

                //The quiz object holds multiple questions. Add one more question to it.
                theQuiz.addQuestion(theQuestion, choice1, choice2, choice3, choice4, correctChoice);

            } //end of for loop, one question added to quiz per loop
        } //end of reading file

        fileReader.close();
        return theQuiz;
    }


    //Save the selected quiz's name so when user clicks next, it knows which quiz questions to load.
    //Note: selected topic must be clicked again to unselect it and make background white again.
    //  If multiple topics are clicked once each they will all become highlighted blue but the
    //  last topic clicked will be the selected one for the next button.
    //It is this way because I need a way to get all holders from a recycler view to be able to
    //  make them all white when one is clicked. onClickItemListener doesn't exist for RecyclerViews
    public void selectQuiz(View view)
    {
        String quizName = ((TextView)view).getText().toString();

        //If selected unselect and color white, else select and color blue
        if(quizName.equals(selectedQuiz) == true)
        {
            view.setBackgroundColor(Color.parseColor("#ffffff"));
            selectedQuiz = "";
        }
        else
        {
            view.setBackgroundColor(Color.parseColor("#1cd9ff"));
            selectedQuiz = quizName;
        }

    }

    public void goNext(View view)
    {
        if(selectedQuiz.equals("") == false)
        {
            Quiz quizToLoad = new Quiz();

            //Take the correct quiz to pass to intent
            for(int currentQuiz = 0; currentQuiz < listOfQuizzes.size(); currentQuiz++)
            {
                if(selectedQuiz.equals(listOfQuizzes.get(currentQuiz).getQuizTitle()))
                {
                    quizToLoad = listOfQuizzes.get(currentQuiz);
                }
            }

            //In case something weird happens, print out this message
            if(quizToLoad.getQuizTitle().equals(""))
            {
                System.out.println("Selected quiz does not match any available quizzes. Empty quiz passed to intent");
            }


            Intent intentObject = new Intent(this, QuizQuestions.class);

            // put the quiz in the intent
            intentObject.putExtra("chosenQuiz", quizToLoad);

            String theName = playerNameEditText.getText().toString();
            intentObject.putExtra("playerName", theName);

            startActivity(intentObject); //Go to next screen
        }

    }

}