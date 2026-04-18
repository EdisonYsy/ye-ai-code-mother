package com.ye.yeaicodemother.service;

public interface ScreenshotService {
    /**
     * 通用的截图服务 打开url进行主页截图并上传到COS对象存储 并返回访问url地址
     * @param webUrl 给定的网页地址
     * @return 对象存储url地址
     */
    String generateAndUploadScreenshot(String webUrl);
}
