package com.ultralinked.voip.api;

import java.io.Serializable;

public class Contact implements Serializable {

    private int id;

    private String name;

    private String status;

    private String headImagePath;

    private String nickName;

    private String mobilePhone;

    private String workPhone;

    private String homePhone;

    private String otherPhone;

    private String url;

    private String email;


    /**
     * Get email address of this contact
     * @return contact email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set email address of this contact
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the url of this contact
     * @return contact url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set url of this contact
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Get other phone of this contact
     * @return contact other phone
     */
    public String getOtherPhone() {
        return otherPhone;
    }

    /**
     * Set other phone of this contact
     * @param otherPhone
     */
    public void setOtherPhone(String otherPhone) {
        this.otherPhone = otherPhone;
    }

    /**
     * Get nickname of this contact
     * @return contact nickname
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * Set nickname of this contact
     * @param nickName
     */

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * Get mobile phone of this contact
     * @return contact mobile phone
     */

    public String getMobilePhone() {
        return mobilePhone;
    }

    /**
     * Set mobile phone of this contact
     * @param mobilePhone
     */
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    /**
     * Get work phone of this contact
     * @return contact work phone
     */
    public String getWorkPhone() {
        return workPhone;
    }

    /**
     * Set work phone of this contact
     * @param workPhone
     */
    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    /**
     * Get home phone of this contact
     * @return contact hom phone
     */
    public String getHomePhone() {
        return homePhone;
    }

    /**
     * Set home phone of this contact
     * @param homePhone
     */
    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    /**
     * Get id of this contact
     * @return contact id
     */
    public int getId() {
        return id;
    }

    /**
     * Set id of this contact
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get name of this  contact
     * @return contact name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of this contact
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get status of this contact
     * @return contact status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set status of this contact
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get image path of this contact
     * @return contact head image path
     */
    public String getHeadImagePath() {
        return headImagePath;
    }

    /**
     * Set head image path of this contact
     * @param headImagePath
     */
    public void setHeadImagePath(String headImagePath) {
        this.headImagePath = headImagePath;
    }
}
