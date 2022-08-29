<template>
  <div class="user-avatar-dropdown">
    <Dropdown @on-click="handleClick">
      <Badge :dot="!!messageUnreadCount">
        {{$store.state.user.thisUser.name}}
      </Badge>
      <Icon :size="18" type="md-arrow-dropdown"></Icon>
      <DropdownMenu slot="list">
        <!--<DropdownItem name="message">-->
          <!--消息中心<Badge style="margin-left: 10px" :count="messageUnreadCount"></Badge>-->
        <!--</DropdownItem>-->
        <DropdownItem name="updatePasswordInit">
          {{ $t('system.user.updatePassword') }}<Badge style="margin-left: 10px"></Badge>
        </DropdownItem>
        <DropdownItem name="logout">{{ $t('system.logout') }}</DropdownItem>
      </DropdownMenu>
    </Dropdown>
    <Modal v-model="isUpdate" width="360">
      <p slot="header" style="color:#2db7f5;text-align:center">
        <Icon type="ios-information-circle"></Icon>
        <span>{{$t('system.user.updatePassword')}}</span>
      </p>
      <div style="text-align:center">
        <Form ref="formValidate" :model="$store.state.menu.info" :label-width="120">
          <FormItem  :label-width="120" prop="originalPassword">
            <span slot='label'>{{ $t('system.user.originalPassword') }}</span>
            <Input v-model="password.originalPassword"/>
          </FormItem>
          <FormItem :label-width="120" prop="newPassword">
            <span slot='label'>{{ $t('system.user.newPassword') }}</span>
            <Input v-model="password.newPassword"/>
          </FormItem>
        </Form>
      </div>
      <div slot="footer">
        <Button type="info" size="large" long @click="updatePassword">{{ $t('system.save') }}</Button>
      </div>
    </Modal>
  </div>
</template>

<script>
import './user.less'
import { mapActions } from 'vuex'
import store from '@/store'
export default {
  name: 'User',
  props: {
    userAvatar: {
      type: String,
      default: ''
    },
    messageUnreadCount: {
      type: Number,
      default: 0
    }
  },
  data () {
    return {
      isUpdate: false,
      password: {
        originalPassword: '',
        newPassword: ''
      }
    }
  },
  methods: {
    ...mapActions([
      'handleLogOut'
    ]),
    logout () {
      this.handleLogOut().then(() => {
        this.$router.push({
          name: 'login'
        })
      })
    },
    updatePasswordInit () {
      this.isUpdate = true
    },
    updatePassword () {
      store.dispatch('updatePassword', this.password).then(res => {
        if (res.data.code === 200) {
          this.$Message.success(this.$t('system.success'))
          this.isUpdate = false
        } else if (res.data.message === '原密码错误！') {
          this.$Message.error(this.$t('system.user.originalPassword') + this.$t('system.validate.error'))
        }
      })
    },
    handleClick (name) {
      switch (name) {
        case 'logout': this.logout()
          break
        case 'updatePasswordInit': this.updatePasswordInit()
          break
      }
    }
  }
}
</script>
