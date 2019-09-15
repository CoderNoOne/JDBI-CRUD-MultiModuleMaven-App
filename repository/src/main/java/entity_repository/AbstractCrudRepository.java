package entity_repository;

import connection.DbConnection;
import exceptions.AppException;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Update;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractCrudRepository<T> implements CrudRepository<T> {

  private Class<T> type = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  private Jdbi jdbi = DbConnection.getInstance().getJdbi();

  @Override
  public void delete(Integer id) {

    if (id == null) {
      throw new AppException("id is null");
    }

    Handle handle = jdbi.open();

    try (handle;
         Update update = handle
                 .createUpdate("delete from " + getTableName(type.getSimpleName()) + " where id = :id")) {
      update
              .bind("id", id)
              .execute();
    } catch (Exception e) {
      throw new AppException(String.format("Delete %s with id: %s exception", id, type));
    }

  }

  @Override
  public Optional<T> findById(Integer id) {

    if (id == null) {
      throw new AppException("id is null");
    }

    return jdbi.withHandle(handle -> handle
            .createQuery("select * from " + getTableName(type.getSimpleName()) + " where id = :id")
            .bind("id", id)
            .mapToBean(type)
            .findFirst());
  }

  @Override
  public List<T> findAll() {
    return jdbi.withHandle(handle -> handle
            .createQuery("select * from " + getTableName(type.getSimpleName()))
            .mapToBean(type)
            .list());
  }

  @Override
  public void deleteAll() {
    jdbi.withHandle(handle -> handle
            .createUpdate("delete from " + getTableName(type.getSimpleName()))
            .execute());
  }

  private String getTableName(String className) {
    return className.chars().skip(1).mapToObj(x -> String.valueOf((char) x))
            .map(x -> Character.isUpperCase(x.charAt(0)) ? x = "_" + x.toLowerCase() : x)
            .collect(Collectors.joining("", className.substring(0, 1).toLowerCase(), "s"));
  }
}
