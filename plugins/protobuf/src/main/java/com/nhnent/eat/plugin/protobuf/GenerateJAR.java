package com.nhnent.eat.plugin.protobuf;

import com.google.protobuf.Descriptors;
import com.nhnent.eat.common.Config.Config;
import com.nhnent.eat.plugin.protobuf.config.ProtoBufInfo;
import com.nhnent.eat.plugin.protobuf.config.ProtobufConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import static com.nhnent.eat.plugin.protobuf.ProtobufUtil.*;

public class GenerateJAR {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static GenerateJAR instance = null;

    private static List<String> configProtoList;

    private static String[] descListString;

    private static List<String> currentProtoList;

    private static String rootPath;
    private static String protoPath;

    public static GenerateJAR getInstance()
    {
        if(instance == null) {
            instance = new GenerateJAR();
        }

        return instance;
    }

    private GenerateJAR()
    {
        configProtoList = new LinkedList<>();
        currentProtoList = new LinkedList<>();
        rootPath = Config.obj().getCommon().getRootDirectory();
        protoPath = rootPath + "/proto";
    }

    /**
     * Generates java class files from google proto files.
     * @param protoInfos .proto file name list
     */
    private void generateJAR(HashMap<String, List<String>> protoInfos)
    {

        File dir;
        String[] command;

        if(isWindows())
        {
            try {
                String protoPathForWindows = protoPath.replace("/", "\\");

                String customApiJarFilePath = Paths.get(protoPathForWindows, "Compiled").toString();
                dir = new File(customApiJarFilePath);
                dir.mkdir();

                // Generate .java files and descriptor files
                for(Map.Entry<String, List<String>> protoInfo : protoInfos.entrySet()) {
                    List<String> protoList = protoInfo.getValue();
                    int protListSize = protoList.size();

                    for (String aProtoList : protoList) {

                        // Create desc files.
                        command = new String[]{"cmd.exe", "/C", protoPathForWindows + "\\windows\\protoc" +
                                " --descriptor_set_out=" + protoPathForWindows + "\\" + aProtoList.replace(".proto", ".desc") +
                                " --proto_path=" + protoPathForWindows
                                + " " + protoPathForWindows + "\\" + aProtoList};

                        byRuntime(command);
                        logger.info("Generates descriptor : {}", aProtoList.replace(".proto", ".desc"));

                        // Create java files
                        command = new String[]{"cmd.exe", "/C",
                                protoPathForWindows + "\\windows\\protoc" +
                                        " " + protoPathForWindows + "\\" + aProtoList +
                                        " --java_out=" + protoPathForWindows + "\\Compiled --proto_path=" + protoPathForWindows
                        };

                        byRuntime(command);
                        logger.info("Generates java file : {}", aProtoList.replace(".proto", ".java"));
                    }

                }

                // .java compile for generate .class file
                List<String> javaFileList = getJavaFileList(protoPathForWindows + "\\Compiled");

                StringBuilder javaFileListString = new StringBuilder("");

                for (String aJavaFileList : javaFileList) {
                    javaFileListString.append(aJavaFileList + " ");
                }

                logger.info("Compile java files to class files");

                String javac;
                if(ProtobufConfig.obj().getProtobuf().getJava8BinPath() == null)
                    javac = "javac";
                else
                    javac = Paths.get(ProtobufConfig.obj().getProtobuf().getJava8BinPath(),"javac").toString();

                command = new String[] {"cmd.exe", "/C",
                        javac + " -classpath \"" + protoPathForWindows + "\\protobuf-java-3.0.0.jar\" " +
                                javaFileListString + " -encoding UTF8 -Xlint:deprecation"
                };

                byRuntime(command);

                // Generates protocols.jar file
                logger.info("Generating protocols.jar file...");

                command = new String[] {"cmd.exe", "/C",
                        "jar cvf " + protoPathForWindows + "/protocols.jar" + " -C " +
                                protoPathForWindows + "/Compiled/ ."
                };

                byRuntime(command);

                try {
                    ProtobufDescPool.obj().loadProtobufDesc(GenerateJAR.getInstance().getDescListString());
                }
                catch (Descriptors.DescriptorValidationException e) {
                    logger.error("GenerateJAR cause error : {}", e);
                }

            }
            catch (IOException | InterruptedException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }

        }
        else if(isMac() || isUnix()) {
            try {
                String osType;
                if(isMac()) {
                    osType = "mac";
                } else {
                    osType = "linux";
                }
                String customApiJarFilePath = Paths.get(protoPath, "Compiled").toString();
                dir = new File(customApiJarFilePath);
                dir.mkdir();

                // Generate .java files and descriptor files
                for(Map.Entry<String, List<String>> protoInfo : protoInfos.entrySet()) {
                    List<String> protoList = protoInfo.getValue();

                    for (String aProtoList : protoList) {

                        // Create desc files.
                        command = new String[]{"/bin/bash", "-c", protoPath + "/" + osType + "/protoc" +
                                " --descriptor_set_out=" + protoPath + "/"
                                + aProtoList.replace(".proto", ".desc") +
                                " --proto_path=" + protoPath
                                + " " + protoPath + "/" + aProtoList};

                        byRuntime(command);
                        logger.info("Generates descriptor : {}", aProtoList.replace(".proto", ".desc"));

                        // Create java files
                        command = new String[]{"/bin/bash", "-c", protoPath + "/" + osType + "/protoc" +
                                " " + protoPath + "/" + aProtoList +
                                " --java_out=" + protoPath + "/Compiled --proto_path=" + protoPath
                        };

                        byRuntime(command);
                        logger.info("Generates java file : {}", aProtoList.replace(".proto", ".java"));
                    }

                }

                // .java compile for generate .class file
                List<String> javaFileList = getJavaFileList(protoPath + "/Compiled");

                StringBuilder javaFileListString = new StringBuilder("");

                for (String aJavaFileList : javaFileList) {
                    javaFileListString.append(aJavaFileList + " ");
                }

                logger.info("Compile java files to class files");

                String javac;
                if(ProtobufConfig.obj().getProtobuf().getJava8BinPath() == null)
                    javac = "javac";
                else
                    javac = Paths.get(ProtobufConfig.obj().getProtobuf().getJava8BinPath(),"javac").toString();

                String protoLibPath = Paths.get(ProtobufConfig.obj().getCommon().getRootDirectory(),
                        ProtobufConfig.obj().getProtobuf().getProtobufLibraryJarPath()).toString();
                command = new String[] {"/bin/bash", "-c",
                        javac + " -classpath \""
                                + protoLibPath + "\" " +
                                javaFileListString + "-encoding UTF8 -Xlint:deprecation"
                };

                byRuntime(command);

                // Generates protocols.jar file
                logger.info("Generating protocols.jar file...");

                command = new String[] {"/bin/bash", "-c",
                        "jar cvf " + protoPath + "/protocols.jar" + " -C " +
                                protoPath + "/Compiled/ ."
                };

                byRuntime(command);

                try {
                    ProtobufDescPool.obj().loadProtobufDesc(GenerateJAR.getInstance().getDescListString());
                }
                catch (Descriptors.DescriptorValidationException e) {
                    logger.error("GenerateJAR cause error : {}", e);
                }

            }
            catch (IOException | InterruptedException e) { logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
        else if(isSolaris()) {
            // TODO : need to implementation
        }
    }

    private String[] getDescListString()
    {
        return descListString;
    }

    /**
     * Find all proto files in subfolders
     * @return list of proto files
     */
    public List<String> getProtoList(String rootPath)
    {
        List<String> protoList = new LinkedList<>();

        File folder = new File(rootPath);

        File[] listOfFiles = folder.listFiles();

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                if (listOfFile.getName().endsWith(".proto")) {
                    protoList.add(rootPath + "/" + listOfFile.getName());
                }
            } else if (listOfFile.isDirectory()) {
                protoList.addAll(getProtoList(rootPath + "/" + listOfFile.getName()));
            }
        }

        return protoList;
    }

    /**
     * Find all java files in subfolders
     * @return list of proto files
     */
    private List<String> getJavaFileList(String rootPath)
    {
        List<String> javaFileList = new LinkedList<>();

        File folder = new File(rootPath);

        File[] listOfFiles = folder.listFiles();


        for (File listOfFile : listOfFiles) {

            if (listOfFile.isFile()) {

                if (listOfFile.getName().endsWith(".java")) {
                    javaFileList.add(rootPath + "/" + listOfFile.getName());
                }
            } else if (listOfFile.isDirectory()) {
                javaFileList.addAll(getJavaFileList(rootPath + "/" + listOfFile.getName()));
            }
        }

        return javaFileList;
    }



    public void initialize()
    {

        //Copy protobuf file from origin to managed
        copyProtoBufFileToManagedDir();

        ProtoBufInfo[] protoBufInfos = ProtobufConfig.obj().getProtobuf().getProtoBufInfos();

        List<String> protoEntireList = new LinkedList<>();

        HashMap<String, List<String>> protoInfos = new HashMap<>();

        List<String> protoList;

        for (ProtoBufInfo protoBufInfo : protoBufInfos) {
            protoList = new LinkedList<>();

            for (int j = 0; j < protoBufInfo.getProtoBufFiles().length; j++) {
                configProtoList.add(protoBufInfo.getProtoBufFiles()[j]);

                // If, there is dependency annotation ("-D")
                // ex) "MSuddaGameProto.proto-DMSuddaCommonProto.proto"
                if (protoBufInfo.getProtoBufFiles()[j].contains("-D")) {
                    String[] dependencyProtoString = protoBufInfo.getProtoBufFiles()[j].split("-D");

                    protoList.add(dependencyProtoString[0]);
                    protoEntireList.add(dependencyProtoString[0]);
                } else {
                    protoList.add(protoBufInfo.getProtoBufFiles()[j]);
                    protoEntireList.add(protoBufInfo.getProtoBufFiles()[j]);
                }
            }

            protoInfos.put(protoBufInfo.getKey(), protoList);
        }

        // Make descList String array
        List<String> descList = new LinkedList<>();

        for (String aConfigProtoList : configProtoList) {
            descList.add(aConfigProtoList.replaceAll(".proto", ".desc"));
        }

        descListString = descList.toArray(new String[0]);

        String protoFilesDirectory = Paths.get(rootPath, "proto").toString();
        // get current .proto files in folder
        File folder = new File(protoFilesDirectory);

        File[] listOfFiles = folder.listFiles();

        for (File listOfFile : listOfFiles) {

            if (listOfFile.isFile()) {

                if (listOfFile.getName().endsWith(".proto")) {
                    currentProtoList.add(listOfFile.getName());
                }
            }
        }

        // If the current proto files are difference from recent_proto files, do compile.
        if(isChangedProtoFiles(protoEntireList)) {
            // Make recent_proto folder for save previous Proto files
            saveRecentProtos();

            // Compile the proto files and generates JAR file
            generateJAR(protoInfos);
        }
        else {
            try {
                ProtobufDescPool.obj().loadProtobufDesc(getDescListString());
            }
            catch (IOException | Descriptors.DescriptorValidationException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    private void copyProtoBufFileToManagedDir() {
        ProtoBufInfo[] protoBufInfos = ProtobufConfig.obj().getProtobuf().getProtoBufInfos();
        String managedProtoPath = Paths.get(rootPath, "proto").toString();

        for(ProtoBufInfo protoBufInfo : protoBufInfos) {
            String originPath = protoBufInfo.getOriginProtoDirPath();

            for(String protoFile : protoBufInfo.getProtoBufFiles()) {

                protoFile = protoFile.split("-D")[0];
                String srcProtoFilePath = Paths.get(originPath, protoFile).toString();
                String dstProtoFilePath = Paths.get(managedProtoPath, protoFile).toString();

                File srcFile = new File(srcProtoFilePath);
                File destDir = new File(dstProtoFilePath);
                if(srcFile.equals(destDir)) {
                    continue;
                }
                try {
                    FileUtils.copyFile(srcFile, destDir);
                } catch (IOException e) {
                    logger.error("Failed to copy protobuf file to managed directory.\n{}",
                            ExceptionUtils.getStackTrace(e));
                }
            }
        }
    }

    private void byRuntime(String[] command)
            throws IOException, InterruptedException {

            p = Runtime.getRuntime().exec(command);
            new InputWriter(null).start();
            new OutputReader().start();
            new ErrorReader().start();
            p.waitFor();
    }



    private Semaphore outputSem;
    private Semaphore errorSem;
    private String error;
    private Process p;

    private class InputWriter extends Thread {
        private final String input;

        InputWriter(String input) {
            this.input = input;
        }

        public void run() {
            PrintWriter pw = new PrintWriter(p.getOutputStream());
            pw.println(input);
            pw.flush();
        }
    }

    private class OutputReader extends Thread {
        OutputReader()
        {
            try {
                outputSem = new Semaphore(1);
                outputSem.acquire();
            }
            catch (InterruptedException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }

        public void run() {
            try {
                StringBuffer readBuffer = new StringBuffer();
                BufferedReader isr = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String buff;

                while ((buff = isr.readLine()) != null) {
                    readBuffer.append(buff);
                }
                outputSem.release();

            }
            catch (IOException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    private class ErrorReader extends Thread {
        ErrorReader() {

            try {
                errorSem = new Semaphore(1);
                errorSem.acquire();
            }
            catch (InterruptedException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }

        public void run() {

            try {
                StringBuffer readBuffer = new StringBuffer();
                BufferedReader isr = new BufferedReader(new InputStreamReader(p
                        .getErrorStream()));
                String buff;

                while ((buff = isr.readLine()) != null) {
                    readBuffer.append(buff);
                }

                error = readBuffer.toString();
                errorSem.release();
            }
            catch (IOException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }

            if (error.length() > 0)
                logger.error(error);
        }
    }


    // File methods
    private void fileCopy(String inFileName, String outFileName)
    {
        try {
            FileInputStream fis = new FileInputStream(inFileName);
            FileOutputStream fos = new FileOutputStream(outFileName);

            int data;
            while((data=fis.read())!=-1) {
                fos.write(data);
            }

            fis.close();
            fos.close();

        }
        catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    // Check the recent proto files with current.
    private Boolean isChangedProtoFiles(List<String> protoList)
    {
        Boolean isChanged = Boolean.FALSE;

        try {

            for (String currentProtoFile : protoList) {
                File currentFile = new File(protoPath + "/" + currentProtoFile);
                File recentFile = new File(protoPath + "/recent_proto/" + currentProtoFile);

                if (!FileUtils.contentEquals(currentFile, recentFile)) {
                    isChanged = Boolean.TRUE;
                }
            }
        }
        catch (IOException e){
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        return isChanged;
    }

    private void saveRecentProtos()
    {
        String prevProtoFolderPath = Paths.get(protoPath, "recent_proto").toString();
        File dir = new File(prevProtoFolderPath);
        dir.mkdir();

        for (String protoFileName : currentProtoList) {
            fileCopy(protoPath + "/" + protoFileName,
                    protoPath + "/recent_proto/" + protoFileName);
        }
    }
}
