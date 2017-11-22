# link-checker

Link Checker is a (command-line) utility, which follows HTTP links found in a JSON document and tests their status.
The most common use-case is when you need to test whether your RESTful API generates correct links.

It would start from the a specific URL, collect links (matching specific criteria) from a response, and then try to repeat the cycle 
for each collected link.

It stops when either or the following is `true`:
- there are no more (non-visited) links left
- number of visited links reaches specified limit
- `TBD` (more rules coming)

## Configuration

### `config/link-checker-cfg.yml`

##### Entry point configuration

Entry-point is the first URL link checker will visit.

```
setup:
  start:
    url: https://api.github.com/users/anshake
    method: GET
```

##### Endpoints configuration

Next you would need to configure endpoints which are considered when Link Checker analyses response of each API call.

```
setup:
  endpoints:
    - url: /**/anshake
      method: GET
      fields:
      - url
      - followers_url
```

Run
---
TBD