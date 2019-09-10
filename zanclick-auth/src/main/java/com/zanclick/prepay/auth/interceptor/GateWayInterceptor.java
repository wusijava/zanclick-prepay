package com.zanclick.prepay.auth.interceptor;

import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.utils.AESUtil;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * H5接口安全拦截
// *
// * @author duchong
// * @date 2019-9-10 11:31:50
// */
//
//public class GateWayInterceptor extends BaseController implements HandlerInterceptor {
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws IOException {
//        String content = getRequestPostStr(request);
//        String decrypt = AESUtil.Decrypt(content);
//        if (decrypt == null){
//            response.setCharacterEncoding("UTF-8");
//            response.setContentType("application/json;charset=utf-8");
//            response.getWriter().write("{\"code\":40015,\"msg\":\"加密信息错误\"}");
//            return false;
//        }
//        return true;
//    }
//}
