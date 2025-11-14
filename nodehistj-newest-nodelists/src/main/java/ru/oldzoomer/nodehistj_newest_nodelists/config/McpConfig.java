package ru.oldzoomer.nodehistj_newest_nodelists.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.oldzoomer.nodehistj_newest_nodelists.mcp.NodelistMcpRepository;
import ru.oldzoomer.nodehistj_newest_nodelists.service.NodelistService;

@Configuration
@RequiredArgsConstructor
public class McpConfig {
    private final NodelistService nodelistService;

    @Bean
    ToolCallbackProvider authorTools() {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(new NodelistMcpRepository(nodelistService))
                .build();
    }
}
