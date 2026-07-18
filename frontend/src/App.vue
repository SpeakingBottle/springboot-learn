<template>
  <el-config-provider :locale="zhCn">
    <!-- 登录页不需要布局壳 -->
    <template v-if="route.path === '/login'">
      <router-view />
    </template>
    <!-- 其他页面用 AppLayout 包裹 -->
    <template v-else>
      <AppLayout />
    </template>
  </el-config-provider>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import AppLayout from '@/components/AppLayout.vue'

const route = useRoute()
</script>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}
</style>
