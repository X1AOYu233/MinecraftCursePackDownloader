import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Objects;

public class Main extends JFrame {


    static Font defaultfont = new Font("Default", Font.PLAIN, 16);


    private static JTextArea errorLog = new JTextArea();
    private static JTextField addressField = new JTextField();
    private static JTextField threadCount = new JTextField();
    private static JFileChooser zipFileChooser = new JFileChooser();
    private static JDialog fileChooseDialog = new JDialog();
    private static String packName;
    private static JScrollPane errorLogScrollPane = new JScrollPane(errorLog);
    private static String packAddress = null;
    private JLabel addressTipLab = new JLabel("整合包位置(zip)");
    private JButton downloadButton = new JButton("下载");
    private JProgressBar progressBar = new JProgressBar();
    private String jarPath = Objects.requireNonNull(getClass().getProtectionDomain().getCodeSource().getLocation()).getPath().replace(getClass().getProtectionDomain().getCodeSource().getLocation().getFile(), "");
    private String downloadedPath;
    private JLabel progressLabel = new JLabel();
    private JButton fileChooseButton = new JButton("选择文件");
    private int modsCount;
    private JLabel modloaderVer = new JLabel();
    private JLabel MCVer = new JLabel();
    private JLabel tip = new JLabel("将下载到同级目录");
    private JLabel errorLogInfo = new JLabel("错误信息:");
    private JLabel threadsTip = new JLabel("一个文件的线程数:");
    private JLabel finishedThreadCountTip = new JLabel("已完成的线程数:");
    private JLabel finishedFileCountTip = new JLabel("已完成文件数:");
    private JLabel finishedThreadCountLable = new JLabel();
    private JLabel progressBarTip = new JLabel("总进度:");


    public static void main(String[] args) {
        new Main().run();

    }

    private static void chooseZipFile() {

        int returnVal = zipFileChooser.showOpenDialog(fileChooseDialog);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (!zipFileChooser.getSelectedFile().getName().endsWith(".zip")) {
                new Dialog("选择的文件无效，请重新选择");
            } else {
                addressField.setText(zipFileChooser.getSelectedFile().getPath());
                packName = zipFileChooser.getSelectedFile().getName().replace(".zip", "");
                System.out.println(packName);
            }
        }
    }

    static void addErrorInfo(String info) {
        errorLog.append(info + "\n");
        errorLogScrollPane.updateUI();
    }

    private static boolean checkChoosedFile() {
        if (packAddress == null) {
            chooseZipFile();
            return false;
        }
        if (packAddress.equals("")) {
            chooseZipFile();
            return false;
        }

        try {
            if (!packAddress.substring(packAddress.lastIndexOf(".")).equalsIgnoreCase(".zip")) {
                System.out.println(packAddress.substring(packAddress.lastIndexOf(".")));
                chooseZipFile();
                return false;
            }
        } catch (StringIndexOutOfBoundsException ignored) {
            chooseZipFile();
            return false;
        }
        return true;
    }

    private void run() {
        this.setVisible(true);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | ClassNotFoundException var12) {
            var12.printStackTrace();
        }
        Container contentPane = this.getContentPane();
        this.setContentPane(contentPane);
        this.setLayout(null);
        this.setTitle("多线程Curse整合包下载器");
        downloadButton.addActionListener((e) -> startDownload());
        /////////////////////////////////////////////////////////////////////////////
        fileChooseButton.addActionListener((l) -> chooseZipFile());

        threadCount.setFont(defaultfont);
        addressField.setFont(defaultfont);
        progressLabel.setFont(defaultfont);
        addressTipLab.setFont(defaultfont);
        fileChooseButton.setFont(defaultfont);
        MCVer.setFont(defaultfont);
        modloaderVer.setFont(defaultfont);
        tip.setFont(defaultfont);
        errorLog.setFont(defaultfont);
        errorLogInfo.setFont(defaultfont);
        threadsTip.setFont(defaultfont);
        finishedFileCountTip.setFont(defaultfont);
        finishedThreadCountTip.setFont(defaultfont);
        finishedThreadCountLable.setFont(defaultfont);
        progressBarTip.setFont(defaultfont);

        System.gc();

        errorLog.setEditable(false);
        errorLog.setAutoscrolls(true);
        errorLog.setLineWrap(true);
        errorLog.setDisabledTextColor(new Color(237, 21, 38));

        this.setBounds(600, 200, 800, 600);
        zipFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".zip") | f.isDirectory();
            }

            @Override
            public String getDescription() {
                return ".zip";
            }
        });
        zipFileChooser.removeChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return false;
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        zipFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        zipFileChooser.setFileSystemView(FileSystemView.getFileSystemView());
        zipFileChooser.setFont(defaultfont);
        zipFileChooser.setDialogTitle("选择整合包");
        zipFileChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());



        fileChooseButton.setBounds(310, 2, 60, 20);
        addressField.setBounds(110, 2, 200, 20);
        downloadButton.setBounds(370, 2, 60, 20);
        addressTipLab.setBounds(0, 0, 120, 20);
        progressBar.setBounds(250, 30, 400, 20);
        progressLabel.setBounds(100, 0, 100, 20);
        progressBarTip.setBounds(190,30,60,20);
        MCVer.setBounds(0, 50, 200, 20);
        modloaderVer.setBounds(0, 70, 600, 20);
        tip.setBounds(0, 52, 150, 20);
        errorLogInfo.setBounds(0, 90, 120, 20);
        errorLogScrollPane.setBounds(10, 120, 790, 400);
        threadsTip.setBounds(0,30,160,20);
        threadCount.setBounds(140,32,60,20);
        finishedFileCountTip.setBounds(0,0,100,20);
        finishedThreadCountTip.setBounds(0,20,140,20);
        finishedThreadCountLable.setBounds(120,20,200,20);

        contentPane.add(addressField);
        contentPane.add(downloadButton);
        contentPane.add(addressTipLab);
        contentPane.add(progressBar);
        contentPane.add(progressLabel);
        contentPane.add(fileChooseButton);
        contentPane.add(MCVer);
        contentPane.add(modloaderVer);
        contentPane.add(tip);
        contentPane.add(errorLogInfo);
        contentPane.add(errorLogScrollPane);
        contentPane.add(threadsTip);
        contentPane.add(threadCount);
        contentPane.add(finishedFileCountTip);
        contentPane.add(finishedThreadCountTip);
        contentPane.add(finishedThreadCountLable);
        contentPane.add(progressBarTip);

        progressBar.setVisible(false);
        progressLabel.setVisible(false);
        finishedFileCountTip.setVisible(false);
        finishedThreadCountTip.setVisible(false);
        finishedThreadCountLable.setVisible(false);
        progressBarTip.setVisible(false);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(800, 600);
    }

    @SuppressWarnings("deprecation")
    private void startDownload() {
        new Thread(() -> {
            int threadNumPreMod = 0;
            try {
                threadNumPreMod = Integer.parseInt(threadCount.getText());
            }catch (NumberFormatException e){
                new Dialog("线程数应为int型整数，不能包含非数字字符");
                Thread.currentThread().stop();
            }
            if (threadNumPreMod<=0){
                new Dialog("线程数不能小于或等于0");
                Thread.currentThread().stop();
            }
            ////////////////////Start Download Context///////////////////////////////////////////////////////////
            hindDownloadThings();
            ////////////////////Get Download Path/////////////////////////////////////////////////////////////////////////////////
            packAddress = addressField.getText();
            downloadedPath = jarPath + new File(packAddress).getName().replace(".zip", "");
            ////////////////////////////////////////////////////////////////////////////
            ////////////////////Get Modpack name/////////////////////
            if (!checkChoosedFile())//noinspection deprecation
                Thread.currentThread().stop();
            downloadButton.setEnabled(false);
            try {
                packName = packAddress.substring(packAddress.lastIndexOf("/"), packAddress.lastIndexOf(".zip"));
            } catch (StringIndexOutOfBoundsException e1) {
                System.out.println("Path Splitter is not /,should use \\");
                packName = packAddress.substring(packAddress.lastIndexOf("\\"), packAddress.lastIndexOf(".zip"));
            }
            //////////////////////////////////////////////////////////////////
            ////////////////////Get JSON file content and mods////////////////
            JsonReader jsonReader = null;
            ZipHelper.unZip(new File(packAddress), ".\\" + packName + "\\", new ArrayList<>());
            try {
                jsonReader = new JsonReader(new FileReader(downloadedPath + "\\manifest.json"));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            Objects.requireNonNull(jsonReader).setLenient(true);
            JsonElement parseredJson = new JsonParser().parse(jsonReader);
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
            /////////////////////////////////////////////////////////////////////////
            /////////////////////////Start to download mods//////////////////////////
            for (JsonElement modElement : parseredJson.getAsJsonObject().getAsJsonObject().get("files").getAsJsonArray()) {
                modsCount++;
                JsonObject mod = modElement.getAsJsonObject();
                int finalThreadNumPreMod = threadNumPreMod;
                new Thread(() -> {
                    try {
                        DownUtil d = new DownUtil(finalThreadNumPreMod, "https://minecraft.curseforge.com/projects/" + mod.get("projectID").getAsString() + "/files/" + mod.get("fileID").getAsString() + "/download", downloadedPath + "\\overrides\\mods\\");
                        d.executeDownLoad();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }).start();
            }
            /////////////////////////////////////////////////////////////////////////
            /////////////////////////Check for is finished///////////////////////////
            progressBar.setMaximum(modsCount);
            try {
                for (; ; ) {
                    System.out.println(DownUtil.finishedThreadNum);

                    progressBar.setValue(DownUtil.finishedThreadNum / threadNumPreMod);
                    progressLabel.setText(DownUtil.finishedThreadNum / threadNumPreMod + "/" + modsCount);
                    finishedThreadCountLable.setText(DownUtil.finishedThreadNum+"/"+modsCount*threadNumPreMod);
                    if (DownUtil.finishedThreadNum / threadNumPreMod >= modsCount) {
                        modsCount = 0;
                        new Dialog("整合包已下载完成");
                        System.gc();
                        break;
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }).start();
    }
    private void hindDownloadThings(){
        addressTipLab.setVisible(false);
        addressField.setVisible(false);
        downloadButton.setVisible(false);
        fileChooseButton.setVisible(false);
        tip.setVisible(false);
        threadCount.setVisible(false);
        threadsTip.setVisible(false);

        finishedFileCountTip.setVisible(true);
        finishedThreadCountTip.setVisible(true);
        finishedThreadCountLable.setVisible(true);
        progressLabel.setVisible(true);
        progressBar.setVisible(true);
        progressBarTip.setVisible(true);
    }
    //TODO Auto download minecraft,forge and install forge
}

//G:/360D/Modern+Skyblock-2.9.0.1.zip