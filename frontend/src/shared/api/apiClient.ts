import axios from 'axios';

export const apiClient = axios.create({
  baseURL: '/api',
  timeout: 3000, // ms
  headers: {
    'Content-Type': 'application/json',
  },
});
