package com.ice.framework.page;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author hubo
 * @since 2021/8/20
 */
@Setter
@Getter
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 5538388032602460753L;

    @ApiModelProperty(value = "当前页数", required = true)
    @NotNull(message = "当前页数不能为空。")
    @Min(value = 1)
    private Long currentPage;

    @ApiModelProperty(value = "每页条数", required = true)
    @NotNull(message = "每页条数不能为空。")
    @Min(value = 1)
    private Long pageSize;

}
