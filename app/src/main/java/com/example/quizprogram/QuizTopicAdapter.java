/*Written by Mohammed Ahmed, msa190000
For CS4301.002, assignment 2, started Feb 7 2021.
Description:
The app will load up some quizzes out of text files and then offer the choices to the user in a
RecyclerView. They enter their name and pick a quiz and click next and take the quiz on the next
screen. Select an answer and click the answer button which will display the result. Click the
button again to load the next question. When done they are greeted and told their result on a
third screen. From the third screen they can return to the first.
*/

package com.example.quizprogram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//Note: This adapter expects to receive a MainActivity type object during construction. It is intended to work with the MainActivity class.
public class QuizTopicAdapter extends RecyclerView.Adapter<QuizTopicAdapter.TopicHolder>
{
    //Each instance of the holder class will hold a view with quiz title in it
    public class TopicHolder extends RecyclerView.ViewHolder
    {
        public TextView textView;
        public boolean isSelected; //for highlighting selected titles

        public TopicHolder(View view)
        {
            super(view);

            textView = view.findViewById(R.id.textViewQuizTitleID);
            isSelected = false;
        }
    }

    //############################################################

    private ArrayList<String> listOfQuizTitles;
    private Context mContext; //This will hold the main activity so the adapter can call functions defined inside it

    // Constructor takes an array of strings to be shown in list.
    public QuizTopicAdapter(ArrayList<String> theList, Context theContext)
    {
        listOfQuizTitles = theList;
        mContext = theContext;
    }

    @NonNull
    @Override
    // When a ViewHolder is created, return the holder with the View
    public TopicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_title, parent, false);
        return new TopicHolder(v);
    }

    @Override
    // When the View is bound, it gets its data to display
    public void onBindViewHolder(@NonNull TopicHolder holder, int position)
    {
        //Slides note: mQuizzes is an array of Strings. Array holding quiz topic names
        //Slides note: mQuizname is likely an individual TextView that is supposed to display the one quiz's topic name

        holder.textView.setText(listOfQuizTitles.get(position));

        holder.textView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Make sure mContext is the right object type then pass the view to selectQuiz()
                // so the title is put into selectedQuiz and highlighting is handled
                if (mContext instanceof MainActivity)
                {
                    int itemPos = holder.getLayoutPosition();
                    ((MainActivity) mContext).selectQuiz(v, itemPos);
                }
            }
        });
    }

    @Override
    // Function needed to determine how many will fit on screen
    public int getItemCount()
    {
        return listOfQuizTitles == null ? 0 : listOfQuizTitles.size();
    }


}
