package com.example.taskmaster.ui.Logout;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.taskmaster.R;
import com.example.taskmaster.Utils.database;

import java.util.Locale;

public class AddTaskFragment extends Fragment {

    private EditText taskNumberInput;
    private EditText taskTitleInput;
    private TextView taskTimeView;
    private Button saveButton;
    private Button openListButton;
    private database databaseHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);
        initView(view);
        initClick(view);
        clearInputs();
        return view;
    }

    private void initView(View view) {
        taskNumberInput = view.findViewById(R.id.taskno);
        taskTitleInput = view.findViewById(R.id.tasktitle);
        taskTimeView = view.findViewById(R.id.tasktime);
        saveButton = view.findViewById(R.id.buttoninsert);
        openListButton = view.findViewById(R.id.buttonOpenList);
        databaseHelper = new database(requireContext(), "todoDatabase", null, 1);
    }

    private void initClick(View view) {
        taskTimeView.setOnClickListener(v -> setTime());

        saveButton.setOnClickListener(v -> {
            if (!validateInputs()) {
                return;
            }

            boolean inserted = databaseHelper.InsertData(
                    valueOf(taskNumberInput),
                    valueOf(taskTitleInput),
                    valueOf(taskTimeView)
            );

            if (inserted) {
                Toast.makeText(getActivity(), "Task saved", Toast.LENGTH_SHORT).show();
                clearInputs();
            } else {
                Toast.makeText(getActivity(), "Could not save task", Toast.LENGTH_SHORT).show();
            }
        });

        openListButton.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.View_tasks));
    }

    private boolean validateInputs() {
        if (valueOf(taskNumberInput).isEmpty()) {
            taskNumberInput.setError("Enter a task number");
            taskNumberInput.requestFocus();
            return false;
        }
        if (valueOf(taskTitleInput).isEmpty()) {
            taskTitleInput.setError("Enter a task title");
            taskTitleInput.requestFocus();
            return false;
        }
        if (valueOf(taskTimeView).equals(getString(R.string.add_task_time_hint))
                || valueOf(taskTimeView).isEmpty()) {
            Toast.makeText(getActivity(), "Pick a task time", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void clearInputs() {
        taskNumberInput.setText("");
        taskTitleInput.setText("");
        taskTimeView.setText(R.string.add_task_time_hint);
        taskTimeView.setTextColor(requireContext().getColor(R.color.text_secondary));
    }

    private String valueOf(TextView view) {
        return view.getText().toString().trim();
    }

    private void setTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.DialogStyle,
                new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker view, int hours, int minutes) {
                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
                        taskTimeView.setText(formattedTime);
                        taskTimeView.setTextColor(requireContext().getColor(R.color.text_primary));
                    }
                }, 15, 56, false);
        timePickerDialog.show();
        timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
        timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
    }
}
