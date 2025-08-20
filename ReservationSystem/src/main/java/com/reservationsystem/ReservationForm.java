package com.reservationsystem;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ReservationForm extends JFrame {
    private JTextField trainNoField, trainNameField, fromField, toField, dateField;
    private JComboBox<String> cmbClass;
    private final String loggedInUser;

    public ReservationForm(String username) {
        this.loggedInUser = username;

        setTitle("Ticket Booking - Logged in as: " + username);
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(null);

        JLabel lblNo = new JLabel("Train No:");
        lblNo.setBounds(20, 20, 80, 25);
        panel.add(lblNo);

        trainNoField = new JTextField();
        trainNoField.setBounds(120, 20, 160, 25);
        panel.add(trainNoField);

        JLabel lblName = new JLabel("Train Name:");
        lblName.setBounds(20, 60, 80, 25);
        panel.add(lblName);

        trainNameField = new JTextField();
        trainNameField.setBounds(120, 60, 160, 25);
        panel.add(trainNameField);

        JLabel lblClass = new JLabel("Class:");
        lblClass.setBounds(20, 100, 80, 25);
        panel.add(lblClass);

        cmbClass = new JComboBox<>(new String[] { "Sleeper", "AC", "Chair Car" });
        cmbClass.setBounds(120, 100, 160, 25);
        panel.add(cmbClass);

        JLabel lblDate = new JLabel("DOJ:");
        lblDate.setBounds(20, 140, 100, 25);
        panel.add(lblDate);

        dateField = new JTextField("YYYY-MM-DD");
        dateField.setBounds(120, 140, 160, 25);
        panel.add(dateField);

        JLabel lblFrom = new JLabel("From:");
        lblFrom.setBounds(20, 180, 80, 25);
        panel.add(lblFrom);

        fromField = new JTextField();
        fromField.setBounds(120, 180, 160, 25);
        panel.add(fromField);

        JLabel lblTo = new JLabel("To:");
        lblTo.setBounds(20, 220, 80, 25);
        panel.add(lblTo);

        toField = new JTextField();
        toField.setBounds(120, 220, 160, 25);
        panel.add(toField);

        JButton btnBook = new JButton("Book Ticket");
        btnBook.setBounds(20, 260, 120, 25);
        panel.add(btnBook);

        JButton btnCancel = new JButton("Cancel Ticket");
        btnCancel.setBounds(150, 260, 120, 25);
        panel.add(btnCancel);

        JButton btnShowBookings = new JButton("Show My Bookings");
        btnShowBookings.setBounds(280, 260, 160, 25);
        panel.add(btnShowBookings);

        btnBook.addActionListener(e -> bookTicket());
        btnCancel.addActionListener(e -> new CancellationForm().setVisible(true));
        btnShowBookings.addActionListener(e -> new MyBookingsForm(loggedInUser).setVisible(true));

        add(panel);
    }

    private void bookTicket() {
        String dateInput = dateField.getText().trim();
        LocalDate doj;

        try {
            doj = LocalDate.parse(dateInput);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Please enter date in YYYY-MM-DD format.");
            return;
        }

        try (Connection conn = DBConnection.connect()) {
            String sql = "INSERT INTO bookings (username, train_no, train_name, class_type, doj, from_station, to_station, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, loggedInUser);
            stmt.setString(2, trainNoField.getText().trim());
            stmt.setString(3, trainNameField.getText().trim());
            stmt.setString(4, cmbClass.getSelectedItem().toString());
            stmt.setObject(5, doj);
            stmt.setString(6, fromField.getText().trim());
            stmt.setString(7, toField.getText().trim());
            stmt.setString(8, "Booked");

            int affected = stmt.executeUpdate();

            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int pnr = rs.getInt(1);
                    JOptionPane.showMessageDialog(this,
                            "✅ Ticket booked successfully!\nYour PNR (Booking ID) is: " + pnr);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error booking ticket: " + ex.getMessage());
        }
    }
}
