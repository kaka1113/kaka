package com.ice.framework.model;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author hubo
 * @since 2020/2/25
 */
public class ResultModel<T> implements Serializable {

    private static final long serialVersionUID = -199320501404298336L;

    /**
     * 响应码
     */
    private int code;
    /**
     * 响应数据
     */
    private T data;
    /**
     * 响应信息
     */
    private String msg;
    /**
     * 响应时间戳
     */
    private LocalDateTime time;
    /**
     * 链路id
     */
    private String tracingXid;
    /**
     * 服务名称
     */
    private String serviceName;

    //中台返回结果
    //@JsonIgnore
    private Boolean success;
    //@JsonIgnore
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        if (ObjectUtils.isNotEmpty(MDC.getMDCAdapter()) &&
                ObjectUtils.isNotEmpty(serviceName)) {
            MDC.put("SERVICE_NAME", serviceName);
        }
        if (null != success) {
            code = success ? 0 : -1;
        }
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        if (null != success && StringUtils.isNotBlank(message)) {
            msg = message;
        }
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getTracingXid() {
        if (ObjectUtils.isNotEmpty(MDC.getMDCAdapter())) {
            return MDC.get("TRACING_XID");
        }
        return tracingXid;
    }

    public void setTracingXid(String tracingXid) {
        this.tracingXid = tracingXid;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "ResultModel{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", time=" + time +
                ", data=" + data +
                '}';
    }
}
