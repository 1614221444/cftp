import { getList, save, del, getAuthList, start, stop, getServerUserList, send } from '@/api/cis/controller'

export default {
  state: {
    dictList: [],
    loading: false,
    query: {
      pageNumber: 1,
      totalNumber: 0,
      singlePage: 20
    },
    info: {},
    sendList: []
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
    },
    /**
     * 导入进度
     * @param state
     * @param data
     */
    setProgress (state, data) {
      for (let i in state.controllerList) {
        // 发送方
        if (state.controllerList[i].id === data.sendId) {
          // 判断是否需要初始化
          if (!state.sendList[state.controllerList[i].id]) {
            state.sendList[state.controllerList[i].id] = []
            state.sendList[state.controllerList[i].id][data.receiverId] = []
            state.sendList[state.controllerList[i].id][data.receiverId].push(data)
            return
          } else if (!state.sendList[state.controllerList[i].id][data.receiverId]) {
            state.sendList[state.controllerList[i].id][data.receiverId] = []
            state.sendList[state.controllerList[i].id][data.receiverId].push(data)
            return
          }
          // 判断是否覆盖进度
          let is = true
          for (let x in state.sendList[state.controllerList[i].id][data.receiverId]) {
            if (state.sendList[state.controllerList[i].id][data.receiverId][x].id === data.id) {
              state.sendList[state.controllerList[i].id][data.receiverId][x] = data
              is = false
            }
          }
          if(is) {
            state.sendList[state.controllerList[i].id][data.receiverId].push(data)
          }
        }
      }
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
    },
    sendFile({ commit, rootState }, sendInfo) {
      let data = { controllerId: sendInfo.server.id, userId: sendInfo.to, data: sendInfo.file }
      return send(data)
    },
    setProgress({ commit, rootState }, data) {
      commit('setProgress', data)
    }
  }
}
