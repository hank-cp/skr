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
package demo.skr.auth.controller;

import demo.skr.SimpleJwtPrincipal;
import demo.skr.auth.model.Account;
import demo.skr.auth.model.User;
import demo.skr.auth.model.certification.LoginTokenCertification;
import demo.skr.auth.model.certification.RefreshTokenCertification;
import demo.skr.auth.model.certification.SmsCertification;
import demo.skr.auth.model.certification.UsernamePasswordCertification;
import org.skr.auth.service.AuthManager;
import org.skr.common.exception.ConfException;
import org.skr.common.exception.ErrorInfo;
import org.skr.common.util.Checker;
import org.skr.common.util.JsonUtil;
import org.skr.security.JwtPrincipal;
import org.skr.security.Token;
import org.skr.security.UserPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@RestController
@RequestMapping("/auth")
public class AuthController extends AuthManager {

    private Function<Token, String> userTokenizer = token -> {
        if (token.getPrincipal() instanceof SimpleJwtPrincipal) {
            SimpleJwtPrincipal principal = (SimpleJwtPrincipal) token.getPrincipal();
            return principal.getUsername() + "∆" + principal.getTenentCode();
        } else {
            return JsonUtil.toJson(token.getPrincipal());
        }
    };

    private JwtPrincipal buildJwtPrincipal(UserPrincipal principal) {
        if (principal instanceof Account) {
            Account account = (Account) principal;
            return SimpleJwtPrincipal.of(
                    account.uid.toString(), null, 0, account.status,
                    null, false, 0);
        } else if (principal instanceof User) {
            User user = (User) principal;
            return SimpleJwtPrincipal.of(
                    user.username, user.tenent.code, user.permissionBit, user.status,
                    null, false, user.tenent.vipLevel);
        }
        throw new ConfException(ErrorInfo.INCOMPATIBLE_TYPE.msgArgs(
                "Account/User", principal.getClass().getName()));
    }

    @PostMapping("/sign-up")
    public @ResponseBody Map<String, Object> signUp(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String tenentCode) {
        UsernamePasswordCertification certification =
                UsernamePasswordCertification.of(username, password);

        UserPrincipal newPrincipal = signUp(null, certification,
                Checker.isEmpty(tenentCode) ? null
                        : Map.of("tenentCode", tenentCode));
        JwtPrincipal jwtPrincipal = buildJwtPrincipal(newPrincipal);

        var accessToken = Token.of(skrSecurityProperties.getAccessToken(), jwtPrincipal);
        var refreshToken = Token.of(skrSecurityProperties.getRefreshToken(), jwtPrincipal);
        var loginToken = Token.of(skrSecurityProperties.getLoginToken(), jwtPrincipal);

        return Map.ofEntries(
                Map.entry(accessToken.getHeader(), accessToken.encode()),
                Map.entry(refreshToken.getHeader(), refreshToken.encode(userTokenizer)),
                Map.entry(loginToken.getHeader(), refreshToken.encode(userTokenizer)),
                Map.entry("principal", jwtPrincipal)
        );
    }

    @PostMapping("/sign-in")
    public @ResponseBody Map<String, Object> signInByUsernamePassword(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String tenentCode) {
        UsernamePasswordCertification certification =
                UsernamePasswordCertification.of(username, password);
        UserPrincipal principal = signIn(certification,
                Checker.isEmpty(tenentCode) ? null
                        : Map.of("tenentCode", tenentCode));
        JwtPrincipal jwtPrincipal = buildJwtPrincipal(principal);

        var accessToken = Token.of(skrSecurityProperties.getAccessToken(), jwtPrincipal);
        var refreshToken = Token.of(skrSecurityProperties.getRefreshToken(), jwtPrincipal);
        var loginToken = Token.of(skrSecurityProperties.getLoginToken(), jwtPrincipal);

        return Map.ofEntries(
                Map.entry(accessToken.getHeader(), accessToken.encode()),
                Map.entry(refreshToken.getHeader(), refreshToken.encode(userTokenizer)),
                Map.entry(loginToken.getHeader(), loginToken.encode(userTokenizer)),
                Map.entry("principal", jwtPrincipal)
        );
    }

    @PostMapping("/sign-in-by-sms")
    public @ResponseBody Map<String, Object> signInBySms(
            @RequestParam String mobile,
            @RequestParam String captcha,
            @RequestParam(required = false) String tenentCode) {
        SmsCertification certification =
                SmsCertification.of(mobile, captcha);
        UserPrincipal principal = signIn(certification,
                Checker.isEmpty(tenentCode) ? null
                        : Map.of("tenentCode", tenentCode));
        JwtPrincipal jwtPrincipal = buildJwtPrincipal(principal);

        var accessToken = Token.of(skrSecurityProperties.getAccessToken(), jwtPrincipal);
        var refreshToken = Token.of(skrSecurityProperties.getRefreshToken(), jwtPrincipal);
        var loginToken = Token.of(skrSecurityProperties.getLoginToken(), jwtPrincipal);

        return Map.ofEntries(
                Map.entry(accessToken.getHeader(), accessToken.encode()),
                Map.entry(refreshToken.getHeader(), refreshToken.encode(userTokenizer)),
                Map.entry(loginToken.getHeader(), loginToken.encode(userTokenizer)),
                Map.entry("principal", jwtPrincipal)
        );
    }

    @PostMapping("/sign-in-by-token")
    public @ResponseBody Map<String, Object> signInByLoginToken(
            @RequestParam String loginToken) {
        LoginTokenCertification certification =
                LoginTokenCertification.of(loginToken);
        UserPrincipal principal = signIn(certification, null);
        JwtPrincipal jwtPrincipal = buildJwtPrincipal(principal);

        var accessToken = Token.of(skrSecurityProperties.getAccessToken(), jwtPrincipal);
        var refreshToken = Token.of(skrSecurityProperties.getRefreshToken(), jwtPrincipal);

        return Map.ofEntries(
                Map.entry(accessToken.getHeader(), accessToken.encode()),
                Map.entry(refreshToken.getHeader(), refreshToken.encode(userTokenizer)),
                Map.entry("principal", jwtPrincipal)
        );
    }

    @PostMapping("/refresh-token")
    public @ResponseBody Map<String, Object> refreshToken(
            @RequestParam String refreshToken,
            @RequestParam(required = false) String tenentCode) {
        RefreshTokenCertification certification =
                RefreshTokenCertification.of(refreshToken);
        UserPrincipal principal = signIn(certification,
                Checker.isEmpty(tenentCode) ? null
                        : Map.of("tenentCode", tenentCode));
        JwtPrincipal jwtPrincipal = buildJwtPrincipal(principal);

        Map<String, Object> response = new HashMap<>();
        var accessToken = Token.of(skrSecurityProperties.getAccessToken(), jwtPrincipal);
        response.put(accessToken.getHeader(), accessToken.encode());
        if (skrSecurityProperties.isRenewRefreshToken()) {
            var newRefreshToken = Token.of(skrSecurityProperties.getRefreshToken(), jwtPrincipal);
            response.put(newRefreshToken.getHeader(), newRefreshToken.encode(userTokenizer));
        }
        response.put("principal", jwtPrincipal);
        return response;
    }

    @PostMapping("/bind-mobile")
    public @ResponseBody Map<String, Object> bindMobile(
            @RequestParam String loginToken,
            @RequestParam String mobile,
            @RequestParam String captcha) {
        SmsCertification certification =
                SmsCertification.of(mobile, captcha);
        bindCertification(LoginTokenCertification.of(loginToken),
                certification, null);
        return Map.of();
    }

    @PostMapping("/unbind-mobile")
    public @ResponseBody Map<String, Object> unbindMobile(
            @RequestParam String loginToken,
            @RequestParam String mobile) {
        SmsCertification certification =
                SmsCertification.of(mobile, "");
        unbindCertification(LoginTokenCertification.of(loginToken),
                certification, null);
        return Map.of();
    }

    @PostMapping("/unbind-username")
    public @ResponseBody Map<String, Object> unbindUsername(
            @RequestParam String loginToken,
            @RequestParam String username) {
        UsernamePasswordCertification certification =
                UsernamePasswordCertification.of(username, "");
        unbindCertification(LoginTokenCertification.of(loginToken),
                certification, null);
        return Map.of();
    }
}
