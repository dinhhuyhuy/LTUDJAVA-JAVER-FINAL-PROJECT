package uichatapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JOptionPane;

import admin.MenuAccountManager;
// import adminchatapp.MenuAccountManager;
import datastructure.UserAccount;
import utils.PasswordService;
import database.DatabaseManagment;

public class CreateAccountForm extends javax.swing.JFrame {

    private javax.swing.JTextField addressField;
    private com.toedter.calendar.JDateChooser birthDayChooser;
    private javax.swing.JButton createAccButton;
    private javax.swing.JButton deleteFieldsButton;
    private javax.swing.JTextField emailField;
    private javax.swing.JRadioButton femaleRadio;
    private javax.swing.ButtonGroup genderGroup;
    private javax.swing.JRadioButton maleRadio;
    private javax.swing.JTextField nameField;
    private javax.swing.JTextField passwordField;
    private javax.swing.JTextField usernameField;

    // private MenuAccountManager menu;
    // TODO 1: Nạp dữ liệu

    public void createNewAccount() {
        DatabaseManagment database = DatabaseManagment.getInstance();

        String birthDay;

        // Lấy dữ liệu ngày sinh
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        birthDay = df.format(birthDayChooser.getDate());

        String username = usernameField.getText().toString().trim();
        String fullname = nameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String address = addressField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String gender = "";

        if (maleRadio.isSelected()) {
            gender = "Nam";
        } else if (femaleRadio.isSelected()) {
            gender = "Nữ";
        }

        UserAccount newUser = new UserAccount();
        newUser.setUsername(username);
        newUser.setFullname(fullname);
        String encryptPassword = PasswordService.encryptPassword(password);
        newUser.setPassword(encryptPassword);
        newUser.setEmail(email);
        newUser.setAddress(address);
        newUser.setGender(gender);
        newUser.setBirthDay(birthDay);

        if (database.addNewAccount(newUser) != -1) {
            JOptionPane.showMessageDialog(null, "Add completed!", "Add account",
                    JOptionPane.INFORMATION_MESSAGE);
            ((MenuAccountManager) jPanel1).filltableUserAccount();
            return;
        } else {
            JOptionPane.showMessageDialog(null, "Add failed!", "Add account",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    private void clearFields() {

        usernameField.setText("");
        nameField.setText("");
        passwordField.setText("");
        addressField.setText("");
        emailField.setText("");
        genderGroup.clearSelection();

    }

    // admin
    public CreateAccountForm(MenuAccountManager menu) {
        initComponents();
        this.jPanel1 = menu;

        createAccButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewAccount();
            }
        });
        deleteFieldsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearFields();
            }
        });

    }

    private void initComponents() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                    .getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CreateAccountForm.class.getName()).log(
                    java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CreateAccountForm.class.getName()).log(
                    java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CreateAccountForm.class.getName()).log(
                    java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CreateAccountForm.class.getName()).log(
                    java.util.logging.Level.SEVERE,
                    null, ex);
        }

        genderGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jlabel_tendangnhap = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        jLabel_hotvaten = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jLabel_diachi = new javax.swing.JLabel();
        addressField = new javax.swing.JTextField();
        jLabel_ngaysinh = new javax.swing.JLabel();
        jLabel_gioitinh = new javax.swing.JLabel();
        jLabel_email = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        passwordField = new javax.swing.JTextField();
        jLabel_matkhau = new javax.swing.JLabel();
        femaleRadio = new javax.swing.JRadioButton();
        maleRadio = new javax.swing.JRadioButton();
        createAccButton = new javax.swing.JButton();
        deleteFieldsButton = new javax.swing.JButton();
        birthDayChooser = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tạo tài khoản");
        setMinimumSize(new java.awt.Dimension(700, 500));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMinimumSize(new java.awt.Dimension(700, 500));

        jlabel_tendangnhap.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jlabel_tendangnhap.setForeground(new java.awt.Color(0, 0, 0));
        jlabel_tendangnhap.setText("Tên đăng nhập :");

        jLabel_hotvaten.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_hotvaten.setForeground(new java.awt.Color(0, 0, 0));
        jLabel_hotvaten.setText("Họ và tên :");

        jLabel_diachi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_diachi.setForeground(new java.awt.Color(0, 0, 0));
        jLabel_diachi.setText("Địa chỉ :");

        jLabel_ngaysinh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_ngaysinh.setForeground(new java.awt.Color(0, 0, 0));
        jLabel_ngaysinh.setText("Ngày sinh:");

        jLabel_gioitinh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_gioitinh.setForeground(new java.awt.Color(0, 0, 0));
        jLabel_gioitinh.setText("Giới tính :");

        jLabel_email.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_email.setForeground(new java.awt.Color(0, 0, 0));
        jLabel_email.setText("Email :");

        jLabel_matkhau.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_matkhau.setForeground(new java.awt.Color(0, 0, 0));
        jLabel_matkhau.setText("Mật khẩu :");

        genderGroup.add(femaleRadio);
        femaleRadio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        femaleRadio.setForeground(new java.awt.Color(0, 0, 0));
        femaleRadio.setText("Nữ");

        genderGroup.add(maleRadio);
        maleRadio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        maleRadio.setForeground(new java.awt.Color(0, 0, 0));
        maleRadio.setText("Nam");

        createAccButton.setText("Hoàn tất");

        deleteFieldsButton.setText("Xóa dữ liệu ô");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(102, 102, 102)
                                .addGroup(jPanel1Layout
                                        .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                jPanel1Layout
                                                        .createSequentialGroup()
                                                        .addComponent(createAccButton,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                192,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                        .addComponent(deleteFieldsButton,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                191,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                jPanel1Layout
                                                        .createSequentialGroup()
                                                        .addComponent(jLabel_email,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                136,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(35, 35, 35)
                                                        .addComponent(emailField))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                jPanel1Layout
                                                        .createSequentialGroup()
                                                        .addComponent(jLabel_matkhau,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                136,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(35, 35, 35)
                                                        .addComponent(passwordField))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                jPanel1Layout
                                                        .createSequentialGroup()
                                                        .addGroup(jPanel1Layout
                                                                .createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(jLabel_hotvaten,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        136,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jlabel_tendangnhap,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        136,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGap(35, 35, 35)
                                                        .addGroup(jPanel1Layout
                                                                .createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                                        false)
                                                                .addComponent(usernameField)
                                                                .addComponent(nameField,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        310,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                jPanel1Layout
                                                        .createSequentialGroup()
                                                        .addComponent(jLabel_gioitinh,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                136,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                        .addComponent(maleRadio,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                98,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(61, 61, 61)
                                                        .addComponent(femaleRadio,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                98,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                jPanel1Layout
                                                        .createSequentialGroup()
                                                        .addGroup(jPanel1Layout
                                                                .createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(jLabel_diachi,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        136,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jLabel_ngaysinh,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        136,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGap(35, 35, 35)
                                                        .addGroup(jPanel1Layout
                                                                .createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(addressField)
                                                                .addComponent(birthDayChooser,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE))))
                                .addContainerGap(117, Short.MAX_VALUE)));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addGroup(jPanel1Layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jlabel_tendangnhap,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                27,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(usernameField,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(20, 20, 20)
                                .addGroup(jPanel1Layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel_hotvaten,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                27,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(nameField,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel_diachi,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                27,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(addressField,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout
                                                .createSequentialGroup()
                                                .addComponent(jLabel_ngaysinh,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        27,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(12, 12, 12))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                jPanel1Layout
                                                        .createSequentialGroup()
                                                        .addComponent(birthDayChooser,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(jPanel1Layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel_gioitinh,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                27,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(femaleRadio)
                                        .addComponent(maleRadio))
                                .addGap(17, 17, 17)
                                .addGroup(jPanel1Layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel_email,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                27,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(emailField,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel_matkhau,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                27,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(passwordField,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(36, 36, 36)
                                .addGroup(jPanel1Layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(deleteFieldsButton,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                62,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(createAccButton,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                62,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(76, Short.MAX_VALUE)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        pack();
        setLocationRelativeTo(null);
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
    }

    // public static void main(String args[]) {
        // java.awt.EventQueue.invokeLater(new Runnable() {
                // public void run() {
                        // new CreateAccountForm().setVisible(true);
                // }
        // }); 
    // }

    private javax.swing.JLabel jLabel_diachi;
    private javax.swing.JLabel jLabel_email;
    private javax.swing.JLabel jLabel_gioitinh;
    private javax.swing.JLabel jLabel_hotvaten;
    private javax.swing.JLabel jLabel_matkhau;
    private javax.swing.JLabel jLabel_ngaysinh;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jlabel_tendangnhap;
}
