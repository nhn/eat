package com.nhnent.eat.common;

import com.nhnent.eat.plugin.protobuf.config.ProtoBufInfo;
import com.nhnent.eat.plugin.protobuf.GenerateJAR;
import com.nhnent.eat.plugin.protobuf.config.ProtobufConfig;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GenerateJARTest {
    @Test
    public void generateJAR() throws Exception {

        GenerateJAR.getInstance().initialize();
    }

    @Test
    public void getProtoList() throws Exception {

        List<String> protoList = GenerateJAR.getInstance().getProtoList(".\\proto");

    }

    @Test
    public void commandRun() throws Exception {

        String[] command = new String[] { "cmd.exe", "/C", ".\\proto\\protoc" +
                " --descriptor_set_out=.\\proto\\base.desc" +
                " --proto_path=.\\proto"
                + " .\\proto\\base.proto"};

        byRuntime(command);



    }

    public void byRuntime(String[] command)
            throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(command);
        printStream(process);
    }
    private void printStream(Process process)
            throws IOException, InterruptedException {
        process.waitFor();
        try (InputStream psout = process.getInputStream()) {
            copy(psout, System.out);
        }
    }

    public void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int n = 0;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
        }
    }

    @Test
    public void getProtoListFromConfig() throws Exception
    {
        ProtoBufInfo[] protoBufInfos = ProtobufConfig.obj().getProtobuf().getProtoBufInfos();

        List<String> protoList;

        HashMap<String, List<String>> protoInfos = new HashMap<>();

        for(int i = 0; i < protoBufInfos.length; i++)
        {
            protoList = new LinkedList<>();
            for(int j = 0; j < protoBufInfos[i].getProtoBufFiles().length; j++)
            {
                // If, there is dependency annotation ("-D")
                // ex) "MSuddaGameProto.proto-DMSuddaCommonProto.proto"
                if(protoBufInfos[i].getProtoBufFiles()[j].contains("-D"))
                {
                    String[] dependencyProtoString = protoBufInfos[i].getProtoBufFiles()[j].split("-D");

                    protoList.add(dependencyProtoString[0]);
                }
                else
                {
                    protoList.add(protoBufInfos[i].getProtoBufFiles()[j]);
                }
            }

            protoInfos.put(protoBufInfos[i].getKey(), protoList);
        }

    }

    @Test
    public void generateJARInitialize() throws ExecutionException, InterruptedException
    {
        GenerateJAR.getInstance().initialize();
    }

    @Test
    public void mkdirTest()
    {
        File dir;
        dir = new File(".\\proto\\TARDIS\\java");
        dir.mkdirs();
    }
}