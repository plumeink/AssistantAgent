# Changelog

All notable changes to Assistant Agent will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial open source release preparation
- Comprehensive documentation

---

## [1.0.0] - YYYY-MM-DD

### Added

#### Core Features
- **Code-as-Action Execution Engine**: Agent generates and executes Python code via GraalVM sandbox
- **Secure Sandbox**: AI-generated code runs safely with resource isolation in GraalVM polyglot environment
- **CodeactTool System**: Unified tool interface supporting multiple tool types

#### Evaluation Module
- Multi-dimensional intent recognition through Evaluation Graph
- LLM-based and Rule-based evaluation engines
- Customizable evaluation criteria with dependency management
- Multiple result types: BOOLEAN, ENUM, SCORE, JSON, TEXT

#### Prompt Builder Module
- Dynamic prompt assembly based on evaluation results
- Priority-based PromptBuilder orchestration
- Support for system text prepend/append
- Non-invasive model interceptor integration

#### Experience Module
- Multi-type experience management (Code, ReAct, Common)
- In-memory experience storage with configurable TTL
- Experience retrieval and injection into prompts
- FastIntent quick response mechanism for familiar scenarios

#### Learning Module
- After-Agent learning: Extract experiences after agent execution
- After-Model learning: Extract experiences after model calls
- Tool Interceptor learning: Learn from tool executions
- Async learning execution support

#### Search Module
- Unified SearchProvider SPI for multiple data sources
- Support for Knowledge, Project, and Web search types
- Configurable result merging strategies
- Mock implementations for demonstration

#### Reply Module
- Multi-channel reply routing
- Configuration-driven reply tool generation
- ReplyChannelDefinition SPI for custom channels
- Built-in IDE text channel for demonstration

#### Trigger Module
- Cron-based scheduled triggers (TIME_CRON)
- One-time delayed triggers (TIME_ONCE)
- Callback event triggers (CALLBACK)
- Persistent trigger repository

#### Dynamic Tools Module
- MCP (Model Context Protocol) tool integration
- HTTP API tool integration via OpenAPI spec
- Dynamic tool registration and discovery

#### Auto Configuration
- Spring Boot auto-configuration support
- Sensible defaults for all modules
- Comprehensive configuration properties

### Configuration
- Full YAML configuration support
- Environment variable support for sensitive values
- Module-level enable/disable switches

### Documentation
- Chinese and English README
- Contributing guidelines
- Roadmap documentation
- Configuration reference

---

## Version History Summary

| Version | Date | Description |
|---------|------|-------------|
| 1.0.0 | TBD | Initial open source release |

---

## Migration Notes

### From Internal Versions

If you're migrating from internal versions of this project:

1. Review configuration property names - some may have changed
2. Implement required SPI interfaces (SearchProvider, etc.) for production use

---

## Deprecation Notices

None at this time.

