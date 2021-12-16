package org.fdfsfus.pojo;

/**
 * @author YanZiMing
 * @DATE 2022/1/17  下午7:56
 */
public class CheckFile {
    //文件名
    private String name;
    //文件类型
    private String type;
    //文件唯一Id
    private String onlyId;
    //文件MD5
    private String md5;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOnlyId() {
        return onlyId;
    }

    public void setOnlyId(String onlyId) {
        this.onlyId = onlyId;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
