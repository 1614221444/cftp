import axios from '@/libs/api.request'

export const getList = (data) => {
  return axios.request({
    url: '/sys/job/list',
    data: data,
    method: 'post'
  })
}

export const save = (data) => {
  return axios.request({
    url: '/sys/job/save',
    data: data,
    method: 'post'
  })
}

export const del = (id) => {
  return axios.request({
    url: '/sys/job/delete',
    data: { id: id },
    method: 'post'
  })
}

export const logList = (data) => {
  return axios.request({
    url: '/sys/job/logList',
    data: data,
    method: 'post'
  })
}
