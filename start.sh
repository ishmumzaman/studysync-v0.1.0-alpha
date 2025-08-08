#!/bin/bash

# StudySync Quick Start Script

echo "🚀 Starting StudySync Development Environment..."
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    echo "Visit: https://docs.docker.com/get-docker/"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    echo "Visit: https://docs.docker.com/compose/install/"
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "⚠️  Java is not installed. Backend will run in Docker only."
    JAVA_AVAILABLE=false
else
    JAVA_AVAILABLE=true
    echo "✅ Java detected: $(java -version 2>&1 | head -n 1)"
fi

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "⚠️  Node.js is not installed. Mobile app cannot be started."
    NODE_AVAILABLE=false
else
    NODE_AVAILABLE=true
    echo "✅ Node.js detected: $(node -v)"
fi

echo ""
echo "📦 Starting Docker containers..."
docker-compose up -d

# Wait for services to be ready
echo ""
echo "⏳ Waiting for services to be ready..."
sleep 10

# Check MongoDB
echo -n "MongoDB: "
if docker-compose exec -T mongodb mongosh --eval "db.adminCommand('ping')" &> /dev/null; then
    echo "✅ Ready"
else
    echo "❌ Not ready"
fi

# Check Redis
echo -n "Redis: "
if docker-compose exec -T redis redis-cli ping &> /dev/null; then
    echo "✅ Ready"
else
    echo "❌ Not ready"
fi

# Build and start backend if Java is available
if [ "$JAVA_AVAILABLE" = true ]; then
    echo ""
    echo "🔨 Building backend..."
    cd backend
    ./mvnw clean package -DskipTests
    cd ..
fi

# Check backend health
echo ""
echo -n "Backend API: "
if curl -f http://localhost:8080/api/v1/actuator/health &> /dev/null; then
    echo "✅ Ready"
else
    echo "⏳ Starting... (this may take a minute)"
fi

# Install mobile dependencies if Node is available
if [ "$NODE_AVAILABLE" = true ]; then
    echo ""
    echo "📱 Setting up mobile app..."
    cd mobile
    npm install
    echo "✅ Mobile app dependencies installed"
    cd ..
fi

echo ""
echo "========================================="
echo "✨ StudySync Development Environment Ready!"
echo "========================================="
echo ""
echo "📚 Resources:"
echo "  • Backend API: http://localhost:8080/api/v1"
echo "  • API Docs: http://localhost:8080/swagger-ui.html"
echo "  • MongoDB: localhost:27017"
echo "  • Redis: localhost:6379"
echo ""
echo "🎯 Next Steps:"
echo "  1. Backend is running at http://localhost:8080"

if [ "$NODE_AVAILABLE" = true ]; then
    echo "  2. Start mobile app: cd mobile && npm start"
else
    echo "  2. Install Node.js to run the mobile app"
fi

echo "  3. View API documentation at http://localhost:8080/swagger-ui.html"
echo ""
echo "📖 For more information, see README.md"
echo ""
echo "To stop all services: docker-compose down"
echo ""



