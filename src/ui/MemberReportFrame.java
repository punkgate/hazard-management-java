package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import db.DatabaseConnection;
import model.User;

public class MemberReportFrame extends JFrame {
    private User user;

    public MemberReportFrame(User user, String mode) {
        this.user = user;

    setTitle(mode.equals("add") ? "Add Hazard Report" : "My Hazard Reports");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        if (mode.equals("add")) showAddReportForm();
        else showMyReports();

        setVisible(true);
    }

    private void showAddReportForm() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JTextField locationField = new JTextField();
        JTextArea descArea = new JTextArea(5, 20);

        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descArea));

    JButton submit = new JButton("Submit Hazard Report");
        panel.add(new JLabel());
        panel.add(submit);

        add(panel);

        submit.addActionListener(e -> {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO accidents (user_id, location, description) VALUES (?, ?, ?)")) {
                stmt.setInt(1, user.getId());
                stmt.setString(2, locationField.getText());
                stmt.setString(3, descArea.getText());
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Hazard report added successfully!");
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding report!");
            }
        });
    }

    private void showMyReports() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, location, description, date FROM accidents WHERE user_id = ?")) {
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();

            JTable table = new JTable(AdminDashboardFrame.buildTableModel(rs));
            add(new JScrollPane(table), BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading reports!");
        }
    }
}
