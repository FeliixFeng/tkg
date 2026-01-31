import { createRouter, createWebHistory } from 'vue-router';
import store from '@/store';

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: {
      requiresAuth: false,
      title: '登录'
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: {
      requiresAuth: true,
      title: '纺织知识图谱'
    }
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

router.beforeEach(async (to, from, next) => {
  await store.dispatch('auth/initializeFromStorage');

  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const isAuthenticated = store.getters['auth/isAuthenticated'];

  if (requiresAuth && !isAuthenticated) {
    next('/login');
  } else if (to.path === '/login' && isAuthenticated) {
    next('/dashboard');
  } else {
    next();
  }
});

export default router;
