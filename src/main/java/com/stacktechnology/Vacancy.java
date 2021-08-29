package com.stacktechnology;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class Vacancy {

    /**
     * Заголовок вакансии
     */
    private String title;

    /**
     * Название компании
     */
    private String company;

    /**
     * Ссылка на вакансию
     */
    private String link;

    /**
     * Текст вакансии
     */
    private String vacancyText;

    /**
     * Блок текста, содержащий технологический стек
     */
    private String vacancyStack;

    public Vacancy(String title, String company, String link) {
        this.title = title;
        this.company = company;
        this.link = link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vacancy vacancy = (Vacancy) o;
        return Objects.equals(title, vacancy.title) && Objects.equals(company, vacancy.company);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, company);
    }

}
