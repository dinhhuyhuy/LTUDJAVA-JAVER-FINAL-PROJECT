package admin;

import database.DatabaseManagment;
import datastructure.LoginHistory;
import datastructure.UserAccount;
import utils.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class UserLoginManagement extends JFrame {
    static void fillLoginTable(JTable jtLogin) {
        Utils.clearTable(jtLogin);
        DatabaseManagment db = DatabaseManagment.getInstance();
        ArrayList<LoginHistory> logins = db.getAllLoginHistory("LOGIN_TIME", "DESC");
        DefaultTableModel tableModel = (DefaultTableModel) jtLogin.getModel();
        for (LoginHistory login : logins) {
            String id = String.valueOf(login.getUserID());
            String username = login.getUserName();
            String fullname = login.getFullName();
            String date_login = login.getLoginTime();

            String row[] = { id, username, fullname, date_login };
            tableModel.addRow(row);
        }
    }
    public UserLoginManagement(){
        this.setTitle("Danh sách đăng nhập");
        this.setSize(800, 720);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        JPanel jpTable = new JPanel();
        jpTable.setBackground(Color.white);
        jpTable.setSize(800, 620);

        final String[][] col = {{"ID người dùng", "Tên đăng nhập", "Họ tên", "Thời gian đăng nhập"}};
        DefaultTableModel tableModel = new DefaultTableModel(col[0], 0);
        JTable jtLogin = new JTable(tableModel);
        fillLoginTable(jtLogin);

        JScrollPane jspLogin = new JScrollPane(jtLogin);
        jspLogin.setPreferredSize(new Dimension(750, 520));
        jpTable.add(jspLogin);

        this.add(jpTable);
    }

}
