package com.zanclick.prepay.web.controller;

import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.ExcelDto;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.common.utils.PoiUtil;
import com.zanclick.prepay.common.utils.RedisUtil;
import com.zanclick.prepay.web.service.InItService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        PoiUtil.batchExport(dto.getHeaders(), dto.getKeys(), dto.getObjectList(), request, response);
        RedisUtil.del(key);
    }

    @GetMapping(value = "getCityData")
    public void getCityData(String date, Integer type, String endtime, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorization = request.getHeader("authorization");
        if (!doBeforeBiz(response, authorization)) {
            return;
        }
        Date d = null;
        DateFormat sdf = new SimpleDateFormat(DateUtil.PATTERN_YYYY_MM_DD);
        DateFormat sdfCN = new SimpleDateFormat("MM月dd日");
        try {
            d = sdf.parse(date);
        } catch (Exception e) {
            d = new Date();
            date = sdf.format(d);
        }
        String extend = "";
        if (type != null && type == 1) {
            extend = " and p.seller_no not in ('zdgdls@163.com','13802882372@139.com')";
        }
        Date lastd = DateUtil.getBeforeDay(d, 1);
        String lastdate = sdf.format(lastd);
        Date nextd = DateUtil.getBeforeDay(d, -1);
        String nextdate = sdf.format(nextd);
        if (endtime != null) {
            nextdate = date + " " + endtime;
        }
        String cndate = sdfCN.format(d) + (type != null && type == 1 ? "社会渠道" : "");
        String cnlastdate = sdfCN.format(lastd) + (type != null && type == 1 ? "社会渠道" : "");
        String html = "<style>body,td,th {font-family: Verdana, Arial, Helvetica, sans-serif;font-size: 12px;color: #1d1007; line-height:24px}td{text-align:center;}</style> " +
                "<div style=\"width: 50%;float: right;\">" +
                "<table  border=\"1\" cellspacing=\"0\" cellpadding=\"0\" text-align=\"center\"><thead><tr><th width=\"110\">运营中心</td><th width=\"70\">地市</th><th width=\"140\">" + cndate + "销量<br/>(截至" + endtime + ")</th><th width=\"80\">增长率</th><th width=\"140\">" + cnlastdate + "销量<br/>(截至" + endtime + ")</th></tr></thead><tbody>TBODY</tbody></table></div>";
        String sql = "SELECT a.id as id,a.groupid as groupid,a.groupname,a.cityname,ifnull(p.n,0) as n from area_group a LEFT JOIN (select city,sum(p.amount) as m," +
                "count(p.id) as n from pay_order p  where p.finish_time BETWEEN '" + date + "' and '" + nextdate + "' and p.state=1 " +
                extend +
                " group by p.city) " +
                " p on p.city=a.cityid " +
                " ORDER BY a.id ASC ";
        List<Map<String, Object>> goal = jdbcTemplate.queryForList(sql);
        String lastDateTime = endtime == null ? date : (lastdate + " " + endtime);
        sql = "SELECT a.id as id,a.groupid as groupid,a.groupname,a.cityname,ifnull(p.n,0) as n from area_group a LEFT JOIN (select city,sum(p.amount) as m," +
                "count(p.id) as n from pay_order p  where p.finish_time BETWEEN '" + lastdate + "' and '" + lastDateTime + "' and p.state=1 " +
                extend +
                " group by p.city) " +
                " p on p.city=a.cityid " +
                " ORDER BY a.id ASC ";
        List<Map<String, Object>> last = jdbcTemplate.queryForList(sql);
        StringBuilder tbody = new StringBuilder();
        Long total1 = 0L;
        Long total2 = 0L;
        DecimalFormat df = new DecimalFormat("0.00");
        List<CityData> cityDatas = new ArrayList<>(goal.size());
        for (int i = 0; i < goal.size(); i++) {
            Map<String, Object> m1 = goal.get(i);
            Map<String, Object> m2 = last.get(i);
            Integer groupid = (Integer) m1.get("groupid");
            Integer id = (Integer) m1.get("id");
            String name = (String) m1.get("cityname");
            String groupname = (String) m1.get("groupname");
            Long num1 = (Long) m1.get("n");
            Long num2 = (Long) m2.get("n");
            double rate = 0.00;
            if (num2 == 0 && num1 != 0) {
                rate = 1.00;
            } else if (num2 != 0) {
                rate = (num1 - num2) * 1.00 / num2;
            }
            total1 += num1;
            total2 += num2;
            String rateStr = rate == 0 ? "0.00" : df.format(rate * 100);
            cityDatas.add(new CityData(groupid, id, groupname, name, date, cndate, num1, lastdate, cnlastdate, num2, rateStr));

        }
        Map<Integer, List<CityData>> groupedCitys = cityDatas.stream().collect(Collectors.groupingBy(CityData::getGroupid));
        groupedCitys.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(entry -> {
            List<CityData> value = entry.getValue();
            int rowspan = value.size();
            value.sort((o1, o2) -> o1.getId() > o2.getId() ? 1 : -1);
            for (int i = 0; i < value.size(); i++) {
                CityData cityData = value.get(i);
                if (i == 0) {
                    tbody.append("<tr><td align=\"center\" rowspan=\"" + rowspan + "\">").append(cityData.getGroupName())
                            .append("</td><td align=\"center\">").append(cityData.getCityName())
                            .append("</td><td align=\"center\">").append(cityData.getNum())
                            .append("</td><td align=\"center\">").append(cityData.getRate())
                            .append("%</td><td align=\"center\">").append(cityData.getLastNum())
                            .append("</td></tr>");
                } else {
                    tbody.append("<tr><td align=\"center\">").append(cityData.getCityName())
                            .append("</td><td align=\"center\">").append(cityData.getNum())
                            .append("</td><td align=\"center\">").append(cityData.getRate())
                            .append("%</td><td align=\"center\">").append(cityData.getLastNum())
                            .append("</td></tr>");
                }
            }

        });
        double rate = total2 == 0 ? total1 == 0 ? 0 : 1.00 : (total1 - total2) * 1.00 / total2;
        tbody.append("<tr><td align=\"center\" colspan=\"2\">合计")
                .append("</td><td align=\"center\">").append(total1).append("</td><td align=\"center\">")
                .append(df.format(rate * 100)).append("%</td><td align=\"center\">").append(total2)
                .append("</td></tr>");
        html = html.replace("TBODY", tbody.toString());
        StringBuilder sb = new StringBuilder(html);
        sb.append("<div style=\"width: 50%;float: right;\">");
        for (CityData cityData : cityDatas) {
            sb.append("<table  border=\"1\" cellspacing=\"0\" cellpadding=\"0\" text-align=\"center\"><thead><tr><th width=\"70\">地市</th><th width=\"140\">" + cndate + "销量<br/>(截至" + endtime + ")</th><th width=\"80\">增长率</th><th width=\"140\">" + cnlastdate + "销量<br/>(截至" + endtime + ")</th></tr></thead><tbody>")
                    .append("<tr><td align=\"center\">").append(cityData.getCityName())
                    .append("</td><td align=\"center\">").append(cityData.getNum())
                    .append("</td><td align=\"center\">").append(cityData.getRate())
                    .append("%</td><td align=\"center\">").append(cityData.getLastNum())
                    .append("</td></tr>").append("</tbody></table><br/>");

        }
        sb.append("</div>");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.println(sb.toString());
        writer.close();
    }

    @Value("${excel.templateDir}")
    private String templateDir;


    @GetMapping(value = "getTotalData")
    public void getCityData(String date, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorization = request.getHeader("authorization");
        if (!doBeforeBiz(response, authorization)) {
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
        String dateStr = new SimpleDateFormat("MM.dd").format(d);
        String monthStr = new SimpleDateFormat("yyyy-MM").format(d);
        String sql = "SELECT a.id as id,a.groupid as groupid,a.groupname,a.cityname,ifnull(p.n,0) as n,ifnull(p.m,0) as m" +
                ",ifnull(c.m,0) as mm,ifnull(c.n,0) as mn,ifnull(d.m,0) as am,ifnull(d.n,0) as an from area_group a " +
                "LEFT JOIN (select city,sum(p.amount) as m," +
                "count(p.id) as n from pay_order p  where p.finish_time BETWEEN '" + date + " 00:00:00' and '" + date + " 23:59:59' and p.state=1 " +
                " group by p.city) " +
                " p on p.city=a.cityid " +
                "LEFT JOIN (select city,sum(p.amount) as m," +
                "count(p.id) as n from pay_order p  where p.finish_time BETWEEN '" + monthStr + "-01 00:00:00' and '" + date + " 23:59:59' and p.state=1 " +
                " group by p.city) " +
                " c on c.city=a.cityid " +
                "LEFT JOIN (select city,sum(p.amount) as m," +
                "count(p.id) as n from pay_order p  where p.finish_time <'" + date + " 23:59:59' and p.state=1 " +
                " group by p.city) " +
                " d on d.city=a.cityid " +
                " ORDER BY a.city_idx ASC ";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        File f = new File(templateDir + File.separator + "和商汇分资方销量日报.xlsx");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            Workbook wb = new XSSFWorkbook(fis);
            Sheet sheet = wb.getSheetAt(0);
            sheet.getRow(2).getCell(0).setCellValue(dateStr);
            Long t1 = 0L;
            Double t2 = 0.00;
            Long t3 = 0L;
            Double t4 = 0.00;
            Long t5 = 0L;
            Double t6 = 0.00;
            for (int i = 0; i < maps.size(); i++) {
                Row row = sheet.getRow(i + 2);
                int j = 3;
                Map<String, Object> map = maps.get(i);
                Long n =  (Long) map.getOrDefault("n", 0);
                Long mn =  (Long) map.getOrDefault("mn", 0);
                Long an =  (Long) map.getOrDefault("an", 0);
                Double m = (Double) map.getOrDefault("m", 0.00);
                Double mm = (Double) map.getOrDefault("mm", 0.00);
                Double am = (Double) map.getOrDefault("am", 0.00);
                t1 += n;
                t2 += m;
                t3 += mn;
                t4 += mm;
                t5 += an;
                t6 += am;
                row.getCell(j++).setCellValue((Long) map.getOrDefault("n", 0));
                row.getCell(j++).setCellValue((Double) map.getOrDefault("m", 0.00));
                row.getCell(j++).setCellValue((Long) map.getOrDefault("mn", 0));
                row.getCell(j++).setCellValue((Double) map.getOrDefault("mm", 0.00));
                row.getCell(j++).setCellValue((Long) map.getOrDefault("an", 0));
                row.getCell(j++).setCellValue((Double) map.getOrDefault("am", 0.00));
            }
            Row row = sheet.getRow(23);
            int j = 3;
            row.getCell(j++).setCellValue(t1);
            row.getCell(j++).setCellValue(t2);
            row.getCell(j++).setCellValue(t3);
            row.getCell(j++).setCellValue(t4);
            row.getCell(j++).setCellValue(t5);
            row.getCell(j++).setCellValue(t6);
            wb.setSheetName(0,dateStr);
            String file_name = "和商汇分资方销量日报" + new SimpleDateFormat("yyyyMMdd").format(d) + ".xlsx";
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(file_name.getBytes("GBK"), "ISO8859-1"));
            response.setCharacterEncoding("utf-8");
            OutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private class CityData {
        private Integer groupid;
        private Integer id;
        private String groupName;
        private String cityName;
        private String date;
        private String cnDate;
        private Long num;
        private String lastDate;
        private String lastCnDate;
        private Long lastNum;
        private String rate;
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
        if (!"heshanghui:heshanghui@#2019".equals(userAndPass)) {
            response.setStatus(401);
            response.setHeader("WWW-authenticate", "Basic realm=\"请输入密码\"");
            response.getWriter().print("对不起你没有权限！！");
            return false;
        }
        return true;
    }

}
