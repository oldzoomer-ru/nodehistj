package ru.oldzoomer.nodehistj_history_diff.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.oldzoomer.nodehistj_history_diff.mcp.NodeHistoryMcpRepository;
import ru.oldzoomer.nodehistj_history_diff.service.NodeHistoryService;

@Configuration
@RequiredArgsConstructor
public class McpConfig {
    private final NodeHistoryService nodeHistoryService;

    @Bean
    ToolCallbackProvider authorTools() {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(new NodeHistoryMcpRepository(nodeHistoryService))
                .build();
    }
}
