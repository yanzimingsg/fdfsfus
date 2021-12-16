# 介绍
本项目是基于[FastDFS-Client](https://github.com/tobato/FastDFS_Client)的基础上开发的接口服务器，顾名思义就是以接口的方式来调用fastdfs-client来进行文件上传。

# 已实现的功能
1. 常规的文件/图片上传（可批量）
2. 分片非并发大文件/大图片上传（可批量）
3. 分片并发大文件/大图片上传（可批量）
4. 大文件秒传（对于已上传的大文件实现秒传）

# 运行环境
本项目使用了[Nacos](https://nacos.io/zh-cn/docs/quick-start.html)不需要的话可以自行去掉依赖。
本项目需要[redis](https://redis.io/)和FastDFS服务端正常才能运行，测试可以使用docker，FastDFS服务端安装使用可以[参考这里](https://github.com/tobato/FastDFS_Client/wiki/%E5%87%86%E5%A4%87Docker%E6%B5%8B%E8%AF%95%E9%95%9C%E5%83%8F)

# 使用方法
修改fdfsfus-program目录里面的BasicShare.java和application.yml文件
>BasicShare.java
```java  
//本地临时存储的文件路径
public static final  String FACE_PATH = "/Users/yanziming/Documents/mike/fdfs";
```
>application.yml
``` 
修改redis的服务端ip和FastDFS服务端ip
```

# 接口测试
普通的文件上传可以直接使用[postman](https://www.postman.com/)来测试，对于分片上传推荐使用百度的[WebUploader](http://fex.baidu.com/webuploader/),先关的前端资料可以参考项目里面的fdfsfusInterfaceTest文件夹。

# 后续更新
1. 批量大文件并发上传 （2022/1/17）（已实现）
2. 并发文件锁
3. 优化上传速度

# 项目暂时的问题
当有不同用户上传同一个文件时，就会有问题，因为项目是用文件的Md5作为唯一Id，所以校验时会有问题，解决也很简单用唯一的ID替代MD5就行，我这里就不改了，我后续解决这个问题。




