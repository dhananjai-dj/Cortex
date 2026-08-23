package com.project.service;

import com.project.constants.Prompts;
import org.springframework.ai.chat.client.ChatClient;
import com.project.dto.KbResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SummaryService {
    private final Logger logger = LoggerFactory.getLogger(SummaryService.class);

    private final DeleteService deleteService;
    private final ChatClient chatClient;

    public SummaryService(ChatClient chatClient, DeleteService deleteService) {
        this.chatClient = chatClient;
        this.deleteService = deleteService;
    }

    public String generateCombinedSummary(String summary, List<String> deleteDocumentIds) {
        try {
            String generatedSummary = chatClient.prompt().system(Prompts.INJECT_SUMMARY_PROMPT).user(summary).call().content();
            if (generatedSummary != null && !generatedSummary.equalsIgnoreCase(summary) && !deleteDocumentIds.isEmpty()) {
                deleteService.deleteData(deleteDocumentIds);
            }
            return generatedSummary;
        } catch (Exception e) {
            logger.error("Error in generating new summary returning the entire summary");
        }
        return summary;
    }

    public List<KbResult> generateResult(List<KbResult> resultList) {
        try {
            String summary = resultList.stream().map(KbResult::toString).collect(Collectors.joining());
            String jsonResponse = chatClient.prompt().system(Prompts.SEARCH_SUMMARY_PROMPT).user(summary).call().content();
            return List.of(KbResult.parse(jsonResponse));
        } catch (Exception e) {
            logger.error("Error in generating result summary", e);
        }
        return resultList;
    }


}
