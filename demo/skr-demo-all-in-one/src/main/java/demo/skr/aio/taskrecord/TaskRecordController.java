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
package demo.skr.aio.taskrecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@RestController
@RequestMapping("/task_record")
public class TaskRecordController {

    @Autowired
    private TaskRecordRepository taskRecordRepository;

    @PostMapping("/{taskId}")
    public void record(@PathVariable long taskId,
                       @RequestParam String operation) {
        TaskRecord taskRecord = new TaskRecord();
        taskRecord.taskId = taskId;
        taskRecord.operation = operation;
        taskRecordRepository.save(taskRecord);
    }

    @GetMapping("/list")
    public List<TaskRecord> getTaskRecords() {
        return taskRecordRepository.findAll();
    }

    @GetMapping("/welcome_to_hell")
    public void welcomeToHell() {
        throw new RuntimeException("This is a test exception");
    }

}