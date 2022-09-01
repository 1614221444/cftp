<template>
  <div  ref="app" id="app">
    <router-view />
  </div>
</template>

<script>
import store from '@/store'
import Push from 'push.js'
import { initSocket } from '@/libs/util'
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
    if(this.$store.state.user.thisUser.id) {
      initSocket()
    }
  },
  data () {
    return {
      pushList: [],
      pushedList: []
    }
  },
  methods: {

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
