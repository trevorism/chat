# Chat
![Build](https://github.com/trevorism/chat/actions/workflows/deploy.yml/badge.svg)
![GitHub last commit](https://img.shields.io/github/last-commit/trevorism/chat)
![GitHub language count](https://img.shields.io/github/languages/count/trevorism/chat)
![GitHub top language](https://img.shields.io/github/languages/top/trevorism/chat)

API that wraps large language models behind one text-to-text interface. `POST /api/chat` takes `message`, optional `context` (system prompt), `previousMessages` (alternating user/assistant turns), `model`, and `effort`, and returns the `message` plus the `model` that answered.

| Provider | Models | API |
|---|---|---|
| OpenAI | `gpt-6.1-sol` (default), `gpt-6-astra`, `gpt-6-luna` | Responses API, stateless (`store: false`) |
| Anthropic | `claude-opus-5-5`, `claude-sonnet-5-5`, `claude-fable-5-1`, `claude-haiku-4-5` | Messages API with adaptive thinking and server-side refusal fallbacks |

Any other model returns 400. `effort` is `low` (default), `medium`, `high`, `xhigh`, or `max` and maps to `reasoning.effort` on OpenAI and `output_config.effort` on Claude; `claude-haiku-4-5` does not support effort and ignores it. Provider refusals return 422 and truncated or incomplete responses return 502.

Deployed at [Trevorism Chat](https://chat.action.trevorism.com/)

# How to build
`gradle clean build`
