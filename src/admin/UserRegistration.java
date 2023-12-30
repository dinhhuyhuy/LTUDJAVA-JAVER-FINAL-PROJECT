package admin;

import database.DatabaseManagment;
import datastructure.UserAccount;
import utils.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class UserRegistration extends JFrame {
    static void fillRegisterTable(JTable jtRegister, String startDate, String endDate,
                                  String fullname, String sort, String by) {
        Utils.clearTable(jtRegister);
        DatabaseManagment db = DatabaseManagment.getInstance();
        ArrayList<UserAccount> registers = db.getAccountRegistration(startDate, endDate,
                fullname, sort, by);
        DefaultTableModel tableModel = (DefaultTableModel) jtRegister.getModel();
        for (UserAccount register : registers) {
            String id = String.valueOf(register.getID());
            String username = register.getUsername();
            String name = register.getFullname();
            String create_at = register.getCreatedAt();

            String row[] = { id, username, name, create_at};
            tableModel.addRow(row);
        }
    }

    UserRegistration(){
        this.setTitle("Đăng ký mới");
        this.setSize(800, 720);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        //Tạo bảng đăng k mới
        JPanel jpTable = new JPanel();
        jpTable.setBackground(Color.white);
        jpTable.setSize(800, 420);

        final String[][] col = {{"ID", "Tên tài khoản", "Họ và tên", "Ngày đăng ký"}};
        DefaultTableModel tableModel = new DefaultTableModel(col[0], 0);
        JTable jtRegister = new JTable(tableModel);
        fillRegisterTable(jtRegister, "", "", "", "CREATED_AT", "DESC");

        JScrollPane jspMember = new JScrollPane(jtRegister);
        jspMember.setPreferredSize(new Dimension(750, 420));
        jpTable.add(jspMember);

        //Chọn ngày đăng ký
        JPanel jpDate = new JPanel();
        jpDate.setBackground(Color.white);
        jpDate.setSize(750, 100);

        JLabel jlStart = new JLabel("Ngày bắt đầu: ");
        JTextField jtfStart = new JTextField(12);
        jpDate.add(jlStart);
        jpDate.add(jtfStart);

        JLabel jlEnd = new JLabel("Ngày kết thúc: ");
        JTextField jtfEnd = new JTextField(12);
        jpDate.add(jlEnd);
        jpDate.add(jtfEnd);

        //Thông báo
        JPanel jpMessage = new JPanel();
        jpMessage.setBackground(Color.white);
        jpMessage.setSize(750, 100);

        JLabel jlMessage = new JLabel("Thông báo: Nhập ngày theo cú pháp yyyy-mm-dd");
        jpMessage.add(jlMessage);

        //Tạo các filter
        JPanel jpSortBar = new JPanel();
        jpSortBar.setBackground(Color.white);
        jpSortBar.setSize(750, 100);

        JLabel jlSearch = new JLabel("Tìm kiếm: ");
        JTextField jtfSearch = new JTextField(30);
        jpSortBar.add(jlSearch);
        jpSortBar.add(jtfSearch);

        String []sort = {"Ngày khởi tạo", "Tên người dùng"};
        JComboBox<String> jcSort = new JComboBox<>(sort);
        jpSortBar.add(jcSort);

        String []by = {"Giảm dần", "Tăng dần"};
        JComboBox<String> jcSortBy = new JComboBox<>(by);
        jpSortBar.add(jcSortBy);

        JButton jbSearch = new JButton("Tìm và sắp xếp");
        jbSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String startDate = (checkDate(jtfStart.getText())) ? jtfStart.getText() : "";
                String endDate = (checkDate(jtfEnd.getText())) ? jtfEnd.getText() : "";
                String fullname = jtfSearch.getText();
                int temp = jcSort.getSelectedIndex();
                String sort = (temp == 0) ? "CREATED_AT" : "FULLNAME";
                temp = jcSortBy.getSelectedIndex();
                String by = (temp == 0) ? "DESC" : "ASC";
                fillRegisterTable(jtRegister, startDate, endDate, fullname, sort, by);
            }
        });
        jpSortBar.add(jbSearch);

        this.add(jpSortBar);
        this.add(jpDate);
        this.add(jpMessage);
        this.add(jpTable);
    }
}
