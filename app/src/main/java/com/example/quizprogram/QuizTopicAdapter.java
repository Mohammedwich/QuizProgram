package com.example.quizprogram;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QuizTopicAdapter extends RecyclerView.Adapter<QuizTopicAdapter.TopicHolder>
{
    public class TopicHolder extends RecyclerView.ViewHolder
    {
        public TextView textView;
        public boolean isSelected;

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

                if (mContext instanceof MainActivity)
                {
                    ((MainActivity) mContext).selectQuiz(v);
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
