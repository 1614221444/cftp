import axios from '@/libs/api.request'
import { getToken } from '@/libs/util'

export const login = ({ username, password }) => {
  const data = {
    username,
    password
  }
  return axios.request({
    url: 'login',
    data,
    method: 'post'
  })
}

export const getUserInfo = (token) => {
  return axios.request({
    url: 'sys/user/get',
    params: {
      id: getToken()
    },
    method: 'post'
  })
}

export const logout = () => {
  return axios.request({
    url: '/logout',
    method: 'post'
  })
}

export const getUserList = (data) => {
  return axios.request({
    url: '/sys/user/list',
    data,
    method: 'post'
  })
}

export const getThisUser = () => {
  let data = {}
  return axios.request({
    url: 'sys/user/getThisUser',
    data,
    method: 'post'
  })
}

export const save = (data) => {
  return axios.request({
    url: 'sys/user/save',
    data,
    method: 'post'
  })
}

export const del = (id) => {
  return axios.request({
    url: 'sys/user/delete',
    data: { id: id },
    method: 'post'
  })
}

export const updatePassword = (data) => {
  return axios.request({
    url: 'sys/user/updatePassword',
    data,
    method: 'post'
  })
}
export const getRoleList = (id) => {
  return axios.request({
    url: 'sys/user/getRoleList',
    data: { userId: id },
    method: 'post'
  })
}
