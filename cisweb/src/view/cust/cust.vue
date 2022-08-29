<template>
  <div data-menu="menu.sys.user" class="split-pane-page-wrapper">
    <Form inline  style="padding: 10px;margin-bottom: 20px;border: 2px solid #e8eaec;">
      <FormItem prop="username">
        <Input type="text" v-model="$store.state.user.query.username" :placeholder="$t('system.user.username')">
        </Input>
      </FormItem>
      <FormItem prop="name">
        <Input type="text" v-model="$store.state.user.query.name" :placeholder="$t('system.user.name')">
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
      :loading="$store.state.user.loading"
      context-menu
      show-context-menu
      :columns="columns1"
      :data="$store.state.user.userList"
      border
      stripe
    >
      <template slot-scope="{ row, index }" slot="action">
        <Button type="primary" size="small" style="margin-right: 5px" @click="info(row, index)">{{ $t('system.info') }}</Button>
        <Button type="error" v-jurisdiction="'del'" size="small" @click="delInit(row)">{{ $t('system.del') }}</Button>
      </template>
    </Table>
    <Page
      @on-change="changePage"
      @on-page-size-change="changeSizePage"
      :page-size="$store.state.user.query.singlePage"
      :total="$store.state.user.query.totalNumber"
      :current="$store.state.user.query.pageNumber"
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
      :title="$t('menu.sys.user') + $t('system.info')"
      v-model="isInfo"
      width="30%"
    >
      <Form ref="formValidate" :model="$store.state.user.info" :rules="validate">
        <Row :gutter="32">
          <Col span="24">
            <FormItem :label="$t('system.user.username')"  prop="username">
              <Input v-model="$store.state.user.info.username" :placeholder="$t('system.pleaseEnter') + $t('system.user.username')" />
            </FormItem>
          </Col>
          <Col span="24" v-show="$store.state.user.info.id === undefined">
            <FormItem :label="$t('system.user.password')"  prop="password">
              <Input v-model="$store.state.user.info.password" :placeholder="$t('system.pleaseEnter') + $t('system.user.password')" />
            </FormItem>
          </Col>
          <Col span="24">
            <FormItem :label="$t('system.user.name')"  prop="name">
              <Input v-model="$store.state.user.info.name" :placeholder="$t('system.pleaseEnter') + $t('system.user.name')" />
            </FormItem>
          </Col>
          <Col span="24">
            <FormItem :label="$t('system.user.enterpriseId')"  prop="enterpriseId">
              <Input v-model="$store.state.user.info.enterpriseId" :placeholder="$t('system.pleaseEnter') + $t('system.user.enterpriseId')" />
            </FormItem>
          </Col>
          <Col span="24">
            <FormItem :label="$t('menu.sys.role')"  prop="roleList">
              <Select v-model="$store.state.user.info.roleList" multiple>
                <Option v-for="item in $store.state.role.roleNoPageList" :value="item.id" :key="item.id">{{ $t(item.roleName) }}</Option>
              </Select>
            </FormItem>
          </Col>
        </Row>
      </Form>
      <div v-jurisdiction="'edit'" class="demo-drawer-footer" style="padding-top:20px;">
        <Button size="large" long type="primary" @click="save()">{{ $t('system.save') }}</Button>
      </div>
    </Drawer>
  </div>
</template>
<script>
import store from '@/store'
export default {
  data () {
    store.dispatch('getUserList')
    return {
      columns1: [
        { type: 'index', width: 60, align: 'center' },
        { title: this.$t('system.user.username'), key: 'username' },
        { title: this.$t('system.user.name'), key: 'name' },
        { title: this.$t('system.user.enterpriseId'), key: 'enterpriseId' },
        { title: this.$t('system.operation'), slot: 'action', width: 150, align: 'center' }
      ],
      contextLine: 0,
      delConfirm: false,
      isInfo: false,
      validate: {
        username: [
          { required: true, message: this.$t('system.user.name') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        name: [
          { required: true, message: this.$t('system.user.name') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        enterpriseId: [
          { required: true, message: this.$t('system.user.enterpriseId') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    del () {
      let data = this.deldata
      this.delConfirm = false
      store.dispatch('delUser', data.id).then(res => {
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
          let roleList = this.$store.state.user.info.roleList
          let sourceRoleList = this.$store.state.role.roleNoPageList
          let index
          for (let i in roleList) {
            index = sourceRoleList.findIndex((obj) => {
              return obj.id === roleList[i]
            })
            roleList[i] = sourceRoleList[index]
          }
          store.dispatch('saveUser').then(res => {
            if (res.data.code === 200) {
              this.query()
              this.$Message.success(this.$t('system.success'))
              this.isInfo = false
            } else if (res.data.message === '用户名已存在！') {
              this.$Message.error(this.$t('system.user.username') + this.$t('system.validate.noRepeat'))
            } else {
              this.$Message.error(this.$t('system.fail'))
            }
          })
        }
      })
    },
    add () {
      store.dispatch('getUserInfo', {})
      store.dispatch('getRoleNoPageList')
      this.isInfo = true
    },
    query () {
      store.dispatch('getUserList')
    },
    info (row) {
      store.dispatch('getUserInfo', row)
      store.dispatch('getRoleNoPageList')
      this.isInfo = true
    },
    changePage (pageNumber) {
      this.$store.state.user.query.pageNumber = pageNumber
      this.query()
    },
    changeSizePage (pageSizeNumber) {
      this.$store.state.user.query.pageNumber = 1
      this.$store.state.user.query.singlePage = pageSizeNumber
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
