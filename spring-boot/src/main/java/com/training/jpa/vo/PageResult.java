package com.training.jpa.vo;

import java.util.List;

public class PageResult<T> {
    private List<T> data;
    private GenericPageable pageable;

    public PageResult(List<T> data, GenericPageable pageable) {
        this.data = data;
        this.pageable = pageable;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public GenericPageable getPageable() {
        return pageable;
    }

    public void setPageable(GenericPageable pageable) {
        this.pageable = pageable;
    }
}

