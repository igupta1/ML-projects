
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class Project2
{
    public static void main (String [] args) throws Exception
    {
        createTable();
    }

    public static void createTable() throws Exception
    {
        try
        {
            Connection con = getConnection();
            PreparedStatement create = con.prepareStatement("CREATE TABLE `students` (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `name` varchar(128) NOT NULL,\n" +
                    "  `enrolled` timestamp NOT NULL,\n" +
                    "  `progress` int(11) NOT NULL,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ");");
            create.executeUpdate();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        finally
        {
            System.out.println("Function Complete.");
        }
    }

    public static Connection getConnection() throws Exception
    {
        try
        {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/DataImport?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            String username = "root";
            String password = "anshuishu21";
            Class.forName(driver);

            Connection conn = DriverManager.getConnection(url,username,password);
            System.out.println("Connected");
            return conn;
        }

        catch(Exception e)
        {
            System.out.println(e);
        }

        return null;
    }
}