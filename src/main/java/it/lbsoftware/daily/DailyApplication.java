package it.lbsoftware.daily;

import java.util.TimeZone;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport
@CommonsLog
public class DailyApplication {

  public static void main(String[] args) {
    SpringApplication.run(DailyApplication.class, args);
    log.info("Running with the following JVM TimeZone: " + TimeZone.getDefault().getDisplayName());
  }
}
