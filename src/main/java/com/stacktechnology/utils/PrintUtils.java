package com.stacktechnology.utils;

import com.stacktechnology.Category;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.util.List;

public class PrintUtils {

    private static final Logger log = (Logger) LogManager.getLogger(PrintUtils.class);

    /**
     * Максимальное количество сообщений, которое выводится для описания одной категории
     */
    private static final int MAX_MSG_BY_CATEGORY = 4;

    /**
     * Вывести все категории, которые встречались в вакансиях больше minCount раз.
     * Категория выводится в формате: 'name' -> 'count' -> {msg1, msg2, ..., msgN}
     */
    public static void printCategoryWithMsg(int minCount, List<Category> categories) {
        categories.stream()
                .filter(category -> ((category.getCategoryName().length() > 1) && (category.getSearchResultList().size() >= minCount)))
                .forEach(category -> {
                    log.info(String.format("%s -> %s -> {", category.getCategoryName(), category.getSearchResultList().size()));
                    category.getSearchResultList().stream().limit(MAX_MSG_BY_CATEGORY).forEach(text -> log.info("\t" + text));
                    log.info("}");
                });
    }

    /**
     * Вывести все категории, которые встречались в вакансиях больше minCount раз.
     * Категория выводится в формате: 'name' -> 'count'
     */
    public static void printCategoryWithoutMsg(int minCount, List<Category> categories) {
        categories.stream()
                .filter(category -> ((category.getCategoryName().length() > 1) && (category.getSearchResultList().size() >= minCount)))
                .forEach(category -> {
                    log.info(String.format("%s -> %s", category.getCategoryName(), category.getSearchResultList().size()));
                });
    }

}
