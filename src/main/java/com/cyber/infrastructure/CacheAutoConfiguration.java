package com.cyber.infrastructure;

import com.cyber.infrastructure.config.CacheConfig;
import org.springframework.context.annotation.Import;

/**
 * cache 自动配置类
 */
@Import({ CacheConfig.class })
public class CacheAutoConfiguration {

}
