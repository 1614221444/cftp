<template>
  <div data-menu="menu.cis.service" class="split-pane-page-wrapper">
    <Form inline  style="padding: 10px;margin-bottom: 20px;border: 2px solid #e8eaec;">
      <FormItem prop="username">
        <Input type="text" v-model="$store.state.model.query.name" :placeholder="$t('cis.model.name')">
        </Input>
      </FormItem>

      <Select v-model="$store.state.model.query.type" style="width: 120px;margin: 1px 11px 1px 1px;" clearable>
        <Option v-for="item in $dictList('MODEL_TYPE')" :value="item.dictValue" :key="item.id">{{ $t(item.title) }}</Option>
      </Select>
      <FormItem>
        <Button type="primary" @click="query()">{{ $t('system.query') }}</Button>
      </FormItem>
      <Divider dashed class="divider"/>
      <div class="operation">
        <Button type="primary" v-jurisdiction="'edit'" @click="add()">{{ $t('system.add') }}</Button>
      </div>
    </Form>
    <Table
      :loading="$store.state.model.loading"
      context-menu
      show-context-menu
      :columns="columns"
      :data="$store.state.model.list"
      border
      stripe
    >
      <template slot-scope="{ row, index }" slot="action">
        <Button type="primary" size="small" style="margin-right: 3px" @click="info(row, index)">{{ $t('system.info') }}</Button>
        <Button type="error" v-jurisdiction="'del'" size="small" @click="delInit(row)">{{ $t('system.del') }}</Button>
      </template>
    </Table>
    <Page
      @on-change="changePage"
      @on-page-size-change="changeSizePage"
      :page-size="$store.state.model.query.size"
      :total="$store.state.model.query.totalNumber"
      :current="$store.state.model.query.current"
      :page-size-opts="[20,40,60,80,100]"
      style="text-align: right;margin-top: 5px;"
      show-total show-sizer >
        <template slot-scope="{ row, index }" slot="action">
          <Button type="primary" size="small" style="margin-right: 5px" @click="info(row, index)">{{ $t('system.info') }}</Button>
          <Button type="error" v-jurisdiction="'del'" size="small" @click="delInit(row)">{{ $t('system.del') }}</Button>
        </template>
    </Page>

    <Drawer
      :title="$t('menu.sys.user') + $t('system.info')"
      v-model="isInfo"
      width="30%"
    >
      <Form ref="formValidate" :model="$store.state.model.info" :rules="validate">
        <Row :gutter="32">
          <Col span="24">
            <FormItem :label="$t('cis.model.name')"  prop="name">
              <Input v-model="$store.state.model.info.name" :placeholder="$t('system.pleaseEnter') + $t('cis.model.name')" />
            </FormItem>
          </Col>
          <Col span="24">
            <FormItem :label="$t('cis.model.type')"  prop="type">
              <Select v-model="$store.state.model.info.type" clearable>
                <Option v-for="item in $dictList('MODEL_TYPE')" :value="item.dictValue" :key="item.id">{{ $t(item.title) }}</Option>
              </Select>
            </FormItem>
          </Col>
          <Col span="24">
            <FormItem :label="$t('cis.model.remakes')"  prop="remakes">
              <Input v-model="$store.state.model.info.remakes" :placeholder="$t('system.pleaseEnter') + $t('cis.model.remakes')" type="textarea" :autosize="{minRows: 5,maxRows: 10}" />
            </FormItem>
          </Col>
          <Col span="24">
            <FormItem :label="$t('cis.model.script')"  prop="script">
              <Input v-model="$store.state.model.info.script" :placeholder="$t('system.pleaseEnter') + $t('cis.model.script')" type="textarea" :autosize="{minRows: 10,maxRows: 150}" />
            </FormItem>
          </Col>
        </Row>
      </Form>
      <div v-jurisdiction="'edit'" class="demo-drawer-footer" style="padding-top:20px;">
        <Button size="large" long type="primary" @click="save()">{{ $t('system.save') }}</Button>
      </div>
    </Drawer>

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
  </div>
</template>
<script>
import store from '@/store'
export default {
  data () {
    this.query()
    return {
      columns: [
        { type: 'index', width: 60, align: 'center' },
        { title: this.$t('cis.model.name'), key: 'name', tooltip: true },
        { title: this.$t('cis.model.type'),
          key: 'type',
          render: (h, params) => {
            return h('div', this.$t(this.$dict('MODEL_TYPE', params.row.type)))
          }
        },
        { title: this.$t('cis.model.remakes'), key: 'remakes', tooltip: true },
        { title: this.$t('system.operation'), slot: 'action', width: 200, align: 'center' }
      ],
      contextLine: 0,
      isInfo: false,
      delConfirm: false
    }
  },
  methods: {
    query () {
      store.dispatch('getModelList', this.$store.state.model.query)
    },
    info (row) {
      store.dispatch('getCisModel', row)
      this.isInfo = true
    },
    add () {
      store.dispatch('getCisModel', {})
      this.isInfo = true
    },
    save () {
      this.$refs['formValidate'].validate((valid) => {
        if (valid) {
          store.dispatch('saveCisModel').then(res => {
            if (res.data.code === 200) {
              this.query()
              this.$Message.success(this.$t('system.success'))
              this.isInfo = false
            } else {
              alert(res.data.message)
            }
          })
        }
      })
    },
    delInit (data) {
      this.deldata = data
      this.delConfirm = true
    },
    del () {
      let data = this.deldata
      this.delConfirm = false
      store.dispatch('delCisModel', data.id).then(res => {
        if (res.data.code === 200) {
          this.query()
          this.$Message.success(this.$t('system.success'))
        } else {
          this.$Message.error(this.$t('system.fail'))
        }
      })
    },
    changePage (pageNumber) {
      this.$store.state.model.query.current = pageNumber
      this.query()
    },
    changeSizePage (pageSizeNumber) {
      this.$store.state.model.query.current = 1
      this.$store.state.model.query.size = pageSizeNumber
      this.query()
    }
  }
}
</script>
