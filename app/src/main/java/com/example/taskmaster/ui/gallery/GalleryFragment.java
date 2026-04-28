package com.example.taskmaster.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.taskmaster.Model.student;
import com.example.taskmaster.R;
import com.example.taskmaster.Utils.database;

import java.util.List;

public class GalleryFragment extends Fragment {

    private TextView emptyRecentView;
    private LinearLayout[] taskCards;
    private TextView[] taskTitleViews;
    private TextView[] taskMetaViews;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        initView(view);
        initClick(view);
        bindRecentTasks();

        return view;
    }

    private void initClick(View view) {
        Button addTaskButton = view.findViewById(R.id.galleryAddTaskButton);
        Button viewTasksButton = view.findViewById(R.id.galleryViewTasksButton);

        addTaskButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.add_Task));

        viewTasksButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.View_tasks));
    }

    private void initView(View view) {
        emptyRecentView = view.findViewById(R.id.galleryEmptyRecent);
        taskCards = new LinearLayout[] {
                view.findViewById(R.id.galleryTaskOne),
                view.findViewById(R.id.galleryTaskTwo),
                view.findViewById(R.id.galleryTaskThree)
        };
        taskTitleViews = new TextView[] {
                view.findViewById(R.id.galleryTaskOneTitle),
                view.findViewById(R.id.galleryTaskTwoTitle),
                view.findViewById(R.id.galleryTaskThreeTitle)
        };
        taskMetaViews = new TextView[] {
                view.findViewById(R.id.galleryTaskOneMeta),
                view.findViewById(R.id.galleryTaskTwoMeta),
                view.findViewById(R.id.galleryTaskThreeMeta)
        };
    }

    private void bindRecentTasks() {
        if (getContext() == null) {
            return;
        }

        List<student> tasks = new database(getContext(), "todoDatabase", null, 1).getData();
        int recentCount = Math.min(tasks.size(), taskCards.length);
        emptyRecentView.setVisibility(recentCount == 0 ? View.VISIBLE : View.GONE);

        for (int i = 0; i < taskCards.length; i++) {
            if (i < recentCount) {
                student task = tasks.get(tasks.size() - 1 - i);
                taskCards[i].setVisibility(View.VISIBLE);
                taskTitleViews[i].setText(getTaskTitle(task, i));
                taskMetaViews[i].setText(getTaskMeta(task));
            } else {
                taskCards[i].setVisibility(View.GONE);
            }
        }
    }

    private String getTaskTitle(student task, int position) {
        String title = safeValue(task.task);
        if (!title.isEmpty()) {
            return title;
        }
        return "Task item " + (position + 1);
    }

    private String getTaskMeta(student task) {
        String taskNumber = safeValue(task.taskno);
        String taskTime = safeValue(task.time);

        if (taskNumber.isEmpty() && taskTime.isEmpty()) {
            return "Open the task list to add details.";
        }
        if (taskNumber.isEmpty()) {
            return "Time: " + taskTime;
        }
        if (taskTime.isEmpty()) {
            return "Task " + taskNumber;
        }
        return "Task " + taskNumber + " | " + taskTime;
    }

    private String safeValue(String value) {
        return value == null ? "" : value.trim();
    }

    @Override
    public void onResume() {
        super.onResume();
        bindRecentTasks();
    }
}
