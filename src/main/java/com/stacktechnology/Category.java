package com.stacktechnology;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Category implements Comparable<Category> {

    /**
     * Название категории
     */
    @Setter
    @Getter
    private String categoryName;

    /**
     * Сообщения, которые попадают под данную категорию
     */
    private final List<String> searchResultList;

    public Category(String categoryName) {
        this.categoryName = categoryName;
        searchResultList = new ArrayList<>();
    }

    public void addSearchResultList(String text) {
        searchResultList.add(text);
    }

    public List<String> getSearchResultList() {
        return new ArrayList<>(searchResultList);
    }

    @Override
    public int compareTo(Category category) {
        return category.searchResultList.size() - this.searchResultList.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(categoryName, category.categoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryName);
    }
}
