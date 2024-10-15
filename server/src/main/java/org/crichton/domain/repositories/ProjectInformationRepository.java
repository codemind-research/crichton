package org.crichton.domain.repositories;

import org.crichton.domain.entities.ProjectInformation;
import org.crichton.util.constants.EntityCode;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("projectInformationRepository")
public class ProjectInformationRepository implements IRepository<ProjectInformation, UUID> {

    private static final Map<UUID, ProjectInformation> store = new HashMap<>();


    @Override
    public Optional<ProjectInformation> findById(UUID id) {
        return Optional.ofNullable(store.getOrDefault(id, null));
    }

    @Override
    public List<ProjectInformation> findAll() {
        return store.entrySet().stream().map(Map.Entry::getValue).toList();
    }

    @Override
    public ProjectInformation save(ProjectInformation projectInformation) {
        var id = projectInformation.getId();
        store.put(id, projectInformation);

        return projectInformation;
    }

    @Override
    public boolean deleteById(UUID id) {
        if(store.containsKey(id)) {
            store.remove(id);
            return true;
        }
        return false;
    }
}
