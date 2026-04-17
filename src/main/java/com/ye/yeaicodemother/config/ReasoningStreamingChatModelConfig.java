package com.ye.yeaicodemother.config;


import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "langchain4j.open-ai.chat-model")
public class ReasoningStreamingChatModelConfig {


    private String baseUrl;

    private String apiKey;

    /**
     * 用于Vue工程项目的生成 有推理能力
     * @return 流式响应大模型
     */
    @Bean
    public StreamingChatModel reasoningStreamingChatModel(){
        // 开发调试下 采用普通模型
        final String modelName = "deepseek-chat";
        final int maxTokens = 8192;
        // 生产环境下使用 推理模型
//        final String modelName = "deepseek-reasoner";
//        final int maxTokens = 32768;
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .maxTokens(maxTokens)
                .logRequests(true)
                .logResponses(true)
                .build();

    }





}
