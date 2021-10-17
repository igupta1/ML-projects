import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelToDb
{
    public static void main( String [] args )
    {
        String fileName="/Users/ishaangupta/Desktop/SkalableInternship/src/SourceFileOne.xlsx";
        Vector dataHolder = read(fileName);
        saveToDatabase(dataHolder);
    }
    public static Vector read(String fileName)
    {
        Vector cellVectorHolder = new Vector();
        try
        {
            FileInputStream myInput = new FileInputStream(fileName);
            //POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            XSSFWorkbook myWorkBook = new XSSFWorkbook(myInput);
            XSSFSheet mySheet = myWorkBook.getSheetAt(0);
            Iterator rowIter = mySheet.rowIterator();
            rowIter.next();
            while(rowIter.hasNext())
            {
                XSSFRow myRow = (XSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();
                //Vector cellStoreVector=new Vector();
                List list = new ArrayList();
                while(cellIter.hasNext())
                {
                    XSSFCell myCell = (XSSFCell) cellIter.next();
                    list.add(myCell);
                }
                cellVectorHolder.addElement(list);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return cellVectorHolder;
    }
    private static void saveToDatabase(Vector dataHolder)
    {
        String pn;
        String on;
        String trn;
        String as;
        String bh;
        String ah;
        Date ad;
        Date fd;
        Date sd;
        String fn;
        String ln;
        String lat;
        String pc;
        String prodname;
        String apr;
        String mdr;
        String mdf;
        String gla;
        String ra;
        String nla;

        System.out.println(dataHolder);

        for(Iterator iterator = dataHolder.iterator();iterator.hasNext();)
        {
            List list = (List) iterator.next();
            pn = list.get(0).toString();
            on = list.get(1).toString();
            trn = list.get(2).toString();


            try
            {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                String jdbcURL = "jdbc:mysql://localhost:3306/DataImport?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
                String username = "root";
                String password = "anshuishu21";
                Connection con = DriverManager.getConnection(jdbcURL, username, password);
                System.out.println("connection made...");
                String sql = "INSERT INTO ExcelImport (ProviderName,OfficeName,TRN,ApplicationStatus,BorrowerHold,AllyHold,ApplicationDate,FundedDate,ServiceDate,FirstName,LastName,LoanApplicition,ProductCategory,ProductName,BaseAPR,MDR,MDF,GrossLoanAmount,RefundAmount,NetLoanAmount) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement stmt=con.prepareStatement(sql);

                stmt.setString(1, pn);
                stmt.setString(2, on);
                stmt.setString(3, trn);
                //stmt.setString(3, AccessDate);
                //stmt.setString(4, ProcessTime);
                stmt.executeUpdate();

                System.out.println("Data is inserted");
                stmt.close();
                con.close();
            }

            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            catch (InstantiationException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }



    }
}