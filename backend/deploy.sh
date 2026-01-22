#!/bin/bash
set -e

echo "🛑 Stopping Tomcat..."
/opt/apache-tomcat-10.1.44/bin/shutdown.sh || true
sleep 2

echo "🗑️  Cleaning old deployment..."
sudo rm -f /opt/apache-tomcat-10.1.44/webapps/devac*.war
sudo rm -rf /opt/apache-tomcat-10.1.44/webapps/devac*/

echo "📦 Building WAR..."
./mvnw clean package -DskipTests

echo "📤 Deploying..."
sudo cp target/devac.war /opt/apache-tomcat-10.1.44/webapps/

echo "🚀 Starting Tomcat..."
/opt/apache-tomcat-10.1.44/bin/startup.sh

echo "✅ Done! Check logs:"
echo "   tail -f /opt/apache-tomcat-10.1.44/logs/catalina.out"