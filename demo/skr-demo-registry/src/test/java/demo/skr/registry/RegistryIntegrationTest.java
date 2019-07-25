package demo.skr.registry;

import demo.skr.registry.model.EndPoint;
import demo.skr.registry.model.Permission;
import demo.skr.registry.model.Realm;
import demo.skr.registry.repository.PermissionRepository;
import demo.skr.registry.repository.RealmRepository;
import demo.skr.registry.service.RegistryServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skr.common.exception.BizException;
import org.skr.common.util.tuple.Tuple3;
import org.skr.registry.SiteEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RegistryApp.class)
@Rollback
public class RegistryIntegrationTest {

    @Autowired
    private RegistryServiceImpl registryService;

    @Autowired
    private RealmRepository realmRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private EntityManager entityManager;

    private Realm testRealm;

    @Before
    public void setup() {
        testRealm = new Realm();
        testRealm.code = "test";
        testRealm.name = "test";
        realmRepository.save(testRealm);
    }

    @Test
    @Transactional
    public void testGeneratePermissionBits() {
        Tuple3<Long, Long, Long> bits = registryService.generatePermissionBits();
        assertThat(bits._0, equalTo(1L));
        assertThat(bits._1, equalTo(1L));
        assertThat(bits._2, equalTo(1L));

        Permission permission = new Permission();
        permission.realm = testRealm;
        permission.code = "test";
        permission.name = "test";
        permission.bit1 = 1L << 32;
        permission.bit2 = 1L;
        permission.bit3 = 1L;
        permissionRepository.save(permission);
        entityManager.flush();

        // new bits generated on persistent
        bits = registryService.generatePermissionBits();
        assertThat(bits._0, equalTo(1L << 33));

        permission = permissionRepository.findById("test").get();
        permission.bit1 = Long.MAX_VALUE;
        permissionRepository.save(permission);
        entityManager.flush();
        bits = registryService.generatePermissionBits();
        assertThat(bits._0, equalTo(1L));
        assertThat(bits._1, equalTo(1L << 1));
        assertThat(bits._2, equalTo(1L));

        permission = permissionRepository.findById("test").get();
        permission.bit1 = Long.MAX_VALUE;
        permission.bit2 = Long.MAX_VALUE;
        permissionRepository.save(permission);
        entityManager.flush();
        bits = registryService.generatePermissionBits();
        assertThat(bits._0, equalTo(1L));
        assertThat(bits._1, equalTo(1L));
        assertThat(bits._2, equalTo(1L << 1));
    }

    @Test
    @Transactional
    public void testRegisterPermission() {
        Permission permission = new Permission();
        permission.realm = testRealm;
        permission.code = "test";
        permission.name = "test";
        registryService.registerPermission(testRealm, permission);
        assertThat(permission.bit1, equalTo(1L));
        assertThat(permission.bit2, equalTo(1L));
        assertThat(permission.bit3, equalTo(1L));
        assertThat(registryService.listPermissions(testRealm), hasSize(1));

        // test save failed by define permission on another realm
        Realm testRealm2 = new Realm();
        testRealm2.code = "test2";
        testRealm2.name = "test2";
        realmRepository.save(testRealm2);

        Permission permission2 = new Permission();
        permission2.code = "test";
        permission2.name = "test";
        assertThat(exceptionOf(() -> registryService.registerPermission(testRealm2, permission2)),
                instanceOf(BizException.class));
    }

    @Test
    @Transactional
    public void testRegisterEndPoint() {
        EndPoint endPoint = new EndPoint();
        endPoint.realm = testRealm;
        endPoint.url = "/test/url";
        registryService.registerEndPoint(testRealm, endPoint);
        assertThat(registryService.getEndPoint("/test/url"), notNullValue());

        // test save failed by define endPoint on another realm
        Realm testRealm2 = new Realm();
        testRealm2.code = "test2";
        testRealm2.name = "test2";
        realmRepository.save(testRealm2);

        EndPoint endPoint2 = new EndPoint();
        endPoint2.url = "/test/url";
        assertThat(exceptionOf(() -> registryService.registerEndPoint(testRealm2, endPoint2)),
                instanceOf(BizException.class));
    }

    @Test
    @Transactional
    public void testBuildSiteMap() {
        // 1
        //   -- a
        //     -- aa
        //     -- bb
        //       -- ccc
        //   -- b
        //     -- aa
        //   -- c
        // 2
        //   -- d
        //     -- dd
        //   -- e
        // 3
        //   -- a
        //     -- bb
        List<EndPoint> endPoints = Arrays.asList(
                EndPoint.of(null, "/test/1", "1.a.aa"),
                EndPoint.of(null, "/test/2", "1.b.aa"),
                EndPoint.of(null, "/test/3", "1.c"),
                EndPoint.of(null, "/test/4", "1"),
                EndPoint.of(null, "/test/5", "1.a.bb.ccc"),
                EndPoint.of(null, "/test/6", "2.d.dd"),
                EndPoint.of(null, "/test/7", "2.e"),
                EndPoint.of(null, "/test/8", "3.a.bb")
        );

        List<SiteEntry> siteMap = registryService.buildSiteMap(endPoints);
        assertThat(siteMap, hasSize(3));
        assertThat(siteMap.get(0).breadcrumb, equalTo("1"));
        assertThat(siteMap.get(0).siteEntries, hasSize(3));
        assertThat(siteMap.get(0).siteEntries.get(0).breadcrumb, equalTo("a"));
        assertThat(siteMap.get(0).siteEntries.get(0).siteEntries.get(0).breadcrumb, equalTo("aa"));
        assertThat(siteMap.get(0).siteEntries.get(0).siteEntries.get(1).breadcrumb, equalTo("bb"));
        assertThat(siteMap.get(0).siteEntries.get(0).siteEntries.get(1).siteEntries.get(0).breadcrumb, equalTo("ccc"));
        assertThat(siteMap.get(1).breadcrumb, equalTo("2"));
        assertThat(siteMap.get(1).siteEntries, hasSize(2));
        assertThat(siteMap.get(2).breadcrumb, equalTo("3"));
        assertThat(siteMap.get(2).siteEntries, hasSize(1));
    }

    public static Throwable exceptionOf(Callable<?> callable) {
        try {
            callable.call();
            return null;
        } catch (Throwable t) {
            return t;
        }
    }

}