package org.mendora.facade;

import io.vertx.core.http.HttpMethod;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestRouting {
    String value();
    HttpMethod method() default HttpMethod.GET;
}
