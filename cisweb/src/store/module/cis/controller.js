import { getList, save, del, getAuthList, start, stop, getServerUserList } from '@/api/cis/controller'

export default {
  state: {
    dictList: [],
    loading: false,
    query: {
      pageNumber: 1,
      totalNumber: 0,
      singlePage: 20
    },
    info: {}
  },
  mutations: {
    controllerList (state, page) {
      state.controllerList = page.records
      state.query.totalNumber = page.total
    },
    getController (state, data) {
      state.info = data
      state.info.authType = '0'
    },
    insertController (state) {
      state.info = {}
      state.info.authType = '0'
      state.info.authList = []
    }
  },
  actions: {
    getCisControllerList ({ commit, rootState }, params) {
      rootState.controller.loading = true
      getList(params).then(res => {
        commit('controllerList', res.data)
        rootState.controller.loading = false
      }).catch(e => {
        console.log(e)
      })
    },
    getCisController ({ commit, rootState }, params) {
      commit('getController', params)
    },
    saveCisController ({ commit, rootState }) {
      return save(rootState.controller.info)
    },
    insertCisController ({ commit, rootState }, pid) {
      commit('insertController', pid)
    },
    delCisController ({ commit, rootState }, id) {
      return del(id)
    },
    getAuthList({ commit, rootState }, controllerId) {
      return getAuthList(controllerId)
    },
    startCisController ({ commit, rootState }, id) {
      return start(id)
    },
    stopCisController ({ commit, rootState }, id) {
      return stop(id)
    },
    getServerUserList({ commit, rootState }, id) {
      return getServerUserList(id)
    }
  }
}
