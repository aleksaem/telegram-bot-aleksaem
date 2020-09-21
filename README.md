# telegram-bot-itcluster

Bot: ITClusterDemoBot
Bot Username: ITClusterDemoBot
Token: 1355633692:AAHM2TKSG1FZCH_XazAtSlnJCXZoP1BzkLs

## heroku
https://devcenter.heroku.com/articles/heroku-cli-commands
* create account on heroku.com
* ```heroku login```
* ```heroku create telegram-bot-itcluster```
* ```heroku info```
* ```mvn clean heroku:deploy```
* ```heroku open```
* ```heroku logs```
* ```heroku logs -tn 20```

## Tokens generation
 * copy token from sonar website -> manual configuration for maven
 * `heroku auth:token` and set it in env variable `HEROKU_API_KEY`