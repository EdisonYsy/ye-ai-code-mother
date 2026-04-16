package com.ye.yeaicodemother.core.parser;

public interface CodeParser<T> {


    /**
     * 根据大模型响应返回不同的结构化对象
     * @param codeContent 大模型响应
     * @return 结构化对象 HtmlCodeResult | MultiFileCodeResult
     */
    T parseCode(String codeContent);
}
