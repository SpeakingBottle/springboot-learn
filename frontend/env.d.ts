/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

// 修复 TS7016: 声明 element-plus 中文语言包模块类型
declare module 'element-plus/dist/locale/zh-cn.mjs'
