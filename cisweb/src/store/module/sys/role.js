import { getRoleList, getRoleNoPageList, save as saveRole, del as delRole } from '@/api/sys/role'
import { ergodicTree } from '@/libs/util'
export default {
  state: {
    roleList: [],
    info: {},
    loading: false,
    query: {
      pageNumber: 1,
      totalNumber: 0,
      singlePage: 20
    },
    roleNoPageList: []
  },
  mutations: {
    roleList (state, page) {
      if (page !== undefined) {
        state.roleList = page.records
        state.query.totalNumber = page.total
      }
    },
    getRole (state, data) {
      state.info = data
    },
    insertRole (state) {
      state.info = {}
    },
    setRoleNoPageList (state, list) {
      state.roleNoPageList = list
    },
    // 封装角色与菜单关系 前台展示的结构
    setJurisdictionList (state, menu) {
      state.info.newJurisdictionList = JSON.parse(JSON.stringify(menu))
      if (state.info.jurisdictionList === undefined) {
        state.info.jurisdictionList = []
      }
      ergodicTree(state.info.newJurisdictionList, (data) => {
        // 是否展开
        data.expand = true
        for (let i in state.info.jurisdictionList) {
          if (state.info.jurisdictionList[i].menu) {
            if (data.id === state.info.jurisdictionList[i].menu.id) {
              data.checked = true
              data.del = state.info.jurisdictionList[i].del
              data.edit = state.info.jurisdictionList[i].edit
              break
            }
          }
        }
      })
    },
    // 封装提交角色与菜单关系的数据
    getJurisdictionList (state) {
      state.info.jurisdictionList = []
      ergodicTree(state.info.newJurisdictionList, (data) => {
        if (data.checked) {
          let jurisdiction = {
            roleId: state.info.id,
            menu: data.data,
            del: data.del,
            edit: data.edit
          }
          state.info.jurisdictionList.push(jurisdiction)
        }
      })
    }
  },
  actions: {
    getRoleList ({ commit, rootState }, params) {
      rootState.role.loading = true
      getRoleList(params).then(res => {
        commit('roleList', res.data)
        rootState.role.loading = false
      }).catch(e => {
        console.log(e)
      })
    },
    getRoleNoPageList ({ commit, rootState }, params) {
      getRoleNoPageList(params).then(res => {
        commit('setRoleNoPageList', res.data)
      }).catch(e => {
        console.log(e)
      })
    },
    getSysRole ({ commit, rootState }, params) {
      commit('getRole', params)
      commit('setJurisdictionList', rootState.app.menuList)
    },
    saveSysRole ({ commit, rootState }) {
      commit('getJurisdictionList')
      return saveRole(rootState.role.info)
    },
    insertRole ({ commit, rootState }) {
      commit('insertRole')
      commit('setJurisdictionList', rootState.app.menuList)
    },
    delSysRole ({ commit, rootState }, id) {
      return delRole(id)
    }
  }
}
