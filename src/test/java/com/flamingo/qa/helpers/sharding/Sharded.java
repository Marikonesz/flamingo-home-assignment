package com.flamingo.qa.helpers.sharding;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Opt-in marker for suite-wide sharding via {@link ShardExecutionCondition}.
 * Excluded tests show as disabled in JUnit/Allure; use {@link ShardPostDiscoveryFilter}
 * at launcher level when they should be omitted from reports entirely.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(ShardExecutionCondition.class)
public @interface Sharded {
}
