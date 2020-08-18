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

import org.springframework.context.ApplicationEvent;
import org.springframework.web.context.request.WebRequest;

/**
 * This event will be published when exception occurred.
 *
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public class ErrorOccurredEvent extends ApplicationEvent {

    private static final long serialVersionUID = -8780401612862384173L;

    private final WebRequest request;

    public ErrorOccurredEvent(Throwable throwable, WebRequest request) {
        super(throwable);
        this.request = request;
    }

    public WebRequest getRequest() {
        return request;
    }
}
