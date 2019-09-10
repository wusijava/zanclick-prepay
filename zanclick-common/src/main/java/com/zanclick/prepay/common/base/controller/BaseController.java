package com.zanclick.prepay.common.base.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.utils.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 初始
 *
 * @author duchong
 * @date 2019-06-11 9:41
 **/
public abstract class BaseController {

    /**
     * 字符集类型
     */
    protected static final String UTF_8 = "utf-8";

    /**
     * 字符集类型
     */
    private static final String GBK = "GBK";

    private static final String AUTHORIZATION = "authorization";

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取request
     *
     * @return
     */
    protected HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取response
     *
     * @return
     */
    protected HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }


    /**
     * 获取response
     *
     * @param params
     */
    protected void writeUtf8Response(Map<String, Object> params) throws IOException {
        HttpServletResponse httpResponse = getResponse();
        httpResponse.setContentType("text/html");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("text/html;charset=utf-8");
        httpResponse.getWriter().write(JSON.toJSONString(params));
    }

    /**
     * 获取session
     *
     * @return
     */
    protected HttpSession getSession() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
    }

    /**
     * 获取authorization
     *
     * @return
     */
    protected String getAuthorization() throws IOException {
        String authorization = getRequest().getHeader(AUTHORIZATION);
        getAuthorization(authorization);
        return authorization;
    }

    /**
     * 获取authorization
     *
     * @return
     */
    protected void getAuthorization(String authorization) throws IOException {
        if (authorization == null || "".equals(authorization) || "null".equals(authorization) || "undefined".equals(authorization)) {
            HttpServletResponse response = getResponse();
            response.setStatus(401);
            response.setHeader("WWW-authenticate", "Basic realm=\"请输入密码\"");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().print("对不起你没有权限！！");
            return;
        }
    }

    /**
     * 验证authorization
     *
     * @return
     */
    protected boolean verifyAuthorization(String authorization, String uPwd) throws IOException {
        String userAndPass = new String(new BASE64Decoder().decodeBuffer(authorization.split(" ")[1]));
        if (!userAndPass.equals(uPwd)) {
            HttpServletResponse response = getResponse();
            response.setStatus(401);
            response.setHeader("WWW-authenticate", "Basic realm=\"请输入密码\"");
            response.getWriter().print("对不起你没有权限！！");
            return false;
        }
        return true;
    }


    /**
     * 获取客户端的IP地址
     *
     * @return
     */
    public String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }

        }

        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    /**
     * 获取refererUrl
     */
    public String getRefererUrl(HttpServletRequest request) {
        return request.getHeader("referer");
    }

    /**
     * @param request 请求
     * @return 返回请求的数据流
     * @throws IOException
     */
    public String parseRequestString(HttpServletRequest request) throws IOException {
        String inputLine;
        String notityXml = "";
        while ((inputLine = request.getReader().readLine()) != null) {
            notityXml += inputLine;
        }
        request.getReader().close();
        return notityXml;
    }

    /**
     * 获取请求里的参数
     *
     * @param request
     * @return
     */
    public static Map<String, String> getAllRequestParam(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration request_BodyNames = request.getParameterNames();
        while (request_BodyNames.hasMoreElements()) {
            String name = (String) request_BodyNames.nextElement();
            String value = request.getParameter(name);
            map.put(name, value);
        }
        return map;
    }

    /**
     * 获取请求里的参数
     *
     * @param request
     * @return
     */
    public static Map<String, String> getParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }


    /**
     * 转换成JSON字符串
     *
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        return JSONObject.toJSONString(object);
    }

    /**
     * 空判断
     *
     * @param object
     * @return
     */
    public static Boolean isEmpty(Object object) {
        return DataUtil.isEmpty(object);
    }

    /**
     * 非空判断
     *
     * @param object
     * @return
     */
    public static Boolean isNotEmpty(Object object) {
        return DataUtil.isNotEmpty(object);
    }

    public static JSONObject getRequestJsonObject(HttpServletRequest request) {
        String json = getRequestJsonString(request);
        return JSONObject.parseObject(json);
    }

    /***
     * 获取 request 中 json 字符串的内容
     *
     * @param request
     * @return : <code>byte[]</code>
     * @throws IOException
     */
    public static String getRequestJsonString(HttpServletRequest request) {
        try {
            String submitMehtod = request.getMethod();
            // GET
            if (submitMehtod.equals("GET")) {
                return new String(request.getQueryString().getBytes("iso-8859-1"), "utf-8").replaceAll("%22", "\"");
                // POST
            } else {
                return getRequestPostStr(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {
            int readlen = request.getInputStream().read(buffer, i,
                    contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

    /**
     * 描述:获取 post 请求内容
     * <pre>
     * 举例
     * </pre>
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getRequestPostStr(HttpServletRequest request)
            throws IOException {
        byte buffer[] = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        return new String(buffer, charEncoding);
    }


    /**
     * 输出JSON数据
     *
     * @param response
     * @param jsonStr
     */
    public void writeJson(HttpServletResponse response, String jsonStr) {
        response.setContentType("text/json;charset=utf-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            pw.write(jsonStr);
            pw.flush();
        } catch (Exception e) {
            logger.info("输出JSON数据异常", e);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    /**
     * 向页面响应json字符数组串流.
     *
     * @param response
     * @param jsonStr
     * @return void
     * @throws IOException
     * @author 蒋勇
     * @date 2015-1-14 下午4:18:33
     */
    public void writeJsonStr(HttpServletResponse response, String jsonStr) throws IOException {
        OutputStream outStream = null;
        try {
            response.reset();
            response.setCharacterEncoding("UTF-8");
            outStream = response.getOutputStream();
            outStream.write(jsonStr.getBytes("UTF-8"));
            outStream.flush();
        } catch (IOException e) {
            logger.info("输出JSON数据异常(writeJsonStr)", e);
        } finally {
            if (outStream != null) {
                outStream.close();
            }
        }
    }

    public void writeJsonStr(HttpServletResponse response, InputStream in) throws IOException {
        if (null == in) {
            return;
        }
        OutputStream outStream = null;
        try {
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            outStream = response.getOutputStream();
            int len = 0;
            byte[] byt = new byte[1024];
            while ((len = in.read(byt)) != -1) {
                outStream.write(byt, 0, len);
            }
            outStream.flush();

        } catch (IOException e) {

            logger.info("输出JSON数据异常(writeJsonStr)", e);
        } finally {
            if (outStream != null) {
                outStream.close();
                in.close();
            }
        }
    }
}
