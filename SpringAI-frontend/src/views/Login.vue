<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center px-4">
    <div class="max-w-md w-full">
      <!-- 登录卡片 -->
      <div class="bg-white rounded-2xl shadow-xl p-8 text-center">
        <!-- 标题 -->
        <div class="mb-8">
          <h1 class="text-2xl font-bold text-gray-900 mb-2">Spring AI 机器人</h1>
          <p class="text-gray-600">请使用微信扫码登录</p>
        </div>

        <!-- 二维码区域 -->
        <div class="mb-8">
          <div class="bg-gray-50 rounded-xl p-6 mb-4">
            <div v-if="qrCodeUrl" class="flex justify-center">
              <img :src="qrCodeUrl" alt="登录二维码" class="w-48 h-48 border border-gray-200 rounded-lg" />
            </div>
            <div v-else class="w-48 h-48 mx-auto bg-gray-200 rounded-lg flex items-center justify-center">
              <div class="text-center">
                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto mb-2"></div>
                <p class="text-gray-500 text-sm">加载中...</p>
              </div>
            </div>
          </div>
          
          <!-- 状态提示 -->
          <div class="text-sm text-gray-600">
            <p v-if="loginStatus === 'waiting'">请使用微信扫描上方二维码</p>
            <p v-else-if="loginStatus === 'scanned'" class="text-blue-600">✓ 扫码成功，请在手机上确认登录</p>
            <p v-else-if="loginStatus === 'success'" class="text-green-600">✓ 登录成功，正在跳转...</p>
            <p v-else-if="loginStatus === 'expired'" class="text-red-600">二维码已过期，请刷新重试</p>
          </div>
        </div>

        <!-- 刷新按钮 -->
        <button 
          @click="refreshQrCode" 
          :disabled="loading"
          class="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white font-medium py-3 px-4 rounded-lg transition-colors"
        >
          <span v-if="loading">刷新中...</span>
          <span v-else>刷新二维码</span>
        </button>

        <!-- 底部提示 -->
        <div class="mt-6 text-xs text-gray-500">
          <p>扫码登录即表示同意用户协议和隐私政策</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 响应式数据
const qrCodeUrl = ref('')
const loginStatus = ref('waiting') // waiting, scanned, success, expired
const loading = ref(false)
const pollTimer = ref(null)
const qrCodeId = ref('')

// 获取二维码
const getQrCode = async () => {
  try {
    loading.value = true
    // 调用后端接口获取二维码
    const response = await fetch('http://localhost:8080/api/auth/qrcode', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })
    
    if (response.ok) {
      const result = await response.json()
      // 修复：正确获取后端返回的字段
      qrCodeUrl.value = result.data.qrCodeImage  // 改为 qrCodeImage
      qrCodeId.value = result.data.qrCodeId
      loginStatus.value = 'waiting'
      
      // 开始轮询登录状态
      startPolling()
    } else {
      console.error('获取二维码失败')
    }
  } catch (error) {
    console.error('获取二维码错误:', error)
  } finally {
    loading.value = false
  }
}

// 轮询登录状态
const startPolling = () => {
  if (pollTimer.value) {
    clearInterval(pollTimer.value)
  }
  
  pollTimer.value = setInterval(async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/auth/status?qrCodeId=${qrCodeId.value}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      })
      
      if (response.ok) {
        const result = await response.json()
        
        // 检查响应结构
        if (result.code === 200 && result.data) {
          const data = result.data
          
          switch (data.status) {
            case 'waiting':
              loginStatus.value = 'waiting'
              break
            case 'success':
              loginStatus.value = 'success'
              // 保存登录信息
              if (data.userInfo) {
                localStorage.setItem('userInfo', JSON.stringify(data.userInfo))
                localStorage.setItem('authToken', 'logged_in')
              }
              // 延迟跳转，让用户看到成功提示
              setTimeout(() => {
                router.push('/chat')
              }, 1500)
              stopPolling()
              break
            case 'expired':
              loginStatus.value = 'expired'
              stopPolling()
              break
            default:
              // 未知状态，继续轮询
              break
          }
        }
      } else {
        console.error('请求失败，状态码:', response.status)
      }
    } catch (error) {
      console.error('轮询登录状态错误:', error)
    }
  }, 2000) // 每2秒轮询一次
}

// 停止轮询
const stopPolling = () => {
  if (pollTimer.value) {
    clearInterval(pollTimer.value)
    pollTimer.value = null
  }
}

// 刷新二维码
const refreshQrCode = () => {
  stopPolling()
  getQrCode()
}

// 组件挂载时获取二维码
onMounted(() => {
  getQrCode()
})

// 组件卸载时清理定时器
onBeforeUnmount(() => {
  stopPolling()
})
</script>

<style scoped>
/* 自定义样式 */
.animate-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>