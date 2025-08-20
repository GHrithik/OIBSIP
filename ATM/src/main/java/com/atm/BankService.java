package com.atm;

import java.sql.*;

public class BankService {

    public boolean authenticateUser(String userId, String pin) {
        String sql = "SELECT * FROM users WHERE user_id=? AND pin=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, pin);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getBalance(String userId) {
        String sql = "SELECT balance FROM users WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("balance");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void insertTransaction(String userId, String type, double amount) {
        String sql = "INSERT INTO transactions(user_id, type, amount) VALUES (?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deposit(String userId, double amount) {
        String sql = "UPDATE users SET balance = balance + ? WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setString(2, userId);
            if (ps.executeUpdate() > 0) {
                insertTransaction(userId, "Deposit", amount);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean withdraw(String userId, double amount) {
        double balance = getBalance(userId);
        if (amount > balance) {
            System.out.println(" Insufficient balance!");
            return false;
        }
        String sql = "UPDATE users SET balance = balance - ? WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setString(2, userId);
            if (ps.executeUpdate() > 0) {
                insertTransaction(userId, "Withdraw", amount);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean transfer(String senderId, String receiverId, double amount) {
        String checkUser = "SELECT balance FROM users WHERE user_id=?";
        String debit = "UPDATE users SET balance=balance-? WHERE user_id=?";
        String credit = "UPDATE users SET balance=balance+? WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // check sender balance
            try (PreparedStatement ps = conn.prepareStatement(checkUser)) {
                ps.setString(1, senderId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next() || rs.getDouble("balance") < amount) {
                    System.out.println(" Insufficient balance.");
                    conn.rollback();
                    return false;
                }
            }

            // debit from sender
            try (PreparedStatement ps = conn.prepareStatement(debit)) {
                ps.setDouble(1, amount);
                ps.setString(2, senderId);
                ps.executeUpdate();
            }

            // credit receiver
            try (PreparedStatement ps = conn.prepareStatement(credit)) {
                ps.setDouble(1, amount);
                ps.setString(2, receiverId);
                ps.executeUpdate();
            }

            // insert transactions
            insertTransaction(senderId, "Transfer to " + receiverId, amount);
            insertTransaction(receiverId, "Transfer from " + senderId, amount);

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void printTransactionHistory(String userId) {
        String sql = "SELECT datetime, type, amount FROM transactions WHERE user_id=? ORDER BY datetime DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("datetime") + " | " +
                        rs.getString("type") + " | ₹" + rs.getDouble("amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
