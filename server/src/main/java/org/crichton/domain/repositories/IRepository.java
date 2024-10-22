package org.crichton.domain.repositories;

import java.util.List;
import java.util.Optional;

public interface IRepository<Entity, ID> {

    Optional<Entity> findById(ID id);

    List<Entity> findAll();

    Entity save(Entity entity);

    boolean deleteById(ID id);

}
