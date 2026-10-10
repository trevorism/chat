## 0.9.0

Move OpenAI from Chat Completions to the Responses API (stateless, `store: false`) and adopt the current Claude request shape (adaptive thinking, `output_config.effort`, server-side refusal fallbacks). Requests take an optional `effort` (low, medium, high, xhigh, max; default low) and responses report the `model` that answered. Models are restricted to an allow-list: gpt-6.1-sol (default), gpt-6-astra, gpt-6-luna, claude-opus-5-5, claude-sonnet-5-5, claude-fable-5-1, claude-haiku-4-5; any other model returns 400. Provider errors now pass through with the provider's message, refusals return 422, and truncated or incomplete responses return 502.

## 0.8.2

Update dependencies, fix gh actions workflow

## 0.8.1

Fix a 400 from Anthropic on claude-fable and claude-mythos models, which reject the thinking parameter because thinking is always on. Those models now omit thinking and use a larger max_tokens so an adaptive-thinking response is not truncated. Every other claude model continues to disable thinking.

## 0.8.0

Route chat requests to either OpenAI or Claude based on the requested model. A model starting with 'claude' goes to the Anthropic Messages API, anything else continues to OpenAI with the gpt-5.4 default. Prior messages now replay as user/assistant turns instead of user/system.

## 0.7.0

Upgrade to Micronaut 5.0.0, Java 25, and latest dependencies (Gradle 9.x, shadow 9.4.1, reactor-core 3.8.5, gson 2.14.0, secure-http-utils 3.2.1, appengine-gradle-plugin 2.8.7).

## 0.6.0

Update dependencies, migrate to java 21, and make chat gpt 5 the default model.

## 0.5.0

Update dependencies.

## 0.4.0

Make the default model gpt-4o-mini now that it is available. Update dependencies.

## 0.3.0

Update dependencies and add test and deploy events.

## 0.2.0

Chat-GPT 4 now live.

## 0.1.0

Chat-GPT 3.5 Turbo wrapped for Trevorism
