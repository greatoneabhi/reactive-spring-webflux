#!/usr/bin/env sh

curl --location 'http://localhost:8080/v1/movieinfos' \
--header 'Content-Type: application/json' \
--header 'User-Agent: insomnia/9.3.2' \
--data '{
	"name": "Black Panther 2",
	"year": "2020",
	"genre": ["Action", "Adventure", "Fantasy"],
	"releaseDate": "2020-06-05"
}'

curl --location 'http://localhost:8080/v1/movieinfos'

curl --location 'http://localhost:8080/v1/movieinfos/stream'