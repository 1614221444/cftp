import { getList, save, del, logList } from '@/api/sys/job'

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
    setList (state, page) {
      if (page !== undefined) {
        state.list = page.records
        state.query.totalNumber = page.total
        for (let i in state.list) {
          // 这里把int类型的数据强转成字符串类型    因为字典value就是字符串类型 必须全部匹配才能被select组件识别（===）
          state.list[i].status += ''
        }
      }
      state.loading = false
    },
    getJob (state, data) {
      state.info = data
    },
    insertJob (state) {
      state.info = {}
      state.info.status = '0'
    }
  },
  actions: {
    getJobList ({ commit, rootState }, params) {
      rootState.job.loading = true
      getList(params).then(res => {
        commit('setList', res.data)
      }).catch(e => {
        console.log(e)
      })
    },
    insertJob ({ commit, rootState }, params) {
      commit('insertJob', params)
    },
    getJob ({ commit, rootState }, params) {
      commit('getJob', params)
    },
    saveJob ({ commit, rootState }, info) {
      if (info) {
        return save(info)
      } else {
        return save(rootState.job.info)
      }
    },
    delJob ({ commit, rootState }, id) {
      return del(id)
    },
    getJobLogList ({ commit, rootState }, data) {
      return logList(data)
    }
  }
}
