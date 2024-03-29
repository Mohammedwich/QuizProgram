/*Written by Mohammed Ahmed, msa190000
For CS4301.002, assignment 4, started Apr 1 2021.
Description:
The app will load up some quizzes out of text files and then offer the choices to the user in a
RecyclerView. They enter their name and pick a quiz and click next and take the quiz on the next
screen. Select an answer and click the answer button which will display the result. Click the
button again to load the next question. When done they are greeted and told their result on a
third screen. From the third screen they can return to the first.
The app also supports fetching quizzes from a website and taking them instead of local quizzes.
It also allows the creation and editing of local quizzes by setting the player name to "professor".
*/

package com.example.quizprogram;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/*The classes implement Serializable so they can be passed as extra in an Intent
  Objects from this class will be used to hold quiz data from the txt files */
public class Quiz implements Serializable
{
    private class Question implements Serializable
    {
        private String theQuestion;
        private String choice1;
        private String choice2;
        private String choice3;
        private String choice4;
        private String correctChoice;

        public Question()
        {
        }

        public Question(String question, String choiceOne, String choiceTwo, String choiceThree, String choiceFour, String theRightChoice)
        {
            this();
            theQuestion = question;
            choice1 = choiceOne;
            choice2 = choiceTwo;
            choice3 = choiceThree;
            choice4 = choiceFour;
            correctChoice = theRightChoice;
        }

        public String getTheQuestion()
        {
            return theQuestion;
        }

        public String getChoice1()
        {
            return choice1;
        }

        public String getChoice2()
        {
            return choice2;
        }

        public String getChoice3()
        {
            return choice3;
        }

        public String getChoice4()
        {
            return choice4;
        }

        public String getCorrectChoice()
        {
            return correctChoice;
        }

        public void setTheQuestion(String theQuestion)
        {
            this.theQuestion = theQuestion;
        }

        public void setChoice1(String choice1)
        {
            this.choice1 = choice1;
        }

        public void setChoice2(String choice2)
        {
            this.choice2 = choice2;
        }

        public void setChoice3(String choice3)
        {
            this.choice3 = choice3;
        }

        public void setChoice4(String choice4)
        {
            this.choice4 = choice4;
        }

        public void setCorrectChoice(String correctChoice)
        {
            this.correctChoice = correctChoice;
        }

        @Override
        public String toString()
        {
            return "Question{" +
                    "theQuestion='" + theQuestion + '\'' +
                    ", choice1='" + choice1 + '\'' +
                    ", choice2='" + choice2 + '\'' +
                    ", choice3='" + choice3 + '\'' +
                    ", choice4='" + choice4 + '\'' +
                    ", correctChoice='" + correctChoice + '\'' +
                    '}';
        }
    }

    //################################################

    private String quizTitle;
    private ArrayList<Question> listOfQuestions;

    public Quiz()
    {
        quizTitle = "";
        listOfQuestions = new ArrayList<Question>();
    }

    public String getQuizTitle()
    {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle)
    {
        this.quizTitle = quizTitle;
    }

    /** Returns a list of arrays. Each array has 6 string elements in the order:
     * {theQuestion, theFirstChoice, theSecondChoice, theThirdChoice, theFourthChoice, theRightChoice}*/
    public ArrayList<String[]> getListOfQuestions()
    {
        ArrayList<String[]> listOfQuestionDataArrays = new ArrayList<String[]>();

        for(int currentQuestion = 0; currentQuestion < listOfQuestions.size(); currentQuestion++)
        {
            String theQuestion = listOfQuestions.get(currentQuestion).getTheQuestion();
            String theFirstChoice = listOfQuestions.get(currentQuestion).getChoice1();
            String theSecondChoice = listOfQuestions.get(currentQuestion).getChoice2();
            String theThirdChoice = listOfQuestions.get(currentQuestion).getChoice3();
            String theFourthChoice = listOfQuestions.get(currentQuestion).getChoice4();
            String theRightChoice = listOfQuestions.get(currentQuestion).getCorrectChoice();

            String[] questionData = {theQuestion, theFirstChoice, theSecondChoice, theThirdChoice, theFourthChoice, theRightChoice};

            listOfQuestionDataArrays.add(questionData);
        }

        return listOfQuestionDataArrays;
    }

    public void setQuestion(int position, String question, String choice1, String choice2, String choice3, String choice4, String theRightChoice)
    {
        Question newQuestion = new Question(question, choice1, choice2, choice3, choice4, theRightChoice);
        listOfQuestions.set(position, newQuestion);
    }

    public void setListOfQuestions(ArrayList<Question> listOfQuestions)
    {
        this.listOfQuestions = listOfQuestions;
    }

    public void addQuestion(String question, String choice1, String choice2, String choice3, String choice4, String theRightChoice)
    {
        Question aQuestion = new Question(question, choice1, choice2, choice3, choice4, theRightChoice);
        listOfQuestions.add(aQuestion);
    }

    /**returns true if it successfully deleted a question, false otherwise */
    public boolean deleteQuestion(String theQuestion)
    {
        int currentIndex = 0;
        boolean questionFound = false;

        for(; currentIndex < listOfQuestions.size(); currentIndex++)
        {
            String currentQuestion = listOfQuestions.get(currentIndex).getTheQuestion();
            if (currentQuestion.equals(theQuestion) == true)
            {
                questionFound = true;
                break;
            }
        }

        if(questionFound == true)
        {
            listOfQuestions.remove(currentIndex);
        }

        return questionFound;
    }

    public void deleteAllQuestions()
    {
        listOfQuestions.clear();
    }

    /** This method overwrites the given file*/
    public void writeToFile(File theFile) throws IOException
    {
        FileWriter writer = new FileWriter(theFile, false);

        writer.append(quizTitle + "\n");

        for(int currentQuestion = 0; currentQuestion < listOfQuestions.size(); currentQuestion++)
        {
            Question theCurrentQuestion = listOfQuestions.get(currentQuestion);
            String theRightAnswer = theCurrentQuestion.correctChoice;

            //write the question
            writer.append(theCurrentQuestion.theQuestion + "\n");

            //write choice 1
            if(theCurrentQuestion.choice1.equals(theRightAnswer) == true) {
                writer.append("*" + theCurrentQuestion.choice1 + "\n");
            }
            else {
                writer.append(theCurrentQuestion.choice1 + "\n");
            }

            //write choice 2
            if(theCurrentQuestion.choice2.equals(theRightAnswer) == true) {
                writer.append("*" + theCurrentQuestion.choice2 + "\n");
            }
            else {
                writer.append(theCurrentQuestion.choice2 + "\n");
            }

            //write choice 3
            if(theCurrentQuestion.choice3.equals(theRightAnswer) == true) {
                writer.append("*" + theCurrentQuestion.choice3 + "\n");
            }
            else {
                writer.append(theCurrentQuestion.choice3 + "\n");
            }

            //write choice 4
            if(theCurrentQuestion.choice4.equals(theRightAnswer) == true) {
                writer.append("*" + theCurrentQuestion.choice4 + "\n");
            }
            else {
                writer.append(theCurrentQuestion.choice4 + "\n");
            }
        } //end of for loop

        writer.close();
        System.out.println("Finished writing quiz to file");
    }

    @Override
    public String toString()
    {
        return "Quiz{" +
                "quizTitle='" + quizTitle + '\'' +
                ", listOfQuestions=" + listOfQuestions +
                '}';
    }
}
