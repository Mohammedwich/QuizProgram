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

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnswerButtonFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnswerButtonFrag extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button answerButton;

    public AnswerButtonFrag()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnswerButtonFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static AnswerButtonFrag newInstance(String param1, String param2)
    {
        AnswerButtonFrag fragment = new AnswerButtonFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_answer_button, container, false);
        answerButton = rootView.findViewById(R.id.AnswerButton);
        answerButton.setOnClickListener(new ButtonListener());

        return rootView;
    }

    private class ButtonListener implements Button.OnClickListener
    {

        //Note: Does not check for unselected answer. Will just give you a 0 for the question if you select nothing.
        @Override
        public void onClick(View v)
        {
            QuizQuestions quiz = (QuizQuestions) getActivity();
            quiz.setTextForAnswerValidity(quiz.textForAnswerValidity);
            //when this AnswerButton fragment is clicked, spawn the answervalidity fragment instead of the AnswerButton frag
            quiz.spawnAnswerValidityFrag();
        }
    }
}