import http from '@/http';

const state = {
  token: sessionStorage.getItem('token') || '',
  user: {
    id: null,
    username: sessionStorage.getItem('username') || '',
    userType: parseInt(sessionStorage.getItem('userType') || '0', 10),
    userId: parseInt(sessionStorage.getItem('userId') || '0', 10),
    phone: sessionStorage.getItem('phone') || '',
    avatar: sessionStorage.getItem('avatar') || ''
  },
  isLoggedIn: !!sessionStorage.getItem('token')
};

const getters = {
  isAuthenticated: (state) => state.isLoggedIn,
  currentUser: (state) => state.user,
  userToken: (state) => state.token,
  isExpert: (state) => state.user.userType === 1
};

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token;
    state.isLoggedIn = !!token;
    if (token) {
      sessionStorage.setItem('token', token);
    } else {
      sessionStorage.removeItem('token');
    }
  },
  SET_USER(state, user) {
    state.user = { ...state.user, ...user };
    // 同步到 sessionStorage
    if (user.username !== undefined) sessionStorage.setItem('username', user.username);
    if (user.userType !== undefined) sessionStorage.setItem('userType', user.userType);
    if (user.userId !== undefined) sessionStorage.setItem('userId', user.userId);
    if (user.phone !== undefined) sessionStorage.setItem('phone', user.phone);
    if (user.avatar !== undefined) sessionStorage.setItem('avatar', user.avatar);
    if (user.id !== undefined) sessionStorage.setItem('userId', user.id);
  },
  CLEAR_AUTH(state) {
    state.token = '';
    state.user = {
      id: null,
      username: '',
      userType: 0,
      userId: 0,
      phone: '',
      avatar: ''
    };
    state.isLoggedIn = false;
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('username');
    sessionStorage.removeItem('userType');
    sessionStorage.removeItem('userId');
    sessionStorage.removeItem('phone');
    sessionStorage.removeItem('avatar');
  },
  INITIALIZE_FROM_STORAGE(state) {
    state.token = sessionStorage.getItem('token') || '';
    state.isLoggedIn = !!state.token;
    state.user = {
      id: parseInt(sessionStorage.getItem('userId') || '0', 10) || null,
      username: sessionStorage.getItem('username') || '',
      userType: parseInt(sessionStorage.getItem('userType') || '0', 10),
      userId: parseInt(sessionStorage.getItem('userId') || '0', 10),
      phone: sessionStorage.getItem('phone') || '',
      avatar: sessionStorage.getItem('avatar') || ''
    };
  }
};

const actions = {
  async login({ commit }, credentials) {
    try {
      const response = await http.post('/api/user/login', {
        username: credentials.username,
        password: credentials.password
      });

      if (response.data.code === 1) {
        const userData = response.data.data;
        commit('SET_TOKEN', userData.token);
        commit('SET_USER', {
          id: userData.id,
          username: userData.username,
          userType: userData.userType,
          userId: userData.id,
          phone: userData.phone,
          avatar: userData.avatar
        });
        return { success: true, data: userData };
      } else {
        return { success: false, message: response.data.msg || '登录失败' };
      }
    } catch (error) {
      console.error('登录失败:', error);
      if (error.response && error.response.data) {
        return { success: false, message: error.response.data.msg || '登录失败' };
      }
      return { success: false, message: '网络错误，请重试' };
    }
  },

  async register({ commit }, credentials) {
    try {
      const response = await http.post('/api/user/register', {
        username: credentials.username,
        phone: credentials.phone,
        password: credentials.password,
        userType: credentials.userType
      });

        if (response.data.code === 1) {
          const userData = response.data.data;
          commit('SET_TOKEN', userData.token || '');
          commit('SET_USER', {
            id: userData.id,
            username: userData.username,
            userType: userData.userType,
            userId: userData.id,
            phone: userData.phone,
            avatar: userData.avatar || ''
          });
          return { success: true, data: userData };
        }
    } catch (error) {
      console.error('注册失败:', error);
      if (error.response && error.response.data) {
        return { success: false, message: error.response.data.msg || '注册失败' };
      }
      return { success: false, message: '网络错误，请重试' };
    }
  },

  async logout({ commit }) {
    commit('CLEAR_AUTH');
    return { success: true };
  },

  checkAuth({ commit }) {
    const token = sessionStorage.getItem('token');
    if (token) {
      commit('INITIALIZE_FROM_STORAGE');
      return true;
    }
    return false;
  },

  initializeFromStorage({ commit }) {
    commit('INITIALIZE_FROM_STORAGE');
  }
};

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
};
