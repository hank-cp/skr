package demo.skr.b.taskrecord;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRecordRepository extends JpaRepository<TaskRecord, Long> {
}
