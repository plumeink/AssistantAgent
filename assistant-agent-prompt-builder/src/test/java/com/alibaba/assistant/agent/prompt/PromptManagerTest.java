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
package com.alibaba.assistant.agent.prompt;

import com.alibaba.cloud.ai.graph.agent.interceptor.ModelRequest;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromptManagerTest {

	@Test
	void assemble_shouldApplyBuildersInPriorityOrder_andMergeSystemText() {
		PromptBuilder b1 = new PromptBuilder() {
			@Override
			public boolean match(ModelRequest request) {
				return true;
			}

			@Override
			public PromptContribution build(ModelRequest request) {
				return PromptContribution.builder().systemTextToAppend("B1").build();
			}

			@Override
			public int priority() {
				return 10;
			}
		};

		PromptBuilder b2 = new PromptBuilder() {
			@Override
			public boolean match(ModelRequest request) {
				return true;
			}

			@Override
			public PromptContribution build(ModelRequest request) {
				return PromptContribution.builder().systemTextToAppend("B2").build();
			}

			@Override
			public int priority() {
				return 0;
			}
		};

		PromptManager manager = new PromptManager(List.of(b1, b2));

		ModelRequest req = ModelRequest.builder()
				.context(Map.of())
				.messages(List.of(new UserMessage("hi")))
				.systemMessage(new SystemMessage("S0"))
				.build();

		PromptContribution c = manager.assemble(req);
		assertEquals("B2\n\nB1", c.systemTextToAppend());
	}

	@Test
	void assemble_shouldIgnoreSystemMessageInMessageLists() {
		PromptBuilder b = new PromptBuilder() {
			@Override
			public boolean match(ModelRequest request) {
				return true;
			}

			@Override
			public PromptContribution build(ModelRequest request) {
				return PromptContribution.builder()
						.prepend(new SystemMessage("SYS"))
						.append(new UserMessage("U"))
						.build();
			}
		};

		PromptManager manager = new PromptManager(List.of(b));

		ModelRequest req = ModelRequest.builder().context(Map.of()).messages(List.of()).build();
		PromptContribution c = manager.assemble(req);

		assertTrue(c.messagesToPrepend().isEmpty());
		assertEquals(1, c.messagesToAppend().size());
		assertEquals("U", c.messagesToAppend().get(0).getText());
	}

}


