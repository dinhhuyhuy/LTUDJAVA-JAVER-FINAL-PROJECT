package admin;


import database.DatabaseManagment;
import utils.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserFriendList extends JFrame {

    private JTable userFriendTable;

    public UserFriendList() {
        setTitle("Danh Sách Người Dùng và Số Bạn Bè");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        JPanel jpFilter = new JPanel();

        JPanel jpSearchUsername = new JPanel();
        JLabel jlSearchUsername = new JLabel("Tên đăng nhập:");
        JTextField jtfSearchUsername = new JTextField(30);
        jpSearchUsername.add(jlSearchUsername);
        jpSearchUsername.add(jtfSearchUsername);
        jpFilter.add(jpSearchUsername);


        add(jpFilter);

        JPanel jpFilterNumberFriend = new JPanel();
        JLabel jlFilterNumberFriend = new JLabel("Số lượng:");
        JTextField jtfFilterNumberFriend = new JTextField(20);
        jpFilterNumberFriend.add(jlFilterNumberFriend);
        jpFilterNumberFriend.add(jtfFilterNumberFriend);
        String []sortFriend = {"Bằng", "Bé hơn", "Lớn hơn"};
        JComboBox<String> jcSortFriend = new JComboBox<>(sortFriend);
        JLabel jlSortFriend = new JLabel("Chọn loại: ");
        jpFilterNumberFriend.add(jlSortFriend);
        jpFilterNumberFriend.add(jcSortFriend);


        add(jpFilterNumberFriend);

        JPanel jpSortButton = new JPanel();
        String []sort = {"ID","Tên đăng nhập","Ngày tạo"};
        JComboBox<String> jcSort = new JComboBox<>(sort);
        JLabel jlSort = new JLabel("Sắp xếp: ");
        jpSortButton.add(jlSort);
        jpSortButton.add(jcSort);

        JButton jbSearchUsername = new JButton("Lọc và sắp xếp");
        jpSortButton.add(jbSearchUsername);

        add(jpSortButton);

        jbSearchUsername.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = jtfSearchUsername.getText();
                String quantity = jtfFilterNumberFriend.getText();
                int selectedType = jcSortFriend.getSelectedIndex();
                String type = "";
                if(selectedType == 0){
                    type = "=";
                }
                if(selectedType == 1){
                    type = "<";
                }
                if(selectedType == 2){
                    type = ">";
                }

                int selectedSort = jcSort.getSelectedIndex();
                String sort = "";
                if(selectedSort == 0){
                    sort = "ID";
                }
                if(selectedSort == 1){
                    sort = "USERNAME";
                }
                if(selectedSort == 2){
                    sort = "CREATED_AT";
                }

                displayUserFriendList(username,sort, quantity, type);

            }
        });

        // Tạo JTable để hiển thị danh sách người dùng và số lượng bạn bè
        userFriendTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(userFriendTable);

        // Thêm JTable vào JFrame
        add(scrollPane, BorderLayout.CENTER);

        // Hiển thị danh sách người dùng và số lượng bạn bè
        displayUserFriendList("","ID", "", "");

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void displayUserFriendList(String username, String sort, String quantity, String type) {
        try {
            // Kết nối đến cơ sở dữ liệu
            Connection connection = DatabaseManagment.getInstance().getConnection();

            String query = "SELECT \n" +
                    "    ua.ID, \n" +
                    "    ua.USERNAME, \n" +
                    "    ua.FULLNAME, \n" +
                    "    ua.CREATED_AT, \n" +
                    "    COUNT(DISTINCT uf1.FRIEND_ID) AS directFriends, \n" +
                    "    COUNT(DISTINCT uf2.FRIEND_ID) AS totalFriends\n" +
                    "FROM \n" +
                    "    USER_ACCOUNT ua\n" +
                    "LEFT JOIN \n" +
                    "    USER_FRIEND uf1 ON ua.ID = uf1.ID\n" +
                    "LEFT JOIN \n" +
                    "    USER_FRIEND uf2 ON uf1.FRIEND_ID = uf2.ID\n";
//
            boolean check = false;
            if(!username.isEmpty()){
                if(check){
                    query += " AND ";
                }
                else {
                    query += " WHERE ";
                }
                query += " ua.USERNAME LIKE '%" + username +"%' ";
                check = true;
            }

            query += " GROUP BY ua.ID ";

            if(!quantity.isEmpty()){
                query += " HAVING COUNT(DISTINCT uf1.FRIEND_ID) " + type + " " + quantity;
            }

            if(!sort.isEmpty()){
                query += " ORDER BY ua." + sort;
            }

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();


            Utils.clearTable(userFriendTable);
            // Tạo DefaultTableModel để chứa dữ liệu của JTable
            String[] columnNames = {"ID", "Tên đăng nhập","Họ tên","Thời gian tạo", "Số Bạn Bè Trực Tiếp", "Tổng Số Bạn Bè"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);


            // Thêm dữ liệu từ ResultSet vào DefaultTableModel
            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getInt("ID"),
                        resultSet.getString("USERNAME"),
                        resultSet.getString("FULLNAME"),
                        resultSet.getString("CREATED_AT"),
                        resultSet.getInt("directFriends"),
                        resultSet.getInt("totalFriends")
                };
                tableModel.addRow(rowData);
            }

            // Đặt DefaultTableModel cho JTable
            userFriendTable.setModel(tableModel);

            // Đóng kết nối
            resultSet.close();
            preparedStatement.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
