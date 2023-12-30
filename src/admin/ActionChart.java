package admin;

import database.DatabaseManagment;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public class ActionChart extends JFrame {
    private JComboBox<Integer> yearComboBox;
    private JButton drawButton;
    private ChartPanel chartPanel;

    public ActionChart() {
        setTitle("Biểu Đồ Số Lượng Người Hoạt Động Theo Năm");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Tạo JComboBox để chọn năm
        yearComboBox = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = currentYear; year >= currentYear - 10; year--) {
            yearComboBox.addItem(year);
        }

        // Tạo JButton để vẽ biểu đồ
        drawButton = new JButton("Vẽ Biểu Đồ");
        drawButton.addActionListener(e -> drawChart());

        // Tạo ChartPanel để chứa biểu đồ
        chartPanel = new ChartPanel(null);
        chartPanel.setPreferredSize(new Dimension(1000, 400));

        // Thêm các thành phần vào JFrame
        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Chọn Năm:"));
        controlPanel.add(yearComboBox);
        controlPanel.add(drawButton);

        add(controlPanel, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void drawChart() {
        int selectedYear = (int) yearComboBox.getSelectedItem();

        // Tạo dataset từ cơ sở dữ liệu
        CategoryDataset dataset = createDataset(selectedYear);

        // Tạo biểu đồ
        JFreeChart chart = ChartFactory.createBarChart(
                "Số Lượng Người Hoạt Động Theo Tháng (" + selectedYear + ")",
                "Tháng",
                "Số Lượng",
                dataset);

        NumberAxis rangeAxis = (NumberAxis) chart.getCategoryPlot().getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // Hiển thị biểu đồ trên ChartPanel
        chartPanel.setChart(chart);
    }

    private CategoryDataset createDataset(int year) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try {
            // Kết nối đến cơ sở dữ liệu
            Statement statement = DatabaseManagment.getInstance().getConnection().createStatement();

            // Tạo một dataset trống chứa tất cả các tháng
            for (int month = 1; month <= 12; month++) {
                dataset.addValue(0, "Người Hoạt Động", getMonthName(month));
            }
            // Truy vấn cơ sở dữ liệu để lấy số lượng người đăng ký mới theo tháng
            String query = "SELECT EXTRACT(MONTH FROM LH.LOGIN_TIME) AS month, COUNT(DISTINCT LH.USER_ID) AS login_count " +
                    "FROM USER_ACCOUNT UA INNER JOIN LOGIN_HISTORY LH ON LH.USER_ID = UA.ID " +
                    "WHERE EXTRACT(YEAR FROM LH.LOGIN_TIME) = " + year + " " +
                    "GROUP BY EXTRACT(MONTH FROM LH.LOGIN_TIME) " +
                    "ORDER BY EXTRACT(MONTH FROM LH.LOGIN_TIME)";
            ResultSet resultSet = statement.executeQuery(query);

            // Thêm dữ liệu vào dataset
            while (resultSet.next()) {
                int month = resultSet.getInt("month");
                int loginCount = resultSet.getInt("login_count");
                dataset.setValue(loginCount, "Người Hoạt Động", getMonthName(month));
            }

            // Đóng kết nối
            resultSet.close();
            statement.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataset;
    }

    private String getMonthName(int month) {
        String[] monthNames = {"", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
        return monthNames[month];
    }

}
