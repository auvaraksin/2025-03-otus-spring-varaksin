package ru.otus.hw.configuration;

import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;
import de.flapdoodle.embed.mongo.distribution.Versions;
import de.flapdoodle.embed.process.distribution.Version;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddedMongoConfig {

    @Bean
    public IFeatureAwareVersion embeddedMongoVersion() {
        return Versions.withFeatures(Version.of("4.0.2"));
    }
}
