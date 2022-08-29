import {
  addTreeList
} from '@/libs/util'
import { getMenuList, saveMenu, del as delMenu } from '@/api/sys/menu'

export default {
  state: {
    menuList: [],
    info: {}
  },
  mutations: {
    menuList (state, list) {
      list = addTreeList(list, (data) => {
        return {
          id: data.id,
          pid: data.pid,
          icon: data.icon,
          title: data.name,
          expand: true,
          data: data,
          children: []
        }
      })
      state.menuList = list
    },
    getMenu (state, data) {
      state.info = data
    },
    insertMenu (state, pid) {
      state.info = []
      state.info.pid = pid
      state.info.showMenu = true
    }
  },
  actions: {
    getSysMenuList ({ commit, rootState }, params) {
      getMenuList(params).then(res => {
        commit('menuList', res.data)
      }).catch(e => {
        console.log(e)
      })
    },
    getSysMenu ({ commit, rootState }, params) {
      commit('getMenu', params.data)
    },
    saveSysMenu ({ commit, rootState }) {
      return saveMenu(rootState.menu.info).then(res => {
        getMenuList().then(res => {
          commit('menuList', res.data)
        }).catch(e => {
          console.log(e)
        })
      }).catch(e => {
        console.log(e)
      })
    },
    insertMenu ({ commit, rootState }, pid) {
      commit('insertMenu', pid)
    },
    delSysMenu ({ commit, rootState }, id) {
      return delMenu(id)
    }
  }
}
