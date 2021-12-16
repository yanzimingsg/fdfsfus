package org.fdfsfus.pojo;

/**
 *
 * @author YanZiMing
 * @DATE 2022/1/11  下午9:44
 */
public class FileMerge {
    //文件名
    public String FileName;
    //文件唯一ID
    public String OnlyId;

    public Integer chunks;

    public String suffix;
    //文件类型
    private String type;
//    文件MD5
    public String md5;

    public Integer getChunks() {
        return chunks;
    }

    public void setChunks(Integer chunks) {
        this.chunks = chunks;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getOnlyId() {
        return OnlyId;
    }

    public void setOnlyId(String onlyId) {
        OnlyId = onlyId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
