package com.tdr.runner;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.core.io.watch.watchers.DelayWatcher;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.tdr.service.FirstStartSysService;
import com.tdr.util.FileSizeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * @author dj
 */

@Component
public class FileListenerRunner implements CommandLineRunner {

    private Log log = LogFactory.get();
    private boolean isFirst = true;

    @Value("${sys.localFilePath}")
    private String localFilePath;

    @Resource
    private FirstStartSysService firstStartSysService;

    @Override
    public void run(String... args) {
        // 创建监听者
        try {
            if (isFirst) {
                isFirst = firstStartSysService.go();
            }

            File file = FileUtil.file(localFilePath);
            //这里只监听文件或目录的修改事件
            WatchMonitor watchMonitor = WatchMonitor.createAll(file, new DelayWatcher(new Watcher() {
                @Override
                public void onCreate(WatchEvent<?> event, Path currentPath) {
                    Object obj = event.context();
                    log.info("创建：{}-> {}", currentPath, obj);
                }

                @Override
                public void onModify(WatchEvent<?> event, Path currentPath) {
                    Object obj = event.context();
                    this.go(currentPath, obj);
                }

                @Override
                public void onDelete(WatchEvent<?> event, Path currentPath) {
                    Object obj = event.context();
                    log.info("删除：{}-> {}", currentPath, obj);
                }

                @Override
                public void onOverflow(WatchEvent<?> event, Path currentPath) {
                    Object obj = event.context();
                }

                private void go(Path currentPath, Object object) {
                    String doLocalFilePath = currentPath.toString() + System.getProperty("file.separator") + object;
                    log.info("操作的文件：{}-> {}", currentPath, object);
                    File file = new File(doLocalFilePath);
                    String size = FileSizeUtil.GetFileSizeByMB(file);
                    System.out.println(size);
                }

            }, 500));

            //设置监听目录的最大深入，目录层级大于制定层级的变更将不被监听，默认只监听当前层级目录
//            watchMonitor.setMaxDepth(1);

            try {
                //启动监听
                watchMonitor.start();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("错误消息", e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
