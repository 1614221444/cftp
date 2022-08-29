import { getList as getLogList, retry } from '@/api/sys/log'

export default {
  state: {
    list: [],
    info: {},
    query: {
      pageNumber: 1,
      totalNumber: 0,
      singlePage: 20
    },
    loading: false
  },
  mutations: {
    logList (state, page) {
      if (page !== undefined) {
        state.list = page.records
        state.query.totalNumber = page.total
      }
      state.loading = false
    },
    getLog (state, data) {
      state.info = data
    }
  },
  actions: {
    getSysLogList ({ commit, rootState }, params) {
      rootState.log.loading = true
      getLogList(params).then(res => {
        commit('logList', res.data)
      }).catch(e => {
        console.log(e)
      })
    },
    getSysLog ({ commit, rootState }, params) {
      commit('getLog', params)
    },
    retry ({ commit, rootState }, params) {
      retry(params)
    }
  }
}
