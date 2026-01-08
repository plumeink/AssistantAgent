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

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Orchestrates multiple {@link PromptBuilder}s and merges their contributions.
 */
public final class PromptManager {

	private final List<PromptBuilder> builders;

	public PromptManager(List<PromptBuilder> builders) {
		List<PromptBuilder> copy = builders != null ? new ArrayList<>(builders) : new ArrayList<>();
		copy.sort(Comparator.comparingInt(PromptBuilder::priority));
		this.builders = List.copyOf(copy);
	}

	public List<PromptBuilder> builders() {
		return builders;
	}

	public PromptContribution assemble(ModelRequest request) {
		Objects.requireNonNull(request, "request must not be null");
		if (builders.isEmpty()) {
			return PromptContribution.empty();
		}

		String systemPrepend = null;
		String systemAppend = null;
		List<Message> prepend = new ArrayList<>();
		List<Message> append = new ArrayList<>();

		for (PromptBuilder builder : builders) {
			if (!builder.match(request)) {
				continue;
			}
			PromptContribution c = builder.build(request);
			if (c == null || c.isEmpty()) {
				continue;
			}

			// Merge system text in builder priority order
			systemPrepend = concatSystemText(systemPrepend, c.systemTextToPrepend());
			systemAppend = concatSystemText(systemAppend, c.systemTextToAppend());

			// Normalize SystemMessage in message lists into systemAppend by default
			normalizeAndAdd(prepend, c.messagesToPrepend());
			normalizeAndAdd(append, c.messagesToAppend());
		}

		return PromptContribution.builder()
				.systemTextToPrepend(systemPrepend)
				.systemTextToAppend(systemAppend)
				.prependAll(prepend)
				.appendAll(append)
				.build();
	}

	private static String concatSystemText(String existing, String next) {
		if (next == null || next.isBlank()) {
			return existing;
		}
		if (existing == null || existing.isBlank()) {
			return next;
		}
		return existing + "\n\n" + next;
	}

	private static void normalizeAndAdd(List<Message> out, List<Message> in) {
		if (in == null || in.isEmpty()) {
			return;
		}
		for (Message message : in) {
			if (message == null) {
				continue;
			}
			if (message instanceof SystemMessage) {
				// Avoid producing multiple SystemMessages in the outgoing message list.
				// Callers should contribute system text via PromptContribution.systemTextTo*
				// instead of returning SystemMessage in message lists.
				continue;
			}
			out.add(message);
		}
	}

}


