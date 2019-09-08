package connection;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SqlCreateTableCommand {
  private final List<String> commands;

  private SqlCreateTableCommand(SqlCreateTableCommandBuilder builder) {
    commands = builder.commands;
  }

  String toSql() {

    if (commands == null || commands.isEmpty()) {
      return "";
    }

    return commands.get(0) +
            commands.stream().skip(1).collect(Collectors.joining(", ")) +
            " );";
  }

  public static SqlCreateTableCommandBuilder builder() {
    return new SqlCreateTableCommandBuilder();
  }

  public static final class SqlCreateTableCommandBuilder {
    private List<String> commands = new ArrayList<>();

    SqlCreateTableCommandBuilder table(String tableName) {
      if (commands.isEmpty() || !commands.get(0).startsWith("create")) {
        commands.add(0, MessageFormat.format("create table if not exists {0} ( ", tableName));
      }
      return this;
    }

    SqlCreateTableCommandBuilder primaryKey(String name) {
      if (commands.size() == 1) {
        commands.add(1, MessageFormat.format("{0} integer primary key auto_increment ", name));
      }
      return this;
    }

    SqlCreateTableCommandBuilder stringColumn(String name, int maxLength, String... options) {
      if (commands.size() >= 2) {
        commands.add(2, MessageFormat.format("{0} varchar({1}) {2} ", name, maxLength, String.join(", ", options)));
      }
      return this;
    }

    SqlCreateTableCommandBuilder intColumn(String name, String... options) {
      if (commands.size() >= 2) {
        commands.add(2, MessageFormat.format("{0} integer {1} ", name, String.join(", ", options)));
      }
      return this;
    }

    SqlCreateTableCommandBuilder decimalColumn(String name, int scale, int precision, String... options) {
      if (commands.size() >= 2) {
        commands.add(2, MessageFormat.format("{0} decimal({1}, {2}) {3} ", name, scale, precision, String.join(", ", options)));
      }
      return this;
    }

    SqlCreateTableCommandBuilder column(String name, String type, String... options) {
      if (commands.size() >= 2) {
        commands.add(2, MessageFormat.format("{0} {1} {2} ", name, type, String.join(", ", options)));
      }
      return this;
    }

    SqlCreateTableCommandBuilder foreignKey(String columnName, String referenceTable, String referenceTableColumn, String... options) {
      if (commands.size() >= 2) {
        commands.add(MessageFormat.format("foreign key ({0}) references {1}({2}) {3} ", columnName, referenceTable, referenceTableColumn, String.join(", ", options)));
      }
      return this;
    }

    public SqlCreateTableCommand build() {
      return new SqlCreateTableCommand(this);
    }

  }
}
