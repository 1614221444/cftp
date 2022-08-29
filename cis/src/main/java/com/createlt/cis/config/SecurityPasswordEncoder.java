package com.createlt.cis.config;

import com.createlt.cis.common.DESUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 自定义的密码加密方法，实现了PasswordEncoder接口
 *
 */
@Component
public class SecurityPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence charSequence) {
        //加密密码
        return DESUtil.encrypt(charSequence.toString(),DESUtil.KEY);
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return encode(charSequence).equals(s);
    }
}
