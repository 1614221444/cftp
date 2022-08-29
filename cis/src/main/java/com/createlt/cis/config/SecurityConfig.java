package com.createlt.cis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Configuration
@CrossOrigin
@EnableWebSecurity // 启用Spring Security的Web安全支持
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityPasswordEncoder securityPasswordEncoder;

    @Autowired
    private UserDetailsService sysUserService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .httpBasic()
                //未登录时，进行json格式的提示
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("text/html;charset=utf-8");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    PrintWriter out = response.getWriter();
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("code", 403);
                    map.put("message", "未登录");
                    out.write(objectMapper.writeValueAsString(map));
                    out.flush();
                    out.close();
                })

                .and()
                .authorizeRequests()
                .anyRequest().access("@rbacService.hasPermission(request,authentication)")    //必须经过认证以后才能访问
                .and().exceptionHandling().accessDeniedHandler((request, response, authentication) -> {
            accessDeniedHandler(response, authentication);
        })
                .and()
                .logout()
                //退出成功，返回json
                .logoutSuccessHandler((request, response, authentication) -> {
                    logoutSuccessHandler(response, authentication);
                })
                .permitAll();
        http.addFilterAt(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        //开启跨域访问
        http.cors();
        //开启模拟请求，比如API POST测试工具的测试，不开启时，API POST为报403错误
        http.csrf().disable();
    }


    /**
     * 自定义登录处理
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Transactional
    SysAuthenticationFilter customAuthenticationFilter() throws Exception {
        SysAuthenticationFilter filter = new SysAuthenticationFilter();
        filter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException {
                successHandler(resp, authentication);
            }
        });
        filter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException {
                failureHandler(resp, e);
            }
        });

        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }

    /**
     * 登录成功返回结果
     *
     * @param resp           响应对象
     * @param authentication 用户信息
     * @throws IOException
     */
    protected void logoutSuccessHandler(HttpServletResponse resp, Authentication authentication) throws IOException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", 200);
        map.put("message", "退出成功");
        map.put("data", authentication);
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.write(objectMapper.writeValueAsString(map));
        out.flush();
        out.close();
    }


    /**
     * 权限错误
     *
     * @param resp 响应对象
     * @param ex
     * @throws IOException
     */
    protected void accessDeniedHandler(HttpServletResponse resp, AccessDeniedException ex) throws IOException {
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        PrintWriter out = resp.getWriter();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", 403);
        map.put("message", "权限不足");
        out.write(objectMapper.writeValueAsString(map));
        out.flush();
        out.close();
    }

    /**
     * 登录成功
     *
     * @param resp           响应对象
     * @param authentication
     * @throws IOException
     */
    protected void successHandler(HttpServletResponse resp, Authentication authentication) throws IOException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", 200);
        map.put("message", "登录成功");
        map.put("data", authentication);
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.write(objectMapper.writeValueAsString(map));
        out.flush();
        out.close();
    }

    /**
     * 登录失败
     *
     * @param resp 响应对象
     * @param ex   响应结果
     * @throws IOException
     */
    protected void failureHandler(HttpServletResponse resp, AuthenticationException ex) throws IOException {
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter out = resp.getWriter();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", 402);
        if (ex instanceof UsernameNotFoundException || ex instanceof BadCredentialsException) {
            map.put("message", "用户名或密码错误");
        } else if (ex instanceof DisabledException) {
            map.put("message", "账户被禁用");
        } else if (ex instanceof InternalAuthenticationServiceException) {
            map.put("message", "老夫掐指一算 你应该是没启Redis服务");
        } else {
            map.put("message", "登录失败!");
        }
        out.write(objectMapper.writeValueAsString(map));
        out.flush();
        out.close();
    }

    @Override
    public void configure(WebSecurity web) {
        //对于在header里面增加token等类似情况，放行所有OPTIONS请求。
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
        // 放行swagger框架API
        web.ignoring().antMatchers(HttpMethod.GET, "/doc.html", "/webjars/**", "/swagger-resources/**", "/v2/api-docs");
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        //对默认的UserDetailsService进行覆盖
        authenticationProvider.setUserDetailsService(sysUserService);
        authenticationProvider.setPasswordEncoder(securityPasswordEncoder);
        return authenticationProvider;
    }

}
