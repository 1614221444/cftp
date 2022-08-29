<template>
  <Poptip trigger="hover" placement="bottom">
    <Button type="text" >
      <Icon :size="20" type="ios-notifications"/>
    </Button>
    <div class="api" slot="content">
      <div style="font-size: 20px;text-align: center;border-bottom: 2px solid #dcdee2;">
        {{ $t('menu.sys.notice') }}
        <a @click="readAll()" style="position: absolute;right: 15px;font-size: 13px;top: 29px;">{{ $t('system.notice.allRead') }}</a>
      </div>
      <List style="height: 400px;">
        <table style="width: 100%;">
          <tr  v-for="item in $store.state.app.pushList" :key="item.id">
            <div :class="isRead(item.read)"  style="padding: 3px;border-bottom: 1px solid #e8eaec;display: flex;">
              <div style="width: 11%">
                <Avatar shape="square" :src="item.icon" />
              </div>
              <div style="width: 75%;padding-left: 5px">
                <a @click="open(item)">{{item.title}}</a>
                <div style="overflow: hidden;text-overflow: ellipsis; white-space: normal;word-break: break-all;-webkit-box-sizing: border-box;box-sizing: border-box;">
                  <Tooltip max-width="220" :content="item.content">
                    {{textEllipsis(item.content)}}
                  </Tooltip>
                </div>
              </div>
              <div style="width: 20%">
                <Time style="float: right;color: #8c8c8c;" :time="item.pushDate" :interval="1" /><br/>
                <div style="float: right;">
                  <span v-show="!item.read">
                    <a @click="read(item)"><Icon type="ios-eye" /></a>&nbsp;|
                  </span>
                  <a @click="del(item)"><Icon type="md-trash" /></a>
                </div>
              </div>
            </div>
          </tr>
        </table>
      </List>
    </div>
  </Poptip>
</template>

<script>
import store from '@/store'
import Push from 'push.js'
export default {
  name: 'Push',
  methods: {
    textEllipsis (text) {
      let length = 20
      if (text.length >= length) {
        return text.substring(0, length) + '...'
      } else {
        return text
      }
    },
    isRead (read) {
      if (read) {
        return 'read'
      } else {
        return 'unread'
      }
    },
    open (data) {
      this.read(data)
      // 跳转至目标页面
      this.$router.push({
        name: this.queryRouter(store.getters.menuList, data.callbackUrl),
        params: { data: data.callbackData }
      })
    },
    read (data) {
      // 修改页面数据 重新渲染组件
      data.read = true
      // 关闭windows推送
      Push.close(data.id)
      // 将修改传递到后台
      store.dispatch('readNotice', data)
    },
    del (data) {
      // 将修改传递到后台
      store.dispatch('delPush', data)
      // 关闭windows推送
      Push.close(data.id)
      // 删除前端显示的缓存
      store.state.app.pushList.splice(store.state.app.pushList.findIndex(item => item.id === data.id), 1)
    },
    readAll () {
      // 将修改传递到后台
      store.dispatch('readAll')
      // 修改页面数据 重新渲染组件
      for (let i in store.state.app.pushList) {
        // 关闭windows推送
        Push.close(store.state.app.pushList[i].id)
        store.state.app.pushList[i].read = true
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
<style>
  .ivu-poptip-popper[x-placement="bottom"] .ivu-poptip-arrow {
    left: 57%;
    margin-left: -7px;
  }
  .api{
    width: 300px;
  }
  .unread {
    font-weight:900;
  }
  .read {
    font-weight:400;
  }
</style>
