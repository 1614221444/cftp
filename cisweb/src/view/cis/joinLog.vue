<template>
  <div data-menu="menu.cis.joinLog" class="split-pane-page-wrapper">
    <Form inline  style="padding: 10px;margin-bottom: 20px;border: 2px solid #e8eaec;">
      <FormItem prop="username">
        <Input type="text" v-model="$store.state.joinLog.query.fileId" :placeholder="$t('cis.joinLog.fileId')">
        </Input>
      </FormItem>
      <FormItem>
        <Button type="primary" @click="query()">{{ $t('system.query') }}</Button>
      </FormItem>
    </Form>
    <Table
      :loading="$store.state.joinLog.loading"
      context-menu
      show-context-menu
      :columns="columns"
      :data="$store.state.joinLog.list"
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
      :page-size="$store.state.joinLog.query.size"
      :total="$store.state.joinLog.query.totalNumber"
      :current="$store.state.joinLog.query.current"
      :page-size-opts="[20,40,60,80,100]"
      style="text-align: right;margin-top: 5px;"
      show-total show-sizer >
    </Page>
  </div>
</template>
<script>
import store from '@/store'
export default {
  data () {

    store.dispatch('getControllerAll', this.$store.state.joinLog.query).then(() => {
      store.dispatch('getJoinLogList', this.$store.state.joinLog.query)
    })
    return {
      columns: [
        { type: 'index', width: 60, align: 'center' },
        { title: this.$t('cis.joinLog.sendId'), key: 'sendId', tooltip: true },
        { title: this.$t('cis.joinLog.receiverId'), key: 'receiverId', tooltip: true },
        { title: this.$t('cis.joinLog.fileId'), key: 'fileId', tooltip: true },
        { title: this.$t('cis.joinLog.fileSize'), key: 'fileSize', tooltip: true },
        { title: this.$t('cis.joinLog.sendDate'), key: 'createTime', tooltip: true }
      ],
      contextLine: 0,
      isInfo: false
    }
  },
  methods: {
    query () {
      store.dispatch('getJoinLogList', this.$store.state.joinLog.query)
    },
    info (row) {
      store.dispatch('getJoinLog', row)
      this.isInfo = true
    },
    changePage (pageNumber) {
      this.$store.state.joinLog.query.current = pageNumber
      this.query()
    },
    changeSizePage (pageSizeNumber) {
      this.$store.state.joinLog.query.current = 1
      this.$store.state.joinLog.query.size = pageSizeNumber
      this.query()
    }
  }
}
</script>
