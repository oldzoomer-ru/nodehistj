package ru.oldzoomer.nodehistj_historic_nodelists.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.oldzoomer.nodehistj_historic_nodelists.mcp.HistoricNodelistMcpRepository;
import ru.oldzoomer.nodehistj_historic_nodelists.service.HistoricNodelistService;

@Configuration
@RequiredArgsConstructor
public class McpConfig {
    private final HistoricNodelistService historicNodelistService;

    @Bean
    ToolCallbackProvider authorTools() {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(new HistoricNodelistMcpRepository(historicNodelistService))
                .build();
    }
}
