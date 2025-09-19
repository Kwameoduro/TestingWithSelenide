FROM maven:3.9.6-eclipse-temurin-21

# Set working directory
WORKDIR /app

# Install dependencies required by Chrome
RUN apt-get update && apt-get install -y \
    wget curl unzip gnupg2 fonts-liberation libnss3 libxss1 \
    libappindicator3-1 libasound2 xdg-utils xvfb libgbm1 libvulkan1 --no-install-recommends && \
    rm -rf /var/lib/apt/lists/*

# Install Google Chrome (matching ChromeDriver version)
RUN CHROME_VERSION="131.0.6778.204" && \
    wget -q -O /tmp/chrome-linux64.zip "https://storage.googleapis.com/chrome-for-testing-public/${CHROME_VERSION}/linux64/chrome-linux64.zip" && \
    unzip /tmp/chrome-linux64.zip -d /tmp/ && \
    mkdir -p /opt/google && \
    mv /tmp/chrome-linux64 /opt/google/chrome && \
    ln -s /opt/google/chrome/chrome /usr/bin/google-chrome && \
    rm -rf /tmp/chrome-linux64.zip

# Install ChromeDriver
RUN CHROMEDRIVER_VERSION="131.0.6778.204" && \
    wget -q -O /tmp/chromedriver.zip "https://storage.googleapis.com/chrome-for-testing-public/$CHROMEDRIVER_VERSION/linux64/chromedriver-linux64.zip" && \
    unzip /tmp/chromedriver.zip -d /tmp && \
    mv /tmp/chromedriver-linux64/chromedriver /usr/local/bin/chromedriver && \
    chmod +x /usr/local/bin/chromedriver && \
    rm -rf /tmp/chromedriver.zip /tmp/chromedriver-linux64

# Install Node.js (for Allure CLI)
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs && \
    npm install -g allure-commandline --save-dev

# Set display for headless Chrome
ENV DISPLAY=:99

# Copy project files
COPY . .

# Download Maven dependencies
RUN mvn dependency:go-offline

# Default command: run tests, saving reports to /app/allure-results
CMD ["sh", "-c", "Xvfb :99 & mvn clean test -Dallure.results.directory=/app/allure-results"]