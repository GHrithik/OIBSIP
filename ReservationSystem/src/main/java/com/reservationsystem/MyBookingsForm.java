package com.reservationsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MyBookingsForm extends JFrame {
    private final String username;
    private JTable bookingsTable;

    public MyBookingsForm(String username) {
        this.username = username;
        setTitle("My Bookings - " + username);
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        bookingsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        add(scrollPane);

        loadBookings();
    }

    private void loadBookings() {
        String[] columnNames = {
                "Booking ID (PNR)", "Train No", "Train Name", "Class",
                "Date of Journey", "From", "To", "Status"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        bookingsTable.setModel(model);

        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT booking_id, train_no, train_name, class_type, doj, from_station, to_station, status " +
                    "FROM bookings WHERE username = ? ORDER BY doj DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("booking_id"),
                        rs.getString("train_no"),
                        rs.getString("train_name"),
                        rs.getString("class_type"),
                        rs.getDate("doj"),
                        rs.getString("from_station"),
                        rs.getString("to_station"),
                        rs.getString("status")
                };
                model.addRow(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + ex.getMessage());
        }
    }
}
