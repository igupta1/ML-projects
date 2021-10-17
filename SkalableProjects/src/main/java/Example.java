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
public class Example {

	public static void main(String[] args) {
		String jdbcURL = "jdbc:mysql://localhost:3306/DataImport?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		String username = "root";
		String password = "anshuishu21";
		String excelFilePath = "/Users/ishaangupta/Desktop/SkalableInternship/src/Students.xlsx";

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
 
            String sql = "INSERT INTO students (name, enrolled, progress) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);		
			
            int count = 0;
            
            //rowIterator.next(); // skip the header row
            
			//while (rowIterator.hasNext()) {
				//Row nextRow = rowIterator.next();
			String name;
			Date enrollDate;
			int progress;

			System.out.println(firstSheet.getPhysicalNumberOfRows());
			System.out.println(firstSheet.getRow(0).getPhysicalNumberOfCells());

			for (int i = 1; firstSheet.getRow(i).getCell(0) != null; i++)
			{
				//Iterator<Cell> cellIterator = nextRow.cellIterator();
				Row row = firstSheet.getRow(i);

				//while (cellIterator.hasNext()) {
					//Cell nextCell = cellIterator.next();
				for (int j = 0; j < row.getPhysicalNumberOfCells(); j++)
				{
					Cell cell = row.getCell(j);

					//int columnIndex = nextCell.getColumnIndex();
					int columnIndex = cell.getColumnIndex();

					switch (columnIndex)
					{
					case 0:
						name = cell.getStringCellValue();
						System.out.println(name);
						statement.setString(1, name);
						break;
					case 1:
						enrollDate = cell.getDateCellValue();
						System.out.println(enrollDate);
						statement.setTimestamp(2, new Timestamp(enrollDate.getTime()));
					case 2:
						progress = (int) cell.getNumericCellValue();
						System.out.println(progress);
						statement.setInt(3, progress);
					}

					//if (name.equals ("Jenny") && )

				}
				
                statement.addBatch();
                
                if (count % batchSize == 0) {
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
