import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class ZipHelper {
    /**
     * 解压文件
     * @param zipFile 目标文件
     * @param descDir 指定解压目录
     * @param urlList 存放解压后的文件目录（可选）
     */
    static void unZip(File zipFile, String descDir, List<String> urlList) {
        File pathFile = new File(descDir);
        if(!pathFile.exists()){
            //noinspection ResultOfMethodCallIgnored
            pathFile.mkdirs();
        }
        ZipFile zip;
        try {
            //指定编码，否则压缩包里面不能有中文目录
            zip = new ZipFile(zipFile, Charset.forName("gbk"));
            for(Enumeration entries = zip.entries(); entries.hasMoreElements();){
                ZipEntry entry = (ZipEntry)entries.nextElement();
                String zipEntryName = entry.getName();
                InputStream in = zip.getInputStream(entry);
                String outPath = (descDir+zipEntryName).replace("/", File.separator);
                //判断路径是否存在,不存在则创建文件路径
                File file = new File(outPath.substring(0, outPath.lastIndexOf(File.separator)));
                if(!file.exists()){
                    //noinspection ResultOfMethodCallIgnored
                    file.mkdirs();
                }
                //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if(new File(outPath).isDirectory()){
                    continue;
                }
                //保存文件路径信息
                urlList.add(outPath);

                OutputStream out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[2048];
                int len;
                while((len=in.read(buf1))>0){
                    out.write(buf1,0,len);
                }
                in.close();
                out.close();
            }
            //必须关闭，否则无法删除该zip文件
            zip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
