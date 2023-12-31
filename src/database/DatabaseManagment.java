package database;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.Locale;
import java.util.Properties;

import datastructure.FriendRequest;
import datastructure.GroupChat;
import datastructure.LoginHistory;
import datastructure.Message;
import datastructure.UserAccount;
import datastructure.*;

public class DatabaseManagment {

    private static volatile DatabaseManagment instance;
    private Connection conn;

    public Connection getConnection() {
        return conn;
    }
    // Khởi tạo database
    private DatabaseManagment() {
        try {
            String databaseName = DatabaseConfig.databaseName;
            // Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/" + databaseName;
            Properties props = new Properties();
            props.setProperty("user", DatabaseConfig.username);
            props.setProperty("password", DatabaseConfig.password);
            props.setProperty("ssl", "false");
            conn = DriverManager.getConnection(url, props);
            System.out.println("connect successfully");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Gọi database bằng cách dùng hàm getInstance()
    // Ex: DatabaseManagement database = DatabaseManagment.getInstance()
    public static DatabaseManagment getInstance() {
        if (instance == null) {
            synchronized (DatabaseManagment.class) {
                if (instance == null) {
                    instance = new DatabaseManagment();
                }
            }
        }
        return instance;
    }
    // Thêm tài khoản mới vào database
    public int addNewAccount(UserAccount account) {
        if (account.isEmpty()) {
            System.out.println("account information is empty");
            return -1;
        }
        String INSERT_QUERY = "INSERT INTO USER_ACCOUNT(USERNAME,PASSWORD,FULLNAME,ADDRESS,DATE_OF_BIRTH,GENDER,EMAIL,ONLINE,BANNED,CREATED_AT)"
                + "VALUES(?,?,?,?,?,?,?,?,?,?) ";

        try (PreparedStatement statement = conn.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);) {
            statement.setString(1, account.getUsername());

            statement.setString(2, account.getPassword());
            statement.setString(3, account.getFullname());
            statement.setString(4, account.getAddress());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            statement.setDate(5, new java.sql.Date(formatter.parse(account.getBirthDay()).getTime()));
            statement.setString(6, account.getGender());
            statement.setString(7, account.getEmail());
            statement.setBoolean(8, false);
            statement.setBoolean(9, false);
            Date date = new Date();
            // ZonedDateTime myDateObj = ZonedDateTime.now( ZoneId.of("Asia/Ho_Chi_Minh"));
            Timestamp loginTime = new Timestamp(date.getTime());
            statement.setTimestamp(10, loginTime);

            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            rs.next();
            int id = rs.getInt("ID");
            return id;

        } catch (Exception e) {
            System.out.println(e);
        }

        return -1;
    }
    // Thêm tài khoản mới vào database
    public int registerNewAccount(UserAccount account) {
        if (account.isEmpty()) {
            System.out.println("account information is empty");
            return -1;
        }
        String INSERT_QUERY = "INSERT INTO USER_ACCOUNT(USERNAME,PASSWORD,EMAIL,ONLINE,BANNED,CREATED_AT)"
                + "VALUES(?,?,?,?,?,?)";

        try (PreparedStatement statement = conn.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);) {
            statement.setString(1, account.getUsername());

            statement.setString(2, account.getPassword());
            statement.setString(3, account.getEmail());
            statement.setBoolean(4, true);
            statement.setBoolean(5, false);
            Date date = new Date();
            // ZonedDateTime myDateObj = ZonedDateTime.now( ZoneId.of("Asia/Ho_Chi_Minh"));
            Timestamp loginTime = new Timestamp(date.getTime());
            statement.setTimestamp(6, loginTime);

            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            rs.next();
            int id = rs.getInt("ID");
            return id;

        } catch (Exception e) {
            System.out.println(e);
        }

        return -1;
    }
    // Kiểm tra tài khoản đã tồn tại hay chưa
    // Trả về ID nếu tài khoản đã tồn tại

    public boolean checkPassword(int ID, String password) {
        String SELECT_QUERY = "SELECT USERNAME FROM USER_ACCOUNT WHERE ID = ? AND PASSWORD = ?";
        ResultSet data = null;
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, ID);
            statment.setString(2, password);
            data = statment.executeQuery();

            if (!data.next()) {
                return false;
            } else {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }
    // Kiểm tra tài khoản đã tồn tại hay chưa
    // Trả về ID nếu tài khoản đã tồn tại
    public int checkAccount(String email) {
        String SELECT_QUERY = "SELECT ID FROM USER_ACCOUNT WHERE EMAIL = ?";
        ResultSet data = null;
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setString(1, email);
            data = statment.executeQuery();

            if (!data.next()) {
                return -1;
            } else {

                int ID = data.getInt("ID");
                if (data.wasNull()) {
                    return -1;
                } else
                    return ID;

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return -1;
    }
    // Đổi mật khẩu
    public void changePasswordUser(int ID, String newPassword) {
        String UPDATE_QUERY = "UPDATE USER_ACCOUNT SET PASSWORD = ? WHERE ID = ?";
        try (PreparedStatement statement = conn.prepareStatement(UPDATE_QUERY);) {

            statement.setString(1, newPassword);
            statement.setInt(2, ID);
            statement.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    // Xem danh sách bạn bè
    public ArrayList<UserAccount> getFriendArrayList(int ID) {
        String SELECT_QUERY = "SELECT UA.ID,UA.USERNAME,UA.FULLNAME,UA.ONLINE,UA.GENDER FROM USER_ACCOUNT UA INNER JOIN USER_FRIEND UF ON UA.ID = UF.FRIEND_ID WHERE UF.ID = ?";
        ResultSet data = null;
        ArrayList<UserAccount> friendList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, ID);
            data = statment.executeQuery();

            if (!data.next()) {
                return friendList;
            } else {

                do {
                    UserAccount account = new UserAccount();
                    account.setID(data.getInt("ID"));
                    account.setUsername(data.getString("USERNAME"));
                    account.setFullname(data.getString("FULLNAME"));
                    account.setOnline(data.getBoolean("ONLINE"));
                    account.setGender(data.getString("Gender"));

                    friendList.add(account);

                } while (data.next());
                return friendList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return friendList;
    }
    // Xem danh sách bạn bè trực tuyến
    public ArrayList<UserAccount> getFriendArrayListByOnline(int ID) {
        String SELECT_QUERY = "SELECT UA.ID,UA.USERNAME,UA.FULLNAME,UA.ONLINE,UA.GENDER FROM USER_ACCOUNT UA INNER JOIN USER_FRIEND UF ON UA.ID = UF.FRIEND_ID WHERE UF.ID = ? ORDER BY UA.ONLINE DESC";
        ResultSet data = null;
        ArrayList<UserAccount> friendList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, ID);
            data = statment.executeQuery();

            if (!data.next()) {
                return friendList;
            } else {

                do {
                    UserAccount account = new UserAccount();
                    account.setID(data.getInt("ID"));
                    account.setUsername(data.getString("USERNAME"));
                    account.setFullname(data.getString("FULLNAME"));
                    account.setOnline(data.getBoolean("ONLINE"));
                    account.setGender(data.getString("Gender"));
                    friendList.add(account);

                } while (data.next());
                return friendList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return friendList;
    }
    //lấy danh sách mảng bạn bè không có trong nhóm
    public ArrayList<UserAccount> getFriendArrayListNotInGroup(int ID, int groupID) {
        String SELECT_QUERY = "SELECT DISTINCT UA.ID,UA.USERNAME,UA.FULLNAME,UA.ONLINE FROM USER_ACCOUNT UA INNER JOIN USER_FRIEND UF ON UA.ID = UF.FRIEND_ID  WHERE UF.ID = ? AND UA.ID NOT IN(SELECT GM.MEMBER_ID FROM GROUPCHAT_MEMBER GM WHERE GM.GROUPCHAT_ID = ?)";
        ResultSet data = null;
        ArrayList<UserAccount> friendList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, ID);
            statment.setInt(2, groupID);
            data = statment.executeQuery();

            if (!data.next()) {
                return friendList;
            } else {
                do {
                    UserAccount account = new UserAccount();
                    account.setID(data.getInt("ID"));
                    account.setUsername(data.getString("USERNAME"));
                    account.setFullname(data.getString("FULLNAME"));
                    account.setOnline(data.getBoolean("ONLINE"));
                    friendList.add(account);

                } while (data.next());
                return friendList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return friendList;
    }
    //Lấy danh sách mảng bạn bè không có trong nhóm
    public ArrayList<UserAccount> getFriendArrayListNotInGroup(int ID, int groupID, String name) {
        String SELECT_QUERY = "SELECT DISTINCT UA.ID,UA.USERNAME,UA.FULLNAME,UA.ONLINE FROM USER_ACCOUNT UA INNER JOIN USER_FRIEND UF ON UA.ID = UF.FRIEND_ID  WHERE UF.ID = ? AND (UA.USERNAME LIKE ? OR UA.FULLNAME LIKE ?) AND UA.ID NOT IN(SELECT GM.MEMBER_ID FROM GROUPCHAT_MEMBER GM WHERE GM.GROUPCHAT_ID = ?)";
        ResultSet data = null;
        ArrayList<UserAccount> friendList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, ID);
            statment.setString(2, "%" + name + "%");
            statment.setString(3, "%" + name + "%");
            statment.setInt(4, groupID);
            data = statment.executeQuery();

            if (!data.next()) {
                return friendList;
            } else {
                do {
                    UserAccount account = new UserAccount();
                    account.setID(data.getInt("ID"));
                    account.setUsername(data.getString("USERNAME"));
                    account.setFullname(data.getString("FULLNAME"));
                    account.setOnline(data.getBoolean("ONLINE"));
                    friendList.add(account);

                } while (data.next());
                return friendList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return friendList;
    }
    // Xem thông tin chi tiết tài khoản
    public UserAccount getDetailAccount(int ID) {
        String SELECT_QUERY = "SELECT * FROM USER_ACCOUNT WHERE ID = ?";
        ResultSet data = null;
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, ID);
            data = statment.executeQuery();

            if (!data.next()) {
                return null;
            } else {
                UserAccount account = new UserAccount();
                account.setID(data.getInt("ID"));
                account.setUsername(data.getString("USERNAME"));
                account.setPassword(data.getString("PASSWORD"));
                account.setFullname(data.getString("FULLNAME"));
                account.setAddress(data.getString("address"));
                Date birthDay = data.getDate("date_of_birth");
                if (!data.wasNull()) {
                    account.setBirthDay(birthDay.toString());
                } else
                    account.setBirthDay("");

                account.setGender(data.getString("Gender"));
                account.setEmail(data.getString("email"));
                Timestamp createdAt = data.getTimestamp("created_at");
                if (!data.wasNull()) {
                    String formattedDate = new SimpleDateFormat("HH:mm dd-MM-yyyy").format(createdAt);
                    account.setCreatedAt(formattedDate);
                } else
                    account.setCreatedAt("");

                account.setOnline(data.getBoolean("ONLINE"));
                return account;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
    // Xem thông tin chi tiết tài khoản
    public UserAccount getDetailAccount(String username) {
        String SELECT_QUERY = "SELECT * FROM USER_ACCOUNT WHERE USERNAME = ?";
        ResultSet data = null;
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setString(1, username);
            data = statment.executeQuery();

            if (!data.next()) {
                return null;
            } else {
                UserAccount account = new UserAccount();
                account.setID(data.getInt("ID"));
                account.setUsername(data.getString("USERNAME"));
                account.setPassword(data.getString("PASSWORD"));
                account.setFullname(data.getString("FULLNAME"));
                account.setOnline(data.getBoolean("ONLINE"));
                account.setBanned(data.getBoolean("BANNED"));
                return account;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
    // Kiểm tra xem tài khoản đã tồn tại hay chưa
    public boolean checkAccount(int ID) {
        String SELECT_QUERY = "SELECT ID FROM USER_ACCOUNT WHERE ID = ?";
        ResultSet data = null;
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, ID);
            data = statment.executeQuery();

            if (!data.next()) {
                return false;
            } else {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }
    //kiểm tra đăng nhập
    public boolean checkAccount(String username, String password) {
        String SELECT_QUERY = "SELECT ID FROM USER_ACCOUNT WHERE USERNAME = ? AND PASSWORD = ?";
        ResultSet data = null;
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setString(1, username);
            statment.setString(2, password);
            data = statment.executeQuery();

            if (!data.next()) {
                return false;
            } else {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }
    //Tìm kiếm tài khoản
    public ArrayList<UserAccount> searchAccounts(String name) {
        String SELECT_QUERY = "SELECT * FROM USER_ACCOUNT WHERE USERNAME LIKE '?%'";
        ResultSet data = null;
        ArrayList<UserAccount> accountList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setString(1, name);
            data = statment.executeQuery();

            if (!data.next()) {
                return accountList;
            } else {

                do {
                    UserAccount account = new UserAccount();
                    account.setID(data.getInt("ID"));
                    account.setUsername(data.getString("USERNAME"));
                    account.setFullname(data.getString("FULLNAME"));
                    account.setOnline(data.getBoolean("ONLINE"));
                    accountList.add(account);

                } while (data.next());
                return accountList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return accountList;
    }
    //Tìm kiếm danh sách bạn bè
    public ArrayList<UserAccount> searchFriendList(int ID, String name) {
        String SELECT_QUERY = "SELECT UA.ID,UA.USERNAME,UA.FULLNAME,UA.ONLINE FROM USER_ACCOUNT UA INNER JOIN USER_FRIEND UF ON UA.ID = UF.FRIEND_ID WHERE UF.ID = ? AND (UA.USERNAME LIKE ? OR UA.FULLNAME LIKE ?)";
        ResultSet data = null;
        ArrayList<UserAccount> accountList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {
            statment.setInt(1, ID);
            statment.setString(2, "%" + name + "%");
            statment.setString(3, "%" + name + "%");
            data = statment.executeQuery();

            if (!data.next()) {
                return accountList;
            } else {

                do {
                    UserAccount account = new UserAccount();
                    account.setID(data.getInt("ID"));
                    account.setUsername(data.getString("USERNAME"));
                    account.setFullname(data.getString("FULLNAME"));
                    account.setOnline(data.getBoolean("ONLINE"));
                    accountList.add(account);

                } while (data.next());
                return accountList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return accountList;
    }
    //Tìm kiếm tài khoản không phải bạn bè
    public ArrayList<UserAccount> searchAccountsNotFriend(int ID, String name) {
        String SELECT_QUERY = "SELECT UA.ID,UA.USERNAME,UA.FULLNAME,UA.EMAIL,UA.ONLINE FROM USER_ACCOUNT UA WHERE UA.ID NOT IN(SELECT FRIEND_ID FROM USER_FRIEND WHERE ID = ?) AND  (UA.USERNAME LIKE ? OR UA.FULLNAME LIKE ?) AND NOT UA.ID = ?";
        ResultSet data = null;
        ArrayList<UserAccount> accountList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {
            statment.setInt(1, ID);
            statment.setString(2, "%" + name + "%");
            statment.setString(3, "%" + name + "%");
            statment.setInt(4, ID);
            data = statment.executeQuery();

            if (!data.next()) {
                return accountList;
            } else {

                do {
                    UserAccount account = new UserAccount();
                    account.setID(data.getInt("ID"));
                    account.setUsername(data.getString("USERNAME"));
                    account.setFullname(data.getString("FULLNAME"));
                    account.setEmail(data.getString("EMAIL"));
                    account.setOnline(data.getBoolean("ONLINE"));
                    accountList.add(account);

                } while (data.next());
                return accountList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return accountList;
    }
    //Tìm kiếm theo ID
    public ArrayList<Integer> searchGroupIDFromUser(int ID) {
        String SELECT_QUERY = "SELECT GROUPCHAT_ID FROM GROUPCHAT_MEMBER WHERE MEMBER_ID = ?";
        ResultSet data = null;
        ArrayList<Integer> allGroupID = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {
            statment.setInt(1, ID);
            data = statment.executeQuery();

            if (!data.next()) {
                return allGroupID;
            } else {

                do {
                    int groupID = data.getInt("GROUPCHAT_ID");
                    allGroupID.add(groupID);

                } while (data.next());
                return allGroupID;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return allGroupID;
    }
    //Thêm thành viên vào nhóm
    public int addNewGroup(GroupChat group) {
        if (group.isEmpty()) {
            System.out.println("group is empty");
            return -1;
        }
        int groupID = addToGroupTable(group);
        group.setID(groupID);
        addMemberToGroup(group);
        return groupID;
    }
    //Thêm thành viên vào nhóm
    private int addToGroupTable(GroupChat group) {
        String INSERT_QUERY = "INSERT INTO GROUPCHAT(GROUP_NAME,CREATED_AT,ONLINE)"
                + "VALUES(?,?,?)";
        try (PreparedStatement statement = conn.prepareStatement(INSERT_QUERY,
                PreparedStatement.RETURN_GENERATED_KEYS);) {
            statement.setString(1, group.getGroupname());
            Date date = new Date();
            // ZonedDateTime myDateObj = ZonedDateTime.now( ZoneId.of("Asia/Ho_Chi_Minh"));
            Timestamp createdAt = new Timestamp(date.getTime());
            statement.setTimestamp(2, createdAt);
            statement.setBoolean(3, false);

            statement.execute();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                int groupID = rs.getInt("ID");
                return groupID;
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return -1;
    }
    //Thêm thành viên vào nhóm
    private void addMemberToGroup(GroupChat group) {
        String INSERT_QUERY = "INSERT INTO GROUPCHAT_MEMBER(GROUPCHAT_ID,MEMBER_ID,POSITION)"
                + "VALUES(?,?,?)";
        try (PreparedStatement statement = conn.prepareStatement(INSERT_QUERY);) {

            ArrayList<UserAccount> admins = group.getAdmins();
            for (UserAccount admin : admins) {
                statement.setInt(1, group.getID());
                statement.setInt(2, admin.getID());
                statement.setString(3, "admin");
                statement.addBatch();
            }
            ArrayList<UserAccount> members = group.getMembers();
            for (UserAccount member : members) {
                statement.setInt(1, group.getID());
                statement.setInt(2, member.getID());
                statement.setString(3, "member");
                statement.addBatch();
            }
            statement.executeBatch();

        } catch (Exception e) {
            System.out.println(e);
        }

    }
    //Thêm thành viên vào nhóm
    public void addNewMemberToGroup(int groupID, int ID) {
        String INSERT_QUERY = "INSERT INTO GROUPCHAT_MEMBER(GROUPCHAT_ID,MEMBER_ID,POSITION)"
                + "VALUES(?,?,?)";
        try (PreparedStatement statement = conn.prepareStatement(INSERT_QUERY);) {

            statement.setInt(1, groupID);
            statement.setInt(2, ID);
            statement.setString(3, "member");
            statement.execute();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    //Lấy lịch sử đăng nhập
    public ArrayList<LoginHistory> getAllLoginHistory() {
        String SELECT_QUERY = "SELECT LH.*,UA.USERNAME FROM LOGIN_HISTORY LH INNER JOIN USER_ACCOUNT UA ON LH.USER_ID = UA.ID";
        ResultSet data = null;
        ArrayList<LoginHistory> loginList = new ArrayList<>();

        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            // statment.setString(1, name);
            data = statment.executeQuery();

            if (!data.next()) {
                return loginList;
            } else {
                do {
                    LoginHistory login = new LoginHistory();
                    login.setID(data.getInt("LOGIN_ID"));
                    login.setUserID(data.getInt("USER_ID"));
                    login.setUserName(data.getString("username"));
                    java.sql.Timestamp date = data.getTimestamp("LOGIN_TIME");
                    String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date);
                    login.setLoginTime(formattedDate);
                    loginList.add(login);

                } while (data.next());
                return loginList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return loginList;
    }
    //Lấy lịch sử đăng nhập
    public ArrayList<LoginHistory> getAllLoginHistoryUser(int ID) {
        String SELECT_QUERY = "SELECT * FROM LOGIN_HISTORY WHERE USER_ID = ?";
        ResultSet data = null;
        ArrayList<LoginHistory> loginList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, ID);
            data = statment.executeQuery();

            if (!data.next()) {
                return loginList;
            } else {
                do {
                    LoginHistory login = new LoginHistory();
                    login.setID(data.getInt("LOGIN_ID"));
                    java.sql.Timestamp date = data.getTimestamp("LOGIN_TIME");
                    String formattedDate = new SimpleDateFormat("HH:mm dd-MM-yyyy").format(date);
                    login.setLoginTime(formattedDate);
                    loginList.add(login);

                } while (data.next());
                return loginList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return loginList;
    }
    //Lấy lịch sử đăng nhập
    public ArrayList<LoginHistory> getAllLoginHistory(String sort, String by) {
        String SELECT_QUERY = "SELECT LH.*,UA.* FROM LOGIN_HISTORY LH INNER JOIN USER_ACCOUNT UA ON LH.USER_ID = UA.ID ORDER BY "
                + sort + " " + by;
        ResultSet data = null;
        ArrayList<LoginHistory> loginList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            // statment.setString(1, name);
            data = statment.executeQuery();

            if (!data.next()) {
                return loginList;
            } else {

                do {
                    LoginHistory login = new LoginHistory();
                    login.setID(data.getInt("LOGIN_ID"));
                    login.setUserID(data.getInt("USER_ID"));
                    login.setUserName(data.getString("username"));
                    login.setFullName(data.getString("fullname"));
                    Timestamp date = data.getTimestamp("LOGIN_TIME");
                    String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date);
                    login.setLoginTime(formattedDate);
                    loginList.add(login);

                } while (data.next());
                return loginList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return loginList;
    }
    //Thêm lịch sử đăng nhập
    public void addToLoginHistory(int ID) {
        String INSERT_QUERY = "INSERT INTO LOGIN_HISTORY(USER_ID,LOGIN_TIME)"
                + "VALUES(?,?)";
        try (PreparedStatement statement = conn.prepareStatement(INSERT_QUERY);) {
            statement.setInt(1, ID);
            Date date = new Date();
            // ZonedDateTime myDateObj = ZonedDateTime.now( ZoneId.of("Asia/Ho_Chi_Minh"));
            Timestamp loginTime = new Timestamp(date.getTime());
            statement.setTimestamp(2, loginTime);

            statement.execute();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public ArrayList<UserAccount> getAllAccounts() {
        String SELECT_QUERY = "SELECT * FROM USER_ACCOUNT";
        ResultSet data = null;
        ArrayList<UserAccount> accountList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            // statment.setString(1, name);
            data = statment.executeQuery();

            if (!data.next()) {
                return accountList;
            } else {

                do {
                    UserAccount account = new UserAccount();
                    account.setID(data.getInt("ID"));
                    account.setUsername(data.getString("USERNAME"));
                    account.setFullname(data.getString("FULLNAME"));
                    account.setAddress(data.getString("address"));
                    Date birthDay = data.getDate("date_of_birth");
                    if (!data.wasNull()) {
                        account.setBirthDay(birthDay.toString());
                    }

                    account.setGender(data.getString("Gender"));
                    account.setEmail(data.getString("email"));
                    Timestamp createdAt = data.getTimestamp("created_at");
                    if (!data.wasNull()) {
                        String formattedDate = new SimpleDateFormat("HH:mm dd-MM-yyyy").format(createdAt);
                        account.setCreatedAt(formattedDate);
                    }

                    account.setOnline(data.getBoolean("ONLINE"));
                    account.setBanned(data.getBoolean("BANNED"));
                    accountList.add(account);

                } while (data.next());
                return accountList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return accountList;

    }

    // ! FIX COLUMN VARIABLE
    public ArrayList<UserAccount> getAllAccounts(String name, String sort, String by) {

        String SELECT_QUERY = "SELECT * FROM USER_ACCOUNT WHERE (USERNAME LIKE ? OR FULLNAME LIKE ?)  ORDER BY " + sort
                + " " + by;
        if (sort == null && name == null) {
            SELECT_QUERY = "SELECT * FROM USER_ACCOUNT";
        } else if (sort != null && name == null) {
            SELECT_QUERY = "SELECT * FROM USER_ACCOUNT ORDER BY " + sort + " " + by;
        } else if (name != null && sort == null) {
            SELECT_QUERY = "SELECT * FROM USER_ACCOUNT WHERE (USERNAME LIKE ? OR FULLNAME LIKE ?)";
        }

        ResultSet data = null;
        ArrayList<UserAccount> accountList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            if (name != null) {
                statment.setString(1, "%" + name + "%");
                statment.setString(2, "%" + name + "%");
            }

            data = statment.executeQuery();

            if (!data.next()) {
                return accountList;
            } else {

                do {
                    UserAccount account = new UserAccount();
                    account.setID(data.getInt("ID"));
                    account.setUsername(data.getString("USERNAME"));
                    account.setFullname(data.getString("FULLNAME"));
                    account.setAddress(data.getString("address"));
                    Date birthDay = data.getDate("date_of_birth");
                    if (!data.wasNull()) {
                        account.setBirthDay(birthDay.toString());
                    }

                    account.setGender(data.getString("Gender"));
                    account.setEmail(data.getString("email"));
                    Timestamp createdAt = data.getTimestamp("created_at");
                    if (!data.wasNull()) {
                        String formattedDate = new SimpleDateFormat("HH:mm dd-MM-yyyy").format(createdAt);
                        account.setCreatedAt(formattedDate);
                    }
                    account.setOnline(data.getBoolean("ONLINE"));
                    account.setBanned(data.getBoolean("BANNED"));
                    accountList.add(account);

                } while (data.next());
                return accountList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return accountList;

    }
    //Lấy danh sách tài khoản
    public ArrayList<UserAccount> searchUserAccount(String username, String fullname, String banned, String sort) {

        boolean check = false;
        String SELECT_QUERY = "SELECT * FROM USER_ACCOUNT WHERE ";
        if(!username.isEmpty()){
            SELECT_QUERY += "USERNAME LIKE '%" + username +"%' ";
            check = true;
        }
        if(!fullname.isEmpty()){
            if(check){
                SELECT_QUERY += "AND ";
            }
            SELECT_QUERY += "FULLNAME LIKE '%" + fullname +"%' ";
            check = true;
        }
        if(check == false){
            SELECT_QUERY = "SELECT * FROM USER_ACCOUNT";
        }
        if(banned != null){
            if(check){
                SELECT_QUERY += "AND ";
            }
            else {
                SELECT_QUERY += " WHERE ";
            }
            SELECT_QUERY += "BANNED = '" + banned +"' ";
        }
        if(sort != null){
            SELECT_QUERY += " ORDER BY " + sort + " ASC" ;
        }


        ResultSet data = null;
        ArrayList<UserAccount> accountList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            data = statment.executeQuery();

            if (!data.next()) {
                return accountList;
            } else {

                do {
                    UserAccount account = new UserAccount();
                    account.setID(data.getInt("ID"));
                    account.setUsername(data.getString("USERNAME"));
                    account.setFullname(data.getString("FULLNAME"));
                    account.setAddress(data.getString("address"));
                    Date birthDay = data.getDate("date_of_birth");
                    if (!data.wasNull()) {
                        account.setBirthDay(birthDay.toString());
                    }

                    account.setGender(data.getString("Gender"));
                    account.setEmail(data.getString("email"));
                    Timestamp createdAt = data.getTimestamp("created_at");
                    if (!data.wasNull()) {
                        String formattedDate = new SimpleDateFormat("HH:mm dd-MM-yyyy").format(createdAt);
                        account.setCreatedAt(formattedDate);
                    }
                    account.setOnline(data.getBoolean("ONLINE"));
                    account.setBanned(data.getBoolean("BANNED"));
                    accountList.add(account);

                } while (data.next());
                return accountList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return accountList;

    }
    //Lấy thông tin group chat
    public GroupChat getDetailGroupChat(int groupID) {
        String SELECT_QUERY = "SELECT GC.ID,GC.GROUP_NAME,COUNT(MB.MEMBER_ID) AS SOLUONG,GC.CREATED_AT,GC.ONLINE FROM GROUPCHAT GC LEFT OUTER JOIN GROUPCHAT_MEMBER MB ON GC.ID = MB.GROUPCHAT_ID WHERE GC.ID = ? GROUP BY GC.ID ";
        ResultSet data = null;
        GroupChat groupChat = new GroupChat();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, groupID);
            data = statment.executeQuery();

            if (!data.next()) {
                return groupChat;
            } else {

                groupChat.setID(data.getInt("ID"));
                groupChat.setGroupname(data.getString("GROUP_NAME"));
                groupChat.setNumberOfMember(data.getInt("soluong"));
                Timestamp date = data.getTimestamp("CREATED_AT");
                String formattedDate = new SimpleDateFormat("HH:mm dd-MM-yyyy").format(date);
                groupChat.setCreatedAt(formattedDate);
                groupChat.setOnline(data.getBoolean("ONLINE"));
                return groupChat;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return groupChat;
    }
    //Lấy thông tin tất cả group chat
    public ArrayList<GroupChat> getAllGroupChat() {
        String SELECT_QUERY = "SELECT GC.ID,GC.GROUP_NAME,COUNT(MB.MEMBER_ID) AS SOLUONG,GC.CREATED_AT,GC.ONLINE FROM GROUPCHAT GC LEFT OUTER JOIN GROUPCHAT_MEMBER MB ON GC.ID = MB.GROUPCHAT_ID GROUP BY GC.ID";
        ResultSet data = null;
        ArrayList<GroupChat> groupList = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            // statment.setString(1, name);
            data = statement.executeQuery();

            if (!data.next()) {
                return groupList;
            } else {

                do {
                    GroupChat group = new GroupChat();
                    group.setID(data.getInt("ID"));
                    group.setGroupname(data.getString("GROUP_NAME"));
                    group.setNumberOfMember(data.getInt("soluong"));
                    Timestamp date = data.getTimestamp("CREATED_AT");
                    String formattedDate = new SimpleDateFormat("HH:mm dd-MM-yyyy").format(date);
                    group.setCreatedAt(formattedDate);
                    group.setOnline(data.getBoolean("ONLINE"));
                    groupList.add(group);

                } while (data.next());
                return groupList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return groupList;
    }
    //Lấy thông tin tất cả group chat
    public ArrayList<GroupChat> getAllGroupChat(String name, String sort, String by) {
        String findName = "";
        if(!name.isEmpty()){
            findName = "WHERE GC.GROUP_NAME LIKE '%" + name + "%' ";
        }

        String SELECT_QUERY = "SELECT GC.ID,GC.GROUP_NAME,COUNT(MB.MEMBER_ID) AS SOLUONG, GC.CREATED_AT,GC.ONLINE "
                + "FROM GROUPCHAT GC INNER JOIN GROUPCHAT_MEMBER MB ON GC.ID = MB.GROUPCHAT_ID "
                + findName
                + "GROUP BY GC.ID ORDER BY GC." + sort + " " + by;

        ResultSet data = null;
        ArrayList<GroupChat> groupList = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            data = statement.executeQuery();

            if (!data.next()) {
                return groupList;
            } else {

                do {
                    GroupChat group = new GroupChat();
                    group.setID(data.getInt("ID"));
                    group.setGroupname(data.getString("GROUP_NAME"));
                    group.setNumberOfMember(data.getInt("soluong"));
                    Timestamp date = data.getTimestamp("CREATED_AT");
                    String formattedDate = new SimpleDateFormat("HH:mm dd-MM-yyyy").format(date);
                    group.setCreatedAt(formattedDate);
                    group.setOnline(data.getBoolean("ONLINE"));
                    groupList.add(group);

                } while (data.next());
                return groupList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return groupList;
    }
    //Lấy thông tin tất cả group chat đang truy cập
    public ArrayList<GroupChat> getAllGroupChatOnline(int ID) {
        String SELECT_QUERY = "SELECT GC.* FROM GROUPCHAT_MEMBER GM LEFT OUTER JOIN GROUPCHAT GC ON GM.GROUPCHAT_ID = GC.ID WHERE GM.MEMBER_ID = ? ORDER BY GC.ONLINE DESC";
        ResultSet data = null;
        ArrayList<GroupChat> groupList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, ID);
            data = statment.executeQuery();

            if (!data.next()) {
                return groupList;
            } else {

                do {
                    GroupChat group = new GroupChat();
                    group.setID(data.getInt("ID"));
                    group.setGroupname(data.getString("GROUP_NAME"));
                    group.setOnline(data.getBoolean("ONLINE"));
                    groupList.add(group);

                } while (data.next());
                return groupList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return groupList;
    }
    //Lấy message từ 1 user
    public ArrayList<Message> getAllMessageFromUser(int ID) {
        String SELECT_QUERY = "SELECT MU.ID,MU.CHATBOX_ID,UA.USERNAME,MU.TIME_SEND,MU.CONTENT,MU.VISIBLE_ONLY FROM MESSAGE_USER MU INNER JOIN USER_ACCOUNT UA ON UA.ID = MU.FROM_USER WHERE (MU.FROM_USER = ? OR MU.TO_USER = ?) AND (VISIBLE_ONLY = ? OR VISIBLE_ONLY = ?) ORDER BY MU.TIME_SEND ASC";
        ResultSet data = null;
        ArrayList<Message> messageList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, ID);
            statment.setInt(2, ID);
            statment.setInt(3, ID);
            statment.setInt(4, Message.NOT_HIDE);
            data = statment.executeQuery();

            if (!data.next()) {
                return messageList;
            } else {

                do {
                    Message message = new Message();
                    message.setChatboxID(data.getString("chatbox_id"));
                    Timestamp date = data.getTimestamp("time_send");
                    String formattedDate = new SimpleDateFormat("HH:mm dd-MM-yyyy").format(date);
                    message.setDateSend(formattedDate);
                    message.setUserName(data.getString("username"));
                    message.setContent(data.getString("content"));
                    message.setVisible_only(data.getInt("visible_only"));

                    messageList.add(message);

                } while (data.next());
                return messageList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return messageList;
    }
    //Lấy message từ 1 user
    public ArrayList<Message> searchMessageUser(String ChatBoxID, String keyword, int ID) {
        String SELECT_QUERY = "SELECT MU.ID,MU.CHATBOX_ID,UA.USERNAME,MU.TIME_SEND,MU.CONTENT,MU.VISIBLE_ONLY FROM MESSAGE_USER MU INNER JOIN USER_ACCOUNT UA ON UA.ID = MU.FROM_USER WHERE MU.CHATBOX_ID = ? AND MU.CONTENT LIKE ? AND (VISIBLE_ONLY = ? OR VISIBLE_ONLY = ?) ORDER BY MU.TIME_SEND ASC";
        ResultSet data = null;
        ArrayList<Message> messageList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setString(1, ChatBoxID);
            statment.setString(2, "%" + keyword + "%");
            statment.setInt(3, ID);
            statment.setInt(4, Message.NOT_HIDE);
            data = statment.executeQuery();

            if (!data.next()) {
                return messageList;
            } else {
                do {
                    Message message = new Message();
                    message.setChatboxID(data.getString("chatbox_id"));
                    Timestamp date = data.getTimestamp("time_send");
                    String formattedDate = new SimpleDateFormat("HH:mm dd-MM-yyyy").format(date);
                    message.setDateSend(formattedDate);
                    message.setUserName(data.getString("username"));
                    message.setContent(data.getString("content"));
                    int id_visibleOnly = data.getInt("visible_only");
                    if (data.wasNull()) {
                        message.setVisible_only(Message.NOT_HIDE);
                    } else {
                        message.setVisible_only(id_visibleOnly);
                    }

                    messageList.add(message);

                } while (data.next());
                return messageList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return messageList;
    }
    //Lấy message từ 1 user trong 1 group
    public ArrayList<Message> getAllMessageGroupFromUser(int ID) {
        String SELECT_QUERY = "SELECT MG.*,UA.USERNAME FROM MESSAGE_GROUP MG LEFT OUTER JOIN USER_ACCOUNT UA ON MG.FROM_USER = UA.ID WHERE TO_GROUP IN (SELECT GC.ID FROM GROUPCHAT_MEMBER GM LEFT OUTER JOIN GROUPCHAT GC ON GM.GROUPCHAT_ID = GC.ID WHERE GM.MEMBER_ID = ?) ORDER BY MG.TIME_SEND ASC";
        ResultSet data = null;
        ArrayList<Message> messageList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, ID);
            data = statment.executeQuery();

            if (!data.next()) {
                return messageList;
            } else {

                do {
                    Message message = new Message();

                    Timestamp date = data.getTimestamp("time_send");
                    String formattedDate = new SimpleDateFormat("HH:mm dd-MM-yyyy").format(date);
                    message.setDateSend(formattedDate);
                    message.setUserName(data.getString("username"));
                    message.setGroupID(data.getInt("TO_GROUP"));
                    message.setContent(data.getString("content"));

                    messageList.add(message);

                } while (data.next());
                return messageList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return messageList;
    }
    //Search mesage group
    public ArrayList<Message> searchMessageGroup(int groupID, String keyword) {
        String SELECT_QUERY = "SELECT MG.*,UA.USERNAME FROM MESSAGE_GROUP MG LEFT OUTER JOIN USER_ACCOUNT UA ON MG.FROM_USER = UA.ID WHERE TO_GROUP = ? AND CONTENT LIKE ? ORDER BY MG.TIME_SEND ASC";
        ResultSet data = null;
        ArrayList<Message> messageList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, groupID);
            statment.setString(2, "%" + keyword + "%");
            data = statment.executeQuery();

            if (!data.next()) {
                return messageList;
            } else {

                do {
                    Message message = new Message();

                    Timestamp date = data.getTimestamp("time_send");
                    String formattedDate = new SimpleDateFormat("HH:mm dd-MM-yyyy").format(date);
                    message.setDateSend(formattedDate);
                    message.setUserName(data.getString("username"));
                    message.setGroupID(data.getInt("TO_GROUP"));
                    message.setContent(data.getString("content"));

                    messageList.add(message);

                } while (data.next());
                return messageList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return messageList;
    }
    //Lấy danh sách yêu cầu
    public ArrayList<FriendRequest> getAllFriendRequestRaw(int ID) {
        String SELECT_QUERY = "SELECT FR.*,UA.USERNAME FROM FRIEND_REQUEST FR LEFT OUTER JOIN USER_ACCOUNT UA ON FR.FROM_ID = UA.ID WHERE TO_ID = ?";
        ResultSet data = null;
        ArrayList<FriendRequest> requestList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, ID);
            data = statment.executeQuery();

            if (!data.next()) {
                return requestList;
            } else {
                do {
                    FriendRequest request = new FriendRequest();
                    request.setFromName(data.getString("USERNAME"));
                    request.setStatus(data.getString("STATUS"));
                    request.setTryTime(data.getInt("TRY"));

                    requestList.add(request);
                } while (data.next());
                return requestList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return requestList;
    }

    public ArrayList<UserAccount> getAllFriendRequest(int ID) {
        String SELECT_QUERY = "SELECT UA.ID,UA.USERNAME,UA.FULLNAME,UA.EMAIL,UA.ONLINE FROM FRIEND_REQUEST FR LEFT OUTER JOIN USER_ACCOUNT UA ON FR.FROM_ID = UA.ID WHERE TO_ID = ? AND FR.STATUS = 'WAIT'";
        ResultSet data = null;
        ArrayList<UserAccount> requestList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, ID);
            data = statment.executeQuery();

            if (!data.next()) {
                return requestList;
            } else {
                do {
                    UserAccount account = new UserAccount();
                    account.setID(data.getInt("ID"));
                    account.setUsername(data.getString("USERNAME"));
                    account.setFullname(data.getString("FULLNAME"));
                    account.setOnline(data.getBoolean("ONLINE"));

                    requestList.add(account);
                } while (data.next());
                return requestList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return requestList;
    }

    public ArrayList<UserAccount> getAllFriendRequest(int ID, String name) {
        String SELECT_QUERY = "SELECT UA.ID,UA.USERNAME,UA.FULLNAME,UA.EMAIL,UA.ONLINE FROM FRIEND_REQUEST FR LEFT OUTER JOIN USER_ACCOUNT UA ON FR.FROM_ID = UA.ID WHERE TO_ID = ? AND FR.STATUS = 'WAIT' AND (UA.USERNAME LIKE ? OR UA.FULLNAME LIKE ?)";
        ResultSet data = null;
        ArrayList<UserAccount> requestList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, ID);
            statment.setString(2, "%" + name + "%");
            statment.setString(3, "%" + name + "%");
            data = statment.executeQuery();

            if (!data.next()) {
                return requestList;
            } else {
                do {
                    UserAccount account = new UserAccount();
                    account.setID(data.getInt("ID"));
                    account.setUsername(data.getString("USERNAME"));
                    account.setFullname(data.getString("FULLNAME"));
                    account.setOnline(data.getBoolean("ONLINE"));

                    requestList.add(account);
                } while (data.next());
                return requestList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return requestList;
    }

    public void addFriendToUser(int ID, int FriendID) {
        String INSERT_QUERY = "INSERT INTO USER_FRIEND(ID,FRIEND_ID) "
                + "VALUES(?,?)";
        try (PreparedStatement statement = conn.prepareStatement(INSERT_QUERY);) {

            statement.setInt(1, ID);
            statement.setInt(2, FriendID);

            statement.addBatch();

            statement.setInt(1, FriendID);
            statement.setInt(2, ID);

            statement.addBatch();

            statement.executeBatch();

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void unfriendUsers(int ID, int friendID) {
        String DELETE_QUERY = "DELETE FROM USER_FRIEND WHERE ID = ? AND FRIEND_ID = ?";
        try (PreparedStatement statement = conn.prepareStatement(DELETE_QUERY);) {

            statement.setInt(1, ID);
            statement.setInt(2, friendID);
            statement.addBatch();

            statement.setInt(1, friendID);
            statement.setInt(2, ID);
            statement.addBatch();

            statement.executeBatch();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public ArrayList<UserAccount> getAllGroupMembers(int groupID) {
        String SELECT_QUERY = "SELECT UA.ID,UA.USERNAME,UA.FULLNAME,UA.ONLINE,GM.POSITION FROM GROUPCHAT_MEMBER GM LEFT OUTER JOIN USER_ACCOUNT UA ON UA.ID = GM.MEMBER_ID WHERE GM.GROUPCHAT_ID = ?";
        ResultSet data = null;
        ArrayList<UserAccount> memberList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {
            statment.setInt(1, groupID);
            data = statment.executeQuery();

            if (!data.next()) {
                return memberList;
            } else {
                do {
                    UserAccount account = new UserAccount();
                    account.setID(data.getInt("ID"));
                    account.setUsername(data.getString("USERNAME"));
                    account.setFullname(data.getString("FULLNAME"));
                    account.setOnline(data.getBoolean("ONLINE"));
                    account.setPosition(data.getString("POSITION"));

                    memberList.add(account);
                } while (data.next());
                return memberList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return memberList;

    }

    public void assignAdminToUser(int ID, int groupID) {
        String UPDATE_QUERY = "UPDATE GROUPCHAT_MEMBER SET POSITION = 'admin' WHERE GROUPCHAT_ID = ? AND MEMBER_ID = ?";
        try (PreparedStatement statement = conn.prepareStatement(UPDATE_QUERY);) {

            statement.setInt(1, groupID);
            statement.setInt(2, ID);
            statement.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void assignMemberToUser(int ID, int groupID) {
        String UPDATE_QUERY = "UPDATE GROUPCHAT_MEMBER SET POSITION = 'member' WHERE GROUPCHAT_ID = ? AND MEMBER_ID = ?";
        try (PreparedStatement statement = conn.prepareStatement(UPDATE_QUERY);) {

            statement.setInt(1, groupID);
            statement.setInt(2, ID);
            statement.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void setNewGroupName(String name, int groupID) {
        String UPDATE_QUERY = "UPDATE GROUPCHAT SET GROUP_NAME = ? WHERE ID = ?";
        try (PreparedStatement statement = conn.prepareStatement(UPDATE_QUERY);) {

            statement.setString(1, name);
            statement.setInt(2, groupID);
            statement.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void removeMemberFromGroup(int groupID, int ID) {
        String DELETE_QUERY = "DELETE FROM GROUPCHAT_MEMBER WHERE GROUPCHAT_ID = ? AND MEMBER_ID = ?";
        try (PreparedStatement statement = conn.prepareStatement(DELETE_QUERY);) {

            statement.setInt(1, groupID);
            statement.setInt(2, ID);
            statement.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public ArrayList<UserAccount> getAllMemberGroup(int groupID, String where) {
        String SELECT_QUERY = "SELECT UA.*,GM.POSITION FROM GROUPCHAT_MEMBER GM LEFT OUTER JOIN USER_ACCOUNT UA ON GM.MEMBER_ID = UA.ID WHERE GM.GROUPCHAT_ID = ? AND GM.POSITION = ?";
        if (where == null) {
            SELECT_QUERY = "SELECT UA.*,GM.POSITION FROM GROUPCHAT_MEMBER GM LEFT OUTER JOIN USER_ACCOUNT UA ON GM.MEMBER_ID = UA.ID WHERE GM.GROUPCHAT_ID = ?";
        }
        ResultSet data = null;
        ArrayList<UserAccount> accountList = new ArrayList<>();
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, groupID);
            if (where != null) {
                statment.setString(2, where);
            }
            data = statment.executeQuery();

            if (!data.next()) {
                return accountList;
            } else {

                do {
                    UserAccount account = new UserAccount();
                    account.setID(data.getInt("ID"));
                    account.setUsername(data.getString("USERNAME"));
                    account.setFullname(data.getString("FULLNAME"));

                    account.setOnline(data.getBoolean("ONLINE"));
                    account.setPosition(data.getString("POSITION"));

                    accountList.add(account);

                } while (data.next());
                return accountList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return accountList;
    }

    public void createFriendRequest(int ID, int otherID) {
        String SELECT_QUERY = "SELECT * FROM FRIEND_REQUEST WHERE FROM_ID = ? AND TO_ID = ?";
        ResultSet data = null;
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, ID);
            statment.setInt(2, otherID);
            data = statment.executeQuery();

            if (!data.next()) {
                String INSERT_QUERY = "INSERT INTO FRIEND_REQUEST(FROM_ID,TO_ID,STATUS,TRY) VALUES(?,?,?,?)";
                try (PreparedStatement newStatement = conn.prepareStatement(INSERT_QUERY);) {

                    newStatement.setInt(1, ID);
                    newStatement.setInt(2, otherID);
                    newStatement.setString(3, "WAIT");
                    newStatement.setInt(4, 0);
                    newStatement.executeUpdate();

                } catch (Exception e) {
                    System.out.println(e);
                }
            } else {
                String status = data.getString("STATUS");
                if (!status.equals("WAIT")) {
                    String UPDATE_QUERY = "UPDATE FRIEND_REQUEST SET STATUS = 'WAIT' WHERE FROM_ID = ? AND TO_ID = ?";
                    try (PreparedStatement updateStatement = conn.prepareStatement(UPDATE_QUERY);) {

                        updateStatement.setInt(1, ID);
                        updateStatement.setInt(2, otherID);
                        updateStatement.executeUpdate();

                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void setResponeToRequest(int fromID, int toID, String status) {
        String UPDATE_QUERY = "UPDATE FRIEND_REQUEST SET STATUS = ? WHERE FROM_ID = ? AND TO_ID = ?";
        try (PreparedStatement updateStatement = conn.prepareStatement(UPDATE_QUERY);) {

            updateStatement.setString(1, status);
            updateStatement.setInt(2, fromID);
            updateStatement.setInt(3, toID);
            updateStatement.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getUsername(int ID) {
        String SELECT_QUERY = "SELECT USERNAME FROM USER_ACCOUNT WHERE ID = ?";

        ResultSet data = null;
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, ID);
            data = statment.executeQuery();

            if (!data.next()) {
                return "null";
            } else {
                String username = data.getString("USERNAME");
                return username;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return "null";
    }

    public boolean checkAdmin(int ID, int groupID) {
        String SELECT_QUERY = "SELECT GROUPCHAT_ID FROM GROUPCHAT_MEMBER WHERE MEMBER_ID = ? AND GROUPCHAT_ID = ? AND POSITION = 'admin'";
        ResultSet data = null;
        try (PreparedStatement statement = conn.prepareStatement(SELECT_QUERY)) {

            statement.setInt(1, ID);
            statement.setInt(2, groupID);
            data = statement.executeQuery();
            if (data.next()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    public void setLockUserAccount(int ID, boolean lock) {
        String UPDATE_QUERY = "UPDATE USER_ACCOUNT SET BANNED = ? WHERE ID = ?";
        try (PreparedStatement updateStatement = conn.prepareStatement(UPDATE_QUERY);) {

            updateStatement.setBoolean(1, lock);
            updateStatement.setInt(2, ID);
            updateStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Kiểm tra xem tài khoản có bị ban
     * 
     * @param ID
     * @return true nếu bị ban, false nếu không
     */
    public boolean checkAccountIsBanned(int ID) {
        String SELECT_QUERY = "SELECT USERNAME FROM USER_ACCOUNT WHERE ID = ? AND BANNED = true";
        ResultSet data = null;
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {

            statment.setInt(1, ID);
            data = statment.executeQuery();

            if (!data.next()) {
                return false;
            } else {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    public void deleteAnAccount(int ID) {
        String DELETE_QUERY = "DELETE FROM USER_ACCOUNT WHERE ID = ?";
        try (PreparedStatement statement = conn.prepareStatement(DELETE_QUERY);) {

            statement.setInt(1, ID);
            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateAccount(UserAccount account) {
        String UPDATE_QUERY = "UPDATE USER_ACCOUNT SET USERNAME = ?,FULLNAME = ?,ADDRESS = ?,DATE_OF_BIRTH = ?,GENDER = ?,EMAIL = ? WHERE ID = ?";
        try (PreparedStatement statement = conn.prepareStatement(UPDATE_QUERY);) {

            statement.setString(1, account.getUsername());
            statement.setString(2, account.getFullname());
            statement.setString(3, account.getAddress());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            statement.setDate(4, new java.sql.Date(formatter.parse(account.getBirthDay()).getTime()));
            statement.setString(5, account.getGender());
            statement.setString(6, account.getEmail());
            statement.setInt(7, account.getID());
            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveMessageUser(Message message, int fromID, int toID) {
        String INSERT_QUERY = "INSERT INTO MESSAGE_USER(CHATBOX_ID,FROM_USER,TO_USER,TIME_SEND,CONTENT,VISIBLE_ONLY) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement statement = conn.prepareStatement(INSERT_QUERY)) {
            statement.setString(1, message.getChatboxID());
            statement.setInt(2, fromID);
            statement.setInt(3, toID);

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
            Date parsedDate = dateFormat.parse(message.getDateSend());
            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
            statement.setTimestamp(4, timestamp);
            statement.setString(5, message.getContent());
            statement.setInt(6, Message.NOT_HIDE);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveMessageGroup(Message message, int fromID) {
        String INSERT_QUERY = "INSERT INTO MESSAGE_GROUP(FROM_USER,TO_GROUP,TIME_SEND,CONTENT) VALUES(?,?,?,?)";
        try (PreparedStatement statement = conn.prepareStatement(INSERT_QUERY)) {

            statement.setInt(1, fromID);
            statement.setInt(2, message.getGroupID());
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
            Date parsedDate = dateFormat.parse(message.getDateSend());
            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
            statement.setTimestamp(3, timestamp);
            statement.setString(4, message.getContent());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteMessageUser(String chatBoxID, int ID, int otherID) {

        String UPDATE_QUERY = "UPDATE MESSAGE_USER SET VISIBLE_ONLY = (CASE WHEN VISIBLE_ONLY = ? THEN ? WHEN VISIBLE_ONLY = ? THEN ? END)  WHERE CHATBOX_ID = ? AND (VISIBLE_ONLY = ? OR VISIBLE_ONLY = ?)";

        try (PreparedStatement statement = conn.prepareStatement(UPDATE_QUERY)) {

            statement.setInt(1, Message.NOT_HIDE);
            statement.setInt(2, otherID);
            statement.setInt(3, ID);
            statement.setInt(4, Message.DELETED);
            statement.setString(5, chatBoxID);
            statement.setInt(6, Message.NOT_HIDE);
            statement.setInt(7, ID);
            System.out.println("deleted");
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStatusUser(int ID, boolean isonline) {
        String UPDATE_QUERY = "UPDATE USER_ACCOUNT SET ONLINE = ? WHERE ID = ?";
        try (PreparedStatement statement = conn.prepareStatement(UPDATE_QUERY)) {
            statement.setBoolean(1, isonline);
            statement.setInt(2, ID);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<UserAccount> getAccountRegistration(String startDate, String endDate,
                                              String fullname, String sort, String by) {
        String find = "";
        if(!fullname.isEmpty()){
            find += "WHERE FULLNAME LIKE '%" + fullname + "%' ";
        }
        if(!startDate.isEmpty()){
            find += (find.isEmpty()) ? "WHERE " : "AND ";
            find += "CREATED_AT >= '" + startDate + " 00:00:00' ";
        }
        if(!endDate.isEmpty()){
            find += (find.isEmpty()) ? "WHERE " : "AND ";
            find += "CREATED_AT <= '" + endDate + " 00:00:00' ";
        }
        String SELECT_QUERY = "SELECT * FROM USER_ACCOUNT "
                + find
                + "ORDER BY " + sort + " " + by;

        ResultSet data = null;
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {
            data = statment.executeQuery();

            ArrayList<UserAccount> accountList = new ArrayList<>();
            if (!data.next()) {
                return accountList;
            } else {
                do{
                    UserAccount account = new UserAccount();
                    account.setID(data.getInt("ID"));
                    account.setUsername(data.getString("USERNAME"));
                    account.setFullname(data.getString("FULLNAME"));
                    Timestamp createdAt = data.getTimestamp("created_at");
                    if (!data.wasNull()) {
                        String formattedDate = new SimpleDateFormat("HH:mm yyyy-MM-dd").format(createdAt);
                        account.setCreatedAt(formattedDate);
                    } else
                        account.setCreatedAt("");
                    accountList.add(account);
                } while (data.next());
                return accountList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
    public ArrayList<Action> getUserAction(String fullname, String login,
                 String userChat, String groupChat, int compare, String sort, String by) {
        String find = "";
        if(!fullname.isEmpty()) {
            find += "WHERE UA.FULLNAME LIKE '%" + fullname + "%' ";
        }
        String compareOperator;
        switch (compare) {
            case 1:
                compareOperator = ">";
                break;
            case 2:
                compareOperator = "<";
                break;
            case 3:
                compareOperator = "=";
                break;
            default:
                compareOperator = "";
                break;
        }
        String having = "";
        if(!compareOperator.isEmpty()){
            if(!login.isEmpty()){
                having += "HAVING COUNT(DISTINCT LH.*) " + compareOperator + " " +  login + " ";
            }
            if(!userChat.isEmpty()){
                having += (having.isEmpty()) ? "HAVING " : "AND ";
                having += "COUNT(distinct MU.id) " + compareOperator + " " +  userChat + " ";
            }
            if(!groupChat.isEmpty()){
                having += (having.isEmpty()) ? "HAVING " : "AND ";
                having += "COUNT(distinct MG.to_group) " + compareOperator + " " +  groupChat + " ";
            }
        }

        String SELECT_QUERY = "SELECT UA.id as ID, UA.fullname as FULLNAME, "
                + "COUNT(distinct LH.*) as LOGIN, "
                + "COUNT(distinct MG.to_group) as GROUPS, "
                + "COUNT(distinct MU.id) as USERS "
                + "FROM user_account UA "
                + "LEFT JOIN login_history LH ON LH.user_id = UA.id "
                + "LEFT JOIN message_group MG ON MG.from_user = UA.id "
                + "LEFT JOIN message_user MU ON MU.from_user = UA.id OR MU.to_user = UA.id "
                + find
                + "GROUP BY UA.id "
                + having
                + "ORDER BY " + sort + " " + by;
        System.out.println(SELECT_QUERY);

        ResultSet data = null;
        try (PreparedStatement statment = conn.prepareStatement(SELECT_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);) {
            data = statment.executeQuery();

            ArrayList<Action> actionList = new ArrayList<>();
            if (!data.next()) {
                return actionList;
            } else {
                do{
                    Action action = new Action();
                    action.setID(data.getInt("ID"));
                    action.setFullName(data.getString("FULLNAME"));
                    action.setLogin(data.getInt("LOGIN"));
                    action.setUserChat(data.getInt("USERS"));
                    action.setGroupChat(data.getInt("GROUPS"));
                    actionList.add(action);
                } while (data.next());
                return actionList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                try {
                    data.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
}
