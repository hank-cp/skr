package demo.skr.registry.repository;

import demo.skr.registry.model.EndPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EndPointRepository extends JpaRepository<EndPoint, String> {

}
