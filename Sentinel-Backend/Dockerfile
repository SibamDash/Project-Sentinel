# Use a Debian-based Java image (Reliable, no Snap issues)
FROM maven:3.8.5-openjdk-17-slim

WORKDIR /app

# Install Chromium and Driver (Works perfectly on Debian)
RUN apt-get update && \
    apt-get install -y chromium chromium-driver && \
    rm -rf /var/lib/apt/lists/*

# Copy source code (Maven is already installed in this image)
COPY .mvn/ .mvn
COPY pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src

# Run the app
CMD ["mvn", "spring-boot:run"]