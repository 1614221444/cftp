<template>
  <div data-menu="menu.sys.role" class="split-pane-page-wrapper">
    <Form inline  style="padding: 10px;margin-bottom: 20px;border: 2px solid #e8eaec;">
      <FormItem prop="roleName">
        <Input type="text" v-model="$store.state.role.query.roleName" :placeholder="$t('system.role.roleName')">
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
      :loading="$store.state.role.loading"
      context-menu
      show-context-menu
      :columns="columns1"
      :data="$store.state.role.roleList"
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
      :page-size="$store.state.role.query.singlePage"
      :total="$store.state.role.query.totalNumber"
      :current="$store.state.role.query.pageNumber"
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
      :title="$t('menu.sys.role') + $t('system.info') + ' - ' + $t($store.state.role.info.roleName)"
      v-model="isInfo"
      width="30%"
    >
      <Form ref="formValidate" :model="$store.state.role.info" :rules="validate">
        <Row :gutter="32">
          <Col span="24">
            <FormItem :label="$t('system.role.roleName')"  prop="roleName">
              <Input v-model="$store.state.role.info.roleName" :placeholder="$t('system.pleaseEnter') + $t('system.role.roleName')" />
            </FormItem>
          </Col>

          <Col span="24">
            <Tree :data="$store.state.role.info.newJurisdictionList" @on-check-change="changeJurisdiction" show-checkbox :render="renderContent"></Tree>
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
import { ergodicTree } from '@/libs/util'
export default {
  data () {
    store.dispatch('getRoleList', this.$store.state.role.query)
    return {
      columns1: [
        { type: 'index', width: 60, align: 'center' },
        { title: this.$t('system.role.roleName'),
          key: 'roleName',
          render: (h, params) => {
            return h('div', [
              h('strong', this.$t(params.row.roleName))
            ])
          }
        },
        { title: this.$t('system.operation'), slot: 'action', width: 150, align: 'center' }
      ],
      contextLine: 0,
      delConfirm: false,
      isInfo: false,
      validate: {
        roleName: [
          { required: true, message: this.$t('system.role.roleName') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    del () {
      let data = this.deldata
      this.delConfirm = false
      store.dispatch('delSysRole', data.id).then(res => {
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
          store.dispatch('saveSysRole').then(res => {
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
      store.dispatch('insertRole', {})
      this.isInfo = true
    },
    query () {
      store.dispatch('getRoleList', this.$store.state.role.query)
    },
    info (row) {
      store.dispatch('getSysRole', row)
      this.isInfo = true
    },
    changePage (pageNumber) {
      this.$store.state.menu.query.pageNumber = pageNumber
      this.query()
    },
    changeSizePage (pageSizeNumber) {
      this.$store.state.menu.query.pageNumber = 1
      this.$store.state.menu.query.singlePage = pageSizeNumber
      this.query()
    },
    renderContent (h, { root, node, data }) {
      // 判断是不是第一次执行
      let isFirst = data.nodeKey !== 0
      return h('span', {
        style: {
          display: 'inline-block',
          width: '100%'
        }
      }, [
        h('span', [
          h('Icon', {
            props: {
              type: data.data.icon
            },
            style: {
              marginRight: '8px'
            }
          }),
          h('span', this.$t(data.name))
        ]),
        h('span', {
          style: {
            display: 'inline-block',
            float: 'right',
            marginRight: '32px'
          }
        }, [
          isFirst
            ? h('Checkbox', {
              props: {
                value: data.edit
              },
              on: {
                'on-change': (val) => {
                  data.edit = val
                }
              }
            }
            )
            : h('span', this.$t('system.edit')),
          isFirst ? h('checkbox', {
            props: {
              value: data.del
            },
            on: {
              'on-change': (val) => {
                data.del = val
              }
            },
            style: {
              marginLeft: '17px'
            }
          }
          ) : h('span', {
            props: {
              type: data.icon
            },
            style: {
              marginLeft: '11px'
            }
          }, this.$t('system.del'))
        ])
      ])
    },
    changeJurisdiction (datas, checked) {
      // 遍历当前所有子节点 将权限同步
      ergodicTree([checked], (data) => {
        data.del = data.checked
        data.edit = data.checked
      })
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
