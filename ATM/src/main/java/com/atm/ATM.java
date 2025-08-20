package com.atm;



import java.util.Scanner;

public class ATM {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BankService service = new BankService();

        System.out.println(" ------ Welcome to ATM ------ ");
        System.out.print("Enter User ID: ");
        String uid = sc.nextLine();
        System.out.print("Enter PIN: ");
        String pin = sc.nextLine();

        if (service.authenticateUser(uid, pin)) {
            System.out.println(" Login Successful!");
            new ATMOperations(uid).start();
        } else {
            System.out.println(" Invalid credentials!");
        }
    }
}


