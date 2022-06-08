package com.ice.framework.component.netty.config;

/**
 * @author : tjq
 * @since : 2022-05-12
 */

public interface NettyConfig {

    /**
     * max packet is 2M.
     */
    int MAX_FRAME_LENGTH = 2 * 1024 * 1024;

    int LENGTH_FIELD_OFFSET = 0;

    int LENGTH_FIELD_LENGTH = 4;

    int INITIAL_BYTES_TO_STRIP = 0;

    int LENGTH_ADJUSTMENT = 0;

    int READ_IDLE_TIME_SECONDS = 60;

    int WRITE_IDLE_TIME_SECONDS = 40;

    int ALL_IDLE_TIME_SECONDS = 0;

}
