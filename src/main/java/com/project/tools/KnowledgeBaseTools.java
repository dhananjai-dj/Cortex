package com.project.tools;

import com.project.dto.KbResult;
import com.project.dto.KbSearchRequest;
import com.project.service.InjectorService;
import com.project.service.SearchService;
import com.project.util.Constants;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class KnowledgeBaseTools {

    private final InjectorService injectorService;
    private final SearchService searchService;

    public KnowledgeBaseTools(InjectorService injectorService, SearchService searchService) {
        this.searchService = searchService;
        this.injectorService = injectorService;
    }

    @McpTool(name = "save_analysis_summary", description = Constants.Prompts.INJECT_TOOL_PROMPT)
    public String injectData(
            @McpToolParam(description = Constants.Prompts.INJECT_TOOL_SUMMARY_PROMPT)
            String summary,
            @McpToolParam(description = Constants.Prompts.INJECT_TOOL_MICROSERVICE_PROMPT)
            String microservice,
            @McpToolParam(description = Constants.Prompts.INJECT_TOOL_AUTHOR_PROMPT)
            String author,
            @McpToolParam(description = Constants.Prompts.INJECT_TOOL_TIMESTAMP_PROMPT)
            String timestamp,
            @McpToolParam(description = Constants.Prompts.INJECT_TOOL_CLASSIFICATION_PROMPT)
            String classification
    ) {
        Map<String, Object> metaData = Map.of(
                "microservice", microservice,
                "author", author,
                "timestamp", timestamp,
                "classification", classification
        );
        injectorService.injectData(summary, metaData);
        return "Data is being pushed to Knowledge Base";
    }

    @McpTool(name = "search_knowledge_base", description = Constants.Prompts.SEARCH_TOOL_PROMPT)
    public List<KbResult> searchInKnowledgeBase(
            @McpToolParam(description = Constants.Prompts.SEARCH_TOOL_QUERY_PROMPT)
            KbSearchRequest searchRequest
    ) {
        return searchService.searchData(searchRequest);
    }
}