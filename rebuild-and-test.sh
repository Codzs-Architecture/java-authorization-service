#!/bin/bash

# Script to rebuild and test the authorization server
echo "Starting rebuild and test process..."

# Step 1: Clean and rebuild the project
echo "Step 1: Cleaning and rebuilding project..."
./mvnw clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "✓ Build successful"
else
    echo "✗ Build failed"
    exit 1
fi

# Step 2: Run the authorization server
echo "Step 2: Starting authorization server..."
java -Dspring.profiles.active=local \
     -Dconfig.server.url=https://local.codzs.com:5002 \
     -Dconfig.password=password \
     -Dserver.ssl.keystore=./../java-codzs-resource/local/local.codzs.com.p12 \
     -Dserver.ssl.password=localpassword \
     -Dgoogle.client_id=google-client-id \
     -Dgoogle.client_secret=google-client-secret \
     -jar target/java-authorization-service.jar

echo "Test completed."