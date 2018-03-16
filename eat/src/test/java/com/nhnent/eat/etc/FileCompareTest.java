package com.nhnent.eat.etc;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class FileCompareTest {


    @Test
    public void FileCompareTest() throws IOException {

        File file1 = new File("D:\\WORK\\04.EAT\\src\\eat\\scenario\\QIP_MSUDDA\\lobby\\common\\enter_lobby.scn");
        File file2 = new File("D:\\WORK\\04.EAT\\src\\eat\\scenario\\QIP_MSUDDA\\lobby\\mystorage\\avatar_request.scn");
        boolean isTwoEqual = FileUtils.contentEquals(file1, file2);
        System.out.println(isTwoEqual);
    }
}
