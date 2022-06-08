package com.ice.framework.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author : tjq
 * @since : 2022/3/29 9:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ApiEntity对象", description = "API接口通知")
public class ApiEntity {

    @ApiModelProperty(value = "开发者")
    private String author;

    @ApiModelProperty(value = "权重")
    private Integer order;

    @ApiModelProperty(value = "手机号")
    private String telPhone;

    @ApiModelProperty(value = "服务名称")
    private String serviceName;

    @ApiModelProperty(value = "类名称")
    private String className;

    @ApiModelProperty(value = "方法名称")
    private String methodName;

    @ApiModelProperty(value = "请求参数")
    private String params;

    @ApiModelProperty(value = "异常消息")
    private String errorMsg;

    @ApiModelProperty(value = "链路id")
    private String tracingXid;


}
