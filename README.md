# Goal

To have an annotation like spring boot's `@Conditional*` with the difference that
it creates a mocked bean.

# Usage (supposed)

Situation: We don't want a bean that can't (shouldn't) be launched during local development. But it's required in other beans (my use case: a bean responsible for error reporting).
Solution:
1. Add @MockOnProperty("prefix.enabled=false"). Similar to spring boot's @ConditionalOnProperty
2. Possibly add reference to an abstract class that will do partly what's expected (for some simple mocking).

# How it works (hopefully will one day)

* a new bean MockFactory is created. It's generated automatically with one method per bean to mock.
* if a bean is needed to be mocked, it's definition is updated with a factory method/bean.
* when a bean is being instantiated, it's replaced with cglib proxy
