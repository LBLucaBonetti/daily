package it.lbsoftware.daily.config;

import it.lbsoftware.daily.notes.Note;
import it.lbsoftware.daily.notes.NoteDtoIn;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DtoEntityMappingConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<NoteDtoIn, Note>() {
            protected void configure() {
                skip().setTagSet(null);
            }
        });
        return modelMapper;
    }

}
