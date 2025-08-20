package com.atm;



import java.util.Scanner;

public class ATMOperations {
    private String userId;
    private BankService service;
    private Scanner sc;

    public ATMOperations(String userId) {
        this.userId = userId;
        this.service = new BankService();
        this.sc = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\n------ ATM Menu -------");
            System.out.println("1. Transaction History");
            System.out.println("2. Balance Inquiry");
            System.out.println("3. Withdraw");
            System.out.println("4. Deposit");
            System.out.println("5. Transfer");
            System.out.println("6. Quit");
            System.out.print("Enter choice: ");
            int ch = sc.nextInt();
            switch (ch) {
                case 1:
                    service.printTransactionHistory(userId);
                    break;
                case 2:
                    System.out.println("Current Balance: ₹" + service.getBalance(userId));
                    break;
                case 3:
                    withdraw();
                    break;
                case 4:
                    deposit();
                    break;
                case 5:
                    transfer();
                    break;
                case 6:
                    System.out.println("✅ Goodbye!");
                    return;
                default:
                    System.out.println("❌ Invalid choice!");
            }

        }
    }

    private void withdraw() {
        System.out.print("Enter amount to withdraw: ₹");
        double amt = sc.nextDouble();
        if (service.withdraw(userId, amt))
            System.out.println("✅ Withdraw successful!");
    }

    private void deposit() {
        System.out.print("Enter amount to deposit: ₹");
        double amt = sc.nextDouble();
        if (service.deposit(userId, amt))
            System.out.println(" Deposit successful!");
    }

    private void transfer() {
        System.out.print("Enter receiver User ID: ");
        String rid = sc.next();
        if (rid.equals(userId)) {
            System.out.println(" Cannot transfer to self.");
            return;
        }
        System.out.print("Enter amount: ₹");
        double amt = sc.nextDouble();
        if (service.transfer(userId, rid, amt))
            System.out.println(" Transfer successful!");
    }
}
