<template>
  <div class="form-container">
    <div class="form-box">
      <!-- 根据 isLogin 的值显示登录或注册 -->
      <h2 v-if="isLogin">系统登录</h2>
      <h2 v-else>用户注册</h2>

      <el-form :model="currentForm" class="form">
        <!-- 用户名（登录和注册时都显示） -->
        <el-form-item>
          <el-input
            v-model="currentForm.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
          />
        </el-form-item>

        <!-- 手机号（仅注册时显示） -->
        <el-form-item v-if="!isLogin">
          <el-input
            v-model="currentForm.phone"
            placeholder="请输入手机号"
            prefix-icon="el-icon-phone"
          />
        </el-form-item>

        <!-- 密码（登录和注册时都显示） -->
        <el-form-item>
          <el-input
            v-model="currentForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
          />
        </el-form-item>

        <!-- 再次输入密码（仅注册时显示） -->
        <el-form-item v-if="!isLogin">
          <el-input
            v-model="currentForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="el-icon-lock"
          />
        </el-form-item>

        <!-- 是否为专家的勾选框（仅注册时显示） -->
        <el-form-item v-if="!isLogin" class="is-expert-container">
          <el-checkbox v-model="currentForm.isExpert">是否为专家</el-checkbox>
        </el-form-item>

        <!-- 登录或注册按钮 -->
        <el-form-item class="button-container">
          <el-button type="primary" @click="isLogin ? onLogin() : onRegister()">
            {{ isLogin ? '登录' : '注册' }}
          </el-button>
        </el-form-item>

        <!-- 切换登录和注册链接 -->
        <el-link @click="toggleForm" class="toggle-link">
          {{ isLogin ? '没有账号？点击我进行注册' : '已经有账号？点击我进行登录' }}
        </el-link>
      </el-form>
    </div>
  </div>
</template>

<script>
import { ElForm, ElFormItem, ElInput, ElButton, ElLink, ElCheckbox } from 'element-plus';
import axios from '../http';

export default {
  components: {
    ElForm,
    ElFormItem,
    ElInput,
    ElButton,
    ElLink,
    ElCheckbox,
  },
  data() {
    return {
      isLogin: true,
      loginForm: {
        username: '',
        password: '',
      },
      registerForm: {
        username: '',
        phone: '',
        password: '',
        confirmPassword: '',
        isExpert: false,
      },
    };
  },
  computed: {
    currentForm() {
      return this.isLogin ? this.loginForm : this.registerForm;
    },
  },
  methods: {
    onLogin() {
      axios.post('/api/user/login', {
        username: this.currentForm.username,
        password: this.currentForm.password,
      })
      .then(response => {
        if (response.data.code === 1) {
          sessionStorage.setItem('username', response.data.data.username);
          sessionStorage.setItem('userType', response.data.data.userType);
          sessionStorage.setItem('userId', response.data.data.id);
          sessionStorage.setItem('password', response.data.data.password);

          this.$store.dispatch('fetchUser', {
            username: response.data.data.username,
            userType: response.data.data.userType,
            userId: response.data.data.id,
            password: response.data.data.password,
          });

          this.$emit('loginSuccess');
        } else {
          alert('登录失败: ' + response.data.msg);
        }
      })
      .catch(error => {
        console.error('登录请求失败', error);
        alert('网络错误，请重试');
      });
    },
    onRegister() {
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        alert('两次输入的密码不一致');
        return;
      }

      axios.get(`/api/user/get_user/${this.registerForm.username}`)
      .then(response => {
        if (response.data && response.data.data) {
          alert('用户名已存在，请重试');
        } else {
          this.proceedWithRegistration();
        }
      })
      .catch(error => {
        if (error.response && error.response.status === 404) {
          this.proceedWithRegistration();
        } else {
          console.error('用户名检查失败', error);
          alert('用户名检查失败，请重试');
        }
      });
    },
    proceedWithRegistration() {
      axios.post('/api/user/register', {
        username: this.registerForm.username,
        phone: this.registerForm.phone,
        password: this.registerForm.password,
        confirmPassword: this.registerForm.confirmPassword,
        userType: this.registerForm.isExpert ? 1 : 0,
      })
      .then(response => {
        if (response.data.code === 1) {
          sessionStorage.setItem('username', response.data.data.username);
          sessionStorage.setItem('userType', response.data.data.userType);

          this.$store.dispatch('fetchUser', {
            username: response.data.data.username,
            userType: response.data.data.userType,
          });
          this.$emit('registerSuccess');
        } else {
          alert('注册失败: ' + response.data.msg);
        }
      })
      .catch(error => {
        console.error('注册请求失败', error);
        alert('网络错误，请重试');
      });
    },
    toggleForm() {
      this.isLogin = !this.isLogin;
    },
  },
};
</script>

<style scoped>
.form-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url('../assets/KnowledgeGraph.jpg'); /* 更新背景图片路径 */
  background-size: cover;
  background-position: center;
}

.form-box {
  background: rgba(255, 255, 255, 0.9);
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  width: 400px;
  text-align: center;
}

.form {
  margin-top: 20px;
}

.button-container {
  display: flex;
  justify-content: center;
}


.toggle-link {
  margin-top: 10px;
  display: block;
}

.is-expert-container {
  text-align: left;
}
</style>