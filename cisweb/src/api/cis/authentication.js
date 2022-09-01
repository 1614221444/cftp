import axios from '@/libs/api.request'

export const getList = (data) => {
  return axios.request({
    url: '/cis/authentication/list',
    data: data,
    method: 'post'
  })
}

export const getNoPageList = (data) => {
  return axios.request({
    url: '/cis/authentication/noPageList',
    data: data,
    method: 'post'
  })
}
export const save = (data) => {
  return axios.request({
    url: '/cis/authentication/save',
    data: data,
    method: 'post'
  })
}

export const del = (id) => {
  return axios.request({
    url: '/cis/authentication/delete',
    data: { id: id },
    method: 'post'
  })
}
