package com.flamingo.qa.helpers.sharding;

import com.flamingo.qa.config.ExecutionConfig;
import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.PostDiscoveryFilter;

/**
 * Drops tests that do not belong to this shard before execution.
 * Unlike {@link ShardExecutionCondition}, excluded tests never appear in Allure as skipped.
 */
public class ShardPostDiscoveryFilter implements PostDiscoveryFilter {

    @Override
    public FilterResult apply(TestDescriptor descriptor) {
        if (!descriptor.getType().isTest()) {
            return FilterResult.included("Container");
        }

        int total = ExecutionConfig.shardTotal();
        if (total <= 1) {
            return FilterResult.included("Sharding disabled");
        }

        return descriptor.getSource()
                .filter(MethodSource.class::isInstance)
                .map(MethodSource.class::cast)
                .map(this::filterMethod)
                .orElseGet(() -> FilterResult.included("No method source"));
    }

    private FilterResult filterMethod(MethodSource source) {
        int total = ExecutionConfig.shardTotal();
        int index = ExecutionConfig.shardIndex();
        String testId = source.getClassName() + "#" + source.getMethodName();

        if (ShardHelper.belongsToShard(testId, index, total)) {
            return FilterResult.included("Assigned to shard " + index + "/" + total);
        }
        return FilterResult.excluded("Not in shard " + index + "/" + total);
    }
}
