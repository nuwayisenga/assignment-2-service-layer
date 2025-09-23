package edu.trincoll.repository;

import edu.trincoll.model.Item;
import java.util.List;

/**
 * TODO: Rename this interface to match your domain
 * Examples: BookmarkRepository, QuoteRepository, etc.
 * 
 * Add domain-specific query methods that make sense for your use case.
 */
public interface ItemRepository extends Repository<Item, Long> {
    
    /**
     * Find all items with a specific status
     */
    List<Item> findByStatus(Item.Status status);
    
    /**
     * Find all items in a category
     */
    List<Item> findByCategory(String category);
    
    /**
     * Find all items containing a specific tag
     */
    List<Item> findByTag(String tag);
    
    /**
     * Find items with a title containing the search term (case-insensitive)
     */
    List<Item> findByTitleContaining(String searchTerm);

    /**
     * Find quotes by author name
     */
    List<Item> findByAuthor(String author);

    /**
     * Find quotes that are marked as favorites
     */
    List<Item> findFavorites();

    /**
     * Find quotes with rating above specified minimum
     */
    List<Item> findByMinRating(double minRating);

    /**
     * Find quotes created within date range
     */
    List<Item> findByDateRange(java.time.LocalDateTime start, java.time.LocalDateTime end);
}