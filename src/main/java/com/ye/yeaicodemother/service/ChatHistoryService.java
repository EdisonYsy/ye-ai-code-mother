package com.ye.yeaicodemother.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.ye.yeaicodemother.model.dto.chathistory.ChatHistoryQueryRequest;
import com.ye.yeaicodemother.model.entity.ChatHistory;
import com.ye.yeaicodemother.model.entity.User;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author <a href="https://github.com/EdsionYsy">叶盛源</a>
 */
public interface ChatHistoryService extends IService<ChatHistory> {
    /**
     * 某应用的历史对话加载
     * @param appId appId
     * @param chatMemory 对话记忆存储
     * @param maxCount 加载最大条数
     * @return 加载成功条数
     */
    int loadChatHistoryToMemory(long appId, MessageWindowChatMemory chatMemory, int maxCount);

    /**
     * 添加对话历史记录
     * @param appId 应用id
     * @param message 对话消息
     * @param messageType 消息类型 - ai ｜ 用户
     * @param userId 用户id
     * @return 是否成功
     */
    boolean addChatMessage(Long appId, String message, String messageType, Long userId);

    /**
     * 关联删除 删除应用时删除对话历史
     * @param appId appid
     * @return 是否成功
     */
    boolean deleteByAppId(Long appId);

    /**
     * 构造查询请求
     * @param chatHistoryQueryRequest 对话历史查询请求
     * @return QueryWrapper
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

    /**
     * 展示对话历史消息 - 游标查询
     * @param appId 应用id
     * @param pageSize 分页大小
     * @param lastCreateTime 游标 最早一条消息的创建时间
     * @param loginUser 登录用户
     * @return
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                               LocalDateTime lastCreateTime,
                                               User loginUser);
}
