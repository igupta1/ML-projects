import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Sample Java program that imports data from an Excel file to MySQL database.
 *
 * @author Nam Ha Minh - https://www.codejava.net
 *
 */
public class FileOnetoDbV3 {

    public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://localhost:3306/DataImport?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        String username = "root";
        String password = "anshuishu21";
        String excelFilePath = "/Users/ishaangupta/Desktop/SkalableInternship/src/SourceFileOne.xlsx";

        int batchSize = 20;

        Connection connection = null;

        try {
            long start = System.currentTimeMillis();

            FileInputStream inputStream = new FileInputStream(excelFilePath);

            Workbook workbook = new XSSFWorkbook(inputStream);

            Sheet firstSheet = workbook.getSheetAt(0);
            //Iterator<Row> rowIterator = firstSheet.iterator();

            connection = DriverManager.getConnection(jdbcURL, username, password);
            connection.setAutoCommit(false);

            //1st HERE
            String sql = "INSERT INTO ExcelImport (ProviderName, OfficeName, TRN, ApplicationStatus, BorrowerHold, AllyHold, ApplicationDate) VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(sql);

            int count = 0;

            //rowIterator.next(); // skip the header row

            //while (rowIterator.hasNext()) {
            //Row nextRow = rowIterator.next();

            //2nd HERE
            String pn;
            String on;
            int trn;
            String as;
            String bh;
            String ah;
            Date ad;

            System.out.println(firstSheet.getPhysicalNumberOfRows());
            System.out.println(firstSheet.getRow(0).getPhysicalNumberOfCells());

            for (int i = 1; i <= 5002; i++)
            {
                //Iterator<Cell> cellIterator = nextRow.cellIterator();
                Row row = firstSheet.getRow(i);


                //3rd HERE
                for (int j = 0; j < 7; j++)
                {
                    Cell cell = row.getCell(j);

                    int columnIndex = cell.getColumnIndex();

                    switch (columnIndex)
                    {
                        case 0:
                            pn = cell.getStringCellValue();
                            System.out.println(pn);
                            statement.setString(1, pn);
                            break;
                        case 1:
                            on = cell.getStringCellValue();
                            System.out.println(on);
                            statement.setString(2, on);
                            break;
                        case 2:
                            trn = (int) cell.getNumericCellValue();
                            System.out.println(trn);
                            statement.setInt(3, trn);
                            break;
                        case 3:
                            as = cell.getStringCellValue();
                            System.out.println(as);
                            statement.setString(4, as);
                            break;
                        case 4:
                            bh = cell.getStringCellValue();
                            System.out.println(bh);
                            statement.setString(5, bh);
                            break;
                        case 5:
                            ah = cell.getStringCellValue();
                            System.out.println(ah);
                            statement.setString(6, ah);
                            break;
                        case 6:
                            ad = cell.getDateCellValue();
                            System.out.println(ad);
                            statement.setTimestamp(7, new Timestamp(ad.getTime()));
                            //break;

                            //4th HERE
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

            long end = System.currentTimeMillis();
            System.out.printf("Import done in %d ms\n", (end - start));

        } catch (IOException ex1) {
            System.out.println("Error reading file");
            ex1.printStackTrace();
        } catch (SQLException ex2) {
            System.out.println("Database error");
            ex2.printStackTrace();
        }

    }

}
