/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.assistant.agent.prompt.interceptor;

import com.alibaba.cloud.ai.graph.agent.interceptor.ModelCallHandler;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelInterceptor;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelRequest;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelResponse;
import com.alibaba.assistant.agent.prompt.PromptContribution;
import com.alibaba.assistant.agent.prompt.PromptManager;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Injects dynamically assembled prompt/messages into {@link com.alibaba.cloud.ai.graph.agent.interceptor.ModelRequest} using {@link com.alibaba.assistant.agent.prompt.PromptManager}.
 */
public final class PromptInjectionInterceptor extends ModelInterceptor {

	private final PromptManager promptManager;

	private final SystemTextMergeMode systemTextMergeMode;

	public PromptInjectionInterceptor(PromptManager promptManager) {
		this(promptManager, SystemTextMergeMode.APPEND);
	}

	public PromptInjectionInterceptor(PromptManager promptManager, SystemTextMergeMode systemTextMergeMode) {
		this.promptManager = Objects.requireNonNull(promptManager, "promptManager must not be null");
		this.systemTextMergeMode = Objects.requireNonNull(systemTextMergeMode, "systemTextMergeMode must not be null");
	}

	@Override
	public ModelResponse interceptModel(ModelRequest request, ModelCallHandler handler) {
		PromptContribution contribution = promptManager.assemble(request);
		if (contribution == null || contribution.isEmpty()) {
			return handler.call(request);
		}

		SystemMessage mergedSystem = mergeSystemMessage(request.getSystemMessage(), contribution);
		List<Message> mergedMessages = mergeMessages(request.getMessages(), contribution);

		ModelRequest updated = ModelRequest.builder(request)
				.systemMessage(mergedSystem)
				.messages(mergedMessages)
				.build();

		return handler.call(updated);
	}

	@Override
	public String getName() {
		return "PromptInjection";
	}

	private SystemMessage mergeSystemMessage(SystemMessage original, PromptContribution contribution) {
		String prepend = contribution.systemTextToPrepend();
		String append = contribution.systemTextToAppend();

		String originalText = original != null ? original.getText() : null;
		String merged = originalText;

		if (systemTextMergeMode == SystemTextMergeMode.PREPEND) {
			merged = join(prepend, merged);
			merged = join(merged, append);
		}
		else {
			merged = join(merged, prepend);
			merged = join(merged, append);
		}

		if (merged == null || merged.isBlank()) {
			return null;
		}
		return new SystemMessage(merged);
	}

	private static List<Message> mergeMessages(List<Message> original, PromptContribution contribution) {
		List<Message> out = new ArrayList<>();
		if (contribution.messagesToPrepend() != null && !contribution.messagesToPrepend().isEmpty()) {
			out.addAll(contribution.messagesToPrepend());
		}
		if (original != null && !original.isEmpty()) {
			out.addAll(original);
		}
		if (contribution.messagesToAppend() != null && !contribution.messagesToAppend().isEmpty()) {
			out.addAll(contribution.messagesToAppend());
		}
		return out;
	}

	private static String join(String a, String b) {
		if (a == null || a.isBlank()) {
			return b;
		}
		if (b == null || b.isBlank()) {
			return a;
		}
		return a + "\n\n" + b;
	}

	public enum SystemTextMergeMode {
		PREPEND, APPEND
	}

}


