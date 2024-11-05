package org.crichton.domain.repositories;

import org.crichton.models.PluginProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component("pluginProcessorManager")
public class PluginProcessorManager implements IRepository<PluginProcessor, UUID> {

    private static final Map<UUID, PluginProcessor> store = new ConcurrentHashMap<>();

    public boolean hasPluginProcessor() {
        return store.size() > 0;
    }

    @Override
    public Optional<PluginProcessor> findById(UUID id) {
        return  Optional.ofNullable(store.getOrDefault(id, null));
    }

    @Override
    public List<PluginProcessor> findAll() {
        return store.entrySet().stream().map(Map.Entry::getValue).toList();
    }

    @Override
    public PluginProcessor save(PluginProcessor pluginProcessor) {
        var id = pluginProcessor.getId();
        store.put(id, pluginProcessor);
        return pluginProcessor;
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
