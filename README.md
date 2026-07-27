# Chat
![Build](https://github.com/trevorism/chat/actions/workflows/deploy.yml/badge.svg)
![GitHub last commit](https://img.shields.io/github/last-commit/trevorism/chat)
![GitHub language count](https://img.shields.io/github/languages/count/trevorism/chat)
![GitHub top language](https://img.shields.io/github/languages/top/trevorism/chat)

API that wraps large language models. Requests route to OpenAI or Claude based on the requested model; a model starting with `claude` goes to the Anthropic Messages API, anything else goes to OpenAI (default `gpt-5.4`).

Deployed at [Trevorism Chat](https://chat.action.trevorism.com/)

# How to build
`gradle clean build`
