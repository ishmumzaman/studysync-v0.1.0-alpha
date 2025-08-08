import React, { useState, useEffect } from 'react';
import { View, StyleSheet, ScrollView } from 'react-native';
import { 
  Card, 
  Title, 
  Paragraph, 
  Button, 
  Surface, 
  Text,
  ActivityIndicator,
  FAB,
  Chip,
  ProgressBar
} from 'react-native-paper';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useAuth } from '../contexts/AuthContext';
import { useSession } from '../hooks/useSession';
import { formatDuration } from '../utils/timeUtils';
import StudyTimer from '../components/StudyTimer';
import WeeklyStats from '../components/WeeklyStats';

export default function HomeScreen({ navigation }: any) {
  const { user } = useAuth();
  const { activeSession, isLoading, startSession, endSession } = useSession();
  const [isStudying, setIsStudying] = useState(false);

  useEffect(() => {
    setIsStudying(!!activeSession);
  }, [activeSession]);

  const handleStartStudy = async () => {
    try {
      await startSession({});
      setIsStudying(true);
    } catch (error) {
      console.error('Failed to start session:', error);
    }
  };

  const handleEndStudy = async () => {
    try {
      await endSession({
        mood: 'focused',
        productivity: 4,
      });
      setIsStudying(false);
    } catch (error) {
      console.error('Failed to end session:', error);
    }
  };

  if (isLoading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContent}>
        {/* Welcome Card */}
        <Card style={styles.welcomeCard}>
          <Card.Content>
            <Title>Welcome back, {user?.displayName}!</Title>
            <Paragraph>Ready to track your study progress?</Paragraph>
          </Card.Content>
        </Card>

        {/* Active Session or Start Button */}
        {isStudying && activeSession ? (
          <StudyTimer 
            session={activeSession}
            onEnd={handleEndStudy}
          />
        ) : (
          <Surface style={styles.startSection} elevation={2}>
            <Title style={styles.startTitle}>Start a Study Session</Title>
            <Paragraph style={styles.startDescription}>
              Track your study time and compete with friends
            </Paragraph>
            <Button 
              mode="contained" 
              onPress={handleStartStudy}
              style={styles.startButton}
              contentStyle={styles.startButtonContent}
            >
              Start Studying
            </Button>
          </Surface>
        )}

        {/* Weekly Stats */}
        <WeeklyStats />

        {/* Quick Actions */}
        <View style={styles.quickActions}>
          <Title style={styles.sectionTitle}>Quick Actions</Title>
          <View style={styles.actionButtons}>
            <Button 
              mode="outlined" 
              onPress={() => navigation.navigate('Groups')}
              style={styles.actionButton}
            >
              My Groups
            </Button>
            <Button 
              mode="outlined" 
              onPress={() => navigation.navigate('Leaderboard')}
              style={styles.actionButton}
            >
              Leaderboard
            </Button>
          </View>
        </View>

        {/* Recent Activity */}
        <Card style={styles.activityCard}>
          <Card.Title title="Recent Activity" />
          <Card.Content>
            <View style={styles.activityItem}>
              <Text variant="bodyMedium">Today</Text>
              <Chip compact>{formatDuration(7200)}</Chip>
            </View>
            <View style={styles.activityItem}>
              <Text variant="bodyMedium">This Week</Text>
              <Chip compact>{formatDuration(25200)}</Chip>
            </View>
            <ProgressBar progress={0.7} style={styles.progressBar} />
            <Text variant="bodySmall" style={styles.progressText}>
              70% of weekly goal completed
            </Text>
          </Card.Content>
        </Card>
      </ScrollView>

      {/* Floating Action Button for Quick Start */}
      {!isStudying && (
        <FAB
          icon="play"
          style={styles.fab}
          onPress={handleStartStudy}
          label="Quick Start"
        />
      )}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  scrollContent: {
    padding: 16,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  welcomeCard: {
    marginBottom: 16,
  },
  startSection: {
    padding: 20,
    marginBottom: 16,
    borderRadius: 12,
    alignItems: 'center',
  },
  startTitle: {
    fontSize: 20,
    marginBottom: 8,
  },
  startDescription: {
    textAlign: 'center',
    marginBottom: 16,
    opacity: 0.7,
  },
  startButton: {
    borderRadius: 25,
  },
  startButtonContent: {
    paddingHorizontal: 32,
    paddingVertical: 8,
  },
  quickActions: {
    marginVertical: 16,
  },
  sectionTitle: {
    fontSize: 18,
    marginBottom: 12,
  },
  actionButtons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    gap: 12,
  },
  actionButton: {
    flex: 1,
  },
  activityCard: {
    marginTop: 16,
  },
  activityItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 12,
  },
  progressBar: {
    marginTop: 16,
    height: 8,
    borderRadius: 4,
  },
  progressText: {
    textAlign: 'center',
    marginTop: 8,
    opacity: 0.7,
  },
  fab: {
    position: 'absolute',
    margin: 16,
    right: 0,
    bottom: 0,
  },
});



