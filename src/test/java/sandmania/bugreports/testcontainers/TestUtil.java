package sandmania.bugreports.testcontainers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestUtil {

    static void createFileWithNumberOfRows(Integer numberOfRows, Path directoryForFiles) {
        var filePath = directoryForFiles.resolve("insert_" + numberOfRows + "_rows.txt");

        var content = new StringBuilder("""
                insert into test_schema.test_table \
                (column1, column2, column3, column4, column5, column6, column7, column8, column9, column10, column11, column12, column13, column14, column15, column16) \
                values
                """);

        var row = "(null, null, 'TEST_STRING', 1, 'EN', null, '2023-01-01', '2025-05-26', '2023-05-26 09:06:34.503515', 'teststring', 'teststring', 'teststring', null, null, null, null),\n";

        content.append(row.repeat(numberOfRows));
        content.replace(content.length() - 2, content.length(), ";"); // Replace last comma with semicolon

        try {
            Files.writeString(filePath, content.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
