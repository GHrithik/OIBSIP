package com.reservationsystem;

import javax.swing.*;
import java.sql.*;


public class LoginForm extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;

    public LoginForm() {
        setTitle("Train Reservation - Login");
        setSize(320, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(20, 25, 80, 25);
        panel.add(lblUser);

        txtUser = new JTextField();
        txtUser.setBounds(110, 25, 160, 25);
        panel.add(txtUser);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(20, 65, 80, 25);
        panel.add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setBounds(110, 65, 160, 25);
        panel.add(txtPass);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(110, 110, 80, 25);
        panel.add(btnLogin);

        // Handle login action
        btnLogin.addActionListener(e -> authenticateUser());

        add(panel);
    }

    private void authenticateUser() {
        String username = txtUser.getText().trim();
        String password = String.valueOf(txtPass.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.");
            return;
        }

        try (Connection conn = DBConnection.connect()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Unable to connect to the database.");
                return;
            }

            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM users WHERE username=? AND password=?"
            );
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Welcome " + username + "!");
                new ReservationForm(username).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new LoginForm().setVisible(true);
    }
}


