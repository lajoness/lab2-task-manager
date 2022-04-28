package com.example.taskmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

public class AddTaskActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        sharedPreferences = getSharedPreferences("SharedPref", MODE_PRIVATE);
    }

    @Override
    protected void onStart() {

        super.onStart();

        TextView addTaskTitleTextView = findViewById(R.id.addTaskTitleTextView);
        addTaskTitleTextView.setText(getIntent().getStringExtra("type"));

        Button addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                EditText titleEditText = findViewById(R.id.titleEditText);
                EditText descriptionEditText = findViewById(R.id.descriptionEditText);
                EditText deadlineEditText = findViewById(R.id.deadlineEditText);

                Task newTask = new Task(titleEditText.getText().toString(), descriptionEditText.getText().toString(), getIntent().getStringExtra("type"), deadlineEditText.getText().toString());

                SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(newTask);
                System.out.println("SIZE: " + sharedPreferences.getAll().keySet().size());
                prefEditor.putString("Task" + findNextKey(sharedPreferences.getAll().keySet().size()), json);
                prefEditor.commit();

                finish();
            }
        });

    }

    private String findNextKey(int id){

        String key = "Task" + (id+1);

        if (sharedPreferences.getAll().keySet().contains(key)) {

            findNextKey(id+1);
        }

        return key;
    }

}
