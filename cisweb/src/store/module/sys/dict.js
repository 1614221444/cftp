import { getList as getDictList, getNoPageList, save as saveDict, del as delDict } from '@/api/sys/dict'

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
    dictList (state, page) {
      state.dictList = page.records
      state.query.totalNumber = page.total
    },
    getDict (state, data) {
      state.info = data
    },
    insertDict (state, pid) {
      state.info = {}
    }
  },
  actions: {
    getSysDictList ({ commit, rootState }, params) {
      rootState.dict.loading = true
      getDictList(params).then(res => {
        commit('dictList', res.data)
        rootState.dict.loading = false
      }).catch(e => {
        console.log(e)
      })
    },
    getSysDict ({ commit, rootState }, params) {
      commit('getDict', params)
    },
    copy ({ commit, rootState }, params) {
      let data = JSON.parse(JSON.stringify(params))
      data.id = null
      data.dictValue = null
      data.title = null
      data.remakes = null
      data.dictOrder++
      commit('getDict', data)
    },
    saveSysDict ({ commit, rootState }) {
      return saveDict(rootState.dict.info)
    },
    insertDict ({ commit, rootState }, pid) {
      commit('insertDict', pid)
    },
    delSysDict ({ commit, rootState }, id) {
      return delDict(id)
    },
    getDictNoPageList ({ commit, rootState }) {
      getNoPageList(params).then(res => {
        commit('dictList', res.data)
      }).catch(e => {
        console.log(e)
      })
    }
  }
}
