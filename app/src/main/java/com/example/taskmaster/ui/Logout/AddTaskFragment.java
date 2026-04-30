package com.example.taskmaster.ui.Logout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.taskmaster.R;
import com.example.taskmaster.ViewModel.TaskViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskFragment extends Fragment {

    private TextView taskNumberInput;
    private EditText taskTitleInput;
    private TextView taskTimeView;
    private Button saveButton;
    private Button openListButton;
    private TaskViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);
        initView(view);
        initClick(view);
        clearInputs();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            int size = tasks.size();
            taskNumberInput.setText("Task " + (size + 1));
        });
    }

    private void initView(View view) {
        taskNumberInput = view.findViewById(R.id.taskno);
        taskTitleInput = view.findViewById(R.id.tasktitle);
        taskTimeView = view.findViewById(R.id.tasktime);
        saveButton = view.findViewById(R.id.buttoninsert);
        openListButton = view.findViewById(R.id.buttonOpenList);
    }

    private void initClick(View view) {
        taskTimeView.setOnClickListener(v -> setTime());

        saveButton.setOnClickListener(v -> {
            if (!validateInputs()) {
                return;
            }

            boolean inserted = viewModel.insertTask(
                    valueOf(taskNumberInput),
                    valueOf(taskTitleInput),
                    valueOf(taskTimeView)
            );

            if (inserted) {
                clearInputs();
                Navigation.findNavController(v).navigate(R.id.View_tasks);
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
        taskTitleInput.setText("");
        taskTimeView.setText(R.string.add_task_time_hint);
        taskTimeView.setTextColor(requireContext().getColor(R.color.text_secondary));
    }

    private String valueOf(TextView view) {
        return view.getText().toString().trim();
    }

    private void setTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            getContext(),
                            (timeView, selectedHour, selectedMinute) -> {
                                String amPm = (selectedHour >= 12) ? "PM" : "AM";
                                int formattedHour = selectedHour % 12;
                                if (formattedHour == 0) formattedHour = 12;

                                String formattedTime = String.format(
                                        Locale.getDefault(),
                                        "%02d:%02d %s",
                                        formattedHour,
                                        selectedMinute,
                                        amPm
                                );

                                String selectedDateTime =
                                        selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear
                                                + "  " + formattedTime;

                                try {
                                    SimpleDateFormat sdf =
                                            new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());

                                    Date selectedDate = sdf.parse(selectedDateTime);
                                    Date currentDate = new Date();

                                    if (selectedDate != null && selectedDate.before(currentDate)) {
                                        taskTimeView.setText("");
                                        taskTimeView.setHint("Deadline");
                                        Toast.makeText(getContext(),
                                                "Deadline cannot be in the past",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        taskTimeView.setText(selectedDateTime);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    taskTimeView.setText("");
                                }
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                    );
                    timePickerDialog.show();
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }
}
