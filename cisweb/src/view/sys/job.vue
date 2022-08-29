<template>
  <div data-menu="menu.sys.job" class="split-pane-page-wrapper">
    <Form inline  style="padding: 10px;margin-bottom: 20px;border: 2px solid #e8eaec;">
      <FormItem prop="code">
        <Input type="text" v-model="$store.state.job.query.jobName" :placeholder="$t('system.job.jobName')">
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
      :loading="$store.state.job.loading"
      context-menu
      show-context-menu
      :columns="columns1"
      :data="$store.state.job.list"
      border
      stripe
    >
      <template slot-scope="{ row, index }" slot="status">
        <i-switch  v-jurisdiction="'edit'" @on-change="save(row)" v-model="row.status" true-color="#13ce66" false-color="#ff4949" true-value="1" false-value="2"/>
      </template>
      <template slot-scope="{ row, index }" slot="action">

        <Button type="warning" size="small" style="margin-right: 5px" @click="getJobLogList(row)">{{ $t('menu.sys.log') }}</Button>
        <Button type="primary" size="small" style="margin-right: 5px" @click="info(row, index)">{{ $t('system.info') }}</Button>
        <Button type="error" v-jurisdiction="'del'" size="small" @click="delInit(row)">{{ $t('system.del') }}</Button>
      </template>
    </Table>
    <Page
      @on-change="changePage"
      @on-page-size-change="changeSizePage"
      :page-size="$store.state.job.query.singlePage"
      :total="$store.state.job.query.totalNumber"
      :current="$store.state.job.query.pageNumber"
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
      :title="$t('menu.sys.job') + $t('system.info')"
      v-model="isInfo"
      width="30%"
    >
      <Form ref="formValidate" :model="$store.state.job.info" :rules="validate">
        <Row :gutter="32">
          <Col span="24">
            <FormItem :label="$t('system.job.jobName')"  prop="jobName">
              <Input v-model="$store.state.job.info.jobName" :placeholder="$t('system.pleaseEnter') + $t('system.job.jobName')" />
            </FormItem>
          </Col>
          <Col span="24">
            <FormItem :label="$t('system.job.beanName')"  prop="beanName">
              <Input v-model="$store.state.job.info.beanName" :placeholder="$t('system.pleaseEnter') + $t('system.job.beanName')" />
            </FormItem>
          </Col>
          <Col span="24">
            <FormItem :label="$t('system.job.methodName')"  prop="methodName">
              <Input v-model="$store.state.job.info.methodName" :placeholder="$t('system.pleaseEnter') + $t('system.job.methodName')" />
            </FormItem>
          </Col>
          <Col span="12">
          <FormItem :label="$t('system.job.cron')"  prop="cron">
            <Input v-model="$store.state.job.info.cronExpression" :placeholder="$t('system.pleaseEnter') + $t('system.job.cron')" />
          </FormItem>
          </Col>
          <Col span="12">
          <FormItem :label="$t('system.job.state')"  prop="state">
            <Select v-model="$store.state.job.info.status" style="width: 100%" >
              <Option v-for="item in $dictList('JOB_STATE')" :value="item.dictValue" :key="item.id">{{ $t(item.title) }}</Option>
            </Select>
          </FormItem>
          </Col>
        </Row>
      </Form>
      <div class="demo-drawer-footer">
        <Button size="large" long v-jurisdiction="'edit'" type="primary" @click="save()">{{ $t('system.save') }}</Button>
      </div>
    </Drawer>

    <Drawer
      :title="$t('menu.sys.job') + $t('menu.sys.log') + '(' + logPage.totalNumber + ') - ' + log.jobName"
      v-model="isLogInfo"
      width="40%"
    >
      <Scroll :on-reach-bottom="handleReachBottom" :height="logInfoHeight">
        <Collapse>
              <Panel :name="item.id" dis-hover v-for="(item, index) in logPage.pageList" :key="index" :class="item.state === '3' ? 'success': 'error'">
                <span style="color:#fff;">
                  {{ $t($dict('HANDLE', item.state)) }}
                </span>
                <span style="color:#fff;float: right;padding-right: 5px">
                  {{ item.startDate }}
                </span>
                <p slot="content">
                  {{ item.text }}
                  <br/>
                  <span style="float: right;">
                    用时：{{ item.timeUse }}s
                 </span>
                  <br/>
                </p>
              </Panel>
        </Collapse>
      </Scroll>
    </Drawer>
  </div>
</template>
<script>
import store from '@/store'

export default {
  data () {
    store.dispatch('getJobList', this.$store.state.job.query)
    return {
      columns1: [
        { type: 'index', width: 60, align: 'center' },
        { title: this.$t('system.job.jobName'), key: 'jobName' },
        { title: this.$t('system.job.beanName'), key: 'beanName' },
        { title: this.$t('system.job.methodName'), key: 'methodName' },
        { title: this.$t('system.job.cron'), key: 'cronExpression' },
        { title: this.$t('system.job.state'), slot: 'status', width: 100, align: 'center' },
        { title: this.$t('system.operation'), slot: 'action', width: 200, align: 'center' }
      ],
      contextLine: 0,
      delConfirm: false,
      isInfo: false,
      isLogInfo: false,
      logPage: {},
      log: {},
      logInfoHeight: window.innerHeight - 100,
      validate: {
        jobName: [
          { required: true, message: this.$t('system.job.jobName') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        beanName: [
          { required: true, message: this.$t('system.job.beanName') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        methodName: [
          { required: true, message: this.$t('system.job.methodName') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        cronExpression: [
          { required: true, message: this.$t('system.job.cron') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    del () {
      let data = this.deldata
      this.delConfirm = false
      store.dispatch('delJob', data.id).then(res => {
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
    save (info) {
      this.$refs['formValidate'].validate((valid) => {
        if (valid) {
          store.dispatch('saveJob', info).then(res => {
            if (res.data.code === 200) {
              this.query()
              this.$Message.success(this.$t('system.success'))
              this.isInfo = false
            }
          })
        }
      })
    },
    getJobLogList (data) {
      this.log = data
      store.state.job.loading = true
      store.dispatch('getJobLogList', { jobId: data.id, pageNumber: 1, singlePage: 100 }).then(res => {
        this.logPage = res.data
        this.isLogInfo = true
        store.state.job.loading = false
      })
    },
    handleReachBottom () {
      return new Promise(resolve => {
        // 本地响应的太快了 以后把他设置成0
        setTimeout(() => {
          // 下一页
          this.logPage.pageNumber++
          store.dispatch('getJobLogList', { jobId: this.log.id, pageNumber: this.logPage.pageNumber, singlePage: 100 }).then(res => {
            for (let i in res.data.pageList) {
              this.logPage.pageList.push(res.data.pageList[i])
            }
            resolve()
          })
        }, 100)
      })
    },
    add () {
      store.dispatch('insertJob', {})
      this.isInfo = true
    },
    query () {
      store.dispatch('getJobList', this.$store.state.job.query)
    },
    info (row) {
      store.dispatch('getJob', row)
      this.isInfo = true
    },
    changePage (pageNumber) {
      this.$store.state.job.query.pageNumber = pageNumber
      this.query()
    },
    changeSizePage (pageSizeNumber) {
      this.$store.state.job.query.pageNumber = 1
      this.$store.state.job.query.singlePage = pageSizeNumber
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
  .error{
    background:#FF6666;
  }
  .success{
    background:#339933;
  }
</style>
