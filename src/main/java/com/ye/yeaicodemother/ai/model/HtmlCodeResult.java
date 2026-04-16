package com.ye.yeaicodemother.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * 原生html结果类
 */
@Data
@Description("生成单个html文件的返回结果")
public class HtmlCodeResult {

    /**
     * HTML代码
     */
    @Description("返回的html代码")
    private String htmlCode;

    /**
     * 描述
     */
    @Description("描述")
    private String description;
}
