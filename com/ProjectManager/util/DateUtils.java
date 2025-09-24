package com.ProjectManager.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class DateUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public static LocalDate lerData(String prompt) {
        Scanner scanner = new Scanner(System.in);
        LocalDate data = null;
        
        while (data == null) {
            try {
                System.out.print(prompt + " (dd/MM/yyyy): ");
                String input = scanner.nextLine().trim();
                data = LocalDate.parse(input, FORMATTER);
            } catch (Exception e) {
                System.out.println("Data inválida! Tente novamente.");
            }
        }
        
        return data;
    }
    
    public static String formatarData(LocalDate data) {
        return data != null ? data.format(FORMATTER) : "N/A";
    }
}