/*
 * Copyright (C) 2019-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package demo.skr.a;

import demo.skr.a.model.Task;
import demo.skr.a.model.TaskExtension;
import org.skr.security.annotation.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskRegHost taskRegHost;

    @PostMapping
    public void addTask(@RequestBody Task task) {
        taskRepository.save(task);
    }

    @GetMapping("/list")
    @RequirePermission("Task")
    public @ResponseBody List<Task> getTasks() {
        return taskRepository.findAll();
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable long id) {
        return taskRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public void editTask(@PathVariable long id, @RequestBody Task task) {
        Task existingTask = taskRepository.findById(id).orElse(null);
        Assert.notNull(existingTask, "Task not found");
        existingTask.description = task.description;
        taskRepository.save(existingTask);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable long id) {
        taskRepository.deleteById(id);
    }

    @GetMapping("/extensions")
    public List<TaskExtension> getExtensions() {
        return taskRegHost.getExtensions();
    }
}