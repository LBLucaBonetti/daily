package it.lbsoftware.daily.search;

/**
 * Used to retrieve a search criteria for the given entity type.
 *
 * @param <T> The entity type
 */
public interface SearchCriteriaRetriever<T> {

  /**
   * Creates a search criteria, that is, an unsaved entity instance whose sensible fields have been
   * populated with data to search with.
   *
   * @return The search criteria, that is, an entity instance
   */
  T get();

  /**
   * Determines whether the fields to be used to create a search criteria are valid.
   *
   * @return True when the fields required to build a valid search criteria are populated, false
   *     otherwise
   */
  boolean isPopulated();
}
