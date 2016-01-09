import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*Calulate the suspect distance from police in database*/
public class CalDistance {

	// ����ǰ����ľ�γ�������ݿ�ֱ���бȽϣ����ھ������ֶ��������Ƚ�����
	// �Ȳ�ѯ��ǰ���õľ�Ա
	public boolean putResultIntoList(List<PoliceBean> pList) throws SQLException {
		// ������ѯ�Ƿ��п��õ����ݿ�
		String sql = "select * from t_pd where status = 1";
		DBHelper db1 = null;
		ResultSet ret = null;
		double[] min = { 0.0 };
		int i = 0;

		db1 = new DBHelper(sql);// ����DBHelper����

		try {
			ret = db1.pst.executeQuery();// ִ����䣬�õ������
			if (ret != null) {
				while (ret.next()) {
					PoliceBean pb = new PoliceBean();

					pb.setTelno(ret.getString(2));
					pb.setLongtitude(ret.getString(4));
					pb.setLatitude(ret.getString(5));
					pb.setStatus(ret.getInt(6));
					pList.add(pb);

					System.out.println("telno= " + ret.getString(2) + " longtitude=" + ret.getString(4) + " latitude= "
							+ ret.getString(5) + "status= " + ret.getInt(6));

				}
				return true;
			} else
				return false;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ret.close();
			db1.close();// �ر�����
		}
		return false;
	}

	// �������ǰ���Գ����ľ�Ա���û��ϱ��ľ�γ������ľ���
	public double cal(String longtitude1, String latitude1, String longtitude, String latitude) throws SQLException {
		double longMinus = Double.parseDouble(longtitude1) - Double.parseDouble(longtitude);
		double latMinus = Double.parseDouble(latitude1) - Double.parseDouble(latitude);
		return (longMinus * longMinus + latMinus * latMinus);

	}

	public int findMin(double[] array) {
		int i = 0;
		int a = 0;
		double min = array[0];
		for (; i < array.length; i++) {
			if (min > array[i])
				{min = array[i];
				a=i;
						}
		}
		return a;
	}

	public boolean findPolice(String passerbyTelNo, String longtitude, String latitude, Map<String, Double> onPolice)
			throws SQLException {
		List<PoliceBean> plist = new ArrayList<PoliceBean>();
		putResultIntoList(plist);
		double[] min = new double[plist.size()];
		int index = 0;
		for (int i = 0; i < plist.size(); i++) {
			min[i] = cal(longtitude, latitude, plist.get(i).getLongtitude(), plist.get(i).getLatitude());
		}
		index = findMin(min);
		System.out.println("" + index);
		int status = Res.ONBUSINESS;
		DBHelper.updatePD(plist.get(index).getTelno(), plist.get(index).getLongtitude(), plist.get(index).getLatitude(),
				status, passerbyTelNo);
		onPolice.put(plist.get(index).getTelno(), min[index]);
		return true;
	}
}
