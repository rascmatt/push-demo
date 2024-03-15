#!/bin/sh

# Build the frontend application
cd frontend
ng build --configuration production

# Remove the dist folder from the backend
rm -rf ../backend/src/main/resources/static

# Copy the dist folder to the backend
cp -r dist/push-demo ../backend/src/main/resources/static