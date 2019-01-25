package org.skr.a.task;

import org.skr.a.proxy.DemoBClient;
import org.skr.common.Errors;
import org.skr.common.util.Apis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public @ResponseBody Map<String, Object> getTasks() {
        return Apis.apiResult(Errors.OK, taskRepository.findAll());
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable long id) {
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