package com.ProjectManager.view;

import java.util.Scanner;

public class ConsoleUtils {
    private static final Scanner scanner = new Scanner(System.in);

    public static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static String lerTexto(String prompt) {
    System.out.print(prompt);
    java.util.Scanner scanner = new java.util.Scanner(System.in);
    return scanner.nextLine();
}

    public static void mostrarTitulo(String titulo) {
        System.out.println("==== " + titulo + " ====");
    }

    public static void mostrarSeparador() {
        System.out.println("----------------------------------------");
    }

    public static void mostrarMensagemErro(String msg) {
        System.out.println("❌ " + msg);
    }

    public static void mostrarMensagemSucesso(String msg) {
        System.out.println("✅ " + msg);
    }

    public static int lerInt(String msg) {
        System.out.print(msg);
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Digite um número válido: ");
        }
        int valor = scanner.nextInt();
        scanner.nextLine(); // consumir newline
        return valor;
    }

    public static String lerString(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }

    public static void pausar() {
        System.out.println("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }

    public static void fecharScanner() {
        scanner.close();
    }
}
