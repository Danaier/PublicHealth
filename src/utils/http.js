import axios from 'axios';
import {ElMessage} from 'element-plus';
// import { localGet } from './index'

const http = axios.create({
  baseURL: '/api', // url = base url + request url,
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 50000,
});

// 数据请求拦截
http.interceptors.request.use(
    (config) => {
      return config;
    },
    (error) => {
      return Promise.reject(error);
    },
);

// 返回响应数据拦截
http.interceptors.response.use(
    (response) => {
      const res = response.data;
      if (res.code !== 0) {
        // eslint-disable-next-line new-cap
        ElMessage({
          type: 'error',
          message: '请求失败',
          showClose: true,
        });
        console.log(res);
        return Promise.reject(new Error(res.message || 'Error'));
      } else {
        return res.data;
      }
    },
    (error) => {
      if (error.response.status) {
        // 状态码超过 2xx 范围时都会调用该函数，处理错误响应
        switch (error.response.status) {
          case 404:
            // eslint-disable-next-line new-cap
            ElMessage({
              type: 'error',
              message: '请求路径找不到！',
              showClose: true,
            });
            break;
          case 502:
            // eslint-disable-next-line new-cap
            ElMessage({
              type: 'error',
              message: '服务器内部报错！',
              showClose: true,
            });
            break;
          default:
            break;
        }
      }
      return Promise.reject(error);
    },
);

export default http;
