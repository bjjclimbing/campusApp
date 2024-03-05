package edu.upc.talent.swqa.jdbc.test.utils;

import edu.upc.talent.swqa.jdbc.Database;
import static edu.upc.talent.swqa.jdbc.HikariCP.getDataSource;
import static edu.upc.talent.swqa.jdbc.test.utils.ConsoleUtils.printAlignedTable;
import static edu.upc.talent.swqa.util.Utils.join;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

public abstract class DatabaseBackedTest {

  protected Database db;

  @BeforeEach
  public final void setUpDatabase() {
    final var databaseName = this.getClass().getSimpleName().toLowerCase();
    try (final var db = new Database(getDataSource("jdbc:postgresql:///", "postgres", "postgres"))) {
      db.withConnection((conn) -> {
        conn.update("DROP DATABASE IF EXISTS " + databaseName);
        conn.update("CREATE DATABASE " + databaseName);
      });
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }

    this.db = new Database(getDataSource("jdbc:postgresql:///" + databaseName, "postgres", "postgres"));
  }

  @AfterEach
  public final void afterEach() throws Exception {
    printDbContents();
    if (this.db != null) this.db.close();
  }


  private void printDbContents() {
    final var tables = db.select(
          "SELECT table_name, table_schema FROM information_schema.tables where table_schema = 'public'",
          (rs) -> rs.getString(1)
    );
    tables.forEach(this::printTableContents);
  }

  private void printTableContents(final String tableName) {
    final var columns = db.select(
          "SELECT column_name FROM information_schema.columns WHERE table_name = '" + tableName + "'",
          (rs) ->
                rs.getString(1)
    );
    final var columnNames = String.join(", ", columns);
    final var rows = db.select("SELECT " + columnNames + " FROM " + tableName, (rs) -> {
      final List<String> row = new ArrayList<>();
      for (int i = 1; i <= columns.size(); i++) {
        row.add(rs.getString(i));
      }
      return row;
    });
    System.out.println(("-- " + tableName + " " + "-".repeat(75)).substring(0,80) + "\n");
    printAlignedTable(join(List.of(columns), rows));
    System.out.println();
  }

}
