package com.ye.yeaicodemother.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.ye.yeaicodemother.model.dto.app.AppQueryRequest;
import com.ye.yeaicodemother.model.entity.App;
import com.ye.yeaicodemother.model.vo.AppVO;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author <a href="https://github.com/EdsionYsy">叶盛源</a>
 */
public interface AppService extends IService<App> {
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
}
