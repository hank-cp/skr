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
package demo.skr.auth.service;

import demo.skr.ErrorInfo;
import demo.skr.auth.model.Account;
import demo.skr.auth.model.User;
import demo.skr.auth.repository.AccountRepository;
import demo.skr.auth.repository.UserRepository;
import org.skr.auth.service.JwtPrincipalProvider;
import org.skr.common.Constants;
import org.skr.common.exception.AuthException;
import org.skr.security.JwtPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Service("authenticationService")
public class AuthenticationService implements UserDetailsService, JwtPrincipalProvider {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findOneByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException(username);
        }
        return account;
    }

    @Override
    public JwtPrincipal loadJwtPrincipal(String username, Map<String, Object> params) {
        String tenentCode = (String) params.get("tenentCode");
        User user = userRepository.findOneByTenentCodeAndAccount(tenentCode, username);

        if (user == null) throw new AuthException(ErrorInfo.ACCOUNT_NOT_BELONG_TO_ORG);
        if (user.status == Constants.DISABLED)
            throw new AuthException(ErrorInfo.USER_DISABLED);
        if (user.status == User.USER_STATUS_JOINING_NEED_APPROVAL)
            throw new AuthException(ErrorInfo.USER_NEED_APPROVAL);
        if (user.status == User.USER_STATUS_JOINING_REJECT)
            throw new AuthException(ErrorInfo.USER_REJECTED);

        return user.buildJwtPrincipal();
    }
}
