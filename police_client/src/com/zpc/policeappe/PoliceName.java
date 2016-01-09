package com.zpc.policeappe;

public class PoliceName {
	private PoliceName(){}
	private static PoliceName instance;
	public static PoliceName getInstance() {
		if (instance==null) {
			instance=new PoliceName();
		}
		return instance;
	}
	private String policeName;

	public String getPoliceName() {
		return policeName;
	}

	public void setPoliceName(String policeName) {
		this.policeName = policeName;
	}

}
