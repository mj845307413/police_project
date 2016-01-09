import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DBHelper {
	public static final String url = "jdbc:mysql://192.168.61.232/pddb";
	public static final String name = "com.mysql.jdbc.Driver";
	public static final String user = "root";
	public static final String password = "123456";

	public Connection conn = null;
	public PreparedStatement pst = null;

	public DBHelper(String sql) {
		try {
			Class.forName(name);// 指定连接类型
			conn = DriverManager.getConnection(url, user, password);// 获取连接
			pst = conn.prepareStatement(sql);// 准备执行语句
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public  void close() {
		try {
			conn.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String retriveAll(String sql, String result) {
		// String sql = null;
		DBHelper db1 = null;
		ResultSet ret = null;
		String policePassword=null;
		System.out.println(sql);
		// sql = "select * from t_pd";// SQL语句
		db1 = new DBHelper(sql);// 创建DBHelper对象//这边可能连接了两次？

		try {
			ret = db1.pst.executeQuery();// 执行语句，得到结果集
			while (ret.next()) {
				String tmp = "";
				String uid = ret.getString(1);
				String utelno = ret.getString(2);
				String ulong = ret.getString(4);
				String ulat = ret.getString(5);
				int status = ret.getInt(6);
				// System.out.println(uid + "\t" + ufname + "\t" + ulname + "\t"
				// + udate);
				tmp += uid;
				tmp += "\t";
				tmp += utelno;
				tmp += "\t";
				tmp += ulong;
				tmp += "\t";
				tmp += ulat;
				tmp += "\t";
				tmp += status;
				System.out.println(tmp);
				result += utelno;
				policePassword=ret.getString(3);
			} // 显示数据
			ret.close();
			db1.close();// 关闭连接
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return policePassword;
	}

	public static void updatePD(String telno, String longtitude, String latitude, int status, String passbytelno) {
		DBHelper db1 = null;
		int ret = 0;

		String sql = "UPDATE t_pd SET longtitude = \'";
		sql += longtitude + "\',";
		sql += "latitude = \'";
		sql += latitude + "\',";
		sql += " status = ";
		sql += status;
		sql += ", pbtelno= \'";
		sql += passbytelno + "\'";
		sql += " where telNo=\'";
		sql += telno+"\'";
		System.out.println(sql);
		db1 = new DBHelper(sql);// 创建DBHelper对象
		try {
			ret = db1.pst.executeUpdate();
			db1.close();// 关闭连接
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}
	public static void updatePD( int status, String passbytelno) {
		DBHelper db1 = null;
		int ret = 0;

		String sql = "UPDATE t_pd SET status = "+status+" where pbtelno = \""+passbytelno+"\"";
		
		System.out.println(sql);
		db1 = new DBHelper(sql);// 创建DBHelper对象
		try {
			ret = db1.pst.executeUpdate();
			db1.close();// 关闭连接
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	// 将路人的信息增加到数据库中
	public static void insertPB(String telno, String longtitude, String latitude, int status) {
		DBHelper db1 = null;
		boolean ret = false;

		String sql = "INSERT INTO t_pb (pbtelno, pblongtitude, pblatitude, pbstatus) VALUES (";
		sql += "\'" + telno + "\',";
		sql += " \'" + longtitude + "\',";
		sql += " \'" + latitude + "\',";
		sql += " " + status + "";
		sql += " )";

		System.out.println(sql);
		db1 = new DBHelper(sql);// 创建DBHelper对象
		try {
			ret = db1.pst.execute();
			//ret.close();
			db1.close();// 关闭连接
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	public static void updatePBTable(String name,int result) {
		// TODO Auto-generated method stub
		DBHelper db1 = null;
		boolean ret = false;

		String sql = "UPDATE t_pb SET pbstatus ="+result+" where pbtelno = ";
		sql += "\'" + name + "\'";
		

		System.out.println(sql);
		db1 = new DBHelper(sql);// 创建DBHelper对象
		try {
			ret = db1.pst.execute();
			db1.close();// 关闭连接
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	public static void updatePDTable(String telNo, String longtitude, String latitude, int status) {
		// TODO Auto-generated method stub

	}

	public static boolean joinQuery(List<PassbyBean> pbList) {
		DBHelper db1 = null;
		ResultSet ret = null;

		String sql = "SELECT t_pb.pbtelno, pblongtitude, pblatitude from t_pb left join t_pd on ";
		sql += ("t_pb.pbtelno = t_pd.pbtelno limit 1");

		System.out.println(sql);
		db1 = new DBHelper(sql);// 创建DBHelper对象

		try {
			ret = db1.pst.executeQuery();
			if(ret.next()) {
				PassbyBean pb = new PassbyBean();

				pb.setTelno(ret.getString(1));
				pb.setLongtitude(ret.getString(2));
				pb.setLatitude(ret.getString(3));
				pbList.add(pb);

				System.out.println("telno= " + ret.getString(1) + " longtitude=" + ret.getString(2) + " latitude= "
							+ ret.getString(3));
				return true;
			} else
				return false;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			try {
				ret.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			db1.close();// 关闭连接
		}
		return false;
	}
	
	public static boolean queryPasserTable(String telno) throws SQLException {
		DBHelper db1 = null;
		ResultSet ret = null;

		String sql = "select * from t_pb where pbtelno = " + telno;
	
		System.out.println(sql);
		db1 = new DBHelper(sql);// 创建DBHelper对象
		try {
			ret = db1.pst.executeQuery();
			if(ret.next())
				return true;			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			ret.close();
			db1.close();// 关闭连接
		}
		return false;
		
	}
}
