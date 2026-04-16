package com.ye.yeaicodemother.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * 多文件结果类
 */
@Data
@Description("生成多个文件的结果类")
public class MultiFileCodeResult {

    /**
     * html代码
     */
    @Description("html代码")
    private String htmlCode;

    /**
     * css代码
     */
    @Description("css代码")
    private String cssCode;

    /**
     * javascript代码
     */
    @Description("javascript代码")
    private String jsCode;

    /**
     * 描述
     */
    @Description("描述")
    private String description;
}
