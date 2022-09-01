package com.createlt.cis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.createlt.cis.entity.CisAuthentication;
import com.createlt.cis.mapper.CisAuthenticationMapper;
import com.createlt.cis.service.ICisAuthenticationService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务认证 服务实现类
 * </p>
 *
 * @author wuyh
 * @since 2022-09-01
 */
@Service
public class CisAuthenticationServiceImpl extends ServiceImpl<CisAuthenticationMapper, CisAuthentication> implements ICisAuthenticationService {

}
