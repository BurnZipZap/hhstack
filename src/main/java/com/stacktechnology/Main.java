package com.stacktechnology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.stacktechnology.pages.SearchVacancyPage;
import com.stacktechnology.pages.VacancyPage;
import com.stacktechnology.utils.PrintUtils;
import com.stacktechnology.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.util.*;
import java.util.regex.Pattern;


public class Main {

    private static final Logger log = (Logger) LogManager.getLogger(Main.class);

    /**
     * Количество страниц с результатами поиска, которые необходимо просканировать
     */
    private static final int COUNT_PAGE_BY_SEARCH = 30;

    /**
     * Минимальное количество сообщений, которое должно быть у категории, чтобы она была выведена на экран
     */
    private static final int MIN_COUNT_MSG_BY_CATEGORY_FOR_PRINT = 5;

    public static void main(String[] args) {
        Configuration.headless = true;
        Configuration.pageLoadTimeout = 1000 * 60;
        Selenide.open("https://rostov.hh.ru/search/vacancy?search_field=name&search_field=company_name&search_field=description&text=Java+QA");

        log.info("Шаг 1. Сканирование страницы с перечнем вакансий (с 1 стр. по " + COUNT_PAGE_BY_SEARCH + ") и сбор ссылок на данные вакансии.");
        Set<Vacancy> vacancies = new HashSet<>();
        for (int pageNumber = 2; pageNumber < COUNT_PAGE_BY_SEARCH; pageNumber++) {
            SearchVacancyPage.getVacanciesCard().forEach(card ->
                    vacancies.add(new Vacancy(card.getTitle().text(), card.getCompany().text(), card.getLink().getAttribute("href"))));
            SearchVacancyPage.goToPage(pageNumber);
        }
        log.info("Сканирование завершилось успешно. Собрано " + vacancies.size() + " вакансий!");

        log.info("Шаг 2. Открытие ссылок на каждую вакансию и сканирование содержимого на наличие стека технологий (это займет достаточно большое количество времени!)");
        int count = 0;
        for (var vacancy : vacancies) {
            Selenide.open(vacancy.getLink());
            if(VacancyPage.getVacancyInfo().isDisplayed()) {
                vacancy.setVacancyText(VacancyPage.getVacancyInfo().text());
                vacancy.setVacancyStack(getOnlyStackText(vacancy.getVacancyText()));
            }
            count++;
            if (count % 10 == 0) {
                log.info("Просканировано " + count + " вакансий. Осталось: " + (vacancies.size() - count));
            }
        }

        log.info("Шаг 3. Анализ собранных данных, создание категорий и подсчет их востребованности");
        List<Category> categories = new ArrayList<>();
        vacancies.forEach(vacancy -> {
            parseBlockTextWithStackAndAddCategories(vacancy.getVacancyStack(), categories);
            parseBlockTextWithStackAndAddLinesToResultList(vacancy.getVacancyStack(), categories);
        });

        Collections.sort(categories);

        PrintUtils.printCategoryWithMsg(MIN_COUNT_MSG_BY_CATEGORY_FOR_PRINT, categories);
       // log.info("\n\n\n");
       // PrintUtils.printCategoryWithoutMsg(MIN_COUNT_MSG_BY_CATEGORY_FOR_PRINT, categories);
    }

    /**
     * Добавить в categories все категории, которые присутствуют в vacancyBlockTextWithStack.
     */
    private static void parseBlockTextWithStackAndAddCategories(String vacancyBlockTextWithStack, List<Category> categories) {
        if (vacancyBlockTextWithStack == null || vacancyBlockTextWithStack.isEmpty()) return;
        var stackTechnology = StringUtils.parseTextAndGetOnlyStackTechnology(vacancyBlockTextWithStack);
        var stackTechnologyArray = stackTechnology.split(",");
        for (var technology : stackTechnologyArray) {
            if (categories == null || categories.isEmpty() || !isCategoryAlreadyAddedToCategoryList(technology, categories)) {
                categories.add(new Category(technology.toLowerCase()));
            }
        }
    }

    /**
     * Добавить в categories информацию в поле searchResultList о строках содержащихся в vacancyBlockTextWithStack, которые коррелируют с этой категорией
     * (Допуситим в vacancyBlockTextWithStack есть строка где говорится о SQL и есть категория 'SQL', тогда данная строка будет добавлена в searchResultList для этой категории)
     */
    private static void parseBlockTextWithStackAndAddLinesToResultList(String vacancyBlockTextWithStack, List<Category> categories) {
        if (vacancyBlockTextWithStack == null || vacancyBlockTextWithStack.isEmpty()) return;
        var vacancyLines = vacancyBlockTextWithStack.toLowerCase().split("\n");
        for (var line : vacancyLines) {
            var stackLine = StringUtils.parseTextAndGetOnlyStackTechnology(line);
            categories.stream()
                    .filter(category -> stackLine.contains(category.getCategoryName()))
                    .forEach(category -> category.addSearchResultList(line));
        }
    }

    /**
     * Проверить наличие категории с названием categoryName в categories
     */
    private static boolean isCategoryAlreadyAddedToCategoryList(String categoryName, List<Category> categories) {
        return categories.stream()
                .filter(category -> category.getCategoryName().toLowerCase().equals(categoryName.toLowerCase()))
                .count() > 0;
    }

    /**
     * Обрезать исходный текст до того блока, где указан стек технологий
     */
    private static String getOnlyStackText(String vacancyText) {
        var regexTextNotContainsRussianSymbols = "^[^а-яА-Я]*$";
        if (Pattern.matches(regexTextNotContainsRussianSymbols, vacancyText)) {
            log.info("1 вакансия была проигнорирована, т.к. оформлена на английском языке (в будущих версиях добавим возможность анализа таких вакансий)");
            return null;
        }
        for (var partText : vacancyText.split(":")) {
            if (isStackText(partText)) {
                return partText.substring(0, partText.lastIndexOf('\n')); //Последняя строка текста относится к другому абзацу, она не нужна для анализа
            }
        }
        log.info("1 вакансия была проигнорирована, т.к. стиль оформления вакансии не подается алгоритмам парсинга");
        return null;
    }

    /**
     * Проверить, является ли текст part стеком технологий
     * (если в строке part встречается больше 2-ух слов из массива wordsThatOccurInStackTechnology, то предполагается, что это стек технологий)
     */
    private static boolean isStackText(String part) {
        var wordsThatOccurInStackTechnology = new String[]{"опыт", "знание", "знает", "умеет", "знания", "понимает", "знаешь", "разбираешься",
                "умеешь", "знаком", "знаниями", "обладаешь", "желание", "умение", "владение"};
        int count = 0;
        for (var word : wordsThatOccurInStackTechnology) {
            count += (part.toLowerCase().length() - part.toLowerCase().replace(word, "").length()) / word.length();
        }
        return count >= 2;
    }

}
