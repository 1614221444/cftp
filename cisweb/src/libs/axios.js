import axios from 'axios'
import store from '@/store'
import qs from 'qs'

class HttpRequest {
  constructor (baseUrl = baseURL) {
    this.baseUrl = baseUrl
    this.queue = {}
  }
  getInsideConfig () {
    const config = {
      baseURL: this.baseUrl,
      headers: {
        //
      },
      default: {
        withCredentials: true
      }
    }
    return config
  }
  destroy (url) {
    delete this.queue[url]
    if (!Object.keys(this.queue).length) {
      // Spin.hide()
    }
  }
  interceptors (instance, url) {
    // 请求拦截
    instance.interceptors.request.use(config => {
      if (config.data && typeof (config.data) !== 'string') {
        // 标识参数是否包含json
        let isJson = config.data.isJson
        config.data = qs.stringify(config.data)
        if (isJson === undefined || isJson === false) {
          // 兼容js对象嵌套
          while (config.data.indexOf('%5B') !== -1) {
            // 判断plantSite[?]判断当前元素是否为嵌套数组 注：如果是嵌套数组则不做任何处理（先把[]替换成一个临时字符下面再将它还原回来）
            if (!isNaN(config.data.substring(config.data.indexOf('%5B') + 3, config.data.indexOf('%5D')))) {
              config.data = config.data.replace('%5B', '「')
              config.data = config.data.replace('%5D', '」')
            } else { // 否则当前元素是嵌套对象
              config.data = config.data.replace('%5B', '.')
              config.data = config.data.replace('%5D', '')
            }
          }
          while (config.data.indexOf('「') !== -1) {
            config.data = config.data.replace('「', '%5B')
            config.data = config.data.replace('」', '%5D')
          }
        }
      }
      // 添加全局的loading...
      if (!Object.keys(this.queue).length) {
        // Spin.show() // 不建议开启，因为界面不友好
      }
      this.queue[url] = true
      return config
    }, error => {
      return Promise.reject(error)
    })
    // 响应拦截
    instance.interceptors.response.use(res => {
      this.destroy(url)
      const { data, status } = res
      return { data, status }
    }, error => {
      this.destroy(url)
      if (error.message === 'Network Error') {
        store.getters.Message.error(store.getters.t('system.networkError'))
      } else if (error.response.data.message === '未登录') {
        store.dispatch('handleLogOut').then(
          () => {
            store.getters.Message.error(store.getters.t('system.loginOvertime'))
            setTimeout(() => {
              location.reload()
            }, 2000)
          }
        )
      } else if (error.response.data.message === '权限不足') {
        store.getters.Message.error(store.getters.t('system.insufficientAuthority'))
      }
      // let errorInfo = error.response
      // addErrorLog(errorInfo)
      return Promise.reject(error.response)
    })
  }
  request (options) {
    const instance = axios.create({
      changeOrigin: true,
      withCredentials: true // cros with cookie
    })
    options = Object.assign(this.getInsideConfig(), options)
    this.interceptors(instance, options.url)
    return instance(options)
  }
}
export default HttpRequest
