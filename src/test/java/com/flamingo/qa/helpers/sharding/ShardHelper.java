package com.flamingo.qa.helpers.sharding;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

/**
 * Deterministic suite-wide sharding: hash(testId) % total == index.
 */
public final class ShardHelper {

    private ShardHelper() {
    }

    public static boolean belongsToShard(String testId, int shardIndex, int shardTotal) {
        if (shardTotal <= 1) {
            return true;
        }
        if (shardIndex < 0 || shardIndex >= shardTotal) {
            throw new IllegalArgumentException(
                    "shard.index must be in [0, shard.total), got index=" + shardIndex + ", total=" + shardTotal);
        }
        return Math.floorMod(stableHash(testId), shardTotal) == shardIndex;
    }

    /** CRC32 over UTF-8 bytes — stable across JVMs for deterministic shard assignment. */
    public static int stableHash(String value) {
        CRC32 crc = new CRC32();
        crc.update(value.getBytes(StandardCharsets.UTF_8));
        return (int) crc.getValue();
    }
}
