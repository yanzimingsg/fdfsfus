package org.fdfsfus.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author YanZiMing
 * @DATE 2022/1/5  下午7:59
 */
public class FFMPegVideo {
    private String ffnpegEXE;

    public FFMPegVideo(String ffnpegEXE) {
        super();
        this.ffnpegEXE = ffnpegEXE;
    }

    //视频合成
    public  boolean VideoSynthesis(String videoInputPath, String videoOutputPath){
        List<String> commonList = new ArrayList<>();
        commonList.add(ffnpegEXE);
        commonList.add("-f");
        commonList.add("concat");
        commonList.add("-i");
        commonList.add(videoInputPath);
        commonList.add("-c");
        commonList.add("copy");
        commonList.add(videoOutputPath);
        ProcessBuilder builder = new ProcessBuilder(commonList);
        Process process;
        try {
            process = builder.start();
            InputStream errorStream = process.getErrorStream();
            InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
            BufferedReader bReader = new BufferedReader(inputStreamReader);
            bReader.close();
            inputStreamReader.close();
            errorStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
