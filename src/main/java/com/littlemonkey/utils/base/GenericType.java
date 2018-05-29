package com.littlemonkey.utils.base;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: xls
 * @Description: 包装一个方法的参数的泛型类型的链表
 * @Date: Created in 16:30 2018/4/4
 * @Version: 1.0
 */
public class GenericType {
    /**
     * 类型
     */
    private Class ownerType;

    /**
     * 是否为通配符类型
     */
    private Boolean isWildcard = Boolean.FALSE;

    private List<GenericType> genericTypeList;

    public Class getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(Class ownerType) {
        this.ownerType = ownerType;
    }

    public Boolean getWildcard() {
        return isWildcard;
    }

    public void setWildcard(Boolean wildcard) {
        isWildcard = wildcard;
    }

    public List<GenericType> getGenericTypeList() {
        return genericTypeList;
    }

    public void setGenericTypeList(List<GenericType> genericTypeList) {
        this.genericTypeList = genericTypeList;
    }

    @Override
    public String toString() {
        return "GenericType{" +
                "ownerType=" + ownerType +
                ", isWildcard=" + isWildcard +
                ", genericTypeList=" + genericTypeList +
                '}';
    }
}
