import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class StudySyncDemo {
    
    private static boolean isStudying = false;
    private static LocalDateTime studyStartTime;
    private static long totalStudyTimeSeconds = 0;
    private static int sessionCount = 0;
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("      StudySync - Demo Application     ");
        System.out.println("========================================");
        System.out.println();
        System.out.println("🎉 Welcome to StudySync!");
        System.out.println("Track your study sessions and see your progress.");
        System.out.println();
        
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            showMenu();
            System.out.print("Choose an option (1-5): ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1 -> startStudySession();
                    case 2 -> endStudySession();
                    case 3 -> showCurrentSession();
                    case 4 -> showStats();
                    case 5 -> {
                        System.out.println("Thanks for using StudySync! Keep studying! 📚");
                        return;
                    }
                    default -> System.out.println("❌ Invalid option. Please choose 1-5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number (1-5).");
            }
            
            System.out.println();
        }
    }
    
    private static void showMenu() {
        System.out.println("📚 StudySync Menu:");
        System.out.println("1. ▶️  Start Study Session");
        System.out.println("2. ⏹️  End Study Session");
        System.out.println("3. ⏱️  Current Session Status");
        System.out.println("4. 📊 Study Statistics");
        System.out.println("5. 🚪 Exit");
        System.out.println();
    }
    
    private static void startStudySession() {
        if (isStudying) {
            System.out.println("⚠️  You already have an active study session!");
            System.out.println("Current session started at: " + 
                studyStartTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            return;
        }
        
        isStudying = true;
        studyStartTime = LocalDateTime.now();
        
        System.out.println("✅ Study session started!");
        System.out.println("⏰ Start time: " + 
            studyStartTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        System.out.println("📖 Happy studying! Stay focused! 💪");
    }
    
    private static void endStudySession() {
        if (!isStudying) {
            System.out.println("❌ No active study session to end.");
            return;
        }
        
        LocalDateTime endTime = LocalDateTime.now();
        long sessionDuration = java.time.Duration.between(studyStartTime, endTime).getSeconds();
        
        totalStudyTimeSeconds += sessionDuration;
        sessionCount++;
        isStudying = false;
        
        System.out.println("🎯 Study session completed!");
        System.out.println("⏰ Session duration: " + formatDuration(sessionDuration));
        System.out.println("📈 Total study time today: " + formatDuration(totalStudyTimeSeconds));
        System.out.println("🔥 Sessions completed: " + sessionCount);
    }
    
    private static void showCurrentSession() {
        if (!isStudying) {
            System.out.println("💤 No active study session.");
            return;
        }
        
        LocalDateTime currentTime = LocalDateTime.now();
        long currentDuration = java.time.Duration.between(studyStartTime, currentTime).getSeconds();
        
        System.out.println("📚 Current Study Session:");
        System.out.println("⏰ Started: " + 
            studyStartTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        System.out.println("⏱️  Duration: " + formatDuration(currentDuration));
        System.out.println("💪 Keep going! You're doing great!");
    }
    
    private static void showStats() {
        System.out.println("📊 Your Study Statistics:");
        System.out.println("============================");
        System.out.println("🔥 Sessions completed: " + sessionCount);
        System.out.println("⏱️  Total study time: " + formatDuration(totalStudyTimeSeconds));
        
        if (sessionCount > 0) {
            long averageSession = totalStudyTimeSeconds / sessionCount;
            System.out.println("📈 Average session: " + formatDuration(averageSession));
        }
        
        if (isStudying) {
            System.out.println("✅ Current status: Studying 📚");
        } else {
            System.out.println("💤 Current status: Not studying");
        }
        
        // Motivational message
        if (totalStudyTimeSeconds > 3600) {
            System.out.println("🏆 Excellent! You've studied over 1 hour!");
        } else if (totalStudyTimeSeconds > 1800) {
            System.out.println("🌟 Great job! Keep up the momentum!");
        } else if (sessionCount > 0) {
            System.out.println("🚀 Good start! Every minute counts!");
        }
    }
    
    private static String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, secs);
        } else {
            return String.format("%ds", secs);
        }
    }
}

