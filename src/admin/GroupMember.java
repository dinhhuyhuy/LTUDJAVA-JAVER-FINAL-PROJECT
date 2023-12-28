package admin;

import database.DatabaseManagment;
import datastructure.GroupChat;
import datastructure.UserAccount;
import utils.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class GroupMember extends JFrame {
    static void fillMemberTable(JTable jtMember, int groupID, String where){
        Utils.clearTable(jtMember);
        DatabaseManagment db = DatabaseManagment.getInstance();
        ArrayList<UserAccount> menbers = db.getAllMemberGroup(groupID, where);
        DefaultTableModel tableModel = (DefaultTableModel) jtMember.getModel();
        for (UserAccount member : menbers) {
            String id = String.valueOf(member.getID());
            String username = member.getUsername();
            String fullname = member.getFullname();
            String online = (member.getOnline()) ? "Trực tuyến" : "Hạ tuyến";
            String position = (member.getPosition().equals("member")) ? "Thành viên" : "Quản lý";

            String row[] = { id, username, fullname, online, position};
            tableModel.addRow(row);
        }
    }

    GroupMember(int groupID, String groupName, String where){
        String title;
        if(where.equals("member")){
            title = "Thành viên nhóm" + groupName;
        }else{
            title = "Quản lý nhóm" + groupName;
        }
        this.setSize(800, 720);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setTitle(title);

        JPanel jp = new JPanel();
        jp.setBackground(Color.white);
        jp.setSize(800, 720);

        JLabel jlTitle = new JLabel(title);
        jlTitle.setHorizontalAlignment(SwingUtilities.CENTER);
        jlTitle.setFont(new Font("Tahoma", Font.BOLD, 30));
        jp.add(jlTitle);

        final String[][] col = {{"ID", "Tên tài khoản", "Họ và tên",
                "Trạng Thái", "Chức vụ"}};
        DefaultTableModel tableModel = new DefaultTableModel(col[0], 0);
        JTable jtMember = new JTable(tableModel);
        fillMemberTable(jtMember, groupID, where);

        JScrollPane jspMember = new JScrollPane(jtMember);
        jspMember.setPreferredSize(new Dimension(750, 620));
        jp.add(jspMember);

        this.add(jp);
    }
}
