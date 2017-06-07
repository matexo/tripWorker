#!/bin/bash

java -Djava.library.path=".:/home/worker/tripWorker/" -jar target/workers-1.0-SNAPSHOT-jar-with-dependencies.jar
