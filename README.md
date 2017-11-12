# link-checker

Link Checker is a (command-line) utility, which follows HTTP links found in a JSON document and tests their status.
The most common use-case is when you need to test whether your RESTful API generates correct links.

It would start from the a specific URL, collect links (matching specific criteria) from a response, and then try to repeat the cycle 
for each collected link.

It stops when:
- there are no more (non-visited) links left
- `TBD` (there will be support for user-defined rules)

Configuration
-------------
TBD

Run
---
TBD