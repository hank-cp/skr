
package org.skr.a.appsvr;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "demo-b")
public interface DemoBClient {

	class TaskRecord {
		public long taskId;
		public String operation;
	}

	@PostMapping("/task_record/{taskId}")
	void record(@PathVariable(name = "taskId") long taskId,
		   @RequestParam(name = "operation") String operation);

	@GetMapping("/task_record/list")
	List<Map<String, Object>> getTaskRecords();

	@GetMapping("/task_record/welcome_to_hell")
	void welcomeToHell();

}