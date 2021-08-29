package com.stacktechnology.pages;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Окно с полным описанием вакансии
 */
public class VacancyPage {

    /**
     * Полная информация, указанная в вакансии
     */
    public static SelenideElement getVacancyInfo() {
        return $(By.xpath("//div[@data-qa='vacancy-description']"));
    }

}
