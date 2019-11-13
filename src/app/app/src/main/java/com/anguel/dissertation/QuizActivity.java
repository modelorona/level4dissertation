package com.anguel.dissertation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        setTitle(R.string.title);

        Button ans1 = (Button) findViewById(R.id.ans1);
        Button ans2 = (Button) findViewById(R.id.ans2);
        Button ans3 = (Button) findViewById(R.id.ans3);
        Button ans4 = (Button) findViewById(R.id.ans4);
        Button ans5 = (Button) findViewById(R.id.ans5);

        ans1.setOnClickListener(this);
        ans2.setOnClickListener(this);
        ans3.setOnClickListener(this);
        ans4.setOnClickListener(this);
        ans5.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        Log.d("ONCLICK", String.valueOf(v.getId()));
//        switch(v.getId()) {
//            case R.id.ans1:
//                break;
//
//            case R.id.ans2:
//                break;
//
//
//        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
