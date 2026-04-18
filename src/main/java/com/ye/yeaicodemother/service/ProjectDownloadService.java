package com.ye.yeaicodemother.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ProjectDownloadService {

    /**
     * 通用的下载Vue项目压缩包方法
     * @param projectPath      项目根路径
     * @param downloadFilename 下载文件名
     * @param reseponse 响应
     * @return
     */
    void downloadProjectAsZip(String projectPath, String downloadFilename, HttpServletResponse reseponse);
}
