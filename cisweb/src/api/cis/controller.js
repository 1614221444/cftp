import axios from '@/libs/api.request'

export const getList = (data) => {
  return axios.request({
    url: '/cis/controller/list',
    data: data,
    method: 'post'
  })
}

export const getNoPageList = (data) => {
  return axios.request({
    url: '/cis/controller/noPageList',
    data: data,
    method: 'post'
  })
}
export const save = (data) => {
  return axios.request({
    url: '/cis/controller/save',
    data: data,
    method: 'post'
  })
}

export const del = (id) => {
  return axios.request({
    url: '/cis/controller/delete',
    data: { id: id },
    method: 'post'
  })
}

export const getAuthList = (controllerId) => {
  return axios.request({
    url: '/cis/controller/getAuthList',
    data: controllerId,
    method: 'post'
  })
}
export const start = (id) => {
  return axios.request({
    url: '/cis/controller/start',
    data: { id: id },
    method: 'post'
  })
}

export const stop = (id) => {
  return axios.request({
    url: '/cis/controller/stop',
    data: { id: id },
    method: 'post'
  })
}
