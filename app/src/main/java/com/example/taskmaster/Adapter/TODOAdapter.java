package com.example.taskmaster.Adapter;

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

import com.example.taskmaster.Model.student;
import com.example.taskmaster.R;
import com.example.taskmaster.Utils.database;

import java.util.List;

public class TODOAdapter extends RecyclerView.Adapter<TODOAdapter.ViewHolder> {

    private final List<student> data;
    private final Context context;
    private final Runnable onTasksChanged;

    public TODOAdapter(Context context, List<student> data, Runnable onTasksChanged) {
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
        student item = data.get(position);

        holder.taskNumberView.setText(context.getString(R.string.task_number_prefix) + safeValue(item.taskno));
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

            boolean updated = databaseHelper.updateData(taskNumber, updatedTitle, updatedTime);
            if (updated) {
                item.task = updatedTitle;
                item.time = updatedTime;
                Toast.makeText(context, "Task updated", Toast.LENGTH_SHORT).show();
                onTasksChanged.run();
            } else {
                Toast.makeText(context, "Task number not found", Toast.LENGTH_SHORT).show();
            }
        });
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskNumberView = itemView.findViewById(R.id.taskno);
            taskTitleInput = itemView.findViewById(R.id.tasktitle);
            taskTimeInput = itemView.findViewById(R.id.tasktime);
            updateButton = itemView.findViewById(R.id.buttonUpdate1);
            deleteButton = itemView.findViewById(R.id.buttondelete1);
        }
    }
}
