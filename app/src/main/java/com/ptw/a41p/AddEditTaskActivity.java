package com.ptw.a41p;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class AddEditTaskActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.ptw.a41p.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.ptw.a41p.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.ptw.a41p.EXTRA_DESCRIPTION";
    public static final String EXTRA_DUE_DATE = "com.ptw.a41p.EXTRA_DUE_DATE";

    private TextInputEditText editTextTitle;
    private TextInputEditText editTextDescription;
    private Button buttonDate;
    private long selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        buttonDate = findViewById(R.id.button_date);
        Button buttonSave = findViewById(R.id.button_save);

        selectedDate = System.currentTimeMillis();
        
        buttonDate.setOnClickListener(v -> showDatePicker());

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit Task");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            selectedDate = intent.getLongExtra(EXTRA_DUE_DATE, System.currentTimeMillis());
        } else {
            setTitle("Add Task");
        }

        buttonSave.setOnClickListener(v -> saveTask());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedDate);
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth);
                selectedDate = selected.getTimeInMillis();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.show();
    }

    private void saveTask() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        if (title.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_DUE_DATE, selectedDate);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }
} 