package com.wikipeng.android.webimagespider;

import android.app.Activity;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.wikipeng.android.webimagespider.dao.WebSpider;

import junit.runner.Version;

import org.htmlparser.tags.ImageTag;

import java.io.File;
import java.util.List;

/**
 * Created by WikiPeng on 15/6/8 上午11:04.
 */
public class MainActivity extends Activity{
    //成功抓取http://qq.yh31.com/zjbq/0551964.html 网页 金馆长的表情，存储在Android手机 /storage/sdcard0/Download/表情/金馆长/
    public static final String URL_DOWNLOAD = "http://qq.yh31.com/zjbq/0551964.html";
    public static final String LOCAL_PATH = "/表情/金馆长/";
    public static final String URL_OTHER_DOWNLOAD = "http://qq.yh31.com/zjbq/0551964_%d.html";
    public static final String URL_DOMAIN = "http://qq.yh31.com";
    public static final int PAGE_COUNT = 15;
    private String downloadFolder;

    private Handler mHandler;

    private TextView mLog;
    WebSpider mWebSpider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        mHandler = new Handler();
        mWebSpider = new WebSpider();
        downloadFolder = Environment.DIRECTORY_DOWNLOADS+LOCAL_PATH;
        String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + LOCAL_PATH;
        Log.e("wiki", "downloadPath is ---> " + downloadPath);
        appendLog("当前下载路径为：" + downloadPath);
        File file = new File(downloadPath);

        if (!file.exists() || file.isFile()) {
            file.mkdirs();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("wiki", "sleep 5 seconds,the task will start");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mWebSpider.init(URL_DOWNLOAD);
                appendLog("开始解析网页：" + URL_DOWNLOAD);
                mWebSpider.parseHtml();

                for (int i = 1; i <= PAGE_COUNT; i++) {
                    mWebSpider.resetUrl(String.format(URL_OTHER_DOWNLOAD, i));
                    appendLog("开始解析网页：" + String.format(URL_OTHER_DOWNLOAD, i));
                    mWebSpider.parseHtml();
                }

                List<ImageTag> imageTagList = mWebSpider.getImageTagList();
                for (ImageTag imageTag : imageTagList) {
                    download(URL_DOMAIN + imageTag.getImageURL());
                    appendLog("开始下载：" + URL_DOMAIN + imageTag.getImageURL());
                }
            }
        }).start();
    }

    private void initView() {
        mLog = (TextView) findViewById(R.id.log);
        mLog.setMovementMethod(new ScrollingMovementMethod());
    }

    private void download(String url) {
        String fileName = url.substring(url.lastIndexOf(File.separator)+1);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalPublicDir(downloadFolder, fileName);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        }
        downloadManager.enqueue(request);
    }

    private void appendLog(final String text){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mLog.append(text);
                mLog.append("\n");
            }
        });
    }
}
