package com.example.taskmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<String> taskList;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        sharedPreferences = getSharedPreferences();
        Map taskMap = sharedPreferences.getAll();
        taskList = mapToList(taskMap);

        recyclerView.setAdapter(new Adapter(taskList, getApplicationContext()));
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();

            switch (direction){

                case ItemTouchHelper.LEFT:

                    Task task = getTask(position);
                    updateTask(position, task);
                    recyclerView.getAdapter().notifyDataSetChanged();
                    break;

                case ItemTouchHelper.RIGHT:

                    removeTaskFromSharedPref(position);
                    taskList.remove(position);
                    recyclerView.getAdapter().notifyDataSetChanged();
                    break;
            }
        }
    };

    private SharedPreferences getSharedPreferences() {

        SharedPreferences sharedPreferences = getSharedPreferences("SharedPref", MODE_PRIVATE);
        return sharedPreferences;
    }

    private void removeTaskFromSharedPref(int position){

        Task task = getTask(position);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.remove(getTaskKey(task)).apply();
    }

    private void updateTask(int position, Task task) {

        String key = getTaskKey(task);
        task.setStatus(true);

        Gson gson = new Gson();
        String json = gson.toJson(task);
        taskList.set(position, json);

        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putString(key, json).apply();
    }

    private Task getTask(int position) {

        Gson gson = new Gson();
        String json = taskList.get(position);
        return gson.fromJson(json, Task.class);
    }

    private String getTaskKey(Task task){

        Gson gson = new Gson();
        String jsonTask = gson.toJson(task);

        String key = "";

        for (Map.Entry<String, ?> entry: sharedPreferences.getAll().entrySet()) {
            if (jsonTask.equals(entry.getValue())) {

                key = entry.getKey();
            }
        }

        return key;
    }

    protected void onResume() {
        super.onResume();
        Map taskMap = sharedPreferences.getAll();
        taskList = mapToList(taskMap);

        recyclerView.setAdapter(new Adapter(taskList, getApplicationContext()));
    }

    private ArrayList mapToList(Map map){

        ArrayList<String> list = new ArrayList<>();

        for (Object task : map.values()){

            list.add((String)task);
        }

        return list;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.toDo:
            case R.id.email:
            case R.id.phone:
            case R.id.meeting:
                Intent taskIntent = new Intent(getApplicationContext(), AddTaskActivity.class);;
                taskIntent.putExtra("type", item.getTitle());
                startActivity(taskIntent);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        public Adapter(ArrayList<String> taskList, Context context) {

        }

        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_view, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(Adapter.ViewHolder holder, int position) {

            Task task = getTask(position);

            holder.icon.setImageResource(findIcon(task.getType()));
            holder.text.setText(task.getTitle() + "\nDue: " + task.getDeadline());
            holder.checkbox.setChecked(task.getStatus());
        }

        private int findIcon(String type){

            int result = 0;

            switch (type) {

                case "TODO":

                    return android.R.drawable.ic_menu_agenda;
                case "EMAIL":

                    return android.R.drawable.ic_dialog_email;
                case "PHONE":

                    return android.R.drawable.ic_menu_call;
                case "MEETING":

                    return android.R.drawable.ic_menu_my_calendar;
            }

            return result;
        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }

        public  class ViewHolder extends RecyclerView.ViewHolder {

            protected ImageView icon;
            protected TextView text;
            protected CheckBox checkbox;

            public ViewHolder(View itemView) {
                super(itemView);
                icon = (ImageView) itemView.findViewById(R.id.task_item_image);
                text = (TextView) itemView.findViewById(R.id.task_item_text);
                checkbox = (CheckBox) itemView.findViewById(R.id.task_item_cb);
            }
        }
    }
}
