package com.payserver.common.result;

import lombok.Data;

import java.util.List;

/**
 * 分页结果封装类
 *
 * @param <T> 列表元素类型
 */
@Data
public class PageResult<T> {

    /** 当前页数据列表 */
    private List<T> list;
    /** 总记录数 */
    private long total;
    /** 当前页码 */
    private int page;
    /** 每页大小 */
    private int size;

    /**
     * 构建分页结果
     *
     * @param list  当前页数据
     * @param total 总记录数
     * @param page  页码
     * @param size  每页大小
     * @param <T>   元素类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> list, long total, int page, int size) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setList(list);
        pageResult.setTotal(total);
        pageResult.setPage(page);
        pageResult.setSize(size);
        return pageResult;
    }
}
