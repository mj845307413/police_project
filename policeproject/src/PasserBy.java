//路人，角色主要是报警，并且通知事件已经完结
public class PasserBy {
	private String pbTelNo = null;
	private String pbLongtitude = null;
	private String pbLatitude = null;
	private int pbStatus = 0;

	public String getTelNo() {
		return pbTelNo;
	}

	public void setTelNo(String telNo) {
		this.pbTelNo = telNo;
	}

	public String getPbLongtitude() {
		return pbLongtitude;
	}

	public void setPbLongtitude(String pbLongtitude) {
		this.pbLongtitude = pbLongtitude;
	}

	public String getPbLatitude() {
		return pbLatitude;
	}

	public void setPbLatitude(String pbLatitude) {
		this.pbLatitude = pbLatitude;
	}

	public int getPbStatus() {
		return pbStatus;
	}

	public void setPbStatus(int pbStatus) {
		this.pbStatus = pbStatus;
	}

	// 路人的信息必须要增加到数据库中
	public void insertPBTable(String telNo, String pbLongtitude, String pbLatitude, int pbStatus) {
		DBHelper.insertPB(telNo, pbLongtitude, pbLatitude, pbStatus);

	}

	public void updatePBTable(String nameString,int result) {
		DBHelper.updatePBTable(nameString,result);
	}
}
