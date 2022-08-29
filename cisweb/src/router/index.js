import Vue from 'vue'
import Router from 'vue-router'
import routes from './routers'
import store from '@/store'
import iView from 'iview'
import { setTitle } from '@/libs/util'
import config from '@/config'
const { homeName } = config

Vue.use(Router)
const router = new Router({
  routes,
  mode: 'history'
})
const LOGIN_PAGE_NAME = 'login'
let start = true
const turnTo = (to, access, next) => {
  if (access === undefined) { // 未登录 重新登录
    next({ replace: true, name: 'login' })
    return
  }
  if (to.name.indexOf('error_') !== -1) { // 跳过权限检测
    next()
    return
  }
  if (access.some(_ => _.menu.name === to.name)) {
    next() // 有权限，可访问
  } else {
    next({ replace: true, name: 'error_401' }) // 无权限，重定向到401页面
  }
}

router.beforeEach((to, from, next) => {
  // 如果是后台登录或者包含/cust的页面 跳过验证权限直接跳转
  if (to.name === 'login') {
    next()
  } else {
    routerNext(to, from, next)
  }
})

const routerNext = (to, from, next) => {
  // 判断是否初始化了路由 如果初始化了 就重新添加业务路由 并且重新请求
  if (start && store.getters.menuList.length !== 0) {
    router.addRoutes(store.getters.menuList)
    start = false
    if (to.fullPath === '/') {
      router.push({ name: to.name })
    } else {
      router.push(to.fullPath)
    }
    return
  }
  iView.LoadingBar.start()
  const thisUser = store.state.user.thisUser
  if (!thisUser.id && to.name !== LOGIN_PAGE_NAME) {
    // 未登录且要跳转的页面不是登录页
    next({
      name: LOGIN_PAGE_NAME // 跳转到登录页
    })
  } else if (!thisUser.id && to.name === LOGIN_PAGE_NAME) {
    // 未登陆且要跳转的页面是登录页
    next() // 跳转
  } else if (thisUser.id && to.name === LOGIN_PAGE_NAME) {
    // 已登录且要跳转的页面是登录页
    next({
      name: homeName // 跳转到homeName页
    })
  } else {
    if (store.state.user.thisUser && store.state.user.thisUser.id) {
      turnTo(to, store.state.user.thisUser.access, next)
    } else {
      store.dispatch('getThisUser').then(user => {
        debugger
        store.state.user.thisUser = user.data
        // 初始化权限列表
        store.state.user.thisUser.access = [ { menu: { name: 'home' } } ]
        for (let r in user.data.roleList) {
          let roleList = user.data.roleList[r]
          for (let j in roleList.jurisdictionList) {
            store.state.user.thisUser.access.push(roleList.jurisdictionList[j])
          }
        }
        // 拉取用户信息，通过用户权限和跳转的页面的name来判断是否有权限访问;access必须是一个数组，如：['super_admin'] ['super_admin', 'admin']
        turnTo(to, store.state.user.thisUser.access, next)
      }).catch(() => {
        next({ replace: true, name: LOGIN_PAGE_NAME }) // 取用户异常 返回登录页
      })
    }
  }
}

router.afterEach(to => {
  setTitle(to, router.app)
  iView.LoadingBar.finish()
  window.scrollTo(0, 0)
})

export default router
