
package admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import database.DatabaseManagment;
import datastructure.UserAccount;
import uichatapp.CreateAccountForm;
import uichatapp.DetailAccountForm;
import uichatapp.SearchBar;
import utils.Utils;

public class MenuAccountManager extends MenuAdminLayout {

    JTable tableUserAccount;
    JComboBox<String> sortFilter;
    SearchBar searchBarFindUser;
    JButton lockAccountButton;
    JButton addAcountButton;
    JButton viewDetailButton;
    JButton refreshButton;
    JButton deleteAccountButton;
    JComboBox<String> sortCriteria;

    public void filltableUserAccount() {
        Utils.clearTable(tableUserAccount);

        DatabaseManagment database = DatabaseManagment.getInstance();
        ArrayList<UserAccount> allUser = database.getAllAccounts();
        // tableFindFriend is JTable
        DefaultTableModel tableModel = (DefaultTableModel) tableUserAccount.getModel();
        for (UserAccount user : allUser) {
            String ID = String.valueOf(user.getID());
            String username = user.getUsername();
            String fullname = user.getFullname();
            String address = user.getAddress();
            String birthDay = user.getBirthDay();
            String gender = user.getGender();
            String email = user.getEmail();
            String ban = "";
            if (user.getBanned()) {
                ban = "Đang bị khóa";
            } else
                ban = "Không bị khóa";
            String online = "";
            if (user.isOnline()) {
                online = "Online";
            } else
                online = "Offline";
            // String online = String.valueOf(user.getOnline());

            String row[] = { ID, username, fullname, address, birthDay, gender, email, online, ban };
            tableModel.addRow(row);
        }
    }

    public void filltableUserAccount(ArrayList<UserAccount> list) {
        Utils.clearTable(tableUserAccount);

        // tableFindFriend is JTable
        DefaultTableModel tableModel = (DefaultTableModel) tableUserAccount.getModel();
        for (UserAccount user : list) {
            String ID = String.valueOf(user.getID());
            String username = user.getUsername();
            String fullname = user.getFullname();
            String address = user.getAddress();
            String birthDay = user.getBirthDay();
            String gender = user.getGender();
            String email = user.getEmail();
            String ban = "";
            if (user.getBanned()) {
                ban = "Đang bị khóa";
            } else
                ban = "Không bị khóa";
            String online = "";
            if (user.isOnline()) {
                online = "Online";
            } else
                online = "Offline";
            // String online = String.valueOf(user.getOnline());

            String row[] = { ID, username, fullname, address, birthDay, gender, email, online, ban };
            tableModel.addRow(row);
        }
    }

    private void fillTableBySort() {
        String Criteria, Filter, input;
        Criteria = new String((String) sortCriteria.getSelectedItem());
        Filter = new String((String) sortFilter.getSelectedItem());
        input = new String(searchBarFindUser.getText());

        String sort = null;
        String by = new String("ASC");
        String name = null;

        if (Filter.equals("Mặc định") && input.isEmpty()) {
            filltableUserAccount();
            return;
        }

        if (Filter.equals("Họ và tên"))
            sort = new String("FULLNAME");
        else if (Filter.equals("Tên đăng nhập"))
            sort = new String("USERNAME");
        else if (Filter.equals("Ngày tạo"))
            sort = new String("CREATED_AT");

        if (!input.isEmpty())
            name = input;

        if (Criteria.equals("Giảm dần"))
            by = new String("DESC");

        DatabaseManagment database = DatabaseManagment.getInstance();
        ArrayList<UserAccount> list = database.getAllAccounts(name, sort, by);
        filltableUserAccount(list);

    }

    private void viewDetailButtonActionPerformed(java.awt.event.ActionEvent evt) {
        UserAccount userSelected = new UserAccount();

        int row = tableUserAccount.getSelectedRow();
        if (row < 0) { // Cảnh báo chưa chọn dòng nào trong bảng
            JOptionPane.showMessageDialog(null, "Please select an account", "Not selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = tableUserAccount.getModel().getValueAt(row, 0).toString();

        userSelected.setID(Integer.parseInt(id));

        DetailAccountForm detailAccountForm = new DetailAccountForm(userSelected, this);
        detailAccountForm.setVisible(true);
        // this.setEnabled(false);
    }

    private void lockAccountButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tableUserAccount.getSelectedRow();
        if (row < 0) { // Cảnh báo chưa chọn dòng nào trong bảng
            JOptionPane.showMessageDialog(null, "Please select an account", "Not selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String IDString = tableUserAccount.getModel().getValueAt(row, 0).toString();
        int ID = Integer.parseInt(IDString);

        DatabaseManagment database = DatabaseManagment.getInstance();
        if (database.checkAccountIsBanned(ID)) {
            if (JOptionPane.showConfirmDialog(null, "Are you sure you want to unban this account?", "Confirm unban",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                database.setLockUserAccount(ID, false);
                filltableUserAccount();
                return;
            } else {
                return;
            }
        } else {
            if (JOptionPane.showConfirmDialog(null, "Are you sure you want to ban this account?", "Confirm ban",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                database.setLockUserAccount(ID, true);
                filltableUserAccount();
                return;
            } else {
                return;
            }
        }
    }

    private void addAcountButtonActionPerformed(java.awt.event.ActionEvent evt) {
        CreateAccountForm createAccountForm = new CreateAccountForm(this);

        createAccountForm.setVisible(true);
    }

    private void deleteAccountButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tableUserAccount.getSelectedRow();
        if (row < 0) { // Cảnh báo chưa chọn dòng nào trong bảng
            JOptionPane.showMessageDialog(null, "Please select an account", "Not selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String IDString = tableUserAccount.getModel().getValueAt(row, 0).toString();
        int ID = Integer.parseInt(IDString);

        if (JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this account \n(The account will not be recoverable) ?",
                "Confirm delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            DatabaseManagment database = DatabaseManagment.getInstance();
            database.deleteAnAccount(ID);
        } else {
            return;
        }
        filltableUserAccount();
    }

    public MenuAccountManager(JFrame parentFrame) {
        super(parentFrame);
        initComponents();

        sortFilter.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                fillTableBySort();
            }
        });

        sortCriteria.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                fillTableBySort();
            }
        });

        searchBarFindUser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fillTableBySort();

            }

        });

        refreshButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                filltableUserAccount();
            }

        });

        filltableUserAccount();
    }

    private void initComponents() {

        tableUserAccount = new JTable();
        sortFilter = new JComboBox<>();
        searchBarFindUser = new SearchBar();
        lockAccountButton = new JButton();
        addAcountButton = new JButton();
        viewDetailButton = new JButton();

        deleteAccountButton = new JButton();
        sortCriteria = new JComboBox<>();
        refreshButton = new JButton();

        JPanel sidePanel = new JPanel();
        JLabel jLabel_sapxeptheo = new JLabel();

        JLabel jLabel_quanlytaikhoan = new JLabel();

        tableUserAccount.setBackground(new java.awt.Color(235, 235, 235));
        tableUserAccount.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {

                },
                new String[] {
                        "id", "Tên đăng nhập", "Họ tên", "Địa chỉ", "Ngày sinh", "Giới tính", "Email", "Tình trạng",
                        "Bị khóa"
                }));
        JScrollPane jScrollpane_tableUserAccount = new JScrollPane();
        jScrollpane_tableUserAccount.setViewportView(tableUserAccount);

        this.add(jScrollpane_tableUserAccount);
        jScrollpane_tableUserAccount.setBounds(320, 120, 920, 560);

        sortFilter.setBackground(new java.awt.Color(235, 235, 235));
        sortFilter.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[] { "Mặc định", "Họ và tên", "Tên đăng nhập", "Ngày tạo" }));
        // sortFilter.addActionListener(new java.awt.event.ActionListener() {
        // public void actionPerformed(java.awt.event.ActionEvent evt) {
        // sortFilterActionPerformed(evt);
        // }
        // });
        this.add(sortFilter);
        sortFilter.setBounds(850, 50, 200, 40);

        jLabel_sapxeptheo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel_sapxeptheo.setForeground(new java.awt.Color(0, 0, 0));
        jLabel_sapxeptheo.setLabelFor(sortFilter);
        jLabel_sapxeptheo.setText("Sắp xếp theo:");
        this.add(jLabel_sapxeptheo);
        jLabel_sapxeptheo.setBounds(720, 50, 130, 30);

        searchBarFindUser.setForeground(new java.awt.Color(0, 0, 0));
        searchBarFindUser.setBackgroundColor(new java.awt.Color(236, 236, 236));
        searchBarFindUser.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        searchBarFindUser.setPlaceHolder("Tìm kiếm theo tên");
        this.add(searchBarFindUser);
        searchBarFindUser.setBounds(400, 40, 290, 50);

        sidePanel.setBackground(new java.awt.Color(235, 235, 235));
        sidePanel.setLayout(null);

        lockAccountButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lockAccountButton.setText("Khóa tài khoản");
        lockAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                lockAccountButtonActionPerformed(evt);
            }
        });
        sidePanel.add(lockAccountButton);
        lockAccountButton.setBounds(30, 550, 240, 70);

        addAcountButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        addAcountButton.setText("Thêm tài khoản");
        addAcountButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAcountButtonActionPerformed(evt);
            }
        });
        sidePanel.add(addAcountButton);
        addAcountButton.setBounds(30, 250, 240, 70);

        viewDetailButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        viewDetailButton.setText("Xem thông tin chi tiết");
        viewDetailButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewDetailButtonActionPerformed(evt);
            }
        });
        sidePanel.add(viewDetailButton);
        viewDetailButton.setBounds(30, 350, 240, 70);
        sidePanel.add(refreshButton);

        refreshButton.setFont(new java.awt.Font("Segoe UI", 1, 18));
        refreshButton.setText("Làm mới danh sách");
        refreshButton.setBounds(30, 170, 240, 40);

        jLabel_quanlytaikhoan.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel_quanlytaikhoan.setForeground(new java.awt.Color(0, 0, 0));
        jLabel_quanlytaikhoan.setText("Quản lý tài khoản");
        sidePanel.add(jLabel_quanlytaikhoan);
        jLabel_quanlytaikhoan.setBounds(50, 40, 220, 70);

        deleteAccountButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        deleteAccountButton.setText("Xóa tài khoản");
        deleteAccountButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAccountButtonActionPerformed(evt);
            }
        });
        sidePanel.add(deleteAccountButton);
        deleteAccountButton.setBounds(30, 450, 240, 70);

        this.add(sidePanel);
        sidePanel.setBounds(0, 0, 300, 750);

        sortCriteria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tăng dần", "Giảm dần" }));
        this.add(sortCriteria);
        sortCriteria.setBounds(1080, 50, 150, 40);
    }

}
