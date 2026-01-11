<template>
  <div id="app">
    <LoginRegister 
      v-if="!isLoggedIn" 
      @loginSuccess="handleLoginSuccess" 
      @registerSuccess="handleRegisterSuccess" 
    />
    <Main v-else />
  </div>
</template>

<script>
import LoginRegister from './components/LoginRegister.vue';
import Main from './components/Main.vue';

export default {
  name: 'App',
  components: {
    LoginRegister,
    Main,
  },
  data() {
    return {
      isLoggedIn: false, // 登录状态
    };
  },
  methods: {
    handleLoginSuccess() {
      this.isLoggedIn = true; // 登录成功后更新状态
    },
    handleRegisterSuccess() {
      this.isLoggedIn = true; // 注册成功后更新状态
    },
  },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 0;
}
</style>