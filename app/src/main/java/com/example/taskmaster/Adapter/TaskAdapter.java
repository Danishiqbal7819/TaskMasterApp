package com.example.taskmaster.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.Model.TasksData;
import com.example.taskmaster.R;
import com.example.taskmaster.Utils.MyDbHelper;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final List<TasksData> data;
    private final Context context;
    private final Runnable onTasksChanged;
    private final String taskstatus;

    public TaskAdapter(Context context, List<TasksData> data,String taskstatus, Runnable onTasksChanged) {
        this.data = data;
        this.context = context;
        this.onTasksChanged = onTasksChanged;
        this.taskstatus=taskstatus;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.tasklist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MyDbHelper myDbHelperHelper = new MyDbHelper(context);
        TasksData item = data.get(position);

        int taskId = item.id;
        holder.taskTitleInput.setText(safeValue(item.task));
        holder.taskTimeInput.setText(safeValue(item.time));

        holder.taskstatus.setText(taskstatus);
        holder.taskstatus.setTextColor(
                context.getColor(R.color.colorPrimarydark)
        );
        holder.lltaskitems12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("filter_type", "COMPLETED");

                Navigation.findNavController(view)
                        .navigate(R.id.View_tasks, bundle);
//                Navigation.findNavController(view).navigate(R.id.View_tasks);
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

        final EditText taskTitleInput;
        final EditText taskTimeInput;
        final TextView taskstatus;
        final LinearLayout lltaskitems12;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitleInput = itemView.findViewById(R.id.tasktitle);
            taskTimeInput = itemView.findViewById(R.id.tasktime);
            taskstatus = itemView.findViewById(R.id.taskStatus);
            lltaskitems12 = itemView.findViewById(R.id.lltaskitems12);
        }
    }
}