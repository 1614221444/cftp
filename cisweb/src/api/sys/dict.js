import axios from '@/libs/api.request'

export const getList = (data) => {
  return axios.request({
    url: '/sys/dict/list',
    data: data,
    method: 'post'
  })
}

export const getNoPageList = (data) => {
  return axios.request({
    url: '/sys/dict/noPageList',
    data: data,
    method: 'post'
  })
}
export const save = (data) => {
  return axios.request({
    url: '/sys/dict/save',
    data: data,
    method: 'post'
  })
}

export const del = (id) => {
  return axios.request({
    url: '/sys/dict/delete',
    data: { id: id },
    method: 'post'
  })
}
