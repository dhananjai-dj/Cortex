package com.project.constants;

public class Prompts {

    public static final String SEARCH_TOOL_PROMPT = "Searches the team's shared knowledge base for prior technical findings, postmortems, or analysis notes relevant to a natural-language query. Use this before starting a new investigation to check whether a similar issue has already been analyzed by a teammate.";

    public static final String INJECT_TOOL_PROMPT = "Saves a technical summary, postmortem, or analysis finding into the team's shared knowledge base. Use this after completing a code analysis, debugging session, or investigation in a microservice, so the finding becomes searchable for other teammates working on related issues later. If a highly similar entry already exists, it will be automatically merged with this new one rather than duplicated.";

    public static final String INJECT_SUMMARY_PROMPT = "You are an expert technical editor. You will receive input text containing multiple concatenated document summaries; your task is to merge them into a single, cohesive, unified summary. Eliminate duplicate or overlapping information while preserving all distinct technical details, metrics, edge cases, and specific terminology from every input piece. Expand the output length as needed to cover all unique technical points without sacrificing detail, and present the final result as a logically structured plain text narrative without any markdown formatting such as asterisks, headers, or bullet symbols.";

    public static final String INJECT_TOOL_SUMMARY_PROMPT = "The technical summary text to store — should describe what was analyzed, what was found, and any fix or recommendation.";

    public static final String INJECT_TOOL_MICROSERVICE_PROMPT = "Name of the microservice this finding relates to, e.g. 'ticket-service'.";

    public static final String INJECT_TOOL_AUTHOR_PROMPT = "Name of the person or agent who produced this finding.";

    public static final String SEARCH_TOOL_QUERY_PROMPT = "Search request containing the natural-language query and optional filters (e.g. result limit, minimum relevance score).";

    public static final String SEARCH_SUMMARY_PROMPT = "You are an expert technical editor. You will receive a JSON list of objects containing content, author, and microservice fields representing multiple document summaries; your task is to merge them into a single summary object in the exact same JSON format with keys content, author, and microservice. For content, create a cohesive, unified summary that eliminates duplicate information while preserving all distinct technical details, metrics, edge cases, and specific terminology from every input piece without any markdown formatting like asterisks, headers, or bullet symbols. For author, combine all unique input authors into a single comma-separated string. For microservice, combine all unique input microservices into a single comma-separated string. Respond ONLY with the valid JSON object and no additional text. Do not enclose the response in markdown code blocks or backticks.";
}
