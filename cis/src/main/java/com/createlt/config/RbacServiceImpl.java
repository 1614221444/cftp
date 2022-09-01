package com.createlt.config;

import com.createlt.sys.entity.SysJurisdiction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * 返回权限验证的接口
 */
interface RbacService {
      boolean hasPermission(HttpServletRequest request, Authentication authentication);
}

@Component("rbacService")
public class RbacServiceImpl implements RbacService {
      @Override
      public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
            Object principal = authentication.getPrincipal();
            boolean hasPermission = false;
            if (principal instanceof UserDetails) { //首先判断先当前用户是否是我们UserDetails对象。
                  // 以下路径将跳过权限检测
                  String[] filterUrl = new String[] {"/sys/notice/news", "/sys/notice/delPush", "/sys/notice/readAll",
                          "/sys/notice/read", "/sys/dict/noPageList", "/sys/menu/list", "/api/"};
                  if (Arrays.asList(filterUrl).contains(request.getRequestURI())) {
                        return true;
                  }
                  List<SysJurisdiction> jurisdiction = (List<SysJurisdiction>) authentication.getAuthorities();
                  // 注意这里不能用equal来判断，因为有些URL是有参数的，所以要用AntPathMatcher来比较
                  for (SysJurisdiction url : jurisdiction) {
                        if(url.getMenu().getBaseUrl() != null && request.getRequestURI() != null){// 判断url不等于空 然后判断
                              if (request.getRequestURI().contains(url.getMenu().getBaseUrl())) {
                                    hasPermission = true;
                                    break;
                              }
                        }
                  }
            } else if("anonymousUser".equals(principal)) { // 匿名用户
                  // 以下路径将跳过权限检测
                  if (request.getRequestURI().contains("/api/")) {
                        return true;
                  }
            }
            return hasPermission;
      }
}
