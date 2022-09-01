import { getList, save, del, push, news, read, readAll, delPush,getRoleList } from '@/api/sys/notice'

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
    noticeList (state, page) {
      if (page !== undefined) {
        state.list = page.records
        state.query.totalNumber = page.total
        for (let i in state.list) {
          for (let x in state.list[i].roleList) {
            state.list[i].roleList[x] = state.list[i].roleList[x].id
          }
        }
      }
      state.loading = false
    },
    getNotice (state, data) {
      let roleLists = []
      for (let i in data.roleList) {
        roleLists.push(data.roleList[i].roleId)
      }
      data.roleList = roleLists
      state.info = data
      // 这里把int类型的数据强转成字符串类型    因为字典value就是字符串类型 必须全部匹配才能被select组件识别（===）
      state.info.pushType = data.pushType + ''
    },
    insertNotice (state) {
      state.info = {}
      state.info.pushType = '1'
    }
  },
  actions: {
    getNoticeList ({ commit, rootState }, params) {
      rootState.notice.loading = true
      getList(params).then(res => {
        commit('noticeList', res.data)
      }).catch(e => {
        console.log(e)
      })
    },
    insertNotice ({ commit, rootState }, params) {
      commit('insertNotice', params)
    },
    getNotice ({ commit, rootState }, params) {
      getRoleList(params.id).then(res => {
        params.roleList = res.data
        commit('getNotice', params)
      })
    },
    saveNotice ({ commit, rootState }) {
      return save(rootState.notice.info)
    },
    delNotice ({ commit, rootState }, id) {
      return del(id)
    },
    pushNotice ({ commit, rootState }, data) {
      return push(data)
    },
    news () {
      return news()
    },
    readNotice ({ commit, rootState }, data) {
      return read(data)
    },
    readAll ({ commit, rootState }, data) {
      return readAll(data)
    },
    delPush ({ commit, rootState }, data) {
      return delPush(data)
    }
  }
}
