/*
 * Copyright 2024-2026 the original author or authors.
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
package agent.demo.agent;

import agent.demo.agent.tools.PythonTool;
import agent.demo.userinfo.tools.UserInfoTool;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.shelltool.ShellToolAgentHook;
import com.alibaba.cloud.ai.graph.agent.hook.skills.SkillsAgentHook;
import com.alibaba.cloud.ai.graph.agent.tools.ShellTool;
import com.alibaba.cloud.ai.graph.agent.extension.tools.filesystem.ReadFileTool;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.skills.registry.SkillRegistry;
import com.alibaba.cloud.ai.graph.skills.registry.filesystem.FileSystemSkillRegistry;

import org.springframework.ai.chat.model.ChatModel;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class ChatbotAgent {

	private static final String INSTRUCTION = """
			你叫小蓝，是我的个人助理.你有一些技能和工具，可以帮我完成对应的任务。
			""";

	@Bean
	public ReactAgent chatbotReactAgent(ChatModel chatModel,
			ToolCallback executeShellCommand,
			ToolCallback executePythonCode,
			ToolCallback viewTextFile,
			ToolCallback searchUsers,
			SkillRegistry skillRegistry,
			MemorySaver memorySaver) {
		
		SkillsAgentHook skillsHook = SkillsAgentHook.builder()
				.skillRegistry(skillRegistry)
				.build();
		
		ShellToolAgentHook shellHook = ShellToolAgentHook.builder()
				.shellToolName(executeShellCommand.getToolDefinition().name())
				.build();
		
		return ReactAgent.builder()
				.name("小蓝")
				.model(chatModel)
				.instruction(INSTRUCTION)
				.enableLogging(true)
				.saver(memorySaver)
				.hooks(List.of(skillsHook, shellHook))
				.tools(
						executeShellCommand,
						executePythonCode,
						viewTextFile,
						searchUsers
				)
				.build();
	}

	@Bean
	public MemorySaver memorySaver() {
		return new MemorySaver();
	}

	@Bean
	public SkillRegistry skillRegistry() {
		return FileSystemSkillRegistry.builder()
				.projectSkillsDirectory(System.getProperty("user.dir") + "/skills")
				.build();
	}

	// Tool: execute_shell_command
	@Bean
	public ToolCallback executeShellCommand() {
		String workspaceRoot = System.getProperty("user.dir");
		return ShellTool.builder(workspaceRoot)
				.withName("execute_shell_command")
				.withDescription("Execute a shell command inside a persistent session. Before running a command, " +
						"confirm the working directory is correct (e.g., inspect with `ls` or `pwd`) and ensure " +
						"any parent directories exist. Prefer absolute paths and quote paths containing spaces, " +
						"such as `cd \"/path/with spaces\"`. Chain multiple commands with `&&` or `;` instead of " +
						"embedding newlines. Avoid unnecessary `cd` usage unless explicitly required so the " +
						"session remains stable. Outputs may be truncated when they become very large, and long " +
						"running commands will be terminated once their configured timeout elapses.")
				.build();
	}

	// Tool: execute_python_code
	@Bean
	public ToolCallback executePythonCode() {
		return FunctionToolCallback.builder("execute_python_code", new PythonTool())
				.description(PythonTool.DESCRIPTION)
				.inputType(PythonTool.PythonRequest.class)
				.build();
	}

	// Tool: view_text_file
	@Bean
	public ToolCallback viewTextFile() {
		ReadFileTool readFileTool = new ReadFileTool();
		return FunctionToolCallback.builder("view_text_file", readFileTool)
				.description("View the contents of a text file. The file_path parameter must be an absolute path. " +
						"You can specify offset and limit to read specific portions of the file. " +
						"By default, reads up to 500 lines starting from the beginning of the file.")
				.inputType(ReadFileTool.ReadFileRequest.class)
				.build();
	}

	// Tool: search_users
	@Bean
	public ToolCallback searchUsers(UserInfoTool userInfoTool) {
		return FunctionToolCallback.builder("search_users", userInfoTool)
				.description("查询用户信息。支持中文姓名、拼音、首字母等多种查询方式。例如：查询'张三'、'zhangsan'、'zs'都可以找到对应的用户。")
				.inputType(UserInfoTool.SearchRequest.class)
				.build();
	}
}
