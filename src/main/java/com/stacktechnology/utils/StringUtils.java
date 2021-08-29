package com.stacktechnology.utils;

public class StringUtils {

    /**
     * Преобразовать текст text в строку, содержащую технологический стек, который разделен запятыми
     */
    public static String parseTextAndGetOnlyStackTechnology(String text) {
        StringBuilder stringBuilder = new StringBuilder();

        boolean lastSymbolIsComma = false;

        for (var ch : text.toCharArray()) {
            if (isSymbolWhyNotBeDisplayed(ch)) {
                continue;
            }
            if (isSeparatorSymbol(ch) || isRussianSymbol(ch)) {
                if (!lastSymbolIsComma && !stringBuilder.isEmpty()) {
                    stringBuilder.append(",");
                    lastSymbolIsComma = true;
                }
                continue;
            }
            if (isNumberSymbol(ch)) {
                if (!lastSymbolIsComma && !stringBuilder.isEmpty()) {
                    stringBuilder.append(ch);
                    lastSymbolIsComma = false;
                }
                continue;
            }
            stringBuilder.append(ch);
            lastSymbolIsComma = false;
        }

        if (lastSymbolIsComma) {
            stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
        }

        return stringBuilder.toString();
    }

    /**
     * Проверить, является ли символ ch числом
     */
    private static boolean isNumberSymbol(char ch) {
        return (ch >= '0' && ch <= '9');
    }

    /**
     * Проверить, является ли символ ch разделительным знаком
     */
    private static boolean isSeparatorSymbol(char ch) {
        return (ch == ',' || ch == ';' || ch == '/' || ch == '\\' || ch == '(' || ch == ')' || ch == '+' || ch == '|' || ch == '.');
    }

    /**
     * Проверить, является ли символ ch символом русского языка
     */
    private static boolean isRussianSymbol(char ch) {
        return ((ch >= 'а' && ch <= 'я') || (ch >= 'А' && ch <= 'Я'));
    }

    /**
     * Проверить, является ли символ ch знаком, который не должен быть отображен для корректного формирования стека
     */
    private static boolean isSymbolWhyNotBeDisplayed(char ch) {
        return (ch == '-' || ch == ' ' || ch == '\n');
    }
}
