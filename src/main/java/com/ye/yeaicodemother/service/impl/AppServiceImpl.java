package com.ye.yeaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ye.yeaicodemother.constant.AppConstant;
import com.ye.yeaicodemother.core.AiCodeGeneratorFacade;
import com.ye.yeaicodemother.exception.BusinessException;
import com.ye.yeaicodemother.exception.ErrorCode;
import com.ye.yeaicodemother.exception.ThrowUtils;
import com.ye.yeaicodemother.model.dto.app.AppQueryRequest;
import com.ye.yeaicodemother.model.entity.App;
import com.ye.yeaicodemother.mapper.AppMapper;
import com.ye.yeaicodemother.model.entity.User;
import com.ye.yeaicodemother.model.enums.CodeGenTypeEnum;
import com.ye.yeaicodemother.model.vo.AppVO;
import com.ye.yeaicodemother.model.vo.UserVO;
import com.ye.yeaicodemother.service.AppService;
import com.ye.yeaicodemother.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author <a href="https://github.com/EdsionYsy">叶盛源</a>
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{
    @Resource
    private UserService userService;

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        // 关联查询用户信息
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }

    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = appList.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        return appList.stream().map(app -> {
            AppVO appVO = getAppVO(app);
            UserVO userVO = userVOMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }


    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
        // 1.参数校验
        ThrowUtils.throwIf(appId == null || appId < 0,ErrorCode.PARAMS_ERROR,"应用ID错误");
        ThrowUtils.throwIf(StrUtil.isBlank(message) , ErrorCode.PARAMS_ERROR,"提示词不能为空");
        // 2.查询应用是否存在
        App app = getById(appId);
        ThrowUtils.throwIf(app == null , ErrorCode.NOT_FOUND_ERROR,"应用不存在");
        // 3.权限校验 仅本人可以和自己应用对话
        if(!app.getUserId().equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"无权限访问该应用");
        }
        // 4.获取生成模式
        String codeGenType = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        if(codeGenType == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"应用代码生成类型错误");
        }
        // 5.调用AI门面类生成代码 - SSE形式返回
        return aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenTypeEnum,appId);
    }

    /**
     * 根据应用id部署应用对应的代码文件到Nginx
     * @param appId 应用id
     * @param loginUser 登录的用户 用于判断权限
     * @return 可访问的部署后的url路径
     */
    @Override
    public String deployApp(Long appId,User loginUser) {
        // 1.参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0,ErrorCode.PARAMS_ERROR,"应用ID不能为空或小于等于0");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR,"用户未登陆");
        // 2.应用是否存在
        App app = getById(appId);
        if(app == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"应用不存在");
        }
        // 3.验证用户是否有权限部署该应用 仅本人可以部署
        if(!app.getUserId().equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"无权限部署该应用");
        }
        // 4.检查是否已有部署的deployKey
        String deployKey = app.getDeployKey();
        if(StrUtil.isBlank(deployKey)){
            deployKey = RandomUtil.randomString(6);
        }
        // 5.获取代码生成类型 构建生成文件目录的路径 用于复制到部署目录用
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        // 6.检查源目录是否 存在 是否文件已生成
        File sourceDir = new File(sourceDirPath);
        if(!sourceDir.exists() || !sourceDir.isDirectory()){
            // 目录不存在 或 非目录
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"应用代码不存在,请先生成代码!");
        }
        // 7.复制文件到部署目录
        String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try{
            FileUtil.copyContent(sourceDir,new File(deployDirPath),true);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"部署失败:" + e.getMessage());
        }
        // 8.更新应用到deployKey和部署时间数据
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean updateResult = this.updateById(updateApp);
        ThrowUtils.throwIf(!updateResult,ErrorCode.OPERATION_ERROR,"更新应用部署信息失败");
        // 9.返回可以访问的URL地址
        return String.format("%s/%s/",AppConstant.CODE_DEPLOY_HOST,deployKey);
    }

}
