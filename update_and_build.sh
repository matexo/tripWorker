#!/bin/bash

clear

echo "Pulling changes..."
git pull

echo "Building JAR with dependencies..."
mvn clean package

