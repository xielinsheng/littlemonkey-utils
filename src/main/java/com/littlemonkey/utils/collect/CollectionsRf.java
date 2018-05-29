package com.littlemonkey.utils.collect;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.Collection;


public class CollectionsRf {

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
}
