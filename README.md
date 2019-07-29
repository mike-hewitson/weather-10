# weather-10

generated using Luminus version "3.42"

## Building from new

```
$ lein new luminus weather-9 +shadow-cljs +re-frame +mongodb +kibit +swagger
```

and test

### Start with services


Copy the travis config, and connect repo to travis and deploying to heroku
Add dependencies
Copy in environment contents
Get data logging names spaces and tests working
Run each of the two data loggers (to test and to set up some data)
Setup data access services, and tests for them

Edit services file with one endpoint and formats. Get this working.
Do the rest of the services




[![Build Status](https://travis-ci.org/mike-hewitson/weather-10.svg?branch=master)](https://travis-ci.org/mike-hewitson/weather-10)

## Running the app

### Local (for development)

Create a set of terminal windows, each to run one of the commands listed below:

```
$ mongod --config /usr/local/etc/mongod.conf
or
$ mongod
$ lein run
$ lein figwheel

```

Got to localhost:3000 to access

Swagger ui localhost:3000/api-docs/*/

### testing in the repl

```
dfsdf
dsfdsf
```

To manually reload src and test, and re-run them
```
(load-facts)
```

### style checking

Before pushing changes, run the following:
```
$ lein bikeshed
$ lein cljfmt check
$ lein kibit
$ lein cloverage --fail-threshold 40
$ lein eastwood   (Clojure linting tool)
```

## heroku

### new heroku app

Create heruko environments

```
$ heroku create
```

Add database URI to environments
Add TZ="Africa/Johannesburg" to environment

To push to heroku

```
$ git push heroku master
```

To test locally first

```
$ heroku open
```
### existing heroku app

Link the project to an existing heroku app by using the following, and then pushing as per above:

```
$ heroku git:remote -a project
```

## getting data from the various weather services

To run the logger to log one set of readings per location at the current time in the development environment.

```
$ lein with-profile dev trampoline run -m weather-9.log-data
$ lein with-profile dev trampoline run -m weather-9.log-tides
$ lein with-profile dev trampoline run -m weather-9.log-moon-phases
```

For the scheduled job in Heroku, use the following, log-data every 10 mins, tides every day
```
$ lein with-profile production trampoline run -m weather-7.log-data
```

Repeat this with log-tides and log-moon-phases, each once per day.

## travis-ci

Commits are checked using travis-ci, and pushed to heroku on success.

The following are parts of the travis-ci build:
- bikeshed (Clojure style checking)
- cljfmt (Clojure formatting)
- kibit (Clojure static code analysis)
- cloverage (runs midje tests and checks for code coverage)

Any failures will fail the build and will not deploy.
