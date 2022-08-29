export default {
  systemName: 'cylt system',
  home: 'Home',
  login: 'Login',
  components: 'Components',
  count_to_page: 'Count-to',
  tables_page: 'Table',
  split_pane_page: 'Split-pane',
  markdown_page: 'Markdown-editor',
  editor_page: 'Rich-Text-Editor',
  icons_page: 'Custom-icon',
  img_cropper_page: 'Image-editor',
  update: 'Update',
  doc: 'Document',
  join_page: 'QQ Group',
  update_table_page: 'Update .CSV',
  update_paste_page: 'Paste Table Data',
  multilevel: 'multilevel',
  directive_page: 'Directive',
  level_1: 'Level-1',
  level_2: 'Level-2',
  level_2_1: 'Level-2-1',
  level_2_3: 'Level-2-3',
  level_2_2: 'Level-2-2',
  level_2_2_1: 'Level-2-2-1',
  level_2_2_2: 'Level-2-2-2',
  excel: 'Excel',
  'upload-excel': 'Upload Excel',
  'export-excel': 'Export Excel',
  tools_methods_page: 'Tools Methods',
  drag_list_page: 'Drag-list',
  i18n_page: 'Internationalization',
  modalTitle: 'Modal Title',
  content: 'This is the modal box content.',
  buttonText: 'Show Modal',
  'i18n-tip': 'Note: Only this page is multi-language, other pages do not add language content to the multi-language package.',
  error_store_page: 'Error Collection',
  error_logger_page: 'Error Logger',
  query: 'Query',
  params: 'Params',
  cropper_page: 'Cropper',
  message_page: 'Message Center',
  tree_table_page: 'Tree Table',
  org_tree_page: 'Org Tree',
  drag_drawer_page: 'Draggable Drawer',
  tree_select_page: 'Tree Selector',

  // sys
  system: {
    info: ' Info',
    save: 'Save',
    copy: 'Copy',
    edit: 'Edit',
    del: 'Delete',
    operation: 'Operation',
    add: 'Add',
    query: 'Query',
    success: 'Success',
    insufficientAuthority: 'Insufficient Authority',
    networkError: 'Network Error',
    loginOvertime: 'Login Overtime',
    fail: 'Fail',
    tree: ' Tree',
    warning: 'Warning',
    pleaseEnter: 'Please Enter ',
    remakes: 'remakes',
    validate: {
      notNull: ' Cannot be empty！',
      noRepeat: ' Non repeatable！',
      error: ' Error'
    },
    confirm: {
      del: 'Are you sure you want to delete this record ? '
    },
    user: {
      username: 'Login Name',
      name: 'Name',
      role: 'Role',
      enterpriseId: 'Enterprise',
      updatePassword: 'Update Password',
      originalPassword: 'Original password',
      newPassword: 'New password',
      password: 'Password'
    },
    role: {
      roleName: 'Role Name',
      roles: {
        sysAdmin: 'Sys Admin',
        financeAdmin: 'Finance Admin',
        custAdmin: 'Cust Admin',
        orderAdmin: 'Order Admin'
      }
    },
    log: {
      title: 'Log Title',
      state: 'Log State',
      startDate: 'Start Date',
      endDate: 'End Date',
      timeUse: 'TimeUse (s)',
      errorText: 'Error Text',
      serviceName: 'Service Name',
      declaredMethodName: 'Function Name',
      pojo: 'Parameter',
      retry: 'Retry',
      delayRefresh: 'Refresh the interface in five seconds'
    },
    // dict
    dict: {
      key: 'Dict Key',
      value: 'Dict Value',
      title: 'Dict Title',
      order: 'Dict Order',
      remakes: 'Remakes'
    },
    // notice
    notice: {
      code: 'Code',
      title: 'Title',
      content: 'Content',
      callbackUrl: 'Callback Url',
      pushType: 'Push Type',
      expiration: 'Expiration(Day)',
      push: 'Push',
      data: 'Data',
      icon: 'Icon',
      allRead: 'All Read'
    },
    job: {
      jobName: 'Job Name',
      state: 'Job State',
      cron: 'Cron Expression',
      beanName: 'Bean Name',
      methodName: 'Method Name'
    }
  },
  neugart: {
    title: 'Title',
    icon: 'Icon',
    value: 'Value',
    valuationType: 'Valuation Type',
    valuation: 'Valuation',
    custType: 'Customer Type',
    order: 'Order',
    defaultData: 'Default',
    discount: {
      number: 'Number',
      discount: 'Discount (%)'
    }
  },
  customer: {
    loginId: 'Login Id',
    customerName: 'Customer Name',
    customerType: 'Customer Type',
    remakes: 'Remakes'
  },
  // menu
  menu: {
    name: 'Menu Name',
    icon: 'Menu Icon',
    logout: 'Logout',
    baseUrl: 'Base Url',
    component: 'Page path',
    showMenu: 'Is Display',
    sys: {
      name: 'System',
      menu: 'Menu',
      role: 'Role',
      user: 'User',
      log: 'Log',
      dict: 'Dict',
      notice: 'Notice',
      job: 'Job'
    },
    customer: {
      name: 'Gateway',
      product: 'Product',
      user: 'User'
    }
  },
  // dict
  dict: {
    sys: {
      log: {
        error: 'Error',
        pending: 'Pending',
        processing: 'Processing',
        successfully: 'Successfully'
      },
      job: {
        start: 'Start',
        suspend: 'Suspend'
      }
    },
    neugart: {
      customerType: {
        v1: 'Level 1',
        v2: 'Level 2',
        v3: 'Level 3',
        v4: 'Level 4',
        v5: 'Level 5',
        v6: 'Level 6'
      },
      valuationType: {
        noCharge: 'No Charge',
        RMB: 'RMB',
        percentage: 'Percentage'
      }
    }
  }
}
