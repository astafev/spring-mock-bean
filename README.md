# Goal

To have an annotation like spring boot's `@Conditional*` with the difference that
it creates a bean of 

# Usage (supposed)

Situation: We don't want a bean that can't (shouldn't) be launched during local development. But it's required in other beans (my use case: a bean responsible for error reporting).
Solution:
1. Add @MockOnProperty("prefix.enabled=false"). Similar to spring boot's @ConditionalOnProperty
2. Possible add reference to an abstract class that will do partly what's expected (for some simple mocking).

