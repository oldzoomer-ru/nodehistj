package ru.oldzoomer.nodehistj_download_nodelists.util;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class RunNodelistUpdate implements ApplicationRunner {
    private final UpdateNodelists updateNodelists;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        updateNodelists.updateNodelists();
    }
}