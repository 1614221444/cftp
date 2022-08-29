<style lang="less">
  @import './login.less';
</style>

<template>
  <div class="login">
    <div class="login-con">
      <Card icon="log-in" title="供应商登录" :bordered="false">
        <div class="form-con">
          <Form ref="loginForm" :model="form" :rules="rules" @keydown.enter.native="handleSubmit">
            <FormItem prop="userName">
              <Input v-model="form.userName" placeholder="请输入手机号">
              <span slot="prepend">
                <Icon :size="16" type="ios-person"></Icon>
              </span>
              </Input>
            </FormItem>
            <FormItem prop="password">
              <Input  v-model="form.password" placeholder="请输入验证码">
              <Button :disabled="form.countDown !== '获取验证码'" slot="append" @click="verificationCode">{{form.countDown}}</Button>
              </Input>
            </FormItem>
            <FormItem>
              <Button @click="handleSubmit" type="primary" long>登录</Button>
            </FormItem>
          </Form>
        </div>
      </Card>
    </div>
  </div>
</template>

<script>
import LoginForm from '_c/login-form'
import { mapActions } from 'vuex'
export default {
  components: {
    LoginForm
  },
  data () {
    return {
      form: {
        userName: '',
        password: '',
        countDown: '获取验证码'
      }
    }
  },
  methods: {
    ...mapActions([
      'custLogin',
      'sendVerificationCode'
    ]),
    handleSubmit () {
      this.custLogin({ loginId: this.form.userName, verificationCode: this.form.password }).then(res => {
        if (res.data.id) {
          this.$router.push({
            path: '/cust/optional'
          })
        } else {
          this.$Message['error']({
            background: true,
            content: res.data.message
          })
        }
      }, res => {
      })
    },
    verificationCode () {
      this.sendVerificationCode({ loginName: this.form.userName }).then(res => {
        let type = 'error'
        if (res.data.code === 200) {
          type = 'success'
          this.countDown(60)
        }
        this.$Message[type]({
          background: true,
          content: res.data.message
        })
      })
    },
    countDown (s) {
      if (s > 0) {
        s--
        this.form.countDown = s
        setTimeout(() => {
          this.countDown(s)
        }, 1000)
      } else {
        this.form.countDown = '获取验证码'
      }
    }
  }
}
</script>

<style>
.login-con {
  font-size: 30px;
}
</style>
