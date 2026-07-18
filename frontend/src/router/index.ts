import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/pages/LoginPage.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: () => import('@/pages/DashboardPage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/leave/create',
      name: 'LeaveCreate',
      component: () => import('@/pages/LeaveCreatePage.vue'),
      meta: { requiresAuth: true, roles: ['STUDENT'] },
    },
    {
      path: '/leave/list',
      name: 'LeaveList',
      component: () => import('@/pages/LeaveListPage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/review/pending',
      name: 'ReviewList',
      component: () => import('@/pages/ReviewListPage.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER', 'DEAN'] },
    },
    {
      path: '/users',
      name: 'UserList',
      component: () => import('@/pages/UserListPage.vue'),
      meta: { requiresAuth: true, roles: ['DEAN'] },
    },
    {
      path: '/',
      redirect: '/dashboard',
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/dashboard',
    },
  ],
})

// 导航守卫
router.beforeEach(async (to, _from, next) => {
  const authStore = useAuthStore()

  // 已登录访问登录页 → 跳转首页
  if (to.path === '/login' && authStore.isLoggedIn) {
    return next('/dashboard')
  }

  // 需要登录的页面
  if (to.meta.requiresAuth) {
    if (!authStore.isLoggedIn) {
      return next('/login')
    }
    // 如果已登录但用户信息未加载，先加载
    if (!authStore.user) {
      await authStore.fetchUser()
    }
    // 角色权限检查
    if (to.meta.roles && Array.isArray(to.meta.roles)) {
      const roles = to.meta.roles as string[]
      if (authStore.role && !roles.includes(authStore.role)) {
        return next('/dashboard')
      }
    }
  }

  next()
})

export default router
