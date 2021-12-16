package org.fdfsfus.pojo;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author YanZiMing
 * @DATE 2021/11/8  11:45 下午
 */
public class FileUpload {
    //文件名
    private String name;
    //文件类型
    private String type;
    //文件大小
    private long size;
    //分片文件开始大小
    private long start;
    //分片文件最后大小
    private long end;

    //总分片数量
    private Integer chunks;
    //当前为第几块分片
    private Integer chunk;
    //文件唯一Id
    private String onlyId;
    //文件MD5
    private String md5;
    //分片对象
    private MultipartFile file;

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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public Integer getChunks() {
        return chunks;
    }

    public void setChunks(Integer chunks) {
        this.chunks = chunks;
    }

    public Integer getChunk() {
        return chunk;
    }

    public void setChunk(Integer chunk) {
        this.chunk = chunk;
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

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
