package admin;

import database.DatabaseManagment;
import datastructure.*;
import datastructure.UserAccount;
import utils.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class AddUser extends  JFrame {
    private JTextField usernameField;
    private JTextField passwordField;
    private JTextField fullnameField;
    private JTextField addressField;
    private JTextField birthDayField;
    private JTextField genderField;
    private JTextField emailField;
    public AddUser() {
        setTitle("Thêm Người Dùng");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Khởi tạo các JTextField

        usernameField = new JTextField(30);
        passwordField = new JTextField(30);
        fullnameField = new JTextField(30);
        addressField = new JTextField(30);
        birthDayField = new JTextField(30);
        genderField = new JTextField(30);
        emailField = new JTextField(30);

        // Tạo JButton để thêm người dùng
        JButton addButton = new JButton("Thêm Người Dùng");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String username = usernameField.getText();
                String password = passwordField.getText();
                String fullname = fullnameField.getText();
                String address = addressField.getText();
                String birthDay = birthDayField.getText();
                String gender = genderField.getText();
                String email = emailField.getText();
                DatabaseManagment db = DatabaseManagment.getInstance();
                int result = db.addNewAccount(new UserAccount(-1, username, password, fullname, address, birthDay, gender, email, false));
                if(result == -1){
                    JOptionPane.showMessageDialog(AddUser.this, "Thêm người dùng không thành công.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(AddUser.this, "Người dùng đã được thêm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // Sắp xếp giao diện bằng cách sử dụng LayoutManager
        setLayout(new GridLayout(14, 2));

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(new JLabel("Fullname:"));
        add(fullnameField);
        add(new JLabel("Address:"));
        add(addressField);
        add(new JLabel("BirthDay:"));
        add(birthDayField);
        add(new JLabel("Gender:"));
        add(genderField);
        add(new JLabel("Email:"));
        add(emailField);
        add(addButton);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
public class UserManagement extends JFrame {

    static void fillUserTable(JTable jtUser, String name, String sort, String by) {
        Utils.clearTable(jtUser);
        DatabaseManagment db = DatabaseManagment.getInstance();
        ArrayList<UserAccount> users = db.getAllAccounts(name, sort, by);
        DefaultTableModel tableModel = (DefaultTableModel) jtUser.getModel();
        for (UserAccount user : users) {
            String id = String.valueOf(user.getID());
            String username = user.getUsername();
            String fullname = user.getFullname();
            String address = user.getAddress();
            String date_of_birth = user.getBirthDay();
            String gender = user.getGender();
            String email = user.getEmail();
            String date_create = user.getCreatedAt();
            String banned = (user.getBanned()) ? "Khóa" : "Mở";

            String row[] = { id, username, fullname, address, date_of_birth, gender, email, date_create, banned };
            tableModel.addRow(row);
        }
    }
    static void fillSearchUserTable(JTable jtUser, String username, String fullname, String banned, String sort) {
        Utils.clearTable(jtUser);
        DatabaseManagment db = DatabaseManagment.getInstance();
        ArrayList<UserAccount> users = db.searchUserAccount(username, fullname, banned, sort);
        DefaultTableModel tableModel = (DefaultTableModel) jtUser.getModel();
        for (UserAccount user : users) {
            String id = String.valueOf(user.getID());
            String username2 = user.getUsername();
            String fullname2 = user.getFullname();
            String address = user.getAddress();
            String date_of_birth = user.getBirthDay();
            String gender = user.getGender();
            String email = user.getEmail();
            String date_create = user.getCreatedAt();
            String banned2 = (user.getBanned()) ? "Khóa" : "Mở";

            String row[] = { id, username2, fullname2, address, date_of_birth, gender, email, date_create, banned2 };
            tableModel.addRow(row);
        }
    }
    public UserManagement(){
        this.setTitle("Danh sách người dùng");
        this.setSize(800, 720);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        //Tạo danh sách người dùng
        JPanel jpTable = new JPanel();
        jpTable.setBackground(Color.white);
        jpTable.setSize(800, 620);

        final String[][] col = {{"ID người dùng", "Tên đăng nhập", "Họ tên",
                "Địa chỉ", "Ngày sinh", "Giới tính", "Email", "Ngày tạo" ,"Trạng thái"}};
        DefaultTableModel tableModel = new DefaultTableModel(col[0], 0);
        JTable jtUser = new JTable(tableModel);
        fillUserTable(jtUser,"", "ID", "ASC");

        JScrollPane jspUser = new JScrollPane(jtUser);
        jspUser.setPreferredSize(new Dimension(750, 520));
        jpTable.add(jspUser);

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

        JPanel jpSearchFullname = new JPanel();
        JLabel jlSearchFullname = new JLabel("Tên đầy đủ:   ");
        JTextField jtfSearchFullname = new JTextField(30);
        jpSearchFullname.add(jlSearchFullname);
        jpSearchFullname.add(jtfSearchFullname);
        jpSearchBar.add(jpSearchFullname);

        JPanel jpSearchButton = new JPanel();
        String []banned = {"Tất cả","Khóa", "Mở"};
        JComboBox<String> jcBanned = new JComboBox<>(banned);
        JLabel jlBanned = new JLabel("Trạng thái: ");
        jpSearchButton.add(jlBanned);
        jpSearchButton.add(jcBanned);

        JPanel jpSortButton = new JPanel();
        String []sort = {"Tên","Ngày tạo"};
        JComboBox<String> jcSort = new JComboBox<>(sort);
        JLabel jlSort = new JLabel("Sắp xếp: ");
        jpSortButton.add(jlSort);
        jpSortButton.add(jcSort);
        jpSearchButton.add(jpSortButton);


        JButton jbSearch = new JButton("Lọc");
        jpSearchButton.add(jbSearch);
        jpSearchBar.add(jpSearchButton);


        jbSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = jtfSearchUsername.getText();
                String fullname = jtfSearchFullname.getText();
                int selectBanned =  jcBanned.getSelectedIndex();
                String banned = null;
                if(selectBanned == 1){
                    banned = "TRUE";
                }
                if(selectBanned == 2){
                    banned = "FALSE";
                }
                if(selectBanned == 0){
                    banned = null;
                }
                int selectSort = jcSort.getSelectedIndex();
                String sort = (selectSort == 0) ? "FULLNAME" : "CREATED_AT";


                fillSearchUserTable(jtUser, username, fullname, banned, sort);
            }
        });


        this.add(jpSearchBar);
        this.add(jpTable);

        JButton jbAddUser = new JButton("Thêm người dùng");
        jbAddUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new AddUser();
                    }
                });
            }
        });
        this.add(jbAddUser);

    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UserManagement um = new UserManagement();
                um.setVisible(true);
            }
        });
    }
}
