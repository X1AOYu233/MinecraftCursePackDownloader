import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 多线程下载模型
 *
 * @author bridge
 */
public class DownUtil {
    /**
     * 线程计数同步辅助
     */

    static int finishedThreadNum = 0;
    /**
     * 同时下载的线程数
     */
    private int threadCount;
    /**
     * 服务器请求路径
     */
    private String serverPath;
    /**
     * 本地路径
     */
    private String localPath;


    DownUtil(int threadCount, String serverPath, String downloadPath) throws IOException {
        this.threadCount = threadCount;
        this.serverPath = serverPath;
        this.localPath = downloadPath + getFileName(serverPath);

    }


    private String getFileName(String url) throws IOException {
        String filename = null;
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.connect();
        conn.getHeaderField(0);
        String file = conn.getURL().getFile();
        try {
            filename = file.substring(file.lastIndexOf('/') + 1);
            System.out.println(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file.substring(file.lastIndexOf('/') + 1).lastIndexOf(".") == -1 | filename == null) {
            Main.addErrorInfo("文件名获取失败，该文件可能已被删除,当前文件地址:" + conn.getURL().toString());
        }
        return filename;
    }

    void executeDownLoad() {

        try {
            URL url = new URL(serverPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int code = conn.getResponseCode();
            if (code == 200) {
                //服务器返回的数据的长度，实际上就是文件的长度,单位是字节
                int length = conn.getContentLength();
                //分割文件
                int blockSize = length / threadCount;
                new RandomAccessFile(localPath, "rwd").setLength(length);
                for (int threadId = 1; threadId <= threadCount; threadId++) {
                    //第一个线程下载的开始位置
                    int startIndex = (threadId - 1) * blockSize;
                    int endIndex = startIndex + blockSize - 1;
                    if (threadId == threadCount) {
                        //最后一个线程下载的长度稍微长一点
                        endIndex = length;
                    }
                    new DownLoadThread(startIndex, endIndex).start();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 内部类用于实现下载
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public class DownLoadThread extends Thread {
        /**
         * 下载起始位置
         */
        private int startIndex;
        /**
         * 下载结束位置
         */
        private int endIndex;
        private int hasRead;

        DownLoadThread(int startIndex, int endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;

        }


        @Override
        public void run() {

            try {
                URL url = new URL(serverPath);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //请求服务器下载部分的文件的指定位置
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                InputStream is = conn.getInputStream();//返回资源
                RandomAccessFile raf = new RandomAccessFile(localPath, "rwd");
                raf.seek(startIndex);
                byte[] buffer = new byte[endIndex - startIndex];
                is.skip(startIndex);
                while ((hasRead = is.read(buffer)) != -1) {
                    raf.write(buffer, 0, hasRead);
                }


//                ReadableByteChannel channel = Channels.newChannel(is);
//                FileChannel fileChannel = raf.getChannel();
//                fileChannel.position(startIndex);
//                fileChannel.transferFrom(channel,startIndex,endIndex-startIndex);
//                System.out.println("当前位置："+fileChannel.position());
                //随机写文件的时候从哪个位置开始写
                is.close();
                raf.close();
//                fileChannel.close();
                addfinishedThreadNum();
            } catch (IOException throwable) {
                throwable.printStackTrace();
                addfinishedThreadNum();
                if (throwable.getMessage().equalsIgnoreCase("connect timed out")) {
                    Main.addErrorInfo("连接超时,无法获取文件,当前下载地址：" + serverPath);
                }

            }

        }


    }

    private synchronized void addfinishedThreadNum() {
        finishedThreadNum++;
    }
}
