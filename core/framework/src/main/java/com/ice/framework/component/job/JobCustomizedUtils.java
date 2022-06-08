package com.ice.framework.component.job;

import com.alibaba.fastjson.JSONObject;
import com.ice.framework.util.DateUtil;
import com.ice.framework.util.HttpUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.glue.GlueTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Author: qiang.su
 * Date: 2020/4/21
 * Msg:
 */
@Slf4j
@Component
public class JobCustomizedUtils {

    private final static String EXPIRE_KEY_PREFIX = "xxl:expire-task:";
    @Value("${mg.job.admin.addresses:#{null}}")
    private String adminAddresses;
    @Value("${mg.job.executor.appname:#{null}}")
    private String appName;
    @Autowired
    private RedisTemplate redisTemplate;


    //-------------------------V3.0.0-------------------------

    /**
     * @Author: qiang.su
     * @since: 2021/9/24 11:18
     * @Desc: 自动保存
     */
    public void addJobTaskCancelable(LocalDateTime executeDate, String expireKey
            , String jobTag, Integer retry, String desc, String param) throws Exception {
        expireKey = EXPIRE_KEY_PREFIX + expireKey;

        String quartz = getCron(executeDate);
        ReturnT<String> jobTask = addJobTask(quartz, jobTag, retry, desc, param);
        String jobId = jobTask.getContent();
        //将对应的JOB_ID放入缓存
        Long expire = DateUtil.getDiffValueAbs(LocalDateTime.now(), executeDate, TimeUnit.SECONDS);
        //额外扩充60秒，防止执行方执行太慢调用removeJobTaskCancelable时已经不存在了
        redisTemplate.opsForValue().set(expireKey, jobId, expire + 60L, TimeUnit.SECONDS);
    }
    //------------------------V3.0.0 end-------------------------

    /**
     * @Author: qiang.su
     * @since: 2021/9/24 11:19
     * @Desc: 建议 addJobTaskCancelable
     */
    public ReturnT<String> addJobTask(String cron, String jobTag, Integer retry, String desc, String param) throws Exception {
        JobModifyRequest request = new JobModifyRequest();
        //查询当前job appName的groupid
        String findIdUrl = adminAddresses + "/jobgroup/findByAppName/" + appName;
        String groupIdResp = HttpUtil.sendGet(findIdUrl);
        Integer groupId = JSONObject.parseObject(groupIdResp, Integer.class);
        request.setJobGroup(groupId);
        //quartz表达式
        request.setScheduleConf(cron);
        //执行对象
        request.setExecutorHandler(jobTag);
        //任务描述
        request.setJobDesc(desc);
        //路由策略
        request.setExecutorRouteStrategy("ROUND");
        //阻塞处理
        request.setExecutorBlockStrategy("SERIAL_EXECUTION");
        //超时时间  大于0生效
        request.setExecutorTimeout(0);
        //运行模式
        request.setGlueType(GlueTypeEnum.BEAN.name());
        //重试次数
        request.setExecutorFailRetryCount(retry);
        //负责人
        request.setAuthor("AUTO");
        //param
        request.setExecutorParam(param);
        request.setScheduleType("CRON");
        request.setMisfireStrategy("DO_NOTHING");
        String updateUrl = adminAddresses + "/jobinfo/addApi";
        String response = HttpUtil.sendPost(updateUrl, request);
        ReturnT responseObject = JSONObject.parseObject(response, ReturnT.class);
        int code = responseObject.getCode();
        if (code == ReturnT.FAIL_CODE) {
            log.error("创建XXLJOB失败，jobTag: = " + jobTag + ",desc : " + desc + ",param" + param + ",error = " + responseObject.getMsg());
        }
        return responseObject;
    }

    /**
     * author: liaojianjie
     */
    public String getCron(LocalDateTime localDateTime) {
        int year = localDateTime.getYear(); //年
        int month = localDateTime.getMonthValue(); //月
        int day = localDateTime.getDayOfMonth(); //日
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        int second = localDateTime.getSecond();
        String cron = "%s %s %s %s %s ? %s";
        String cronStr = String.format(cron, second, minute, hour, day, month, year);
        return cronStr;
    }

    /**
     * @Author: qiang.su
     * @since: 2021/6/1 16:20
     * @Desc: 删除定时任务 ，需要手动 addJobTask预先保存返回id。 如果麻烦建议用addJobTaskCancelable
     */
    public ReturnT<String> removeJobTask(Long jobInfoId) throws Exception {
        String updateUrl = adminAddresses + "/jobinfo/remove/remote/" + jobInfoId;
        String response = HttpUtil.sendPost(updateUrl, updateUrl);
        ReturnT responseObject = JSONObject.parseObject(response, ReturnT.class);
        return responseObject;
    }

}
