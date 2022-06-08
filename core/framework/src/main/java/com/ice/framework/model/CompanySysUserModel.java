package com.ice.framework.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author hubo
 * @since 2020/2/25
 */
@Setter
@Getter
public class CompanySysUserModel implements Serializable {

    private static final long serialVersionUID = 3817168050921069686L;

    /**
     * 用户ID
     */
    private Integer id;
    /**
     * 运营商企业ID
     */
    private Long companyId;
    /**
     * 用户姓名
     */
    private String name;


}
