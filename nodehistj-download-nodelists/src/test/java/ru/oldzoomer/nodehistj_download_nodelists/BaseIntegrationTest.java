package ru.oldzoomer.nodehistj_download_nodelists;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import ru.oldzoomer.nodehistj.test.BaseContainerTest;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseIntegrationTest extends BaseContainerTest {

    @DynamicPropertySource
    static void registerExtraProperties(DynamicPropertyRegistry registry) {
        registry.add("s3.user", minioContainer::getUserName);
        registry.add("s3.password", minioContainer::getPassword);
        registry.add("scheduling.enabled", () -> false);
        registry.add("app.updateAtStart", () -> false);
    }
}
