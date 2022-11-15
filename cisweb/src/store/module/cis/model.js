import { getList, save, del, getlistAll } from '@/api/cis/model'

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
    setList (state, page) {
      if (page !== undefined) {
        state.list = page.records
        state.query.totalNumber = page.total
      }
      state.loading = false
    },
    setLoading (state, isLoading) {
      state.loading = isLoading
    },
    getModel (state, params) {
      if (params) {
        state.info = params
      } else {
        state.info = {}
      }
    },
  },
  actions: {
    getModelList ({ commit, rootState }, params) {
      commit('setLoading', true)
      getList(params).then(res => {
        commit('setList', res.data)
      }).catch(e => {
        console.log(e)
      })
    },
    getModelListAll ({ commit, rootState }) {
      return getlistAll()
    },
    saveCisModel ({ commit, rootState }) {
      return save(rootState.model.info)
    },
    getCisModel ({ commit, rootState }, params) {
      commit('getModel', params)
    },
    delCisModel ({ commit, rootState }, id) {
      return del(id)
    }
  }
}
