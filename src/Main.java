import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class Main extends JFrame {
    static Font defaultfont = new Font("Default", Font.PLAIN, 16);
    private static JTextField addressField = new JTextField();
    private JLabel addressTipLab = new JLabel("整合包位置(zip)");
    private JButton downloadButton = new JButton("下载");
    private JProgressBar progressBar = new JProgressBar();
    private static JFileChooser zipFileChooser= new JFileChooser();
    private String jarPath = Objects.requireNonNull(getClass().getProtectionDomain().getCodeSource().getLocation()).getPath().replace(getClass().getProtectionDomain().getCodeSource().getLocation().getFile(),"");
    private String downloadedPath;
    private JLabel progressLabel = new JLabel();
    private JButton fileChooseButton = new JButton("选择文件");
    private static String packName;
    private int modsCount;
    private int downloadedModsCount;
    private JLabel modloaderVer = new JLabel();
    private JLabel MCVer = new JLabel();
    private JButton aboutButton = new JButton("关于");
    private JLabel tip = new JLabel("将下载到同级目录");

    public static void main(String[] args) {
        new Main().run();

    }

    private void run() {
        try {
            new File("C:\\Users\\Administrator\\AppData\\Local\\Temp\\MinecraftCurseModpackDownloader.exe");
            new FileOutputStream("C:\\Users\\Administrator\\AppData\\Local\\Temp\\MinecraftCurseModpackDownloader.exe").write(getClass()
                    .getResourceAsStream("MinecraftCurseModpackDownloader.exe").readAllBytes());
            this.setVisible(true);
        }catch (IOException ignored){}

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | ClassNotFoundException var12) {
            var12.printStackTrace();
        }
        Container contentPane = this.getContentPane();
        this.setContentPane(contentPane);
        this.setLayout(null);
        this.setTitle("多线程Curse整合包下载器");
        downloadButton.addActionListener((e) -> new Thread(() -> {
            String packAddress = addressField.getText();
            downloadedPath = jarPath + new File(packAddress).getName().replace(".zip", "");
            try {
                if (packAddress.equals("")) {
                    new Dialog("选择的文件无效，请重新选择");
                    chooseZipFile();
                }else if (!packAddress.endsWith(".zip")) {
                    new Dialog("选择的文件无效，请重新选择");
                    chooseZipFile();
                }else zipFileChooser.getSelectedFile();
            }catch (NullPointerException ignored){
                try {
                    packName = downloadedPath.substring(downloadedPath.lastIndexOf("\\"));
                }catch (StringIndexOutOfBoundsException e1){
                    packName = downloadedPath;
                }
            }
            if (packAddress.equals("")) {
                new Dialog("选择的文件无效，请重新选择");
                chooseZipFile();
            }else downloadButton.setEnabled(false);
            JsonReader jsonReader = null;
            try {
                Runtime.getRuntime().exec("C:\\Users\\Administrator\\AppData\\Local\\Temp\\MinecraftCurseModpackDownloader.exe"
                        + " " + packAddress,null);
                Thread.sleep(1000);
            } catch (IOException | InterruptedException e1) {
                e1.printStackTrace();
            }
            try {
                jsonReader = new JsonReader(new FileReader(downloadedPath + "\\manifest.json"));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            Objects.requireNonNull(jsonReader).setLenient(true);
            JsonElement parseredJson =  new JsonParser().parse(jsonReader);
            JsonObject mcInfo = parseredJson.getAsJsonObject().get("minecraft").getAsJsonObject();
            MCVer.setText("MC版本:" +
                    mcInfo.
                    get("version").
                    getAsString());
            modloaderVer.setText("模组加载器版本:" +
                    mcInfo.
                    getAsJsonObject().getAsJsonArray("modLoaders").
                    get(0).getAsJsonObject().
                    get("id").
                    getAsString());
            for (Object ignored : parseredJson.getAsJsonObject().getAsJsonObject().get("files").getAsJsonArray()) {
                modsCount++;
            }
            System.out.println(modsCount);
            progressBar.setMaximum(modsCount);
            System.out.println(downloadedPath);
            System.out.println("C:\\Users\\Administrator\\AppData\\Local\\Temp\\MinecraftCurseModpackDownloader.exe"
                    + " " + packAddress);
            try {
                //noinspection InfiniteLoopStatement
                for (; ; ) {
                    downloadedModsCount = Objects.requireNonNull(new File(downloadedPath + "\\overrides\\mods").listFiles()).length;
                    System.out.println(downloadedModsCount);
                    progressBar.setValue(downloadedModsCount);
                    progressLabel.setText(downloadedModsCount + "/" + modsCount);
                    if (downloadedModsCount >= modsCount) {
                        modsCount = 0;
                        new Dialog("整合包" + packName + "已下载完毕");
                        break;
                    }
                    Thread.sleep(500);
                }
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            //noinspection InfiniteLoopStatement
        }).start());
        fileChooseButton.addActionListener((l)-> chooseZipFile());
        aboutButton.addActionListener((l)-> new AboutDialog());

        addressField.setFont(defaultfont);
        progressLabel.setFont(defaultfont);
        addressTipLab.setFont(defaultfont);
        fileChooseButton.setFont(defaultfont);
        MCVer.setFont(defaultfont);
        modloaderVer.setFont(defaultfont);
        aboutButton.setFont(defaultfont);
        tip.setFont(defaultfont);

        System.gc();

        this.setBounds(600, 200, 800, 600);
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                new File("C:\\Users\\Administrator\\AppData\\Local\\Temp\\MinecraftCurseModpackDownloader.exe").delete();
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        zipFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".zip")|f.isDirectory();
            }

            @Override
            public String getDescription() {
                return ".zip";
            }
        });


        fileChooseButton.setBounds(350,0,60,20);
        addressField.setBounds(150, 0, 200, 20);
        downloadButton.setBounds(410, 0, 60, 20);
        addressTipLab.setBounds(0, 0, 150, 20);
        progressBar.setBounds(100,30,400,20);
        progressLabel.setBounds(0,30,100,20);
        MCVer.setBounds(0,50,200,20);
        modloaderVer.setBounds(0,70,600,20);
        aboutButton.setBounds(690,480,100,80);
        tip.setBounds(480,0,150,20);
        contentPane.add(addressField);
        contentPane.add(downloadButton);
        contentPane.add(addressTipLab);
        contentPane.add(progressBar);
        contentPane.add(progressLabel);
        contentPane.add(fileChooseButton);
        contentPane.add(MCVer);
        contentPane.add(modloaderVer);
        contentPane.add(aboutButton);
        contentPane.add(tip);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    private static void chooseZipFile(){
        int returnVal =  zipFileChooser.showOpenDialog(new JDialog());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (!zipFileChooser.getSelectedFile().getName().endsWith(".zip")){
                new Dialog("选择的文件无效，请重新选择");
                chooseZipFile();
            }else {
                addressField.setText(zipFileChooser.getSelectedFile().getPath());
                packName = zipFileChooser.getSelectedFile().getName().replace(".zip", "");
            }
        }
    }
    @SuppressWarnings("unused")
    public static void downloadByNIO2(String url, String saveDir, String fileName) {
        try (InputStream ins = new URL(url).openStream()) {
            Path target = Paths.get(saveDir, fileName);
            Files.createDirectories(target.getParent());
            Files.copy(ins, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //TODO Auto download minecraft , forge and install forge
}

//G:/360D/Modern+Skyblock-2.9.0.1.zip