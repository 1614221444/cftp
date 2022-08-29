import axios from '@/libs/api.request'

export const getRoleList = (data) => {
  return axios.request({
    url: '/sys/role/list',
    data: data,
    method: 'post'
  })
}

export const getRoleNoPageList = (data) => {
  return axios.request({
    url: '/sys/role/noPageList',
    data: data,
    method: 'post'
  })
}
export const save = (data) => {
  return axios.request({
    url: '/sys/role/save',
    data: data,
    method: 'post'
  })
}

export const del = (id) => {
  return axios.request({
    url: '/sys/role/delete',
    data: { id: id },
    method: 'post'
  })
}
