package br.hallel.relational.api.app.global.utils;

public class NumberUtils {
    public static Double extrairEConverterParaDouble(String valorString) {
        if (valorString == null || valorString.trim().isEmpty()) {
            return null;
        }
        try {
            String somenteNumeros = valorString.replaceAll("[^\\d.,]+", "");
            somenteNumeros = somenteNumeros.replace(',', '.');
            return Double.parseDouble(somenteNumeros);
        } catch (NumberFormatException e) {
            System.err.println("Erro ao converter '" + valorString + "' para double: " + e.getMessage());
            return null;
        }
    }
}
