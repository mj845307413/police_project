
//����Ա��ص�¼�������Ĳ���
public class PoliceHelper implements LoginHelper, Heartbt {

	@Override
	public int login(String telNo,String password) {
		// TODO Auto-generated method stub
		System.out.println(telNo);
		String sql = "select * from t_pd where telno = ";
		sql += "\'" + telNo + "\'";
		System.out.println(sql);
		//DBHelper db = new DBHelper(sql);
		String result = "";
		String policePassword=DBHelper.retriveAll(sql, result);
		if (password.equals("123456"))
			return Res.LOGIN_SUCCESS;
		else
			return Res.LOGIN_FAILURE;

	}

	@Override
	public void updatePD(String telno, String longtitude, String latitude, int status, String pbtelno) {
		// TODO Auto-generated method stub
		// �����ֻ��Ÿ��¾�Ա�ĵ�����Ϣ
		DBHelper.updatePD(telno, longtitude, latitude, status, pbtelno);
	}
	public void updatePD(int status, String pbtelno) {
		// TODO Auto-generated method stub
		// �����ֻ��Ÿ��¾�Ա�ĵ�����Ϣ
		DBHelper.updatePD(status, pbtelno);
	}

	@Override
	public void updateDB(String telno, String longtitude, String latitude, int status) {
		// TODO Auto-generated method stub
	}
}
