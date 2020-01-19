package connection;

import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;

@Slf4j
public class DbConnection {

    private static DbConnection ourInstance = new DbConnection();
    private final Properties properties = new Properties();

    public static DbConnection getInstance() {
        return ourInstance;
    }

    private final Jdbi jdbi;

    {
        try (InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("META-INF/generated/db_properties.properties")) {
            properties.load(Objects.requireNonNull(inputStream));
        } catch (IOException e) {
            log.info(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }

    private DbConnection() {

        String dbUrl = MessageFormat.format("jdbc:mysql://{0}:3306/{1}?createDatabaseIfNotExist=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false",
                properties.getProperty("mysqlService"), properties.getProperty("mysqlDatabase"));
        jdbi = Jdbi.create(dbUrl, properties.getProperty("mysqlUser"), properties.getProperty("mysqlPassword"));
        createTables();
    }

    public Jdbi getJdbi() {
        return jdbi;
    }


    private void createTables() {
        createMovieTable();
        createLoyaltyCardsTable();
        createCustomersTable();
        createSalesStandTable();
    }

    private void createMovieTable() {

        final var sqlMovies = SqlCreateTableCommand
                .builder()
                .table("movies")
                .primaryKey("id")
                .stringColumn("title", 50, "not null")
                .decimalColumn("price", 4, 2, "not null")
                .stringColumn("genre", 50, "not null")
                .intColumn("duration", "not null")
                .column("release_date", "date", "not null")
                .build().toSql();

        jdbi.useHandle(handle -> handle.execute(sqlMovies));
    }

    private void createCustomersTable() {
        final var sqlCustomerTable = SqlCreateTableCommand
                .builder()
                .table("customers")
                .primaryKey("id")
                .intColumn("loyalty_card_id")
                .stringColumn("email", 50, "not null")
                .intColumn("age", "not null")
                .stringColumn("surname", 50, "not null")
                .stringColumn("name", 50, "not null")
                .foreignKey("loyalty_card_id", "loyalty_cards", "id")
                .build().toSql();

        jdbi.useHandle(handle -> handle.execute(sqlCustomerTable));
    }

    private void createSalesStandTable() {
        final var sqlSalesStandTable = SqlCreateTableCommand
                .builder()
                .table("sales_stands")
                .primaryKey("id")
                .intColumn("customer_id", "not null")
                .intColumn("movie_id", "not null")
                .column("start_date_time", "timestamp", "not null")
                .foreignKey("customer_id", "customers", "id")
                .foreignKey("movie_id", "movies", "id")
                .build().toSql();

        jdbi.useHandle(handle -> handle.execute(sqlSalesStandTable));

    }

    private void createLoyaltyCardsTable() {

        final var sqlLoyaltyCards = SqlCreateTableCommand
                .builder()
                .table("loyalty_cards")
                .primaryKey("id")
                .column("expiration_date", "date", "not null")
                .decimalColumn("discount", 2, 0, "not null")
                .intColumn("movies_number", "not null")
                .build().toSql();

        jdbi.useHandle(handle -> handle.execute(sqlLoyaltyCards));
    }
}
