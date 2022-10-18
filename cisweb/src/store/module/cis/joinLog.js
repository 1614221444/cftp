import { getList } from '@/api/cis/joinLog'
import { getListAll } from '@/api/cis/controller'

export default {
  state: {
    list: [],
    info: {},
    query: {
      current: 1,
      totalNumber: 0,
      size: 20
    },
    loading: false
  },
  mutations: {
    joinLogList (state, page) {
      if (page !== undefined) {
        for (let i in page.records) {
          if (state.controller[page.records[i].sendId]) {
            page.records[i].sendId = state.controller[page.records[i].sendId]
          }
          if (state.controller[page.records[i].receiverId]) {
            page.records[i].receiverId = state.controller[page.records[i].receiverId]
          }
          page.records[i].fileSize = (page.records[i].fileSize / 1000).toFixed(2)
          if (page.records[i].fileSize > 1024) {
            page.records[i].fileSize = (page.records[i].fileSize / 1000).toFixed(2) + 'G'
          } else {
            page.records[i].fileSize += 'M'
          }
        }
        state.list = page.records
        state.query.totalNumber = page.total
      }
      state.loading = false
    },
    setLoading (state, isLoading) {
      state.loading = isLoading
    },
    setController (state, controller) {
      state.controller = []
      for (let i in controller) {
        state.controller[controller[i].id] = controller[i].controllerName
      }
    }
  },
  actions: {
    getJoinLogList ({ commit, rootState }, params) {
      commit('setLoading', true)
      getList(params).then(res => {
        commit('joinLogList', res.data)
      }).catch(e => {
        console.log(e)
      })
    },
    getControllerAll ({ commit, rootState }, params) {
      getListAll(params).then(res => {
        commit('setController', res.data)
      }).catch(e => {
        console.log(e)
      })
    }
  }
}
