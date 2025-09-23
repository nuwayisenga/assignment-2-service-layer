package edu.trincoll.repository;

import edu.trincoll.model.Item;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
/**
 * TODO: Rename this class to match your domain
 * 
 * In-memory implementation of the repository using Java collections.
 * Uses ConcurrentHashMap for thread-safety.
 */
@Repository
public class InMemoryItemRepository implements ItemRepository {
    
    private final Map<Long, Item> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public Item save(Item entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }
    
    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }
    
    @Override
    public List<Item> findAll() {
        // TODO: Return defensive copy
        return new ArrayList<>(storage.values());
    }
    
    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
    
    @Override
    public long count() {
        return storage.size();
    }
    
    @Override
    public void deleteAll() {
        storage.clear();
        idGenerator.set(1);
    }
    
    @Override
    public List<Item> saveAll(List<Item> entities) {
        return entities.stream()
                .map(this::save)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Item> findByStatus(Item.Status status) {
        // TODO: Implement using streams
        return storage.values().stream()
                .filter(item -> item.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findByCategory(String category) {
        if (category == null) {
            return new ArrayList<>();
        }
        return storage.values().stream()
                .filter(item -> category.equals(item.getCategory()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findByTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return storage.values().stream()
                .filter(item -> item.hasTag(tag))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findByTitleContaining(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return storage.values().stream()
                .filter(item -> item.getTitle() != null &&
                        item.getTitle().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return storage.values().stream()
                .filter(item -> author.equals(item.getAuthor()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findFavorites() {
        return storage.values().stream()
                .filter(Item::isFavorite)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findByMinRating(double minRating) {
        return storage.values().stream()
                .filter(item -> item.getRating() >= minRating)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return storage.values().stream()
                .filter(item -> item.getCreatedAt().isAfter(start) &&
                        item.getCreatedAt().isBefore(end))
                .collect(Collectors.toList());
    }
}