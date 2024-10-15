package org.crichton.domain.repositories;

import org.crichton.domain.entities.ProjectInformation;
import org.crichton.util.constants.EntityCode;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("projectInformationRepository")
public class ProjectInformationRepository implements IRepository<ProjectInformation, Long> {

    private static final Map<Long, ProjectInformation> store = new HashMap<>();


    @Override
    public Optional<ProjectInformation> findById(Long id) {
        return Optional.ofNullable(store.getOrDefault(id, null));
    }

    @Override
    public List<ProjectInformation> findAll() {
        return store.entrySet().stream().map(Map.Entry::getValue).toList();
    }

    @Override
    public ProjectInformation save(ProjectInformation projectInformation) {
        var id = projectInformation.getId();

        if(id == EntityCode.UNDEFINED) {
            var newId = store.size() + 1L;
            projectInformation.setId(newId);
        }
        store.put(id, projectInformation);

        return projectInformation;
    }

    @Override
    public boolean deleteById(Long id) {
        if(store.containsKey(id)) {
            store.remove(id);
            return true;
        }
        return false;
    }
}
