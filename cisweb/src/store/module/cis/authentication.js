import { getList, save, del } from '@/api/cis/authentication'

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
    authenticationList (state, page) {
      state.authenticationList = page.records
      state.query.totalNumber = page.total
    },
    getAuthentication (state, data) {
      state.info = data
    },
    insertAuthentication (state, pid) {
      state.info = {}
    }
  },
  actions: {
    getAuthenticationList ({ commit, rootState }, params) {
      rootState.authentication.loading = true
      getList(params).then(res => {
        commit('authenticationList', res.data)
        rootState.authentication.loading = false
      }).catch(e => {
        console.log(e)
      })
    },
    getCisAuthentication ({ commit, rootState }, params) {
      commit('getAuthentication', params)
    },
    saveCisAuthentication ({ commit, rootState }) {
      return save(rootState.authentication.info)
    },
    insertCisAuthentication ({ commit, rootState }, pid) {
      commit('insertAuthentication', pid)
    },
    delCisAuthentication ({ commit, rootState }, id) {
      return del(id)
    }
  }
}
