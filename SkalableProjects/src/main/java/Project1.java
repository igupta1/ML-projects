
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class Project1
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
            PreparedStatement create = con.prepareStatement("CREATE TABLE ExcelImport (Id int NOT NULL AUTO_INCREMENT, ProviderName varchar(255),OfficeName varchar(255),TRN int,ApplicationStatus varchar(255),BorrowerHold varchar(255),AllyHold varchar(255),ApplicationDate datetime NULL," +
                    "FundedDate datetime NULL,ServiceDate datetime NULL,FirstName varchar(255),LastName varchar(255),LoanApplicitionType varchar(255),ProductCategory varchar(255),ProductName varchar(255),BaseAPR varchar(255),MDR varchar(255),MDF varchar(255),GrossLoanAmount varchar(255),RefundAmount varchar(255),NetLoanAmount varchar(255), PRIMARY KEY(Id))");
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