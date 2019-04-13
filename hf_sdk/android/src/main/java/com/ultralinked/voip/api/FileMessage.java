package com.ultralinked.voip.api;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.ultralinked.voip.api.utils.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Administrator on 2015/11/4.
 */
public class FileMessage extends CustomMessage {


    public static  final String TAG="FileMessage";

    private int totalFileSize;

    private int FileCurSize;

    private String fileMD5;
    private String thumbUrl;
    private String thumbPath;

    /**
     * Get file message md5 value
     * @return md5 value
     */
    public String getFileMD5() {
        return fileMD5;
    }


    /**
     * Set file message md5 value
     * @param fileMD5
     */
    public void setFileMD5(String fileMD5) {
        this.fileMD5 = fileMD5;
    }

    /**
     * Get file upload or download current already done size
     * @return
     */
    public int getFileCurSize() {


        return FileCurSize;
    }

    /**
     *Set file upload or download current size
     * @param fileCurSize
     */
    public void setFileCurSize(int fileCurSize) {
        FileCurSize = fileCurSize;
    }

    /**
     * Get fiel total size
     * @return total file size
     */
    public int getTotalFileSize() {
        return totalFileSize;
    }

    /**
     * Set file total size
     * @param totalFileSize
     */
    public void setTotalFileSize(int totalFileSize) {
        this.totalFileSize = totalFileSize;
    }



    private String fileName;

    private String filePath;

//    protected   static  Callback.Cancelable cancelable;



    public static String getFileInfoJson(String FileUrl, String fileName, Options options) {


        try {
            JSONObject jsonObject = FileMessage.getFileJsonObject(FileUrl,fileName);

            return getFormatMessageJson(TAG,jsonObject, Message.MESSAGE_TYPE_FILE, options);
        } catch (Exception e) {
            Log.i(
                    TAG,
                    (new StringBuilder())
                            .append("getFileInfoJson error e:")
                            .append(e.getMessage()).toString());
        }
        return null;
    }

    @Override
    public void parseData(JSONObject json) throws JSONException {
        super.parseData(json);
        //parse publick info
        setThumbUrl(json.optString(THUMB_URL));
        setThumbPath(json.optString(THUMB_PATH));
        parseFileInfo(getJsonData());
    }

    /**
     * Get filename of this file message
     * @return filename of file message
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set filename of this file message
     * @param fileName
     */
    public void setFileName(String fileName) {

        this.fileName = fileName;
    }

    /**
     * Get file local path of file message
     * @return
     */
    public String getFilePath() {

        String fileName = null;
        String fileDir = MessagingApi.DOWNLOAD_DIRECTORY;
        if (isSender()){
            fileDir = MessagingApi.UPLOAD_DIRECTORY;
        }
        if (!TextUtils.isEmpty(getFileName())){
            //check file md5.
            String md5 =  getFileUrl();
            if (!TextUtils.isEmpty(md5)){

                //http://61.8.195.42:5443/3e1a6e3b9817e578c13209f50e52bcad9e2eca5f/vOHqXh7H436wqmDGXx2EhOebUvFuE7GENDyPskf8/3f577856594635261781662481cb1a9b.txt
                int pos = md5.lastIndexOf("/")+1;
                if (pos>=0){
                    md5 = md5.substring(pos).replace(".txt","");
                    Log.i(TAG,"file url md5:"+md5);
                }
            }


            if (md5 == null){

                md5 =  getFileMD5();
            }

            if (md5 == null){
                Log.i(TAG,"file url is empty, need to check");
            }
            fileName = FileMessage.getFileNameByMd5(getFileName(), md5);
            //check by md5 again.
            if (FileUtils.isFileExist(fileDir + fileName)) {
                Log.i(TAG, "file with md5 already exsit:" + md5);

            }

            filePath = fileDir + fileName;
            return filePath;

        }{
            Log.i(TAG,"error  file name is not exsit :filePath is:"+filePath);
        }

        return filePath;
    }

    /**
     * Set file local path of file message
     * @param filePath
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    private String fileUrl;

    /**
     * Get file remote path in server of file message
     * @return file remote path in server
     */
    public String getFileUrl() {
        if (TextUtils.isEmpty(fileUrl)){
            String messageBody = getBody();
            try {
                JSONObject jsonObject = new JSONObject(messageBody);
                String url = jsonObject.optString("fUrl");
                if (!TextUtils.isEmpty(url)){
                    fileUrl = url;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return fileUrl;
    }

    /**
     * Set file remote path in server of file message
     * @param fileUrl
     */
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    protected String getDownloadFileName() {
        String fileName = null;
        if (!TextUtils.isEmpty(getFileName())){
            //check file md5.
            String md5 = getFileUrl();
            if (!TextUtils.isEmpty(md5)){

                //http://61.8.195.42:5443/3e1a6e3b9817e578c13209f50e52bcad9e2eca5f/vOHqXh7H436wqmDGXx2EhOebUvFuE7GENDyPskf8/3f577856594635261781662481cb1a9b.txt
                int pos = md5.lastIndexOf("/")+1;
                if (pos>=0){
                    md5 = md5.substring(pos).replace(".txt","");
                    Log.i(TAG,"file url md5:"+md5);
                }


            }

            if (md5 ==null){

                md5 =  getFileMD5();
            }

            if (md5 == null){
                Log.i(TAG,"file url is empty, need to check");
            }

            fileName = FileMessage.getFileNameByMd5(getFileName(), md5);
            //check by md5 again.
            String fileDir = MessagingApi.DOWNLOAD_DIRECTORY;
            if (isSender()){
                fileDir = MessagingApi.UPLOAD_DIRECTORY;
                FileUtils.createFileDir(fileDir);
            }

            if (FileUtils.isFileExist(fileDir + fileName)){
                Log.i(TAG,"file with md5 already exsit:"+md5);
                return null;
            }
        }{
            Log.i(TAG,"error download file name is not exsit");
        }

        return  fileName;
    }
    /**
     * accept the incoming offline file message
     */
    public void accept(){

        Log.i(TAG,"~ File :"+ fileName+ " accpet");
        MessagingApi.accept(this);

    }



    /**
     * reject the incoming file message
     */
    public void reject(){

       Log.i(TAG,"reject the file");

       MessagingApi.reject(this);
    }

    /**
     * cancel the in progress file message
     */
    public void cancel() {

        Log.i(TAG, "cancel the file");

        MessagingApi.cancel(this);
    }

    /**
     * when file message failure resend this message
     */
    public void reSend() {
       MessagingApi.reSendMsg(getConversationId(), getKeyId(),true);
    }

    private  static  final String FILE_NAME="fileName",FILE_LENGTH="fileLength";

    public static JSONObject getFileJsonObject(String filePath, String fileName) throws JSONException {
            JSONObject jsonObject = new JSONObject();
            File file = new File(filePath);
            jsonObject.put(FILE_NAME, fileName);
            jsonObject.put(FILE_LENGTH, file.length());
        return  jsonObject;

    }

    public  void parseFileInfo(JSONObject data) throws JSONException {
        setFileName(data.optString(FILE_NAME));
        setTotalFileSize(data.optInt(FILE_LENGTH));
    }




   // {"data":{"fileLength":1200,"fileName":"re-share.png"},"desc":"base64",
   // "fUrl":"http://61.8.195.42:5443/0520f0091e578cb5c551adfade40d26028efea49/7amFmvs8rKHbxnJqLvxKhpwBYc44MwEwttVqdoY4/ead722592f4a89cea796a09c67dcc685.txt",
   // "messageType":"ImageMessage","messageTypeValue":3}
    private String getPreviewImageBase64(){

      return  MessagingApi.getPreviewImageBase64(this);
    }

//    @Override
//    public int getStatus() {
//        int status = super.getStatus();
//        if (status == STATUS_OK
//                ||
//                status == STATUS_DELIVERY_OK
//                ||
//                status == STATUS_READ
//
//                ){
//            if (!FileUtils.isFileExist(getFilePath())){
//                return STATUS_DRAFT;
//            }
//        }
//        return status;
//    }

    /**
     * get the preivew image of the video message
     * @return the bitmap of the preview image
     */
    public Bitmap getPreviewImage() {


        String imageBase64 = getPreviewImageBase64();

        Log.i(TAG,getFileName()+" imagebase64 : "+imageBase64);

        if(imageBase64==null){
            return null;
        }

        return BitmapUtils.getBitmapFromBase64String(imageBase64);

    }

    public static String getFileNameByMd5(String name, String orignalFileMd5) {


        return FileUtils.getFileNameNoEx(name) + "_"+ orignalFileMd5 +"."+ FileUtils.getExtension(name);
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }
}
