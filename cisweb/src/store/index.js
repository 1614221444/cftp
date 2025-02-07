import Vue from 'vue'
import Vuex from 'vuex'
import createVuexAlong from 'vuex-along'

import user from './module/sys/user'
import app from './module/app'
import menu from './module/sys/menu'
import role from './module/sys/role'
import log from './module/sys/log'
import dict from './module/sys/dict'
import notice from './module/sys/notice'
import job from './module/sys/job'
import controller from './module/cis/controller'
import authentication from './module/cis/authentication'
import joinLog from './module/cis/joinLog'
import model from './module/cis/model'
Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    //
  },
  mutations: {
    //
  },
  actions: {
    //
  },
  modules: {
    user,
    app,
    menu,
    role,
    log,
    dict,
    notice,
    job,
    controller,
    authentication,
    joinLog,
    model
  },
  plugins: [
    createVuexAlong({
      name: 'cylt' // 设置保存的集合名字，避免同站点下的多项目数据冲突
    })
  ]
})
