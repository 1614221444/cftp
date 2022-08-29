import axios from '@/libs/api.request'

export const getList = (data) => {
  return axios.request({
    url: '/sys/log/list',
    data: data,
    method: 'post'
  })
}

export const retry = (data) => {
  // 标识参数包含json
  data.isJson = true
  return axios.request({
    url: '/sys/log/retry',
    data: data,
    method: 'post'
  })
}
