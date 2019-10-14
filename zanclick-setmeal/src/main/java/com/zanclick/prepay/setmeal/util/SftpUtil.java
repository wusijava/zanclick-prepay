package com.zanclick.prepay.setmeal.util;


import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class SftpUtil {
    private static Logger log = LoggerFactory.getLogger(SftpUtil.class);

    private String host;
    private String username;
    private String password;
    private int port = 22;
    private ChannelSftp sftp = null;
    private Session sshSession = null;

    public SftpUtil()
    {
    }

    public SftpUtil(String host, int port, String username, String password)
    {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public SftpUtil(String host, String username, String password)
    {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    /**
     *  * 通过SFTP连接服务器
     *
     */
    public boolean connect()
    {
        try
        {
            JSch jsch = new JSch();
            sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);

            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            if (log.isInfoEnabled())
            {
                log.info("jsch Session connected.");
            }

            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            if (log.isInfoEnabled())
            {
                log.info("jsch Opening sftp Channel.");
            }

            sftp = (ChannelSftp) channel;
            if (log.isInfoEnabled())
            {
                log.info("Connected to " + host + ".");
            }

            return true;
        }
        catch (Exception e)
        {
            log.error(" sftp 连接超时...{}", e.getMessage());
        }

        return false;
    }

    /**
     *  * 关闭连接
     *
     */
    public void disconnect()
    {
        if (this.sftp != null)
        {
            if (this.sftp.isConnected())
            {
                this.sftp.disconnect();
                if (log.isInfoEnabled())
                {
                    log.info(" sftp is closed");
                }
            }
        }
        if (this.sshSession != null)
        {
            if (this.sshSession.isConnected())
            {
                this.sshSession.disconnect();
                if (log.isInfoEnabled())
                {
                    log.info(" sshSession is closed");
                }
            }
        }
    }

    /**
     *  * 批量下载文件
     *  * @param remotPath：远程下载目录(以路径符号结束,可以为相对路径eg: /home/sftp/2014/)
     *  * @param localPath：本地保存目录(以路径符号结束,D:\downloac\sftp\)
     *  * @param prefix：文件名开头
     *  * @param suffix：文件名结尾
     *  * @param delRemote：下载后是否删除sftp文件
     *  * @return
     *
     */
    public List<String> downloadFiles(String remotePath, String localPath, String prefix, String suffix, boolean delRemote)
    {
        List<String> downfiles = new ArrayList<String>();
        try
        {
            Vector v = listFiles(remotePath);

            if (v.size() > 0)
            {
                prefix = prefix == null ? "" : prefix.trim();
                suffix = suffix == null ? "" : suffix.trim();

                boolean isDown;
                Iterator it = v.iterator();
                while (it.hasNext())
                {
                    ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) it.next();
                    String filename = entry.getFilename();
                    SftpATTRS attrs = entry.getAttrs();
                    if (!attrs.isDir())
                    {
                        File localFile = new File(localPath, filename);
                        // 验证开头和结尾
                        if ((prefix.equals("") || filename.startsWith(prefix)) && (suffix.equals("") || filename.endsWith(suffix)))
                        {
                            isDown = downloadFile(remotePath, filename, localFile);
                            if (isDown)
                            {
                                downfiles.add(localFile.getName());
                                if (delRemote) {
                                    deleteSFTP(remotePath, filename);
                                }
                            }
                        }
                    }
                }

                if (log.isInfoEnabled())
                {
                    log.info("file download success，file size : {}", downfiles.size());
                }
            }
        }
        catch (SftpException e)
        {
            e.printStackTrace();
        }
        finally

        {
            // this.disconnect();
        }
        return downfiles;
    }

    /**
     * 下载单个文件
     *
     * @param remotePath
     * @param remoteFileName
     * @param localFile
     * @return
     */
    public boolean downloadFile(String remotePath, String remoteFileName, File localFile)
    {
        //FileOutputStream fieloutput = null;
        try
        {
            // sftp.cd(remotePath);
            // mkdirs(localPath + localFileName);
            //fieloutput = new FileOutputStream(localFile);

            // sftp.get(remotePath + remoteFileName, fieloutput);
            sftp.get(remotePath + remoteFileName, localFile.getAbsolutePath());

            log.info("=== file.download: [{}] success.", remoteFileName);
            return true;
        }
        catch (Exception e)
        {
            if (e.getMessage().toLowerCase().equals("no such file"))
            {
                if (log.isDebugEnabled())
                {
                    log.debug("=== file.download.error: [{}], {}.", remoteFileName, e.getMessage());
                }
            }
            else {
                log.error("=== file.download.error: [{}], {}.", remoteFileName, e.getMessage());
            }

            localFile.delete();
        }
        finally
        {
            //            if (null != fieloutput)
            //            {
            //                try
            //                {
            //                    fieloutput.close();
            //                }
            //                catch (IOException e)
            //                {
            //                }
            //            }
        }
        return false;
    }

    /**
     *  * 上传单个文件
     *  * @param remotePath：远程保存目录
     *  * @param remoteFileName：保存文件名
     *  * @param localPath：本地上传目录(以路径符号结束)
     *  * @param localFileName：上传的文件名
     *  * @return
     *
     */
    public boolean uploadFile(String remotePath, String remoteFileName, String localFile)
    {
        FileInputStream in = null;
        try
        {
            cdDir(remotePath);
            in = new FileInputStream(localFile);
            sftp.put(in, remoteFileName);

            if (log.isInfoEnabled())
            {
                log.info(" file.upload: [{}] success.", localFile.substring(localFile.lastIndexOf(File.separator)));
            }

            return true;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (SftpException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     *  * 批量上传文件
     *  * @param remotePath：远程保存目录
     *  * @param localPath：本地上传目录(以路径符号结束)
     *  * @param delLocal：上传后是否删除本地文件
     *  * @return
     *
     */
    public List<String> uploadFiles(String remotePath, String localPath, String prefix, String suffix, boolean delLocal)
    {
        List<String> upfiles = new ArrayList<String>();
        try
        {
            File file = new File(localPath);
            File[] files = file.listFiles();

            prefix = prefix == null ? "" : prefix.trim();
            suffix = suffix == null ? "" : suffix.trim();

            for (int i = 0; i < files.length; i++)
            {
                String fileName = files[i].getName();
                if ((prefix.equals("") || fileName.startsWith(prefix)) && (suffix.equals("") || fileName.endsWith(suffix)))
                {
                    if (files[i].isFile())
                    {
                        boolean isUpload = this.uploadFile(remotePath, fileName, files[i].getAbsolutePath());
                        if (isUpload)
                        {
                            upfiles.add(files[i].getAbsolutePath());
                            if (delLocal) {
                                deleteLocal(files[i]);
                            }
                        }
                    }
                }
            }

            if (log.isInfoEnabled())
            {
                log.info(" file upload success，file size : {}", upfiles.size());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            this.disconnect();
        }

        return upfiles;

    }

    /**
     *  * 删除本地文件
     *  * @param filePath
     *  * @return
     *
     */
    public boolean deleteLocal(File file)
    {
        if (!file.exists())
        {
            return false;
        }

        if (!file.isFile())
        {
            return false;
        }

        boolean rs = file.delete();
        if (rs && log.isInfoEnabled())
        {
            log.info(" file.delete.success.");
        }
        return rs;
    }

    /**
     *  * 创建目录
     *  * @param createpath
     *  * @return
     *
     */
    public boolean cdDir(String createpath)
    {
        try
        {

            String pwd = this.sftp.pwd();
            if (pwd.contains("/" + createpath + "/")) {
                return true;
            }

            if (isDirExist(createpath))
            {
                this.sftp.cd(createpath);
                return true;
            }
            String pathArry[] = createpath.split("/");
            StringBuffer filePath = new StringBuffer("/");
            for (String path : pathArry)
            {
                if (path.equals(""))
                {
                    continue;
                }
                filePath.append(path + "/");
                if (isDirExist(filePath.toString()))
                {
                    sftp.cd(filePath.toString());
                }
                else
                {
                    // 建立目录
                    sftp.mkdir(filePath.toString());

                    // 进入并设置为当前目录
                    sftp.cd(filePath.toString());
                }

            }

            pwd = this.sftp.pwd();
            if (pwd.contains("/" + createpath + "/")){
                return true;
            }
        }
        catch (SftpException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *  * 判断目录是否存在
     *  * @param directory
     *  * @return
     *
     */
    public boolean isDirExist(String directory)
    {
        boolean isDirExistFlag = false;
        try
        {
            SftpATTRS sftpATTRS = sftp.lstat(directory);
            isDirExistFlag = true;
            return sftpATTRS.isDir();
        }
        catch (SftpException e)
        {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE)
            //if (e.getMessage().toLowerCase().equals("no such file"))
            {
                isDirExistFlag = false;
            }
        }
        return isDirExistFlag;
    }

    /**
     *  * 删除stfp文件
     *  * @param directory：要删除文件所在目录
     *  * @param deleteLocal：要删除的文件
     *  * @param sftp
     *
     */
    public void deleteSFTP(String directory, String deleteFile)
    {
        try
        {
            // sftp.cd(directory);
            sftp.rm(directory + deleteFile);
            if (log.isInfoEnabled())
            {
                log.info("delete file success from sftp.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *  * 如果目录不存在就创建目录
     *  * @param path
     *
     */
    public void mkdirs(String path)
    {
        File f = new File(path);

        String fs = f.getParent();

        f = new File(fs);

        if (!f.exists())
        {
            f.mkdirs();
        }
    }

    /**
     *  * 列出目录下的文件
     *  *
     *  * @param directory：要列出的目录
     *  * @param sftp
     *  * @return
     *  * @throws SftpException
     *
     */
    public Vector listFiles(String directory) throws SftpException
    {
        return sftp.ls(directory);
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public ChannelSftp getSftp()
    {
        return sftp;
    }

    public void setSftp(ChannelSftp sftp)
    {
        this.sftp = sftp;
    }

    /**
     * 测试
     */
    public static void main(String[] args)
    {


        //        SftpUtils sftp = null;
        //        // 本地存放地址
        //        String localPath = "D:\\dev\\java\\workspace\\testobj\\outfiles\\";
        //
        //        // Sftp下载路径
        //        String sftpPath = "/home/web/static/js/";
        //        List<String> filePathList = new ArrayList<String>();
        //        try
        //        {
        //            sftp = new SftpUtils("127.0.0.1", 22, "root", "xxxx");
        //            sftp.connect();
        //            // 下载
        //            // sftp.downloadFiles(sftpPath, localPath, "", ".js", false);
        //
        //            sftp.uploadFiles(sftpPath, localPath, "", "rar", true);
        //
        //        }
        //        catch (Exception e)
        //        {
        //            e.printStackTrace();
        //        }
        //        finally
        //        {
        //            sftp.disconnect();
        //        }
    }
}
