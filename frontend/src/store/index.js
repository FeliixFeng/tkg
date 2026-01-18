import { createStore } from 'vuex';

export default createStore({
  state: {
    user: {
      username: '',
      userType: '',
      userId: '',
      password: '',
      token: ''
    }
  },
  mutations: {
    setUser(state, user) {
      state.user = user;
    }
  },
  actions: {
    fetchUser({ commit }, user) {
      commit('setUser', user);
    }
  }
});