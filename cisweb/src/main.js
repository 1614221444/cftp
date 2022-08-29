// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import store from './store'
import iView from 'iview'
import i18n from '@/locale'
import config from '@/config'
import importDirective from '@/directive'
import { directive as clickOutside } from 'v-click-outside-x'
import installPlugin from '@/plugin'
import './index.less'
import '@/assets/icons/iconfont.css'
import TreeTable from 'tree-table-vue'
import VOrgTree from 'v-org-tree'
import 'v-org-tree/dist/v-org-tree.css'
// 实际打包时应该不引入mock
/* eslint-disable */
//if (process.env.NODE_ENV !== 'production') require('@/mock')

Vue.use(iView, {
  i18n: (key, value) => i18n.t(key, value)
})
Vue.use(TreeTable)
Vue.use(VOrgTree)
/**
 * @description 注册admin内置插件
 */
installPlugin(Vue)
/**
 * @description 生产环境关掉提示
 */
Vue.config.productionTip = false
/**
 * @description 全局注册应用配置
 */
Vue.prototype.$config = config
/**
 * 注册指令
 */
importDirective(Vue)
Vue.directive('clickOutside', clickOutside)

/**
 * 判断有没有权限显示该按钮
 */
Vue.directive("jurisdiction", {
  inserted: function(el, binding) {
    // 取当前菜单
    let menu = ''
    let temporaryEl = el
    while (temporaryEl) {
      if (temporaryEl.dataset.menu) {
        menu = temporaryEl.dataset.menu
        break
      }
      temporaryEl = temporaryEl.parentElement
    }
    // 判断该按钮是否有权限
    if (!jurisdiction(menu, binding.value)) {
      el.parentNode.removeChild(el)
    }
  }
})


/**
 * 判断当前节点是否有权限 （js用的）
 */
Vue.prototype.$jurisdiction = jurisdiction

/**
 * 判断当前操作是否有权限
 * @param menu 菜单名称
 * @param operation 操作
 * @return boolean 是否有权限
 */
function jurisdiction(menu, operation) {
  // 取当前菜单的权限
  let access = store.state.user.thisUser.access
  let display = false
  for (let i in access) {
    if(access[i].menu.name === menu){
      // 判断是否显示该标签
      if (access[i]) {
        if (operation === 'edit') { // 判断是否有编辑权限
          display = access[i].edit
        } else if (operation === 'del') { // 判断是否有删除权限
          display = access[i].del
        }
      }
      // 判断如果有权限则直接退出循环
      if (display) {
        break
      }
    }
  }

  return display
}

/**
 * 取数据字典
 * @param key
 * @param val
 */
Vue.prototype.$dict = dict
function dict(key, val) {
  let dict
  for (let i in store.state.app.dictList) {
    dict = store.state.app.dictList[i]
    if (dict.dictKey === key && dict.dictValue == val) {
      return dict.title
    }
  }
}


/**
 * 取数据字典数组
 */
Vue.prototype.$dictList = dictList
function dictList(key) {
  let dictList = []
  let dict
  for (let i in store.state.app.dictList) {
    dict = store.state.app.dictList[i]
    if (dict.dictKey == key) {
      dictList.push(dict)
    }
  }
  return dictList
}

/**
 * @description 全局注册应用配置
 */
Vue.prototype.jurisdiction = config

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  i18n,
  store,
  render: h => h(App)
})
