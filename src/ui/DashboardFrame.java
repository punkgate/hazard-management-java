package ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import db.DatabaseConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class DashboardFrame extends JFrame {
    private String username;
    private JTable accidentsTable;
    private JScrollPane tableScrollPane;
    private final Color criticalSeverityColor = new Color(139, 0, 0); // dark red
    private final Color highSeverityColor = new Color(255, 140, 0); // light orange
    private final Color mediumSeverityColor = new Color(255, 215, 0); // yellow
    private final Color lowSeverityColor = new Color(76, 175, 80); // green

    private Color getSeverityColor(String severity) {
        if (severity == null) return Color.WHITE;
        switch (severity.toLowerCase()) {
            case "critical": return criticalSeverityColor;
            case "high": return highSeverityColor;
            case "moderate":
            case "medium": return mediumSeverityColor;
            case "low": return lowSeverityColor;
            default: return Color.WHITE;
        }
    }

    public DashboardFrame(String username) {
        this.username = username;
        setTitle("User Dashboard - Hazard Tracker");
        setSize(1200, 700);  // Increased window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel welcome = new JLabel("Welcome, " + username, SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(welcome, BorderLayout.NORTH);

        // Create main panel with table and charts
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Charts panel on the left
        JPanel chartsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        chartsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Hazard Analytics"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Create and add charts with minimum size
        ChartPanel pieChart = createSeverityPieChart();
        ChartPanel barChart = createMonthlyBarChart();
        
        // Set preferred size for charts
        Dimension chartSize = new Dimension(400, 300);
        pieChart.setPreferredSize(chartSize);
        barChart.setPreferredSize(chartSize);
        
        // Add charts to panel
        chartsPanel.add(pieChart);
        chartsPanel.add(barChart);
        
        mainPanel.add(chartsPanel);
        
        // Table panel on the right (will be added in loadAccidents())
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Hazard Records"));
        mainPanel.add(tablePanel);
        
        add(mainPanel, BorderLayout.CENTER);

        JButton reportBtn = new JButton("Report Hazard");
        reportBtn.addActionListener(e -> reportAccident());
        add(reportBtn, BorderLayout.SOUTH);

        loadAccidents();
        setVisible(true);
    }

    private void loadAccidents() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, location, severity, description, date, status FROM accidents WHERE reported_by=? ORDER BY date DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (accidentsTable == null) {
                accidentsTable = new JTable(AdminDashboardFrame.buildTableModel(rs)) {
                    @Override
                    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                        Component c = super.prepareRenderer(renderer, row, column);
                        if (!isRowSelected(row)) {
                            String severity = getValueAt(row, getColumn("severity").getModelIndex()).toString();
                            c.setBackground(getSeverityColor(severity));
                        }
                        return c;
                    }
                };
                tableScrollPane = new JScrollPane(accidentsTable);
                Container parent = getContentPane();
                Component[] components = parent.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        JPanel mainPanel = (JPanel) comp;
                        for (Component innerComp : mainPanel.getComponents()) {
                            if (innerComp instanceof JPanel && ((JPanel) innerComp).getBorder() != null &&
                                ((JPanel) innerComp).getBorder().toString().contains("Hazard Records")) {
                                ((JPanel) innerComp).add(tableScrollPane, BorderLayout.CENTER);
                                break;
                            }
                        }
                    }
                }
            } else {
                accidentsTable.setModel(AdminDashboardFrame.buildTableModel(rs));
            }
            
            // Set column widths and renderers
            accidentsTable.getColumnModel().getColumn(0).setMaxWidth(50); // ID column
            accidentsTable.setRowHeight(25);
            accidentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            accidentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading accidents: " + e.getMessage());
        }
    }

    private void reportAccident() {
    JDialog dialog = new JDialog(this, "Report Hazard", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Location
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Location:"), gbc);
        JTextField locationField = new JTextField(20);
        gbc.gridx = 1;
        form.add(locationField, gbc);

        // Severity
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Severity:"), gbc);
        String[] severities = {"Low", "Moderate", "High", "Critical"};
        JComboBox<String> severityBox = new JComboBox<>(severities);
        gbc.gridx = 1;
        form.add(severityBox, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Description:"), gbc);
        JTextArea descArea = new JTextArea(4, 20);
        descArea.setLineWrap(true);
        gbc.gridx = 1;
        form.add(new JScrollPane(descArea), gbc);

        // Submit button
        JButton submitBtn = new JButton("Submit Report");
        submitBtn.addActionListener(e -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (locationField.getText().trim().isEmpty() || descArea.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all fields!");
                    return;
                }

                String query = "INSERT INTO accidents (location, severity, description, reported_by, date, status) VALUES (?, ?, ?, ?, NOW(), 'Pending')";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, locationField.getText().trim());
                stmt.setString(2, (String) severityBox.getSelectedItem());
                stmt.setString(3, descArea.getText().trim());
                stmt.setString(4, username);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "Hazard reported successfully!");
                dialog.dispose();
                loadAccidents(); // Refresh the table
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error reporting accident: " + ex.getMessage());
            }
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(submitBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private ChartPanel createSeverityPieChart() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT severity, COUNT(*) as count FROM accidents GROUP BY severity")) {
            
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                String severity = rs.getString("severity");
                int count = rs.getInt("count");
                System.out.println("Adding to pie chart: " + severity + " = " + count);
                dataset.setValue(severity, count);
            }
            
            if (!hasData) {
                // Add dummy data if no records exist
                dataset.setValue("No Data", 1);
                System.out.println("No hazard data found in database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Add error state to chart
            dataset.setValue("Error Loading Data", 1);
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
            "Hazards by Severity",
            dataset,
            true,
            true,
            false
        );

        return new ChartPanel(pieChart);
    }

    private ChartPanel createMonthlyBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT MONTH(date) as month, severity, COUNT(*) as count " +
                "FROM accidents " +
                "WHERE date >= DATE_SUB(NOW(), INTERVAL 6 MONTH) " +
                "GROUP BY MONTH(date), severity " +
                "ORDER BY MONTH(date)")) {
            
            while (rs.next()) {
                String month = getMonthName(rs.getInt("month"));
                dataset.addValue(rs.getDouble("count"), rs.getString("severity"), month);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFreeChart barChart = ChartFactory.createBarChart(
            "Monthly Hazards by Severity",
            "Month",
            "Number of Hazards",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        return new ChartPanel(barChart);
    }

    private String getMonthName(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month - 1];
    }
}
