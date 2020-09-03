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
package demo.skr.aio.integration;

import demo.skr.aio.AioApp;
import demo.skr.reg.PermRegistryPack;
import demo.skr.reg.model.EndPoint;
import demo.skr.reg.model.Permission;
import demo.skr.registry.model.PersistedRealm;
import demo.skr.registry.repository.EndPointRepository;
import demo.skr.registry.repository.PermissionRepository;
import demo.skr.registry.repository.RealmRepository;
import demo.skr.registry.service.PermRegHost;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Callable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AioApp.class)
@AutoConfigureMockMvc
@Transactional
@Rollback
public class RegistryIntegrationTest {

    @Autowired
    private PermRegHost permRegHost;

    @Autowired
    private RealmRepository realmRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private EndPointRepository endPointRepository;

    private PersistedRealm testRealm;

    @Before
    public void setup() {
        testRealm = new PersistedRealm();
        testRealm.code = "test";
        realmRepository.save(testRealm);
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

    public static Throwable exceptionOf(Callable<?> callable) {
        try {
            callable.call();
            return null;
        } catch (Throwable t) {
            return t;
        }
    }

}