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
package org.skr.config;

import org.skr.SkrProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Component
public class ErrorMessageSource extends ResourceBundleMessageSource implements InitializingBean {

    @Autowired(required = false)
    private MessageSourceProperties messageSourceProperties;

    @Autowired
    private SkrProperties skrProperties;

    @Override
    public void afterPropertiesSet() {
        if (messageSourceProperties == null) {
            messageSourceProperties = new MessageSourceProperties();
        }

        if (StringUtils.hasText(skrProperties.getErrorMsgPropBasename())) {
            setBasenames(StringUtils
                    .commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(skrProperties.getErrorMsgPropBasename())));
        }
        if (messageSourceProperties.getEncoding() != null) {
            setDefaultEncoding(messageSourceProperties.getEncoding().name());
        }
        setFallbackToSystemLocale(messageSourceProperties.isFallbackToSystemLocale());
        Duration cacheDuration = messageSourceProperties.getCacheDuration();
        if (cacheDuration != null) {
            setCacheMillis(cacheDuration.toMillis());
        }
        setAlwaysUseMessageFormat(messageSourceProperties.isAlwaysUseMessageFormat());
        setUseCodeAsDefaultMessage(messageSourceProperties.isUseCodeAsDefaultMessage());
    }
}