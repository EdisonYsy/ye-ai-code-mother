package com.ye.yeaicodemother.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.ye.yeaicodemother.model.dto.app.AppAddRequest;
import com.ye.yeaicodemother.model.dto.app.AppQueryRequest;
import com.ye.yeaicodemother.model.entity.App;
import com.ye.yeaicodemother.model.entity.User;
import com.ye.yeaicodemother.model.vo.AppVO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author <a href="https://github.com/EdsionYsy">叶盛源</a>
 */
public interface AppService extends IService<App> {
    /**
     * 创建应用APP
     * @param appAddRequest 新增app请求
     * @param loginUser 当前登录用户
     * @return appid
     */
    public Long createApp(AppAddRequest appAddRequest,User loginUser);

    /**
     * 通过对话生成应用代码
     * @param appId 应用ID
     * @param message 用户提示词
     * @param loginUser 当前用户
     * @return SSE响应内容
     */
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    public String deployApp(Long appId,User loginUser);

    /**
     * 根据app查询关联信息
     * @param app
     * @return
     */
    public AppVO getAppVO(App app);

    /**
     * 分页查询时根据多个app查询多个app的用户信息
     * @param appList app集合
     * @return
     */
    public List<AppVO> getAppVOList(List<App> appList);

    /**
     * 分页查询
     * @param appQueryRequest app详情查询请求
     * @return 分页查询器
     */
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    void generateAppScreenshotAsync(Long appId, String appUrl);
}
