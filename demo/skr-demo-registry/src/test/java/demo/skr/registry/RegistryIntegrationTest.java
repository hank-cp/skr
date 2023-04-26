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
package demo.skr.registry;

import demo.skr.reg.PermRegistryPack;
import demo.skr.reg.model.EndPoint;
import demo.skr.reg.model.Permission;
import demo.skr.registry.model.PersistedEndPoint;
import demo.skr.registry.model.PersistedPermission;
import demo.skr.registry.model.PersistedRealm;
import demo.skr.registry.model.SiteEntry;
import demo.skr.registry.repository.EndPointRepository;
import demo.skr.registry.repository.PermissionRepository;
import demo.skr.registry.repository.RealmRepository;
import demo.skr.registry.service.PermRegHost;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skr.common.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RegistryApp.class)
@Rollback
@Transactional
public class RegistryIntegrationTest {

    @Autowired
    private PermRegHost permRegHost;

    @Autowired
    private RealmRepository realmRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private EndPointRepository endPointRepository;

    @Autowired
    private EntityManager entityManager;

    private PersistedRealm testRealm;

    @Before
    public void setup() {
        testRealm = new PersistedRealm();
        testRealm.code = "test";
        realmRepository.save(testRealm);
    }

    @Test
    public void testGeneratePermissionBits() {
        Long bit = permRegHost.generatePermissionBits();
        assertThat(bit, equalTo(1L));

        PersistedPermission permission = new PersistedPermission();
        permission.realm = testRealm;
        permission.code = "test";
        permission.name = "test";
        permission.bit = 1L << 32;
        permissionRepository.save(permission);
        entityManager.flush();

        // new bits generated on persistent
        bit = permRegHost.generatePermissionBits();
        assertThat(bit, equalTo(1L << 33));
    }

    @Test
    public void testRegisterPermission() {
        Permission permission = new Permission();
        permission.code = "test";
        permission.name = "test";

        permRegHost.registerPermission(testRealm, permission);
        assertThat(permission.bit, equalTo(1L));
        assertThat(permissionRepository.findById("test").orElse(null), notNullValue());

        // test save failed by define permission on another realm
        PersistedRealm testRealm2 = new PersistedRealm();
        testRealm2.code = "test2";
        realmRepository.save(testRealm2);

        PersistedPermission permission2 = new PersistedPermission();
        permission2.code = "test";
        permission2.name = "test";
        assertThat(exceptionOf(() -> permRegHost.registerPermission(testRealm2, permission2)),
                instanceOf(BizException.class));
    }

    @Test
    public void testRegisterEndPoint() {
        EndPoint endPoint = new EndPoint();
        endPoint.url = "/test/url";
        permRegHost.registerEndPoint(testRealm, endPoint);
        assertThat(endPointRepository.findById("/test/url").orElse(null), notNullValue());

        // test save failed by define endPoint on another realm
        PersistedRealm testRealm2 = new PersistedRealm();
        testRealm2.code = "test2";
        realmRepository.save(testRealm2);

        PersistedEndPoint endPoint2 = new PersistedEndPoint();
        endPoint2.url = "/test/url";
        assertThat(exceptionOf(() -> permRegHost.registerEndPoint(testRealm2, endPoint2)),
                instanceOf(BizException.class));
    }

    @Test
    public void testRegisterRealm() {
        PermRegistryPack regPack = new PermRegistryPack();
        regPack.permissions.add(Permission.of("test", "test"));
        regPack.endPoints.add(EndPoint.of("test", "/test/url", ""));

        permRegHost.register("test", null, regPack);
        assertThat(permissionRepository.findById("test").orElse(null), notNullValue());
        assertThat(endPointRepository.findById("/test/url").orElse(null), notNullValue());

        // test unregister Realm
        permRegHost.unregister("test");
        assertThat(permissionRepository.findById("test").get().disabled, equalTo(true));
        assertThat(endPointRepository.findById("/test/url").get().disabled, equalTo(true));
    }

    @Test
    public void testRegisterContinually() {
        PermRegistryPack regPack = new PermRegistryPack();
        regPack.permissions.add(Permission.of("test", "test"));
        regPack.endPoints.add(EndPoint.of("test", "/test/url", ""));

        permRegHost.register("test", null, regPack);
        assertThat(permissionRepository.countByRealmCode("test"), equalTo(1));
        assertThat(endPointRepository.countByRealmCode("test"), equalTo(1));
        assertThat(permissionRepository.findById("test").orElse(null), allOf(
                notNullValue(),
                hasProperty("disabled", equalTo(false))
        ));
        assertThat(endPointRepository.findById("/test/url").orElse(null), allOf(
                notNullValue(),
                hasProperty("disabled", equalTo(false))
        ));

        // register new permission and endPoint
        regPack.permissions.clear();
        regPack.endPoints.clear();
        regPack.permissions.add(Permission.of("test2", "test2"));
        regPack.endPoints.add(EndPoint.of("test2", "/test/url2", ""));

        permRegHost.register("test", "1", regPack);
        assertThat(permissionRepository.countByRealmCode("test"), equalTo(2));
        assertThat(endPointRepository.countByRealmCode("test"), equalTo(2));
        assertThat(permissionRepository.findById("test").orElse(null), allOf(
                notNullValue(),
                hasProperty("disabled", equalTo(true)) // old permission should be disabled
        ));
        assertThat(endPointRepository.findById("/test/url").orElse(null), allOf(
                notNullValue(),
                hasProperty("disabled", equalTo(true)) // old endPoint should be disabled
        ));
        assertThat(permissionRepository.findById("test2").orElse(null), allOf(
                notNullValue(),
                hasProperty("disabled", equalTo(false)) // new permission
        ));
        assertThat(endPointRepository.findById("/test/url2").orElse(null), allOf(
                notNullValue(),
                hasProperty("disabled", equalTo(false)) // old endPoint
        ));

        // register new permission and endPoint
        regPack.permissions.clear();
        regPack.endPoints.clear();
        regPack.permissions.add(Permission.of("test3", "test3"));
        regPack.endPoints.add(EndPoint.of("test3", "/test/url3", ""));

        // provides version, registration should be skipped because realm has no change
        permRegHost.register("test", "1", regPack);
        assertThat(permissionRepository.countByRealmCode("test"), equalTo(2));
        assertThat(endPointRepository.countByRealmCode("test"), equalTo(2));
    }

    @Test
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
                PersistedEndPoint.of(null, "/test/1", "1.a.aa"),
                PersistedEndPoint.of(null, "/test/2", "1.b.aa"),
                PersistedEndPoint.of(null, "/test/3", "1.c"),
                PersistedEndPoint.of(null, "/test/4", "1"),
                PersistedEndPoint.of(null, "/test/5", "1.a.bb.ccc"),
                PersistedEndPoint.of(null, "/test/6", "2.d.dd"),
                PersistedEndPoint.of(null, "/test/7", "2.e"),
                PersistedEndPoint.of(null, "/test/8", "3.a.bb")
        );

        List<SiteEntry> siteMap = SiteEntry.buildSiteMap(endPoints);
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

    public static Throwable exceptionOf(Runnable runnable) {
        try {
            runnable.run();
            return null;
        } catch (Throwable t) {
            return t;
        }
    }

}