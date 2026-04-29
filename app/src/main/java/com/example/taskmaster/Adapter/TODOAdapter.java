package com.example.taskmaster.Adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.Model.TasksData;
import com.example.taskmaster.R;
import com.example.taskmaster.Utils.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        View view = LayoutInflater.from(context)
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        database databaseHelper = new database(context);
        TasksData item = data.get(position);

        int taskId = item.id;
        holder.taskNumberView.setTextColor(context.getColor(R.color.colorPrimarydark));
        holder.taskNumberView.setText(item.taskno);
        holder.taskTitleInput.setText(safeValue(item.task));
        holder.taskTimeInput.setText(safeValue(item.time));

        holder.markCompleteButton.setText("Done");
        holder.markCompleteButton.setVisibility(View.VISIBLE);
        holder.taskstatus.setText("Pending");
        holder.taskstatus.setTextColor(
                context.getColor(R.color.colorPrimarydark)
        );
        // -------------------------------
        // 1. If task is completed -> GREEN background only
        // -------------------------------
        if (databaseHelper.getStatus(taskId) == 1) {
            holder.markCompleteButton.setVisibility(View.GONE);
            holder.taskstatus.setText("Completed");
            holder.taskstatus.setTextColor(
                    context.getColor(R.color.light_green)
            );
        }


        // -------------------------------
        // 2. If task time is expired -> LIGHT RED background
        // only if task is not completed
        // -------------------------------
        else {
            try {
                String taskTime = safeValue(item.time);

                java.text.SimpleDateFormat sdf =
                        new java.text.SimpleDateFormat(
                                "dd/MM/yyyy  hh:mm a",
                                Locale.getDefault()
                        );

                java.util.Date taskDate = sdf.parse(taskTime);
                java.util.Date currentDate = new java.util.Date();

                if (taskDate != null && taskDate.before(currentDate)) {
                    holder.taskstatus.setText("Overtime");
                    holder.taskstatus.setTextColor(
                            context.getColor(R.color.light_red)
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // DELETE USING ID
        holder.deleteButton.setOnClickListener(v -> {

            boolean deleted = databaseHelper.deleteData(taskId);

            if (deleted) {

                int adapterPosition = holder.getBindingAdapterPosition();

                if (adapterPosition != RecyclerView.NO_POSITION) {
                    data.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                }

                Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show();
                onTasksChanged.run();

            } else {
                Toast.makeText(context, "Task not found", Toast.LENGTH_SHORT).show();
            }
        });

        // UPDATE USING ID
        holder.updateButton.setOnClickListener(v -> {

            String updatedTitle = holder.taskTitleInput
                    .getText()
                    .toString()
                    .trim();

            String updatedTime = holder.taskTimeInput
                    .getText()
                    .toString()
                    .trim();

            callUpdateDialog(
                    databaseHelper,
                    taskId,
                    updatedTitle,
                    updatedTime
            );
        });

        // MARK COMPLETE
        holder.markCompleteButton.setOnClickListener(v -> {

            boolean completed = databaseHelper.setComplete(taskId, 1);

            if (completed) {
                Toast.makeText(context, "Task marked complete", Toast.LENGTH_SHORT).show();
                onTasksChanged.run();
            } else {
                Toast.makeText(context, "Failed to update task", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callUpdateDialog(database databaseHelper,
                                  int taskId,
                                  String updatedTitle,
                                  String updatedTime) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.updatelayout, null);

        EditText taskTitle = dialogView.findViewById(R.id.tasktitle);
        EditText taskTime = dialogView.findViewById(R.id.tasktime);
        Button updateBtn = dialogView.findViewById(R.id.buttonUpdate1);
        Button cancelBtn = dialogView.findViewById(R.id.buttondelete1);
        TextView taskNo = dialogView.findViewById(R.id.taskno);

        taskNo.setText("Task " + taskId);
        taskTitle.setText(updatedTitle);
        taskTime.setText(updatedTime);

        taskTime.setFocusable(false);
        taskTime.setClickable(true);

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

                        openTimePicker(context, taskTime, selectedDay, selectedMonth, selectedYear, hour, minute);
                    },
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        });

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        dialog.setOnShowListener(d -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );
            }
        });

        updateBtn.setOnClickListener(v -> {

            boolean updated = databaseHelper.updateData(
                    taskId,
                    "Task " + taskId,
                    taskTitle.getText().toString().trim(),
                    taskTime.getText().toString().trim(),
                    0
            );

            if (updated) {
                Toast.makeText(context, "Task updated", Toast.LENGTH_SHORT).show();
                onTasksChanged.run();
            } else {
                Toast.makeText(context, "Task update failed", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

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
        final TextView taskstatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            taskNumberView = itemView.findViewById(R.id.taskno);
            taskTitleInput = itemView.findViewById(R.id.tasktitle);
            taskTimeInput = itemView.findViewById(R.id.tasktime);
            updateButton = itemView.findViewById(R.id.buttonUpdate1);
            deleteButton = itemView.findViewById(R.id.buttondelete1);
            markCompleteButton = itemView.findViewById(R.id.buttonComplete);
            taskstatus = itemView.findViewById(R.id.taskStatus);
        }
    }
    private void openTimePicker(Context context,
                                EditText taskTime,
                                int day,
                                int month,
                                int year,
                                int hour,
                                int minute) {

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
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
                            day + "/" + (month + 1) + "/" + year + "  " + formattedTime;

                    try {
                        SimpleDateFormat sdf =
                                new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());

                        Date selectedDate = sdf.parse(selectedDateTime);
                        Date currentDate = new Date();

                        if (selectedDate != null && selectedDate.before(currentDate)) {

                            Toast.makeText(context,
                                    "Please select a future time",
                                    Toast.LENGTH_SHORT).show();

                            taskTime.setText("");

                            // 🔥 reopen fresh picker safely
                            new Handler(Looper.getMainLooper()).postDelayed(() ->
                                    openTimePicker(context, taskTime, day, month, year, hour, minute), 200);

                        } else {
                            taskTime.setText(selectedDateTime);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                },
                hour,
                minute,
                false
        );

        timePickerDialog.show();
    }
}