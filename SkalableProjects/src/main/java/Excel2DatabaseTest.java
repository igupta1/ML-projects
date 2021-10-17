import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

/**
 * Sample Java program that imports data from an Excel file to MySQL database.
 *
 * @author Nam Ha Minh - https://www.codejava.net
 *
 */
public class Excel2DatabaseTest
{

    public static void main(String[] args)
    {
        String jdbcURL = "jdbc:mysql://localhost:3306/DataImport?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        String username = "root";
        String password = "anshuishu21";
        String excelFilePath = "/Users/ishaangupta/Desktop/SkalableInternship/src/SourceFileOne.xlsx";
        int batchSize = 20;

        Connection connection = null;

        try
        {
            //long start = System.currentTimeMillis();

            FileInputStream inputStream = new FileInputStream(excelFilePath);

            Workbook workbook = new XSSFWorkbook(inputStream);

            Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = firstSheet.iterator();

            connection = DriverManager.getConnection(jdbcURL, username, password);
            connection.setAutoCommit(false);

            String sql = "INSERT INTO ExcelImport (ProviderName,OfficeName,TRN,ApplicationStatus,BorrowerHold,AllyHold,ApplicationDate,FundedDate,ServiceDate,FirstName,LastName,LoanApplicition,ProductCategory,ProductName,BaseAPR,MDR,MDF,GrossLoanAmount,RefundAmount,NetLoanAmount) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            int count = 0;

            rowIterator.next(); // skip the header row

            while (rowIterator.hasNext())
            {
                Row nextRow = rowIterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();

                while (cellIterator.hasNext())
                {
                    Cell nextCell = cellIterator.next();

                    int columnIndex = nextCell.getColumnIndex();

                    switch (columnIndex)
                    {

                        case 0:
                            String name = nextCell.getStringCellValue();
                            if (name == null)
                                break;
                            System.out.println(name);
                            statement.setString(1, name);
                            break;
                        case 1:
                            String office = nextCell.getStringCellValue();
                            if (office == null)
                                break;
                            System.out.println(office);
                            statement.setString(2, office);
                            break;
                        case 2:
                            int trn = (int) nextCell.getNumericCellValue();
                            //if (trn == null)
                                //break;
                            System.out.println(trn);
                            statement.setInt(3, trn);
                            break;
                        case 3:
                            String as = nextCell.getStringCellValue();
                            if (as == null)
                                break;
                            System.out.println(as);
                            statement.setString(4, as);
                            break;
                        case 4:
                            String bh = nextCell.getStringCellValue();
                            if (bh == null)
                                break;
                            System.out.println(bh);
                            statement.setString(5, bh);
                            break;
                        case 5:
                            String ah = nextCell.getStringCellValue();
                            if (ah == null)
                                break;
                            System.out.println(ah);
                            statement.setString(6, ah);
                            break;
                        case 6:
                            Date ad = nextCell.getDateCellValue();
                            if (ad == null)
                                break;
                            System.out.println(ad);
                            statement.setTimestamp(7, new Timestamp(ad.getTime()));
                            break;
                        case 7:
                            Date fd = nextCell.getDateCellValue();
                            if (fd == null)
                                break;
                            System.out.println(fd);
                            statement.setTimestamp(8, new Timestamp(fd.getTime()));
                            break;
                        case 8:
                            Date sd = nextCell.getDateCellValue();
                            if (sd == null)
                                break;
                            System.out.println(sd);
                            statement.setTimestamp(9, new Timestamp(sd.getTime()));
                            break;
                        case 9:
                            String fn = nextCell.getStringCellValue();
                            if (fn == null)
                                break;
                            System.out.println(fn);
                            statement.setString(10, fn);
                            break;
                        case 10:
                            String ln = nextCell.getStringCellValue();
                            if (ln == null)
                                break;
                            System.out.println(ln);
                            statement.setString(11, ln);
                            break;
                        case 11:
                            String lat = nextCell.getStringCellValue();
                            if (lat == null)
                                break;
                            System.out.println(lat);
                            statement.setString(12, lat);
                            break;
                        case 12:
                            String pc = nextCell.getStringCellValue();
                            if (pc == null)
                                break;
                            System.out.println(pc);
                            statement.setString(13, pc);
                            break;
                        case 13:
                            String pn = nextCell.getStringCellValue();
                            if (pn == null)
                                break;
                            System.out.println(pn);
                            statement.setString(14, pn);
                            break;
                        case 14:
                            String apr = nextCell.getStringCellValue();
                            if (apr == null)
                                break;
                            System.out.println(apr);
                            statement.setString(15, apr);
                            break;
                        case 15:
                            String mdr = nextCell.getStringCellValue();
                            if (mdr == null)
                                break;
                            System.out.println(mdr);
                            statement.setString(16, mdr);
                            break;
                        case 16:
                            String mdf = nextCell.getNumericCellValue() + "";
                            if (mdf == null)
                                break;
                            System.out.println(mdf);
                            statement.setString(17, mdf);
                            break;
                        case 17:
                            String gla = nextCell.getNumericCellValue() + "";
                            if (gla == null)
                                break;
                            System.out.println(gla);
                            statement.setString(18, gla);
                            break;
                        case 18:
                            String ra = nextCell.getNumericCellValue() + "";
                            if (ra == null)
                                break;
                            System.out.println(ra);
                            statement.setString(19, ra);
                            break;
                        case 19:
                            String nla = nextCell.getNumericCellValue() + "";
                            if (nla == null)
                                break;
                            System.out.println(nla);
                            statement.setString(20, nla);
                            break;
                    }

                }

                statement.addBatch();

                if (count % batchSize == 0)
                {
                    statement.executeBatch();
                }

            }

            workbook.close();

            // execute the remaining queries
            statement.executeBatch();

            connection.commit();
            connection.close();

            //long end = System.currentTimeMillis();
            //System.out.printf("Import done in %d ms\n", (end - start));

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