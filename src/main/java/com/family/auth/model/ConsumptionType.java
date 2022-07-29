package com.family.auth.model;

import java.util.Date;

public class ConsumptionType {
    private Integer typeId;

    private Integer parentTypeId;

    private String typeName;

    private String owner;

    private Integer groupId;

    private Integer status;

    private Byte isReadonly;

    private String createBy;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    private Boolean useArea;

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getParentTypeId() {
        return parentTypeId;
    }

    public void setParentTypeId(Integer parentTypeId) {
        this.parentTypeId = parentTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Byte getIsReadonly() {
        return isReadonly;
    }

    public void setIsReadonly(Byte isReadonly) {
        this.isReadonly = isReadonly;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Boolean getUseArea() {
        return useArea;
    }

    public void setUseArea(Boolean useArea) {
        this.useArea = useArea;
    }
}