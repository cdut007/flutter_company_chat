package com.ultralinked.voip.api;

import java.io.Serializable;

public class GroupMember implements Serializable {


	private static final long serialVersionUID = 1L;

	private String groupID;

	private String memberName;

	private String memberId;

	private Message.PeerInfo peerInfo;
	/**
	 * Get the group member name
 	 * @return group member name
	 */
	public String getMemberName() {
		return memberName;
	}

	/**
	 * Set the group member name
	 * @param memberName
	 */
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	/**
	 * Get the group name
	 * @return  groupID
	 */
	public String getGroupId() {
		return groupID;
	}

	/**
	 * Set group name
	 * @param groupName
	 */
	public void setGroupId(String groupName) {
		this.groupID = groupName;
	}


	public Message.PeerInfo getPeerInfo() {
		return peerInfo;
	}

	public void setPeerInfo(Message.PeerInfo peerInfo) {
		this.peerInfo = peerInfo;
	}

	public String getMemberId() {

		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
}
