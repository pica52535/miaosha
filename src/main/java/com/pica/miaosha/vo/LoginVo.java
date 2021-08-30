package com.pica.miaosha.vo;

import com.pica.miaosha.validator.isMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class LoginVo {

    @NotNull
    @isMobile
    private String mobile;
    @NotNull
    @Length(min=32)
    private String password;
}
