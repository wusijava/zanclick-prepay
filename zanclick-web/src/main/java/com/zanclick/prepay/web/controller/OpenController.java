package com.zanclick.prepay.web.controller;

import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.ExcelDto;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.common.utils.PoiUtil;
import com.zanclick.prepay.common.utils.RedisUtil;
import com.zanclick.prepay.web.service.InItService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 内部接口
 *
 * @author duchong
 * @date 2019-9-6 10:43:11
 */
@Api(description = "开放接口")
@Slf4j
@RestController(value = "open_controller")
@RequestMapping(value = "/api/open/")
public class OpenController extends BaseController {

    @Autowired
    private InItService inItService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @ApiOperation(value = "通用excel下载接口")
    @GetMapping(value = "/downloadExcel/{key}")
    @ResponseBody
    public void downloadExcel(@PathVariable(value = "key") String key, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object object = RedisUtil.get(key);
        ExcelDto dto = object == null ? null : (ExcelDto) object;
        if (dto == null) {
            log.error("key已经过期:{}", key);
            return;
        }
        PoiUtil.batchExport(dto.getHeaders(), dto.getKeys(),dto.getObjectList(), request, response);
        RedisUtil.del(key);
    }

    @GetMapping(value = "getCityData")
    public void getCityData(String date,Integer type,String endtime, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorization = request.getHeader("authorization");
        if (!doBeforeBiz(response, authorization)){
            return;
        }
        Date d = null;
        DateFormat sdf = new SimpleDateFormat(DateUtil.PATTERN_YYYY_MM_DD);
        try {
            d = sdf.parse(date);
        } catch (Exception e) {
            d = new Date();
            date = sdf.format(d);
        }
        String extend = "";
        if(type != null && type == 1){
            extend = " and p.seller_no not in ('zdgdls@163.com','13802882372@139.com')";
        }
        Date lastd = DateUtil.getBeforeDay(d, 1);
        String lastdate = sdf.format(lastd);
        Date nextd = DateUtil.getBeforeDay(d, -1);
        String nextdate = sdf.format(nextd);
        if (endtime != null){
            nextdate = date + " "+ endtime;
        }
        String html = "<table  border=\"1\" cellspacing=\"0\" cellpadding=\"0\"><thead><tr><td>大区</td><td>城市</td><td>" + date + "数量</td><td>环比</td><td>" + lastdate + "数量</td></tr></thead><tbody>TBODY</tbody></table>";
        String sql = "SELECT a.groupname,a.cityname,ifnull(p.n,0) as n from area_group a LEFT JOIN (select city,sum(p.amount) as m," +
                "count(p.id) as n from pay_order p  where p.finish_time BETWEEN '" + date + "' and '" + nextdate + "' and p.state=1 " +
                extend +
                " group by p.city) " +
                " p on p.city=a.cityid " +
                " ORDER BY a.id ASC ";
        List<Map<String, Object>> goal = jdbcTemplate.queryForList(sql);
        String lastDateTime = endtime==null?date:(lastdate+" "+endtime);
        sql = "SELECT a.groupname,a.cityname,ifnull(p.n,0) as n from area_group a LEFT JOIN (select city,sum(p.amount) as m," +
                "count(p.id) as n from pay_order p  where p.finish_time BETWEEN '" + lastdate + "' and '" + lastDateTime + "' and p.state=1 " +
                extend +
                " group by p.city) " +
                " p on p.city=a.cityid " +
                " ORDER BY a.id ASC ";
        List<Map<String, Object>> last = jdbcTemplate.queryForList(sql);
        StringBuilder tbody = new StringBuilder();
        Long total1 = 0L;
        Long total2 = 0L;
        DecimalFormat df = new DecimalFormat("#.00");
        for (int i = 0; i < goal.size(); i++) {
            Map<String, Object> m1 = goal.get(i);
            Map<String, Object> m2 = last.get(i);
            String name = (String) m1.get("cityname");
            String groupname = (String) m1.get("groupname");
            Long num1 = (Long) m1.get("n");
            Long num2 = (Long) m2.get("n");
            double rate = 0.00;
            if (num2 == 0 && num1 != 0 ) {
                rate = 1.00;
            }else if(num2 != 0){
                rate = (num1-num2)*1.00/num2;
            }
            total1 += num1;
            total2 += num2;
            tbody.append("<tr><td>").append(groupname).append("</td><td>").append(name)
                    .append("</td><td>").append(num1).append("</td><td>").append(df.format(rate*100)).append("%</td><td>").append(num2)
                    .append("</td></tr>");

        }
        double rate = total2==0?total1==0?0:1.00:(total1-total2)*1.00/total2;
        tbody.append("<tr><td colspan=\"2\">合计")
                .append("</td><td>").append(total1).append("</td><td>").append(df.format(rate*100)).append("%</td><td>").append(total2)
                .append("</td></tr>");
        html = html.replace("TBODY",tbody.toString());
        response.setContentType("text/html; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.println(html);
        writer.close();
    }

    private boolean doBeforeBiz(HttpServletResponse response, String authorization) throws IOException {
        if (authorization == null || authorization.equals("")) {
            response.setStatus(401);
            response.setHeader("WWW-authenticate", "Basic realm=\"请输入密码\"");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().print("对不起你没有权限！！");
            return false;
        }
        String userAndPass = new String(new BASE64Decoder().decodeBuffer(authorization.split(" ")[1]));
        if (!"Zyjk:Zyjk@#2019".equals(userAndPass)) {
            response.setStatus(401);
            response.setHeader("WWW-authenticate", "Basic realm=\"请输入密码\"");
            response.getWriter().print("对不起你没有权限！！");
            return false;
        }
        return true;
    }

}
