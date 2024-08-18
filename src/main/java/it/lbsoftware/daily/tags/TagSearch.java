package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.search.SearchCriteriaRetriever;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

/**
 * The {@link Tag} search parameters.
 *
 * @param name The name to search
 * @param colorHex The color hex to search
 */
public record TagSearch(String name, String colorHex) implements SearchCriteriaRetriever<Tag> {

  @Override
  public Tag get() {
    return new Tag(name, colorHex, Collections.emptySet(), Collections.emptySet(), null);
  }

  @Override
  public boolean isPopulated() {
    return Stream.of(name, colorHex).anyMatch(StringUtils::isNotBlank);
  }

  @Override
  public String toString() {
    var entries = new HashMap<String, String>();
    entries.put("name", name);
    entries.put("colorHex", colorHex);
    return this.getClass().getSimpleName()
        + "["
        + entries.entrySet().stream()
            .filter((Entry<String, String> entry) -> StringUtils.isNotBlank(entry.getValue()))
            .map((Entry<String, String> entry) -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining(", "))
        + "]";
  }
}
