import axios, { AxiosInstance, InternalAxiosRequestConfig } from 'axios';
import * as SecureStore from 'expo-secure-store';
import { API_BASE_URL } from '../../config/constants';

const createAxiosInstance = (): AxiosInstance => {
  const instance = axios.create({
    baseURL: API_BASE_URL,
    timeout: 30000,
    headers: {
      'Content-Type': 'application/json',
    },
  });

  // Request interceptor to add auth token
  instance.interceptors.request.use(
    async (config: InternalAxiosRequestConfig) => {
      try {
        const tokensStr = await SecureStore.getItemAsync('tokens');
        if (tokensStr) {
          const tokens = JSON.parse(tokensStr);
          if (tokens.accessToken) {
            config.headers.Authorization = `Bearer ${tokens.accessToken}`;
          }
        }
      } catch (error) {
        console.error('Error getting auth token:', error);
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  // Response interceptor to handle token refresh
  instance.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;

      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;

        try {
          const tokensStr = await SecureStore.getItemAsync('tokens');
          if (tokensStr) {
            const tokens = JSON.parse(tokensStr);
            const response = await axios.post(
              `${API_BASE_URL}/auth/refresh`,
              {},
              {
                headers: {
                  'X-Refresh-Token': tokens.refreshToken,
                },
              }
            );

            const newTokens = response.data;
            await SecureStore.setItemAsync('tokens', JSON.stringify(newTokens));

            originalRequest.headers.Authorization = `Bearer ${newTokens.accessToken}`;
            return instance(originalRequest);
          }
        } catch (refreshError) {
          // Refresh failed, redirect to login
          await SecureStore.deleteItemAsync('tokens');
          await SecureStore.deleteItemAsync('user');
          // Navigation to login will be handled by auth context
        }
      }

      return Promise.reject(error);
    }
  );

  return instance;
};

export const apiClient = createAxiosInstance();



