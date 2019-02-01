package org.skr.a.task;

import org.skr.a.feign.DemoBClient;
import org.skr.security.annotation.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private DemoBClient demoBClient;

    @PostMapping
    public void addTask(@RequestBody Task task) {
        taskRepository.save(task);
        demoBClient.record(task.id, "new");
    }

    @GetMapping("/list")
    @RequirePermission("Task_Management")
    public @ResponseBody List<Task> getTasks() {
        return taskRepository.findAll();
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable long id) {
        demoBClient.welcomeToHell();
        return taskRepository.findOne(id);
    }

    @PutMapping("/{id}")
    public void editTask(@PathVariable long id, @RequestBody Task task) {
        Task existingTask = taskRepository.findOne(id);
        Assert.notNull(existingTask, "Task not found");
        existingTask.description = task.description;
        taskRepository.save(existingTask);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable long id) {
        taskRepository.delete(id);
    }
}