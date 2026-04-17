package com.ye.yeaicodemother.core;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ye.yeaicodemother.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeGeneratorFacadeTest {
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;
    @Test
    void generateAndSaveCode() {
        File result = aiCodeGeneratorFacade.generateAndSaveCode("生成一个个人博客 不超过20行", CodeGenTypeEnum.HTML,100L);
        Assertions.assertNotNull(result);
    }

    @Test
    void generateAndSaveCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream("生成一个个人博客,不超过60行", CodeGenTypeEnum.MULTI_FILE,100L);
        // 阻塞等待手机完成
        List<String> result = codeStream.collectList().block();
        //
        Assertions.assertNotNull(result);
        String completeResult = String.join("", result);
        Assertions.assertNotNull(completeResult);
    }

    @Test
    void generateVueProjectCodeStream(){
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(
                "简单的个人博客网站,代码总量不超过200行",CodeGenTypeEnum.VUE_PROJECT,1L
        );
        List<String> result = codeStream.collectList().block();
        Assertions.assertNotNull(result);
        String completeResult = String.join("", result);
        Assertions.assertNotNull(completeResult);
    }
}