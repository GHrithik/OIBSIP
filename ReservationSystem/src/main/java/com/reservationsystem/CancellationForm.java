package com.reservationsystem;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;


public class CancellationForm extends JFrame {

    private JTextField bookingIdField;

    public CancellationForm() {
        setTitle("Cancel Ticket");
        setSize(350, 180);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(null);

        JLabel lblBookingId = new JLabel("Enter Booking ID (PNR):");
        lblBookingId.setBounds(20, 20, 180, 25);
        panel.add(lblBookingId);

        bookingIdField = new JTextField();
        bookingIdField.setBounds(200, 20, 100, 25);
        panel.add(bookingIdField);

        JButton btnCancel = new JButton("Confirm Cancel");
        btnCancel.setBounds(100, 70, 140, 30);
        btnCancel.addActionListener(e -> cancelTicket());
        panel.add(btnCancel);

        add(panel);
    }

    private void cancelTicket() {
        try (Connection conn = DBConnection.connect()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed!");
                return;
            }

            int bookingId;
            try {
                bookingId = Integer.parseInt(bookingIdField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric Booking ID.");
                return;
            }

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE bookings SET status = 'Cancelled' WHERE booking_id = ?"
            );
            stmt.setInt(1, bookingId);

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this,
                        "✅ Ticket with Booking ID " + bookingId + " cancelled successfully!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "⚠ No booking found for Booking ID: " + bookingId);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cancelling ticket: " + ex.getMessage());
        }
    }
}

