package com.ye.yeaicodemother.ai;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ye.yeaicodemother.service.ChatHistoryService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Ai服务创建工厂
 */
@Configuration
@Slf4j
public class AiCodeGeneratorServiceFactory {

    @Resource
    private ChatModel chatModel;

    @Resource
    private StreamingChatModel streamingChatModel;
    /**
     * 创建Ai服务对象方法
     * @return AI服务对象
     */

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Resource
    private ChatHistoryService chatHistoryService;
    /**
     * AI服务实例缓存
     * 缓存策略:
     * 最大1000个key 写入后30分钟过期 访问10分钟后过期
     */
    private final Cache<Long,AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI服务实例被移除,appId:{},原因:{}",key,cause);
            })
            .build();


    /**
     * 根据app id获取Aiservice服务
     * @param appId appid
     * @return
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId){
        // app:1 Aiservice1 ...
        return serviceCache.get(appId, // 生成AiSerice
            this::createAiCodeGeneratorService
        );
    }

    private AiCodeGeneratorService createAiCodeGeneratorService(long appId){
        log.info("为appId:{}创建新的AI服务实例",appId);
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();
        // 历史对话加载到记忆存储中
        chatHistoryService.loadChatHistoryToMemory(appId,chatMemory,20);
        return AiServices.builder(AiCodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .chatMemory(chatMemory)
                .build();
    }

//    @Bean
//    public AiCodeGeneratorService aiCodeGeneratorService(){
//        return getAiCodeGeneratorService(0);
//    }
}
