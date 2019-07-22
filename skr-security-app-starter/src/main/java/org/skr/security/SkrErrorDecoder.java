package org.skr.security;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.skr.common.exception.UnvarnishedFeignException;

public class SkrErrorDecoder extends ErrorDecoder.Default {

    @Override
    public Exception decode(String methodKey, Response response) {
        return UnvarnishedFeignException.build(methodKey, response);
    }
}
