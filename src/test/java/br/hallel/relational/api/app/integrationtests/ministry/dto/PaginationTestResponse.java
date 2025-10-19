package br.hallel.relational.api.app.integrationtests.ministry.dto;

import java.util.List;

public class PaginationTestResponse<T> {
    List<T> content;
    PageTestResponseInfo page;

    public PaginationTestResponse(List<T> content, PageTestResponseInfo page) {
        this.content = content;
        this.page = page;
    }

    public PaginationTestResponse() {
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public PageTestResponseInfo getPage() {
        return page;
    }

    public void setPage(PageTestResponseInfo page) {
        this.page = page;
    }
}
