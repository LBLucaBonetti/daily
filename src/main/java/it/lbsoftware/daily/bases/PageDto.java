package it.lbsoftware.daily.bases;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.domain.Page;

@Getter
@EqualsAndHashCode
public class PageDto<T> {

  private final List<T> content;
  private final Integer totalPages;
  private final Long totalElements;
  private final Boolean last;
  private final Integer numberOfElements;

  public PageDto(@NonNull Page<T> page) {
    this.content = page.getContent();
    this.totalPages = page.getTotalPages();
    this.totalElements = page.getTotalElements();
    this.last = page.isLast();
    this.numberOfElements = page.getNumberOfElements();
  }
}
