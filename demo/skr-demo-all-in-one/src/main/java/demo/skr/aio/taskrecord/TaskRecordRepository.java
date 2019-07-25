package demo.skr.aio.taskrecord;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRecordRepository extends JpaRepository<TaskRecord, Long> {
}
