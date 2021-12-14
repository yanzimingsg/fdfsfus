package org.fdfsfus.controller.share;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author YanZiMing
 * @DATE 2021/10/21  3:12 下午
 */
public class BasicShare {
        /**
         * 日志
         */
        final static Logger logger = LoggerFactory.getLogger(BasicShare.class);
        //文件类型
        public static String IMG_TYPE_JPG = "jpg";
        public static String IMG_TYPE_JPEG = "jpeg";
        public static String IMG_TYPE_PNG = "png";
        public static String IMG_TYPE_DMG = "bmp";
        public static String IMG_TYPE_GIF = "gif";

        private  final static  String UPLOADING="Uploading:";
        private final  static  String LOCK=UPLOADING+"lock:";
        private  final  static String FILE=UPLOADING+"file:";

        //当前所有锁(用在不同用户的上传前或重传前对整个文件的锁)
        public  final static  String  currLocks=LOCK+"currLocks:";
        //当前锁的拥有者
        public  final static  String  lockOwner=LOCK+"lockOwner:";


        //当前文件传输到第几块
        public final  static  String CHUNK = FILE+"chunk:";

        //当前文件传输到的总快数
        public final  static  String CHUNKS = FILE+"chunks:";

        //当前文件上传到fastdfs路径
        public final static String FASTDFSPATH= FILE+"fastDfsPath:";

        //默认分组
        public final static  String DEFAULT_GROUP = "mike";

        //保存所有上传成功的文件MD5
        public final static String FILEMD5LIST=UPLOADING+"fileMd5List";

        //文件块锁(解决同一个用户正在上传时并发解决,比如后端正在上传一个大文件块,前端页面点击删除按钮,
        // 继续添加删除的文件,这时候要加锁来阻止其上传,否则会出现丢块问题,
        // 因为fastdfs上传不像迅雷下载一样,下载时会创建一个完整的文件,如果上传第一块时,服务器能快速创建一个大文件0填充,那么这样可以支持并发乱序来下载文件块,上传速度会成倍提升,要实现乱序下载文件块,估计就得研究fastdfs源码了)
        public  final static String CHUNKLOCK=LOCK+"chunkLock:";

        //用与缓存每次上传一块的文件大小
        public final static  String HISTORYUPLOAD="historyUpload:";

        //本地临时存储的文件路径
        public static final  String FACE_PATH = "/Users/yanziming/Documents/mike/fdfs";

        /**
         * 解析路径
         */
        private static final String SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR = "/";

        /**
         * group
         */
        private static final String SPLIT_GROUP_NAME = "mike";

        /**
         * 正则表达式匹配两个指定字符串中间的内容
         * @param soap
         * @return
         */
        public static List<String> getSubUtil(String soap,String rgex){
                List<String> list = new ArrayList<String>();
                Pattern pattern = Pattern.compile(rgex);// 匹配的模式
                Matcher m = pattern.matcher(soap);
                while (m.find()) {
                        int i = 1;
                        list.add(m.group(i));
                        i++;
                }
                return list;
        }

        /**
         * 返回单个字符串，若匹配到多个的话就返回第一个，方法与getSubUtil一样
         * @param soap
         * @param rgex
         * @return
         */
        public static String getSubUtilSimple(String soap,String rgex){
                Pattern pattern = Pattern.compile(rgex);// 匹配的模式
                Matcher m = pattern.matcher(soap);
                while(m.find()){
                        return m.group(1);
                }
                return "";
        }

        /**
         * 从文件中获取MD5
         * @param file
         * @return
         */
        public String getMd5(MultipartFile file) {
                try {
                        //获取文件的byte信息
                        byte[] uploadBytes = file.getBytes();
                        // 拿到一个MD5转换器
                        MessageDigest md5 = MessageDigest.getInstance("MD5");
                        byte[] digest = md5.digest(uploadBytes);
                        //转换为16进制
                        return new BigInteger(1, digest).toString(16);
                } catch (Exception e) {
                        logger.error(e.getMessage());
                }
                return null;
        }


        /**
         * 获取Group名称
         *
         * @param filePath
         * @return
         */
        public static String getGroupName(String filePath) {
                //先分隔开路径
                String[] paths = filePath.split(SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR);
                if (paths.length == 1) {
                        throw new FdfsUnsupportStorePathException("解析文件路径错误,有效的路径样式为(group/path) 而当前解析路径为".concat(filePath));
                }
                for (String item : paths) {
                        if (item.contains(SPLIT_GROUP_NAME)) {
                                return item;
                        }
                }
                throw new FdfsUnsupportStorePathException("解析文件路径错误,被解析路径url没有group,当前解析路径为".concat(filePath));
        }

        /**
         * 从Url当中解析存储路径对象
         *
         * @param filePath 有效的路径样式为(group/path) 或者
         *                 (http://ip/group/path),路径地址必须包含group
         * @return
         */
        public static StorePath parseFromUrl(String filePath) {
                Validate.notNull(filePath, "解析文件路径不能为空");

                String group = getGroupName(filePath);

                // 获取group起始位置
                int pathStartPos = filePath.indexOf(group) + group.length() + 1;
                String path = filePath.substring(pathStartPos, filePath.length());
                return new StorePath(group, path);
        }


        /**
         * 获取file名称
         *
         * @param filePath
         * @return
         */
        public static String getFileName(String filePath) {
                //先分隔开路径
                String[] paths = filePath.split(SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR);
                if (paths.length == 1) {
                        throw new FdfsUnsupportStorePathException("解析文件路径错误,有效的路径样式为(group/path) 而当前解析路径为".concat(filePath));
                }
                return paths[paths.length -1];
        }


}
