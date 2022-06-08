package com.ice.framework.enums;

import com.ice.framework.util.ObjectUtils;

/**
 * @author tjq
 * @since 2022/5/17 17:30
 */
public enum ModuleEnum {

    OMS(ProjectEnum.MG, "OMS", "OMS订单服务"),
    ;

    private ProjectEnum project;
    private String module;
    private String desc;

    ModuleEnum(ProjectEnum project, String module, String desc) {
        this.project = project;
        this.module = module;
        this.desc = desc;
    }

    public ProjectEnum getProject() {
        return project;
    }

    public void setProject(ProjectEnum project) {
        this.project = project;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static ModuleEnum projectOf(ProjectEnum project) {
        if (ObjectUtils.isEmpty(project)) {
            return null;
        }
        ModuleEnum[] values = ModuleEnum.values();
        for (ModuleEnum item : values) {
            if (item.getProject() == project) {
                return item;
            }
        }
        return null;
    }

    public static ModuleEnum keyOf(String module) {
        if (ObjectUtils.isEmpty(module)) {
            return null;
        }
        ModuleEnum[] values = ModuleEnum.values();
        for (ModuleEnum item : values) {
            if (item.getModule().equalsIgnoreCase(module)) {
                return item;
            }
        }
        return null;
    }

}
