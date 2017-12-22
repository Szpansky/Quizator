package com.apps.szpansky.quizator.DialogsFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.szpansky.quizator.R;
import com.apps.szpansky.quizator.SimpleData.NewQuestion;
import com.apps.szpansky.quizator.Tasks.AddQuestionTask;
import com.apps.szpansky.quizator.Tools.TextChecker;


public class AddQuestion extends DialogFragment {

    TextView questionCategory,
            questionText,
            questionA,
            questionB,
            questionC,
            questionD;

    Button addButton;

    RadioGroup correctAnswer;

    NewQuestion question = new NewQuestion();

    TextChecker textChecker = new TextChecker();

    public static AddQuestion newInstance() {
        AddQuestion addQuestion = new AddQuestion();

        addQuestion.setStyle(STYLE_NO_TITLE, 0);

        return addQuestion;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_question, container, false);

        questionCategory = view.findViewById(R.id.question_category);
        questionText = view.findViewById(R.id.question_text);
        questionA = view.findViewById(R.id.question_a);
        questionB = view.findViewById(R.id.question_b);
        questionC = view.findViewById(R.id.question_c);
        questionD = view.findViewById(R.id.question_d);
        correctAnswer = view.findViewById(R.id.correct_answer);

        addButton = view.findViewById(R.id.add_button);

        onClickListeners();

        return view;
    }

    private void onClickListeners() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureQuestion();

                if (textChecker.isValidBase(question.toString())) {
                    AddQuestionTask addQuestionTask = new AddQuestionTask(question, getFragmentManager(), getActivity().getBaseContext());
                    addQuestionTask.execute((Void) null);
                    dismiss();
                }else{
                    Toast.makeText(getContext(),getString(R.string.error_invalid_text_field),Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void configureQuestion() {
        if (questionCategory.getText().toString().isEmpty())
            question.setCategory(getString(R.string.empty));
        else
            question.setCategory(questionCategory.getText().toString());

        question.setQuestionText(questionText.getText().toString());
        question.setAnswerA(questionA.getText().toString());
        question.setAnswerB(questionB.getText().toString());
        question.setAnswerC(questionC.getText().toString());
        question.setAnswerD(questionD.getText().toString());
        question.setCorrectAnswer(getCorrectAnswer());


    }

    private String getCorrectAnswer(){
        switch (correctAnswer.getCheckedRadioButtonId()){
            case R.id.check_a:{
                return "a";
            }
            case R.id.check_b:{
                return "b";
            }
            case R.id.check_c:{
                return "c";
            }
            case R.id.check_d:{
                return "d";
            }
            default: return "x";
        }
    }
}
