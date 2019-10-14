package com.zanclick.prepay.setmeal.util;

import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.setmeal.entity.SetMeal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetMealManager {

    private String appId;

    private String host;

    private int port = 22;

    private String username;

    private String password;

    private String remoteDir;

    private String localDir;

    public List<SetMeal> getNewestSetMeals() {
        String filepath = getNewestFile();
        if(filepath == null){
            return null;
        }
        List<SetMeal> setMeals = readFile(filepath);
        return setMeals;
    }

    private String getNewestFile() {
        SftpUtil sftp = null;
        try {
            new SftpUtil(host, port, username, password);
            String date = DateUtil.formatDate(new Date(), DateUtil.PATTERN_YYYYMMDD);
            String prefix = "outplanInfo_" + date;
            sftp.connect();
            List<String> downloadFiles = sftp.downloadFiles(remoteDir, localDir, prefix, ".txt", false);
            String filename = DataUtil.isEmpty(downloadFiles)?null:downloadFiles.get(0);
            return filename;
        } finally {
            if (sftp != null) {
                sftp.disconnect();
            }
        }
    }

    private List<SetMeal> readFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return Collections.emptyList();
        }
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String s = null;
            List<SetMeal> list = new ArrayList<>();
            while ((s = bufferedReader.readLine()) != null) {
                String[] arr = s.split("|");
                SetMeal setMeal = new SetMeal();
                setMeal.setPackageNo(arr[0]);
                setMeal.setTitle(arr[2]);
                setMeal.setAmount(arr[3]);
                setMeal.setNum(Integer.valueOf(arr[4]));
                setMeal.setTotalAmount(arr[5]);
                setMeal.setAppId(appId);
                list.add(setMeal);
            }
            return list;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Collections.emptyList();
    }

}
