package demo.skr.auth.service;

import demo.skr.DemoErrors;
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

        if (user == null) throw new AuthException(DemoErrors.ACCOUNT_NOT_BELONG_TO_ORG);
        if (user.status == Constants.DISABLED)
            throw new AuthException(DemoErrors.USER_DISABLED);
        if (user.status == User.USER_STATUS_JOINING_NEED_APPROVAL)
            throw new AuthException(DemoErrors.USER_NEED_APPROVAL);
        if (user.status == User.USER_STATUS_JOINING_REJECT)
            throw new AuthException(DemoErrors.USER_REJECTED);

        return user.buildJwtPrincipal();
    }
}
