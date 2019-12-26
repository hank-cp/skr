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

import demo.skr.MyErrorInfo;
import demo.skr.auth.model.Account;
import demo.skr.auth.model.User;
import demo.skr.auth.model.certification.SmsCertification;
import demo.skr.auth.repository.AccountRepository;
import demo.skr.auth.repository.UserRepository;
import demo.skr.auth.repository.certification.SmsCertificationRepository;
import lombok.NonNull;
import org.skr.auth.service.CertificationHandler;
import org.skr.common.exception.AuthException;
import org.skr.common.exception.BizException;
import org.skr.common.exception.ErrorInfo;
import org.skr.common.util.Checker;
import org.skr.security.Certification;
import org.skr.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Component
public class SmsCertificationHandler
        implements CertificationHandler<SmsCertification> {

    @Autowired
    private SmsCertificationRepository smsCertificationRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean supports(@NonNull Certification certification) {
        return certification instanceof SmsCertification;
    }

    @Override
    public UserPrincipal authenticate(@NonNull SmsCertification certification,
                                      Map<String, Object> arguments) throws AuthException {
        SmsCertification existedSmsCertification =
                smsCertificationRepository.findByMobilePhone(
                        certification.mobilePhone);

        // Username doesn't existed
        if (existedSmsCertification == null) {
            throw new AuthException(ErrorInfo.CERTIFICATION_NOT_FOUND
                    .msgArgs(certification.getIdentity()));
        }

        // password mismatch
        if (Checker.isEmpty(certification.captcha)) {
            throw new AuthException(MyErrorInfo.INVALID_SMS_CAPTCHA);
        }

        Account account = existedSmsCertification.account;

        // authentication pass, return principal
        if (arguments != null && arguments.containsKey("tenentCode")) {
            User user = userRepository.findOneByTenentCodeAndAccount(
                    arguments.get("tenentCode").toString(), account);
            if (user != null) return user;
        }
        return account;
    }

    @Override
    public SmsCertification findByIdentity(@NonNull String certificationIdentity) {
        return smsCertificationRepository.findByMobilePhone(certificationIdentity);
    }

    @Override
    public SmsCertification getCertification(@NonNull UserPrincipal principal) {
        Account account = null;
        if (principal instanceof Account) {
            account = (Account) principal;

        } else if (principal instanceof User) {
            account = ((User) principal).account;
        }
        assert account != null;

        return smsCertificationRepository.findByAccount(account);
    }

    @Override
    public UserPrincipal saveCertification(@NonNull UserPrincipal principal,
                                           @NonNull SmsCertification certification,
                                           Map<String, Object> arguments) {
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
                                                SmsCertification certification) {
        if (account == null) {
            account = new Account();
            accountRepository.save(account);
        }

        certification.account = account;
        smsCertificationRepository.save(certification);
        return account;
    }

    private UserPrincipal saveCertificationAndUser(@NonNull User user,
                                                   SmsCertification certification) {
        Account account = saveCertificationAndAccount(user.account, certification);

        if (user.tenent != null && Checker.isEmpty(user.tenent.code)) {
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

        SmsCertification smsCertification
                = smsCertificationRepository.findByMobilePhone(certificationIdentity);
        if (smsCertification == null
                || !smsCertification.account.equals(account)) {
            throw new BizException(ErrorInfo.ENTITY_NOT_FOUND
                    .msgArgs("SmsCertification", certificationIdentity));
        }
        smsCertificationRepository.delete(smsCertification);
    }
}