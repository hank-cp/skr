package org.skr.config;

import org.skr.security.JwtPrincipal;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;
import java.util.Optional;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext ctx = null;

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (ctx == null) return null;
        return ctx.getBean(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        if (ctx == null) return null;
        return (T) ctx.getBean(beanName);
    }

    public static String getMessage(String msgKey, Object...params) {
        try {
            return Optional.ofNullable(getBean(MessageSource.class)).map(bean -> bean.getMessage(
                    msgKey, params, LocaleContextHolder.getLocale())).orElse(msgKey);
        } catch (NoSuchMessageException ignored) {
            return msgKey;
        }
    }

    public static JwtPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtPrincipal user = null;
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof JwtPrincipal) {
            user = (JwtPrincipal) authentication.getPrincipal();
        }
        return user;
    }

    public static Validator getJSR303Validator() {
        if (ctx == null) return null;
        LocalValidatorFactoryBean factoryBean = ctx.getBean(LocalValidatorFactoryBean.class);
        return factoryBean.getValidator();
    }

    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        // Assign the ApplicationContext into a static method
        ApplicationContextProvider.ctx = ctx;
    }
}
