import database.DatabaseHelper;
import gui.LoginFrame;
import model.User;
import service.Authorization;
import service.MenuService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--cli")) {
            DatabaseHelper.init();
            Scanner scanner = new Scanner(System.in);

            System.out.println("==========================================");
            System.out.println("   CAR RENTAL SYSTEM                      ");
            System.out.println("==========================================");

            String auth = null;
            while (true) {
                System.out.println("\n[1] LOG IN");
                System.out.println("[2] SIGN IN (New Account)");
                System.out.print("\nChoose an option (1 or 2): ");
                String input = scanner.nextLine().trim();
                if (input.equals("1") || input.equalsIgnoreCase("LOG IN")) {
                    auth = "LOG IN";
                    break;
                } else if (input.equals("2") || input.equalsIgnoreCase("SIGN IN")) {
                    auth = "SIGN IN";
                    break;
                } else {
                    System.out.println(">> Invalid input. Please enter 1 or 2.");
                }
            }

            if (auth.equalsIgnoreCase("SIGN IN")) {
                System.out.println("\n--- REGISTRATION ---");
                boolean registered = false;
                while (!registered) {
                    System.out.print("Create Username: ");
                    String un = scanner.nextLine().trim();
                    if (un.isEmpty()) {
                        System.out.println(">> Error: Username cannot be empty!");
                        continue;
                    }
                    System.out.print("Create Password: ");
                    String p = scanner.nextLine().trim();
                    if (p.isEmpty()) {
                        System.out.println(">> Error: Password cannot be empty!");
                        continue;
                    }
                    registered = Authorization.register(un, p, "CUSTOMER");
                    if (registered) {
                        System.out.println(">> Registration successful! Please log in.");
                    }
                }
            }

            System.out.println("\n--- AUTHORIZATION ---");
            User user = null;
            while (user == null) {
                System.out.print("Username: ");
                String username = scanner.nextLine().trim();
                System.out.print("Password: ");
                String password = scanner.nextLine().trim();
                user = Authorization.login(username, password);
                if (user == null) {
                    System.out.println(">> Invalid credentials. Try again.");
                }
            }

            System.out.print("Accessing database...");
            try { Thread.sleep(800); } catch (InterruptedException e) {}
            System.out.println(" Access granted!");

            if (user.getRole().equalsIgnoreCase("CUSTOMER")) {
                MenuService.CustomerMenu(user, scanner);
            } else if (user.getRole().equalsIgnoreCase("ADMIN")) {
                MenuService.AdminMenu(user, scanner);
            }
        } else {
            DatabaseHelper.init();
            javax.swing.SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}


