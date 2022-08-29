<template>
  <div data-menu="menu.sys.dict" class="split-pane-page-wrapper">
    <Form inline  style="padding: 10px;margin-bottom: 20px;border: 2px solid #e8eaec;">
      <FormItem prop="roleName">
        <Input type="text" v-model="$store.state.dict.query.dictKey" :placeholder="$t('system.dict.key')">
        </Input>
      </FormItem>
      <FormItem prop="remakes">
        <Input type="text" v-model="$store.state.dict.query.remakes" :placeholder="$t('system.dict.remakes')">
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
      :loading="$store.state.dict.loading"
      context-menu
      show-context-menu
      :columns="columns1"
      :data="$store.state.dict.dictList"
      border
      stripe
    >
      <template slot-scope="{ row, index }" slot="action">
        <Button type="success" v-jurisdiction="'edit'" size="small" style="margin-right: 5px" @click="copy(row, index)">{{ $t('system.copy') }}</Button>
        <Button type="primary" size="small" style="margin-right: 5px" @click="info(row, index)">{{ $t('system.info') }}</Button>
        <Button type="error" v-jurisdiction="'del'" size="small" @click="delInit(row)">{{ $t('system.del') }}</Button>
      </template>
    </Table>
    <Page
      @on-change="changePage"
      @on-page-size-change="changeSizePage"
      :page-size="$store.state.dict.query.singlePage"
      :total="$store.state.dict.query.totalNumber"
      :current="$store.state.dict.query.pageNumber"
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
      :title="$t('menu.sys.dict') + $t('system.info')"
      v-model="isInfo"
      width="30%"
    >
      <Form ref="formValidate" :model="$store.state.dict.info" :rules="validate">
        <Row :gutter="32">
          <Col span="24">
            <FormItem :label="$t('system.dict.key')"  prop="dictKey">
              <Input v-model="$store.state.dict.info.dictKey" :placeholder="$t('system.pleaseEnter') + $t('system.dict.key')" />
            </FormItem>
          </Col>
          <Col span="24">
          <FormItem :label="$t('system.dict.title')"  prop="title">
            <Input v-model="$store.state.dict.info.title" :placeholder="$t('system.pleaseEnter') + $t('system.dict.title')" />
          </FormItem>
          </Col>
          <Col span="24">
            <FormItem :label="$t('system.dict.value')"  prop="dictValue">
              <Input v-model="$store.state.dict.info.dictValue" :placeholder="$t('system.pleaseEnter') + $t('system.dict.value')" />
            </FormItem>
          </Col>
          <Col span="24">
          <FormItem :label="$t('system.dict.remakes')"  prop="remakes">
            <Input v-model="$store.state.dict.info.remakes" :placeholder="$t('system.pleaseEnter') + $t('system.dict.remakes')" />
          </FormItem>
          </Col>
          <Col span="24">
          <FormItem :label="$t('system.dict.order')"  prop="dictOrder">
            <Input v-model="$store.state.dict.info.dictOrder" :placeholder="$t('system.pleaseEnter') + $t('system.dict.order')" />
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
    // TEST CODE console.log(this.$route.params.data)
    store.dispatch('getSysDictList', this.$store.state.dict.query)
    return {
      columns1: [
        { type: 'index', width: 60, align: 'center' },
        { title: this.$t('system.dict.key'), key: 'dictKey' },
        { title: this.$t('system.dict.title'), key: 'title' },
        { title: this.$t('system.dict.value'), key: 'dictValue' },
        { title: this.$t('system.dict.remakes'), key: 'remakes', tooltip: true },
        { title: this.$t('system.operation'), slot: 'action', width: 200, align: 'center' }
      ],
      contextLine: 0,
      delConfirm: false,
      isInfo: false,
      validate: {
        key: [
          { required: true, message: this.$t('system.dict.key') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        value: [
          { required: true, message: this.$t('system.dict.value') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    del () {
      let data = this.deldata
      this.delConfirm = false
      store.dispatch('delSysDict', data.id).then(res => {
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
          store.dispatch('saveSysDict').then(res => {
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
      store.dispatch('insertDict', {})
      this.isInfo = true
    },
    query () {
      store.dispatch('getSysDictList', this.$store.state.dict.query)
    },
    info (row) {
      store.dispatch('getSysDict', row)
      this.isInfo = true
    },
    copy (row) {
      store.dispatch('copy', row)
      this.isInfo = true
    },
    changePage (pageNumber) {
      this.$store.state.dict.query.pageNumber = pageNumber
      this.query()
    },
    changeSizePage (pageSizeNumber) {
      this.$store.state.dict.query.pageNumber = 1
      this.$store.state.dict.query.singlePage = pageSizeNumber
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
