package sandmania.bugreports.testcontainers;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.ext.ScriptUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.testcontainers.ext.ScriptUtils.*;
import static sandmania.bugreports.testcontainers.TestUtil.createFileWithNumberOfRows;

class ScriptUtilsPerformanceTest {

    @TempDir
    Path tempDir;

    @ParameterizedTest(name = "numberOfRows = {0}")
    @ValueSource(ints = {1000, 10000, 20000, 30000, 40000, 50000, 60000, 70000, 80000, 90000, 100000, })
    void call_ScriptUtils_splitSqlScript_withDifferentFileSizes(Integer numberOfRows) throws IOException {
        // Arrange
        createFileWithNumberOfRows(numberOfRows, tempDir);
        var filePath = tempDir.resolve("insert_" + numberOfRows + "_rows.txt").toString();
        List<String> statements = new LinkedList<>();
        long splitSqlScriptDuration;
        
        try (var sqlScriptIS = new FileInputStream(filePath)) {
            var script = new String(sqlScriptIS.readAllBytes(), StandardCharsets.UTF_8);

            long startTime = System.nanoTime();
        // Act
            ScriptUtils.splitSqlScript(filePath, script, DEFAULT_COMMENT_PREFIX, DEFAULT_STATEMENT_SEPARATOR,
                    DEFAULT_BLOCK_COMMENT_START_DELIMITER, DEFAULT_BLOCK_COMMENT_END_DELIMITER, statements);
            splitSqlScriptDuration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        }

        // Assert
        assertFalse(statements.isEmpty());
        assertEquals(1, statements.size());

        System.out.printf("%s rows took roughly %s milliseconds%n", numberOfRows, splitSqlScriptDuration);
    }
}
