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

import demo.skr.auth.model.Account;
import demo.skr.auth.model.User;
import demo.skr.auth.model.certification.UsernamePasswordCertification;
import demo.skr.auth.repository.AccountRepository;
import demo.skr.auth.repository.TenentRepository;
import demo.skr.auth.repository.UserRepository;
import demo.skr.auth.repository.certification.UsernamePasswordCertificationRepository;
import lombok.NonNull;
import org.skr.auth.service.CertificationHandler;
import org.skr.common.exception.AuthException;
import org.skr.common.exception.ErrorInfo;
import org.skr.common.util.Checker;
import org.skr.security.Certification;
import org.skr.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Component
public class UsernamePasswordCertificationHandler
        implements CertificationHandler<UsernamePasswordCertification> {

    @Autowired
    private UsernamePasswordCertificationRepository usernamePasswordCertificationRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenentRepository tenentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Certification certification) {
        return certification instanceof UsernamePasswordCertification;
    }

    @Override
    public UserPrincipal authenticate(@NonNull UsernamePasswordCertification certification,
                                      Map<String, Object> arguments) throws AuthException {
        UsernamePasswordCertification existedUsernamePasswordCertification =
                usernamePasswordCertificationRepository.findByUsername(
                        certification.username);

        // Username doesn't existed
        if (existedUsernamePasswordCertification == null) {
            throw new AuthException(ErrorInfo.CERTIFICATION_NOT_FOUND
                    .msgArgs(certification.getIdentity()));
        }

        // password mismatch
        if (!passwordEncoder.matches(certification.password,
                existedUsernamePasswordCertification.password)) {
            throw new AuthException(ErrorInfo.BAD_CERTIFICATION
                    .msgArgs(certification.getIdentity()));
        }

        Account account = existedUsernamePasswordCertification.account;

        // authentication pass, return principal
        if (arguments != null && arguments.containsKey("tenentCode")) {
            User user = userRepository.findOneByTenentCodeAndAccount(
                    arguments.get("tenentCode").toString(), account);
            if (user != null) return user;
        }
        return account;
    }

    @Override
    public UsernamePasswordCertification findByIdentity(String certificationIdentity) {
        return usernamePasswordCertificationRepository.findByUsername(certificationIdentity);
    }

    @Override
    public UsernamePasswordCertification getCertification(@NonNull UserPrincipal principal) {
        Account account = null;
        if (principal instanceof Account) {
            account = (Account) principal;

        } else if (principal instanceof User) {
            account = ((User) principal).account;
        }
        assert account != null;

        return usernamePasswordCertificationRepository.findByAccount(account);
    }

    @Override
    public UserPrincipal saveCertification(UserPrincipal principal,
                                           @NonNull UsernamePasswordCertification certification,
                                           Map<String, Object> arguments) {
        if (principal == null) {
            Account account = saveCertificationAndAccount(null,
                    certification);
            if (arguments != null && arguments.containsKey("tenentCode")) {
                User user = new User();
                user.account = account;
                user.username = certification.username;
                user.tenent = tenentRepository.findById(arguments.get("tenentCode").toString()).get();
                return saveCertificationAndUser(user,
                        certification);
            } else {
                return account;
            }
        }

        if (principal instanceof Account) {
            return saveCertificationAndAccount(
                    (Account) principal,
                    certification);

        } else if (principal instanceof User) {
            return saveCertificationAndUser(
                    (User) principal,
                    certification);
        }
        return principal;
    }

    private Account saveCertificationAndAccount(Account account,
                                                UsernamePasswordCertification certification) {
        if (account == null) {
            account = new Account();
            accountRepository.save(account);
        }

        certification.account = account;
        certification.password = passwordEncoder.encode(certification.password);
        usernamePasswordCertificationRepository.save(certification);
        return account;
    }

    private UserPrincipal saveCertificationAndUser(@NonNull User user,
                                          UsernamePasswordCertification certification) {
        Account account = saveCertificationAndAccount(user.account, certification);

        if (user.tenent != null && !Checker.isEmpty(user.tenent.code)) {
            user.account = account;
            userRepository.save(user);
            return user;
        }

        return account;
    }

    @Override
    public void removeCertification(@NonNull UserPrincipal principal,
                                    @NonNull String certificationIdentity) {
        Account account = null;
        if (principal instanceof Account) {
            account = (Account) principal;

        } else if (principal instanceof User) {
            account = ((User) principal).account;
        }
        assert account != null;

        UsernamePasswordCertification usernamePasswordCertification
                = usernamePasswordCertificationRepository.findByUsername(certificationIdentity);
        if (usernamePasswordCertification == null
                || !usernamePasswordCertification.account.equals(account)) {
            throw new AuthException(ErrorInfo.MISSING_PROPERTY.msgArgs(certificationIdentity));
        }
        usernamePasswordCertificationRepository.delete(usernamePasswordCertification);
    }
}