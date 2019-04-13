package com.ultralinked.voip.api;


import java.io.File;
import java.util.List;

/**
 * Created by mac on 17/1/4.
 */

public interface IMessageConvertFactory {
    String im_version();

    void deleteAllConversationMessages();

    List<Conversation> getConversations(int begin, int end);

    List<Conversation> getAllConversationsByType(int chatType);

    List<Conversation> getAllConversationsByFlag(int propsType, boolean hasFlag);

    List<Message> getMessages(int conversationId, int begin, int end);

    void deleteOneMessage(int conversationId, int id);

    Message getMessageById(int id, boolean isGroup);

    void deleteAllMessages(int conversationId);

    void deleteMultipleMessages(int conversationId, int[] ids, int length);

    CustomMessage sendCustomFileMessage(String chatId, String jsonData, int convsationType, File file, boolean needcopy);

    void sendCustomBroadcast(String chatId, String jsonData, int conversationType);

    Message updateMessageBody(int keyId, int chatType, String body);

    CustomMessage insertCustomMessage(String chatId, String from, String to, String jsonData, int conversationType);

    CustomMessage sendCustomMessage(String chatId, String jsonData, int convsationType);

    void exitGroup(int conversationId, String groupId);

    void createGroup(String groupTopic);

    void modifyTitle(String groupId, String groupTopic);

    void deleteConversation(int conversationId);

    void checkGroupMember(String groupId);

    int createConversation(String chatId, boolean isGroup);

    int setConversationDraft(int converastionId, byte[] bytes);

    void setConversationPriority(int conversationId, boolean isTopUp);

    void sendAppToken(String token, String android);

    void setConversationMute(String chatId, boolean isGroup, boolean isSetMute);

    void setConversationProperty(String chatId, boolean isGroup, String properties);

    String getConversationProperties(String chatId, boolean isGroup);

    void reSendMsg(int conversationId, int keyId, boolean isFileMsg);

    void flushLog();

    void kickMember(String groupID, String memberName);

    void setConversationBlock(String chatId, boolean isGroup, boolean isSetBlock);

    boolean getConversationIsBlock(String chatId);

    boolean getConversationIsMute(String chatId);

    GroupConversation getConversationByGroupId(String groupId);

    List<Message> getMessagesByMessageId(int conversationId, int msgId, int count, boolean isBeforeMsg, boolean containMsg);

    void setConfig(String im_flag, String value);

    Conversation createConversationWithFlag(String contactNumber, int conversationFlag);

    List<GroupMember> getGroupMemembers(String groupId, String adminId);

    void JoinGroup(String groupId);

    void inviteToGroup(String groupId, String member);

    int getAllUnreadMessageCounts();

    int getAllUnreadMessageCountsByConvType(boolean isGroup);

    int getConversationUnreadMessageCounts(int conversationId);

    void conversationRead(int conversationId);

    void messageRead(int conversationId, int msgId);

    void uploadFile(final File sendingFile, final FileMessage fileMessage);

    Conversation getConversationById(int convId);

    List<Message> searchMessageListOfConversationByKeyword(Conversation conversation, String searchText);

    List<Conversation> searchConversations(String searchText);

  //  void uploadThumbFile(final File sendingFile, final FileMessage fileMessage, final Callback.ProgressCallback<String> callbackListener);

    void setIMCallbackObject(Object object);

    void FileUrlCallback(Message msg);

    void updateMsgStatus(int conversationId, int msgId, int msgStatus);

    void LoginXmpp(MLoginApi.Account currentAccount);

    boolean isLogin();

    int relogin();

    void disconnect();


    void accept(FileMessage fileMessage);

    void reject(FileMessage fileMessage);

    String getPreviewImageBase64(FileMessage fileMessage);

    void cancel(FileMessage fileMessage);

    NetRtcXMPPCallbackImpl getIMCallbackObject();
}
