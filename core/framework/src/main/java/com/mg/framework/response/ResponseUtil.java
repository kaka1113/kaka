package com.mg.framework.response;

/**
 * @author hubo
 * @since 2021/8/19
 */
public class ResponseUtil {

    /**
     * 成功，不带返回对象
     *
     * @return
     */
    public static ResponseModel success() {
        return new ResponseModel();
    }

    /**
     * 成功，带返回对象
     *
     * @param obj
     * @return
     */
    public static ResponseModel success(Object obj) {
        ResponseModel model = new ResponseModel();
        model.setData(obj);
        return model;
    }

    /**
     * 错误，不带返回对象（自定义错误码）
     *
     * @param errCode
     * @param message
     * @return
     */
    public static ResponseModel fail(int errCode, String message) {
        ResponseModel model = new ResponseModel();
        model.setSuccess(false);
        model.setErrCode(errCode);
        model.setMessage(message);
        return model;
    }

    /**
     * 错误，带返回对象（自定义错误码）
     *
     * @param errCode
     * @param message
     * @param obj
     * @return
     */
    public static ResponseModel fail(int errCode, String message, Object obj) {
        ResponseModel model = new ResponseModel();
        model.setSuccess(false);
        model.setErrCode(errCode);
        model.setMessage(message);
        model.setData(obj);
        return model;
    }

    /**
     * 未查询到相关数据
     *
     * @return
     */
    public static ResponseModel noData() {
        ResponseModel model = new ResponseModel();
        model.setSuccess(true);
        model.setErrCode(ResponseErrorCodeEnum.NODATA_EXCEPTION.getCode());
        model.setMessage(ResponseErrorCodeEnum.NODATA_EXCEPTION.getMsg());
        return model;
    }

    /**
     * 错误，接口调用异常
     *
     * @param message
     * @return
     */
    public static ResponseModel fail(String message) {
        ResponseModel model = new ResponseModel();
        model.setSuccess(false);
        model.setErrCode(ResponseErrorCodeEnum.API_ERROR.getCode());
        model.setMessage(message);
        return model;
    }

}
