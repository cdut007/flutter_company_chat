package com.ultralinked.voip.api;

import android.text.TextUtils;

import com.ultralinked.voip.api.utils.FileUtils;
import com.ultralinked.voip.api.utils.MediaFile;
import com.ultralinked.voip.imapi.c_Conversation;
import com.ultralinked.voip.imapi.c_GroupMember;
import com.ultralinked.voip.imapi.c_Message;
import com.ultralinked.voip.imapi.eMessageStatus;
import com.ultralinked.voip.imapi.eMessageType;
import com.ultralinked.voip.imapi.imapij;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.ultralinked.voip.api.Message.STATUS_IN_PROGRESS;
import static com.ultralinked.voip.api.MessagingApi.senderHanders;

/**
 * Created by mac on 17/1/4.
 */

public class MessageConvertImp implements IMessageConvertFactory {
    private static final String TAG = "MessageConvertImp";

    @Override
    public String im_version() {
        return imapij.netrtcim_version();
    }

    @Override
    public void deleteAllConversationMessages() {
        imapij.deleteAllConversationMessages();
    }

    @Override
    public List<Conversation> getConversations(int begin, int end) {

        List<Conversation> conversations = new ArrayList<Conversation>();


        c_Conversation[] tmpConverisons = imapij.getConversationList(end, begin);

        if (tmpConverisons == null) {

            return conversations;
        }

        Log.i(TAG, "get conversation size : " + tmpConverisons.length);

        for (c_Conversation conversation : tmpConverisons) {

            Conversation con = convert2Conversation(conversation);

            conversations.add(con);

        }
        if (tmpConverisons != null && tmpConverisons.length > 0) {
            imapij.releaseConversation(tmpConverisons[0], tmpConverisons.length);
        }

        return conversations;
    }

    @Override
    public List<Conversation> getAllConversationsByType(int chatType) {
        List<Conversation> conversations = new ArrayList<Conversation>();
        int type = 0;
        if (chatType == Message.CHAT_TYPE_GROUP) {
            type = 1;
        }
        c_Conversation[] tmpConverisons = imapij.getConversationListByType(MessagingApi.MAX_NUMBER, 0, type);

        if (tmpConverisons == null) {

            return conversations;
        }

        Log.i(TAG, "chatType==" + chatType + "; get conversations by type size : " + tmpConverisons.length);

        for (c_Conversation conversation : tmpConverisons) {

            Conversation con = convert2Conversation(conversation);

            conversations.add(con);

        }
        if (tmpConverisons != null && tmpConverisons.length > 0) {
            imapij.releaseConversation(tmpConverisons[0], tmpConverisons.length);
        }

        return conversations;
    }

    @Override
    public List<Conversation> getAllConversationsByFlag(int propsType, boolean hasFlag) {

        List<Conversation> conversations = new ArrayList<Conversation>();

        String props = Conversation.getFlagStr(propsType);
        if (TextUtils.isEmpty(props)) {
            Log.i(TAG, "the props not exsit,type is " + propsType);
            return conversations;
        }
        c_Conversation[] tmpConverisons = imapij.getConversationListByFlag(MessagingApi.MAX_NUMBER, 0, props, hasFlag);

        if (tmpConverisons == null) {

            return conversations;
        }

        Log.i(TAG, "props==" + props + "; get getAllConversationsByProps by type size : " + tmpConverisons.length);

        for (c_Conversation conversation : tmpConverisons) {

            Conversation con = convert2Conversation(conversation);

            conversations.add(con);

        }
        if (tmpConverisons != null && tmpConverisons.length > 0) {
            imapij.releaseConversation(tmpConverisons[0], tmpConverisons.length);
        }

        return conversations;
    }

    @Override
    public List<Message> getMessages(int conversationId, int begin, int end) {
        List<Message> messages = new ArrayList<Message>();

        if (conversationId == -1) {

            return messages;
        }

        Log.i(TAG, conversationId + " pending to get message list from :" + begin + " to " + end);

        c_Message[] tmpMessages = imapij.getMessageList(conversationId, end, begin);

        if (tmpMessages == null) {
            return messages;
        }
        Log.i(TAG, conversationId + " get real message list size : " + tmpMessages.length);

        for (c_Message message : tmpMessages) {

            Message m = convert2Message(message);

            messages.add(m);
        }
        if (tmpMessages != null && tmpMessages.length > 0) {
            imapij.releaseMessage(tmpMessages[0], tmpMessages.length);
        }

        return messages;
    }

    @Override
    public void deleteOneMessage(int conversationId, int id) {
        imapij.deleteOneMessage(conversationId, id);
    }

    @Override
    public Message getMessageById(int id, boolean isGroup) {
        c_Message[] messages = imapij.getMessageByMsgID(id, isGroup);
        if (messages != null && messages.length > 0) {
            Message message = convert2Message(messages[0]);
            imapij.releaseMessage(messages[0], messages.length);
            return message;
        }

        return null;
    }

    @Override
    public void deleteAllMessages(int conversationId) {
        imapij.deleteAllMessages(conversationId);
    }

    @Override
    public void deleteMultipleMessages(int conversationId, int[] ids, int length) {

        imapij.deleteMultipleMessages(conversationId, ids, ids.length);
    }

    @Override
    public CustomMessage sendCustomFileMessage(String chatId, String jsonData, int convsationType, File file, boolean needcopy) {
        MessagingApi.checkLoginStatus();
        Log.i(TAG, "send message to : " + chatId + " message jsonData : " + jsonData);
        Log.i(TAG, " jsonData : " + jsonData);


        if (file == null) {
            Log.i(TAG, "file is null");
            return null;
        }

        if (file.length() == 0) {
            Log.i(TAG, "file length is  0");
            return null;
        }
        if (!MessagingApi.sizeCheck(file)) {
            return null;

        }
        ;


        if (needcopy) {
            file = MessagingApi.copyFile2UploadDirectory(file);
        }

        c_Message[] msgs = null;

        try {
            //later change by native method
            if (convsationType == Message.CHAT_TYPE_GROUP) {
                msgs = imapij.netimCreateFileMsgForGroupChatArray(chatId, jsonData.getBytes("UTF-8"), MessagingApi.calculateMD54File(file), (int) file.length());
            } else {
                msgs = imapij.netimCreateFileMsgForSingleChatArray(chatId, jsonData.getBytes("UTF-8"), MessagingApi.calculateMD54File(file), (int) file.length());

            }


        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

        if (msgs == null || msgs.length == 0) {
            Log.i(TAG, " create custom  file message failure");
            return null;
        }

        Message message = convert2Message(msgs[0]);

        if (message == null) {
            return null;
        }

        if (msgs != null && msgs.length > 0) {
            imapij.releaseMessage(msgs[0], msgs.length);
        }

        return (CustomMessage) message;
    }

    @Override
    public void sendCustomBroadcast(String chatId, String jsonData, int conversationType) {
        try {

            imapij.netimSendCustomBroadcastChatArray(chatId, jsonData.getBytes("UTF-8"), conversationType);


        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

    }

    @Override
    public Message updateMessageBody(int keyId, int chatType, String body) {

        c_Message[] msgs = null;


        try {

            msgs = imapij.netimUpdateMessageBodyArray(keyId, chatType, body.getBytes("UTF-8"));


        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        if (msgs == null || msgs.length == 0) {
            Log.i(TAG, " create custom  message failure");
            return null;
        }

        Message message = convert2Message(msgs[0]);

        if (message == null) {
            return null;
        }
        if (msgs != null && msgs.length > 0) {
            imapij.releaseMessage(msgs[0], msgs.length);
        }
        return message;
    }

    @Override
    public CustomMessage insertCustomMessage(String chatId, String from, String to, String jsonData, int conversationType) {

        MessagingApi.checkLoginStatus();
        Log.i(TAG, "send message to : " + chatId + " message jsonData : " + jsonData);

        c_Message[] msgs = null;

        try {
            //later change by native method
            if (conversationType == Message.CHAT_TYPE_GROUP) {
                //  msgs = imapij.grInsertCustomChatArray(chatId, jsonData.getBytes("UTF-8"));
            } else {
                msgs = imapij.netimInsertCustomChatArray(chatId, from, to, jsonData.getBytes("UTF-8"));
            }


        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        if (msgs == null || msgs.length == 0) {
            Log.i(TAG, " create custom  message failure");
            return null;
        }

        Message message = convert2Message(msgs[0]);

        if (message == null) {
            return null;
        }
        if (msgs != null && msgs.length > 0) {
            imapij.releaseMessage(msgs[0], msgs.length);
        }

        //imapij.updateMsgStatus(message.getConversationId(), message.getKeyId(), eMessageStatus.C_MESSAGE_STATUS_MESSAGE_SUCCESS);
        return (CustomMessage) message;
    }

    @Override
    public CustomMessage sendCustomMessage(String chatId, String jsonData, int convsationType) {
        MessagingApi.checkLoginStatus();
        Log.i(TAG, "send message to : " + chatId + " message jsonData : " + jsonData);

        c_Message[] msgs = null;

        try {
            //later change by native method
            if (convsationType == Message.CHAT_TYPE_GROUP) {
                msgs = imapij.netimSendCustomGroupChatArray(chatId, jsonData.getBytes("UTF-8"));
            } else {
                msgs = imapij.netimSendCustomChatArray(chatId, jsonData.getBytes("UTF-8"));
            }


        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        if (msgs == null || msgs.length == 0) {
            Log.i(TAG, " create custom  message failure");
            return null;
        }

        Message message = convert2Message(msgs[0]);

        if (message == null) {
            return null;
        }
        if (msgs != null && msgs.length > 0) {
            imapij.releaseMessage(msgs[0], msgs.length);
        }

        return (CustomMessage) message;
    }

    @Override
    public void exitGroup(int conversationId, String groupId) {
        imapij.deleteCoversation(conversationId);
        Log.i(TAG, "exit group : " + groupId);

        imapij.netimLeaveMuc(groupId);
    }

    @Override
    public void createGroup(String groupTopic) {
        try {
            imapij.netimCreateMucArray(groupTopic.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifyTitle(String groupId, String groupTopic) {
        imapij.netimModifyMucTitle(groupId, groupTopic);
    }

    @Override
    public void deleteConversation(int conversationId) {
        imapij.deleteCoversation(conversationId);
    }

    @Override
    public void checkGroupMember(String groupId) {
        imapij.netimTriggerGetRoomMembers(groupId);
    }

    @Override
    public int createConversation(String chatId, boolean isGroup) {
        return imapij.createConversation(chatId, isGroup);
    }

    @Override
    public int setConversationDraft(int converastionId, byte[] bytes) {
        return imapij.setConversationDraftArray(converastionId, bytes);
    }

    @Override
    public void setConversationPriority(int conversationId, boolean isTopUp) {
        imapij.setConversationPriority(conversationId, isTopUp);
    }

    @Override
    public void sendAppToken(String token, String android) {
        imapij.netimSendAppToken(token, android);
    }

    @Override
    public void setConversationMute(String chatId, boolean isGroup, boolean isSetMute) {
        imapij.setConversationMute(chatId, isGroup, isSetMute ? 1 : 0);
    }

    @Override
    public void setConversationProperty(String chatId, boolean isGroup, String properties) {
        imapij.setConversationProperty(chatId, isGroup, properties);
    }

    @Override
    public String getConversationProperties(String chatId, boolean isGroup) {
        return imapij.getConversationProperties(chatId, isGroup);
    }

    @Override
    public void reSendMsg(int conversationId, int keyId, boolean isFileMsg) {
        if (isFileMsg) {
            imapij.netimPrepareReSendFileMsg(conversationId, keyId);
        } else {
            imapij.netimResendMsg(conversationId, keyId);
        }
    }

    @Override
    public void flushLog() {
        imapij.netimFlushLog();
    }

    @Override
    public void kickMember(String groupID, String memberName) {
        imapij.netimKickFromMuc(groupID, memberName, "kick " + memberName);
    }


    @Override
    public void setConversationBlock(String chatId, boolean isGroup, boolean isSetBlock) {
        imapij.setConversationMute(chatId, isGroup, isSetBlock ? 2 : 0);
    }

    @Override
    public boolean getConversationIsBlock(String chatId) {
        return imapij.getConversationIsMute(chatId) >= 2;
    }

    @Override
    public boolean getConversationIsMute(String chatId) {
        return imapij.getConversationIsMute(chatId) >= 1;
    }

    @Override
    public GroupConversation getConversationByGroupId(String groupId) {
        GroupConversation groupConversation = null;

        Log.i(TAG, "get group conversation by groupId : " + groupId);

        c_Conversation[] c_conversations = imapij.getConversationByGlobalID(groupId, true);

        if (c_conversations != null && c_conversations.length > 0) {

            groupConversation = (GroupConversation) convert2Conversation(c_conversations[0]);

            imapij.releaseConversation(c_conversations[0], c_conversations.length);
        }


        return groupConversation;
    }

    @Override
    public List<Message> getMessagesByMessageId(int conversationId, int msgId, int count, boolean isBeforeMsg, boolean containMsg) {

        List<Message> messages = new ArrayList<Message>();

        if (conversationId == -1) {

            return messages;
        }

        if (msgId == -1) {
            Log.i(TAG, conversationId + "getMessages by count:" + count);
            return getMessages(conversationId, 0, count - 1);
        }

        if (!containMsg) {
            count = count + 1;
        }

        c_Message[] tmpMessages = imapij.getMessageListBesideMessageID(conversationId, msgId, count, isBeforeMsg);
        if (tmpMessages != null && tmpMessages.length > 0) {
            Log.i(TAG, conversationId + "getMessagesByMessageId get real message list size : " + tmpMessages.length);
            for (c_Message message : tmpMessages) {

                Message m = convert2Message(message);

                if (!containMsg && m.getKeyId() == msgId) {
                    continue;
                }
                messages.add(m);
            }
            imapij.releaseMessage(tmpMessages[0], tmpMessages.length);
        } else {
            Log.i(TAG, conversationId + " getMessagesByMessageId is null");

        }


        return messages;
    }

    @Override
    public void setConfig(String im_flag, String value) {
        imapij.netrtcim_set_config(im_flag, value);
    }

    @Override
    public Conversation createConversationWithFlag(String contactNumber, int conversationFlag) {
        Conversation conversation = null;

        if (contactNumber == null) {
            Log.e(TAG, "you must have a contact id");
            return null;
        }

        String propsStr = Conversation.getFlagStr(conversationFlag);

        if (!TextUtils.isEmpty(propsStr)) {
            contactNumber = contactNumber + propsStr;
        }
        Log.i(TAG, "get createConversationWithFlag type by contact : " + contactNumber);

        c_Conversation[] c_conversations = imapij.getConversationByGlobalID(contactNumber, false);

        if (c_conversations != null && c_conversations.length > 0) {

            conversation = convert2Conversation(c_conversations[0]);
            imapij.releaseConversation(c_conversations[0], c_conversations.length);
        }
        if (conversation == null) {

            conversation = new Conversation();

            conversation.setContactNumber(contactNumber);
            if (conversationFlag != Conversation.CONVERSATION_FLAG_NONE) {
                //create the conversation in database later.
                int convId = createConversation(contactNumber, false);
                if (convId >= 0) {
                    c_Conversation[] create_conversations = imapij.getConversationByGlobalID(contactNumber, false);

                    if (create_conversations != null && create_conversations.length > 0) {

                        conversation = convert2Conversation(create_conversations[0]);
                        imapij.releaseConversation(create_conversations[0], create_conversations.length);
                        if (conversation != null) {
                            MessagingApi.sendConversationCreatedBroadcast(conversation);
                        } else {
                            Log.i(TAG, " convert conversation failed by contact : " + contactNumber);
                        }
                    } else {
                        Log.i(TAG, " getConversationByGlobalID failed by contact : " + contactNumber);
                    }

                } else {
                    Log.i(TAG, " createConversationWithFlag type failed by contact : " + contactNumber);
                }
            }

        }

        return conversation;
    }

    protected static GroupMember convert2GroupMember(c_GroupMember c_member) {

        GroupMember groupMember = new GroupMember();
        groupMember.setGroupId(c_member.getGroupName());
        groupMember.setMemberId(c_member.getMemberName());
        groupMember.setMemberName(c_member.getMemberName());
        try {
            groupMember.setPeerInfo(MessagingApi.parsePeerInfo(new String(c_member.getNickname(), "UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "convert2GroupMember error--> " + e.getLocalizedMessage());
        }
        return groupMember;
    }


    @Override
    public List<GroupMember> getGroupMemembers(String groupId, String adminId) {

        List<GroupMember> members = new ArrayList<GroupMember>();

        c_GroupMember[] tmpMembers = imapij.netimGetRoomMembers(groupId);

        Log.i(TAG, groupId + " get Group Member size : " + tmpMembers.length + "adminId:" + adminId);

        GroupMember admin = null;
        for (c_GroupMember c_member : tmpMembers) {
            String userId = c_member.getMemberName();
            Log.i(TAG, "group member name : " + userId);
            GroupMember mem = convert2GroupMember(c_member);
            if (userId.equals(adminId)) {
                admin = mem;
            } else {
                members.add(mem);
            }

        }
        if (admin != null) {
            members.add(0, admin);
        } else {
            Log.i(TAG, "can not found the admin from the group id:" + groupId);
        }

        if (tmpMembers.length > 0) {
            imapij.netimReleaseRoomMembers(tmpMembers[0], tmpMembers.length);
        }

        return members;
    }

    @Override
    public void JoinGroup(String groupId) {
        imapij.netimJoinMuc(groupId);
    }

    @Override
    public void inviteToGroup(String groupId, String member) {
        imapij.netimInviteToMuc(member, groupId, "invite");
    }

    @Override
    public int getAllUnreadMessageCounts() {
        return imapij.getCountOfAllUnreadMsg();
    }

    @Override
    public int getAllUnreadMessageCountsByConvType(boolean isGroup) {
        return imapij.getCountOfAllUnreadMsgByConvType(isGroup);
    }

    @Override
    public int getConversationUnreadMessageCounts(int conversationId) {
        return imapij.getCountOfConversationUnreadMsg(conversationId);
    }

    @Override
    public void conversationRead(int conversationId) {
        imapij.setConversationAllMsgReadState(conversationId);
    }

    @Override
    public void messageRead(int conversationId, int msgId) {
        imapij.setOneMsgReadState(conversationId, msgId);
    }

    @Override
    public void uploadFile(final File sendingFile, final FileMessage fileMessage) {
//
//        Callback.Cancelable cancelable = FileTransferUtil.sendFile(sendingFile, fileMessage.getFileUrl(), new Callback.ProgressCallback<String>() {
//            @Override
//            public void onWaiting() {
//
//            }
//
//            @Override
//            public void onStarted() {
//                imapij.updateMsgStatus(fileMessage.getConversationId(), fileMessage.getKeyId(), eMessageStatus.C_MESSAGE_STATUS_FILE_PROGRESSING);
//                Log.i(TAG, "start uploading");
//
//            }
//
//            @Override
//            public void onLoading(long total, long current, boolean isDownloading) {
//
//                Log.i(TAG, "conversationID : " + fileMessage.getConversationId() + " msgID : " + fileMessage.getKeyId() + "update  current : " + current + " total : " + total);
//
//                imapij.updateFileSizeOfFileMsg(fileMessage.getConversationId(), fileMessage.getKeyId(), (int) current, (int) total);
//
//                fileMessage.setFileCurSize((int) current);
//                fileMessage.setStatus(STATUS_IN_PROGRESS);
//                MessagingApi.sendMessageProgressChangeBroadcast((Message) fileMessage);
//            }
//
//            @Override
//            public void onSuccess(String result) {
//
//                Log.i(TAG, "send file success conversation id : " + fileMessage.getConversationId());
//
//                imapij.updateFileSizeOfFileMsg(fileMessage.getConversationId(), fileMessage.getKeyId(), fileMessage.getTotalFileSize(), fileMessage.getTotalFileSize());
//                imapij.netimSendFileMsg(fileMessage.getConversationId(), fileMessage.getKeyId(), true);
//
//
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//
//                Log.i(TAG, "~ File upload failure :" + sendingFile.getName() + ex.toString());
//
//                //Toast.makeText(mContext, ex.toString(), Toast.LENGTH_LONG).show();
//
//                imapij.updateFileSizeOfFileMsg(fileMessage.getConversationId(), fileMessage.getKeyId(), 0, fileMessage.getTotalFileSize());
//
//                imapij.netimSendFileMsg(fileMessage.getConversationId(), fileMessage.getKeyId(), false);
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//                Log.i(TAG, "cancelled");
//
//                imapij.updateFileSizeOfFileMsg(fileMessage.getConversationId(), fileMessage.getKeyId(), 0, fileMessage.getTotalFileSize());
//
//                imapij.netimSendFileMsg(fileMessage.getConversationId(), fileMessage.getKeyId(), false);
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
//
//        senderHanders.put(fileMessage.getKeyId(), cancelable);
    }

    @Override
    public Conversation getConversationById(int convId) {
        Conversation conversation = null;

        Log.i(TAG, "get conversation by convId : " + convId);

        c_Conversation[] c_conversations = imapij.getConversationByConversationID(convId);

        if (c_conversations != null && c_conversations.length > 0) {

            conversation = convert2Conversation(c_conversations[0]);

            imapij.releaseConversation(c_conversations[0], c_conversations.length);
        }


        return conversation;
    }

    @Override
    public List<Message> searchMessageListOfConversationByKeyword(Conversation conversation, String searchText) {
        c_Message[] tmpMessages = null;
        try {
            tmpMessages = imapij.getMessageListOfConversationByKeywordArray(conversation.getConversationId(), searchText.getBytes("UTF-8"));
            if (tmpMessages == null) {
                Log.i(TAG, "get searchMessages null");
                return null;
            }

            Log.i(TAG, conversation.getConversationId() + " get search message list size : " + tmpMessages.length);
            List<Message> searchMessageList = new ArrayList<Message>(tmpMessages.length);
            for (c_Message message : tmpMessages) {

                Message m = convert2Message(message);

                searchMessageList.add(m);
            }
            if (tmpMessages != null && tmpMessages.length > 0) {
                imapij.releaseMessage(tmpMessages[0], tmpMessages.length);
            }
            return searchMessageList;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public List<Conversation> searchConversations(String searchText) {
        c_Conversation[] tmpConverisons = null;
        try {
            tmpConverisons = imapij.getConversationListWithKeywordMessageArray(searchText.getBytes("UTF-8"));//max is 1000.
            if (tmpConverisons == null) {
                Log.i(TAG, "get searchConversations null");
                return null;
            }

            Log.i(TAG, "get search conversation size : " + tmpConverisons.length);

            List<Conversation> conversationList = new ArrayList<Conversation>(tmpConverisons.length);

            for (c_Conversation conversation : tmpConverisons) {

                Conversation con = convert2Conversation(conversation);

                conversationList.add(con);

            }
            if (tmpConverisons != null && tmpConverisons.length > 0) {
                imapij.releaseConversation(tmpConverisons[0], tmpConverisons.length);
            }
            return conversationList;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return null;
    }

 //   @Override
 //   public void uploadThumbFile(final File sendingFile, final FileMessage fileMessage, final Callback.ProgressCallback<String> callbackListener) {
//        Callback.Cancelable cancelable = FileTransferUtil.sendThumbFile(sendingFile, fileMessage.getThumbUrl(), new Callback.ProgressCallback<String>() {
//            @Override
//            public void onWaiting() {
//
//            }
//
//            @Override
//            public void onStarted() {
//                imapij.updateMsgStatus(fileMessage.getConversationId(), fileMessage.getKeyId(), eMessageStatus.C_MESSAGE_STATUS_FILE_PROGRESSING);
//                Log.i(TAG, "start thumb uploading");
//
//            }
//
//            @Override
//            public void onLoading(long total, long current, boolean isDownloading) {
//
//                Log.i(TAG, "onloading thumb file conversationID : " + fileMessage.getConversationId() + " msgID : " + fileMessage.getKeyId() + " update  current : " + current + " total : " + total);
//
//            }
//
//            @Override
//            public void onSuccess(String result) {
//                callbackListener.onSuccess(result);
//
//                Log.i(TAG, "send thumb file success ,go to next step , response result : " + result);
//
//
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//
//                Log.i(TAG, "~ thumb File upload failure :" + sendingFile.getName() + ex.toString());
//
//                //Toast.makeText(mContext, ex.toString(), Toast.LENGTH_LONG).show();
//
//                imapij.updateFileSizeOfFileMsg(fileMessage.getConversationId(), fileMessage.getKeyId(), 0, fileMessage.getTotalFileSize());
//
//                imapij.netimSendFileMsg(fileMessage.getConversationId(), fileMessage.getKeyId(), false);
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//                Log.i(TAG, "cancelled");
//
//                imapij.updateFileSizeOfFileMsg(fileMessage.getConversationId(), fileMessage.getKeyId(), 0, fileMessage.getTotalFileSize());
//
//                imapij.netimSendFileMsg(fileMessage.getConversationId(), fileMessage.getKeyId(), false);
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
//
//        if (cancelable != null) {
//
//            senderHanders.put(fileMessage.getKeyId(), cancelable);
//        }
 //   }

    @Override
    public void setIMCallbackObject(Object object) {
        imapij.setIMCallbackObject((NetRtcXMPPCallbackImpl) object);
    }


    protected static void valueMapping(Message message, c_Message cMessage) {

        try {
            message.setBody(new String(cMessage.getMsg_body(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        message.setChatType(cMessage.getChatType());
        message.setSender(cMessage.getMsg_sender());
        message.setKeyId(cMessage.getMessageID());
        message.setConversationId(cMessage.getConversationID());
        String serverDate = cMessage.getMsg_server_date();
        if (TextUtils.isEmpty(serverDate)) {
            message.setGlobalMsgTime(cMessage.getMsg_date());
        } else {
            message.setGlobalMsgTime(serverDate);
        }
        message.setReceiver(cMessage.getMsg_receiver());
        message.setStatus(getMsgStatus(cMessage.getMsg_status()));
        message.setErrorInfo(cMessage.getError_info());
        boolean isSender = true;

        MLoginApi.initAccount();
        if (!cMessage.getMsg_sender().equalsIgnoreCase(MLoginApi.currentAccount.id)) {

            isSender = false;

        }
        message.isSender = isSender;
        message.setIsRead(!(cMessage.getIsRead() == 0));
    }


    private static int getMsgStatus(int msg_status) {
        int status = Message.STATUS_DRAFT;
        if (msg_status == eMessageStatus.C_MESSAGE_STATUS_MESSAGE_CREATE.ordinal()) {
            status = STATUS_IN_PROGRESS;
        } else if (msg_status == eMessageStatus.C_MESSAGE_STATUS_MESSAGE_WAIT_RECEIVER.ordinal()) {
            status = Message.STATUS_DELIVERY_OK;
        } else if (msg_status == eMessageStatus.C_MESSAGE_STATUS_MESSAGE_SUCCESS.ordinal()) {
            status = Message.STATUS_OK;
        } else if (msg_status == eMessageStatus.C_MESSAGE_STATUS_MESSAGE_SEND_FAILED.ordinal()) {
            status = Message.STATUS_FAILURE;
        } else if (msg_status == eMessageStatus.C_MESSAGE_STATUS_FAILED.ordinal()) {
            status = Message.STATUS_FAILURE;
        } else if (msg_status == eMessageStatus.C_MESSAGE_STATUS_FILE_CREATE.ordinal()) {
            status = STATUS_IN_PROGRESS;
        } else if (msg_status == eMessageStatus.C_MESSAGE_STATUS_FILE_URL_FAILED.ordinal()) {
            status = FileMessage.STATUS_FAILURE;
        } else if (msg_status == eMessageStatus.C_MESSAGE_STATUS_FILE_SEND_OK.ordinal()) {
            status = STATUS_IN_PROGRESS;
        } else if (msg_status == eMessageStatus.C_MESSAGE_STATUS_FILE_SEND_FAILED.ordinal()) {
            status = FileMessage.STATUS_FAILURE;
        } else if (msg_status == eMessageStatus.C_MESSAGE_STATUS_FILE_PROGRESSING.ordinal()) {
            status = STATUS_IN_PROGRESS;
        } else if (msg_status == eMessageStatus.C_MESSAGE_STATUS_FILE_RECV_CREATE.ordinal()) {
            status = Message.STATUS_DRAFT;
        } else if (msg_status == eMessageStatus.C_MESSAGE_STATUS_FILE_RECV_FAILED.ordinal()) {
            status = Message.STATUS_FAILURE;
        } else if (msg_status == eMessageStatus.C_MESSAGE_STATUS_READ.ordinal()) {
            status = Message.STATUS_READ;
        }


        return status;
    }


    private static CustomMessage parseCustomMessage(c_Message cMessage) throws Exception {

        String messageData = new String(cMessage.getMsg_body(), "UTF-8");
        CustomMessage message = MessagingApi.parseMessageInfo(messageData);
        return message;
    }

    //convert native message to Java Message
    protected static Message convert2Message(c_Message cMessage) {


        if ((cMessage.getMessageType() == eMessageType.C_MESSAGE_TYPE_TEXT.ordinal()) || (cMessage.getMessageType() == eMessageType.C_MESSAGE_TYPE_DRAFT.ordinal())) {

            Message message = new TextMessage();

            valueMapping(message, cMessage);

            if (cMessage.getMessageType() == eMessageType.C_MESSAGE_TYPE_TEXT.ordinal()) {
                message.setType(Message.MESSAGE_TYPE_TEXT);

            } else {
                message.setType(Message.MESSAGE_TYPE_DRAFT);
            }

            return message;

        } else if (cMessage.getMessageType() == eMessageType.C_MESSAGE_TYPE_CUSTOM_TEXT.ordinal()) {
            CustomMessage customMessage = null;
            try {
                customMessage = parseCustomMessage(cMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (customMessage != null) {//find custom type
                valueMapping(customMessage, cMessage);
                if (customMessage.hasFileTag()) {
                    customfileMessageMapping(cMessage, (FileMessage) customMessage);
                }

            } else {
                Log.i(TAG, "custom message parse error");
            }
            return customMessage;
        } else if (cMessage.getMessageType() == eMessageType.C_MESSAGE_TYPE_FILE_ONLY.ordinal()) {


            CustomMessage customMessage = null;
            try {
                customMessage = parseCustomMessage(cMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (customMessage != null) {//find custom type
                valueMapping(customMessage, cMessage);
                customfileMessageMapping(cMessage, (FileMessage) customMessage);
                return customMessage;
            } else {
                //for old parse later maybe removed.
                FileMessage fileMessage = new FileMessage();

                valueMapping(fileMessage, cMessage);

                fileMessageMapping(cMessage, fileMessage);

                fileMessage.setType(Message.MESSAGE_TYPE_FILE);
                return fileMessage;
            }


        } else if (cMessage.getMessageType() == eMessageType.C_MESSAGE_TYPE_TIPS.ordinal()) {


            TipsMessage message = null;
            try {

                message = new TipsMessage();
                valueMapping(message, cMessage);
                String messageData = new String(cMessage.getMsg_body(), "UTF-8");

                JSONObject json = new JSONObject(messageData);
                message = TipsMessage.parseJson(json, message);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return message;

        } else if (cMessage.getMessageType() == eMessageType.C_MESSAGE_TYPE_FILE_WITH_THUMBNAIL.ordinal()) {


            CustomMessage customMessage = null;
            try {
                customMessage = parseCustomMessage(cMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (customMessage != null) {//find custom type
                valueMapping(customMessage, cMessage);
                customfileMessageMapping(cMessage, (FileMessage) customMessage);
                return customMessage;
            } else {
                //for old parse later maybe removed.
                String fileName = null;
                try {
                    fileName = new String(cMessage.getMsg_body(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                FileMessage fileMessage = null;
                if (MediaFile.isVideoFileType(fileName)) {
                    fileMessage = new VideoMessage();

                    valueMapping(fileMessage, cMessage);

                    fileMessageMapping(cMessage, fileMessage);

                    fileMessage.setType(Message.MESSAGE_TYPE_VIDEO);
                } else {
                    fileMessage = new ImageMessage();

                    valueMapping(fileMessage, cMessage);

                    fileMessageMapping(cMessage, fileMessage);

                    fileMessage.setType(Message.MESSAGE_TYPE_IMAGE);
                }


                return fileMessage;
            }


        } else {
            return null;
        }
    }


    private static void customfileMessageMapping(c_Message cMessage, FileMessage fileMessage) {

        fileMessage.setFileUrl(cMessage.getFileURL());

        String fileName = fileMessage.getFileName();

        fileMessage.setFileMD5(cMessage.getLocal_resource());

        if (fileMessage.isSender()) {

            fileMessage.setFilePath(MessagingApi.UPLOAD_DIRECTORY + fileName);
            //fileMessage.setFilePath(cMessage.);
        } else {

            fileMessage.setFilePath(MessagingApi.DOWNLOAD_DIRECTORY + fileName);
        }

        fileMessage.setFileCurSize(cMessage.getFileCurSize());

        if ((cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_FILE_CREATE.ordinal() || cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_FILE_RECV_CREATE.ordinal()) && cMessage.getFileCurSize() == 0) {

            if (cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_FILE_CREATE.ordinal()) {
                fileMessage.setStatus(STATUS_IN_PROGRESS);
            } else {
                fileMessage.setStatus(Message.STATUS_DRAFT);
            }


        } else if (cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_FILE_RECV_FAILED.ordinal() || cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_FILE_SEND_FAILED.ordinal() || cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_FILE_URL_FAILED.ordinal()) {


            fileMessage.setStatus(FileMessage.STATUS_FAILURE);


        } else if ((cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_FILE_CREATE.ordinal() || cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_FILE_RECV_CREATE.ordinal()) && cMessage.getFileCurSize() > 0 && cMessage.getFileTotalSize() > 0 && (cMessage.getFileCurSize()) <= (cMessage.getFileTotalSize())) {


            fileMessage.setStatus(STATUS_IN_PROGRESS);

        } else if (cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_MESSAGE_WAIT_RECEIVER.ordinal()) {

            fileMessage.setStatus(Message.STATUS_DELIVERY_OK);
        }
    }


    private static void fileMessageMapping(c_Message cMessage, FileMessage fileMessage) {

        fileMessage.setFileUrl(cMessage.getFileURL());

        String fileName = fileMessage.getBody();

        if (TextUtils.isEmpty(fileMessage.getFileName())) {//maybe remove mapping file later
            fileMessage.setFileName(fileName);
        }

        fileMessage.setFileMD5(cMessage.getLocal_resource());

        if (fileMessage.isSender()) {

            fileMessage.setFilePath(MessagingApi.UPLOAD_DIRECTORY + fileName);
            //fileMessage.setFilePath(cMessage.);
        } else {

            fileMessage.setFilePath(MessagingApi.DOWNLOAD_DIRECTORY + fileName);
        }
        if (cMessage.getFileTotalSize() > 0) {
            fileMessage.setTotalFileSize(cMessage.getFileTotalSize());
        }
        if (cMessage.getFileCurSize() > 0) {
            fileMessage.setFileCurSize(cMessage.getFileCurSize());
        }


        if ((cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_FILE_CREATE.ordinal() || cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_FILE_RECV_CREATE.ordinal()) && cMessage.getFileCurSize() == 0) {

            if (cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_FILE_CREATE.ordinal()) {
                fileMessage.setStatus(STATUS_IN_PROGRESS);
            } else {
                fileMessage.setStatus(Message.STATUS_DRAFT);
            }


        } else if (cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_FILE_RECV_FAILED.ordinal() || cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_FILE_SEND_FAILED.ordinal() || cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_FILE_URL_FAILED.ordinal()) {


            fileMessage.setStatus(FileMessage.STATUS_FAILURE);


        } else if ((cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_FILE_CREATE.ordinal() || cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_FILE_RECV_CREATE.ordinal()) && cMessage.getFileCurSize() > 0 && cMessage.getFileTotalSize() > 0 && (cMessage.getFileCurSize()) <= (cMessage.getFileTotalSize())) {


            fileMessage.setStatus(STATUS_IN_PROGRESS);

        } else if (cMessage.getMsg_status() == eMessageStatus.C_MESSAGE_STATUS_MESSAGE_WAIT_RECEIVER.ordinal()) {

            fileMessage.setStatus(Message.STATUS_OK);
        }
    }

    //convert native conversation to Java Conversation
    protected static Conversation convert2Conversation(c_Conversation cConversation) {

        if (cConversation.getIsGroupChat()) {

            GroupConversation groupConversation = new GroupConversation();

            conversationValueMapping(cConversation, groupConversation);

            try {
                groupConversation.setGroupTopic(new String(cConversation.getConversation_nickname(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            groupConversation.setGroupID(cConversation.getConversation());

            return groupConversation;


        } else {

            Conversation conversation = new Conversation();

            conversationValueMapping(cConversation, conversation);

            conversation.setContactNumber(cConversation.getConversation());

            return conversation;

        }


    }

    private static void conversationValueMapping(c_Conversation cConversation, Conversation conversation) {

        conversation.setConversationId(cConversation.getConversationID());
        conversation.setChairMan(cConversation.getChair_man());
        String serverDate = cConversation.getMsg_server_date();
        if (TextUtils.isEmpty(serverDate)) {
            conversation.setTime(cConversation.getMsg_date());
        } else {
            conversation.setTime(serverDate);
        }


        try {
            String draft = new String(cConversation.getDraft(), "UTF-8");
            conversation.setDraft(draft);

        } catch (UnsupportedEncodingException e) {

        }

        int msgType = Message.MESSAGE_TYPE_UNKNOWN;
        CustomMessage customMessage = null;
        String msgContent = "";

        try {
            msgContent = new String(cConversation.getMsg_body(), "UTF-8");
            if (!TextUtils.isEmpty(msgContent)) {
                customMessage = MessagingApi.parseMessageInfo(msgContent);
                msgType = customMessage.getType();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        if (customMessage == null) {
            customMessage = new CustomMessage();
        }

        customMessage.setSender(cConversation.getMsg_sender());
        boolean isSender = true;
        MLoginApi.initAccount();
        if (!TextUtils.isEmpty(customMessage.getSender()) && !customMessage.getSender().equalsIgnoreCase(MLoginApi.currentAccount.id)) {

            isSender = false;

        }
        customMessage.isSender = isSender;

        customMessage.setReceiver(cConversation.getConversation());

        customMessage.setStatus(cConversation.getMsg_status());
        customMessage.setKeyId(cConversation.getMessageID());

        if (cConversation.getMessageType() == eMessageType.C_MESSAGE_TYPE_TIPS.ordinal()) {

            try {
                msgType = TipsMessage.parseSystemMessageType(msgContent);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (msgType == Message.MESSAGE_TYPE_UNKNOWN) {
                Log.i(TAG, "unkonw message type: body is:" + msgContent);
            }

            customMessage.setType(msgType);
        } else {
            //all is custom


            if (msgType == Message.MESSAGE_TYPE_UNKNOWN) {
                msgType = cConversation.getMessageType();
            }
            customMessage.setType(msgType);
        }


        conversation.setLastMessage(customMessage);
        conversation.peerInfo = customMessage.getPeerInfo();
        conversation.setMsgCount(cConversation.getMessageCount());

        conversation.setGroup(cConversation.getIsGroupChat() ? true : false);

        conversation.isTopUp = cConversation.getIsTopPriority();
        conversation.setUnreadCount(cConversation.getUnreadMessageCount());
        conversation.setConvProperties(cConversation.getProperties());
    }

    @Override
    public void FileUrlCallback(final Message message) {


        final FileMessage fileMessage = (FileMessage) message;

        final String fileName = fileMessage.getFileName();

        Log.i(TAG, "fileName : " + fileName + "~ fileUrl : " + fileMessage.getFileUrl());

        if (fileMessage.getFilePath() == null) {
            Log.i(TAG, "getFilePath is null ");
            return;

        }

        final File sendingFile = new File(fileMessage.getFilePath());

        if (sendingFile == null) {
            Log.i(TAG, "file is destory");
            return;

        }

        if (sendingFile != null && !TextUtils.isEmpty(fileName)) {
            Log.i(TAG, "thumb_icons:" + fileMessage.getThumbPath());
            if (fileMessage.getThumbPath() != null) {
                File thumbFile = new File(fileMessage.getThumbPath());
                //has thumb,.
                if (thumbFile.exists()) {
//                    uploadThumbFile(thumbFile, fileMessage, new Callback.ProgressCallback<String>() {
//                        @Override
//                        public void onWaiting() {
//
//                        }
//
//                        @Override
//                        public void onStarted() {
//
//                        }
//
//                        @Override
//                        public void onLoading(long total, long current, boolean isDownloading) {
//
//                        }
//
//                        @Override
//                        public void onSuccess(String result) {
//                            uploadFile(sendingFile, fileMessage);
//                        }
//
//                        @Override
//                        public void onError(Throwable ex, boolean isOnCallback) {
//
//                        }
//
//                        @Override
//                        public void onCancelled(CancelledException cex) {
//
//                        }
//
//                        @Override
//                        public void onFinished() {
//
//                        }
//                    });
                } else {
                    Log.i(TAG, "not find the thumb upload orignal");
                    uploadFile(sendingFile, fileMessage);
                }

            } else {
                uploadFile(sendingFile, fileMessage);
            }

        } else {


            Log.i(TAG, fileName + " not in pengding list");


        }
    }

    @Override
    public void updateMsgStatus(int conversationId, int msgId, int msgStatus) {
        if (msgStatus == Message.STATUS_DRAFT) {

            imapij.updateMsgStatus(conversationId, msgId, eMessageStatus.C_MESSAGE_STATUS_MESSAGE_CREATE);

        } else if (msgStatus == Message.STATUS_FAILURE) {

            imapij.updateMsgStatus(conversationId, msgId, eMessageStatus.C_MESSAGE_STATUS_MESSAGE_SEND_FAILED);

        } else if (msgStatus == Message.STATUS_OK) {

            imapij.updateMsgStatus(conversationId, msgId, eMessageStatus.C_MESSAGE_STATUS_MESSAGE_SUCCESS);

        }

    }

    @Override
    public void LoginXmpp(MLoginApi.Account currentAccount) {
        imapij.netimLoginXmpp(currentAccount.id, currentAccount.password);
    }

    @Override
    public boolean isLogin() {
        return (imapij.netimRunningState() == 0);
    }

    @Override
    public int relogin() {
        return imapij.netimReconnectXmpp_current_Account();
    }

    @Override
    public void disconnect() {
        imapij.netimDisposeXmpp();
    }

    @Override
    public void accept(final FileMessage fileMessage) {
        FileUtils.createFileDir(MessagingApi.DOWNLOAD_DIRECTORY);
        //check file if exsit.
        String downloadFileName = fileMessage.getDownloadFileName();
        if (downloadFileName == null){//update to succ ,already download.
            imapij.updateFileSizeOfFileMsg(fileMessage.getConversationId(), fileMessage.getKeyId(),  fileMessage.getTotalFileSize(), fileMessage.getTotalFileSize());
            imapij.updateMsgStatus(fileMessage.getConversationId(), fileMessage.getKeyId(), eMessageStatus.C_MESSAGE_STATUS_MESSAGE_SUCCESS);

            return;
        }
        String downloadDir = fileMessage.isSender()? MessagingApi.UPLOAD_DIRECTORY: MessagingApi.DOWNLOAD_DIRECTORY;
//        FileMessage.cancelable= FileTransferUtil.downLoadFile(downloadFileName, downloadDir , fileMessage.getFileUrl(), new Callback.ProgressCallback<File>() {
//            @Override
//            public void onWaiting() {
//
//            }
//
//            @Override
//            public void onStarted() {
//                imapij.updateMsgStatus(fileMessage.getConversationId(), fileMessage.getKeyId(), eMessageStatus.C_MESSAGE_STATUS_FILE_PROGRESSING);
//            }
//
//            @Override
//            public void onLoading(long total, long current, boolean isDownloading) {
//
//                Log.i(TAG, "conversationID : " + fileMessage.getConversationId() + " msgID : " + fileMessage.getKeyId()+ " current : " + current + "~ total : " + total);
//
//                imapij.updateFileSizeOfFileMsg(fileMessage.getConversationId(), fileMessage.getKeyId(), (int) current, (int) total);
//                fileMessage.setFileCurSize((int)current);
//                fileMessage.setStatus(STATUS_IN_PROGRESS);
//                MessagingApi.sendMessageProgressChangeBroadcast(fileMessage);
//            }
//
//            @Override
//            public void onSuccess(File result) {
//
//                Log.i(TAG, "~ File :" + fileMessage.getFileName() + " download success  " );
//                imapij.updateFileSizeOfFileMsg(fileMessage.getConversationId(), fileMessage.getKeyId(),  fileMessage.getTotalFileSize(), fileMessage.getTotalFileSize());
//                imapij.updateMsgStatus(fileMessage.getConversationId(), fileMessage.getKeyId(), eMessageStatus.C_MESSAGE_STATUS_MESSAGE_SUCCESS);
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//
//                imapij.updateMsgStatus(fileMessage.getConversationId(), fileMessage.getKeyId(), eMessageStatus.C_MESSAGE_STATUS_FILE_RECV_FAILED);
//
//                Log.i(TAG, "~ File download failure :" + fileMessage.getFileName() + ex.toString());
//
//                imapij.updateFileSizeOfFileMsg(fileMessage.getConversationId(), fileMessage.getKeyId(), 0, fileMessage.getTotalFileSize());
//
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//                imapij.updateFileSizeOfFileMsg(fileMessage.getConversationId(), fileMessage.getKeyId(), 0,fileMessage.getTotalFileSize());
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
    }

    @Override
    public void reject(FileMessage fileMessage) {
        if(fileMessage.isSender()) {

            imapij.updateMsgStatus(fileMessage.getConversationId(), fileMessage.getKeyId(), eMessageStatus.C_MESSAGE_STATUS_FILE_SEND_FAILED);

        }else{

            imapij.updateMsgStatus(fileMessage.getConversationId(),fileMessage.getKeyId(), eMessageStatus.C_MESSAGE_STATUS_FILE_RECV_FAILED);

        }
    }

    @Override
    public String getPreviewImageBase64(FileMessage fileMessage) {
        String base64 = null;

        String messageBody =fileMessage. getBody();
        try {
            JSONObject jsonObject = new JSONObject(messageBody);
            base64 = jsonObject.optString(CustomMessage.DESC);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(base64)){
            base64  = imapij.netrtcim_hashmap_getstr(imapij.getThumbnailDataByMD5(fileMessage.getFileMD5()), "PARAM_IM_MESSAGE_THUMBNAIL_DATA");
        }

        return  base64;
    }

    @Override
    public void cancel(FileMessage fileMessage) {
//        if (fileMessage.isSender()) {
//
//            FileMessage.cancelable = (Callback.Cancelable) MessagingApi.senderHanders.get(fileMessage.getKeyId());
//        }
//
//        if (FileMessage.cancelable != null) {
//            FileMessage.cancelable.cancel();
//            FileMessage.cancelable = null;
//            MessagingApi.senderHanders.remove(fileMessage.getKeyId());
//
//            if (fileMessage.isSender()) {
//
//                imapij.netimSendFileMsg(fileMessage.getConversationId(), fileMessage.getKeyId(), false);
//
//            } else {
//
//                imapij.updateMsgStatus(fileMessage.getConversationId(), fileMessage.getKeyId(), eMessageStatus.C_MESSAGE_STATUS_FILE_RECV_FAILED);
//
//            }
//
//        }
    }

    @Override
    public NetRtcXMPPCallbackImpl getIMCallbackObject() {
        return null;
    }


}
