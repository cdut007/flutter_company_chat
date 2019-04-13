package com.ultralinked.voip.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ezvcard.Ezvcard;
import ezvcard.VCard;

/**
 * Created by yongjun on 2016/04/29.
 */
public class VcardMessage extends FileMessage {

    public static final String TAG = "VcardMessage";
    private String vCardName;
    private String firstName;
    private String number;

    public void setMobileTelList(List<String> mobileTelList) {
        this.mobileTelList = mobileTelList;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setWorkTelList(List<String> workTelList) {
        this.workTelList = workTelList;
    }

    public void setHomeTelList(List<String> homeTelList) {
        this.homeTelList = homeTelList;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    private List<String> mobileTelList = new ArrayList<String>();
    private List<String> workTelList = new ArrayList<String>();
    private List<String> homeTelList = new ArrayList<String>();
    private String familyName;

    @Override
    public void parseData(JSONObject json) throws JSONException {
        super.parseData(json);
        parseVcard(getJsonData());//data
    }

    private  static  final String NAME_TAG="vCardName",NUMBER_TAG="number";

    private void parseVcard(JSONObject data) throws JSONException {

        vCardName = data.optString(NAME_TAG);
        number = data.optString(NUMBER_TAG);


    }

    VCard vCard;

    private void parseVcardFile(){
        vCard = parseVcardFromFile(getFilePath());
        if (vCard!=null){
            if (vCard.getStructuredName()!=null) {
                vCardName = vCard.getStructuredName().getFamily();
            }
            if (vCard.getTelephoneNumbers()!=null&&vCard.getTelephoneNumbers().size()>0){
                number = vCard.getTelephoneNumbers().get(0).getText();
            }

        }

    }

    private static  VCard parseVcardFromFile(String vCardPath){
        try{
            File file = new File(vCardPath);
            VCard vcard = Ezvcard.parse(file).first();
           return  vcard;
        }catch (Exception e){
            e.printStackTrace();
            android.util.Log.i("parseVcard", "parse vcard error = " + e.getLocalizedMessage());
        }
        return  null;
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public int hashCode() {
        return super.hashCode();
    }

    private static final long serialVersionUID = -6121254108866010288L;


    protected static String getTextVcardJson(String vcardPath, String fileName, Options options) {


        try {
            VCard vCard = parseVcardFromFile(vcardPath);
            JSONObject jsonObject = FileMessage.getFileJsonObject(vcardPath,fileName);
            if (vCard.getStructuredName()!=null){
                jsonObject.put(NAME_TAG,vCard.getStructuredName().getFamily());
            }


            if (vCard.getTelephoneNumbers()!=null&&vCard.getTelephoneNumbers().size()>0){
                jsonObject.put(NUMBER_TAG,vCard.getTelephoneNumbers().get(0).getText());
            }


            return getFormatMessageJson(TAG,jsonObject, Message.MESSAGE_TYPE_VCARD, options);
        } catch (Exception e) {
            Log.i(
                    TAG,
                    (new StringBuilder())
                            .append("getTextVcardJson error e:")
                            .append(e.getMessage()).toString());
        }
        return null;
    }


    private String vCardJsonData;




    public String getVcardName() {
    if (vCard == null){
        parseVcardFile();
    }
        return vCardName;
    }

    public String getFirstName() {
        if (vCard == null){
            parseVcardFile();
        }

        return firstName;
    }

    public String getFamilyName() {
        if (vCard == null){
            parseVcardFile();
        }
        return familyName;
    }

    public String getMobileTel() {
        if (vCard == null){
            parseVcardFile();
        }
        if (null != mobileTelList && mobileTelList.size() > 0)
            return (String) mobileTelList.get(0);
        else
            return null;
    }

    public String getHomeTel() {
        if (vCard == null){
            parseVcardFile();
        }
        if (null != homeTelList && homeTelList.size() > 0)
            return  homeTelList.get(0);
        else
            return null;
    }

    public String getWorkTel() {
        if (vCard == null){
            parseVcardFile();
        }
        if (null != workTelList && workTelList.size() > 0)
            return workTelList.get(0);
        else
            return null;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setVcardName(String vName) {
        this.vCardName = vName;
    }

    public String getNumber() {

        return number;
    }

    public List<String> getMobileTelList() {
        if (vCard == null){
            parseVcardFile();
        }
        return mobileTelList;
    }

    public List<String> getHomeTelList() {
        if (vCard == null){
            parseVcardFile();
        }
        return homeTelList;
    }

    public List<String> getWorkTelList() {
        if (vCard == null){
            parseVcardFile();
        }
        return workTelList;
    }

}
