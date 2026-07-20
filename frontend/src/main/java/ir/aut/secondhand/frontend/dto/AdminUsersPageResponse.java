package ir.aut.secondhand.frontend.dto;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersPageResponse {

    private List<AdminUserResponse> content =
            new ArrayList<>();

    private int number;
    private int size;
    private int totalPages;
    private long totalElements;
    private boolean first;
    private boolean last;

    public AdminUsersPageResponse() {
    }

    public List<AdminUserResponse> getContent() {
        return content;
    }

    public void setContent(
            List<AdminUserResponse> content
    ) {
        this.content = content;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}