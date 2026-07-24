package com.flamingo.qa.config;

/**
 * Cross-cutting run settings (sharding / parallelism), not tied to api or ui.
 * Shard assignment uses {@code class#methodName} so renaming {@code @DisplayName} does not reshuffle tests.
 */
public final class ExecutionConfig {

    private ExecutionConfig() {
    }

    public static int shardTotal() {
        return Integer.parseInt(ConfigProperties.get("shard.total", "1"));
    }

    public static int shardIndex() {
        return Integer.parseInt(ConfigProperties.get("shard.index", "0"));
    }

    public static int threads() {
        return Integer.parseInt(ConfigProperties.get("threads", "2"));
    }
}
