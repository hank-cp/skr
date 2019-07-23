package org.skr.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.skr.config.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;

public class JwtFeignInterceptor implements RequestInterceptor {

    @Autowired
    private SkrSecurityProperties skrSecurityProperties;

    @Override
    public void apply(RequestTemplate template) {
        JwtPrincipal jwtPrincipal = ApplicationContextProvider.getCurrentPrincipal();
        if (jwtPrincipal == null) return;
        template.header(skrSecurityProperties.getAccessToken().getHeader(),
                jwtPrincipal.getApiTrainJwtToken());
    }
}
