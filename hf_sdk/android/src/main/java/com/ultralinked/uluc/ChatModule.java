package com.ultralinked.uluc;

import android.util.Log;

import com.ultralinked.voip.api.Conversation;
import com.ultralinked.voip.api.LoginApi;
import com.ultralinked.voip.api.MLoginApi;
import com.ultralinked.voip.api.Message;
import com.ultralinked.voip.api.MessagingApi;

import java.util.List;

public class ChatModule {

    private static  String TAG = "ChatModule";

    public static void Auth() {
        if (!LoginApi.isLogin() || !LoginApi.isConnecting()) {
//                call.argument()
//                LoginApi.login(userID, password);
        } else {
            Log.i(TAG, "LoginApi.isLogin()-->true");
        }

        if (!MLoginApi.isLogin() && !MLoginApi.isConnecting) {
            MLoginApi.Account account1 = new MLoginApi.Account();
//                    account1.password = SPUtil.getToken();
//                    account1.userName = SPUtil.getUsername();
//                    account1.nickName = SPUtil.getNickname();
//                    account1.id = SPUtil.getUserID();

            Log.i(TAG, "IM grLogin status " + account1.toString());

            MLoginApi.login(account1);
        } else {
            Log.i(TAG, "MLoginApi.isLogin()-->true");
        }
    }


    public static List<Message> getMessageListWithFront(int msgId, int count) {
        return MessagingApi.getMessagesByMessageId(msgId, msgId, count, true, true);
    }


    public static void sendTextMessage() {
      //  MessagingApi.sendText(this.getContactNumber(), text, 1, options);
    }
}
