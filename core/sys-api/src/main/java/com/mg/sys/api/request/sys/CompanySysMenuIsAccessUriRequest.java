package com.mg.sys.api.request.sys;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author sqw
 * @since 2022/06/08
 */
@Setter
@Getter
public class CompanySysMenuIsAccessUriRequest implements Serializable {

    private static final long serialVersionUID = 2725224646964066961L;

    /**
     * 系统用户主键ID
     */
    private Integer userId;

    /**
     * 资源
     */
    private String uri;

}
