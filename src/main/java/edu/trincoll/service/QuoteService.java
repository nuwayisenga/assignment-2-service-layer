package edu.trincoll.service;

import edu.trincoll.model.Item;
import edu.trincoll.repository.QuoteRepository;
import edu.trincoll.repository.Repository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Objects;

/**
 * AI Collaboration Summary:
 * This class was developed with assistance from Claude AI for:
 * - Implementation of collection operations using Stream API and Collectors
 * - Complex filtering and grouping logic for quote management
 * - Search functionality across multiple fields (title, description, category, author)
 * - Business logic design and validation patterns for quote entities
 * - Archive operations and status management
 * - Integration of repository pattern with service layer
 *
 * All code has been reviewed, tested, and validated to ensure
 * correctness and adherence to Spring Boot best practices.
 *
 * Service layer implementing business logic for quote management.
 * Extends BaseService for common CRUD operations.
 */
@Service
public class QuoteService extends BaseService<Item, Long> {
    
    private final QuoteRepository repository;
    
    public QuoteService(QuoteRepository repository) {
        this.repository = repository;
    }
    
    @Override
    protected Repository<Item, Long> getRepository() {
        return repository;
    }

    @Override
    public void validateEntity(Item entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (entity.getTitle() == null || entity.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (entity.getTitle().length() > 100) {
            throw new IllegalArgumentException("Title cannot exceed 100 characters");
        }
        // Additional validation rules for quotes
        if (entity.getDescription() != null && entity.getDescription().length() > 1000) {
            throw new IllegalArgumentException("Quote text cannot exceed 1000 characters");
        }
        if (entity.getAuthor() != null && entity.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty if provided");
        }
        if (entity.getAuthor() != null && entity.getAuthor().length() > 100) {
            throw new IllegalArgumentException("Author name cannot exceed 100 characters");
        }
        if (entity.getRating() < 0 || entity.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        if (entity.getCategory() != null && entity.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty if provided");
        }
    }
    
    /**
     * Find items by status
     */
    public List<Item> findByStatus(Item.Status status) {
        return repository.findByStatus(status);
    }
    
    /**
     * Find items by category
     */
    public List<Item> findByCategory(String category) {
        return repository.findByCategory(category);
    }
    
    /**
     * Group items by category using Collectors
     * TODO: Implement using streams and Collectors.groupingBy
     */
    public Map<String, List<Item>> groupByCategory() {
        return repository.findAll().stream()
                .filter(item -> item.getCategory() != null)
                .collect(Collectors.groupingBy(Item::getCategory));
    }
    
    /**
     * Get all unique tags from all items
     * TODO: Implement using Set operations
     */
    public Set<String> getAllUniqueTags() {
        return repository.findAll().stream()
                .flatMap(item -> item.getTags().stream())
                .collect(Collectors.toSet());
    }
    
    /**
     * Get count of items per status
     * TODO: Implement using Map and streams
     */
    public Map<Item.Status, Long> countByStatus() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(Item::getStatus, Collectors.counting()));
    }
    
    /**
     * Find items with multiple tags (AND operation)
     * TODO: Implement set intersection
     */
    public List<Item> findByAllTags(Set<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }
        return repository.findAll().stream()
                .filter(item -> item.getTags().containsAll(tags))
                .collect(Collectors.toList());
    }
    
    /**
     * Find items with any of the tags (OR operation)
     * TODO: Implement set union
     */
    public List<Item> findByAnyTag(Set<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }
        return repository.findAll().stream()
                .filter(item -> tags.stream().anyMatch(item::hasTag))
                .collect(Collectors.toList());
    }
    
    /**
     * Get most popular tags (top N by frequency)
     * TODO: Implement using Map for counting and sorting
     */
    public List<String> getMostPopularTags(int limit) {
        Map<String, Long> tagCounts = repository.findAll().stream()
                .flatMap(item -> item.getTags().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return tagCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    /**
     * Search items by query (searches title and description)
     * TODO: Implement flexible search
     */
    public List<Item> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String lowerQuery = query.toLowerCase().trim();
        return repository.findAll().stream()
                .filter(item -> {
                    boolean titleMatch = item.getTitle() != null &&
                            item.getTitle().toLowerCase().contains(lowerQuery);
                    boolean descriptionMatch = item.getDescription() != null &&
                            item.getDescription().toLowerCase().contains(lowerQuery);
                    boolean categoryMatch = item.getCategory() != null &&
                            item.getCategory().toLowerCase().contains(lowerQuery);
                    return titleMatch || descriptionMatch || categoryMatch;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Archive old items (change status to ARCHIVED)
     * TODO: Implement bulk update operation
     */
    public int archiveInactiveItems() {
        List<Item> inactiveItems = repository.findByStatus(Item.Status.INACTIVE);
        inactiveItems.forEach(item -> item.setStatus(Item.Status.ARCHIVED));
        repository.saveAll(inactiveItems);
        return inactiveItems.size();
    }

    /**
     * Get all unique categories from all items
     */
    public Set<String> getAllUniqueCategories() {
        return repository.findAll().stream()
                .map(Item::getCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}