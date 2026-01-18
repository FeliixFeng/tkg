import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080', // 本地后端服务地址
  timeout: 5000, // 可选：设置请求超时时间
});

// 请求拦截器
instance.interceptors.request.use(config => {
  const token = sessionStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default instance;
