# Changelog

All notable changes to this project will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.16.0-223] - 2025-01-28

### Added

- DeepSeek V3 and R1 models (CodeGPT, Ollama and llama.cpp)
- New 'Include Current Changes' chat action

### Improved

- Chat text input actions UI/UX

## [2.15.2-223] - 2025-01-13

### Added

- Example code assistant guideline in the prompt.
- Support for opening all code assistant changes in full mode.
- Additional commit dialog actions:
    - Generate Message (no functional changes).
    - Generate Message with Additional Input: Adds support for additional input in the prompt.
    - Review Changes: Creates a new chat session and asks CodeGPT to review your changes and suggest improvements.

### Fixed

- Inconsistencies in the model combobox title when changing between tabs [#814](https://github.com/carlrobertoh/CodeGPT/issues/814).

## [2.15.1-223] - 2025-01-07

### Added

- Code Assistant predictions for Paste action
- Prioritization and ordering of predictions closest to cursor position

### Fixed

- Code Assistant custom accept shortcut and missing keymap labels

## [2.15.0-223] - 2024-12-30

### Added

- Code Assistant: A new feature that suggests edits throughout the file [[Read More](https://www.codegpt.ee/blog/introducing-code-assistant)]
- 'Include Open Files' chat action item [#796](https://github.com/carlrobertoh/CodeGPT/issues/796)

## [2.14.3-223] - 2024-12-17

### Added

- Support for copying user and response messages in their original format [#791](https://github.com/carlrobertoh/CodeGPT/issues/791)
- Right-click popup menu for response messages

### Fixed

- Escaped code response characters [#169](https://github.com/carlrobertoh/CodeGPT/issues/169), [#437](https://github.com/carlrobertoh/CodeGPT/issues/437)
- Model value change for empty conversations
- Order of Custom OpenAI applied settings [#797](https://github.com/carlrobertoh/CodeGPT/issues/797)

## [2.14.1-223] - 2024-12-06

### Fixed

- Response streaming for o1 models
- IDE error caused by credential validation in EDT
- Submission handling when current response is ongoing

## [2.14.0-223] - 2024-12-04

### Added

- Recent Qwen 2.5 Coder models (llama.cpp)
- Codestral as default code model (CodeGPT)

### Fixed

- NPE caused by lookup element sorter

### Removed

- StarCoder 7B model (CodeGPT)

## [2.13.0-223] - 2024-11-25

### Added

- Single standalone view for prompt configuration
- Codestral model for code completion (CodeGPT)

### Fixed

- Git diff logic for commit message generation

## [2.12.5-223] - 2024-11-18

### Added

- Option to explain commits from VCS log tree [#688](https://github.com/carlrobertoh/CodeGPT/issues/688)

### Fixed

- Chat response streaming for messages received faster than polling rate [#757](https://github.com/carlrobertoh/CodeGPT/issues/757)

### Improved

- Default CodeGPT prompt instructions

## [2.12.4-223] - 2024-11-14

### Added

- Qwen 2.5 Coder model for chat and code completion (CodeGPT)

### Fixed

- Image popup notification condition fix [#711](https://github.com/carlrobertoh/CodeGPT/issues/711)

## [2.12.3-223] - 2024-11-07

### Fixed

- Files not persisting between messages in same session

## [2.12.2-223] - 2024-11-04

### Fixed

- Ollama chat completion streaming [#744](https://github.com/carlrobertoh/CodeGPT/issues/744)
- Non-stream chat completions error handling (CodeGPT, OpenAI) [#746](https://github.com/carlrobertoh/CodeGPT/issues/746)
- Prompt text component height issues [#747](https://github.com/carlrobertoh/CodeGPT/issues/747)
- Endless loading when persona name or instructions are null [#748](https://github.com/carlrobertoh/CodeGPT/issues/748)

## [2.12.1-223] - 2024-11-01

### Added

- Gemini 1.5 Pro model (CodeGPT)

### Fixed

- Code completion state when session contains active completion

### Removed

- StarCoder 16B model (CodeGPT)

## [2.12.0-223] - 2024-10-29

### Added

- 'Auto Apply' feature for instant AI-suggested code updates [#743](https://github.com/carlrobertoh/CodeGPT/issues/743) (CodeGPT)

### Fixed

- Code completion enabled/disabled state
- Total token calculation for highlighted texts

## [2.11.7-223] - 2024-10-18

### Fixed

- Edit Code request building [#737](https://github.com/carlrobertoh/CodeGPT/issues/737) (OpenAI)
- Chat UI performance issues
- Minor issues related to git repository lookup

## [2.11.6-223] - 2024-10-15

### Fixed

- High CPU usage [#716](https://github.com/carlrobertoh/CodeGPT/issues/716)
- NPE on suggestion actions when no files are opened in editor [#725](https://github.com/carlrobertoh/CodeGPT/issues/725)
- Inconsistent text rendering in the chat tool window

## [2.11.5-223] - 2024-10-10

### Added

- Support high context limits (up to 200k tokens) via RAG when chatting with files (CodeGPT)
- Gemini 1.5 Pro model (Google)
- Legacy response support for text completions in code completions (Custom OpenAI)

### Fixed

- Chat UI threading issues [#730](https://github.com/carlrobertoh/CodeGPT/issues/730)

## [2.11.4-223] - 2024-10-01

### Added

- Qwen 2.5 (72B) and o1 models (CodeGPT, OpenAI)
- Free limited access to SOTA models (CodeGPT)

## [2.11.3-223] - 2024-09-25

### Fixed

- Anthropic (Claude) chat requests [#707](https://github.com/carlrobertoh/CodeGPT/issues/707)

## [2.11.2-223] - 2024-09-24

### Added

- Edit Code and Name Suggestions feature support for all providers
- Qwen 2.5 Coder models and prompt templates

### Fixed

- Model popup submenu auto-hover [#681](https://github.com/carlrobertoh/CodeGPT/issues/681)
- Prompt text field newline creation [#694](https://github.com/carlrobertoh/CodeGPT/issues/694)
- Prompt text field theme issues [#701](https://github.com/carlrobertoh/CodeGPT/issues/701)

## [2.11.1-223] - 2024-09-12

### Added

- Git history referencing in prompts
- Quick code snippet referencing in prompts [#690](https://github.com/carlrobertoh/CodeGPT/issues/690)
- Name suggestions for languages other than Java [#615](https://github.com/carlrobertoh/CodeGPT/issues/615)

### Fixed

- Prompt text field persona suggestions [#685](https://github.com/carlrobertoh/CodeGPT/issues/685)
- Text field autofocus when opening the chat toolwindow [#691](https://github.com/carlrobertoh/CodeGPT/issues/691)
- Git commit dialog selected changes not being included in diff
- Ollama server host configuration overriding [#684](https://github.com/carlrobertoh/CodeGPT/issues/684)

### Improved

- Model combobox menu UI/UX by extracting the providers into standalone subgroups [#681](https://github.com/carlrobertoh/CodeGPT/pull/681)
- Prompt text field inlay rendering

## [2.11.0-223] - 2024-09-05

### Added

- Multiple tool window editor actions for generated code:
  - Direct apply: Directly replaces the highlighted code in the main editor with the generated code
  - Compare with Original: Opens a diff view to compare the generated code with the highlighted code
  - Insert at Caret: Inserts the generated code at the exact location of the caret in the main editor
- Vision support for Azure models
- General improvements to code completions, including:
    - Proper streaming support
    - Git context integration

### Fixed

- Prompt text field background transparency issues
- OpenRouter default template for custom OpenAI provider
- Ollama streaming requests

## [2.10.2-223] - 2024-08-23

### Added

- Progress display for web docs

### Fixed

- Code completion prompt creation from placeholders
- Prompt text field inlay offset and up/down key events

## [2.10.1-223] - 2024-08-20

### Fixed

- Codestral Infill templates for code completion in llama.cpp [#667](https://github.com/carlrobertoh/CodeGPT/pull/667)

## [2.10.0-223] - 2024-08-19

### Added

- Web documentation support [#650](https://github.com/carlrobertoh/CodeGPT/pull/650)
- Improved user prompt text field component [#665](https://github.com/carlrobertoh/CodeGPT/pull/665)
    - Spellchecking
    - Auto-enclosing brackets, quotes, etc.
    - Removable suggestion action inlays
    - Context menu for copy-pasting, and more
- DeepSeek Coder V2 model for CodeGPT users
- Image support for Custom OpenAI-compatible providers

### Fixed

- IDE freezes caused by long running file search queries [#652](https://github.com/carlrobertoh/CodeGPT/pull/652)

## [2.9.0-223] - 2024-08-03

### Added

- **Web search support** - available only through the CodeGPT provider [#641](https://github.com/carlrobertoh/CodeGPT/pull/641)
- **Customizable Personas** - a quick way to define and switch between different system prompts. Initial release comes with over 600 pre-defined prompts. [#638](https://github.com/carlrobertoh/CodeGPT/pull/638)

### Other Improvements

- The textbox suggestions popover now offers better usability, making it easier and quicker to add additional context to your session for more accurate responses.
- Support for attaching folders, allowing all files within the folder to be added to your current chat session.
- Optimized the chat user interface for better performance. We noticed that for faster models, the inference speed didn't match what was being rendered on the screen.

## [2.8.5-223] - 2024-07-24

### Added

- **Llama 3.1 405B** model for CodeGPT Individual plan users

### Fixed

- Wrong line separators exception during code completion [(#637)](https://github.com/carlrobertoh/CodeGPT/issues/637)

## [2.8.4-223] - 2024-07-19

### Added

- GPT-4o mini model (**free** with [our hosted service](https://www.codegpt.ee/#pricing))
- New UI/UX design for the toolwindow's user input component
- Support for searching for and attaching files in the textarea popover when the '@' key is used

### Fixed

- Numeric values in Custom providers' headers section ([#619](https://github.com/carlrobertoh/CodeGPT/issues/619))
- Ollama settings sync ([#616](https://github.com/carlrobertoh/CodeGPT/issues/616))

## [2.8.3-223] - 2024-07-15

### Added

- Enter key shortcut for Fast Edit submission (#602)

### Fixed

- Ollama form combobox state (#606)
- Replace code behaviour (#614)
- Wrong line separators on Windows (#605)

## [2.8.2-233] - 2024-07-01

### Added

- Fast Code Edits [#601](https://github.com/carlrobertoh/CodeGPT/pull/601)

### Fixed

- Chat toolwindow autoscroller issues
- Wrong line separators on Windows machine

### Removed

- You.com provider support

## [2.8.1-223] - 2024-06-21

### Added

- Claude 3.5 Sonnet model (CodeGPT)
- Additional `maxTokens`, `temperature` and `apiKey` fields (Ollama)

### Fixed

- Model re-selection after refresh (Ollama)
- Commit message placeholders

## [2.8.0-223] - 2024-06-06

### Added

- OpenAI and Anthropic models for CodeGPT subscribed users
- Support Stable Code Instruct 3B (#552)
- Support Phi-3 Medium 128K (#577)
- Support Codestral 22B 32K (#587)
- Add field for environment variables for Llama server (#550)
- Add OpenRouter service template (#581)

### Fixed

- Remove trailing slashes from URL text fields (#579)
- Disable code completions by default

## [2.7.1-223] - 2024-05-15

### Fixed

- Backward compatibility issues on plugin updates (#551)

### Added

- Display a model name when starting the llama.cpp server (#546)

## [2.7.0-223] - 2024-05-14

### Added

- Three new providers - **Ollama**, **Google** and **CodeGPT** 🚀🚀🚀
- OpenAI GPT-4o model (#547)
- New provider configuration user interface (#538)
- Support Phi-3 Mini, CodeGemma and CodeQwen1.5 models (#516, #524, #525, #527)
- Llama 3 download sizes (#498)
- Mistral AI service template (#532)
- Support for starting/stopping llama.cpp server from statusbar (#544)

### Fixed

- Commit message generation for Custom OpenAI services (#496)
- NPE when using unsupported model for code completions (#499)
- Editor Actions configuration UI issues (#518)
- IDE error caused by Git4Idea dependency (#526)
- Custom Service test connection (#531)
- UI issues around llama.cpp provider configuration (#543, #529)

### Removed

- `max_tokens` configuration for code completions (will be handled internally)

## [2.6.3-223] - 2024-04-22

### Added

- Support for Llama 3 model via llama.cpp port (#479)
- Code completion for "Custom OpenAI Service" (#476)
- Support for configuring llama.cpp server build parameters (#481)
- "Include file in context" to editor context menu (#475)
- Support for placeholders in the commit message system prompt (#458)

### Fixed

- High CPU usage during new files check (#474)
- Persistence of credentials back into the PasswordSafe (#465)

## [2.6.2-222] - 2024-04-15

### Fixed

- Text rendering anomalies upon streaming

## [2.6.1-222] - 2024-04-12

### Fixed

- EncodingManager error handling for invalid inputs (#444)
- Initial default values for `checkForPluginUpdatesCheckBox` and `checkForNewScreenshotsCheckBox` (#446)
- Random IDE crashes caused by tree-sitter (#452, #446)
- Azure base url creation (#449)

## [2.6.0-222] - 2024-04-10

### Added

- Vision support (image understanding) for OpenAI GPT-4 and Anthropic Claude models
- Total token panel for all providers
- Autofocus for UserTextArea when the tool window is visible

### Fixed

- Git commit message generation
- Error when adding a single file to the context
- Several IntelliJ platform warnings

### Removed

- Azure custom configuration (use OpenAI-compatible service to override the default configuration)

## [2.5.1] - 2024-03-14

### Added

- Support for You Pro modes
- Basic post-processing for code completions
- Code completion feature toggle keyboard-shortcut
- Support for git commit message generation with Custom OpenAI and Anthropic services

### Fixed

- Several IntelliJ platform warnings

### Changed

- Supported minimum IDE build from 213 to 222

## [2.5.0] - 2024-03-06

### Added

- Support for Anthropic Claude service

### Fixed

- `IndexOutOfBoundsException` for Azure completions

## [2.4.0] - 2024-02-26

### Added

- Support for custom OpenAI-compatible service (#383)
- Llama cpp for generation of git commit message (#380)
- Remote server settings for Windows + Mixtral Instruct template (#378)

### Fixed

- Proxy settings for azure client (#382)

## [2.3.1] - 2024-02-19

### Added

- Support for custom OpenAI model configuration (#250)

### Fixed

- General Settings isModified state
- Azure service credential condition (#375)
- Caret offset location upon document changes (#367)

## [2.3.0] - 2024-02-14

### Added

- Support for autocompletion (disabled by default)
- Support for auto resolving compilation errors

### Replaced

- OpenAI chat models with the most recent ones
- CodeGPT header key

## [2.2.12] - 2023-12-21

### Added

- Support for extended request parameters for llama.cpp

## [2.2.11] - 2023-12-14

### Added

- Support for chatting with multiple files
- Support for generating commit messages and method names with Azure service

## [2.2.10] - 2023-12-12

### Added

- Configurable commit-message system prompt

### Fixed

- Redundant chat tab creation on provider/model change
- Azure bas host and path overriding

## [2.2.9] - 2023-12-04

### Added

- IDE notification on new plugin versions

### Fixed

- LLaMA settings state on server failure

### Improved

- LLaMA server logging

## [2.2.8] - 2023-12-01

### Fixed

- UI concurrency issues (run completion events on EDT)

## [2.2.7] - 2023-11-30

### Fixed

- LLaMA server boot up when additional params are not set

## [2.2.6] - 2023-11-27

### Added

- Support for automatic code formatting (#262)
- Support for additional command-line params for the llama server startup process
- Support for changing between different provides/models in the chat window (#227)
- Deepseek coder instruct models

### Fixed

- Git commit message generation based on the actual user selected/checked files (#291)

### Improved

- Toolwindow Chat UI

## [2.2.5] - 2023-11-23

### Added

- Option to set the number of threads for local LLM models

### Fixed

- Tool window chat editor enabled/disabled logic
- Tool window text pane caret visibility
- Editor actions for `createNewChatOnEachAction` state
- Plugin from crashing when the LLaMA server process is stuck

## [2.2.4] - 2023-11-20

### Added

- Support for git commit message generation
- Support for method/function name lookup generation

### Fixed

- Llama client read/connect timeout configuration

## [2.2.3] - 2023-11-15

### Added

- Expand/Collapse logic for tool window editors
- Interactive total token count label

### Removed

- Azure model selection (redundant field)

## [2.2.2] - 2023-11-07

### Added

- OpenAI GPT-4 Turbo and the latest GPT-3.5 models

### Fixed

- Credential validation when using llama service

## [2.2.1] - 2023-11-06

### Fixed

- Right click context menu (#251)
- Azure path (#232)
- Llama telemetry service value
- Include all feature builds by removing the value (#242)

## [2.2.0] - 2023-11-03

### Added

- Support for running local LLMs via LLaMA C/C++ port (#249)
- Support for fetching You.com subscription

### Fixed

- Project build for Windows ARM64

### Improved

- You.com coupon design
- Overall UI design

## [2.1.7] - 2023-10-26

### Replaced

- You.com GPT-4 toggle component with checkbox
- Telemetry segment service with rudderstack

### Fixed

- You.com web search not being displayed regardless of the flag

## [2.1.6] - 2023-10-12

### Added

- Default destination value for toolwindow editor save file action
- Suggestion to switch to different LLM provider on quota exceeded error
- Support for You.com GPT-4 model

### Fixed

- The style of wrapping used if the text area is wrapping lines
- Memory leaks (correct disposals for Configurable classes)

## [2.1.5] - 2023-10-02

### Added

- Support for diffing generated code

## [2.1.4] - 2023-10-01

### Added

- Support for modifying generated code within the chat toolwindow
- Support for creating new files directly from the chat toolwindow 

### Fixed

- Privacy policy url in the settings

## [2.1.3] - 2023-09-28

### Added

- More telemetry actions

### Fixed

- Error logging

## [2.1.2] - 2023-09-28

### Fixed

- Incorrect window selection during chat or toolwindow actions

## [2.1.1] - 2023-09-27

### Added

- Telemetry based on redhat impl

## [2.1.0] - 2023-09-21

### Added

- You API integration
- Ability to override completion path

### Updated

- Chat History UI

## [2.0.6] - 2023-08-29

### Removed

- Functionality to fetch/use OpenAI account name

### Fixed

- Rendering user input's newlines

### Added

- Support for closing other tabs (#172)
- Support for configuring custom hosts for OpenAI and Azure services

## [2.0.5] - 2023-06-12

### Fixed

- Tool window not opening on editor actions (#157)

### Added

- Support for changing the editor action behaviour (#157)
- Support for overriding completions request parameters (#152)
- User text area autofocus on creating a new chat (#155)
- Keymap tool window actions (#148)

## [2.0.4] - 2023-05-27

### Fixed

- TypeScript and C# code highlighting

- ToolWindow usability when virtual space option turned ON (#125) 

### Added

- ToolWindow code editor copy/replace header actions
- Custom prompt main editor action (#144)
- Support for 2023.2 EAP builds (#149)

## [2.0.3] - 2023-05-18

### Fixed

- Empty editor context menu item text (#137)
- Temp file path resolving (#130)

### Improved

- Response streaming

### Added

- Reset chat window toolbar action (#138)

## [2.0.2] - 2023-05-16

### Fixed

- Settings deserialization error

### Improved

- Memory consumption by disposing unused editors which are no longer needed

### Removed

- Main editor focus stealing on response streaming

## [2.0.1] - 2023-05-14

### Added

- New GPT-3.5-16k model 

### Fixed

- NPE when `displayName` couldn't be fetched

### Improved

- Proxy support by disabling the default trustmanager

### Removed

- Off-screen rendering setting option
- Automatic textarea focus on stream completion (#126)

## [2.0.0] - 2023-05-03

### Added

- Automatic retry logic on stream timeouts

### Improved

- Input prompt text field UI/UX (height grows with the content)

### Removed

- Custom prompt editor action (users can now provide custom actions within the chat window itself)

### Replaced

- ToolWindow HTML content with native Swing components

### Secured

- `OPENAI_API_KEY` persistence, key is saved in the OS password safe from now on

[Unreleased]: https://github.com/carlrobertoh/CodeGPT/compare/v2.16.0-223...HEAD
[2.16.0-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.15.2-223...v2.16.0-223
[2.15.2-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.15.1-223...v2.15.2-223
[2.15.1-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.15.0-223...v2.15.1-223
[2.15.0-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.14.3-223...v2.15.0-223
[2.14.3-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.14.1-223...v2.14.3-223
[2.14.1-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.14.0-223...v2.14.1-223
[2.14.0-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.13.0-223...v2.14.0-223
[2.13.0-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.12.5-223...v2.13.0-223
[2.12.5-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.12.4-223...v2.12.5-223
[2.12.4-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.12.3-223...v2.12.4-223
[2.12.3-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.12.2-223...v2.12.3-223
[2.12.2-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.12.1-223...v2.12.2-223
[2.12.1-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.12.0-223...v2.12.1-223
[2.12.0-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.11.7-223...v2.12.0-223
[2.11.7-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.11.6-223...v2.11.7-223
[2.11.6-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.11.5-223...v2.11.6-223
[2.11.5-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.11.4-223...v2.11.5-223
[2.11.4-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.11.3-223...v2.11.4-223
[2.11.3-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.11.2-223...v2.11.3-223
[2.11.2-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.11.1-223...v2.11.2-223
[2.11.1-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.11.0-223...v2.11.1-223
[2.11.0-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.10.2-223...v2.11.0-223
[2.10.2-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.10.1-223...v2.10.2-223
[2.10.1-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.10.0-223...v2.10.1-223
[2.10.0-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.9.0-223...v2.10.0-223
[2.9.0-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.8.5-223...v2.9.0-223
[2.8.5-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.8.4-223...v2.8.5-223
[2.8.4-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.8.3-223...v2.8.4-223
[2.8.3-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.8.2-233...v2.8.3-223
[2.8.2-233]: https://github.com/carlrobertoh/CodeGPT/compare/v2.8.1-223...v2.8.2-233
[2.8.2-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.8.1-223...v2.8.2-223
[2.8.1-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.8.0-223...v2.8.1-223
[2.8.0-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.7.1-223...v2.8.0-223
[2.7.1-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.7.0-223...v2.7.1-223
[2.7.0-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.6.3-223...v2.7.0-223
[2.6.3-223]: https://github.com/carlrobertoh/CodeGPT/compare/v2.6.2-222...v2.6.3-223
[2.6.2-222]: https://github.com/carlrobertoh/CodeGPT/compare/v2.6.1-222...v2.6.2-222
[2.6.1-222]: https://github.com/carlrobertoh/CodeGPT/compare/v2.6.0-222...v2.6.1-222
[2.6.0-222]: https://github.com/carlrobertoh/CodeGPT/compare/v2.5.1...v2.6.0-222
[2.5.1]: https://github.com/carlrobertoh/CodeGPT/compare/v2.5.0...v2.5.1
[2.5.0]: https://github.com/carlrobertoh/CodeGPT/compare/v2.4.0...v2.5.0
[2.4.0]: https://github.com/carlrobertoh/CodeGPT/compare/v2.3.1...v2.4.0
[2.3.1]: https://github.com/carlrobertoh/CodeGPT/compare/v2.3.0...v2.3.1
[2.3.0]: https://github.com/carlrobertoh/CodeGPT/compare/v2.2.12...v2.3.0
[2.2.12]: https://github.com/carlrobertoh/CodeGPT/compare/v2.2.11...v2.2.12
[2.2.11]: https://github.com/carlrobertoh/CodeGPT/compare/v2.2.10...v2.2.11
[2.2.10]: https://github.com/carlrobertoh/CodeGPT/compare/v2.2.9...v2.2.10
[2.2.9]: https://github.com/carlrobertoh/CodeGPT/compare/v2.2.8...v2.2.9
[2.2.8]: https://github.com/carlrobertoh/CodeGPT/compare/v2.2.7...v2.2.8
[2.2.7]: https://github.com/carlrobertoh/CodeGPT/compare/v2.2.6...v2.2.7
[2.2.6]: https://github.com/carlrobertoh/CodeGPT/compare/v2.2.5...v2.2.6
[2.2.5]: https://github.com/carlrobertoh/CodeGPT/compare/v2.2.4...v2.2.5
[2.2.4]: https://github.com/carlrobertoh/CodeGPT/compare/v2.2.3...v2.2.4
[2.2.3]: https://github.com/carlrobertoh/CodeGPT/compare/v2.2.2...v2.2.3
[2.2.2]: https://github.com/carlrobertoh/CodeGPT/compare/v2.2.1...v2.2.2
[2.2.1]: https://github.com/carlrobertoh/CodeGPT/compare/v2.2.0...v2.2.1
[2.2.0]: https://github.com/carlrobertoh/CodeGPT/compare/v2.1.7...v2.2.0
[2.1.7]: https://github.com/carlrobertoh/CodeGPT/compare/v2.1.6...v2.1.7
[2.1.6]: https://github.com/carlrobertoh/CodeGPT/compare/v2.1.5...v2.1.6
[2.1.5]: https://github.com/carlrobertoh/CodeGPT/compare/v2.1.4...v2.1.5
[2.1.4]: https://github.com/carlrobertoh/CodeGPT/compare/v2.1.3...v2.1.4
[2.1.3]: https://github.com/carlrobertoh/CodeGPT/compare/v2.1.2...v2.1.3
[2.1.2]: https://github.com/carlrobertoh/CodeGPT/compare/v2.1.1...v2.1.2
[2.1.1]: https://github.com/carlrobertoh/CodeGPT/compare/v2.1.0...v2.1.1
[2.1.0]: https://github.com/carlrobertoh/CodeGPT/compare/v2.0.6...v2.1.0
[2.0.6]: https://github.com/carlrobertoh/CodeGPT/compare/v2.0.5...v2.0.6
[2.0.5]: https://github.com/carlrobertoh/CodeGPT/compare/v2.0.4...v2.0.5
[2.0.4]: https://github.com/carlrobertoh/CodeGPT/compare/v2.0.3...v2.0.4
[2.0.3]: https://github.com/carlrobertoh/CodeGPT/compare/v2.0.2...v2.0.3
[2.0.2]: https://github.com/carlrobertoh/CodeGPT/compare/v2.0.1...v2.0.2
[2.0.1]: https://github.com/carlrobertoh/CodeGPT/compare/v2.0.0...v2.0.1
[2.0.0]: https://github.com/carlrobertoh/CodeGPT/commits/v2.0.0
