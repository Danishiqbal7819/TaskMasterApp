package com.example.taskmaster.Adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.Model.TasksData;
import com.example.taskmaster.R;
import com.example.taskmaster.Utils.database;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TODOAdapter extends RecyclerView.Adapter<TODOAdapter.ViewHolder> {

    private final List<TasksData> data;
    private final Context context;
    private final Runnable onTasksChanged;

    public TODOAdapter(Context context, List<TasksData> data, Runnable onTasksChanged) {
        this.data = data;
        this.context = context;
        this.onTasksChanged = onTasksChanged;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        database databaseHelper = new database(context, "todoDatabase", null, 1);
        TasksData item = data.get(position);

        holder.taskNumberView.setText(context.getString(R.string.task_number_prefix)+":" + safeValue(item.taskno));
        holder.taskTitleInput.setText(safeValue(item.task));
        holder.taskTimeInput.setText(safeValue(item.time));

        holder.deleteButton.setOnClickListener(v -> {
            String taskNumber = safeValue(item.taskno);
            boolean deleted = databaseHelper.deleteData(taskNumber);
            if (deleted) {
                int adapterPosition = holder.getBindingAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    data.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                }
                Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show();
                onTasksChanged.run();
            } else {
                Toast.makeText(context, "Task number not found", Toast.LENGTH_SHORT).show();
            }
        });

        holder.updateButton.setOnClickListener(v -> {
            String updatedTitle = holder.taskTitleInput.getText().toString().trim();
            String updatedTime = holder.taskTimeInput.getText().toString().trim();
            String taskNumber = safeValue(item.taskno);
            callupdateDialog(databaseHelper,updatedTitle,updatedTime,taskNumber);
        });
        holder.markCompleteButton.setOnClickListener(v -> {
        });
    }

    private void callupdateDialog(database databaseHelper,String updatedTitle, String updatedTime, String taskNumber) {

        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.updatelayout, null);

        // Find views
        EditText taskTitle = dialogView.findViewById(R.id.tasktitle);
        EditText taskTime = dialogView.findViewById(R.id.tasktime);
        Button updateBtn = dialogView.findViewById(R.id.buttonUpdate1);
        Button deleteBtn = dialogView.findViewById(R.id.buttondelete1);
        TextView taskNo = dialogView.findViewById(R.id.taskno);

        // Example task number
        taskNo.setText(taskNumber);
        taskTitle.setText(updatedTitle);
        taskTime.setText(updatedTime);

        // Make time field non-keyboard clickable
        taskTime.setFocusable(false);
        taskTime.setClickable(true);

        // Open Date + Time Picker when clicking taskTime
        taskTime.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context,
                    (view, selectedYear, selectedMonth, selectedDay) -> {

                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                context,
                                (timeView, selectedHour, selectedMinute) -> {

                                    // Convert to 12-hour format with AM/PM
                                    String amPm = (selectedHour >= 12) ? "PM" : "AM";

                                    int formattedHour = selectedHour % 12;
                                    if (formattedHour == 0) {
                                        formattedHour = 12;
                                    }

                                    String formattedTime = String.format(
                                            Locale.getDefault(),
                                            "%02d:%02d %s",
                                            formattedHour,
                                            selectedMinute,
                                            amPm
                                    );

                                    // Final format: 11/12/2024  11:00 AM
                                    String selectedDateTime =
                                            selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear
                                                    + "  " + formattedTime;

                                    taskTime.setText(selectedDateTime);

                                },
                                hour,     // already defined above
                                minute,   // already defined above
                                false
                        );

                        timePickerDialog.show();

                    },
                    year,
                    month,
                    day
            );

            datePickerDialog.show();
        });

        // Create AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Update button click
        updateBtn.setOnClickListener(v -> {
            ;
            ;
            ;
            boolean updated = databaseHelper.updateData(taskNo.getText().toString().trim(), taskTitle.getText().toString().trim(), taskTime.getText().toString().trim());
            if (updated) {
                Toast.makeText(context, "Task updated", Toast.LENGTH_SHORT).show();
                onTasksChanged.run();
            } else {
                Toast.makeText(context, "Task number not found", Toast.LENGTH_SHORT).show();
            }
            // Your update logic here
            dialog.dismiss();
        });

        // Delete button click
        deleteBtn.setOnClickListener(v -> {
            // Your delete logic here

            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private String safeValue(String value) {
        return value == null ? "" : value.trim();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView taskNumberView;
        final EditText taskTitleInput;
        final EditText taskTimeInput;
        final Button updateButton;
        final Button deleteButton;
        final Button markCompleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskNumberView = itemView.findViewById(R.id.taskno);
            taskTitleInput = itemView.findViewById(R.id.tasktitle);
            taskTimeInput = itemView.findViewById(R.id.tasktime);
            updateButton = itemView.findViewById(R.id.buttonUpdate1);
            deleteButton = itemView.findViewById(R.id.buttondelete1);
            markCompleteButton = itemView.findViewById(R.id.buttonComplete);
        }
    }
}
