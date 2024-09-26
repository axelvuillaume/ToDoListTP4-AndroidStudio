package com.example.todolisttp4;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todolisttp4.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "user_pref";
    private static final String USER_NAME_KEY = "user_name";
    private MaterialToolbar tool_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        String userName = sharedPreferences.getString(USER_NAME_KEY, "User");
        binding.userName.setTitle(userName);
        tool_bar = binding.userName;

        setSupportActionBar(tool_bar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.addJobButton.setOnClickListener(v -> addJob());

        loadJobs();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.rename) {
            editUserName();
        }

        return super.onOptionsItemSelected(item);

    }


    private void editUserName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setTitle("Edit Name");

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newName = input.getText().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(USER_NAME_KEY, newName);
            editor.apply();
            binding.userName.setTitle(newName);
        });
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void addJob() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setTitle("Add Job");

        builder.setPositiveButton("OK", (dialog, which) -> {
            String job = input.getText().toString();
            ToDoDatabaseHelper db = new ToDoDatabaseHelper(this);
            db.addJob(job);
            loadJobs();
        });
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void loadJobs() {
        ToDoDatabaseHelper db = new ToDoDatabaseHelper(this);
        Cursor cursor = db.getAllJobs();
        ArrayList<String> jobs = new ArrayList<>();
        ArrayList<Integer> jobIds = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                jobs.add(cursor.getString(1));
                jobIds.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, jobs);
        binding.jobsList.setAdapter(adapter);

        binding.jobsList.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
            deleteDialog.setTitle("Delete Job")
                    .setMessage("Are you sure you want to delete this job?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int jobId = jobIds.get(position);
                        db.deleteJob(jobId);
                        loadJobs();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });

        binding.jobsList.setOnItemClickListener((parent, view, position, id) -> {
            AlertDialog.Builder renameDialog = new AlertDialog.Builder(this);
            final EditText input = new EditText(this);
            renameDialog.setView(input);

            renameDialog.setTitle("Rename");
            int jobId = jobIds.get(position);

            renameDialog.setPositiveButton("OK", (dialog, which) -> {
                String newJob = input.getText().toString();
                db.updateJob(jobId, newJob);
                loadJobs();
            });

            renameDialog.setNegativeButton("Cancel", null);

            renameDialog.show();
        });
    }

}
