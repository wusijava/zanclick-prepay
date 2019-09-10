package com.zanclick.prepay.common.utils.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.zanclick.prepay.common.utils.RedisUtil;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 二维码生成
 *
 * @author duchong
 * @date 2019-8-26 15:05:11
 */
public class QrUtil {

    /**
     * 保存二维码
     *
     * @param params
     */
    public static String createKey( List<QrModel> params) {
        String key = UUID.randomUUID().toString().replaceAll("-","");
        RedisUtil.set(key,params,1000*60*30L);
        return key;
    }

    /**
     * 获取二维码
     *
     * @param key
     */
    public static List<QrModel> getQrList(String key) {
        Object object = RedisUtil.get(key);
        return object == null ? null : (List<QrModel>) object;
    }

    /**
     * 批量下载二维码
     *
     * @param params
     * @param response
     */
    public static void downloadAllQr(HttpServletResponse response, List<QrModel> params)
            throws WriterException, IOException {
        String downloadFilename = URLEncoder.encode("二维码批量" + System.currentTimeMillis(), "UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFilename + ".zip");
        OutputStream ops = response.getOutputStream();
        ZipOutputStream zos = new ZipOutputStream(ops);
        for (QrModel qr : params) {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(qr.getUrl(), BarcodeFormat.QR_CODE, qr.getWidth(), qr.getHeight());
            BufferedImage buffImg = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ZipEntry entry = new ZipEntry(qr.getName() + "." + qr.getType());
            zos.putNextEntry(entry);
            ImageIO.write(buffImg, qr.getType(), zos);
        }
        zos.flush();
        zos.close();
        ops.flush();
        ops.close();
    }

    /**
     * 批量下载二维码
     *
     * @param params
     * @param response
     */
    public static void downloadAll(HttpServletResponse response, List<QrModel> params) {
        ZipOutputStream zos = null;
        try {
            String downloadFilename = URLEncoder.encode("二维码批量" + System.currentTimeMillis(), "UTF-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + downloadFilename + ".zip");
            OutputStream ops = response.getOutputStream();
            zos = new ZipOutputStream(ops);
            BitMatrix bitMatrix;
            for (QrModel qr : params) {
                zos.putNextEntry(new ZipEntry(qr.getName() + "." + qr.getType()));
                bitMatrix = new MultiFormatWriter().encode(qr.getUrl(), BarcodeFormat.QR_CODE, qr.getWidth(), qr.getHeight());
                MatrixToImageWriter.writeToStream(bitMatrix, qr.getType(), zos);
                zos.flush();
            }
            zos.close();
            ops.flush();
            ops.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        } finally {

        }
    }
}


