# Erä-Leijonat member application form emailer

This is a Scala app that receives form submissions as JSON and emails the data to the correct parties via Mailgun's API.

## Build & run ##

Set environment variables `mailgun_api_key`, `mailgun_api_login`, `new_member_recipients` (=comma-separated list of emails).

```sh
sbt compile
sbt container:start
```
