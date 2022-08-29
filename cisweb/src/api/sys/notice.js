import axios from '@/libs/api.request'

export const getList = (data) => {
  return axios.request({
    url: '/sys/notice/list',
    data: data,
    method: 'post'
  })
}

export const save = (data) => {
  return axios.request({
    url: '/sys/notice/save',
    data: data,
    method: 'post'
  })
}

export const del = (id) => {
  return axios.request({
    url: '/sys/notice/delete',
    data: { id: id },
    method: 'post'
  })
}

export const push = (data) => {
  return axios.request({
    url: '/sys/notice/push',
    data: data,
    method: 'post'
  })
}

export const news = () => {
  return axios.request({
    url: '/sys/notice/news',
    method: 'post'
  })
}

export const read = (data) => {
  return axios.request({
    url: '/sys/notice/read',
    data: data,
    method: 'post'
  })
}

export const readAll = (data) => {
  return axios.request({
    url: '/sys/notice/readAll',
    data: data,
    method: 'post'
  })
}

export const delPush = (data) => {
  return axios.request({
    url: '/sys/notice/delPush',
    data: data,
    method: 'post'
  })
}
