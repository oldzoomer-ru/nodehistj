package ru.oldzoomer.nodehistj_download_nodelists.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnBooleanProperty(name = "app.updateAtStart", matchIfMissing = true)
public class RunNodelistUpdate implements ApplicationRunner {
    private final UpdateNodelists updateNodelists;

    @Override
    public void run(ApplicationArguments args) {
        updateNodelists.updateNodelists();
    }
}