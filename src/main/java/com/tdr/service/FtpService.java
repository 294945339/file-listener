package com.tdr.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author: dj
 * @Date: 2018/12/21 15:02
 * @Description:
 */

@Service
public class FtpService {

    private static final Log log = LogFactory.get();

    @Value("${ftp.user}")
    private String ftpUser;

    @Value("${ftp.password}")
    private String ftpPassword;

    @Value("${ftp.host}")
    private String ftpHost;

    @Value("${ftp.port}")
    private int ftpPort;

    @Value("${ftp.isActiveType}")
    private boolean isActiveType;

    /**
     * 获取ftp连接
     *
     * @return
     */
    public FTPClient getFTPClient() {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding("UTF-8");
        //设置传输超时时间为60秒
        ftpClient.setDataTimeout(60000);
        //连接超时为60秒
        ftpClient.setConnectTimeout(60000);
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
     * 关闭链接
     *
     * @param ftpClient
     */
    public void close(FTPClient ftpClient) {
        try {
            ftpClient.logout();
        } catch (IOException e) {
            log.error("ftp关闭链接失败:" + e);
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    log.error("ftp关闭链接失败,强制关闭链接:" + e);
                    e.printStackTrace();
                }
            }
        }
    }

}
