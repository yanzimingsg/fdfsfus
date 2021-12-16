package org.fdfsfus.pojo;

/**
 * @author YanZiMing
 * @DATE 2022/1/13  下午7:20
 */
public class Block {
    int chunk;
    int chunks;
    long start;
    long total;

    public int getChunk() {
        return chunk;
    }

    public void setChunk(int chunk) {
        this.chunk = chunk;
    }

    public int getChunks() {
        return chunks;
    }

    public void setChunks(int chunks) {
        this.chunks = chunks;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
