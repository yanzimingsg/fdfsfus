package org.fdfsfus.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.util.*;

@Service
public class MyFileUtils {


	/**
	 * 日志
	 */
	final static Logger logger = LoggerFactory.getLogger(MyFileUtils.class);
	// 缓存文件头信息-文件头信息
	public static final HashMap<String, String> mFileTypes = new HashMap<String, String>();
	static {
		// images
		mFileTypes.put("FFD8FF", "jpg");
		mFileTypes.put("89504E47", "png");
		mFileTypes.put("47494638", "gif");
		mFileTypes.put("424D", "bmp");
	}



	/**
	 * 根据url拿取file
	 * 
	 * @param url
	 * @param suffix
	 *            文件后缀名
	 */
	public static File createFileByUrl(String url, String suffix) {
		byte[] byteFile = getImageFromNetByUrl(url);
		if (byteFile != null) {
			File file = getFileFromBytes(byteFile, suffix);
			return file;
		} else {
			return null;
		}
	}

	/**
	 * 根据地址获得数据的字节流
	 * 
	 * @param strUrl
	 *            网络连接地址
	 * @return
	 */
	private static byte[] getImageFromNetByUrl(String strUrl) {
		try {
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);
			InputStream inStream = conn.getInputStream();// 通过输入流获取图片数据
			byte[] btImg = readInputStream(inStream);// 得到图片的二进制数据
			return btImg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从输入流中获取数据
	 * 
	 * @param inStream
	 *            输入流
	 * @return
	 * @throws Exception
	 */
	private static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}

	public static void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myDelFile = new File(filePath);
			myDelFile.delete();
		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * // 创建临时文件
	 * @param b
	 * @param suffix
	 * @return
	 */

	private static File getFileFromBytes(byte[] b, String suffix) {
		BufferedOutputStream stream = null;
		File file = null;
		try {
			file = File.createTempFile("pattern", "." + suffix);
			System.out.println("临时文件位置：" + file.getCanonicalPath());
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return file;
	}

	/**
	 * @param file     文件块
	 * @param filePath 文件上传到的目的路径
	 * @param fileSize 文件大小
	 * @param chunk 当前第几块
	 * @return
	 */
	public static String fileUpload(MultipartFile file, String filePath, Long fileSize,  String fileId, int chunk) throws IOException {
		String temporaryFilePath = filePath + File.separatorChar + fileId;
		File temporaryFolder = new File(temporaryFilePath);
		String fileSizeStr = String.valueOf(fileSize);

		if (!temporaryFolder.exists()) {
			// 临时目录用来存放所有分片文件
			temporaryFolder.mkdirs();
		}

//		copyFile(file.getInputStream(), temporaryFilePath, fileName + "_" + chunk);
		copyFile(file.getInputStream(), temporaryFilePath,   chunk+"_"+fileSizeStr);
		return temporaryFilePath;
	}


	/**
	 * @param inputStream 文件流
	 * @param dir         目的文件夹
	 * @param realName    文件名
	 * @return
	 * @throws IOException
	 */
	private static File copyFile(InputStream inputStream, String dir, String realName) throws IOException {
		//生成临时文件
		File destFile = new File(dir, realName);
		return copyFile(inputStream, destFile);
	}


	/**
	 * 文件拷贝
	 * @param inputStream
	 * @param destFile
	 * @return
	 * @throws IOException
	 */
	private static File copyFile(InputStream inputStream, File destFile) throws IOException {
		if (null == inputStream) {
			return null;
		}
		if (!destFile.exists()) {
			if (!destFile.getParentFile().exists()) {
				destFile.getParentFile().mkdir();
			}
			destFile.createNewFile();
			FileUtils.copyInputStreamToFile(inputStream, destFile);
		}

		return destFile;
	}

	/**
	 * 分片文件合并
	 * @param fileName
	 * @param fileId
	 * @param path
	 * @return
	 */
	public static String fileMerge(String fileName,String fileId,String path){
		FileChannel outChannel = null;
		//分片文件所在的目录
		String fileTablePath = path + File.separatorChar + fileId;
		//合并后的文件所在的目录
		String mergeFilePath = path + File.separatorChar+ "merge" + File.separatorChar +fileId + File.separatorChar;
		//合并后的文件位置
		String FilePath = path + File.separatorChar+ "merge" + File.separatorChar + fileId + File.separatorChar + fileName;

		try {
			// 读取目录里的所有文件
			File dir = new File(fileTablePath);
			File[] childs = dir.listFiles();
			if(Objects.isNull(childs)|| childs.length==0){
				return null;
			}
			// 转成集合，便于排序
			List<File> fileList = fileList(fileTablePath);
			// 创建空白文件
			File outputFile = new File(FilePath);
			// 创建文件
			if(!outputFile.exists()){
				File mergeMd5Dir = new File(mergeFilePath);
				if(!mergeMd5Dir.exists()){
					mergeMd5Dir.mkdirs();
				}
				logger.info("创建文件");
				outputFile.createNewFile();
			}
			outChannel = new FileOutputStream(outputFile).getChannel();
			FileChannel inChannel = null;
			try {
				for (File file : fileList) {
					inChannel = new FileInputStream(file).getChannel();
					inChannel.transferTo(0, inChannel.size(), outChannel);
					inChannel.close();
					// 删除分片
					file.delete();
				}
			}catch (Exception e){
				e.printStackTrace();
				//发生异常，文件合并失败 ，删除创建的文件
				outputFile.delete();
				dir.delete();//删除文件夹
			}finally {
				if(inChannel!=null){
					inChannel.close();
				}
			}
			dir.delete(); //删除分片所在的文件夹
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(outChannel!=null){
					outChannel.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return FilePath;
	}


//	/**
//	 * 字符串+1方法，该方法将其结尾的整数+1,适用于任何以整数结尾的字符串,不限格式，不限分隔符。
//	 * @param testStr 要+1的字符串
//	 * @return +1后的字符串
//	 * @exception NumberFormatException
//	 */
//	public static String addOne(String testStr) {
//		String[] strs = testStr.split("[^0-9]");//根据不是数字的字符拆分字符串
//		String numStr = strs[strs.length - 1];//取出最后一组数字
//		if (numStr != null && numStr.length() > 0) {//如果最后一组没有数字(也就是不以数字结尾)，抛NumberFormatException异常
//			int n = numStr.length();//取出字符串的长度
//			int num = Integer.parseInt(numStr) + 1;//将该数字加一
//			String added = String.valueOf(num);
//			n = Math.min(n, added.length());
//			//拼接字符串
//			return testStr.subSequence(0, testStr.length() - n) + added;
//		} else {
//			throw new NumberFormatException();
//		}
//	}

//	/**
//	 * 获取目标文件夹里的所有文件并排序
//	 * @param path 文件夹路径
//	 * @return Map
//	 */
	public static Map<Integer,MultipartFile> fileMap(String path){
			List<File> files = fileList(path);
			Map<Integer,MultipartFile> map = new HashMap<>();
			for (int i=0;i<=files.size()-1;i++) {
				map.put(i,fileToMultipart(files.get(i),files.get(i).getName(),getContentType(files.get(i))));
			}
			return map;
	}


	/**
	 * 获取指定目录的所有文件并排序
	 * @param path 文件夹路径
	 * @return Map
	 */
	public static List<File> fileList (String path){
		List<File> files = Arrays.asList(Objects.requireNonNull(new File(path).listFiles()));
		//获取所有文件并按文件名排序
		files.sort(new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				if (o1.isDirectory() && o2.isFile())
					return -1;
				if (o1.isFile() && o2.isDirectory())
					return 1;
				return o1.getName().compareTo(o2.getName());
			}
		});

		return files;
	}

//	/**
//	 * file 转 MultipartFile
//	 * @param file 文件
// 	 * @param fileName 文件名（后缀有没有都行）
//	 * @param contentType 文件类型
//	 * @return MultipartFile
//	 */
//	@Deprecated
//	public  MultipartFile fileToMultipartFile(File file,String fileName,String contentType) {
//		FileItem fileItem = createFileItem(file,fileName,contentType);
//		return new CommonsMultipartFile(fileItem);
//	}
//	private  FileItem createFileItem(File file,String fileName,String contentType) {
//		FileItemFactory factory = new DiskFileItemFactory(16, null);
//		FileItem item = factory.createItem(fileName, contentType, true, file.getName());
//		int bytesRead = 0;
//		byte[] buffer = new byte[8192];
//		try {
//			FileInputStream fis = new FileInputStream(file);
//			OutputStream os = item.getOutputStream();
//			while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
//				os.write(buffer, 0, bytesRead);
//			}
//			os.close();
//			fis.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return item;
//	}





	/**
	 * 将File转换成MultipartFile
	 * @return
	 */
	public static MultipartFile fileToMultipart(File file,String suffix,String contentType) {
		FileInputStream inputStream = null;
		try {
			 inputStream = new FileInputStream(file);
			MultipartFile multipartFile = new MockMultipartFile(file.getName(), suffix, contentType, inputStream);
			inputStream.close();
			return multipartFile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取文件contentType
	 * 该方式支持本地文件，也支持http/https远程文件
	 * @param file String
	 */
	public static String getContentType(File file) {
		String contentType = null;
		try {
			contentType = new MimetypesFileTypeMap().getContentType(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contentType;
	}







	public static MultipartFile createImg(String url) {
		try {
			// File转换成MutipartFile
			File file = MyFileUtils.createFileByUrl(url, "jpg");
			FileInputStream inputStream = new FileInputStream(file);
			MultipartFile multipartFile = new MockMultipartFile(file.getName(), inputStream);
			return multipartFile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}



	/**
	 * 将base64保存成文件到本地
	 * @param filePath
	 * @param base64Data
	 * @return
	 * @throws Exception
	 */
	public static boolean base64ToFile(String filePath, String base64Data)  throws Exception {
		String dataPrix = "";
        String data = "";

        if(base64Data == null || "".equals(base64Data)){
            return false;
        }else{
            String [] d = base64Data.split("base64,");
            if(d != null && d.length == 2){
                dataPrix = d[0];
                data = d[1];
            }else{
                return false;
            }
        }

        // 因为BASE64Decoder的jar问题，此处使用spring框架提供的工具包
        byte[] bs = Base64Utils.decodeFromString(data);
        // 使用apache提供的工具类操作流
        FileUtils.writeByteArrayToFile(new File(filePath), bs);

        return true;
	}


	/**
	 * 文件下载
	 * @param fileName
	 * @param fileByte
	 * @param response
	 */
	public static void FileDownloadUtil(String fileName, byte[] fileByte, HttpServletResponse response) {
		ServletOutputStream outputStream = null;
		try {
			response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
			response.setCharacterEncoding("UTF-8");
			if (fileByte != null) {
				outputStream = response.getOutputStream();
				outputStream.write(fileByte);
				outputStream.flush();
			}
		} catch (IOException e) {
			logger.debug("下载文件输出流异常：{}", e);
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				logger.debug("下载文件关闭流异常：{}", e);
			}
		}

	}



	//将文件转换成Byte数组
	public  byte[] getBytesByFile(String pathStr) {
		File file = new File(pathStr);
		try {
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			byte[] data = bos.toByteArray();
			bos.close();
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//将Byte数组转换成文件
	public  void getFileByBytes(byte[] bytes, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath + "\\" + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 根据文件路径获取文件头信息
	 *
	 * @param filePath
	 *            文件路径
	 * @return 文件头信息
	 */
	public  String getFileHeader(String filePath) {
		FileInputStream is = null;
		String value = null;
		try {
			is = new FileInputStream(filePath);
			byte[] b = new byte[4];
			/*
			 * int read() 从此输入流中读取一个数据字节。 int read(byte[] b) 从此输入流中将最多 b.length
			 * 个字节的数据读入一个 byte 数组中。 int read(byte[] b, int off, int len)
			 * 从此输入流中将最多 len 个字节的数据读入一个 byte 数组中。
			 */
			is.read(b, 0, b.length);
			value = bytesToHexString(b);
		} catch (Exception e) {
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return value;
	}

	/**
	 * 将要读取文件头信息的文件的byte数组转换成string类型表示
	 *
	 * @param src
	 *            要读取文件头信息的文件的byte数组
	 * @return 文件头信息
	 */
	private  String bytesToHexString(byte[] src) {
		StringBuilder builder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		String hv;
		for (int i = 0; i < src.length; i++) {
			// 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
			hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
			if (hv.length() < 2) {
				builder.append(0);
			}
			builder.append(hv);
		}
		System.out.println(builder.toString());
		return builder.toString();
	}




}
