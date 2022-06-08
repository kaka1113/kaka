package com.mg.sys.api.feign;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.ice.framework.response.ResponseModel;
import com.mg.sys.api.request.sys.CompanySysMenuIsAccessUriRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author sqw
 * @since 2022/06/08
 */
@FeignClient("ice-sys-service")
public interface CompanySysMenuFeign {

    public static final String REQUEST_MAPPING = "/companySysMenu";

    @PostMapping(value = REQUEST_MAPPING + "/isAccessUri")
    @ApiOperation(value = "是否可访问的资源路径", notes = "是否可访问的资源路径")
    @ApiOperationSupport(author = "HB")
    public ResponseModel isAccessUri(@Valid @RequestBody CompanySysMenuIsAccessUriRequest request);
}
