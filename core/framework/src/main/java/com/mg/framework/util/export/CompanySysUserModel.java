package com.mg.framework.util.export;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author hubo
 * @since 2020/2/25
 */
@Setter
@Getter
@ApiModel(value = "Mos后台用户实体")
public class CompanySysUserModel implements Serializable {

    private static final long serialVersionUID = 3817168050921069686L;

    /**
     * 用户ID
     */
    private Integer id;
    /**
     * 用户姓名
     */
    private String name;
    /**
     * 应用 COMPANY - 企业平台
     */
    private String app;
    /**
     * 角色id
     */
    private Integer roleId;
    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 较长的用户id--第三方的应用用户--2021/1/11 liyangsa
     */
    private Long longId;

    /**
     * //XQ - 210818 - 2b商户优惠券
     * 2b用户转化超长新增的字段
     */
    @ApiModelProperty(value = "用户id - organizationId")
    private String userId;



}
