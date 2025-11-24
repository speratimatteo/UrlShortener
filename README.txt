##################################################### DESCRIPTION #########################################################################

Simple api to shorten a URL and retrieve from DB the shortened URL.
Includes 3 endpoints:

1) POST /shorten ---------------> returns a ShortifyResponse, an object with the shortCode and the full URL to which is associated.
                                  If the URL has already been shortened before, returns the one saved on DB.
                                  Otherwise, create a new record and saves it.

2) GET /getFullUrl/{shortCode} -> also returns a ShortifyResponse, containing the full URL associated to the shortCode passed as input.
                                  Being a (potentially) high-volume API, is cached with Redis.

3) GET /redirect/{shortCode} ---> rather than just returning a ShortifyResponse like the API above, redirects the user the full URL associated to
                                  the shortCode in input.
                                  Also cached with Redis.

######################################################## SWAGGER ##########################################################################

Swagger available at:
http://localhost:8080/dev/swagger-ui/index.html

######################################################## SET UP ###########################################################################

Requires Docker Desktop installed.
Here are the commands to stop, build and launch the application

# SLOWER: stop, build and recreate all 3 components (api, db, redis). Quite slow

docker compose down
docker build -t url-shortener-app --no-cache .
docker compose up --force-recreate

# FASTER: stop, build and recreate only the api component (the one more subject to changes).

docker compose down api
docker compose build --no-cache api
docker compose up -d --force-recreate api

# SPECIFIC FOR ENV: I only put a single docker compose file, but in case of multiple ones for different environment,
these commands specify which docker-compose file to use in place of the default one.

docker compose -f docker-compose-{env}.yml stop
docker compose -f docker-compose-{env}.yml down
docker compose -f docker-compose-{env}.yml build --no-cache <----------------- BUILD WITH CACHE
docker compose -f docker-compose-{env}.yml build <---------------------------- SAME AS ABOVE BUT WITH CACHE
docker compose -f docker-compose-{env}.yml up

#################################################### RUNNING APPLICATION LOCALLY ###########################################################

1) Launch the container on Docker, then stop the api (will be launched on IDE).
2) Add the following VM options:

-Dspring.profiles.active=dev
