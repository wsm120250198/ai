import { createRouter, createWebHashHistory } from 'vue-router'

// 统一在这里声明所有路由
const routes = [
    {
        path: '/', // 默认路由 - 登录页面
        component: () => import('@/views/Login.vue'),
        meta: {
            title: 'Spring AI 机器人 - 登录'
        }
    },
    {
        path: '/chat', // 聊天页面路由
        component: () => import('@/views/Index.vue'),
        meta: {
            title: 'Spring AI 机器人首页'
        }
    }
]

// 创建路由
const router = createRouter({
    // 指定路由策略，hash 模式指的是 URL 的路径是通过 hash 符号（#）进行标识
    history: createWebHashHistory(),
    // routes: routes 的缩写
    routes, 
})

// 路由守卫 - 检查登录状态
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('authToken')
    
    // 如果访问聊天页面但没有登录，重定向到登录页
    if (to.path === '/chat' && !token) {
        next('/')
    } else {
        next()
    }
})

// ES6 模块导出语句，它用于将 router 对象导出，以便其他文件可以导入和使用这个对象
export default router
