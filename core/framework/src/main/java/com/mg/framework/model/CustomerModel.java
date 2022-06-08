package com.mg.framework.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Vick
 * @since 2021/1/4
 */
@Data
public class CustomerModel implements Serializable {

    private static final long serialVersionUID = -8457398499787140910L;

    /**
     * @see "com.gateon.mos.customer.api.enums.wechatinfo.AppTypeEnum"
     */
    @ApiModelProperty(value = "应用类型")
    private String appType;

    @ApiModelProperty(value = "appId")
    private String appId;

    /**
     * @see "com.gateon.mos.customer.api.enums.wechatinfo.LoginTypeEnum"
     */
    @ApiModelProperty(value = "登录类型")
    private String loginType;

    @ApiModelProperty(value = "openId[只有LOGIN_APP_BY_PHONE类型才可能为空]")
    private String openId;

    @ApiModelProperty(value = "会员id")
    private Long id;

    @ApiModelProperty(value = "客户姓名")
    private String customerName;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "用户头像")
    private String headImg;

    @ApiModelProperty(value = "用户手机号")
    private Long phone;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "等级 0：没有等级")
    private Long gradeId;

    @ApiModelProperty(value = "等级失效时间")
    private LocalDate gradeExpire;

    @ApiModelProperty(value = "宝付通账号")
    private String bftContractNo;

}
