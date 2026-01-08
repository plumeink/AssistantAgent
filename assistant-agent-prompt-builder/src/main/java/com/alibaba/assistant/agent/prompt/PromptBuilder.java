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

/**
 * General interface for dynamically assembling prompt/messages based on a runtime input.
 * <p>
 * Implementations typically:
 * <ul>
 *   <li>inspect the incoming {@link com.alibaba.cloud.ai.graph.agent.interceptor.ModelRequest} (messages/system/context/options/tools)</li>
 *   <li>return a {@link PromptContribution} to be merged into the {@link com.alibaba.cloud.ai.graph.agent.interceptor.ModelRequest}</li>
 * </ul>
 */
public interface PromptBuilder {

	/**
	 * Whether this builder should contribute for the given {@link com.alibaba.cloud.ai.graph.agent.interceptor.ModelRequest}.
	 */
	boolean match(ModelRequest request);

	/**
	 * Build the prompt contribution. Only called when {@link #match(com.alibaba.cloud.ai.graph.agent.interceptor.ModelRequest)} is true.
	 */
	PromptContribution build(ModelRequest request);

	/**
	 * Priority for ordering multiple builders. Lower values run earlier.
	 */
	default int priority() {
		return 0;
	}

}


