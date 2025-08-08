import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class StudySyncSimpleAPI {
    
    private static final Map<String, Object> sessions = new ConcurrentHashMap<>();
    private static int sessionCounter = 0;
    
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // API Routes
        server.createContext("/", new HomeHandler());
        server.createContext("/health", new HealthHandler());
        server.createContext("/api/v1/sessions/start", new StartSessionHandler());
        server.createContext("/api/v1/sessions/end", new EndSessionHandler());
        server.createContext("/api/v1/sessions/active", new ActiveSessionHandler());
        server.createContext("/api/v1/stats", new StatsHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("=========================================");
        System.out.println("  StudySync API Server is running!");
        System.out.println("=========================================");
        System.out.println();
        System.out.println("API Endpoints:");
        System.out.println("  http://localhost:8080/               - Home");
        System.out.println("  http://localhost:8080/health         - Health Check");
        System.out.println("  http://localhost:8080/api/v1/sessions/start  - Start Session");
        System.out.println("  http://localhost:8080/api/v1/sessions/end    - End Session");
        System.out.println("  http://localhost:8080/api/v1/sessions/active - Active Sessions");
        System.out.println("  http://localhost:8080/api/v1/stats           - Statistics");
        System.out.println();
        System.out.println("Server started on port 8080");
    }
    
    static class HomeHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\n" +
                "  \"message\": \"StudySync API is running!\",\n" +
                "  \"version\": \"1.0.0-demo\",\n" +
                "  \"timestamp\": \"" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\",\n" +
                "  \"endpoints\": [\n" +
                "    \"GET /health - Health check\",\n" +
                "    \"POST /api/v1/sessions/start - Start study session\",\n" +
                "    \"POST /api/v1/sessions/end - End study session\",\n" +
                "    \"GET /api/v1/sessions/active - Get active sessions\",\n" +
                "    \"GET /api/v1/stats - Get statistics\"\n" +
                "  ]\n" +
                "}";
            
            sendResponse(exchange, 200, response, "application/json");
        }
    }
    
    static class HealthHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\n" +
                "  \"status\": \"UP\",\n" +
                "  \"application\": \"StudySync\",\n" +
                "  \"timestamp\": \"" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\"\n" +
                "}";
            
            sendResponse(exchange, 200, response, "application/json");
        }
    }
    
    static class StartSessionHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                sessionCounter++;
                String sessionId = "session_" + sessionCounter;
                
                Map<String, Object> session = new ConcurrentHashMap<>();
                session.put("id", sessionId);
                session.put("startTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                session.put("status", "ACTIVE");
                session.put("userId", "demo_user");
                
                sessions.put(sessionId, session);
                
                String response = "{\n" +
                    "  \"success\": true,\n" +
                    "  \"sessionId\": \"" + sessionId + "\",\n" +
                    "  \"message\": \"Study session started successfully!\",\n" +
                    "  \"startTime\": \"" + session.get("startTime") + "\"\n" +
                    "}";
                
                sendResponse(exchange, 201, response, "application/json");
            } else {
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}", "application/json");
            }
        }
    }
    
    static class EndSessionHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // Find the most recent active session
                String activeSessionId = null;
                for (Map.Entry<String, Object> entry : sessions.entrySet()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> session = (Map<String, Object>) entry.getValue();
                    if ("ACTIVE".equals(session.get("status"))) {
                        activeSessionId = entry.getKey();
                        break;
                    }
                }
                
                if (activeSessionId != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> session = (Map<String, Object>) sessions.get(activeSessionId);
                    session.put("status", "COMPLETED");
                    session.put("endTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    session.put("durationMinutes", "5"); // Simplified duration
                    
                    String response = "{\n" +
                        "  \"success\": true,\n" +
                        "  \"sessionId\": \"" + activeSessionId + "\",\n" +
                        "  \"message\": \"Study session ended successfully!\",\n" +
                        "  \"endTime\": \"" + session.get("endTime") + "\",\n" +
                        "  \"duration\": \"" + session.get("durationMinutes") + " minutes\"\n" +
                        "}";
                    
                    sendResponse(exchange, 200, response, "application/json");
                } else {
                    sendResponse(exchange, 400, "{\"error\": \"No active session found\"}", "application/json");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}", "application/json");
            }
        }
    }
    
    static class ActiveSessionHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder response = new StringBuilder();
            response.append("{\n");
            response.append("  \"activeSessions\": [\n");
            
            boolean first = true;
            for (Map.Entry<String, Object> entry : sessions.entrySet()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> session = (Map<String, Object>) entry.getValue();
                if ("ACTIVE".equals(session.get("status"))) {
                    if (!first) response.append(",\n");
                    response.append("    {\n");
                    response.append("      \"id\": \"").append(session.get("id")).append("\",\n");
                    response.append("      \"startTime\": \"").append(session.get("startTime")).append("\",\n");
                    response.append("      \"status\": \"").append(session.get("status")).append("\"\n");
                    response.append("    }");
                    first = false;
                }
            }
            
            response.append("\n  ],\n");
            response.append("  \"count\": ").append(sessions.size()).append("\n");
            response.append("}");
            
            sendResponse(exchange, 200, response.toString(), "application/json");
        }
    }
    
    static class StatsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            int totalSessions = sessions.size();
            int activeSessions = 0;
            int completedSessions = 0;
            
            for (Object sessionObj : sessions.values()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> session = (Map<String, Object>) sessionObj;
                String status = (String) session.get("status");
                if ("ACTIVE".equals(status)) {
                    activeSessions++;
                } else if ("COMPLETED".equals(status)) {
                    completedSessions++;
                }
            }
            
            String response = "{\n" +
                "  \"totalSessions\": " + totalSessions + ",\n" +
                "  \"activeSessions\": " + activeSessions + ",\n" +
                "  \"completedSessions\": " + completedSessions + ",\n" +
                "  \"serverUptime\": \"" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\",\n" +
                "  \"version\": \"1.0.0-demo\"\n" +
                "}";
            
            sendResponse(exchange, 200, response, "application/json");
        }
    }
    
    private static void sendResponse(HttpExchange exchange, int statusCode, String response, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

