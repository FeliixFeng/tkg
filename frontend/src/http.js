import axios from 'axios';
import { ElMessage } from 'element-plus';
import router from './router';

const instance = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
});

instance.interceptors.request.use(
  config => {
    const token = sessionStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

instance.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    if (error.response) {
      const { status, data } = error.response;

      if (status === 401) {
        ElMessage.error('登录已过期，请重新登录');
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('username');
        sessionStorage.removeItem('userType');
        sessionStorage.removeItem('userId');
        sessionStorage.removeItem('phone');
        sessionStorage.removeItem('avatar');
        router.push('/login');
        return Promise.reject(error);
      }

      if (status === 403) {
        ElMessage.error('权限不足');
        return Promise.reject(error);
      }

      if (status === 404) {
        console.warn('资源不存在:', error.config.url);
        return Promise.reject(error);
      }

      if (data && data.msg) {
        ElMessage.error(data.msg);
      } else {
        ElMessage.error('请求失败');
      }
    } else if (error.request) {
      ElMessage.error('网络错误，请检查连接');
    } else {
      ElMessage.error('请求配置错误');
    }

    return Promise.reject(error);
  }
);

export default instance;
