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
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelRequest;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelResponse;
import com.alibaba.assistant.agent.prompt.PromptBuilder;
import com.alibaba.assistant.agent.prompt.PromptContribution;
import com.alibaba.assistant.agent.prompt.PromptManager;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PromptInjectionInterceptorTest {

	@Test
	void interceptModel_shouldMergeSystemAndMessages() {
		PromptBuilder b = new PromptBuilder() {
			@Override
			public boolean match(ModelRequest request) {
				return request.getContext() != null && request.getContext().containsKey("input");
			}

			@Override
			public PromptContribution build(ModelRequest request) {
				return PromptContribution.builder()
						.systemTextToAppend("DYNAMIC")
						.prepend(new UserMessage("PRE"))
						.append(new UserMessage("POST"))
						.build();
			}
		};
		PromptManager manager = new PromptManager(List.of(b));
		PromptInjectionInterceptor interceptor = new PromptInjectionInterceptor(
				manager, PromptInjectionInterceptor.SystemTextMergeMode.APPEND);

		ModelRequest req = ModelRequest.builder()
				.context(Map.of("input", "input"))
				.systemMessage(new SystemMessage("S0"))
				.messages(List.of(new UserMessage("U0")))
				.build();

		AtomicReference<ModelRequest> captured = new AtomicReference<>();
		ModelCallHandler handler = r -> {
			captured.set(r);
			return ModelResponse.of(new AssistantMessage("ok"));
		};

		interceptor.interceptModel(req, handler);

		ModelRequest out = captured.get();
		assertNotNull(out);
		assertEquals("S0\n\nDYNAMIC", out.getSystemMessage().getText());
		assertEquals(List.of("PRE", "U0", "POST"), out.getMessages().stream().map(m -> m.getText()).toList());
	}

	@Test
	void interceptModel_whenNoBuilderMatches_shouldBypass() {
		PromptBuilder b = new PromptBuilder() {
			@Override
			public boolean match(ModelRequest request) {
				return false;
			}

			@Override
			public PromptContribution build(ModelRequest request) {
				return PromptContribution.builder()
						.systemTextToAppend("SHOULD_NOT_APPLY")
						.prepend(new UserMessage("SHOULD_NOT_APPLY"))
						.build();
			}
		};

		PromptManager manager = new PromptManager(List.of(b));
		PromptInjectionInterceptor interceptor = new PromptInjectionInterceptor(
				manager, PromptInjectionInterceptor.SystemTextMergeMode.APPEND);

		ModelRequest req = ModelRequest.builder()
				.context(Map.of("input", "ignored"))
				.systemMessage(new SystemMessage("S0"))
				.messages(List.of(new UserMessage("U0")))
				.build();

		AtomicReference<ModelRequest> captured = new AtomicReference<>();
		ModelCallHandler handler = r -> {
			captured.set(r);
			return ModelResponse.of(new AssistantMessage("ok"));
		};

		interceptor.interceptModel(req, handler);

		ModelRequest out = captured.get();
		assertNotNull(out);
		// bypass means request is passed through unmodified
		assertEquals("S0", out.getSystemMessage().getText());
		assertEquals(List.of("U0"), out.getMessages().stream().map(m -> m.getText()).toList());
	}

}


