export default {
  systemName: 'cylt',
  home: '首頁',
  login: '登錄',
  components: '组件',
  count_to_page: '数字渐变',
  tables_page: '多功能表格',
  split_pane_page: '分割窗口',
  markdown_page: 'Markdown編輯器',
  editor_page: '富文本編輯器',
  icons_page: '自定義圖標',
  img_cropper_page: '圖片編輯器',
  update: '上傳',
  join_page: 'QQ群',
  doc: '文檔',
  update_table_page: '上傳CSV文件',
  update_paste_page: '粘貼表格數據',
  multilevel: '多级菜单',
  directive_page: '指令',
  level_1: 'Level-1',
  level_2: 'Level-2',
  level_2_1: 'Level-2-1',
  level_2_3: 'Level-2-3',
  level_2_2: 'Level-2-2',
  level_2_2_1: 'Level-2-2-1',
  level_2_2_2: 'Level-2-2-2',
  excel: 'Excel',
  'upload-excel': '上傳excel',
  'export-excel': '導出excel',
  tools_methods_page: '工具函數',
  drag_list_page: '拖拽列表',
  i18n_page: '多語言',
  modalTitle: '模態框題目',
  content: '這是模態框內容',
  buttonText: '顯示模態框',
  'i18n-tip': '注：僅此頁做了多語言，其他頁面沒有在多語言包中添加語言內容',
  error_store_page: '錯誤收集',
  error_logger_page: '錯誤日誌',
  query: '帶參路由',
  params: '動態路由',
  cropper_page: '圖片裁剪',
  message_page: '消息中心',
  tree_table_page: '樹狀表格',
  org_tree_page: '組織結構樹',
  drag_drawer_page: '可拖動抽屜',
  tree_select_page: '樹狀下拉選擇器',

  // sys
  system: {
    info: '信息',
    save: '保存',
    copy: '复制',
    edit: '编辑',
    del: '删除',
    operation: '操作',
    updatePassword: '修改密码',
    logout: '退出登录',
    add: '添加',
    query: '查询',
    success: '操作成功',
    insufficientAuthority: '权限不足',
    networkError: '网络异常',
    loginOvertime: '登录超时',
    fail: '操作失败',
    tree: '树',
    warning: '警告',
    pleaseEnter: '请输入',
    remakes: '备注',
    validate: {
      notNull: '不能为空！',
      noRepeat: '不可重复！',
      error: '错误'
    },
    confirm: {
      del: '确定要删除此记录吗 ? '
    },
    user: {
      username: '登录名',
      name: '用户名',
      role: '角色',
      enterpriseId: '企业',
      updatePassword: '修改密码',
      originalPassword: '原密码',
      newPassword: '新密码',
      password: '密码'
    },
    role: {
      roleName: '角色名称',
      roles: {
        sysAdmin: '系统管理员',
        financeAdmin: '财务管理员',
        custAdmin: '客户管理员',
        orderAdmin: '订单管理员'
      }
    },
    log: {
      title: '日志标题',
      state: '日志状态',
      startDate: '处理时间',
      endDate: '结束时间',
      timeUse: '用时（秒）',
      errorText: '错误信息',
      serviceName: '服务名',
      declaredMethodName: '方法名',
      pojo: '实体',
      retry: '重试',
      delayRefresh: '正在处理，五秒后刷新界面'
    },
    // dict
    dict: {
      key: '字典键',
      value: '字典值',
      title: '字典标题',
      order: '字典顺序',
      remakes: '备注'
    },
    // notice
    notice: {
      code: '推送主键',
      title: '标题',
      content: '内容',
      callbackUrl: '回调地址',
      pushType: '推送类型',
      expiration: '过期时间(天)',
      push: '推送',
      data: '回调参数',
      icon: '推送图标',
      allRead: '全部已读'
    },
    job: {
      jobName: '任务名称',
      state: '任务状态',
      cron: 'cron表达式',
      beanName: '服务名称',
      methodName: '方法名称'
    }
  },
  neugart: {
    title: '标题',
    icon: '图标',
    value: '内容',
    valuationType: '计费类型',
    valuation: '计费值',
    custType: '用户类型',
    order: '顺序',
    defaultData: '默认选中',
    discount: {
      number: '折扣数量',
      discount: '折扣(%)'
    }
  },
  customer: {
    loginId: '登录名(手机号)',
    customerName: '客户名称',
    customerType: '客户类型',
    remakes: '备注'
  },
  // menu
  menu: {
    name: '菜单名称',
    icon: '菜单图标',
    baseUrl: '基础路径',
    component: '页面路径',
    showMenu: '是否显示',
    sys: {
      name: '系统',
      menu: '菜单',
      role: '角色',
      user: '用户',
      log: '日志',
      dict: '字典',
      notice: '通知',
      job: '任务'
    },
    customer: {
      name: '门户管理',
      product: '产品',
      user: '用户'
    }
  },
  // dict
  dict: {
    sys: {
      log: {
        error: '处理失败',
        pending: '等待处理',
        processing: '正在处理',
        successfully: '处理成功'
      },
      job: {
        start: '启动',
        suspend: '暂停'
      }
    },
    neugart: {
      customerType: {
        v1: '一级采购方',
        v2: '二级采购方',
        v3: '三级采购方',
        v4: '四级采购方',
        v5: '五级采购方',
        v6: '六级采购方'
      },
      valuationType: {
        noCharge: '不计费',
        RMB: '人民币',
        percentage: '百分比'
      }
    }
  }
}
