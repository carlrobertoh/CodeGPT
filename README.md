<a name="readme-top"></a>

<br />
<div align="center">
  <a href="https://github.com/carlrobertoh/CodeGPT">
    <img alt="plugin-icon" src="https://github.com/carlrobertoh/CodeGPT-docs/blob/main/images/icon.png?raw=true">
  </a>
  <h1 style="margin: 0;" align="center">CodeGPT</h1>
  <p>
    A JetBrains extension providing access to state-of-the-art LLMs, such as GPT-4, Claude 3, Code Llama, and others, all for free
  </p>
</div>

[![Downloads][downloads-shield]][plugin-repo]
[![Rating][rating-shield]][plugin-repo]
[![Discord][discord-shield]][invite-link]
[![Version][version-shield]][plugin-repo]

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about-the-project">About The Project</a></li>
    <li><a href="#quick-start-guide">Quick Start Guide</a></li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
        <li><a href="#api-key-configuration">API Key Configuration</a></li>
      </ul>
    </li>
    <li><a href="#features">Features</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>

## About The Project

CodeGPT is your go-to AI coding assistant, offering assistance throughout your entire software development journey while keeping privacy in mind. Access state-of-the-art large language models from leading providers such as OpenAI, Anthropic, Azure, Mistral, and others, or connect to a locally hosted model for a completely offline and transparent development experience.

## Core Features

Leveraging large language models, CodeGPT offers a wide range of features to enhance your coding experience, including, but not limited to:

### Code Completions

Receive single-line or whole-function autocomplete suggestions as you type.

![Code Completions](https://github.com/carlrobertoh/CodeGPT-docs/blob/main/images/new/inline-completion.png?raw=true)

### Chat (with Vision)

Get instant coding advice through a ChatGPT-like interface. Ask questions, seek explanations, or get guidance on your projects without leaving your IDE.

CodeGPT also supports vision models and image understanding, allowing you to attach images for more context-aware assistance. It can detect new screenshots automatically, saving you time by eliminating the need to manually upload images each time you take a screenshot.

![Chat with Vision](https://github.com/carlrobertoh/CodeGPT-docs/blob/main/images/new/chat-interface.png?raw=true)

### Fast Edit

Transform your code effortlessly using natural language instructions. Highlight the code you want to modify, describe the desired changes, and let CodeGPT implement them automatically.

![Fast Edits](https://www.codegpt.ee/fast-code-edits-cover.png)

### Commit Message Generation

CodeGPT can generate meaningful commit messages based on the changes made in your codebase. It analyzes the diff of your staged changes and suggests concise and descriptive commit messages, saving you time and effort.

![Commit Message Generation](https://github.com/carlrobertoh/CodeGPT-docs/blob/main/images/new/generate-commit-message.png?raw=true)

### Reference Files

CodeGPT allows you to reference specific files or documentation during your chat sessions, ensuring that responses are always relevant and accurate.

![Reference Files](https://github.com/carlrobertoh/CodeGPT-docs/blob/main/images/new/reference-files-modal.png?raw=true)

### Name Suggestions

Stuck on naming a method or variable? CodeGPT offers context-aware suggestions, helping you adhere to best practices and maintain readability in your codebase.

![Name Suggestions](https://github.com/carlrobertoh/CodeGPT-docs/blob/main/images/method-name-suggestions.png?raw=true)

### OpenAI Compatibility

Interested in trying out 800t/s or getting access to new models as soon as they're released? We provide integration with most cloud providers that are OpenAI-compatible, such as Together.ai, Grok, Anyscale, and others, as well as the option to customize your own setup.

![OpenAI Compatibility](https://github.com/carlrobertoh/CodeGPT-docs/blob/main/images/openai-compatibility.png?raw=true)

### Offline Development Support

CodeGPT supports a completely offline development workflow by allowing you to connect to a locally hosted language model. This ensures that your code and data remain private and secure within your local environment, eliminating the need for an internet connection or sharing sensitive information with third-party servers.

![Offline Development Support](https://github.com/carlrobertoh/CodeGPT-docs/blob/main/images/new/llama-settings.png?raw=true)

## Running locally

**Linux or macOS**
```shell
git clone https://github.com/carlrobertoh/CodeGPT.git
cd CodeGPT
git submodule update
./gradlew runIde
```

**Windows ARM64**
```shell
./gradlew runIde -Penv=win-arm64
```

**Tailing logs**
```shell
tail -f build/idea-sandbox/system/log/idea.log
```

## Privacy

**Your data stays yours.** CodeGPT **does not** collect or store any kind of sensitive information.

However, with users' consent, we do collect anonymous usage data, which we use to understand how users interact with the extension, including the most-used features and preferred providers.

## Feedback

Your input helps us grow. Reach out through:

- [Issue Tracker](https://github.com/carlrobertoh/CodeGPT/issues)
- [Discord](https://discord.gg/8dTGGrwcnR)
- [Email](mailto:carlrobertoh@gmail.com)

## License

Apache 2.0 © [Carl-Robert Linnupuu][portfolio]

If you found this project interesting, kindly rate it on the marketplace and don't forget to give it a star. Thanks again!
<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[downloads-shield]: https://img.shields.io/jetbrains/plugin/d/21056-codegpt
[discord-shield]: https://img.shields.io/discord/1118629761049182238?style=flat&logo=discord&label=Discord
[version-shield]: https://img.shields.io/jetbrains/plugin/v/21056-codegpt?label=version
[rating-shield]: https://img.shields.io/jetbrains/plugin/r/rating/21056-codegpt
[marketplace-img]: https://github.com/carlrobertoh/CodeGPT-docs/blob/main/images/marketplace.png?raw=true
[plugin-repo]: https://plugins.jetbrains.com/plugin/21056-codegpt
[invite-link]: https://discord.gg/8dTGGrwcnR
[open-issues]: https://github.com/carlrobertoh/CodeGPT/issues
[api-key-url]: https://platform.openai.com/account/api-keys
[portfolio]: https://carlrobert.ee
