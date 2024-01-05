package admin;

import database.DatabaseManagment;
import datastructure.UserAccount;
import utils.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SpamReportManagement extends JFrame{

    static void fillSpamTable(JTable jtSpam, String username, String time, String sort) {

        Utils.clearTable(jtSpam);
        Connection conn = DatabaseManagment.getInstance().getConnection();
        DefaultTableModel tableModel = (DefaultTableModel) jtSpam.getModel();

        String SELECT_QUERY = "SELECT SR.SEND_ID AS SEND_ID, " +
                "UA.USERNAME AS SEND_USERNAME, " +
                "SR.REPORT_ID AS REPORT_ID, " +
                "UA2.USERNAME AS REPORT_USERNAME, " +
                "SR.REPORT_CONTENT REPORT_CONTENT, " +
                "SR.CREATED_AT AS CREATED_AT " +
                "FROM SPAM_REPORT SR, USER_ACCOUNT UA, USER_ACCOUNT UA2" +
                " WHERE SR.SEND_ID = UA.ID " +
                " AND SR.REPORT_ID = UA2.ID" ;
        if(!username.isEmpty()){
            SELECT_QUERY += " AND UA.USERNAME LIKE  '%" + username + "%' " ;
        }
        if(!time.isEmpty()){
            SELECT_QUERY +=  " AND SR.CREATED_AT = '" + time + "' ";
        }
        SELECT_QUERY += " ORDER BY " + sort;
        ResultSet rs = null;
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {
            rs = statment.executeQuery();

            while(rs.next()){
                int sendId = rs.getInt("SEND_ID");
                String sendUsername = rs.getString("SEND_USERNAME");
                int reportId = rs.getInt("REPORT_ID");
                String reportedUsername = rs.getString("REPORT_USERNAME");
                String reportContent = rs.getString("REPORT_CONTENT");
                String createdAt = rs.getString("CREATED_AT");
                String row[] = {String.valueOf(sendId), sendUsername,String.valueOf(reportId),reportedUsername, reportContent, createdAt };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    public SpamReportManagement(){
        this.setTitle("Danh sách báo cáo spam");
        this.setSize(800, 720);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        JPanel jpTable = new JPanel();
        jpTable.setBackground(Color.white);
        jpTable.setSize(800, 620);

        final String[][] col = {{"ID người gửi","Tên đăng nhập người gửi", "ID người bị báo cáo","Tên đăng nhập người bị báo cáo", "Nội dung báo cáo", "Ngày tạo" }};
        DefaultTableModel tableModel = new DefaultTableModel(col[0], 0);
        JTable jtSpam = new JTable(tableModel);
        fillSpamTable(jtSpam,"", "", "SR.CREATED_AT");

        JScrollPane jspSpam = new JScrollPane(jtSpam);
        jspSpam.setPreferredSize(new Dimension(750, 520));
        jpTable.add(jspSpam);

        JPanel jpSearchBar = new JPanel();
        jpSearchBar.setBackground(Color.white);
        jpSearchBar.setSize(800, 300);
        jpSearchBar.setLayout(new BoxLayout(jpSearchBar, BoxLayout.Y_AXIS));

        JPanel jpSearchUsername = new JPanel();
        JLabel jlSearchUsername = new JLabel("Tên đăng nhập:");
        JTextField jtfSearchUsername = new JTextField(30);
        jpSearchUsername.add(jlSearchUsername);
        jpSearchUsername.add(jtfSearchUsername);
        jpSearchBar.add(jpSearchUsername);

        JPanel jpSearchTime = new JPanel();
        JLabel jlSearchTime = new JLabel("Thời gian:   ");
        JTextField jtfSearchTime = new JTextField(30);
        jpSearchTime.add(jlSearchTime);
        jpSearchTime.add(jtfSearchTime);
        jpSearchBar.add(jpSearchTime);

        JPanel jpSearchButton = new JPanel();


        JPanel jpSortButton = new JPanel();
        String []sort = {"Thời gian","Tên đăng nhập"};
        JComboBox<String> jcSort = new JComboBox<>(sort);
        JLabel jlSort = new JLabel("Sắp xếp: ");
        jpSortButton.add(jlSort);
        jpSortButton.add(jcSort);
        jpSearchButton.add(jpSortButton);


        JButton jbSearch = new JButton("Lọc");
        jpSearchButton.add(jbSearch);
        jpSearchBar.add(jpSearchButton);


        this.add(jpSearchBar);
        this.add(jpTable);

        jbSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = jtfSearchUsername.getText();
                String time = jtfSearchTime.getText();
                int sortSlected = jcSort.getSelectedIndex();
                String sort = "";
                if(sortSlected == 0){
                    sort = "SR.CREATED_AT";
                }
                if(sortSlected == 1){
                    sort = "UA.USERNAME";
                }
                fillSpamTable(jtSpam, username, time, sort);
            }
        });

        JPanel jpLockUser = new JPanel();
        JButton jbLockUser = new JButton("Khóa người dùng");
        jpLockUser.add(jbLockUser);
        jbLockUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String Id = JOptionPane.showInputDialog(SpamReportManagement.this, "Nhập ID:", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                boolean result = DatabaseManagment.getInstance().checkAccount(Integer.parseInt(Id));
                if(result){
                    DatabaseManagment.getInstance().setLockUserAccount(Integer.parseInt(Id), true);
                    JOptionPane.showMessageDialog(SpamReportManagement.this, "Khóa người dùng thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(SpamReportManagement.this, "Không tìm thấy người dùng.", "Thông báo", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton jbUnlockUser = new JButton("Mở khóa người dùng");
        jpLockUser.add(jbUnlockUser);
        jbUnlockUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String Id = JOptionPane.showInputDialog(SpamReportManagement.this, "Nhập ID:", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                boolean result = DatabaseManagment.getInstance().checkAccount(Integer.parseInt(Id));
                if(result){
                    DatabaseManagment.getInstance().setLockUserAccount(Integer.parseInt(Id), false);
                    JOptionPane.showMessageDialog(SpamReportManagement.this, "Mở khóa người dùng thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(SpamReportManagement.this, "Không tìm thấy người dùng.", "Thông báo", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        this.add(jpLockUser);

    }
//    public static void main(String [] args){
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new SpamReportManagement().setVisible(true);
//            }
//        });
//    }
}
