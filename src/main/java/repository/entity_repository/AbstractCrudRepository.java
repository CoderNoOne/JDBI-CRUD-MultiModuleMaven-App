package repository.entity_repository;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

public abstract class AbstractCrudRepository<T> implements CrudRepository<T> {

  private Class<T> type = (Class<T>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];


  @Override
  public void delete(Integer id) {
    // type.getSimpleName()
  }

  @Override
  public Optional<T> findById(Integer id) {
    return Optional.empty();
  }

  @Override
  public List<T> findAll() {
    return null;
  }

  @Override
  public void deleteAll() {

  }
}
