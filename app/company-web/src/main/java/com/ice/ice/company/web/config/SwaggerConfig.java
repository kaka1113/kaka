
package com.ice.ice.company.web.config;

import com.ice.framework.model.CompanySysUserModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;


@Configuration
@EnableSwagger2WebMvc
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

    @Bean
    public Docket groupCompanyWebSys() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(groupApiInfo())
                .groupName("运营商企业WEB端 - 系统服务")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mg.mg.company.web.controller.sys"))
                .paths(PathSelectors.any())
                .build()
                .ignoredParameterTypes(CompanySysUserModel.class);
    }

    @Bean
    public Docket groupCompanyWebProduct() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(groupApiInfo())
                .groupName("运营商企业WEB端 - 商品服务")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mg.mg.company.web.controller.product"))
                .paths(PathSelectors.any())
                .build()
                .ignoredParameterTypes(CompanySysUserModel.class);
    }

    @Bean
    public Docket groupCompanyWebOrg() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(groupApiInfo())
                .groupName("运营商企业WEB端 - 组织服务")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mg.mg.company.web.controller.org"))
                .paths(PathSelectors.any())
                .build()
                .ignoredParameterTypes(CompanySysUserModel.class);
    }

    @Bean
    public Docket groupCompanyWebCommon() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(groupApiInfo())
                .groupName("运营商企业WEB端 - 公共服务")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mg.mg.company.web.controller.common"))
                .paths(PathSelectors.any())
                .build()
                .ignoredParameterTypes(CompanySysUserModel.class);
    }

    @Bean
    public Docket groupCompanyWebOms() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(groupApiInfo())
                .groupName("运营商企业WEB端 - 订单服务")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mg.mg.company.web.controller.oms"))
                .paths(PathSelectors.any())
                .build()
                .ignoredParameterTypes(CompanySysUserModel.class);
    }

    @Bean
    public Docket groupCompanyWebCrm() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(groupApiInfo())
                .groupName("运营商企业WEB端 - 会员服务")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mg.mg.company.web.controller.crm"))
                .paths(PathSelectors.any())
                .build()
                .ignoredParameterTypes(CompanySysUserModel.class);
    }

    private ApiInfo groupApiInfo() {
        return new ApiInfoBuilder()
                .title("API文档")
                .description("<div style='font-size:14px;color:red;'>RESTful APIs</div>")
                .termsOfServiceUrl("HB")
                .contact(new Contact("HB", "", ""))
                .version("1.0")
                .build();
    }

}
