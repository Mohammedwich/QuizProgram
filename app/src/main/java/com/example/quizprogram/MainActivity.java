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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;


// This class is for first screen
public class MainActivity extends AppCompatActivity
{
    EditText playerNameEditText;
    String selectedQuiz = "";
    File theFileDir;
    File[] arrayOfQuizFiles; //Hold the quiz files after filtering directory
    ArrayList<Quiz> listOfLocalQuizzes = new ArrayList<Quiz>();
    ArrayList<String> localQuizTitles = new ArrayList<String>();

    RecyclerView aRecyclerView;

    //adapter declared here instead of only in onCreate so it can be referenced outside of onCreate since onItemClick in QuizTopicAdapter is tied to selectQuiz() in this class
    QuizTopicAdapter quizTopicAdapter;

    int oldSelectedQuizPosition = -1; //will be used to notify adapter of change
    View oldSelectedQuizView = null; //will be used to un-highlight a previously selected quiz.

    //Stuff for online quiz functionality
    //************************************************************
    String currentlyHighlightedOnlineQuizTitle;

    boolean directoriesFetchComplete = false; //this will be used to check if we have the titles before doing something
    boolean quizFetchComplete = false; //this will be used to check if we have the questions before doing something

    RadioButton currentlySelectedRadioButton;
    RadioButton localRadioButton;
    RadioButton onlineRadioButton;

    GetQuizDirectoriesTask getQuizDirectoriesTask;
    GetOnlineQuizzesTask getOnlineQuizzesTask;

    String selectedOnlineQuiz = "";
    ArrayList<String> quizDirectoriesList = new ArrayList<>();
    ArrayList<Quiz> listOfOnlineQuizzes = new ArrayList<Quiz>();
    ArrayList<String> onlineQuizTitles = new ArrayList<String>();

    int oldSelectedOnlineQuizPosition = -1; //will be used to notify adapter of change
    View oldSelectedOnlineQuizView = null; //will be used to un-highlight a previously selected quiz.

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
                listOfLocalQuizzes.add(extractQuiz(arrayOfQuizFiles[indexOfCurrentFile]));
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        //Fill the list of quiz titles we have
        for(int indexOfCurrentQuiz = 0; indexOfCurrentQuiz < listOfLocalQuizzes.size(); indexOfCurrentQuiz++)
        {
            localQuizTitles.add(listOfLocalQuizzes.get(indexOfCurrentQuiz).getQuizTitle());
        }

        /*RecyclerView uses an adapter, which uses a holder class to display each view.
         View in holder, adapter spawns holders, Recyclerview displays them all. */
        quizTopicAdapter = new QuizTopicAdapter(localQuizTitles, this);

        aRecyclerView.setAdapter(quizTopicAdapter);

        //layout managers lets us set a layout and control how it works like when to recycle and other stuff
        aRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        localRadioButton = findViewById(R.id.localRadioID);
        onlineRadioButton = findViewById(R.id.onlineRadioID);

        //Start with local toggled
        localRadioButton.toggle();
        currentlySelectedRadioButton = localRadioButton;

    } // onCreate end

    //Takes a txt quiz file and returns a quiz object that has a title and contains question objects
    public Quiz extractQuiz(File quizFile) throws FileNotFoundException
    {
        Quiz theQuiz = new Quiz();
        Scanner fileReader = new Scanner(quizFile);
        String quizTitle = fileReader.nextLine(); //First line should hold quiz title
        theQuiz.setQuizTitle(quizTitle);

        /*reader cursor will be at the start of each question after each loop.
          Assumes text file is structured properly. */
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
                /*if any line reads end up having no line, the loop will be broken to prevent an exception
                  Breaking will also make the current question not be added to the quiz.*/
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


    /*Save the selected quiz's name so when user clicks next, it knows which quiz questions to load.
    Note to self: Prof showed a different method for handling onItemClick situation with recycler view
        after this was written so check that later.
    */
    public void selectQuiz(View view, int clickedItemPosition)
    {
        String quizName = ((TextView)view).getText().toString();

        if (currentlySelectedRadioButton == localRadioButton)
        {
            //Un-highlight old selections
            if(oldSelectedQuizView != null && quizName.equals(selectedQuiz) != true)
            {
                oldSelectedQuizView.setBackgroundColor(Color.parseColor("#ffffff")); //make old selected white

                if(oldSelectedQuizPosition != -1 && quizName.equals(selectedQuiz) != true)
                {
                    quizTopicAdapter.notifyItemChanged(oldSelectedQuizPosition);
                }
            }

            //selected quiz used as both current value and also checked as a previous value next click before it is reassigned in the block above
            selectedQuiz = quizName;
            //Record current values so next click can reference them as the previous values.
            oldSelectedQuizView = view;
            oldSelectedQuizPosition = clickedItemPosition;

            //Update the currently selected quiz to be highlighted blue
            view.setBackgroundColor(Color.parseColor("#1cd9ff"));
        }
        else if (currentlySelectedRadioButton == onlineRadioButton)
        {
            //Un-highlight old selections
            if(oldSelectedOnlineQuizView != null && quizName.equals(selectedOnlineQuiz) != true)
            {
                oldSelectedOnlineQuizView.setBackgroundColor(Color.parseColor("#ffffff")); //make old selected white

                if(oldSelectedOnlineQuizPosition != -1 && quizName.equals(selectedOnlineQuiz) != true)
                {
                    quizTopicAdapter.notifyItemChanged(oldSelectedOnlineQuizPosition);
                }
            }

            //selected quiz used as both current value and also checked as a previous value next click before it is reassigned in the block above
            selectedOnlineQuiz = quizName;
            //Record current values so next click can reference them as the previous values.
            oldSelectedOnlineQuizView = view;
            oldSelectedOnlineQuizPosition = clickedItemPosition;

            //Update the currently selected quiz to be highlighted blue
            view.setBackgroundColor(Color.parseColor("#1cd9ff"));
        }

    }

    public void goNext(View view)
    {
        //Make sure name is valid, not default text or empty string
        if (playerNameEditText.getText().toString().compareTo("Enter name here") != 0 &&
                playerNameEditText.getText().toString().compareTo("") != 0)
        {

            if (currentlySelectedRadioButton == localRadioButton)
            {
                //print error message in console if next is clicked but no quiz selected
                if (selectedQuiz.equals("") == true)
                {
                    System.out.println("No quiz selected");
                }

                if (selectedQuiz.equals("") == false) //make sure there is a quiz selected
                {
                    Quiz quizToLoad = new Quiz();

                    //Take the correct quiz to pass to intent
                    for (int currentQuiz = 0; currentQuiz < listOfLocalQuizzes.size(); currentQuiz++)
                    {
                        if (selectedQuiz.equals(listOfLocalQuizzes.get(currentQuiz).getQuizTitle()))
                        {
                            quizToLoad = listOfLocalQuizzes.get(currentQuiz);
                        }
                    }

                    //In case something weird happens, print out this message
                    if (quizToLoad.getQuizTitle().equals(""))
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
            } else if (currentlySelectedRadioButton == onlineRadioButton)
            {
                //print error message in console if next is clicked but no quiz selected
                if (selectedOnlineQuiz.equals("") == true)
                {
                    System.out.println("No quiz selected");
                }

                if (selectedOnlineQuiz.equals("") == false)
                {
                    Quiz quizToLoad = new Quiz();

                    //Take the correct quiz to pass to intent
                    for (int currentQuiz = 0; currentQuiz < listOfOnlineQuizzes.size(); currentQuiz++)
                    {
                        if (selectedOnlineQuiz.equals(listOfOnlineQuizzes.get(currentQuiz).getQuizTitle()))
                        {
                            quizToLoad = listOfOnlineQuizzes.get(currentQuiz);
                        }
                    }

                    //In case something weird happens, print out this message
                    if (quizToLoad.getQuizTitle().equals(""))
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
        else
        {
            //Show a toast message telling user the name is invalid
            Toast.makeText(MainActivity.this, "Enter a valid name", Toast.LENGTH_LONG).show();
        }
    }

    public void radioClick(View view)
    {
        //If clicked when already selected, do nothing since it would be redundant and a waste of processing
        if(view == localRadioButton && currentlySelectedRadioButton != view)
        {
            //Clear the onlineQuizTitles list so titles don't get appended to it every time online
            // quizzes are loaded when the online radio button is activated
            onlineQuizTitles.clear();

            //null the selectedOnlineQuiz to avoid strange behavior when toggling local then online
            // which causes a situation where nothing is selected on screen but the old selected value is still saved
            selectedOnlineQuiz = "";

            /*RecyclerView uses an adapter, which uses a holder class to display each view.
            View in holder, adapter spawns holders, Recyclerview displays them all. */
            quizTopicAdapter = new QuizTopicAdapter(localQuizTitles, this);

            aRecyclerView.setAdapter(quizTopicAdapter);

            //layout managers lets us set a layout and control how it works like when to recycle and other stuff
            aRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        if(view == onlineRadioButton && currentlySelectedRadioButton != view)
        {
            //null the selectedOnlineQuiz to avoid strange behavior when toggling local then online
            // which causes a situation where nothing is selected on screen but the old selected value is still saved
            selectedQuiz = "";

            //reset to false so data can be fetched and titleFetchComplete can be set to true by the code that will fetch the data
            directoriesFetchComplete = false;
            quizFetchComplete = false;
            String quizDirectoriesUrl = getString(R.string.quiz_title_site); //get the url for quiz directories

            //run the async task here
            getQuizDirectoriesTask = new GetQuizDirectoriesTask(this); //create task object
            getQuizDirectoriesTask.execute(quizDirectoriesUrl); //execute task using url
            //After above code we should have a list of each quiz's directory in quizDirectoriesList
            // Also when getQuizDirectoriesTask completes it should call another task which will fill
            //  the list of online quizzes
            // The getQuizzesTask assigns a new adapter to the recycler view


            //Show a toast message to user so they know data is being fetched and loaded
            Toast.makeText(MainActivity.this, "Fetching and loading online quiz...", Toast.LENGTH_LONG).show();
        }

        //update what is currently selected
        currentlySelectedRadioButton = (RadioButton) view;
    }

    //same as extractQuiz but takes InputStream. Can scrap them both and make a template to avoid duplicate code
    public Quiz extractQuizFromInputStream(InputStream theStream)
    {
        Quiz theQuiz = new Quiz();
        Scanner stringReader = new Scanner(theStream);
        String quizTitle = stringReader.nextLine(); //First line should hold quiz title
        theQuiz.setQuizTitle(quizTitle);

        /*reader cursor will be at the start of each question after each loop.
          Assumes text file is structured properly. */
        while(stringReader.hasNextLine())
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
                /*if any line reads end up having no line, the loop will be broken to prevent an exception
                  Breaking will also make the current question not be added to the quiz.*/
                if(stringReader.hasNextLine())
                {
                    theQuestion = stringReader.nextLine();
                }
                else
                {
                    break;
                }

                if(stringReader.hasNextLine()) {
                    choice1 = stringReader.nextLine();
                }
                else{
                    break;
                }

                if(stringReader.hasNextLine()) {
                    choice2 = stringReader.nextLine();
                }
                else{
                    break;
                }

                if(stringReader.hasNextLine()) {
                    choice3 = stringReader.nextLine();
                }
                else{
                    break;
                }

                if(stringReader.hasNextLine()) {
                    choice4 = stringReader.nextLine();
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

        stringReader.close();
        return theQuiz;
    }


    //****************************************************************************
    //This class will be used to asynchronously get quiz directories from a website
    //They will be used to access the web pages holding quizzes
    private class GetQuizDirectoriesTask extends AsyncTask<String, Integer, ArrayList<String>>
    {
        ArrayList<String> quizDirectories;
        MainActivity context;

        public GetQuizDirectoriesTask(MainActivity theMainActivity)
        {
            quizDirectories = new ArrayList<String>(5);
            context = theMainActivity;
        }

        @Override
        protected ArrayList<String> doInBackground(String... urls)
        {
            URL u = null;
            String currentLine;
            Scanner lineReader;
            InputStream inStream = null;
            int response = 0; //for website connection response
            String theUrl = urls[0];

            //make the URL object
            try
            {
                u = new URL(theUrl);
            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            }

            URLConnection connection = null; //set up the connection

            //open the connection
            try
            {
                connection = u.openConnection();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            //create an http connection out of the connection previously made
            HttpURLConnection httpConn = (HttpURLConnection) connection;

            //check the response code of the connection
            try
            {
                response = httpConn.getResponseCode();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            if (response == HttpURLConnection.HTTP_OK) //this HTTP_OK is an enum
            {
                //Take website content and put in inStream
                try
                {
                    inStream = httpConn.getInputStream();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                try
                {
                    lineReader = new Scanner(inStream);

                    while (lineReader.hasNextLine())
                    {
                        currentLine = lineReader.nextLine();
                        quizDirectories.add(currentLine);
                    }

                    lineReader.close();
                } catch (Exception ex)
                {
                    System.out.println(ex.getMessage());
                }
            }
            httpConn.disconnect(); //disconnect since we are done to avoid wasting battery

            return quizDirectories;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result)
        {
            //Create an adapter with this string so it can be loaded into recycler view
            //pass this adapter a method in context that deals with updating adapter
            quizDirectoriesList = result;
            directoriesFetchComplete = true;

            //*******************************************************************
            //Now execute another task to build the quiz objects
            getOnlineQuizzesTask = new GetOnlineQuizzesTask(context);

            //make an array of url strings so it can be used as a parameter for the getOnlineQuizTask execute
            String quizParentDirUrl = getString(R.string.quiz_title_site_parent_dir); //partial url
            String [] urlArray = new String[result.size()];

            for(int currentDir = 0; currentDir < result.size(); currentDir++)
            {
                //append directory to the partial url to get a url for a quiz and add to array
                urlArray[currentDir] = quizParentDirUrl + result.get(currentDir);
            }

            getOnlineQuizzesTask.execute(urlArray);
        }
    }


    //This class will be used to asynchronously get quizzes from a website
    private class GetOnlineQuizzesTask extends AsyncTask<String, Integer, ArrayList<Quiz>>
    {
        ArrayList<Quiz> quizList;
        MainActivity context;

        public GetOnlineQuizzesTask(MainActivity theMainActivity)
        {
            quizList = new ArrayList<Quiz>(5);
            context = theMainActivity;
        }

        @Override
        protected ArrayList<Quiz> doInBackground(String... urls)
        {
            for(int currentDir = 0; currentDir < quizDirectoriesList.size(); currentDir++)
            {
                URL u = null;
                InputStream inStream = null;
                int response = 0; //for website connection reponse
                String theUrl = urls[currentDir];

                //make the URL object
                try
                {
                    u = new URL(theUrl);
                } catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }

                URLConnection connection = null; //set up the connection

                //open the connection
                try
                {
                    connection = u.openConnection();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                //create an http connection out of the connection previously made
                HttpURLConnection httpConn = (HttpURLConnection) connection;

                //check the response code of the connection
                try
                {
                    response = httpConn.getResponseCode();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                if (response == HttpURLConnection.HTTP_OK) //this HTTP_OK is an enum
                {
                    //Take website content and put in inStream then extract the quiz and add it to list
                    try
                    {
                        inStream = httpConn.getInputStream();
                        quizList.add(extractQuizFromInputStream(inStream));
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                httpConn.disconnect(); //disconnect since we are done to avoid wasting battery
            }

            return quizList;
        }

        @Override
        protected void onPostExecute(ArrayList<Quiz> result)
        {
            listOfOnlineQuizzes = result;
            quizFetchComplete = true;

            //fill up the list of online quiz titles
            for(int indexOfCurrentQuiz = 0; indexOfCurrentQuiz < listOfOnlineQuizzes.size(); indexOfCurrentQuiz++)
            {
                onlineQuizTitles.add(listOfOnlineQuizzes.get(indexOfCurrentQuiz).getQuizTitle());
            }

            //create adapter to load in the recycler view

            /*RecyclerView uses an adapter, which uses a holder class to display each view.
            View in holder, adapter spawns holders, Recyclerview displays them all. */
            quizTopicAdapter = new QuizTopicAdapter(onlineQuizTitles, context);

            aRecyclerView.setAdapter(quizTopicAdapter);

            //layout managers lets us set a layout and control how it works like when to recycle and other stuff
            aRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
    }
}