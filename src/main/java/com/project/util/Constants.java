package com.project.util;

public class Constants {

    public static final String KAFKA_RETRY_INJECTION_TOPIC = "INJECTION_RETRY";

    public static class Prompts {
        // =========================================================================
        // Search Tool Descriptions & Parameters
        // =========================================================================

        public static final String SEARCH_TOOL_PROMPT =
                "Searches the team's shared knowledge base for prior technical findings, postmortems, or analysis notes relevant to a natural-language query. Use this before starting a new investigation to check whether a similar issue has already been analyzed by a teammate.";

        public static final String SEARCH_TOOL_QUERY_PROMPT =
                "Natural-language query describing the technical issue or context, with optional filters such as result limit or minimum relevance score.";

        // =========================================================================
        // Inject Tool Descriptions & Parameters
        // =========================================================================

        public static final String INJECT_TOOL_PROMPT =
                "Saves a technical summary, postmortem, or investigation finding into the team's shared knowledge base. Use this after completing a code analysis, debugging session, or investigation so findings are searchable by teammates working on related issues. Existing similar entries will automatically merge with this finding.";

        public static final String INJECT_TOOL_SUMMARY_PROMPT =
                "The technical summary text to store. Must describe what was analyzed, key findings, root causes, and any fixes or recommendations.";

        public static final String INJECT_TOOL_MICROSERVICE_PROMPT =
                "Name of the microservice this finding relates to (e.g., 'ticket-service').";

        public static final String INJECT_TOOL_AUTHOR_PROMPT =
                "Name of the person, team, or agent who generated this finding.";

        public static final String INJECT_TOOL_TIMESTAMP_PROMPT =
                "Timestamp when the finding was produced, formatted as 'YYYY-MM-DD HH:mm:ss'.";

        // =========================================================================
        // Summary Classification Prompts
        // =========================================================================

        public static final String INJECT_TOOL_CLASSIFICATION_PROMPT =
                "Analyze the provided technical summary and classify its primary intent into exactly one of these three categories: " +
                        "1) 'ISSUE' (bug fixes, postmortems, outages, or runtime error investigations), " +
                        "2) 'DEVELOPMENT' (new features, new services, or API additions), " +
                        "3) 'REFACTOR' (code cleanups, performance optimizations, restructuring, or dependency updates). " +
                        "4) 'SEARCH' (code search, searching for a particular topic in the microservice)." +
                        "Respond ONLY with one of the exact category words: ISSUE, DEVELOPMENT, or REFACTOR. Do not include any extra text, punctuation, or markdown formatting.";

        // =========================================================================
        // LLM Synthesis & Editing Prompts
        // =========================================================================

        public static final String INJECT_SUMMARY_PROMPT =
                "You are an expert technical editor. You will receive input text containing multiple concatenated document summaries. Your task is to merge them into a single, cohesive, unified summary. Eliminate duplicate or overlapping information while preserving all distinct technical details, metrics, edge cases, and specific terminology from every input piece. Expand the output length as needed to capture all unique technical points without losing detail. Present the final result purely as a logically structured plain text narrative without any markdown formatting (do not use asterisks, headers, bold styling, or bullet symbols).";

        public static final String SEARCH_SUMMARY_PROMPT =
                "You are an expert technical editor. You will receive a JSON array of objects, where each object contains 'content', 'author', and 'microservice' fields representing prior findings. Your task is to merge them into a single JSON object with the exact keys: 'content', 'author', and 'microservice'. For 'content', craft a unified plain-text summary that eliminates duplicate information while preserving all distinct technical details, metrics, edge cases, and terminology (do not use markdown formatting like asterisks, headers, or bullet symbols, double quotes). For 'author', combine all unique input authors into a single comma-separated string. For 'microservice', combine all unique input microservices into a single comma-separated string. Output strictly valid raw JSON without backticks, markdown code blocks, or preamble.";
    }
}
