import axios from '@/libs/api.request'

export const getList = (data) => {
  return axios.request({
    url: '/cis/sendLog/list',
    data: data,
    method: 'post'
  })
}
