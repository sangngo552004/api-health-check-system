package com.example.apihealthchecksystem.delivery.rest.common.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * Annotation to extract the ID from the current authenticated user. This decouples the Controller
 * from the specific UserDetails implementation in Infrastructure.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression = "id")
public @interface CurrentUserId {}
