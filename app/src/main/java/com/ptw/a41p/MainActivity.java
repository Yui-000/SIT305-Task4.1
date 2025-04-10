package com.ptw.a41p;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ptw.a41p.adapters.TaskAdapter;
import com.ptw.a41p.viewmodels.TaskViewModel;
import com.ptw.a41p.data.Task;

public class MainActivity extends AppCompatActivity {
    private TaskViewModel taskViewModel;
    private ActivityResultLauncher<Intent> addTaskLauncher;
    private ActivityResultLauncher<Intent> editTaskLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 ActivityResultLauncher
        addTaskLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String title = data.getStringExtra(AddEditTaskActivity.EXTRA_TITLE);
                    String description = data.getStringExtra(AddEditTaskActivity.EXTRA_DESCRIPTION);
                    long dueDate = data.getLongExtra(AddEditTaskActivity.EXTRA_DUE_DATE, 0);

                    Task task = new Task(title, description, dueDate);
                    taskViewModel.insert(task);
                }
            });

        editTaskLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    int id = data.getIntExtra(AddEditTaskActivity.EXTRA_ID, -1);
                    if (id == -1) {
                        return;
                    }

                    String title = data.getStringExtra(AddEditTaskActivity.EXTRA_TITLE);
                    String description = data.getStringExtra(AddEditTaskActivity.EXTRA_DESCRIPTION);
                    long dueDate = data.getLongExtra(AddEditTaskActivity.EXTRA_DUE_DATE, 0);

                    Task task = new Task(title, description, dueDate);
                    task.setId(id);
                    taskViewModel.update(task);
                }
            });

        FloatingActionButton buttonAddTask = findViewById(R.id.fab_add_task);
        buttonAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            addTaskLauncher.launch(intent);
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        TaskAdapter adapter = new TaskAdapter();
        recyclerView.setAdapter(adapter);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, adapter::submitList);

        adapter.setOnItemClickListener(task -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            intent.putExtra(AddEditTaskActivity.EXTRA_ID, task.getId());
            intent.putExtra(AddEditTaskActivity.EXTRA_TITLE, task.getTitle());
            intent.putExtra(AddEditTaskActivity.EXTRA_DESCRIPTION, task.getDescription());
            intent.putExtra(AddEditTaskActivity.EXTRA_DUE_DATE, task.getDueDate());
            editTaskLauncher.launch(intent);
        });

        adapter.setOnItemLongClickListener(task -> {
            new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", (dialog, which) -> taskViewModel.delete(task))
                .setNegativeButton("No", null)
                .show();
        });
    }
}