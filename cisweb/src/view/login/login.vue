<style lang="less">
  @import './login.less';
</style>

<template>
  <div class="login">
    <div class="login-con">
      <Card icon="log-in" title="欢迎登录" :bordered="false">
        <div class="form-con">
          <login-form @on-success-valid="handleSubmit"></login-form>
        </div>
      </Card>
    </div>
  </div>
</template>

<script>
import LoginForm from '_c/login-form'
import { mapActions } from 'vuex'
import store from '@/store'
export default {
  components: {
    LoginForm
  },
  methods: {
    ...mapActions([
      'handleLogin'
    ]),
    handleSubmit ({ username, password }) {
      this.handleLogin({ username, password }).then(res => {
        store.state.user.thisUser = res
        store.dispatch('getDictData')
        store.dispatch('getMenuData')
        this.$router.push({
          name: this.$config.homeName
        })
      }, res => {
        this.$Message['error']({
          background: true,
          content: res.data.message
        })
      }
      )
    }
  }
}
</script>

<style>

</style>
