package user;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import database.DatabaseManagment;
import datastructure.UserAccount;
import utils.MailService;
import utils.PasswordService;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.JTextField;
import javax.swing.JButton;

public class ForgetPassword extends JFrame {
	private JPanel contentPane;
	private JTextField txtEmail;
	private JButton btnSend;
    private JButton btnBack;

	private UserAccount socketTemp;
	/**
	 * Launch the application.
	 */
	private void btnSendActionPerformed(java.awt.event.ActionEvent evt) { 
		String email = txtEmail.getText().trim();
		DatabaseManagment database = DatabaseManagment.getInstance();
		int ID = database.checkAccount(email);
		if(ID < 0){
			JOptionPane.showMessageDialog(null, "This account does not exist","Not exist",JOptionPane.WARNING_MESSAGE);
			return;
		}
		String newRandomPassword = PasswordService.generateRandomPasseword(10);
		String encryptPassword = PasswordService.encryptPassword(newRandomPassword);
		JOptionPane.showMessageDialog(null, "Sending new password to your email....", "Please wait", JOptionPane.INFORMATION_MESSAGE);
		MailService.sendEmail("Your new password :" + newRandomPassword, email);
		database.changePasswordUser(ID, encryptPassword);
		JOptionPane.showMessageDialog(null, "A new password is sent to your email", "Email sent successfully", JOptionPane.INFORMATION_MESSAGE);

		Login menuForm = new Login(socketTemp.clienSocket,socketTemp.pw,socketTemp.br);
		menuForm.setVisible(true);
		this.dispose();
	}  
	 
	  private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {                                         
		Login menuForm = new Login(socketTemp.clienSocket,socketTemp.pw,socketTemp.br);
		menuForm.setVisible(true);
		this.dispose();
	}  

	/**
	 * Create the frame.
	 */
	public ForgetPassword(UserAccount socketTemp) {
            initComponent();
            this.socketTemp =socketTemp;
            btnSend.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnSendActionPerformed(evt);
                }
            });
             
            btnBack.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                  btnBackActionPerformed(evt);
              }
          });
            
	}
        
    private void initComponent(){
        setFont(null);
		setTitle("Đăng ký tài khoản");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 810, 564);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

                setLocationRelativeTo(null);
		setContentPane(contentPane);
		contentPane.setLayout(null);
		//contentPane.setBackground(new Color(255, 255, 255));
		btnBack = new JButton("Quay về");
		btnBack.setForeground(new Color(0, 0, 0));
		btnBack.setBackground(new Color(255, 255, 255));
		btnBack.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnBack.setBounds(263, 400, 153, 39);
		contentPane.add(btnBack);
		//btnBack.setText("Quay về");
		JLabel lblLogo = new JLabel("");
		lblLogo.setForeground(Color.LIGHT_GRAY);
		lblLogo.setBackground(Color.LIGHT_GRAY);
		lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogo.setIcon(new ImageIcon(Login.class.getResource("/resource/chat.png")));
		lblLogo.setBounds(304, 67, 226, 117);
		contentPane.add(lblLogo);
		//lblLogo.setIcon(new ImageIcon(Login.class.getResource("/resource/chat.png")));
		JLabel lblChangePass = new JLabel("Khởi tạo lại mật khẩu");
		lblChangePass.setHorizontalAlignment(SwingConstants.CENTER);
		lblChangePass.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblChangePass.setBounds(304, 226, 226, 28);
		contentPane.add(lblChangePass);
		//lblChangePass.setText("Khởi tạo lại mật khẩu");
		JLabel lblRequire = new JLabel("Hãy nhập vào Email để nhận mật khẩu mới");
		lblRequire.setForeground(new Color(128, 128, 128));
		lblRequire.setHorizontalAlignment(SwingConstants.CENTER);
		lblRequire.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblRequire.setBounds(259, 276, 329, 26);
		lblRequire.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		contentPane.add(lblRequire);
		//lblRequire.setText("Hãy nhập vào Email để nhận mật khẩu mới");
		txtEmail = new JTextField();
		txtEmail.setHorizontalAlignment(SwingConstants.LEFT);
		txtEmail.setForeground(Color.GRAY);
		txtEmail.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtEmail.setColumns(10);
		txtEmail.setBounds(263, 322, 313, 48);
		contentPane.add(txtEmail);
		//txtEmail.setText("Email");
		btnSend = new JButton("Gửi");
		btnSend.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnSend.setBackground(Color.BLACK);
		btnSend.setBounds(423, 400, 153, 39);
		contentPane.add(btnSend);
        }        
}
