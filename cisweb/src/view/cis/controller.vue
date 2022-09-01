<template>
  <div data-menu="menu.cis.controller" class="split-pane-page-wrapper">
    <Form inline  style="padding: 10px;margin-bottom: 20px;border: 2px solid #e8eaec;">
      <FormItem prop="roleName">
        <Input type="text" v-model="$store.state.controller.query.controllerName" :placeholder="$t('cis.controller.controllerName')">
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
    <!--<Table
      :loading="$store.state.controller.loading"
      context-menu
      show-context-menu
      :columns="columns1"
      :data="$store.state.controller.controllerList"
      border
      stripe
    >
      <template slot-scope="{ row, index }" slot="action">
        <Button type="success" v-jurisdiction="'edit'" size="small" style="margin-right: 5px" @click="copy(row, index)">{{ $t('system.copy') }}</Button>
        <Button type="primary" size="small" style="margin-right: 5px" @click="info(row, index)">{{ $t('system.info') }}</Button>
        <Button type="error" v-jurisdiction="'del'" size="small" @click="delInit(row)">{{ $t('system.del') }}</Button>
      </template>
    </Table>-->

    <Col span="8"
         :loading="$store.state.controller.loading"
         v-for="(data, index) in $store.state.controller.controllerList"
         :key="index"
        class="server">
      <Card>
        <p slot="title">
          {{data.controllerName}}
        </p>
        <p slot="extra" type="record">
          <Icon @click="start(data)" v-if="!data.isStart" class="server_button server_status server_start" type="ios-play"></Icon>
          <Icon @click="stop(data)" v-if="data.isStart" class="server_button server_status server_stop" type="ios-power"></Icon>
          <Icon :class="['server_status', data.isStart ? 'server_start': 'server_stop']" type="ios-radio-button-on"></Icon>
        </p>
        <p class="server_text">{{data.ip + ':' +data.port}}</p>
        <p class="server_text">{{$t('cis.controller.agreementType')}}：{{$t($dict('AGREEMENT_TYPE',data.agreementType))}}</p>
        <p class="server_text">{{$t('cis.controller.serverType')}}：{{$t($dict('SERVER_TYPE',data.serverType))}}</p>
        <p class="server_text">{{$t('cis.controller.startTime')}}：{{data.startTime}}</p>
        <p style="text-align: right">
          <Icon v-jurisdiction="'edit'" @click="info(data)" class="server_button server_status server_abnormal" type="ios-create"></Icon>
          <Icon v-jurisdiction="'del'" @click="delInit(data)" class="server_button server_status server_stop" type="ios-trash"></Icon>
        </p>

      </Card>
    </Col>
    <div style="width: 300px;">
      <Page
        @on-change="changePage"
        @on-page-size-change="changeSizePage"
        :page-size="$store.state.controller.query.singlePage"
        :total="$store.state.controller.query.totalNumber"
        :current="$store.state.controller.query.pageNumber"
        :page-size-opts="[20,40,60,80,100]"
        style="text-align: right;margin-top: 5px;"
        show-total show-sizer >
      </Page>
    </div>
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
      :title="$t('menu.cis.controller') + $t('system.info')"
      v-model="isInfo"
      width="30%"
    >
      <Form ref="formValidate" :model="$store.state.controller.info" :rules="validate">
        <Row :gutter="32">
          <Col span="24">
            <FormItem :label="$t('cis.controller.controllerName')"  prop="controllerName">
              <Input v-model="$store.state.controller.info.controllerName" :placeholder="$t('system.pleaseEnter') + $t('cis.controller.controllerName')" />
            </FormItem>
          </Col>
          <Col span="24">
            <FormItem :label="$t('cis.controller.agreementType')"  prop="agreementType">
              <Select :disabled="$store.state.controller.info.id != undefined" v-model="$store.state.controller.info.agreementType" style="width: 100%" >
                <Option v-for="item in $dictList('AGREEMENT_TYPE')" :value="item.dictValue" :key="item.id">{{ $t(item.title) }}</Option>
              </Select>
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem :label="$t('cis.controller.serverType')"  prop="serverType">
              <Select :disabled="$store.state.controller.info.id != undefined" v-model="$store.state.controller.info.serverType" style="width: 100%"  @on-change="changeServerType" >
                <Option v-for="item in $dictList('SERVER_TYPE')" :value="item.dictValue" :key="item.id">{{ $t(item.title) }}</Option>
              </Select>
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem :label="$t('cis.controller.startType')"  prop="startType">
              <Select v-model="$store.state.controller.info.startType" style="width: 100%">
                <Option v-for="item in $dictList('START_TYPE')" :value="item.dictValue" :key="item.id">{{ $t(item.title) }}</Option>
              </Select>
            </FormItem>
          </Col>
          <Col span="16">
            <FormItem :label="$t('cis.controller.ip')"  prop="ip">
              <Input v-model="$store.state.controller.info.ip" :disabled="$store.state.controller.info.serverType === '0'" :placeholder="$t('system.pleaseEnter') + $t('cis.controller.ip')" />
            </FormItem>
          </Col>
          <Col span="8">
            <FormItem :label="$t('cis.controller.port')"  prop="port">
              <Input v-model="$store.state.controller.info.port" :placeholder="$t('system.pleaseEnter') + $t('cis.controller.port')" />
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
    store.dispatch('getCisControllerList', this.$store.state.controller.query)
    return {
      contextLine: 0,
      delConfirm: false,
      isInfo: false,
      validate: {
        controllerName: [
          { required: true, message: this.$t('cis.controller.controllerName') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        agreementType: [
          { required: true, message: this.$t('cis.controller.agreementType') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        serverType: [
          { required: true, message: this.$t('cis.controller.serverType') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        startType: [
          { required: true, message: this.$t('cis.controller.startType') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        ip: [
          { required: true, message: this.$t('cis.controller.ip') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        port: [
          { required: true, message: this.$t('cis.controller.port') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    del () {
      let data = this.deldata
      this.delConfirm = false
      store.dispatch('delCisController', data.id).then(res => {
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
          store.dispatch('saveCisController').then(res => {
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
      store.dispatch('insertCisController', {})
      this.isInfo = true
    },
    query () {
      store.dispatch('getCisControllerList', this.$store.state.controller.query)
    },
    info (row) {
      store.dispatch('getCisController', row)
      this.isInfo = true
    },
    changePage (pageNumber) {
      this.$store.state.controller.query.pageNumber = pageNumber
      this.query()
    },
    changeSizePage (pageSizeNumber) {
      this.$store.state.controller.query.pageNumber = 1
      this.$store.state.controller.query.singlePage = pageSizeNumber
      this.query()
    },
    changeServerType (data) {
      if (data === '0') {
        this.$store.state.controller.info.ip = 'localhost'
      } else {
        this.$store.state.controller.info.ip = ''
      }
    },
    start (data) {
      store.dispatch('startCisController', data.id).then(res => {
        if (res.data.code === 200) {
          this.query()
          this.$Message.success(this.$t('system.success'))
          this.isInfo = false
        }
      })
    },
    stop (data) {
      store.dispatch('stopCisController', data.id).then(res => {
        if (res.data.code === 200) {
          this.query()
          this.$Message.success(this.$t('system.success'))
          this.isInfo = false
        }
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
  .server_status {
    font-size: 25px;
  }
  .server {
    padding: 3px;
  }
  .server_start {
    color: #499C54;
  }
  .server_stop {
    color: RED;
  }
  .server_abnormal {
    color: #DE9A2E;
  }
  .server_button {
    cursor: pointer;
  }
  .server_text {
    padding: 2px 1px
  }
</style>
