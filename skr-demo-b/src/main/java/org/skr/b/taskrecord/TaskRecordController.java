package org.skr.b.taskrecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}