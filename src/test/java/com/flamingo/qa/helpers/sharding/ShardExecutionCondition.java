package com.flamingo.qa.helpers.sharding;

import com.flamingo.qa.config.ExecutionConfig;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Suite-wide sharding: only tests whose hash belongs to this shard index are executed.
 */
public class ShardExecutionCondition implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        // Class-level evaluation has no test method yet — always allow container, filter per method.
        if (context.getTestMethod().isEmpty()) {
            return ConditionEvaluationResult.enabled("Class container — shard filter applied per method");
        }

        int total = ExecutionConfig.shardTotal();
        int index = ExecutionConfig.shardIndex();

        if (total <= 1) {
            return ConditionEvaluationResult.enabled("Sharding disabled (shard.total <= 1)");
        }

        // Stable id: renaming @DisplayName must not reshuffle shards.
        String testId = context.getRequiredTestClass().getName() + "#"
                + context.getRequiredTestMethod().getName();

        if (ShardHelper.belongsToShard(testId, index, total)) {
            return ConditionEvaluationResult.enabled(
                    "Test assigned to shard " + index + "/" + total);
        }
        return ConditionEvaluationResult.disabled(
                "Skipped by shard filter (index=" + index + ", total=" + total + ")");
    }
}
