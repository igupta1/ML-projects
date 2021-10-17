import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Date;
//import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileOnetoDbV4
{

    public static void main(String[] args)
    {
        String jdbcURL = "jdbc:mysql://localhost:3306/DataImport?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        String username = "root";
        String password = "anshuishu21";
        String excelFilePath = "/Users/ishaangupta/Desktop/SkalableInternship/src/SourceFileOne.xlsx";

        int batchSize = 300;

        Connection connection = null;

        try
        {
            long start = System.currentTimeMillis();
            FileInputStream inputStream = new FileInputStream(excelFilePath);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet firstSheet = workbook.getSheetAt(0);
            connection = DriverManager.getConnection(jdbcURL, username, password);
            connection.setAutoCommit(false);
            String sql = "INSERT INTO ExcelImport (ProviderName, OfficeName, TRN, ApplicationStatus, BorrowerHold, AllyHold, ApplicationDate, FundedDate, ServiceDate, FirstName, LastName, LoanApplicitionType, ProductCategory, ProductName, BaseAPR, MDR, MDF, GrossLoanAmount, RefundAmount, NetLoanAmount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            int count = 0;

            String pn;
            String on;
            int trn;
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

            for (int i = 1; i <= 5002; i++)
            {
                Row row = firstSheet.getRow(i);
                for (int j = 0; j < 20; j++)
                {
                    Cell cell = row.getCell(j);
                    int columnIndex = cell.getColumnIndex();

                    switch (columnIndex)
                    {
                        case 0:
                            pn = cell.getStringCellValue();
                            statement.setString(1, pn);
                            break;
                        case 1:
                            on = cell.getStringCellValue();
                            statement.setString(2, on);
                            break;
                        case 2:
                            trn = (int) cell.getNumericCellValue();
                            statement.setInt(3, trn);
                            break;
                        case 3:
                            as = cell.getStringCellValue();
                            statement.setString(4, as);
                            break;
                        case 4:
                            bh = cell.getStringCellValue();
                            statement.setString(5, bh);
                            break;
                        case 5:
                            ah = cell.getStringCellValue();
                            statement.setString(6, ah);
                            break;
                        case 6:
                            ad = cell.getDateCellValue();
                            if (ad != null)
                            {
                                statement.setTimestamp(7, new Timestamp(ad.getTime()));
                            }
                            else
                            {
                                ad = new Date(2000,1,1);
                                statement.setTimestamp(7, new Timestamp(ad.getTime()));
                            }
                            break;
                        case 7:
                            fd = cell.getDateCellValue();
                            if (fd != null)
                            {
                                statement.setTimestamp(8, new Timestamp(fd.getTime()));
                            }
                            else
                            {
                                fd = new Date(2000,1,1);
                                statement.setTimestamp(8, new Timestamp(fd.getTime()));
                            }
                            break;
                        case 8:
                            sd = cell.getDateCellValue();
                            if (sd != null)
                            {
                                statement.setTimestamp(9, new Timestamp(sd.getTime()));
                            }
                            else
                            {
                                sd = new Date(2000,1,1);
                                statement.setTimestamp(9, new Timestamp(sd.getTime()));
                            }
                            break;
                        case 9:
                            fn = cell.getStringCellValue();
                            statement.setString(10, fn);
                            break;
                        case 10:
                            ln = cell.getStringCellValue();
                            statement.setString(11, ln);
                            break;
                        case 11:
                            lat = cell.getStringCellValue();
                            statement.setString(12, lat);
                            break;
                        case 12:
                            pc = cell.getStringCellValue();
                            statement.setString(13, pc);
                            break;
                        case 13:
                            prodname = cell.getStringCellValue();
                            statement.setString(14, prodname);
                            break;
                        case 14:
                            apr = cell.getNumericCellValue() + "";
                            statement.setString(15, apr);
                            break;
                        case 15:
                            mdr = cell.getNumericCellValue() + "";
                            statement.setString(16, mdr);
                            break;
                        case 16:
                            mdf = cell.getNumericCellValue() + "";
                            statement.setString(17, mdf);
                            break;
                        case 17:
                            gla = cell.getNumericCellValue() + "";
                            statement.setString(18, gla);
                            break;
                        case 18:
                            ra = cell.getNumericCellValue() + "";
                            statement.setString(19, ra);
                            break;
                        case 19:
                            nla = cell.getNumericCellValue() + "";
                            statement.setString(20, nla);
                    }
                }

                statement.addBatch();

                if (count % batchSize == 0)
                {
                    statement.executeBatch();
                }
            }

            workbook.close();
            statement.executeBatch();
            connection.commit();
            connection.close();
            long end = System.currentTimeMillis();
            System.out.printf("Import done in %d ms\n", (end - start));

        }
        catch (IOException ex1)
        {
            System.out.println("Error reading file");
            ex1.printStackTrace();
        }

        catch (SQLException ex2)
        {
            System.out.println("Database error");
            ex2.printStackTrace();
        }

    }

}
