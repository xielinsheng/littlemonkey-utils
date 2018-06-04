package com.littlemonkey.utils.reflection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.littlemonkey.utils.base.GenericType;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import sun.reflect.generics.reflectiveObjects.WildcardTypeImpl;

import java.lang.reflect.*;
import java.util.*;


public final class ReflectionUtils2 extends org.springframework.util.ReflectionUtils {

    private static Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);

    private final static ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    /**
     * <p>获取方法参数名称数组</p>
     *
     * @param targetMethod
     * @return
     */
    public static String[] getParameterNames(Method targetMethod) {
        return parameterNameDiscoverer.getParameterNames(targetMethod);
    }

    /**
     * <p>判断是否是包装类或者基本类型以及String</p>
     *
     * @param tClass
     * @return
     */
    public static boolean isSimple(Class tClass) {
        try {
            return StringUtils.endsWithIgnoreCase(tClass.getTypeName(), String.class.getTypeName())
                    || ((Class) tClass.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return tClass.isPrimitive();
        }
    }

    /**
     * <p>判断是否是集合或者map</p>
     *
     * @param target
     * @return
     */
    public static boolean isContainer(Object target) {
        if (Objects.isNull(target)) {
            throw new IllegalArgumentException("target object is null.");
        }
        return isCollection(target) || isMap(target);
    }

    /**
     * <p>判断对象是否是集合</p>
     *
     * @param target
     * @return
     */
    public static boolean isCollection(Object target) {
        if (Objects.isNull(target)) {
            throw new IllegalArgumentException("target object is null.");
        }
        return Collection.class.isAssignableFrom(target.getClass());
    }

    /**
     * <p>判断对象是否是map</p>
     *
     * @param target
     * @return
     */
    public static boolean isMap(Object target) {
        if (Objects.isNull(target)) {
            throw new IllegalArgumentException("target object is null.");
        }
        return Map.class.isAssignableFrom(target.getClass());
    }

    /**
     * <p>获取方法的参数泛型</p>
     *
     * @param targetMethod
     * @return
     */
    public static List<GenericType> getGenericType(Method targetMethod) {
        if (Objects.isNull(targetMethod)) {
            throw new IllegalArgumentException("targetMethod is null.");
        }
        Type[] genericParameterTypes = targetMethod.getGenericParameterTypes();
        List<GenericType> genericTypeList = Lists.newArrayListWithCapacity(genericParameterTypes.length);
        for (Type genericParameterType : genericParameterTypes) {
            GenericType genericType = new GenericType();
            if (genericParameterType instanceof ParameterizedType) {
                genericType.setOwnerType((Class) ((ParameterizedType) genericParameterType).getRawType());
                ParameterizedType aType = (ParameterizedType) genericParameterType;
                Type[] parameterArgTypes = aType.getActualTypeArguments();
                getGenericType(genericType, parameterArgTypes);
                genericTypeList.add(genericType);
            } else {
                genericType.setOwnerType((Class) genericParameterType);
                genericTypeList.add(genericType);
            }
        }
        return genericTypeList;
    }


    private static void getGenericType(GenericType generic, Type[] types) {
        List<GenericType> genericTypeList = Lists.newArrayListWithCapacity(types.length);
        for (Type type : types) {
            GenericType genericType = new GenericType();
            System.out.println(type.getClass());
            if (type instanceof ParameterizedTypeImpl) {
                genericType.setOwnerType(((ParameterizedTypeImpl) type).getRawType());
                getGenericType(genericType, ((ParameterizedTypeImpl) type).getActualTypeArguments());
            } else if (type instanceof Class) {
                genericType.setOwnerType((Class) type);
            } else if (type instanceof WildcardTypeImpl){// 通配符类型
                genericType.setOwnerType(Object.class);
                genericType.setWildcard(true);
            }
            genericTypeList.add(genericType);
        }
        generic.setGenericTypeList(genericTypeList);
    }

    /**
     * 直接读取对象属性值,无视private/protected修饰符,不经过getter函数.
     */
    public static Object getFieldValue(final Object object, final String fieldName) {
        Field field = getDeclaredField(object, fieldName);
        if (field == null)
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        makeAccessible(field);
        Object result = null;
        try {
            result = field.get(object);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
        return result;
    }

    /**
     * 直接设置对象属性值,无视private/protected修饰符,不经过setter函数.
     */
    public static void setFieldValue(final Object object, final String fieldName, final Object value) {
        Field field = getDeclaredField(object, fieldName);
        if (field == null)
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        makeAccessible(field);
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常:{}", e.getMessage());
        }
    }

    /**
     * 循环向上转型,获取对象的DeclaredField.
     */
    protected static Field getDeclaredField(final Object object, final String fieldName) {
        return getDeclaredField(object.getClass(), fieldName);
    }

    /**
     * 循环向上转型,获取类的DeclaredField.
     */
    @SuppressWarnings("unchecked")
    protected static Field getDeclaredField(final Class clazz, final String fieldName) {
        for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                // Field不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 强制转换fileld可访问.
     */
    public static void makeAccessible(final Field field) {
        if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
            field.setAccessible(true);
        }
    }

    /**
     * 通过反射,获得定义Class时声明的父类的泛型参数的类型. 如public UserDao extends HibernateDao
     *
     * @param clazz The class to introspect
     * @return the first generic declaration, or Object.class if cannot be
     * determined
     */
    @SuppressWarnings("unchecked")
    public static Class getSuperClassGenericType(final Class clazz) {
        return getSuperClassGenericType(clazz, 0);
    }

    /**
     * 通过反射,获得定义Class时声明的父类的泛型参数的类型. 如public UserDao extends
     * HibernateDao
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be
     * determined
     */
    @SuppressWarnings("unchecked")
    public static Class getSuperClassGenericType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            logger.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            logger.warn("Index:" + index + ", Size of" + clazz.getSimpleName() + "'s Parameterized Type:" + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            logger.warn(clazz.getSimpleName() + "not set the actual class on superclass generic parameter");
            return Object.class;
        }
        return (Class) params[index];
    }

    /**
     * 提取集合中的对象的属性,组合成List.
     *
     * @param collection   来源集合.
     * @param propertyName 要提取的属性名.
     */
    @SuppressWarnings("unchecked")
    public static Collection fetchElementPropertyToList(final Collection collection, final String propertyName) throws Exception {
        return Collections2.transform(collection, new Function<Object, Object>() {
            @Override
            public Object apply(Object input) {
                try {
                    return PropertyUtils.getProperty(input, propertyName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
}
