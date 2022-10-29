package it.lbsoftware.daily.bases;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.Page;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class PageDto<T> {

  private List<T> content;
  private Integer totalPages;
  private Long totalElements;
  private Boolean last;
  private Integer numberOfElements;

  public PageDto(@NonNull Page<T> page) {
    this.content = page.getContent();
    this.totalPages = page.getTotalPages();
    this.totalElements = page.getTotalElements();
    this.last = page.isLast();
    this.numberOfElements = page.getNumberOfElements();
  }
}
