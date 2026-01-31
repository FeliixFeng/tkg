<template>
  <div class="login-container">
    <!-- 半透明遮罩层 -->
    <div class="overlay"></div>
    
    <!-- 左侧品牌展示区 -->
    <div class="brand-section">
      <div class="brand-content">
        <div class="logo">
          <el-icon :size="64" color="#fff"><Collection /></el-icon>
        </div>
        <h1 class="brand-title">纺织知识图谱</h1>
        <p class="brand-subtitle">Textile Knowledge Graph</p>
        <div class="brand-divider"></div>
        <p class="brand-description">
          探索纺织世界的智慧网络<br>
          构建行业知识的数字化桥梁
        </p>
      </div>
      <!-- 装饰性元素 -->
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>

    <!-- 右侧表单区域 -->
    <div class="form-section">
      <div class="form-card">
        <!-- 表单头部 -->
        <div class="form-header">
          <div class="form-icon">
            <el-icon :size="32" color="#1976d2"><UserFilled v-if="isLogin" />    <CirclePlus v-else /></el-icon>
          </div>
          <h2 class="form-title">{{ isLogin ? '欢迎回来' : '创建账号' }}</h2>
          <p class="form-subtitle">{{ isLogin ? '请登录您的账户' : '请填写以下信息完成注册' }}</p>
        </div>

        <!-- 表单内容 -->
        <el-form :model="currentForm" class="login-form" @keyup.enter="handleSubmit">
          <!-- 用户名 -->
          <el-form-item>
            <el-input
              v-model="currentForm.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              size="large"
              class="custom-input"
            />
          </el-form-item>

          <!-- 手机号（仅注册） -->
          <el-form-item v-if="!isLogin">
            <el-input
              v-model="currentForm.phone"
              placeholder="请输入手机号"
              :prefix-icon="Phone"
              size="large"
              class="custom-input"
            />
          </el-form-item>

          <!-- 密码 -->
          <el-form-item>
            <el-input
              v-model="currentForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              size="large"
              class="custom-input"
              show-password
            />
          </el-form-item>

          <!-- 确认密码（仅注册） -->
          <el-form-item v-if="!isLogin">
            <el-input
              v-model="currentForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码确认"
              :prefix-icon="Key"
              size="large"
              class="custom-input"
              show-password
            />
          </el-form-item>

          <!-- 专家选项（仅注册） -->
          <el-form-item v-if="!isLogin" class="expert-option">
            <el-checkbox v-model="currentForm.isExpert" size="large">
              <span class="checkbox-label">注册为专家用户</span>
              <el-tooltip content="专家用户可以审核其他用户提交的知识" placement="top">
                <el-icon class="info-icon"><InfoFilled /></el-icon>
              </el-tooltip>
            </el-checkbox>
          </el-form-item>

          <!-- 登录选项（仅登录） -->
          <el-form-item v-if="isLogin" class="login-options">
            <el-checkbox v-model="rememberMe" size="small">记住我</el-checkbox>
          </el-form-item>

          <!-- 提交按钮 -->
          <el-form-item class="submit-container">
            <el-button
              type="primary"
              size="large"
              class="submit-btn"
              :loading="loading"
              @click="handleSubmit"
            >
              <el-icon v-if="!loading" class="btn-icon"><ArrowRight /></el-icon>
              <span>{{ isLogin ? '立即登录' : '创建账号' }}</span>
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 底部切换 -->
        <div class="form-footer">
          <div class="divider">
            <span class="divider-line"></span>
            <span class="divider-text">{{ isLogin ? '还没有账号？' : '已有账号？' }}</span>
            <span class="divider-line"></span>
          </div>
          <el-button
            type="primary"
            link
            class="switch-btn"
            @click="toggleForm"
          >
            {{ isLogin ? '立即注册' : '立即登录' }}
            <el-icon class="switch-icon"><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex';
import { ElMessage } from 'element-plus';
import http from '@/http';
import {
  User,
  UserFilled,
  CirclePlus,
  Phone,
  Lock,
  Key,
  ArrowRight,
  Collection,
  InfoFilled
} from '@element-plus/icons-vue';

export default {
  name: 'Login',
  components: {
    UserFilled,
    CirclePlus,
    Collection,
    InfoFilled
  },
  data() {
    return {
      isLogin: true,
      loading: false,
      rememberMe: false,
      User,
      Phone,
      Lock,
      Key,
      ArrowRight,
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
        ElMessage.success('登录成功，欢迎回来！');
        this.$router.push('/dashboard');
      } else {
        ElMessage.error(result.message || '登录失败，请检查用户名和密码');
      }
    },

    async handleRegister() {
      // 验证必填项
      if (!this.registerForm.username || !this.registerForm.phone || !this.registerForm.password) {
        ElMessage.warning('请填写所有必填项');
        return;
      }

      // 验证密码一致性
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        ElMessage.warning('两次输入的密码不一致');
        return;
      }

      // 验证手机号格式
      const phoneRegex = /^1[3-9]\d{9}$/;
      if (!phoneRegex.test(this.registerForm.phone)) {
        ElMessage.warning('手机号格式不正确');
        return;
      }

      // 验证用户名是否已存在
      try {
        const response = await http.get(`/api/user/get_user/${this.registerForm.username}`);
        if (response.data && response.data.data) {
          ElMessage.warning('用户名已存在，请更换其他用户名');
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
        ElMessage.success('注册成功，欢迎加入！');
        this.$router.push('/dashboard');
      } else {
        ElMessage.error(result.message || '注册失败，请稍后重试');
      }
    },

    toggleForm() {
      this.isLogin = !this.isLogin;
      // 清空表单
      this.loginForm = { username: '', password: '' };
      this.registerForm = {
        username: '',
        phone: '',
        password: '',
        confirmPassword: '',
        isExpert: false,
      };
    },
  },
};
</script>

<style scoped>
/* 主容器 */
.login-container {
  display: flex;
  min-height: 100vh;
  background-image: url('@/assets/KnowledgeGraph_no_char.png');
  background-size: cover;
  background-position: center;
  position: relative;
  overflow: hidden;
}

/* 半透明遮罩层 */
.overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(26, 35, 126, 0.70) 0%,
    rgba(57, 73, 171, 0.60) 50%,
    rgba(26, 35, 126, 0.70) 100%
  );
  z-index: 1;
}

/* 左侧品牌区域 */
.brand-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  z-index: 2;
}

.brand-content {
  text-align: center;
  color: white;
  max-width: 500px;
}

.logo {
  margin-bottom: 24px;
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.brand-title {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 12px 0;
  letter-spacing: 6px;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.4);
}

.brand-subtitle {
  font-size: 20px;
  font-weight: 300;
  margin: 0 0 32px 0;
  opacity: 0.95;
  letter-spacing: 3px;
}

.brand-divider {
  width: 100px;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.8), transparent);
  margin: 0 auto 32px;
}

.brand-description {
  font-size: 18px;
  line-height: 2;
  opacity: 0.95;
  font-weight: 400;
  letter-spacing: 1px;
}

/* 装饰性圆圈 */
.decoration-circle {
  position: absolute;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.1);
  animation: pulse 4s ease-in-out infinite;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: 10%;
  left: 10%;
  animation-delay: 1s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 20%;
  right: 5%;
  animation-delay: 2s;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.1; }
  50% { transform: scale(1.1); opacity: 0.3; }
}

/* 右侧表单区域 */
.form-section {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
  z-index: 2;
}

.form-card {
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 36px 40px;
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(255, 255, 255, 0.2) inset;
  animation: slideIn 0.6s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 表单头部 */
.form-header {
  text-align: center;
  margin-bottom: 20px;
}

.form-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  box-shadow: 0 4px 12px rgba(25, 118, 210, 0.2);
}

.form-title {
  font-size: 28px;
  font-weight: 600;
  color: #1a237e;
  margin: 0 0 8px 0;
}

.form-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}

/* 表单样式 */
.login-form {
  margin-top: 16px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 16px;
}

/* 自定义输入框 */
:deep(.custom-input .el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  padding: 4px 16px;
  transition: all 0.3s ease;
}

:deep(.custom-input .el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(25, 118, 210, 0.15);
}

:deep(.custom-input .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.2), 0 4px 12px rgba(25, 118, 210, 0.15);
}

:deep(.custom-input .el-input__inner) {
  height: 44px;
  font-size: 15px;
}

/* 专家选项 */
.expert-option {
  margin-bottom: 16px;
}

.checkbox-label {
  font-size: 14px;
  color: #333;
  margin-right: 4px;
}

.info-icon {
  font-size: 14px;
  color: #999;
  cursor: help;
}

/* 登录选项 */
.login-options {
  margin-bottom: 8px;
}

/* 提交按钮容器 */
.submit-container {
  margin-top: 24px;
  margin-bottom: 0;
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #1976d2 0%, #1565c0 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(25, 118, 210, 0.4);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(25, 118, 210, 0.5);
  background: linear-gradient(135deg, #1e88e5 0%, #1976d2 100%);
}

.submit-btn:active {
  transform: translateY(0);
}

.btn-icon {
  font-size: 18px;
}

/* 表单底部 */
.form-footer {
  margin-top: 20px;
  text-align: center;
}

.divider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.divider-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, transparent, #ddd, transparent);
}

.divider-text {
  padding: 0 12px;
  font-size: 13px;
  color: #999;
}

.switch-btn {
  font-size: 15px;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.switch-btn:hover .switch-icon {
  transform: translateX(4px);
}

.switch-icon {
  font-size: 14px;
  transition: transform 0.3s ease;
}

/* 响应式设计 */
@media screen and (max-width: 1024px) {
  .brand-section {
    display: none;
  }
  
  .form-section {
    width: 100%;
    padding: 20px;
  }
  
  .form-card {
    max-width: 420px;
  }
}

@media screen and (max-width: 480px) {
  .form-card {
    padding: 32px 24px;
    border-radius: 20px;
  }
  
  .brand-title {
    font-size: 32px;
  }
}
</style>
