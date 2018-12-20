package com.tdr.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * @author dj
 */

@Service
public class FtpFileService {

    @Value("${ftp.user}")
    private String ftpUser;

    @Value("${ftp.password}")
    private String ftpPassword;

    @Value("${ftp.host}")
    private String ftpHost;

    @Value("${ftp.port}")
    private int ftpPort;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ftpClient;
    }

    /**
     * 上传文件（可供Action/Controller层使用）
     *
     * @param pathname    FTP服务器保存目录
     * @param fileName    上传到FTP服务器后的文件名称
     * @param inputStream 输入文件流
     * @return
     */
    public boolean uploadFile(String pathname, String fileName, InputStream inputStream) {
        boolean flag = false;
        FTPClient ftpClient = this.getFTPClient();
        try {
            //是否成功登录FTP服务器
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                return false;
            }
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.makeDirectory(pathname);
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            ftpClient.logout();
            flag = true;
        } catch (Exception e) {
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
        try {
            InputStream inputStream = new FileInputStream(new File(originFilePath));
            flag = uploadFile(ftpPath, fileName, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 上传文件（不可以进行文件的重命名操作）
     *
     * @param ftpPath        FTP服务器保存目录
     * @param originFilePath 待上传文件的名称（绝对地址）
     * @return
     */
    public boolean uploadFileFromProduction(String ftpPath, String originFilePath) {
        boolean flag = false;
        try {
            String fileName = new File(originFilePath).getName();
            InputStream inputStream = new FileInputStream(new File(originFilePath));
            flag = uploadFile(ftpPath, fileName, inputStream);
        } catch (Exception e) {
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

//    public static void main(String[] args) {
//        String hostname = "127.0.0.1";
//        int port = 21;
//        String username = "test";
//        String password = "123456";
//        String pathname = "business/ebook";
//        String filename = "big11.rar";
//        String originfilename = "H:\\ftpRead\\1.xlsx";
//        FtpFileService ftpService = new FtpFileService();
//        ftpService.uploadFileFromProduction(hostname, port, username, password, pathname, filename, originfilename);
//    }

}
