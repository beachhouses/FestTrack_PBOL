import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/concertdb";
    private static final String USER = "root";
    private static final String PASSWORD = "";

static {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        System.out.println("DRIVER KETEMU!!");
    } catch (ClassNotFoundException e) {
        System.out.println("DRIVER TIDAK KETEMU!!");
        e.printStackTrace();
    }
}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}