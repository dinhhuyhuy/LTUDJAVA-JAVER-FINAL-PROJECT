package database;

public class DatabaseConfig {
    public static String databaseName = "chat_application";
    public static String username = System.getenv("postgres"); // thay bằng chuỗi tài khoản postgres nếu k dùng biến môi
                                                               // trường, mặc định là "postgres"
    public static String password = System.getenv("1234"); // thay bằng chuỗi mật khẩu postgres nếu k dùng biến môi
                                                           // trường
}