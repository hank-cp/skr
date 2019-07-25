package demo.skr.registry.repository;

import demo.skr.registry.model.EndPoint;
import demo.skr.registry.model.Realm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EndPointRepository extends JpaRepository<EndPoint, String> {

    List<EndPoint> findByRealm(Realm realm);

    @Query("SELECT e FROM EndPoint e WHERE e.enabled = true")
    List<EndPoint> findEnabledEndPoint(Realm realm);
}
