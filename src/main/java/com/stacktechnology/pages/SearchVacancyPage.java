package com.stacktechnology.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Окно hh с перечнем вакансий по текущему запросу
 */
public class SearchVacancyPage {

    /**
     * Перейти на страницу номер number
     */
    public static void goToPage(int number) {
        $(By.xpath("//a[@class='bloko-button']//span[text()='" + number + "']")).click();
    }

    /**
     * Получить лист всех карточек с вакансиями на текущем листе
     */
    public static List<VacancyCard> getVacanciesCard() {
        List<VacancyCard> vacanciesCardList = new ArrayList<>();
        standardVacancyContainer().forEach(container -> vacanciesCardList.add(new VacancyCard(container)));
        standardPlusVacancyContainer().forEach(container -> vacanciesCardList.add(new VacancyCard(container)));
        return vacanciesCardList;
    }

    /**
     * Все вакансии с подпиской standardPlus
     */
    private static ElementsCollection standardPlusVacancyContainer() {
        return $$(By.xpath("//div[@data-qa='vacancy-serp__vacancy vacancy-serp__vacancy_standard_plus']"));
    }

    /**
     * Все вакансии с подпиской standard
     */
    private static ElementsCollection standardVacancyContainer() {
        return $$(By.xpath("//div[@data-qa='vacancy-serp__vacancy vacancy-serp__vacancy_standard']"));
    }

    /**
     * Карточка с вакансией
     */
    public static class VacancyCard {

        public VacancyCard(SelenideElement cardBlock) {
            this.cardBlock = cardBlock;
        }

        /**
         * Карточка со всем содержимым
         */
        private SelenideElement cardBlock;

        /**
         * Заголовок
         */
        public SelenideElement getTitle() {
            return cardBlock.$(By.xpath(".//div[@class='vacancy-serp-item__info']"));
        }

        /**
         * Название компании
         */
        public SelenideElement getCompany() {
            return cardBlock.$(By.xpath(".//div[@class='vacancy-serp-item__meta-info-company']"));
        }

        /**
         * Ссылка на вакансию
         */
        public SelenideElement getLink() {
            return cardBlock.$(By.xpath(".//a[@data-qa='vacancy-serp__vacancy-title']"));
        }
    }
}

