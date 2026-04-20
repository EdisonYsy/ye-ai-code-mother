package com.ye.yeaicodemother;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.ye.yeaicodemother.mapper")
@EnableCaching
public class YeAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(YeAiCodeMotherApplication.class, args);
    }

}
