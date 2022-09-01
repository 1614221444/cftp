<template>
  <div data-menu="menu.sys.notice" class="split-pane-page-wrapper">
    <Form inline  style="padding: 10px;margin-bottom: 20px;border: 2px solid #e8eaec;">
      <FormItem prop="code">
        <Input type="text" v-model="$store.state.notice.query.code" :placeholder="$t('system.notice.code')">
        </Input>
      </FormItem>
      <FormItem>
        <Button type="primary" @click="query()">{{ $t('system.query') }}</Button>
      </FormItem>
      <Divider dashed class="divider"/>
      <div class="operation">
        <Button type="primary" v-jurisdiction="'edit'" @click="add()">{{ $t('system.add') }}</Button>
      </div>
    </Form>
    <Table
      :loading="$store.state.notice.loading"
      context-menu
      show-context-menu
      :columns="columns1"
      :data="$store.state.notice.list"
      border
      stripe
    >
      <template slot-scope="{ row, index }" slot="action">
        <Button type="primary" v-jurisdiction="'edit'" size="small" style="margin-right: 5px" @click="push(row, index)">{{ $t('system.notice.push') }}</Button>
        <Button type="primary" size="small" style="margin-right: 5px" @click="info(row, index)">{{ $t('system.info') }}</Button>
        <Button type="error" v-jurisdiction="'del'" size="small" @click="delInit(row)">{{ $t('system.del') }}</Button>
      </template>
    </Table>
    <Page
      @on-change="changePage"
      @on-page-size-change="changeSizePage"
      :page-size="$store.state.notice.query.singlePage"
      :total="$store.state.notice.query.totalNumber"
      :current="$store.state.notice.query.pageNumber"
      :page-size-opts="[20,40,60,80,100]"
      style="text-align: right;margin-top: 5px;"
      show-total show-sizer >
    </Page>
    <Modal v-model="delConfirm" width="360">
      <p slot="header" style="color:#f60;text-align:center">
        <Icon type="ios-information-circle"></Icon>
        <span>{{ $t('system.warning') }}</span>
      </p>
      <div style="text-align:center">
        <p>{{ $t('system.confirm.del') }}</p>
      </div>
      <div slot="footer">
        <Button type="error" size="large" long @click="del()">{{ $t('system.del') }}</Button>
      </div>
    </Modal>
    <Drawer
      :title="$t('menu.sys.notice') + $t('system.info')"
      v-model="isInfo"
      width="30%"
    >
      <Form ref="formValidate" :model="$store.state.notice.info" :rules="validate">
        <Row :gutter="32">
          <Col span="24">
            <FormItem :label="$t('system.notice.code')"  prop="code">
              <Input v-model="$store.state.notice.info.code" :placeholder="$t('system.pleaseEnter') + $t('system.notice.code')" />
            </FormItem>
          </Col>
          <Col span="24">
            <FormItem :label="$t('system.notice.title')"  prop="title">
              <Input v-model="$store.state.notice.info.title" :placeholder="$t('system.pleaseEnter') + $t('system.notice.title')" />
            </FormItem>
          </Col>
          <Col span="24">
            <FormItem :label="$t('system.notice.content')"  prop="content">
              <Input v-model="$store.state.notice.info.content" :placeholder="$t('system.pleaseEnter') + $t('system.notice.content')" />
            </FormItem>
          </Col>
          <Col span="12">
          <FormItem :label="$t('system.notice.callbackUrl')"  prop="callbackUrl">
            <AutoComplete
              v-model="$store.state.notice.info.callbackUrl"
              icon="ios-search"
              placeholder="input here">
              <div class="demo-auto-complete-item" v-for="item in $store.getters.menuList" :key="item">
                <div class="demo-auto-complete-group">
                  <span>{{ $t(item.name) }}</span>
                </div>
                <Option v-for="option in item.children" :value="option.path" :key="option.path">
                  <span class="demo-auto-complete-title">{{ $t(option.name) }}</span>
                  <span class="demo-auto-complete-count">{{ option.path }}</span>
                </Option>
              </div>
            </AutoComplete>
          </FormItem>
          </Col>
          <Col span="12">
          <FormItem :label="$t('system.notice.icon')"  prop="icon">
            <Input v-model="$store.state.notice.info.icon" :placeholder="$t('system.pleaseEnter') + $t('system.notice.icon')" />
          </FormItem>
          </Col>
          <Col span="24">
          <FormItem :label="$t('system.notice.data')"  prop="expiration">
            <Input type="textarea" v-model="$store.state.notice.info.jsonData" :placeholder="$t('system.pleaseEnter') + $t('system.notice.data')" />
          </FormItem>
          </Col>
          <Col span="24">
          <FormItem :label="$t('menu.sys.role')"  prop="roleList">
            <Select v-model="$store.state.notice.info.roleList" multiple>
              <Option v-for="item in $store.state.role.roleNoPageList" :value="item.id" :key="item.id">{{ $t(item.roleName) }}</Option>
            </Select>
          </FormItem>
          </Col>
          <Col span="24">
          <FormItem :label="$t('system.remakes')"  prop="dictOrder">
            <Input type="textarea" v-model="$store.state.notice.info.dictOrder" :placeholder="$t('system.pleaseEnter') + $t('system.remakes')" />
          </FormItem>
          </Col>
        </Row>
      </Form>
      <div class="demo-drawer-footer">
        <Button size="large" long v-jurisdiction="'edit'" type="primary" @click="save()">{{ $t('system.save') }}</Button>
      </div>
    </Drawer>
  </div>
</template>
<script>
import store from '@/store'
export default {
  data () {
    store.dispatch('getNoticeList', this.$store.state.notice.query)
    return {
      columns1: [
        { type: 'index', width: 60, align: 'center' },
        { title: this.$t('system.notice.code'), key: 'code' },
        { title: this.$t('system.notice.title'), key: 'title' },
        { title: this.$t('system.notice.content'), key: 'content' },
        { title: this.$t('system.notice.callbackUrl'), key: 'callbackUrl' },
        /* { title: this.$t('system.notice.pushType'),
          key: 'pushType',
          render: (h, params) => {
            return h('div', [
              h('strong',
                this.$t(this.$dict('NOTICE_TYPE', params.row.pushType)))
            ])
          }
        }, */
        { title: this.$t('system.notice.icon'), key: 'icon' },
        { title: this.$t('system.notice.data'), key: 'jsonData' },
        { title: this.$t('system.remakes'), key: 'remakes', tooltip: true },
        { title: this.$t('system.operation'), slot: 'action', width: 200, align: 'center' }
      ],
      contextLine: 0,
      delConfirm: false,
      isInfo: false,
      validate: {
        code: [
          { required: true, message: this.$t('system.notice.code') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        title: [
          { required: true, message: this.$t('system.notice.title') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        content: [
          { required: true, message: this.$t('system.notice.content') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        callbackUrl: [
          { required: true, message: this.$t('system.notice.callbackUrl') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    del () {
      let data = this.deldata
      this.delConfirm = false
      store.dispatch('delNotice', data.id).then(res => {
        if (res.data.code === 200) {
          this.query()
          this.$Message.success(this.$t('system.success'))
        } else {
          this.$Message.error(this.$t('system.fail'))
        }
      })
    },
    delInit (data) {
      this.deldata = data
      this.delConfirm = true
    },
    save () {
      this.$refs['formValidate'].validate((valid) => {
        if (valid) {
          // 通过ID查询角色对象
          let roleList = this.$store.state.notice.info.roleList
          let sourceRoleList = this.$store.state.role.roleNoPageList
          let index
          for (let i in roleList) {
            index = sourceRoleList.findIndex((obj) => {
              return obj.id === roleList[i]
            })
            roleList[i] = { roleId: sourceRoleList[index].id }
          }
          this.$store.state.notice.info.roleList = roleList
          store.dispatch('saveNotice').then(res => {
            if (res.data.code === 200) {
              this.query()
              this.$Message.success(this.$t('system.success'))
              this.isInfo = false
            }
          })
        }
      })
    },
    add () {
      store.dispatch('getRoleNoPageList')
      store.dispatch('insertNotice', {})
      this.isInfo = true
    },
    query () {
      store.dispatch('getNoticeList', this.$store.state.notice.query)
    },
    info (row) {
      store.dispatch('getNotice', row)
      this.isInfo = true
    },
    push (row) {
      // 通过ID查询角色对象
      let roleList = row.roleList
      let sourceRoleList = this.$store.state.role.roleNoPageList
      let index
      for (let i in roleList) {
        index = sourceRoleList.findIndex((obj) => {
          return obj.id === roleList[i]
        })
        roleList[i] = sourceRoleList[index]
      }
      store.dispatch('pushNotice', row).then(res => {
        if (res.data.code === 200) {
          this.query()
          this.$Message.success(this.$t('system.success'))
          this.isInfo = false
        }
      })
    },
    changePage (pageNumber) {
      this.$store.state.notice.query.pageNumber = pageNumber
      this.query()
    },
    changeSizePage (pageSizeNumber) {
      this.$store.state.notice.query.pageNumber = 1
      this.$store.state.notice.query.singlePage = pageSizeNumber
      this.query()
    }
  }
}
</script>
<style lang="less">
  .split-pane-page-wrapper{
    height: 100%;
  }
  /*.ivu-form-item{*/
    /*margin-bottom: 4px;*/
  /*}*/
  .divider{
    margin: 2px 0px
  }
  .operation{
    text-align: right;
  }
</style>
<style>
  .demo-auto-complete-item{
    padding: 4px 0;
    border-bottom: 1px solid #F6F6F6;
  }
  .demo-auto-complete-group{
    font-size: 12px;
    padding: 4px 6px;
  }
  .demo-auto-complete-group span{
    color: #666;
    font-weight: bold;
  }
  .demo-auto-complete-group a{
    float: right;
  }
  .demo-auto-complete-count{
    float: right;
    color: #999;
  }
</style>
