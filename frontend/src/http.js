import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080', // 本地后端服务地址
  timeout: 5000, // 可选：设置请求超时时间
});

export default instance;
