package com.littlemonkey.utils.collect;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;


public class Collections3 extends CollectionUtils {

    /**
     * <p>过滤集合</p>
     *
     * @param unfiltered
     * @param predicate
     * @param <E>
     * @return
     */
    public static <E> Collection<E> filter(Collection<E> unfiltered, Predicate<? super E> predicate) {
        return Collections2.filter(unfiltered, predicate);
    }

    public static boolean isEmpty(Object[] objects) {
        return (objects == null || objects.length == 0);
    }

    public static boolean isContainer(Class targetClass) {
        return Collection.class.isAssignableFrom(targetClass) || targetClass.isArray();
    }
}
