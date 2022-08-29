import {
  getBreadCrumbList,
  setTagNavListInLocalstorage,
  getMenuByRouter,
  getTagNavListFromLocalstorage,
  getHomeRoute,
  getNextRoute,
  routeHasExist,
  routeEqual,
  getRouteTitleHandled,
  localSave,
  localRead,
  addTreeList
} from '@/libs/util'
import { getMenuList } from '@/api/data'
import { getNoPageList } from '@/api/sys/dict'
import router from '@/router'
import store from '@/store'
// import routers from '@/router/routers'
import config from '@/config'
const { homeName } = config

const closePage = (state, route) => {
  const nextRoute = getNextRoute(state.tagNavList, route)
  state.tagNavList = state.tagNavList.filter(item => {
    return !routeEqual(item, route)
  })
  router.push(nextRoute)
}

export default {
  state: {
    breadCrumbList: [],
    tagNavList: [],
    homeRoute: {},
    local: localRead('local'),
    errorList: [],
    hasReadErrorPage: false,
    menuList: [],
    pushList: [],
    pushedList: []
  },
  getters: {
    // menuList: (state, getters, rootState) => getMenuByRouter(routers, rootState.user.access),
    menuList: (state, getters, rootState) => getMenuByRouter(state.menuList),
    errorCount: state => state.errorList.length
  },
  mutations: {
    setBreadCrumb (state, route) {
      state.breadCrumbList = getBreadCrumbList(route, state.homeRoute)
    },
    setHomeRoute (state, routes) {
      state.homeRoute = getHomeRoute(routes, homeName)
    },
    setTagNavList (state, list) {
      let tagList = []
      if (list) {
        tagList = [...list]
      } else tagList = getTagNavListFromLocalstorage() || []
      if (tagList[0] && tagList[0].name !== homeName) tagList.shift()
      let homeTagIndex = tagList.findIndex(item => item.name === homeName)
      if (homeTagIndex > 0) {
        let homeTag = tagList.splice(homeTagIndex, 1)[0]
        tagList.unshift(homeTag)
      }
      state.tagNavList = tagList
      setTagNavListInLocalstorage([...tagList])
    },
    closeTag (state, route) {
      let tag = state.tagNavList.filter(item => routeEqual(item, route))
      route = tag[0] ? tag[0] : null
      if (!route) return
      closePage(state, route)
    },
    addTag (state, { route, type = 'unshift' }) {
      let router = getRouteTitleHandled(route)
      if (!routeHasExist(state.tagNavList, router)) {
        if (type === 'push') state.tagNavList.push(router)
        else {
          if (router.name === homeName) state.tagNavList.unshift(router)
          else state.tagNavList.splice(1, 0, router)
        }
        setTagNavListInLocalstorage([...state.tagNavList])
      }
    },
    setLocal (state, lang) {
      localSave('local', lang)
      state.local = lang
    },
    addError (state, error) {
      state.errorList.push(error)
    },
    setHasReadErrorLoggerStatus (state, status = true) {
      state.hasReadErrorPage = status
    },
    setMenuList (state, list) {
      state.menuList = []
      list = addTreeList(list, (data) => {
        return {
          id: data.id,
          pid: data.pid,
          name: data.name,
          path: data.baseUrl,
          data: data,
          meta: {
            hideInMenu: !data.showMenu,
            icon: data.icon,
            title: data.name
          },
          component: data.component,
          children: []
        }
      })
      for (let i in list) {
        state.menuList.push(list[i])
      }
      state.hasInfo = true
    },
    setDictList (state, list) {
      state.dictList = list
    }
  },
  actions: {
    getMenuData ({ commit, rootState }) {
      getMenuList().then(res => {
        store.state.menuList = res.data
        commit('setMenuList', res.data)
      }).catch(e => {
        console.log(e)
      })
    },
    getDictData ({ commit, rootState }) {
      getNoPageList().then(res => {
        commit('setDictList', res.data)
      }).catch(e => {
        console.log(e)
      })
    }
  }
}
