package com.example.cinema.cache;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * LFU (Least Frequently Used) cache implementation.
 * Stores a limited number of objects and removes the least frequently used ones.
 *
 * @param <T> the type of stored objects
 */
@Slf4j
public abstract class LfuCache<T> {

  private final int capacity;
  private final Map<Long, Entry<T>> cache = new HashMap<>();

  /**
   * Represents a cache entry with a stored value and access frequency.
   *
   * @param <T> the type of the stored value
   */
  protected static class Entry<T> {
    T value;
    int frequency;

    /**
     * Creates a new cache entry with the specified value.
     * The access frequency is set to 1 by default.
     *
     * @param value the value to store
     */
    Entry(T value) {
      this.value = value;
      this.frequency = 1;
    }
  }

  /**
   * Creates an LFU cache with the specified capacity.
   *
   * @param capacity the maximum number of elements the cache can hold
   */
  protected LfuCache(int capacity) {
    this.capacity = capacity;
  }

  /**
   * Retrieves an object from the cache.
   * If the object is found, its frequency is increased.
   *
   * @param id the ID of the object
   * @return the stored object if present, otherwise {@code null}
   */
  public T get(Long id) {
    Entry<T> entry = cache.get(id);
    if (entry == null) {
      return null;
    }
    entry.frequency++;
    log.info(
            "Объект успешно извлечён из кэша. ID: {}, Частота доступа: {}",
            id, entry.frequency
    );
    return entry.value;
  }

  /**
   * Adds or updates an object in the cache.
   * If the cache is full, it removes the least frequently used item.
   *
   * @param id    the ID of the object
   * @param value the value to store
   */
  public void put(Long id, T value) {
    if (cache.containsKey(id)) {
      Entry<T> entry = cache.get(id);
      entry.value = value;
      entry.frequency++;
      log.info(
              "Объект обновлен в кэше. ID: {}, Новая частота: {}",
              id, entry.frequency
      );
    } else {
      if (cache.size() >= capacity) {
        evictLeastFrequentlyUsed();
      }
      cache.put(id, new Entry<>(value));
      log.info(
              "Новый объект добавлен в кэш. ID: {}",
              id
      );
    }
  }


  private void evictLeastFrequentlyUsed() {
    Long lfuKey = null;
    int minFrequency = Integer.MAX_VALUE;

    for (Map.Entry<Long, Entry<T>> entry : cache.entrySet()) {
      if (entry.getValue().frequency < minFrequency) {
        minFrequency = entry.getValue().frequency;
        lfuKey = entry.getKey();
      }
    }

    if (lfuKey != null) {
      cache.remove(lfuKey);
      log.info(
              "Удалённый объект из кэша. ID: {}, Частота при удалении: {}",
              lfuKey, minFrequency
      );
    }
  }

  /**
   * Removes an object from the cache by its ID.
   *
   * @param id the ID of the object to remove
   */
  public void remove(Long id) {
    if (cache.remove(id) != null) {
      log.info(
              "Объект успешно удалён из кэша. ID: {}",
              id
      );
    }
  }

  /**
   * Clears all objects from the cache.
   */
  public void clear() {
    cache.clear();
    log.info("Все объекты успешно удалены из кэша.");
  }
}