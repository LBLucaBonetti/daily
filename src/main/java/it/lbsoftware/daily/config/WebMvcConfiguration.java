package it.lbsoftware.daily.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // The index.html file should never be cached since the frontend is a Single Page Application.
    // The Cache-Control HTTP header set to no-store instead of no-cache is the right choice here
    registry
        .addResourceHandler("/index.html")
        .addResourceLocations("classpath:/static/")
        .setCacheControl(CacheControl.noStore());
  }
}
