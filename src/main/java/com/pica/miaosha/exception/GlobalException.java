package com.pica.miaosha.exception;

import com.pica.miaosha.result.CodeMsg;
import lombok.Data;

@Data
public class GlobalException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }
}
