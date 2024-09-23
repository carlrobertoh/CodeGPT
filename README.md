<a name="readme-top"></a>

<br />
<div align="center">
  <a href="https://github.com/carlrobertoh/CodeGPT">
    <img alt="plugin-icon" src="https://github.com/carlrobertoh/CodeGPT-docs/blob/main/images/icon.png?raw=true">
  </a>
  <h1 style="margin: 0;" align="center">CodeGPT</h1>
  <p style="width: 640px">
    The leading open-source AI copilot for JetBrains. Connect to any model in any environment, and customize your coding experience in any way you like.
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
    <li><a href="#core-features">Core Features</a></li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
        <li><a href="#api-key-configuration">API Key Configuration</a></li>
      </ul>
    </li>
    <li><a href="#features">Features</a></li>
    <li><a href="#running-locally">Running Locally</a></li>
    <li><a href="#privacy">Privacy</a></li>
    <li><a href="#feedback">Feedback</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>

![Plugin screenshot](https://www.codegpt.ee/images/main.png)

## About The Project

CodeGPT is your go-to AI coding assistant, offering assistance throughout your entire software development journey while keeping privacy in mind. Access state-of-the-art large language models from leading providers such as OpenAI, Anthropic, Azure, Mistral, and others, or connect to a locally hosted model for a completely offline and transparent development experience.

## Core Features

CodeGPT offers a wide range of features to enhance your coding experience:

### Chat
- Engage in natural language conversations about your code
- Get explanations, suggestions, and answers to your programming questions
- Upload and discuss images related to your code or project

### Code
- Receive context-aware code completions as you type
- Edit existing code using natural language instructions
- Generate descriptive commit messages automatically
- Get naming suggestions for variables, methods, and classes

### Customize
- Choose from multiple AI models, including local and self-hosted options
- Personalize your AI assistant with different personas
- Configure custom commands for repetitive tasks

### Integrate
- Reference project files and folders for context-aware assistance
- Access web documentation directly within your chat sessions
- Incorporate Git history into your conversations
- Perform web searches without leaving your IDE

For a full list of features and detailed descriptions, visit our [official documentation](https://docs.codegpt.ee/features).

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
