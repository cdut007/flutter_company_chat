package com.ultralinked.voip.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.ultralinked.voip.api.utils.CommonUtils;
import com.ultralinked.voip.api.utils.FileUtils;
import com.ultralinked.voip.gcm.MyFirebaseInstanceIDService;
import com.ultralinked.voip.gcm.QuickstartPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;


public class MessagingApi {

    public static String THUMB_UPLOAD_URL = null;
    public static String THUMB_DIRECTORY = null;
    public static Context mContext;

    public static final int MAX_NUMBER = 5000;

    public static final String TAG = "MessagingApi";

    private static SharedPreferences preferences;

    public static final String EVENT_MESSAGE_INCOMING = "com.ultralinked.voip.messageIncoming";

    public static final String EVENT_MESSAGE_PUSH = "com.ultralinked.voip.messagePush";


    public static final String PARAM_MESSAGE = "message";

    public static final String PARAM_CONVERSION = "conversion";

    public static final String EVENT_GROUP_MEMBER_CHANGE = "com.ultralinked.voip.groupMember";

    public static final String EVENT_GROUP_INVITE = "com.ultralinked.voip.groupInvite";

    public static final String EVENT_COMPOSING = "com.ultralinked.voip.message.COMPOSING";

    public static final String EVENT_BROADCAST = "com.ultralinked.voip.message.BROADCAST";


    public static final String PARAM_BROADCAST_TYPE = "BroadcastType";


    public static final String EVENT_PAINT = "com.ultralinked.voip.message.PAINT";

    public static final String PARAM_COMPOSING_STATUS = "composing_status";

    public static final String PARAM_BROADCAST_INFO = "broadcast_info";

    public static final String PARAM_PAINT_INFO = "paint_info";

    public static final String PARAM_FROM_TO = "from_to";

    public static final String PARAM_CHAT_TYPE = "chatType";

    public static final String PARAM_CHAT_ID = "chatId";

    public static final String PARAM_COUNT = "count";

    public static final String PARAM_GROUP = "group_to";

    public static final String PARAM_GROUP_TITLE = "group_title";

    public static final String PARAM_GROUP_MEMBER = "group_member";

    public static final String EVENT_GROUP_INFO_CHANGED = "com.ultralinked.voip.groupInfochanged";

    public static final String EVENT_CONV_INFO_CHANGED = "com.ultralinked.voip.convInfochanged";

    public static final String EVENT_CONV_CREATED = "com.ultralinked.voip.convCreated";

    public static final String EVENT_MESSAGE_FROM_HISTORY = "com.ultralinked.voip.messageHistory";

    public static final String EVENT_MESSAGE_STATUS_CHANGED = "com.ultralinked.voip.messageStatusChanged";

    public static final String EVENT_MESSAGE_PROGRESS_CHANGED = "com.ultralinked.voip.progressChange";

    public static final String EVENT_GET_FILE_URL = "com.ultralinked.voip.getFieURL";

    public static final String PARAM_FILE_NAME = "file_name";

    public static final String PARAM_FILE_URL = "file_url";

    public static final String PARAM_RESULT = "result";

    public static HashMap<Integer, Object> senderHanders = new HashMap<Integer, Object>();
    private static BroadcastReceiver mRegistrationBroadcastReceiver;

    private final int MAX = 1000;

    public final static String PARAM_STATUS = "group_status";

    public final static String PARAM_ACTION_EVENT = "action_event";

    public final static int JOIN_GROUP_SUCCESS = 1;

    public final static int JOIN_GROUP_FAILURE = 2;

    public final static int LEAVE_GROUP_SUCCESS = 3;

    public final static int KICK_OUT_BY_GROUP = 4;

    public final static int TITLE_CHANGE_GROUP_SUCCESS = 5;

    public final static int LEAVE_GROUP_FAILURE = 6;

    public final static int TITLE_CHANGE_GROUP_FAILURE = 7;

    public final static int INVITE_GROUP_SUCCESS = 8;

    public final static int INVITE_GROUP_FAILURE = 9;

    public final static int RECV_INVITE_GROUP_SUCCESS = 10;


    public final static int CONVERSATION_ACTION_MUTE = 1;

    public final static int CONVERSATION_ACTION_PROPERTIES = 2;

    //upload file directory
    protected static String UPLOAD_DIRECTORY;
    //download file directory
    protected static String DOWNLOAD_DIRECTORY;


    private static NetRtcXMPPCallbackImpl grrtcXmppcallback;
    private static IMessageConvertFactory iMessageConvertFactory;

    private static final int MAX_FILE_SIZE = 1024 * 1024 * 20;


    /**
     * static method to load the share library
     */
    static {

        try {

            System.loadLibrary("message_lib");

        } catch (UnsatisfiedLinkError e) {

            Log.i(TAG, "we have a problem to load Grrtc lib");

        } catch (Exception e) {
            e.printStackTrace();

            Log.i(TAG, "we have a problem to load Grrtc lib");
        }

    }

    public static void release() {
    }


    /**
     * init the message api
     *
     * @param context
     */
    public static void init(Context context) {
        init(context, null);
    }

    /**
     * init the message api
     *
     * @param context
     */
    public static void init(Context context, IMessageConvertFactory messageConvertFactory) {

        iMessageConvertFactory = messageConvertFactory;


        if (iMessageConvertFactory == null) {
            iMessageConvertFactory = new MessageConvertImp();
        }


        grrtcXmppcallback = iMessageConvertFactory.getIMCallbackObject();

        String currentVersion = iMessageConvertFactory.im_version();
        Log.i(TAG, "IM sdk version:" + currentVersion);


        if (!CommonUtils.isMainPid(context)) {

            return;
        }


        mContext = context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                if (grrtcXmppcallback == null) {
                    grrtcXmppcallback = new NetRtcXMPPCallbackImpl();

                    iMessageConvertFactory.setIMCallbackObject(grrtcXmppcallback);
                } else {
                    Log.i(TAG, "grrtcXmppcallback info has init.");
                }

            }
        }).start();


        //for sonnect
        CustomEventApi.init(context);


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

                boolean sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);

                if (sentToken) {

                    Log.i(TAG, "Token retrieved and sent to server! You can now use gcmsender to\n" +
                            "        send downstream messages to this app.");
                } else {

                    Log.i(TAG, "An error occurred while either fetching the InstanceID token,\n" +
                            " sending the fetched token to the server or subscribing to the PubSub topic. Please try\n" +
                            "  running the sample again.");

                }
            }
        };

        // Start IntentService to register this application with GCM.
        Intent intent = new Intent(context, MyFirebaseInstanceIDService.class);
        context.startService(intent);


        LocalBroadcastManager.getInstance(context).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

        preferences = PreferenceManager.getDefaultSharedPreferences(context);


        String appName = ConfigApi.appName;

        UPLOAD_DIRECTORY = FileUtils.getSDPath() + File.separator + appName + File.separator + "upload" + File.separator;

        DOWNLOAD_DIRECTORY = FileUtils.getSDPath() + File.separator + appName + File.separator + "download" + File.separator;

        THUMB_DIRECTORY = FileUtils.getSDPath() + File.separator + appName + File.separator + "thumb_icons" + File.separator;

        Log.i(TAG, "set im domain : " + PreferenceManager.getDefaultSharedPreferences(CallApi.getContext()).getString("sipdomain", "caas.grcaassip.com"));

        String imLogPath = FileUtils.getSDPath() + File.separator + appName + File.separator + "IMLogs";

        String imDBPath = FileUtils.getApplicationFileDir(context) + File.separator + appName;//

        FileUtils.createFileDir(imDBPath);
        FileUtils.createFileDir(imLogPath);

        FileUtils.createFileDir(UPLOAD_DIRECTORY);
        FileUtils.createFileDir(DOWNLOAD_DIRECTORY);
        FileUtils.createFileDirNoMedia(THUMB_DIRECTORY);


        iMessageConvertFactory.setConfig("IM_LOG_PATH", imLogPath);

        iMessageConvertFactory.setConfig("IM_DB_PATH", imDBPath);
    }

    public static String getVcardDir() {
        String vcardDir = UPLOAD_DIRECTORY;
        FileUtils.createFileDir(vcardDir);
        return vcardDir;
    }

    //send a local broad when new messge received
    public static void sendIncomingMsgBroadcast(Message message) {

        Intent callHandlerIntent = new Intent(EVENT_MESSAGE_INCOMING);

        callHandlerIntent.putExtra(PARAM_MESSAGE, message);

        if (mContext != null) {

            Log.i(TAG, "send incoming message  --> " + message.getSender());
            if (!message.isSender) {
                if (message instanceof VoiceMessage) {
                    ((VoiceMessage) message).accept();
                } else if (message instanceof VcardMessage) {
                    ((VcardMessage) message).accept();
                } else {
                    if (ConfigApi.IMConfig.mediaAutoDownload) {
                        if (message instanceof FileMessage) {
                            ((FileMessage) message).accept();
                        }
                    }
                }


            }

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(callHandlerIntent);

        } else {

            Log.i(TAG, "TestApplication is not Running()");
        }

    }


    public static void deleteAllConversationsMessages() {
        iMessageConvertFactory.deleteAllConversationMessages();
    }


    /**
     * get the conversations from begin to end of the current user
     *
     * @param begin
     * @param end
     * @return conversaton list
     */

    public static List<Conversation> getConversations(int begin, int end) {

        return iMessageConvertFactory.getConversations(begin, end);

    }

    /**
     * get all conversations fo the current user
     *
     * @return all the conversations of current user
     */
    public static List<Conversation> getAllConversations() {
        return getConversations(0, MAX_NUMBER);

    }

    /**
     * get all conversations fo the current user by type
     *
     * @return all the conversations of current user
     */
    public static List<Conversation> getAllConversationsByType(int chatType) {

        return iMessageConvertFactory.getAllConversationsByType(chatType);

    }

    /**
     * get conversations fo the current user by type
     *
     * @return all the conversations of current user
     */
    public static List<Conversation> getAllConversationsByFlag(int propsType, boolean hasFlag) {

        return iMessageConvertFactory.getAllConversationsByFlag(propsType, hasFlag);
    }

    /**
     * get message list from begin to end of the specified conversation
     *
     * @param conversationId
     * @param begin
     * @param end
     * @return message list
     */
    protected static List<Message> getMessages(int conversationId, int begin, int end) {

        return iMessageConvertFactory.getMessages(conversationId, begin, end);


    }


    public static CustomMessage parseMessageInfo(String contenct) throws Exception {
        CustomMessage message = null;
        JSONObject json = new JSONObject(contenct);
        String customType = json.optString(CustomMessage.MESSAGE_TYPE_TAG);
        if (TextMessage.TAG.equals(customType)) {
            message = new TextMessage();
            message.setType(Message.MESSAGE_TYPE_TEXT);
        } else if (ImageMessage.TAG.equals(customType)) {
            message = new ImageMessage();
            message.setHasFileTag(true);
            message.setType(Message.MESSAGE_TYPE_IMAGE);
        } else if (VideoMessage.TAG.equals(customType)) {
            message = new VideoMessage();
            message.setHasFileTag(true);
            message.setType(Message.MESSAGE_TYPE_VIDEO);
        } else if (FileMessage.TAG.equals(customType)) {
            message = new FileMessage();
            message.setHasFileTag(true);
            message.setType(Message.MESSAGE_TYPE_FILE);
        } else if (LocationMessage.TAG.equals(customType)) {
            message = new LocationMessage();
            message.setType(Message.MESSAGE_TYPE_LOCATION);
        } else if (VoiceMessage.TAG.equals(customType)) {
            message = new VoiceMessage();
            message.setHasFileTag(true);
            message.setType(Message.MESSAGE_TYPE_VOICE);
        } else if (StickerMessage.TAG.equals(customType)) {
            message = new StickerMessage();
            message.setHasFileTag(true);
            message.setType(Message.MESSAGE_TYPE_STICKER);
        } else if (VcardMessage.TAG.equals(customType)) {
            message = new VcardMessage();
            message.setHasFileTag(true);
            message.setType(Message.MESSAGE_TYPE_VCARD);
        } else if (EventMessage.TAG.equals(customType)) {
            message = new EventMessage();
            message.setType(Message.MESSAGE_TYPE_EVENT);
        } else if (VoipCallMessage.TAG.equals(customType)) {
            message = new VoipCallMessage();
            message.setType(Message.MESSAGE_TYPE_VOIP);
        } else if (SubscribeMessage.TAG.equals(customType)) {
            message = new SubscribeMessage();
            message.setType(Message.MESSAGE_TYPE_SUBSCRIBE);
        } else if (SystemMessage.TAG.equals(customType)) {
            message = new SystemMessage();
            message.setType(Message.MESSAGE_TYPE_SYSTEM);
        } else {//customType
            message = CustomMessageApi.createNewCustomMessage();
            message.setType(Message.MESSAGE_TYPE_CUSTOM);
        }
        message.parseData(json);

        return message;
    }


    // get all message by the contact number

    /**
     * get messages of the specified conversaion
     *
     * @param conversationId
     * @return message list
     */

    protected static List<Message> getAllMessages(int conversationId) {

        return getMessages(conversationId, 0, MAX_NUMBER);

    }

    /**
     * delete message with id in specified conversation
     *
     * @param conversationId
     * @param id
     */
    public static void deleteMessage(int conversationId, int id) {

        Log.i(TAG, "delete one  message from  conversation : " + conversationId + "message id : " + id);
        iMessageConvertFactory.deleteOneMessage(conversationId, id);


    }


    public static void updateMsgStatus(int conversationId, int msgId, int msgStatus) {
        iMessageConvertFactory.updateMsgStatus(conversationId, msgId, msgStatus);
    }

    public static Message getMessageById(int id, boolean isGroup) {

        return iMessageConvertFactory.getMessageById(id, isGroup);

    }

    /**
     * delete all messages in specified conversation
     *
     * @param conversationId
     */
    protected static void deleteAllMessages(int conversationId) {

        Log.i(TAG, "delete all message from conversation : " + conversationId);

        iMessageConvertFactory.deleteAllMessages(conversationId);


    }

    /**
     * delete mutiple messages with id arrayin specified conversation
     *
     * @param conversationId
     * @param ids
     */
    protected static void deleteMutipleMessage(int conversationId, int[] ids) {

        StringBuffer logString = new StringBuffer();

        logString.append("delete mutiple message from conversation : " + conversationId);

        logString.append(" ids : ");

        for (int id : ids) {

            logString.append(id).append(";");

        }

        Log.i(TAG, logString.toString());

        iMessageConvertFactory.deleteMultipleMessages(conversationId, ids, ids.length);
    }


    /**
     * send custom file message in  chat
     *
     * @param chatId
     * @param jsonData
     * @return the message object which send
     */
    protected static CustomMessage sendCustomFileMessage(String chatId, String jsonData, int convsationType, File file, boolean needcopy) {

        return iMessageConvertFactory.sendCustomFileMessage(chatId, jsonData, convsationType, file, needcopy);
    }


    /**
     * send location message in  chat
     *
     * @param chatId
     * @param options
     * @return the message object which send
     */
    protected static CustomMessage sendLocationMessage(String chatId, HashMap<String, String> locationInfo, int convsationType, Message.Options options) {
        String jsonData = LocationMessage.getTextLocationJson(locationInfo, options);
        return sendCustomMessage(chatId, jsonData, convsationType);
    }

    /**
     * send voice message in  chat
     *
     * @param chatId
     * @param options
     * @return the message object which send
     */
    protected static CustomMessage sendVoiceMessage(String chatId, int during, String voiceUrl, int convsationType, Message.Options options) {
        voiceUrl = getCompatibilityFilePath(voiceUrl);
        File file = new File(voiceUrl);
        String fileName = file.getName();
        fileName = getCompatibilityFileName(fileName, file);
        String jsonData = VoiceMessage.getVoiceInfoJson(voiceUrl, during, fileName, options);
        return sendCustomFileMessage(chatId, jsonData, convsationType, file, true);
    }

    private static String getCompatibilityFilePath(String filePath) {
        //remove _md5
        if (filePath.contains(UPLOAD_DIRECTORY) ||
                filePath.contains(DOWNLOAD_DIRECTORY)) {

            File file = new File(filePath);
            String md5 = calculateMD54File(file);
            if (md5 != null) {
                if (!file.getName().contains(md5)) {
                    File renameFile = new File(file.getParent() + File.separator + FileMessage.getFileNameByMd5(file.getName(), md5));
                    boolean rename = file.renameTo(renameFile);
                    filePath = renameFile.getPath();
                    Log.i(TAG, "rename:" + rename + ";file rename with the md5:" + renameFile.getName() + ";filePath:" + filePath);
                }

            }
        }


        return filePath;
    }


    /**
     * send vcard message in  chat
     *
     * @param chatId
     * @param options
     * @return the message object which send
     */
    protected static CustomMessage sendVcardMessage(String chatId, String vcardUrl, int convsationType, Message.Options options) {
        vcardUrl = getCompatibilityFilePath(vcardUrl);
        File file = new File(vcardUrl);
        String fileName = file.getName();
        fileName = getCompatibilityFileName(fileName, file);
        String jsonData = VcardMessage.getTextVcardJson(vcardUrl, fileName, options);
        return sendCustomFileMessage(chatId, jsonData, convsationType, file, true);
    }


    /**
     * send custom message in  chat
     *
     * @param chatId
     * @param options
     * @return the message object which send
     */
    protected static CustomMessage sendCustomTypeMessage(String chatId, JSONObject data, int convsationType, Message.Options options) {
        String jsonData = null;
        try {
            jsonData = CustomMessage.getFormatMessageJson(CustomMessage.TAG, data, Message.MESSAGE_TYPE_CUSTOM, options);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sendCustomMessage(chatId, jsonData, convsationType);
    }


    /**
     * send composing in  chat
     *
     * @param chatId
     * @return the message object which send
     */
    protected static void sendComposingBroadcast(String chatId, boolean isComposing, int convsationType) {
        String jsonData = BroadcastApi.getComposingStatusJson(isComposing);
        sendCustomBroadcast(chatId, jsonData, convsationType);
    }


    /**
     * send custom broadcast in  chat
     *
     * @param chatId
     * @param jsonData
     * @return the broadcast object which send
     */
    public static void sendCustomBroadcast(String chatId, String jsonData, int conversationType) {

        iMessageConvertFactory.sendCustomBroadcast(chatId, jsonData, conversationType);

    }


    /**
     * insert text message in  chat
     *
     * @param chatId
     * @return the message object which insert
     */
    public static Message insertTextMessage(String chatId, String from, String to, String content, int convsationType) {
        String jsonData = TextMessage.getTextJson(content, null);

        return insertCustomMessage(chatId, from, to, jsonData, convsationType);
    }


    /**
     * insert SystemMessage message in  chat
     *
     * @param chatId
     * @return the message object which insert
     */
    public static Message insertSystemMessage(String chatId, String from, String to, String content, int convsationType) {
        String jsonData = SystemMessage.getSystemJson(content, null);

        return insertCustomMessage(chatId, from, to, jsonData, convsationType);
    }


    /**
     * insert voipCalllog message in  chat
     *
     * @param chatId
     * @return the message object which insert
     */
    public static Message insertVoipCallLogMessage(String chatId, String caller, String callee, int during, int callType, int convsationType) {
        String jsonData = VoipCallMessage.getVoipCallInfoJson(caller, callee, during, callType, null);

        return insertCustomMessage(chatId, caller, callee, jsonData, convsationType);
    }


    /**
     * insert subscribe message in  chat
     *
     * @param chatId
     * @param options
     * @return the message object which insert
     */
    public static Message insertSubscribeMessage(String chatId, String from, String to, JSONObject data, int convsationType, Message.Options options) {
        String jsonData = SubscribeMessage.getSubscribeJson(data, options);

        return insertCustomMessage(chatId, from, to, jsonData, convsationType);
    }


    /**
     * insert custom message in  chat
     *
     * @param chatId
     * @param options
     * @return the message object which insert
     */
    public static Message insertCustomTypeMessage(String chatId, String from, String to, JSONObject data, int convsationType, Message.Options options) {
        String jsonData = null;
        try {
            jsonData = CustomMessage.getFormatMessageJson(CustomMessage.TAG, data, Message.MESSAGE_TYPE_CUSTOM, options);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return insertCustomMessage(chatId, from, to, jsonData, convsationType);
    }


    public static Message updateMessageBody(int keyId, int chatType, String body) {

        return iMessageConvertFactory.updateMessageBody(keyId, chatType, body);

    }

    /**
     * insert custom message in  chat
     *
     * @param chatId
     * @return the message object which insert
     */
    protected static CustomMessage insertCustomMessage(String chatId, String from, String to, String jsonData, int convsationType) {

        return iMessageConvertFactory.insertCustomMessage(chatId, from, to, jsonData, convsationType);
    }


    /**
     * send custom message in  chat
     *
     * @param chatId
     * @param jsonData
     * @return the message object which send
     */
    protected static CustomMessage sendCustomMessage(String chatId, String jsonData, int convsationType) {

        return iMessageConvertFactory.sendCustomMessage(chatId, jsonData, convsationType);

    }


    public static Conversation forwordMsg(Message message, String forwardedChatId, int conversationType) {
        Conversation conversation;
        if (conversationType == Message.CHAT_TYPE_GROUP) {
            conversation = GroupConversation.getConversationByGroupId(forwardedChatId);
        } else {
            conversation = MessagingApi.getConversation(forwardedChatId);
        }

        if (conversation == null) {
            Log.i(TAG, "converastion is null");
            return null;
        }
        switch (message.getType()) {

            case Message.MESSAGE_TYPE_TEXT:
                conversation.sendText(((TextMessage) message).getText());
                break;
            case Message.MESSAGE_TYPE_LOCATION:
                LocationMessage locationMessage = (LocationMessage) message;
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(LocationMessage.SUBTITLE, locationMessage.getSubTitle());
                hashMap.put(LocationMessage.TITLE, locationMessage.getTitle());
                hashMap.put(LocationMessage.LATITUDE, locationMessage.getLatitude() + "");
                hashMap.put(LocationMessage.LONGITUDE, locationMessage.getLongitude() + "");

                conversation.sendLocation(hashMap);
                break;
            case Message.MESSAGE_TYPE_STICKER:
                StickerMessage stickerMessage = (StickerMessage) message;
                if (!FileUtils.isFileExist(stickerMessage.getFilePath())) {
                    Log.i(TAG, "stickerMessage file not exsit.");
                    return null;
                }
                conversation.sendSticker(stickerMessage.getStickerName(), stickerMessage.getFilePath());

                break;
            case Message.MESSAGE_TYPE_CUSTOM:
                try {
                    conversation.sendCustomMessage(new JSONObject(message.getBody()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Message.MESSAGE_TYPE_FILE:
                FileMessage fileMessage = (FileMessage) message;
                if (!FileUtils.isFileExist(fileMessage.getFilePath())) {
                    Log.i(TAG, "fileMessage file not exsit.");
                    return null;
                }
                conversation.sendFile(fileMessage.getFilePath());
                break;
            case Message.MESSAGE_TYPE_VCARD:
                VcardMessage vcardMessage = (VcardMessage) message;
                if (!FileUtils.isFileExist(vcardMessage.getFilePath())) {
                    Log.i(TAG, "vcard file not exsit.");
                    return null;
                }
                conversation.sendVcard(vcardMessage.getFilePath());
                break;
            case Message.MESSAGE_TYPE_IMAGE:
                ImageMessage imageMessage = (ImageMessage) message;
                if (!FileUtils.isFileExist(imageMessage.getFilePath())) {
                    Log.i(TAG, "image file not exsit.");
                    return null;
                }
                conversation.sendImage(imageMessage.getFilePath());
                break;
            case Message.MESSAGE_TYPE_VIDEO:
                VideoMessage videoMessage = (VideoMessage) message;
                if (!FileUtils.isFileExist(videoMessage.getFilePath())) {
                    Log.i(TAG, "video file not exsit.");
                    return null;
                }
                conversation.sendVideo(videoMessage.getFilePath());
                break;
            case Message.MESSAGE_TYPE_VOICE:
                VoiceMessage voiceMessage = (VoiceMessage) message;
                if (!FileUtils.isFileExist(voiceMessage.getFilePath())) {
                    Log.i(TAG, "voice file not exsit.");
                    return null;
                }
                conversation.sendVoice(voiceMessage.getFilePath(), voiceMessage.getDuration());
                break;
        }

        return conversation;
    }


    /**
     * send text message in single chat
     *
     * @param chatId
     * @param msgContent
     * @param options
     * @return the message object which send
     */
    public static Message sendText(String chatId, String msgContent, int convsationType, Message.Options options) {
        String jsonData = TextMessage.getTextJson(msgContent, options);
        return sendCustomMessage(chatId, jsonData, convsationType);

    }

    /**
     * exit the specified group conversation
     *
     * @param groupId
     */
    protected static void exitGroup(int conversationId, String groupId) {
        checkLoginStatus();
        iMessageConvertFactory.exitGroup(conversationId, groupId);
    }

    /**
     * create the group chat with the group topic and the  group chat memembers
     *
     * @param groupTopic
     */
    public static void createGroup(String groupTopic) {
        checkLoginStatus();
        Log.i(TAG, "createGroup : " + groupTopic);
        iMessageConvertFactory.createGroup(groupTopic);

    }

    /**
     * modify the group tile
     *
     * @param groupTopic
     */
    protected static void modifyTitle(String groupId, String groupTopic) {
        checkLoginStatus();
        Log.i(TAG, "modifyTitle : " + groupTopic);
        iMessageConvertFactory.modifyTitle(groupId, groupTopic);

    }


    protected static void checkLoginStatus() {
        MLoginApi.checkLoginStatus();
    }

    /**
     * delete the specified conversaion
     *
     * @param conversationId
     */
    protected static void deleteConversation(int conversationId) {

        Log.i(TAG, "delete all message from conversation : " + conversationId);

        iMessageConvertFactory.deleteConversation(conversationId);


    }

/*
     public static List<Message> getHistoryMessages(int conversationId, int messageId,int limit){


 		List<Message> messages = new ArrayList<Message>();

 		Log.i(TAG, conversationId+"pending to get history message list begin messgeId :"+messageId+" to "+ limit);

 		c_Message[] tmpMessages =  rtcapij.getMessageListByMessageID(conversationId,messageId,limit);

 		Log.i(TAG, conversationId + " get real message list size : " + tmpMessages.length);

 		for (c_Message message : tmpMessages) {

 			Message m = convert2Message(message);


 			messages.add(m);
 		}

 		return messages;

     }*/

    /**
     * check the lastest group member in the server
     *
     * @param groupId
     */

    protected static void checkGroupMember(String groupId) {


        Log.i(TAG, "penging 2 get the group memembers on group : " + groupId);

        iMessageConvertFactory.checkGroupMember(groupId);
    }


    /**
     * get the group member list of the specified group conversaion
     *
     * @param groupId
     * @return list of the group member
     */
    protected static List<GroupMember> getGroupMemembers(String groupId, String adminId) {

        return iMessageConvertFactory.getGroupMemembers(groupId, adminId);

    }


    //send a local broad cast when the conv created
    protected static void sendConversationCreatedBroadcast(Conversation conversation) {
        Intent callHandlerIntent = new Intent(EVENT_CONV_CREATED);

        callHandlerIntent.putExtra(PARAM_CONVERSION, conversation);

        if (mContext != null) {

            Log.i(TAG, "sendConversationCreatedBroadcast--> ");

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(callHandlerIntent);

        } else {

        }
    }

    //send a local broad cast when the conv information changed
    protected static void sendConversationChangedBroadcast(String chatId, int conversationType, int action) {
        Intent callHandlerIntent = new Intent(EVENT_CONV_INFO_CHANGED);

        callHandlerIntent.putExtra(PARAM_CHAT_TYPE, conversationType);

        callHandlerIntent.putExtra(PARAM_CHAT_ID, chatId);

        callHandlerIntent.putExtra(PARAM_ACTION_EVENT, action);

        if (mContext != null) {

            Log.i(TAG, "sendConversationChangedBroadcast--> " + "==chatId:" + chatId + "==action:" + action);

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(callHandlerIntent);

        } else {

        }
    }

    //send a local broad cast when the group member information changed
    protected static void sendGroupMemberBroadcast(String groupId) {

        Intent callHandlerIntent = new Intent(EVENT_GROUP_MEMBER_CHANGE);

        callHandlerIntent.putExtra(PARAM_GROUP, groupId);

        if (mContext != null) {

            Log.i(TAG, "sendGroupMemberBroadcast--> ");

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(callHandlerIntent);

        } else {

        }

    }

    // send a local broad cast  when receive the group invite event
    protected static void sendGroupInviteBroadcast(String from, String groupId, String groupTopic, int inviteResult) {

        Intent callHandlerIntent = new Intent(EVENT_GROUP_INVITE);

        callHandlerIntent.putExtra(PARAM_FROM_TO, from);

        callHandlerIntent.putExtra(PARAM_GROUP, groupId);

        callHandlerIntent.putExtra(PARAM_GROUP_TITLE, groupTopic);

        callHandlerIntent.putExtra(PARAM_STATUS, inviteResult);

        if (mContext != null) {

            Log.i(TAG, "sendGroupInviteBroadcast--> ");

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(callHandlerIntent);

        } else {

        }

    }


    protected static Message.PeerInfo parsePeerInfo(String info) throws Exception {
        if (TextUtils.isEmpty(info)) {
            return null;
        }
        Log.i(TAG, "group member info : " + info);
        JSONObject peerInfoObj = new JSONObject(info);
        if (peerInfoObj != null) {
            Message.PeerInfo peerInfo = new Message.PeerInfo();
            peerInfo.mobile = peerInfoObj.optString(CustomMessage.MOBILE);
            peerInfo.userName = peerInfoObj.optString(CustomMessage.USER_NAME);
            peerInfo.nickName = peerInfoObj.optString(CustomMessage.NICK_NAME);

            return peerInfo;
        }

        return null;

    }

    /**
     * join the specified group
     * dead
     *
     * @param groupId
     */
    public static void JoinGroup(String groupId) {
        iMessageConvertFactory.JoinGroup(groupId);
    }

    /**
     * invite the contact to join specified group
     *
     * @param groupId
     * @param member
     */
    public static void inviteToGroup(String groupId, String member) {
        iMessageConvertFactory.inviteToGroup(groupId, member);
    }

    /**
     * get all unread message counts of the  current user
     *
     * @return all unread message counts
     */
    public static int getAllUnreadMessageCounts() {

        return iMessageConvertFactory.getAllUnreadMessageCounts();
    }

    /**
     * get all unread message counts of the  current user by conversation type
     *
     * @return all unread message counts
     */
    public static int getAllUnreadMessageCountsByConvType(boolean isGroup) {

        return iMessageConvertFactory.getAllUnreadMessageCountsByConvType(isGroup);
    }

    // get unread message counts of the specified conversaion
    protected static int getConversationUnreadMessageCounts(int conversationId) {

        return iMessageConvertFactory.getConversationUnreadMessageCounts(conversationId);


    }

    // set unread messag read in conversaion
    protected static void conversationRead(int conversationId) {
        iMessageConvertFactory.conversationRead(conversationId);

    }

    protected static void messageRead(int conversationId, int msgId) {

        iMessageConvertFactory.messageRead(conversationId, msgId);
    }

    protected static void sendGroupInfoChangeBroadcast(String groupId, int status) {

        Intent callHandlerIntent = new Intent(EVENT_GROUP_INFO_CHANGED);

        callHandlerIntent.putExtra(PARAM_STATUS, status);

        callHandlerIntent.putExtra(PARAM_GROUP, groupId);


        if (mContext != null) {

            Log.i(TAG, "sendGroupInfoChangeBroadcast--> " + status);

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(callHandlerIntent);

        } else {

        }

    }

    //send the msg status change broadcast
    protected static void sendMessageStatusChangeBroadcast(Message msg) {

        Intent callHandlerIntent = new Intent(EVENT_MESSAGE_STATUS_CHANGED);


        if (mContext != null && msg != null) {

            Log.i(TAG, "sendMessageStatusChangeBroadcast--> " + msg.getStatus());


            callHandlerIntent.putExtra(PARAM_MESSAGE, msg);

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(callHandlerIntent);

        } else {

        }

    }


    private static void uploadFile(final File sendingFile, final FileMessage fileMessage) {

        iMessageConvertFactory.uploadFile(sendingFile, fileMessage);

    }


//    private static void uploadThumbFile(final File sendingFile, final FileMessage fileMessage, final Callback.ProgressCallback<String> callbackListener) {
//
//        iMessageConvertFactory.uploadThumbFile(sendingFile, fileMessage, callbackListener);
//    }


    //process the send file message callback


    /**
     * send file message in  chat
     *
     * @param chatId
     * @param absolutePath
     * @param options
     */
    protected static Message sendFileChat(String chatId, String absolutePath, int mConversationType, Message.Options options) {

        absolutePath = getCompatibilityFilePath(absolutePath);
        File file = new File(absolutePath);
        String fileName = file.getName();
        fileName = getCompatibilityFileName(fileName, file);
        String filebody = FileMessage.getFileInfoJson(absolutePath, fileName, options);
        return sendCustomFileMessage(chatId, filebody, mConversationType, file, true);

    }

    private static String getCompatibilityFileName(String fileName, File file) {
        String absolutePath = file.getPath();
        if (absolutePath.contains(MessagingApi.UPLOAD_DIRECTORY) ||
                absolutePath.contains(MessagingApi.DOWNLOAD_DIRECTORY)) {
            //remove _md5
            String md5 = calculateMD54File(file);
            if (md5 != null) {
                fileName = fileName.replace("_" + md5, "");
                Log.i(TAG, "file has the md5:" + fileName);
            }
        }

        return fileName;

    }


    protected static boolean sizeCheck(File file) {
        if (file.length() > MAX_FILE_SIZE) {
            Toast.makeText(mContext, "Exceeds the maximum file limit 20M", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    public static Bitmap getVideoPrewImage(String videoFile) {
        //  String compress_image = "";
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoFile, MediaStore.Images.Thumbnails.MICRO_KIND);
        if (bitmap == null) {
            return bitmap;
        }

        System.out.println("w" + bitmap.getWidth());
        System.out.println("h" + bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 96, 96,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);//default
        if (bitmap == null) {
            Log.i("videoPrew", "image video thumb is null ");
        }

//		Bitmap previewImage = null;
//		try {
//			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//			retriever.setDataSource(videoFile);
//
//			if (android.os.Build.VERSION.SDK_INT > 9) {
//				previewImage = retriever.getFrameAtTime(1L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//			}
//			retriever.release();
//			if(previewImage==null){
//				Log.i("videoPrew","image thumb is null ");
//			}
//			return  previewImage;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

        return bitmap;

    }


    /**
     * send video message in  chat
     *
     * @param chatId
     * @param absolutePath
     * @param options
     */
    protected static Message sendVideoChat(String chatId, String absolutePath, int mConversationType, Message.Options options) {

        absolutePath = getCompatibilityFilePath(absolutePath);
        File file = new File(absolutePath);
        String fileName = file.getName();
        fileName = getCompatibilityFileName(fileName, file);
        Bitmap imageThumbnail = getThumbnail(getVideoPrewImage(file.getAbsolutePath()));
        String thumbFilePath = BitmapUtils.saveFile(imageThumbnail, fileName);
        //String thumbData = BitmapUtils.getBitmapStrBase64(imageThumbnail);
        String thumbUrl = null;
        if (thumbFilePath != null) {
            File thumbfile = new File(thumbFilePath);
            thumbUrl = THUMB_UPLOAD_URL + thumbfile.getName();
        }
        String filebody = VideoMessage.getVideoInfoJson(file.getAbsolutePath(), fileName, thumbFilePath, thumbUrl, options);
        return sendCustomFileMessage(chatId, filebody, mConversationType, file, true);

    }


    /**
     * get search message list by searchText
     *
     * @param searchText
     */
    public static List<Message> searchMessageListOfConversationByKeyword(Conversation conversation, String searchText) {

        return iMessageConvertFactory.searchMessageListOfConversationByKeyword(conversation, searchText);
    }


    /**
     * get converstion list by searchText
     *
     * @param searchText
     */
    public static List<Conversation> searchConversations(String searchText) {

        return iMessageConvertFactory.searchConversations(searchText);
    }


    /**
     * send sticker message in  chat
     *
     * @param chatId
     * @param absolutePath
     * @param options
     */
    protected static Message sendStickerChat(String chatId, String stickerName, String absolutePath, int mConversationType, Message.Options options) {

        absolutePath = getCompatibilityFilePath(absolutePath);
        File file = new File(absolutePath);
        String fileName = file.getName();
        fileName = getCompatibilityFileName(fileName, file);
        Bitmap imageThumbnail = getThumbnail(BitmapUtils.loadImagePrew(file.getAbsolutePath()));
        String thumbFilePath = BitmapUtils.saveFile(imageThumbnail, fileName);
        String thumbUrl = null;
        if (thumbFilePath != null) {
            File thumbfile = new File(thumbFilePath);
            thumbUrl = THUMB_UPLOAD_URL + thumbfile.getName();
        }
        //   String thumbData = BitmapUtils.getBitmapStrBase64(imageThumbnail);
        String filebody = StickerMessage.getStickerInfoJson(file.getAbsolutePath(), stickerName, fileName, thumbFilePath, thumbUrl, options);
        return sendCustomFileMessage(chatId, filebody, mConversationType, file, true);

    }

    /**
     * send image message in  chat
     *
     * @param chatId
     * @param absolutePath
     * @param options
     */
    protected static Message sendImageChat(String chatId, String absolutePath, int mConversationType, Message.Options options) {

        absolutePath = getCompatibilityFilePath(absolutePath);
        File file = new File(absolutePath);
        String fileName = file.getName();
        fileName = getCompatibilityFileName(fileName, file);
        Bitmap imageThumbnail = getThumbnail(BitmapUtils.loadImagePrew(file.getAbsolutePath()));

        int w = 0;
        int h = 0;
        if (imageThumbnail != null) {

            w = imageThumbnail.getWidth();
            h = imageThumbnail.getHeight();
        }

        String thumbFilePath = BitmapUtils.saveFile(imageThumbnail, fileName);
        String thumbUrl = null;
        if (thumbFilePath != null) {
            File thumbfile = new File(thumbFilePath);
            thumbUrl = THUMB_UPLOAD_URL + thumbfile.getName();
        }
        //   String thumbData = BitmapUtils.getBitmapStrBase64(imageThumbnail);
        String filebody = ImageMessage.getImageInfoJson(file.getAbsolutePath(), fileName, thumbFilePath, w, h, thumbUrl, options);
        return sendCustomFileMessage(chatId, filebody, mConversationType, file, true);

    }

    /**
     * get the thumbnail of the origin bitmap max size is 8kb
     *
     * @param orginBitmap
     * @return
     */
    private static Bitmap getThumbnail(Bitmap orginBitmap) {
        Bitmap bitmap = null;
        if (BitmapHelp.kBSizeOf(orginBitmap) > 200) {

            bitmap = BitmapUtils.compressImage(orginBitmap, 200, 2);
            if (bitmap == null) {
                Log.i("getThumbnail", "getThumbnail is null from compress");
            } else {
                Log.i("getThumbnail", "getThumbnail compress image w:" + bitmap.getWidth() + ";image h:" + bitmap.getHeight());
            }
        } else {

            bitmap = orginBitmap;
            if (bitmap == null) {
                Log.i("getThumbnail", "getThumbnail is null from orginal");
            }
        }

        return bitmap;

    }

    /**
     * when send file and image make a copy in upload path
     *
     * @param file
     */
    protected static File copyFile2UploadDirectory(final File file) {
        try {
            return fileCopy(file, UPLOAD_DIRECTORY);
        } catch (IOException e) {
            e.printStackTrace();
        }
//						new Thread(new Runnable() {
//							@Override
//							public void run() {
//
//								try {
//
//									fileCopy(file, UPLOAD_DIRECTORY);
//
//								} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//			}
//		}).start();
        return null;
    }

    private static File fileCopy(File file, String newFilePath) throws IOException {


        FileUtils.createFileDir(newFilePath);


        String orignalFileMd5 = calculateMD54File(file);
        File copyFile = null;
        String chooseFilePath = file.getPath();
        if (chooseFilePath.contains(MessagingApi.UPLOAD_DIRECTORY) ||
                chooseFilePath.contains(MessagingApi.DOWNLOAD_DIRECTORY)) {
            copyFile = new File(newFilePath + file.getName());
        } else {
            copyFile = new File(newFilePath + FileMessage.getFileNameByMd5(file.getName(), orignalFileMd5));
        }

        if (copyFile.exists()) {//check md5 is same.
            String md5 = calculateMD54File(copyFile);
            if (md5 != null && md5.equals(orignalFileMd5)) {
                Log.i(TAG, "has the same file by md5:" + file.getName());
                return copyFile;
            }
            if (md5 != null) {
                copyFile = new File(newFilePath + FileMessage.getFileNameByMd5(copyFile.getName(), md5));
            } else {
                copyFile = new File(newFilePath + FileMessage.getFileNameByMd5(file.getName(), "" + System.currentTimeMillis()));
            }
            Log.i(TAG, "same file name but content different:" + file.getName() + ";md5:" + md5);
        } else {
            Log.i(TAG, "get the copy file path:" + copyFile.getAbsolutePath());
        }

        FileInputStream inputStream = new FileInputStream(file);

        byte[] data = new byte[1024];
        int byteread = 0; // 读取的字节数
        FileOutputStream outputStream = new FileOutputStream(copyFile);

        while ((byteread = inputStream.read(data)) != -1) {
            outputStream.write(data, 0, byteread);
        }

        inputStream.close();
        outputStream.close();
        return copyFile;
    }

    /**
     * caculate the md5 value of a file
     *
     * @param updateFile
     * @return
     */
    protected static String calculateMD54File(File updateFile) {
        if (updateFile == null){
            Log.e(TAG, "updateFile not exsit while getting FileInputStream");
            return null;
        }
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output.toLowerCase();
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }
    }

    /**
     * send a local broad cast when file message upload or download proress  changed
     *
     * @param message
     */
    protected static void sendMessageProgressChangeBroadcast(Message message) {

        Intent callHandlerIntent = new Intent(EVENT_MESSAGE_PROGRESS_CHANGED);

        callHandlerIntent.putExtra(PARAM_MESSAGE, message);

        if (mContext != null) {

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(callHandlerIntent);

        } else {

            Log.i(TAG, "TestApplication is not Running()");
        }

    }


    /**
     * Get  conversation by convId
     *
     * @param convId
     * @return conversaion  if not find return null
     */
    public static Conversation getConversationById(int convId) {
        return iMessageConvertFactory.getConversationById(convId);
    }


    /**
     * create single chat conversation by contact number with Flag
     *
     * @param contactNumber
     * @param conversationFlag
     * @return
     */
    public static Conversation createConversationWithFlag(String contactNumber, int conversationFlag) {

        return iMessageConvertFactory.createConversationWithFlag(contactNumber, conversationFlag);
    }


    /**
     * Get single chat conversation by contact number
     *
     * @param contactNumber
     * @return
     */
    public static Conversation getConversation(String contactNumber) {


        return createConversationWithFlag(contactNumber, Conversation.CONVERSATION_FLAG_NONE);

    }

    public static String GCMProjectNumber;

    public static void sendHistroyCallbackBroadcast(String chatID, int chatType, int count) {

        Intent callHandlerIntent = new Intent(EVENT_MESSAGE_FROM_HISTORY);

        callHandlerIntent.putExtra(PARAM_FROM_TO, chatID);

        callHandlerIntent.putExtra(PARAM_CHAT_TYPE, chatType);

        callHandlerIntent.putExtra(PARAM_COUNT, count);

        if (mContext != null) {

            Log.i(TAG, "sendHistroyCallbackBroadcast--> ");

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(callHandlerIntent);

        } else {

        }
    }


    public static List<Message> getMessagesByMessageId(int conversationId, int msgId, int count, boolean isBeforeMsg, boolean containMsg) {

        return iMessageConvertFactory.getMessagesByMessageId(conversationId, msgId, count, isBeforeMsg, containMsg);
    }


    public static void sendAppToken(String token, String android) {
        iMessageConvertFactory.sendAppToken(token, android);
    }

    public static void setConfig(String im_flag, String value) {

        iMessageConvertFactory.setConfig(im_flag, value);
    }

    public static void reSendMsg(int conversationId, int keyId, boolean isFileMsg) {

        iMessageConvertFactory.reSendMsg(conversationId, keyId, isFileMsg);

    }

    public static void flushLog() {

        iMessageConvertFactory.flushLog();

    }

    public static GroupConversation getConversationByGroupId(String groupId) {
        return iMessageConvertFactory.getConversationByGroupId(groupId);
    }

    public static void kickMember(String groupID, String memberName) {

        iMessageConvertFactory.kickMember(groupID, memberName);

    }


    public static void setConversationBlock(String chatId, boolean isGroup, boolean isSetBlock) {

        iMessageConvertFactory.setConversationBlock(chatId, isGroup, isSetBlock);

    }

    public static boolean getConversationIsBlock(String chatId) {

        return iMessageConvertFactory.getConversationIsBlock(chatId);
    }


    public static void setConversationMute(String chatId, boolean isGroup, boolean isSetMute) {
        iMessageConvertFactory.setConversationMute(chatId, isGroup, isSetMute);
    }

    public static boolean getConversationIsMute(String chatId) {
        return iMessageConvertFactory.getConversationIsMute(chatId);
    }

    public static void setConversationProperty(String chatId, boolean isGroup, String properties) {
        iMessageConvertFactory.setConversationProperty(chatId, isGroup, properties);
    }

    public static String getConversationProperties(String chatId, boolean isGroup) {
        return iMessageConvertFactory.getConversationProperties(chatId, isGroup);
    }

    public static void setConversationPriority(int conversationId, boolean isTopUp) {

        iMessageConvertFactory.setConversationPriority(conversationId, isTopUp);
    }

    public static int setConversationDraftArray(int conversationId, byte[] bytes) {

        return iMessageConvertFactory.setConversationDraft(conversationId, bytes);
    }

    public static int createConversation(String contactNumber, boolean isGroup) {
        return iMessageConvertFactory.createConversation(contactNumber, isGroup);
    }

    public static void FileUrlCallback(Message msg) {
        iMessageConvertFactory.FileUrlCallback(msg);
    }

    public static void LoginXmpp(MLoginApi.Account currentAccount) {
        iMessageConvertFactory.LoginXmpp(currentAccount);
    }

    public static boolean isLogin() {
        if (iMessageConvertFactory == null) {
            return false;
        }
        return iMessageConvertFactory.isLogin();
    }

    public static int relogin() {

        if (iMessageConvertFactory == null) {

            return -1;
        }

        return iMessageConvertFactory.relogin();
    }

    public static void disconnect() {
        iMessageConvertFactory.disconnect();
    }

    public static void accept(FileMessage fileMessage) {
        iMessageConvertFactory.accept(fileMessage);
    }

    public static void reject(FileMessage fileMessage) {
        iMessageConvertFactory.reject(fileMessage);
    }

    public static String getPreviewImageBase64(FileMessage fileMessage) {
        return iMessageConvertFactory.getPreviewImageBase64(fileMessage);
    }


    public static void cancel(FileMessage fileMessage) {
        iMessageConvertFactory.cancel(fileMessage);
    }
}
