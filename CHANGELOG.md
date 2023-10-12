# Changelog

All notable changes to this project will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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

[Unreleased]: https://github.com/carlrobertoh/CodeGPT/compare/v2.1.6...HEAD
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
[2.0.1]: https://github.com/carlrobertoh/CodeGPT/commits/v2.0.1
[2.0.0]: https://github.com/carlrobertoh/CodeGPT/commits/v2.0.0
