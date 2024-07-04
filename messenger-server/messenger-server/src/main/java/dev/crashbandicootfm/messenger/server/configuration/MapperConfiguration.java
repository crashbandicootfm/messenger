package dev.crashbandicootfm.messenger.server.configuration;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {

    @Bean
    public Mapper dozerMapperBean() {
        return DozerBeanMapperBuilder.buildDefault();
    }
}
