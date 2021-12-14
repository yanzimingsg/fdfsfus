package org.fdfsfus.pojo;

/**
 * @author YanZiMing
 * @DATE 2021/11/1  12:04 上午
 */
public class FileDownload {
    private String FileName;
    private byte[] FileByte;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public byte[] getFileByte() {
        return FileByte;
    }

    public void setFileByte(byte[] fileByte) {
        FileByte = fileByte;
    }
}
