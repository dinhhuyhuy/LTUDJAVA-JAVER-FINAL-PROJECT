package admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainAdmin{
    MainAdmin(){
        JFrame mainFrame = new JFrame();
        mainFrame.setTitle("Trang quản trị");
        mainFrame.setSize(800, 520);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jpMain = new JPanel();
        jpMain.setBackground(Color.white);
        jpMain.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel jlTitle = new JLabel("Chào mừng đến với trang quản lý.");
        jlTitle.setHorizontalAlignment(SwingUtilities.CENTER);
        jlTitle.setFont(new Font("Tahoma", Font.BOLD, 30));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 3; c.weightx = 0.0;
        c.fill = GridBagConstraints.BOTH; c.ipady = 60;
        jpMain.add(jlTitle, c);

        //Quản lý danh sách người dùng
        JButton jbUser = new JButton("Xem danh sách người dùng");
        jbUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.setEnabled(true);
                UserManagement um = new UserManagement();
                um.setVisible(true);
            }
        });
        c.gridx = 0; c.gridy = 1; c.gridwidth = 1; c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        jpMain.add(jbUser, c);

        //Xem danh sách đăng nhập
        JButton jbLogin = new JButton("Xem danh sách đăng nhập");
        jbLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        c.gridx = 1; c.gridy = 1; c.gridwidth = 1; c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        jpMain.add(jbLogin, c);

        //Xem danh sách nhóm chat
        JButton jbGroup = new JButton("Xem danh sách nhóm");
        jbGroup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.setEnabled(true);
                GroupManagement gm = new GroupManagement();
                gm.setVisible(true);
            }
        });
        c.gridx = 2; c.gridy = 1; c.gridwidth = 1; c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        jpMain.add(jbGroup, c);

        //Xem danh sách báo cáo spam
        JButton jbSpam = new JButton("Xem danh sách spam");
        jbSpam.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        c.gridx = 0; c.gridy = 2; c.gridwidth = 1; c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        jpMain.add(jbSpam, c);

        //Xem danh sách đăng ký mới
        JButton jbSignUp = new JButton("Xem danh sách đăng ký");
        jbSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        c.gridx = 1; c.gridy = 2; c.gridwidth = 1; c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        jpMain.add(jbSignUp, c);

        //Biểu đồ số lượng người đăng ký mới
        JButton jbSignUpChart = new JButton("Biểu đồ đăng ký mới");
        jbSignUpChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        c.gridx = 2; c.gridy = 2; c.gridwidth = 1; c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        jpMain.add(jbSignUpChart, c);

        //Xem danh sách người dùng và số lượng bạn bè
        JButton jbfriend = new JButton("Xem số lượng bạn bè");
        jbfriend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        c.gridx = 0; c.gridy = 3; c.gridwidth = 1; c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        jpMain.add(jbfriend, c);

        //Xem danh sách người dùng hoạt động
        JButton jbAction = new JButton("Xem danh sách hoạt động");
        jbAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        c.gridx = 1; c.gridy = 3; c.gridwidth = 1; c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        jpMain.add(jbAction, c);

        //Biểu đồ số lượng người hoạt động
        JButton jbActionChart = new JButton("Biểu đồ số lượng người hoạt động ");
        jbActionChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        c.gridx = 2; c.gridy = 3; c.gridwidth = 1; c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        jpMain.add(jbActionChart, c);

        mainFrame.setContentPane(jpMain);
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainAdmin();
            }
        });
    }
}
