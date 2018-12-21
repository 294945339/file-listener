package com.tdr.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.tdr.util.FileSizeUtil;
import com.tdr.util.PathUtil;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import javax.annotation.Resource;

/**
 * @author dj
 */

@Service
public class FtpFileService {

    private static final Log log = LogFactory.get();

    @Value("${sys.fileMaxSize}")
    private String fileMaxSize;

    @Resource
    private FtpService ftpService;

    /**
     * 上传文件（可供Action/Controller层使用）
     *
     * @param ftpPath     FTP服务器保存目录
     * @param fileName    保存到FTP服务器后的文件名称
     * @param inputStream 输入文件流
     * @return
     */
    public boolean uploadFile(String ftpPath, String fileName, InputStream inputStream) {
        boolean flag = false;
        FTPClient ftpClient = ftpService.getFTPClient();
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
            flag = true;
            log.info("本地文件上传成功:" + fileName + ";保存到:" + ftpPath);
        } catch (Exception e) {
            log.error("本地文件上传失败:" + fileName + ";原因:" + e);
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ftpService.close(ftpClient);
        }
        return flag;
    }

    /**
     * 上传文件（可对文件进行重命名）
     *
     * @param ftpPath        FTP服务器保存目录
     * @param fileName       保存到FTP服务器后的文件名称
     * @param originFilePath 待上传文件的名称（绝对地址）
     * @return
     */
    public boolean uploadFileFromProduction(String ftpPath, String fileName, String originFilePath) {
        boolean flag = false;
        if (FileSizeUtil.notIsBigByMB(fileMaxSize, originFilePath)) {
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
    public boolean uploadFileFromProduction(String originFilePath, String ftpFileDownloadPath) {
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
    public boolean deleteFile(String ftpPath, String filename, FTPClient ftpClient) {
        boolean flag = false;
        try {
            //验证FTP服务器是否登录成功
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                return false;
            }
            //切换FTP目录
            ftpClient.changeWorkingDirectory(ftpPath);
            ftpClient.dele(filename);
            flag = true;
        } catch (Exception e) {
            log.error("删除ftp文件失败:" + ftpPath + ";原因:" + e);
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除文件夹
     *
     * @param ftpPath
     * @return
     */
    public boolean deleteFileDir(String ftpPath, FTPClient ftpClient) {
        ftpPath = PathUtil.pathTransformation(ftpPath);
        String basePath = System.getProperty("file.separator") + "upload";
        if (basePath.equals(ftpPath)) {
            return true;
        }
        boolean flag = false;
        try {
            //验证FTP服务器是否登录成功
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                return false;
            }
            ftpClient.removeDirectory(ftpPath);
            flag = true;
        } catch (Exception e) {
            log.error("删除ftp文件夹失败:" + ftpPath + ";原因:" + e);
            e.printStackTrace();
        }
        return flag;
    }

    /***
     * 下载文件
     * @param remoteFileName   待下载文件名称
     * @param localPath 下载到当地那个路径下
     * @param remoteDownLoadPath remoteFileName所在的路径
     * */
    public boolean downloadFile(String remoteFileName, String localPath, String remoteDownLoadPath, FTPClient ftpClient) {
        String strFilePath = localPath + System.getProperty("file.separator") + remoteFileName;
        BufferedOutputStream outStream = null;
        boolean success = false;
        try {
            ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            new File(localPath).mkdirs();
            outStream = new BufferedOutputStream(new FileOutputStream(strFilePath));
            success = ftpClient.retrieveFile(remoteFileName, outStream);
            log.info("FTP文件下载成功:" + remoteFileName + ";保存到:" + strFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("FTP文件下载失败:" + remoteFileName + ";原因:" + e);
        } finally {
            if (null != outStream) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    /***
     * 下载文件夹
     * @param localFileDownloadPath 本地地址
     * @param ftpFileUploadPath 远程文件夹
     * */
    public boolean downLoadDirectory(String localFileDownloadPath, String ftpFileUploadPath) {
        FTPClient ftpClient = ftpService.getFTPClient();
        try {
            FTPFile[] allFile = ftpClient.listFiles(ftpFileUploadPath);
            String filePath;
            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
                if (!allFile[currentFile].isDirectory()) {
                    String fileName = allFile[currentFile].getName();
                    if (FileSizeUtil.notIsBigByMB(fileMaxSize, allFile[currentFile].getSize())) {
                        log.error("ftp文件下载失败:" + ftpFileUploadPath + ";" + fileName +
                                ";原因:文件超过" + fileMaxSize + "MB");
                    } else {
                        filePath = PathUtil.getLocalUploadPath(ftpFileUploadPath, localFileDownloadPath);
                        if (downloadFile(fileName, filePath, ftpFileUploadPath, ftpClient)) {
                            deleteFile(ftpFileUploadPath, fileName, ftpClient);
                        }
                    }
                }
            }

            allFile = ftpClient.listFiles(ftpFileUploadPath);
            //如果文件夹下面的文件为空,则删除文件夹
            if (0 == allFile.length) {
                this.deleteFileDir(ftpFileUploadPath, ftpClient);
            }

            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
                if (allFile[currentFile].isDirectory()) {
                    filePath = PathUtil.getLocalUploadPath(allFile[currentFile].getName(), localFileDownloadPath);
                    String strremoteDirectoryPath = ftpFileUploadPath + "/" + allFile[currentFile].getName();
                    downLoadDirectory(filePath, strremoteDirectoryPath);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.error("FTP文件批量下载失败:" + ftpFileUploadPath + ";原因:" + e);
            return false;
        } finally {
            ftpService.close(ftpClient);
        }
        return true;
    }

}
