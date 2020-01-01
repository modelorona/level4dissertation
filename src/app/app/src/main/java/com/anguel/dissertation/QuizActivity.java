package com.anguel.dissertation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anguel.dissertation.logger.Logger;
import com.anguel.dissertation.persistence.userdata.UserData;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {


    private int total = 0;
    private final int AMOUNT_OF_QUESTIONS = 20;
    private int totalQuestionsSoFar = 0;
    private TextView question;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        setTitle(R.string.title);

        id = getUserID();

//        get all buttons
        Button ans1 = (Button) findViewById(R.id.ans1);
        Button ans2 = (Button) findViewById(R.id.ans2);
        Button ans3 = (Button) findViewById(R.id.ans3);
        Button ans4 = (Button) findViewById(R.id.ans4);
        Button ans5 = (Button) findViewById(R.id.ans5);

//        attach handler to all buttons
        ans1.setOnClickListener(this);
        ans2.setOnClickListener(this);
        ans3.setOnClickListener(this);
        ans4.setOnClickListener(this);
        ans5.setOnClickListener(this);

//        set initial question text
        question = (TextView) findViewById(R.id.question);
        question.setText(R.string.q1);

    }

        public String getUserID() {
//        preferencemanager was deprecated
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String id = sharedPref.getString(getString(R.string.shprefprefix)+"_ID", "");
        if (id.equalsIgnoreCase("")) {
            UUID g = UUID.randomUUID();
            id = g.toString();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.shprefprefix)+"_ID", id);
            editor.apply();
        }
        return id;
    }

    @Override
    public void onClick(View v) {
        int value;
        switch (v.getId()) {
            case R.id.ans2:
                value = 1;
                break;
            case R.id.ans3:
                value = 2;
                break;
            case R.id.ans4:
                value = 3;
                break;
            case R.id.ans5:
                value = 4;
                break;
            case R.id.ans1:
            default: // set a default value of 0. the total will not change
                value = 0;
                break;
        }
        total += value;
        if (totalQuestionsSoFar++ < AMOUNT_OF_QUESTIONS - 1) {
            updateQuestion(getBaseContext(), totalQuestionsSoFar);
        } else {
            finishQuiz();
        }
    }

    public void updateQuestion(Context context, int index) {
        String resource = String.format("q%s", String.valueOf(index + 1));
        int requestedId = context.getResources().getIdentifier(resource, "string", context.getPackageName());
        question.setText(getString(requestedId));
    }

    public void finishQuiz() {
//        save score before doing anything else
        Logger logger = new Logger();
        try {
            logger.saveSiasScore(getApplicationContext(), UserData.builder().userId(id).sias(total).build());
//        tell the user what has happened. give them chance to read more about the score
            AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
            builder.setMessage("Thank you for taking the test. Your SIAS score is " + total + ". If you would like to know more about this test, please click the left button below to learn more.")
                    .setTitle("Thank you")
                    .setCancelable(false);

            builder.setPositiveButton("Finish", (dialog, which) -> finish());

            builder.setNeutralButton("Learn more", (dialog, which) -> {
                Intent learnMoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.sias_url)));
                startActivity(learnMoreIntent);
                finish();
            });

            Dialog d = builder.create();
            d.setCanceledOnTouchOutside(false);
            d.show();

        } catch (InterruptedException | ExecutionException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
            builder.setTitle("Test could not save")
                    .setMessage("A system error occured and the test score could not be saved. Please try again by clicking the button button, or if the error persists, quit and immediately email me at 2255541h@student.gla.ac.uk.")
                    .setCancelable(false);

            builder.setPositiveButton("Try again", (dialog, which) -> {
                finishQuiz(); // cheeky
            });

            builder.setNeutralButton("Cancel", (dialog, which) -> {
                finish();
            });

            Dialog d = builder.create();
            d.setCanceledOnTouchOutside(false);
            d.show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
