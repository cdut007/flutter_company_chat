package com.ultralinked.voip.api;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.Executor;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Administrator on 2015/10/22.
 */
public class FileTransferUtil {

    protected final static String TAG="FileTransferUtil";

    protected final static int MAX_FILE_SIZE=20*1024*1024;

    private final static int MAX_DOWNLOAD_THREAD = 3;


    protected final static String[] supportedFileExts=new String[]{"txt","doc","bmp","jpeg","jpg","mp3","mp4","zip","txt"};


    protected static boolean isSupportedSize(String fileName){

         File file=new File(fileName);

        if(file.length()<=MAX_FILE_SIZE){
            return true;
        }
         return false;
    }


//   protected static Callback.Cancelable  downLoadFile(String fileName, String downloaddir, String downloadUrl, Callback.CommonCallback<File> callback ){
//
//
//       Log.i(TAG, "download file url : " + downloadUrl + " filePath : " +downloaddir + fileName);
//
//       RequestParams params = new RequestParams(downloadUrl);
//       params.setAutoRename(true);
//       params.setAutoResume(true);
//       params.setConnectTimeout(120000);
//       params.setSaveFilePath(downloaddir + fileName);
//       params.setExecutor(executor);
//       params.setCancelFast(true);
//       Callback.Cancelable cancelable = x.http().get(params, callback);
//       return cancelable;
//
//    }
//
//
//
//    protected static Callback.Cancelable sendThumbFile(final File file, String uploadFileUrl, final Callback.ProgressCallback callback) {
//
//        RequestParams params = new RequestParams(uploadFileUrl);
//        Callback.Cancelable cancelable=null;
//        Log.i(TAG, "sendThumbFile  " +file.getName()+" path  "+file.getPath()+" fileType : " +  " fileLength : " + file.length());
//
//     //   uploadFile(file,uploadFileUrl,callback);
//        try {
//            FileBody requestBody=new FileBody(file, "binary/octet-stream");
//            params.setRequestBody(requestBody);
//            params.setConnectTimeout(120000);
//            cancelable= x.http().request(HttpMethod.POST,params, callback);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        return cancelable;
//    }
//
//
//    protected static Callback.Cancelable sendFile(final File file, String uploadFileUrl, final Callback.ProgressCallback callback) {
//
//        RequestParams params = new RequestParams(uploadFileUrl);
//        Callback.Cancelable cancelable=null;
//        Log.i(TAG, "sendFile  " +file.getName()+" path  "+file.getPath()+" fileType : " +  " fileLength : " + file.length());
//
//       // uploadFile(file,uploadFileUrl,callback);
//        try {
//            FileBody requestBody=new FileBody(file, "binary/octet-stream");
//            params.setRequestBody(requestBody);
//            params.setConnectTimeout(120000);
//            cancelable= x.http().request(HttpMethod.POST,params, callback);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        return cancelable;
//    }

    private static final int TIME_OUT = 10*1000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    /**
     * android上传文件到服务器
     * @param file  需要上传的文件
     * @param RequestURL  请求的rul
     * @param callback
     * @return  返回响应的内容
     */
//    public static Callback.ProgressCallback uploadFile(File file, String RequestURL, Callback.ProgressCallback callback)
//    {
//        String result = null;
//        String BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
//        String PREFIX = "--" , LINE_END = "\r\n";
//        String CONTENT_TYPE = "binary/octet-stream";   //内容类型
//
//        try {
//            URL url = new URL(RequestURL);
//            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//            conn.setReadTimeout(TIME_OUT);
//            conn.setConnectTimeout(TIME_OUT);
//            conn.setDoInput(true);  //允许输入流
//            conn.setDoOutput(true); //允许输出流
//            conn.setUseCaches(false);  //不允许使用缓存
//            conn.setRequestMethod("POST");  //请求方式
//            conn.setRequestProperty("Charset", CHARSET);  //设置编码
//            conn.setRequestProperty("connection", "keep-alive");
//            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
//
//            if(file!=null)
//            {
//                /**
//                 * 当文件不为空，把文件包装并且上传
//                 */
//                DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
//                StringBuffer sb = new StringBuffer();
//                sb.append(PREFIX);
//                sb.append(BOUNDARY);
//                sb.append(LINE_END);
//                /**
//                 * 这里重点注意：
//                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
//                 * filename是文件的名字，包含后缀名的   比如:abc.png
//                 */
//
//                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""+file.getName()+"\""+LINE_END);
//                sb.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
//                sb.append(LINE_END);
//                dos.write(sb.toString().getBytes());
//                InputStream is = new FileInputStream(file);
//                byte[] bytes = new byte[1024];
//                int len = 0;
//                while((len=is.read(bytes))!=-1)
//                {
//                    dos.write(bytes, 0, len);
//                }
//                is.close();
//                dos.write(LINE_END.getBytes());
//                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
//                dos.write(end_data);
//                dos.flush();
//                /**
//                 * 获取响应码  200=成功
//                 * 当响应成功，获取响应的流
//                 */
//                int res = conn.getResponseCode();
//                Log.e(TAG, "response code:"+res);
//                if(res==200)
//                {
//                Log.e(TAG, "request success");
//                InputStream input =  conn.getInputStream();
//                StringBuffer sb1= new StringBuffer();
//                int ss ;
//                while((ss=input.read())!=-1)
//                {
//                    sb1.append((char)ss);
//                }
//                result = sb1.toString();
//                callback.onSuccess(result);
//                Log.e(TAG, "result : "+ result);
//                }
//                else{
//                    Log.e(TAG, "request error");
//                }
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            callback.onError(e, false);
//        } catch (IOException e) {
//            e.printStackTrace();
//            callback.onError(e,false);
//        }
//        return callback;
//    }
}
