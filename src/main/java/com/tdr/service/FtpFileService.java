package com.tdr.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.tdr.util.FileSizeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * @author dj
 */

@Service
public class FtpFileService {

    private static final Log log = LogFactory.get();

    @Value("${ftp.user}")
    private String ftpUser;

    @Value("${ftp.password}")
    private String ftpPassword;

    @Value("${ftp.host}")
    private String ftpHost;

    @Value("${ftp.port}")
    private int ftpPort;

    @Value("${sys.fileMaxSize}")
    private String fileMaxSize;

    @Value("${ftp.isActiveType}")
    private boolean isActiveType;

    /**
     * 获取ftp连接
     *
     * @return
     */
    private FTPClient getFTPClient() {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding("UTF-8");
        try {
            //连接FTP服务器
            ftpClient.connect(ftpHost, ftpPort);
            //登录FTP服务器
            ftpClient.login(ftpUser, ftpPassword);
            if (isActiveType) {
                //主动模式
                ftpClient.enterLocalActiveMode();
                ftpClient.setRemoteVerificationEnabled(false);
            } else {
                //被动模式
                ftpClient.enterLocalPassiveMode();
            }
        } catch (Exception e) {
            log.error("ftp连接失败:" + e);
            e.printStackTrace();
        }
        return ftpClient;
    }

    /**
     * 上传文件（可供Action/Controller层使用）
     *
     * @param ftpPath     FTP服务器保存目录
     * @param fileName    上传到FTP服务器后的文件名称
     * @param inputStream 输入文件流
     * @return
     */
    public boolean uploadFile(String ftpPath, String fileName, InputStream inputStream) {
        boolean flag = false;
        FTPClient ftpClient = this.getFTPClient();
        try {
            //是否成功登录FTP服务器
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                return false;
            }
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.makeDirectory(ftpPath);
            ftpClient.changeWorkingDirectory(ftpPath);
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            ftpClient.logout();
            flag = true;
            log.info("本地文件上传成功:" + fileName);
        } catch (Exception e) {
            log.error("本地文件上传失败:" + fileName + ";原因:" + e);
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * 上传文件（可对文件进行重命名）
     *
     * @param ftpPath        FTP服务器保存目录
     * @param fileName       上传到FTP服务器后的文件名称
     * @param originFilePath 待上传文件的名称（绝对地址）
     * @return
     */
    public boolean uploadFileFromProduction(String ftpPath, String fileName, String originFilePath) {
        boolean flag = false;
        if (!FileSizeUtil.notIsBigByMB(fileMaxSize, originFilePath)) {
            log.error("本地文件上传失败:" + originFilePath + ";原因:文件超过" + fileMaxSize + "MB");
            return false;
        }
        try {
            InputStream inputStream = new FileInputStream(new File(originFilePath));
            flag = uploadFile(ftpPath, fileName, inputStream);
        } catch (Exception e) {
            log.error("本地文件上传失败:" + originFilePath + ";原因:文件超过" + fileMaxSize + "MB");
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 上传文件（不可以进行文件的重命名操作）
     *
     * @param ftpFileDownloadPath FTP服务器保存目录
     * @param originFilePath      待上传文件的名称（绝对地址）
     * @return
     */
    public boolean uploadFileFromProduction(String ftpFileDownloadPath, String originFilePath) {
        boolean flag = false;
        if (FileSizeUtil.notIsBigByMB(fileMaxSize, originFilePath)) {
            log.error("本地文件上传失败:" + originFilePath + ";原因:文件超过" + fileMaxSize + "MB");
            return false;
        }
        try {
            String fileName = new File(originFilePath).getName();
            InputStream inputStream = new FileInputStream(new File(originFilePath));
            flag = uploadFile(ftpFileDownloadPath, fileName, inputStream);
        } catch (Exception e) {
            log.error("本地文件上传失败:" + originFilePath + ";原因:文件超过" + fileMaxSize + "MB");
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除文件
     *
     * @param ftpPath  FTP服务器保存目录
     * @param filename 要删除的文件名称
     * @return
     */
    public boolean deleteFile(String ftpPath, String filename) {
        boolean flag = false;
        FTPClient ftpClient = this.getFTPClient();
        try {
            //验证FTP服务器是否登录成功
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                return false;
            }
            //切换FTP目录
            ftpClient.changeWorkingDirectory(ftpPath);
            ftpClient.dele(filename);
            ftpClient.logout();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                } catch (IOException e) {

                }
            }
        }
        return flag;
    }


    /**
     * 从FTP下载文件到本地
     *
     * @param ftpFileUploadPath     FTP上的目标文件路径
     * @param localFileDownloadPath 下载到本地的文件路径
     */
    public void downloadFileFromFTP(String ftpFileUploadPath, String localFileDownloadPath) {
        InputStream is = null;
        FileOutputStream fos = null;
        OutputStream os;
        FileInputStream fis;
        try {
            FTPClient ftpClient = this.getFTPClient();
            // 获取ftp上的文件
            os = ftpClient.storeFileStream(ftpFileUploadPath);
            fis = new FileInputStream(new File(localFileDownloadPath));
            // 文件保存方式一
            int length;
            byte[] bytes = new byte[1024];
            while ((length = fis.read(bytes)) != -1) {
                os.write(bytes, 0, length);
            }
//            ftpClient.storeFile(localFileDownloadPath, new FileInputStream(new File(localFileDownloadPath)));
//            ftpClient.completePendingCommand();
            log.info("FTP文件上传成功:" + ftpFileUploadPath);
        } catch (Exception e) {
            log.error("FTP文件下载失败:" + ftpFileUploadPath + ";原因:" + e);
        } finally {
            try {
                if (null != fos) {
                    fos.close();
                }
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    public static void main(String[] args) {
//        String hostname = "127.0.0.1";
//        int port = 21;
//        String username = "test";
//        String password = "123456";
//        String pathname = "business/ebook";
//        String filename = "big11.rar";
//        String originfilename = "H:\\ftpRead\\1.xlsx";
//        ftpService.uploadFileFromProduction(hostname, port, username, password, pathname, filename, originfilename);
//    }

}
