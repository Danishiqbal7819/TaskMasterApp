package com.example.taskmaster.ViewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.taskmaster.Model.TasksData;
import com.example.taskmaster.Repository.TaskRepository;
import java.util.ArrayList;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private final TaskRepository repository;
    private final MutableLiveData<List<TasksData>> allTasks = new MutableLiveData<>();
    private final MutableLiveData<List<TasksData>> pendingTasks = new MutableLiveData<>();
    private final MutableLiveData<List<TasksData>> completedTasks = new MutableLiveData<>();
    private final MutableLiveData<List<TasksData>> overdueTasks = new MutableLiveData<>();

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        refreshTasks();
    }

    public LiveData<List<TasksData>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<TasksData>> getPendingTasks() {
        return pendingTasks;
    }

    public LiveData<List<TasksData>> getCompletedTasks() {
        return completedTasks;
    }

    public LiveData<List<TasksData>> getOverdueTasks() {
        return overdueTasks;
    }

    public void refreshTasks() {
        allTasks.setValue(repository.getAllTasks());
        pendingTasks.setValue(repository.getPendingTasks());
        completedTasks.setValue(repository.getCompletedTasks());
        overdueTasks.setValue(repository.getOverdueTasks());
    }

    public boolean insertTask(String taskNo, String taskTitle, String taskTime) {
        boolean result = repository.insertTask(taskNo, taskTitle, taskTime);
        if (result) refreshTasks();
        return result;
    }

    public boolean deleteTask(int id) {
        boolean result = repository.deleteTask(id);
        if (result) refreshTasks();
        return result;
    }

    public boolean updateTask(int id, String taskNo, String task, String taskTime, int isComplete) {
        boolean result = repository.updateTask(id, taskNo, task, taskTime, isComplete);
        if (result) refreshTasks();
        return result;
    }

    public boolean setComplete(int id, int isComplete) {
        boolean result = repository.setComplete(id, isComplete);
        if (result) refreshTasks();
        return result;
    }
}
