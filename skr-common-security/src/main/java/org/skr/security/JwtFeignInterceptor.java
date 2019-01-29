package org.skr.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.skr.config.ApplicationContextProvider;

public class JwtFeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        SkrSecurityProperties skrSecurityProperties =
                ApplicationContextProvider.getBean(SkrSecurityProperties.class);

        JwtPrincipal jwtPrincipal = ApplicationContextProvider.getCurrentPrincipal();
        if (jwtPrincipal == null) return;
        template.header(skrSecurityProperties.getAccessToken().getHeader(),
                jwtPrincipal.getServiceJwtToken());
    }
}
