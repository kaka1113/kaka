package com.mg.framework.page;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mg.framework.serializer.LongJsonSerializer;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * @author hubo
 * @since 2020/2/25
 */
@NoArgsConstructor
public class Page<T> extends com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> {

    private static final long serialVersionUID = 2285332413185444356L;

    /**
     * 总数
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    private long total;

    /**
     * 每页显示条数
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    private long size;

    /**
     * 当前页
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    private long current;

    /**
     * 总页数
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    private long pages;

    /**
     * 分页构造函数
     *
     * @param current 当前页
     * @param size    每页显示条数
     */
    public Page(long current, long size) {
        super(current, size);
    }

    @Override
    public String toString() {
        return "Page{" +
                "total=" + total +
                ", size=" + size +
                ", current=" + current +
                ", pages=" + pages +
                '}';
    }

    @Override
    public Page<T> setRecords(List<T> records) {
        super.setRecords(records);
        return this;
    }

    /**
     * Page 的泛型转换
     *
     * @param mapper 转换函数
     * @param <R>    转换后的泛型
     * @return 转换泛型后的 Page
     * @see #convert(Function)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <R> Page<R> convert(Function<? super T, ? extends R> mapper) {
        List<R> collect = this.getRecords().stream().map(mapper).collect(toList());
        return ((Page<R>) this).setRecords(collect);
    }

    /**
     * 直接转化成实体
     *
     * @param target
     * @param <R>
     * @return
     */
    public <R> Page<R> convertEntity(Class<R> target) {
        List<R> collect = this.getRecords().stream().map(r -> JSON.parseObject(JSON.toJSONString(r), target)).collect(toList());
        return ((Page<R>) this).setRecords(collect);
    }

    /**
     * Page 的泛型转换
     *
     * @param <R> 转换后的泛型
     * @return 转换泛型后的 Page
     */
    @SuppressWarnings("unchecked")
    public <R> Page<R> convert(List<R> collect) {
        return ((Page<R>) this).setRecords(collect);
    }

    /**
     * 该方法仅对具有空记录的Page转换泛型类型
     *
     * @param <R> 转换后的泛型
     * @return 转换泛型后的 Page
     */
    @SuppressWarnings("unchecked")
    public <R> Page<R> convertEmptyRecord(Class<R> targetClass) {
        if (this.getRecords() == null || this.getRecords().size() == 0) {
            return ((Page<R>) this);
        } else {
            throw new RuntimeException("the method only supports the page object conversion generic types with empty records");
        }
    }
}