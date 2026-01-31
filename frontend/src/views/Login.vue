<template>
  <div class="form-container">
    <div class="form-box">
      <h2 v-if="isLogin">系统登录</h2>
      <h2 v-else>用户注册</h2>

      <el-form :model="currentForm" class="form">
        <el-form-item>
          <el-input
            v-model="currentForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item v-if="!isLogin">
          <el-input
            v-model="currentForm.phone"
            placeholder="请输入手机号"
            prefix-icon="Phone"
          />
        </el-form-item>

        <el-form-item>
          <el-input
            v-model="currentForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item v-if="!isLogin">
          <el-input
            v-model="currentForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item v-if="!isLogin" class="is-expert-container">
          <el-checkbox v-model="currentForm.isExpert">是否为专家</el-checkbox>
        </el-form-item>

        <el-form-item class="button-container">
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            {{ isLogin ? '登录' : '注册' }}
          </el-button>
        </el-form-item>

        <el-link @click="toggleForm" class="toggle-link">
          {{ isLogin ? '没有账号？点击我进行注册' : '已经有账号？点击我进行登录' }}
        </el-link>
      </el-form>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex';
import { ElMessage } from 'element-plus';
import http from '@/http';

export default {
  name: 'Login',
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
      loading: false,
    };
  },
  computed: {
    currentForm() {
      return this.isLogin ? this.loginForm : this.registerForm;
    },
  },
  methods: {
    ...mapActions('auth', ['login', 'register']),

    async handleSubmit() {
      if (this.isLogin) {
        await this.handleLogin();
      } else {
        await this.handleRegister();
      }
    },

    async handleLogin() {
      if (!this.loginForm.username || !this.loginForm.password) {
        ElMessage.warning('请填写用户名和密码');
        return;
      }

      this.loading = true;
      const result = await this.login(this.loginForm);
      this.loading = false;

      if (result.success) {
        ElMessage.success('登录成功');
        this.$router.push('/dashboard');
      } else {
        ElMessage.error(result.message);
      }
    },

    async handleRegister() {
      if (!this.registerForm.username || !this.registerForm.phone || !this.registerForm.password) {
        ElMessage.warning('请填写所有必填项');
        return;
      }

      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        ElMessage.warning('两次输入的密码不一致');
        return;
      }

      const phoneRegex = /^1[3-9]\d{9}$/;
      if (!phoneRegex.test(this.registerForm.phone)) {
        ElMessage.warning('手机号格式不正确');
        return;
      }

      try {
        const response = await http.get(`/api/user/get_user/${this.registerForm.username}`);
        if (response.data && response.data.data) {
          ElMessage.warning('用户名已存在');
          return;
        }
      } catch (error) {
        if (error.response && error.response.status !== 404) {
          ElMessage.error('用户名检查失败，请重试');
          return;
        }
      }

      this.loading = true;
      const result = await this.register({
        username: this.registerForm.username,
        phone: this.registerForm.phone,
        password: this.registerForm.password,
        userType: this.registerForm.isExpert ? 1 : 0
      });
      this.loading = false;

      if (result.success) {
        ElMessage.success('注册成功');
        this.$router.push('/dashboard');
      } else {
        ElMessage.error(result.message);
      }
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
  background-image: url('@/assets/KnowledgeGraph.jpg');
  background-size: cover;
  background-position: center;
}

.form-box {
  background: rgba(255, 255, 255, 0.95);
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  width: 420px;
  text-align: center;
  margin-left: auto;
  margin-right: 15%;
}

.form {
  margin-top: 20px;
}

.button-container {
  display: flex;
  justify-content: center;
}

.toggle-link {
  margin-top: 15px;
  display: block;
  font-size: 14px;
}

.is-expert-container {
  text-align: left;
}
</style>
