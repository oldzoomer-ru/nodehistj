package ru.oldzoomer.nodehistj_download_nodelists.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class RunNodelistUpdate implements ApplicationRunner {
    private final UpdateNodelists updateNodelists;

    @Override
    public void run(ApplicationArguments args) {
        updateNodelists.updateNodelists();
    }
}