import {
  login,
  logout,
  getUserList,
  save as saveUser,
  del as delUser,
  getThisUser,
  updatePassword,
  getRoleList
} from '@/api/user'
import { initSocket } from '@/libs/util'

export default {
  state: {
    thisUser: {},
    userList: [],
    loading: false,
    query: {
      pageNumber: 1,
      totalNumber: 0,
      singlePage: 20
    },
    info: {

    }
  },
  mutations: {
    setAvatar (state, avatarPath) {
      state.avatarImgPath = avatarPath
    },
    setUserId (state, id) {
      state.userId = id
    },
    setUserName (state, name) {
      state.userName = name
    },
    setAccess (state, access) {
      state.access = access
    },
    login (state, user) {
      state.thisUser = user
      // 初始化权限列表
      state.thisUser.access = []
      state.thisUser.access.push({ menu: { name: 'home' } })
      for (let i in user.authorities) {
        state.thisUser.access.push(user.authorities[i])
      }
    },
    logout (state) {
      state.thisUser = {}
    },
    setHasGetInfo (state, status) {
      state.hasGetInfo = status
    },
    setMessageCount (state, count) {
      state.unreadCount = count
    },
    setMessageUnreadList (state, list) {
      state.messageUnreadList = list
    },
    setMessageReadedList (state, list) {
      state.messageReadedList = list
    },
    setMessageTrashList (state, list) {
      state.messageTrashList = list
    },
    updateMessageContentStore (state, { msg_id, content }) {
      state.messageContentStore[msg_id] = content
    },
    moveMsg (state, { from, to, msg_id }) {
      const index = state[from].findIndex(_ => _.msg_id === msg_id)
      const msgItem = state[from].splice(index, 1)[0]
      msgItem.loading = false
      state[to].unshift(msgItem)
    },
    setUserList (state, page) {
      if (page !== undefined) {
        state.userList = page.records
        state.query.totalNumber = page.total
        for (let i in state.userList) {
          for (let x in state.userList[i].roleList) {
            state.userList[i].roleList[x] = state.userList[i].roleList[x].id
          }
        }
      }
    },
    setLoading (state, isLoading) {
      state.loading = isLoading
    },
    initInfo (state, data) {
      let ids = []
      for (let i in data.roleList) {
        ids.push(data.roleList[i].roleId)
      }
      data.roleList = ids
      state.info = data
    }
  },
  actions: {
    // 登录
    handleLogin ({ commit }, { username, password }) {
      username = username.trim()
      return new Promise((resolve, reject) => {
        login({
          username,
          password
        }).then(res => {
          const data = res.data.data.principal
          commit('login', data)
          initSocket()
          resolve(data)
        }).catch(err => {
          reject(err)
        })
      })
    },
    // 退出登录
    handleLogOut ({ state, commit }) {
      return new Promise((resolve, reject) => {
        logout().then(() => {
          commit('logout')
          resolve()
        }).catch(err => {
          reject(err)
        })
      })
    },
    // 读取用户列表
    getUserList ({ state, commit }) {
      commit('setLoading', true)
      return new Promise((resolve, reject) => {
        getUserList(state.query).then((obj) => {
          commit('setUserList', obj.data)
          commit('setLoading', false)
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },
    // 读取用户列表
    getUserInfo ({ state, commit }, info) {
      if (info) {
        getRoleList(info.id).then((res) => {
          info.roleList = res.data
          return commit('initInfo', info)
        })
      } else {
        return getThisUser()
      }
    },
    // 保存用户
    saveUser ({ state }) {
      return saveUser(state.info)
    },
    // 保存用户
    delUser ({ state }, id) {
      return delUser(id)
    },
    // 修改密码
    updatePassword ({ state }, password) {
      return updatePassword(password)
    }
  }
}
