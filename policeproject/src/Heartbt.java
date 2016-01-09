public interface Heartbt {
	//终端会将警员信息通过心跳报文上报，必须要将表中的信息进行更新，其中更新的信息包括经纬度、警员当前状态
	public void updateDB(String telno, String longtitude, String latitude, int status);

	void updatePD(String telno, String longtitude, String latitude, int status, String pbtelno);
}
