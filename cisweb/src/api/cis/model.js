import axios from '@/libs/api.request'

export const getList = (data) => {
  return axios.request({
    url: '/cis/model/list',
    data: data,
    method: 'post'
  })
}

export const getlistAll = () => {
  return axios.request({
    url: '/cis/model/listAll',
    method: 'post'
  })
}
export const save = (data) => {
  return axios.request({
    url: '/cis/model/save',
    data: data,
    method: 'post'
  })
}

export const del = (id) => {
  return axios.request({
    url: '/cis/model/delete',
    data: { id: id },
    method: 'post'
  })
}
