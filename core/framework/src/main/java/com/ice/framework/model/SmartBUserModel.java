package com.ice.framework.model;

import com.ice.framework.enums.RoleEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author hubo
 * @since 2020/2/25
 */
@Setter
@Getter
public class SmartBUserModel implements Serializable {

    private static final long serialVersionUID = 3817168050921069686L;

    /**
     * 用户ID
     */
    private Long id;
    /**
     * 登录角色
     */
    private RoleEnum role;

}
