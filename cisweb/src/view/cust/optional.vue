<template>
  <div style="height:-webkit-fill-available;overflow-y:scroll; overflow-x:scroll;">
    <Affix :offset-top="1">
      <Steps :current="stage" style="padding: 20px 15%;background-color: #ebebeb;color: #ebebeb">
        <Step title="选择系列"></Step>
        <Step title="选择类型"></Step>
        <Step title="选择规格"></Step>
        <Step title="选择级数"></Step>
        <Step title="选择参数"></Step>
      </Steps>
    </Affix>
    <div style="padding-top: 10px;padding-bottom: 10px;padding-left: 10%;padding-right: 10%;padding-bottom: 400px;">
      <Card  style="">
        <div id="series">
          <Divider>选择系列</Divider>
          <a href="#prod" v-for="option in data" :key="option.id"  @click="optional(option, 1)">
            <i-col :style="choiceStyle(option)" span="8">
              <card style="padding:10px; margin: 10px;">
                <Divider>{{ option.value }}</Divider>
              </card>
            </i-col>
          </a>
        </div>
        <div id="prod" v-if="stage > 0">
          <Divider>选择产品</Divider>
          <a href="#size" v-for="option in select[0].children" :key="option.id"  @click="optional(option, 2)">
            <i-col :style="choiceStyle(option)" span="6">
              <card style="padding:10px; margin: 10px;">
                <img v-if="option.icon" :src="option.icon" width="100%;" />
                <Divider>{{ option.value }}</Divider>
              </card>
            </i-col>
          </a>
        </div>
        <div id="size" v-if="stage > 1">
          <Divider>选择规格</Divider>
          <a href="#stage" v-for="option in select[1].children" :key="option.id" @click="optional(option, 3)">
            <i-col :style="choiceStyle(option)" span="3" >
              <card  style="padding:10px; margin: 10px;text-align: center;font-size: 25px;">
                {{ option.value }}
              </card>
            </i-col>
          </a>
        </div>
        <div id="stage" v-if="stage > 2">
          <Divider>选择级数</Divider>
          <a href="#parameter" v-for="option in select[2].children" :key="option.id" @click="optional(option, 4)">
            <i-col :style="choiceStyle(option)" span="6">
              <card  style="padding:10px; margin: 10px;text-align: center;font-size: 25px;">
                {{ option.value }}
                <Divider  style="font-size: 15px;">价格</Divider>
                <div style="font-size: 20px;">￥{{option.valuation}}</div>
              </card>
            </i-col>
          </a>
        </div>
        <div id="parameter"  v-if="stage > 3">
          <Divider>参数配置</Divider>
          <div v-for="option in select[3].children" :key="option.id">
            <i-col span="8">
              <card  style="padding:10px; margin: 10px;text-align: center;">
                <Divider orientation="left">{{ option.title }}</Divider>
                <div v-for="coption in option.children" :key="coption.id"  @click="optionals(coption, option)">
                  <i-col :style="choiceStyle(coption)" span="12" >
                    <card style="margin: 4px;">
                      <img v-if="coption.icon" :src="coption.icon" width="100%;" />
                      {{coption.title}}<br/>
                      {{coption.valuationType === '1' ? '￥' + coption.valuation : coption.valuationType === '0' ? 'kAP' : coption.valuation + '%'}}
                    </card>
                  </i-col>
                </div>
                <Divider />
              </card>
            </i-col>
          </div>
        </div>
        <Divider />
      </Card>
    </div>
    <Affix  v-if="stage > 2" :offset-bottom="1">
      <div style="background-color: #ebebeb;display: flex;width: 100%">
        <div style="width: 70%;font-size: 20px;">
          <h2>满减策略</h2>
          <div id="discountText" style="padding-left: 40px;"></div>
        </div>
        <div style="width: 30%;text-align: right;background-color: #bbbbbb;padding-right: 21px;padding-left: 21px">
          <Divider>选配清单</Divider>
          <div v-for="text in optionalList" :key="text.text" style="font-size: 15px;">{{text}}</div>
          <div style="font-size: 19px;">
            数量：
            <Input-number style="background-color: #EBEBEB;" @on-change="calcPrice()" :min="1" size="small" v-model="quantity"></Input-number>
          </div>
          <div style="font-size: 20px;">原价：{{price}}</div>
          <div v-if="discountPrice !== 0" style="font-size: 22px;">
            满<span class="highlight">{{discount.number}}</span>件 可享<span class="highlight">{{discount.discount / 10}}</span>折优惠<br />
            折扣价：<span class="highlight">{{discountPrice}}</span>
          </div>
        </div>
      </div>
    </Affix>
  </div>
</template>

<script>
import store from '@/store'
export default {
  name: 'iotional',
  data () {
    this.query()
    return {
      quantity: 1,
      data: store.state.custApp.data,
      select: [],
      selects: {},
      stage: 0,
      optionalList: [],
      price: 0,
      discountPrice: 0,
      discount: {}
    }
  },
  updated () {
    // 让头部和底部长度变成自适应
    let affix = document.getElementsByClassName('ivu-affix')
    for (const i in affix) {
      affix[i].style.width = '100%'
    }
  },
  methods: {
    query () {
      store.dispatch('getCustData', { custType: store.state.custApp.custUser.customerType }).then(() => {
        this.data = store.state.custApp.data
      })
      if (store.state.custApp.custUser) {
      } else {
        this.$router.push({
          path: '/custLogin'
        })
      }
    },
    // 单选
    optional (data, index) {
      this.select[index - 1] = data
      // 删除之后的配置
      this.select = this.select.splice(0, index)
      this.selects = []
      this.stage = index
      // 选详细配置时默认选中
      if (index > 3) {
        this.defaultSelect(this.select[3].children)
      }
      this.calcPrice()
      this.$forceUpdate()
    },
    // 多选默认选中配置
    defaultSelect (data) {
      // 遍历选配类型
      for (let i in data) {
        let type = data[i]
        // 取选配内容
        for (let o in type.children) {
          // 判断是否默认选中
          if (type.children[o].defaultData) {
            // 手动触发选中方法
            this.optionals(type.children[o], type)
            break
          }
        }
      }
    },
    // 多选
    optionals (data, pdata) {
      data.pTitle = pdata.title
      this.selects['pid_' + data.pid] = data
      this.calcPrice()
      this.$forceUpdate()
    },
    // 判断当前配置是否被选中
    choiceStyle (data) {
      let style = {}
      for (let i in this.select) {
        if (this.select[i].id === data.id) {
          style = { backgroundColor: '#296174' }
        }
      }
      if (this.selects['pid_' + data.pid] && this.selects['pid_' + data.pid].id === data.id) {
        style = { backgroundColor: '#296174' }
      }
      return style
    },
    // 计算价格
    calcPrice () {
      // 基础价格
      let basePrice = 0
      // 选配价格
      let optionalPrice = 0
      // 选配明细
      let optionalText = []
      // 计算基础价格
      for (let i in this.select) {
        optionalText.push(`${this.select[i].title} : ${this.select[i].value}`)
        if (this.select[i].valuationType === '1') {
          basePrice += parseFloat(this.select[i].valuation)
        }
      }
      optionalText.push('--------------------------')
      // 计算选配价格
      for (let i in this.selects) {
        optionalText.push(`${this.selects[i].pTitle} : ${this.selects[i].title}`)
        if (this.selects[i].valuationType === '1') {
          optionalPrice += parseFloat(this.selects[i].valuation)
        } else if (this.selects[i].valuationType === '2') {
          optionalPrice += basePrice * (this.selects[i].valuation * 0.01 + 1) - basePrice
        }
      }
      this.optionalList = optionalText
      this.price = ((basePrice + optionalPrice) * this.quantity).toFixed(2)
      // 计算折扣价与税后价格
      this.calcDiscount(this.select, this.price)
    },
    calcDiscount (selects, price) {
      let discountList = []
      // 排查出优先级最接近的折扣方案
      for (let i = selects.length - 1; i >= 0; i--) {
        if (selects[i].discountList.length !== 0) {
          discountList = selects[i].discountList
          break
        }
      }
      let optimal = -1
      let discountText = ''
      // 按折扣方案计算折扣
      if (discountList.length !== 0) {
        // 给折扣排个序
        discountList.sort(function (a, b) {
          return a.number - b.number
        })
        for (let i in discountList) {
          discountText += `满${discountList[i].number}件 可享 ${(discountList[i].discount / 10)}折 优惠！<br />`
          let num = this.quantity - discountList[i].number
          if (num >= 0) {
            // 初始化最佳方案
            if (optimal === -1) {
              optimal = i
              // 对比最佳方案
            } else if (num < (this.quantity - discountList[optimal].number)) {
              optimal = i
            }
          }
        }
      }
      let htmlElement = document.getElementById('discountText')
      htmlElement.innerHTML = discountText
      if (optimal !== -1) {
        this.discountPrice = (price * (discountList[optimal].discount / 100)).toFixed(2)
        this.discount = discountList[optimal]
      } else {
        this.discountPrice = 0
      }
    }
  }
}
</script>
<style>
  #app {
    background-color: #f3f5f6;
  }
  .choice {
    background-color: #00ff00;
  }
  a {
    color: #000c17;
  }
  a:hover {
    color: #000c17;
  }
  .highlight {
    color: rgba(56, 96, 114, 0.88);
    font-size: 30px;
    font-weight: 600;
  }
</style>
