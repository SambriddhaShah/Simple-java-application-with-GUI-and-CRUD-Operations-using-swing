import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtility {

  public static Connection getDbConnection() throws SQLException {
    // jdbc:mysql://localhost:3306/iitstudent
    String connectionString = "jdbc:mysql://" + Config.dbHost + ":" + Config.dbPort
        + "/" + Config.dbName;
    // DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());

    return DriverManager.getConnection(connectionString, Config.dbUser, Config.dbPass);
  }

}
