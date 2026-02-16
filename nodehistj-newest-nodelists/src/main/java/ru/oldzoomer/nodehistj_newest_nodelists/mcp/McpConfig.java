package ru.oldzoomer.nodehistj_newest_nodelists.mcp;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class McpConfig {
    private final NodelistMcpRepository nodelistMcpRepository;

    @Bean
    ToolCallbackProvider authorTools() {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(nodelistMcpRepository)
                .build();
    }
}
