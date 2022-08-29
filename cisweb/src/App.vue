<template>
  <div  ref="app" id="app">
    <router-view />
  </div>
</template>

<script>
import store from '@/store'
import Push from 'push.js'
export default {
  name: 'App',
  created () {
    // 注册推送通知
    Push.Permission.request()
    if (!store.getters.Message) {
      // 将全局消息赋值给store
      store.getters.Message = this.$Message
    }

    if (!store.getters.t) {
      // 将国际化赋值给store
      store.getters.t = this.$t
    }
  },
  mounted () {
    this.checkPush()
  },
  data () {
    return {
      pushList: [],
      pushedList: []
    }
  },
  methods: {
    checkPush () {
      let $this = this
      if (store.state.user.thisUser.id !== undefined) {
        store.dispatch('news').then(res => {
          store.state.app.pushList = res.data
          setTimeout(() => {
            this.checkPush()
          }, 5000)
          for (let i in res.data) {
            if (res.data[i].read === false && res.data[i].pushState === 0) {
              store.state.app.pushedList.push(res.data[i].id)
              Push.create(res.data[i].title, {
                link: res.data[i].callbackUrl,
                body: res.data[i].content,
                icon: res.data[i].icon,
                requireInteraction: true,
                tag: res.data[i].id,
                onClick: function () {
                  // 使网站获取焦点
                  window.focus()
                  store.dispatch('readNotice', res.data[i])
                  store.state.app.pushList[i].read = true
                  // 关闭通知
                  $this.$router.push({
                    name: $this.queryRouter(store.getters.menuList, res.data[i].callbackUrl),
                    params: { data: res.data[i].callbackData }
                  })
                  this.close()
                },
                onClose: function () {
                  store.dispatch('readNotice', res.data[i])
                  store.state.app.pushList[i].read = true
                }
              })
              // this.$Notice.open({
              //   title: res.data[i].title,
              //   desc: res.data[i].content,
              //   duration: 0,
              //   render: h => {
              //     return h('span', [
              //       res.data[i].content,
              //       h('a', '打开链接')
              //     ])
              //   }
              // });
            }
          }
        })
      } else {
        // 如果没有用户登录搁5秒钟在判断一下
        setTimeout(() => {
          this.checkPush()
        }, 5000)
      }
    },
    queryRouter (data, url) {
      for (let i in data) {
        if (data[i].path === url) {
          return data[i].name
        } else if (data[i].children !== undefined) {
          return this.queryRouter(data[i].children, url)
        }
      }
    }
  }
}
</script>

<style lang="less">
.size{
  width: 100%;
  height: 100%;
}
html,body{
  .size;
  overflow: hidden;
  margin: 0;
  padding: 0;
}
#app {
  .size;
}
</style>
