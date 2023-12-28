package admin;

import database.DatabaseManagment;
import datastructure.GroupChat;
import utils.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GroupManagement extends JFrame {
    private int groupID;
    private String groupName;

    static void fillGroupTable(JTable jtGroup, String name, String sort, String by) {
        Utils.clearTable(jtGroup);
        DatabaseManagment db = DatabaseManagment.getInstance();
        ArrayList<GroupChat> groups = db.getAllGroupChat(name, sort, by);
        DefaultTableModel tableModel = (DefaultTableModel) jtGroup.getModel();
        for (GroupChat group : groups) {
            String id = String.valueOf(group.getID());
            String groupname = group.getGroupname();
            String member = String.valueOf(group.getNumberOfMember());
            String create_at = group.getCreatedAt();
            String online = (group.getOnline()) ? "Trực tuyến" : "Hạ tuyến";

            String row[] = { id, groupname, member, create_at, online };
            tableModel.addRow(row);
        }
    }


    public GroupManagement(){
        this.setTitle("Danh sách nhóm");
        this.setSize(800, 720);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        //Tạo danh sách nhóm
        JPanel jpTable = new JPanel();
        jpTable.setBackground(Color.white);
        jpTable.setSize(800, 520);

        final String[][] col = {{"ID Nhóm", "Tên Nhóm", "Số thành viên",
                "Ngày Khởi Tạo", "Trạng Thái"}};
        DefaultTableModel tableModel = new DefaultTableModel(col[0], 0);
        JTable jtGroup = new JTable(tableModel);
        fillGroupTable(jtGroup,"", "CREATED_AT", "DESC");
        jtGroup.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                groupID = Integer.parseInt(jtGroup.getValueAt(jtGroup.getSelectedRow(), 0).toString());
                groupName = jtGroup.getValueAt(jtGroup.getSelectedRow(), 1).toString();
            }
        });

        JScrollPane jspGroup = new JScrollPane(jtGroup);
        jspGroup.setPreferredSize(new Dimension(750, 520));
        jpTable.add(jspGroup);

        //Tạo các filter
        JPanel jpSortBar = new JPanel();
        jpSortBar.setBackground(Color.white);
        jpSortBar.setSize(800, 100);

        JLabel jlSearch = new JLabel("Tìm kiếm: ");
        JTextField jtfSearch = new JTextField(30);
        jpSortBar.add(jlSearch);
        jpSortBar.add(jtfSearch);

        String []sort = {"Ngày khởi tạo", "Tên nhóm"};
        JComboBox<String> jcSort = new JComboBox<>(sort);
        jpSortBar.add(jcSort);

        String []by = {"Giảm dần", "Tăng dần"};
        JComboBox<String> jcSortBy = new JComboBox<>(by);
        jpSortBar.add(jcSortBy);

        JButton jbSearch = new JButton("Tìm và sắp xếp");
        jbSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = jtfSearch.getText();
                int temp = jcSort.getSelectedIndex();
                String sort = (temp == 0) ? "CREATED_AT" : "GROUP_NAME";
                temp = jcSortBy.getSelectedIndex();
                String by = (temp == 0) ? "DESC" : "ASC";
                System.out.println(name);
                System.out.println(sort);
                System.out.println(by);
                fillGroupTable(jtGroup, name, sort, by);
            }
        });
        jpSortBar.add(jbSearch);

        JPanel jpMember = new JPanel();
        jpMember.setBackground(Color.white);
        jpMember.setSize(800, 100);

        JButton jbMenber = new JButton("Xem thành viên");
        jbMenber.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame jfrMember = new GroupMember(groupID, groupName, "member");
                jfrMember.setVisible(true);
            }
        });
        jpMember.add(jbMenber);

        JButton jbAdmin = new JButton("Xem quản lý");
        jbAdmin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame jfrMember = new GroupMember(groupID, groupName, "admin");
                jfrMember.setVisible(true);
            }
        });
        jpMember.add(jbAdmin);


        this.add(jpSortBar);
        this.add(jpTable);
        this.add(jpMember);
    }
}
