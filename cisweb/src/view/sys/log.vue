<template>
  <div data-menu="menu.sys.log" class="split-pane-page-wrapper">
    <Form inline  style="padding: 10px;margin-bottom: 20px;border: 2px solid #e8eaec;">
      <FormItem prop="username">
        <Input type="text" v-model="$store.state.log.query.title" :placeholder="$t('system.log.title')">
        </Input>
      </FormItem>
      <FormItem prop="name">
        <Input type="text" v-model="$store.state.log.query.errorText" :placeholder="$t('system.log.errorText')">
        </Input>
      </FormItem>
      <Select v-model="$store.state.log.query.state" style="width: 120px;margin: 1px 11px 1px 1px;" clearable>
        <Option v-for="item in $dictList('HANDLE')" :value="item.dictValue" :key="item.id">{{ $t(item.title) }}</Option>
      </Select>
      <FormItem>
        <Button type="primary" @click="query()">{{ $t('system.query') }}</Button>
      </FormItem>
    </Form>
    <Table
      :loading="$store.state.log.loading"
      context-menu
      show-context-menu
      :columns="columns"
      :data="$store.state.log.list"
      border
      stripe
    >
      <template slot-scope="{ row, index }" slot="action">
        <Button type="primary" size="small" style="margin-right: 3px" @click="info(row, index)">{{ $t('system.info') }}</Button>
        <Button v-show="row.state == 0" size="small" type="warning" v-jurisdiction="'edit'" @click="retry(row)">{{ $t('system.log.retry') }}</Button>
      </template>
    </Table>
    <Page
      @on-change="changePage"
      @on-page-size-change="changeSizePage"
      :page-size="$store.state.log.query.singlePage"
      :total="$store.state.log.query.totalNumber"
      :current="$store.state.log.query.pageNumber"
      :page-size-opts="[20,40,60,80,100]"
      style="text-align: right;margin-top: 5px;"
      show-total show-sizer >
    </Page>
    <Drawer
      :title="$t('menu.sys.log') + $t('system.info')"
      v-model="isInfo"
      width="40%"
    >
      <Form ref="formValidate">
        <col :gutter="32">
          <Col span="24">
            <FormItem :label="$t('system.log.title')"  prop="title">
              <Input v-model="$store.state.log.info.title" />
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem :label="$t('system.log.state')"  prop="state">
              <Input v-model="$store.state.log.info.state" />
            </FormItem>
          </Col>
          <Col span="12">
          <FormItem :label="$t('system.log.timeUse')"  prop="timeUse">
            <Input v-model="$store.state.log.info.timeUse" />
          </FormItem>
          </Col>
          <Col span="12">
            <FormItem :label="$t('system.log.startDate')"  prop="startDate">
              <Input v-model="$store.state.log.info.startDate" />
            </FormItem>
          </Col>
          <Col span="12">
          <FormItem :label="$t('system.log.endDate')"  prop="endDate">
            <Input v-model="$store.state.log.info.endDate" />
          </FormItem>
          </Col>
          <Col span="12">
          <FormItem :label="$t('system.log.serviceName')"  prop="serviceName">
            <Input v-model="$store.state.log.info.serviceName" />
          </FormItem>
          </Col>
          <Col span="12">
          <FormItem :label="$t('system.log.declaredMethodName')"  prop="declaredMethodName">
            <Input v-model="$store.state.log.info.declaredMethodName" />
          </FormItem>
          </Col>
          <Col span="24">
          <FormItem :label="$t('system.log.pojo')"  prop="pojo">
            <Input v-model="$store.state.log.info.pojo" type="textarea" :rows="4"/>
          </FormItem>
          </Col>
          <Col span="24">
          <FormItem :label="$t('system.log.errorText')"  prop="errorText">
            <Input v-model="$store.state.log.info.errorText" type="textarea" :rows="4"/>
          </FormItem>
          </Col>
          <Col span="24">
            <Button v-show="$store.state.log.info.state == 0" long type="warning" v-jurisdiction="'edit'" @click="retry($store.state.log.info)">{{ $t('system.log.retry') }}</Button>
          </col>
        </Row>
      </Form>
    </Drawer>
  </div>
</template>
<script>
import store from '@/store'
export default {
  data () {
    store.dispatch('getSysLogList', this.$store.state.log.query)
    return {
      columns: [
        { type: 'index', width: 60, align: 'center' },
        { title: this.$t('system.log.title'), width: 450, key: 'title', tooltip: true },
        { title: this.$t('system.log.state'),
          key: 'state',
          render: (h, params) => {
            let color = '#000'
            switch (params.row.state) {
              case '0':
                color = 'red'
                break
              case 1:
                color = 'yellow'
                break
              case 2:
                color = 'blue'
                break
              case '3':
                color = 'green'
                break
            }
            return h('div', [
              h('strong', {
                style: {
                  color: color
                }
              }, this.$t(this.$dict('HANDLE', params.row.state)))
            ])
          }
        },
        { title: this.$t('system.log.startDate'), width: 150, key: 'startDate' },
        { title: this.$t('system.log.timeUse'), key: 'timeUse' },
        { title: this.$t('system.operation'), slot: 'action', width: 130, align: 'center' }
      ],
      contextLine: 0,
      isInfo: false
    }
  },
  methods: {
    query () {
      store.dispatch('getSysLogList', this.$store.state.log.query)
    },
    info (row) {
      store.dispatch('getSysLog', row)
      this.isInfo = true
    },
    retry (row) {
      store.dispatch('retry', row).then(
        () => {
          this.$store.state.log.loading = true
          const msg = this.$Message.loading({
            content: this.$t('system.log.delayRefresh'),
            duration: 0
          })
          setTimeout(msg, 5000)
          setTimeout(() => this.query(), 5000)
        }
      )
      this.isInfo = false
    },
    changePage (pageNumber) {
      this.$store.state.log.query.pageNumber = pageNumber
      this.query()
    },
    changeSizePage (pageSizeNumber) {
      this.$store.state.log.query.pageNumber = 1
      this.$store.state.log.query.singlePage = pageSizeNumber
      this.query()
    }
  }
}
</script>
