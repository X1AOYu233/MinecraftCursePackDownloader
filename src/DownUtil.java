import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Objects;

public class DownUtil {
    //定义已完成的线程数
    static int finishedThreadNum;
    // 定义下载资源的路径
    private String path;
    // 定义需要使用多少线程下载资源
    private int threadNum;
    // 定义下载的线程对象
    private DownThread[] threads;
    private URL url;
    //定义下载到的目录
    private String targetPath;
    //定义文件名
    private String fileName;

    DownUtil(String path, int threadNum, String targetPath) {
        this.path = path;
        this.threadNum = threadNum;
        // 初始化threads数组
        threads = new DownThread[threadNum];
        this.targetPath = targetPath;
    }

    void download() throws Exception {
        url = new URL(path);
        // 指定所下载的文件的保存位置
        fileName = getFileName();
        String targetFile = targetPath + fileName;
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        setRequestProperty(conn);
        // 得到文件大小
        // 定义下载的文件的总大小
        long fileSize = conn.getContentLengthLong();
        System.out.println(conn.getContentLengthLong());
        conn.disconnect();
        long currentPartSize = fileSize / threadNum + 1;
        RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
        // 设置本地文件的大小
        try {
            file.setLength(fileSize);
        }catch (IOException e){
            System.out.println(e.getMessage() + ":" + fileSize);
            finishedThreadNum++;
            Main.addErrorInfo("文件指针错误："  + e.getMessage() + "指针位置：" + fileSize + "当前下载地址" + path+"\n");
        }
        file.close();
        for (int i = 0; i < threadNum; i++) {
            // 计算每条线程的下载的开始位置
            long startPos = i * currentPartSize;
            // 每个线程使用一个RandomAccessFile进行下载
            RandomAccessFile currentPart = new RandomAccessFile(targetFile, "rw");
            // 定位该线程的下载位置
            currentPart.seek(startPos);
            // 创建下载线程
            threads[i] = new DownThread(startPos, currentPartSize, currentPart);
            // 启动下载线程
            threads[i].start();
        }
    }

    // 获取下载的完成百分比
    public boolean isComplete() {
        return finishedThreadNum == threadNum;
    }

    private String getFileName() throws IOException {
        String filename = null;
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.connect();
        if (conn.getResponseCode() == 200) {
            String file = conn.getURL().getFile();
            System.out.println(file);
            try {
                filename = file.substring(file.lastIndexOf('/') + 1);
            }catch (Exception ignored){}
        }
        return filename;
    }

    private void setRequestProperty(HttpURLConnection conn) throws ProtocolException {
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept-Language", "en-US");
        conn.setRequestProperty("Charset", "UTF-8");
    }

    private class DownThread extends Thread {
        // 定义已经该线程已下载的字节数
        public int length;
        // 当前线程的下载位置
        private long startPos;
        // 定义当前线程负责下载的文件大小
        private long currentPartSize;
        // 当前线程需要下载的文件块
        private RandomAccessFile currentPart;

        DownThread(long startPos, long currentPartSize, RandomAccessFile currentPart) {
            this.startPos = startPos;
            this.currentPartSize = currentPartSize;
            this.currentPart = currentPart;
        }

        @Override
        public void run() {
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream inStream = null;
                try {
                    inStream = new BufferedInputStream(conn.getInputStream());
                } catch (IOException e) {
                    String msg = e.getMessage();
                    try {
                        System.out.println(msg);
                        conn = (HttpURLConnection) new URL(msg.substring(msg.indexOf("https"), msg.lastIndexOf(".jar")).replace(" ", "%20") + ".jar").openConnection();
                        fileName = msg.substring(msg.lastIndexOf("/"), msg.lastIndexOf(".jar")).replace(" ", "%20") + ".jar";
                        currentPart = new RandomAccessFile(targetPath + fileName, "rw");
                        inStream = new BufferedInputStream(conn.getInputStream());
                    } catch (StringIndexOutOfBoundsException e1) {
                        finishedThreadNum++;
                        if (msg.equalsIgnoreCase("Remote host closed connection during handshake")) {
                            Main.addErrorInfo("远程主机在TLS握手时关闭了连接:" + msg + "出错文件名：" + fileName + "\n");
                            this.stop();
                        }
                        if (msg.equalsIgnoreCase("Connection reset")){
                            Main.addErrorInfo("下载失败，连接已被重置,当前下载地址：" + path + "出错文件名：" + fileName + "\n");
                            this.stop();
                        }
                        if (msg.equalsIgnoreCase("Connection timed out: connect")){
                            Main.addErrorInfo("下载失败，连接超时，当前下载地址："+ path);
                            this.stop();
                        }
                        if (inStream == null) {
                            Main.addErrorInfo("无法下载文件，可能文件已不存在,当前下载地址：" + path + "出错模组名：" + msg.substring(msg.indexOf("projects/")+"projects/".length(), msg.lastIndexOf("/files")) + "\n");
                            this.stop();
                        }
                        if (fileName == null ){
                            System.err.println("Error in getting file name");
                            Main.addErrorInfo("无法获取文件名，下载地址：" + path + "\n");
                            this.stop();
                        }
                        //noinspection ConstantConditions

                    }
                }

                // 跳过startPos个字节，表明该线程只下载自己负责哪部分文件。
                try {
                    //noinspection ResultOfMethodCallIgnored
                    Objects.requireNonNull(inStream).skip(this.startPos);
                } catch (NullPointerException ignored) {
                }catch (IOException e){
                    finishedThreadNum++;
                    Main.addErrorInfo("移动文件指针出错："  + e.getMessage() + "指针位置：" + startPos + ",当前文件名：" + fileName + ",下载地址：" + path + "\n");
                    this.interrupt();
                }

                byte[] buffer = new byte[1024];
                int hasRead;
                // 读取网络数据，并写入本地文件
                //noinspection ConstantConditions
                while (length < currentPartSize && (hasRead = inStream.read(buffer)) != -1) {
                    currentPart.write(buffer, 0, hasRead);
                    // 累计该线程下载的总大小
                    length += hasRead;
                }
                currentPart.close();
                Objects.requireNonNull(inStream).close();
                finishedThreadNum++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}





