import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import * as SecureStore from 'expo-secure-store';
import { authApi } from '../services/api/authApi';
import { User, TokenPair } from '../types/auth';

interface AuthContextType {
  user: User | null;
  tokens: TokenPair | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (data: RegisterData) => Promise<void>;
  logout: () => Promise<void>;
  refreshTokens: () => Promise<void>;
}

interface RegisterData {
  email: string;
  password: string;
  displayName: string;
  timezone: string;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [tokens, setTokens] = useState<TokenPair | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadStoredAuth();
  }, []);

  const loadStoredAuth = async () => {
    try {
      const storedTokens = await SecureStore.getItemAsync('tokens');
      const storedUser = await SecureStore.getItemAsync('user');

      if (storedTokens && storedUser) {
        setTokens(JSON.parse(storedTokens));
        setUser(JSON.parse(storedUser));
      }
    } catch (error) {
      console.error('Error loading stored auth:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const saveAuthData = async (userData: User, tokenData: TokenPair) => {
    await SecureStore.setItemAsync('tokens', JSON.stringify(tokenData));
    await SecureStore.setItemAsync('user', JSON.stringify(userData));
    setUser(userData);
    setTokens(tokenData);
  };

  const clearAuthData = async () => {
    await SecureStore.deleteItemAsync('tokens');
    await SecureStore.deleteItemAsync('user');
    setUser(null);
    setTokens(null);
  };

  const login = async (email: string, password: string) => {
    try {
      const response = await authApi.login({ email, password });
      await saveAuthData(response.user, response.tokens);
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  };

  const register = async (data: RegisterData) => {
    try {
      const response = await authApi.register(data);
      await saveAuthData(response.user, response.tokens);
    } catch (error) {
      console.error('Registration error:', error);
      throw error;
    }
  };

  const logout = async () => {
    try {
      if (tokens?.refreshToken) {
        await authApi.logout(tokens.refreshToken);
      }
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      await clearAuthData();
    }
  };

  const refreshTokens = async () => {
    try {
      if (!tokens?.refreshToken) {
        throw new Error('No refresh token available');
      }

      const newTokens = await authApi.refreshToken(tokens.refreshToken);
      await SecureStore.setItemAsync('tokens', JSON.stringify(newTokens));
      setTokens(newTokens);
    } catch (error) {
      console.error('Token refresh error:', error);
      await clearAuthData();
      throw error;
    }
  };

  const value: AuthContextType = {
    user,
    tokens,
    isLoading,
    isAuthenticated: !!user && !!tokens,
    login,
    register,
    logout,
    refreshTokens,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};



