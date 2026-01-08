package com.alibaba.assistant.agent.extension.experience.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ExperienceArtifact - 经验可执行产物（FastPath Intent 使用）
 *
 * <p>设计目标：
 * <ul>
 *     <li>REACT：可直接构造 AssistantMessage(toolCalls + 可选文本) 进入 tool 执行</li>
 *     <li>CODE：提供可注册/可执行的函数代码（跳过 code-generator LLM）</li>
 * </ul>
 */
public class ExperienceArtifact {

    private CodeArtifact code;

    private ReactArtifact react;

    public CodeArtifact getCode() {
        return code;
    }

    public void setCode(CodeArtifact code) {
        this.code = code;
    }

    public ReactArtifact getReact() {
        return react;
    }

    public void setReact(ReactArtifact react) {
        this.react = react;
    }

    public static class CodeArtifact {

        /**
         * e.g. python
         */
        private String language;

        private String functionName;

        private List<String> parameters = new ArrayList<>();

        /**
         * Full function code (should be parsable by RuntimeEnvironmentManager.extractFunctionName)
         */
        private String code;

        private String description;

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getFunctionName() {
            return functionName;
        }

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
        }

        public List<String> getParameters() {
            return parameters;
        }

        public void setParameters(List<String> parameters) {
            this.parameters = parameters != null ? parameters : new ArrayList<>();
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class ReactArtifact {

        /**
         * Optional assistant text shown to the model/user (align with AssistantMessage text + toolCalls)
         */
        private String assistantText;

        private ToolPlan plan;

        public String getAssistantText() {
            return assistantText;
        }

        public void setAssistantText(String assistantText) {
            this.assistantText = assistantText;
        }

        public ToolPlan getPlan() {
            return plan;
        }

        public void setPlan(ToolPlan plan) {
            this.plan = plan;
        }
    }

    public static class ToolPlan {

        private List<ToolCallSpec> toolCalls = new ArrayList<>();

        public List<ToolCallSpec> getToolCalls() {
            return toolCalls;
        }

        public void setToolCalls(List<ToolCallSpec> toolCalls) {
            this.toolCalls = toolCalls != null ? toolCalls : new ArrayList<>();
        }
    }

    public static class ToolCallSpec {

        private String toolName;

        /**
         * Arguments map, will be serialized to JSON string for AssistantMessage.ToolCall
         */
        private Map<String, Object> arguments;

        public String getToolName() {
            return toolName;
        }

        public void setToolName(String toolName) {
            this.toolName = toolName;
        }

        public Map<String, Object> getArguments() {
            return arguments;
        }

        public void setArguments(Map<String, Object> arguments) {
            this.arguments = arguments;
        }
    }
}


