package com.example.taskmaster.Repository;

import android.content.Context;
import com.example.taskmaster.Model.TasksData;
import com.example.taskmaster.Utils.MyDbHelper;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private final MyDbHelper dbHelper;

    public TaskRepository(Context context) {
        dbHelper = new MyDbHelper(context);
    }

    public List<TasksData> getAllTasks() {
        return dbHelper.getData();
    }

    public ArrayList<TasksData> getPendingTasks() {
        return dbHelper.getPendingTasks();
    }

    public ArrayList<TasksData> getCompletedTasks() {
        return dbHelper.getAllCompletedTasks();
    }

    public ArrayList<TasksData> getOverdueTasks() {
        return dbHelper.getOverdueTasks();
    }

    public boolean insertTask(String taskNo, String taskTitle, String taskTime) {
        return dbHelper.insertData(taskNo, taskTitle, taskTime);
    }

    public boolean deleteTask(int id) {
        return dbHelper.deleteData(id);
    }

    public boolean updateTask(int id, String taskNo, String task, String taskTime, int isComplete) {
        return dbHelper.updateData(id, taskNo, task, taskTime, isComplete);
    }

    public boolean setComplete(int id, int isComplete) {
        return dbHelper.setComplete(id, isComplete);
    }
}
