package admin;

import database.DatabaseManagment;
import datastructure.Action;
import datastructure.UserAccount;
import utils.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ActionMagament extends JFrame {
    static void fillActionTable(JTable jtAction, String login, String userChat, String groupChat,
                                  String fullname, int compare, String sort, String by) {
        Utils.clearTable(jtAction);
        DatabaseManagment db = DatabaseManagment.getInstance();
        ArrayList<Action> actions = db.getUserAction(fullname, login, userChat,
                groupChat, compare, sort, by);
        DefaultTableModel tableModel = (DefaultTableModel) jtAction.getModel();
        for (Action action : actions) {
            String id = String.valueOf(action.getID());
            String name = action.getFullName();
            String logins = String.valueOf(action.getLogin());
            String users = String.valueOf(action.getUserChat());
            String groups = String.valueOf(action.getGroupChat());
            String row[] = { id, name, logins, users, groups};
            tableModel.addRow(row);
        }
    }

    ActionMagament(){
         this.setTitle("Danh sách hoạt động");
         this.setSize(800, 720);
         this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
         this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

         //Tạo bảng đăng k mới
         JPanel jpTable = new JPanel();
         jpTable.setBackground(Color.white);
         jpTable.setSize(800, 520);

         final String[][] col = {{"ID", "Họ và tên", "Số lần đăng nhập", "Số người chat", "Số nhóm chat"}};
         DefaultTableModel tableModel = new DefaultTableModel(col[0], 0);
         JTable jtAction = new JTable(tableModel);
        fillActionTable(jtAction, "", "", "", "", 0, "ID", "ASC");


         JScrollPane jspMember = new JScrollPane(jtAction);
         jspMember.setPreferredSize(new Dimension(750, 520));
         jpTable.add(jspMember);

//         //Chọn ngày đăng ký
//         JPanel jpDate = new JPanel();
//         jpDate.setBackground(Color.white);
//         jpDate.setSize(750, 50);
//
//         JLabel jlStart = new JLabel("Ngày bắt đầu: ");
//         JTextField jtfStart = new JTextField(12);
//         jpDate.add(jlStart);
//         jpDate.add(jtfStart);
//
//         JLabel jlEnd = new JLabel("Ngày kết thúc: ");
//         JTextField jtfEnd = new JTextField(12);
//         jpDate.add(jlEnd);
//         jpDate.add(jtfEnd);
//
//         //Thông báo
//         JPanel jpMessage = new JPanel();
//         jpMessage.setBackground(Color.white);
//         jpMessage.setSize(750, 50);
//
//         JLabel jlMessage = new JLabel("Thông báo: Nhập ngày theo cú pháp yyyy-mm-dd");
//         jpMessage.add(jlMessage);

         //So sánh
         JPanel jpCompare = new JPanel();
         jpCompare.setBackground(Color.white);
         jpCompare.setSize(750, 50);

         JLabel jlLogin = new JLabel("Số đăng nhập: ");
         JTextField jtfLogin = new JTextField(6);
         jpCompare.add(jlLogin);
         jpCompare.add(jtfLogin);

         JLabel jlUser = new JLabel("Số người: ");
         JTextField jtfUser = new JTextField(6);
         jpCompare.add(jlUser);
         jpCompare.add(jtfUser);

         JLabel jlGroup = new JLabel("Số nhóm: ");
         JTextField jtfGroup = new JTextField(6);
         jpCompare.add(jlGroup);
         jpCompare.add(jtfGroup);

         JLabel jlCompare = new JLabel("So sánh: ");
         String []compare = {"Không", "Lớn hơn", "Nhỏ hơn", "Bằng"};
         JComboBox<String> jcCompare = new JComboBox<>(compare);
         jpCompare.add(jlCompare);
         jpCompare.add(jcCompare);

         //Tạo các filter
         JPanel jpSortBar = new JPanel();
         jpSortBar.setBackground(Color.white);
         jpSortBar.setSize(750, 50);

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
//                 String startDate = (Utils.checkDate(jtfStart.getText())) ? jtfStart.getText() : "";
//                 String endDate = (Utils.checkDate(jtfEnd.getText())) ? jtfEnd.getText() : "";
                 String fullname = jtfSearch.getText();
                 int temp = jcSort.getSelectedIndex();
                 String sort = (temp == 0) ? "CREATED_AT" : "FULLNAME";
                 temp = jcSortBy.getSelectedIndex();
                 String by = (temp == 0) ? "DESC" : "ASC";
                 String login = (Utils.isInt(jtfLogin.getText())) ? jtfLogin.getText() : "";
                 String userChat = (Utils.isInt(jtfUser.getText())) ? jtfUser.getText() : "";
                 String groupChat = (Utils.isInt(jtfGroup.getText())) ? jtfGroup.getText() : "";
                 int compare = jcCompare.getSelectedIndex();
                 fillActionTable(jtAction, login, userChat, groupChat, fullname, compare, sort, by);
             }
         });
         jpSortBar.add(jbSearch);

         this.add(jpSortBar);
         this.add(jpCompare);
//         this.add(jpDate);
//         this.add(jpMessage);
         this.add(jpTable);
    }
}
