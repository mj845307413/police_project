import java.util.List;
import java.util.Map;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class HttpServer {
	/**
	 * @param args
	 * @throws IOException
	 */
	private static final String IP = "192.168.50.216";
	private static final int PORT = 8008;	
	
	public static void main(String[] args) throws IOException, SQLException {
		// TODO Auto-generated method stub

		InetSocketAddress isa = new InetSocketAddress(IP, PORT);

		ServerSocket server = new ServerSocket();

		server.bind(isa);
		System.out.println("isBound: " + server.isBound());
		System.out.println("SocketAddress: " + server.getLocalSocketAddress());

		//new Thread(new WorkingServer(server)).start();

		while (true) {
			Socket client = server.accept();
			System.out.println("this main thread");

			InputStream in = client.getInputStream();

			InputStreamReader read = new InputStreamReader(new BufferedInputStream(in));

			OutputStream out = client.getOutputStream();

			OutputStreamWriter write = new OutputStreamWriter(new BufferedOutputStream(out));

			char[] cbuf = new char[1024];
			Arrays.fill(cbuf, '\0');
			int len = read.read(cbuf, 0, 1024);
			System.out.println("len"+len);
			StringBuilder sb = new StringBuilder(1024);
			sb.append(cbuf, 0, len);
			System.out.println(sb.toString());

			String head = null;
			String[] content = null;
			String response = "";
			String right = "";
			double distance = 0.0;
			int ret = 0xFFFFF;
			String url = sb.toString();
			int k = url.length();
			for (int i = 0; i < url.length(); i++) {
				if (url.substring(i, i + 1).equals("@")) {
					head = url.substring(0, i).trim();
					right = url.substring(i + 1, k).trim();
				}
			}
			content = right.split("&");
			//StringUtil.split(sb.toString(), head, content);
			System.out.println(head);
			//警员首先进行登录，终端将警员的手机号发送到服务器，如果查询数据库中有，就是返回成功，否则失败
			if (head.equals("policelogin")) {
				PoliceHelper police = new PoliceHelper();
				System.out.println(content[0].toString());
				String[] login = content[0].split("=");
				System.out.println(login[1].toString());				
				ret = police.login(login[1].toString(),"123456");
				if(ret == Res.LOGIN_SUCCESS)
					response += "success";
				
			} else if (head.equals("policeheartbt")) {
				// 警员发送心跳，更新其地理位置信息
				PoliceHelper police = new PoliceHelper();
				// 更新之前，需要查询数据库，此时是否需要出警，也就是将用户的号码发送给警员？？
				police.updatePD(content[0].split("=")[1].toString(), content[1].split("=")[1].toString(),
						content[2].split("=")[1].toString(), Integer.parseInt(content[3].split("=")[1].toString(), 16),
						null);
				// 警察心跳响应中需要将报警人的电话和经纬度告知给警察
				List<PassbyBean> pbList = new ArrayList<PassbyBean>();
				if (DBHelper.joinQuery(pbList)) {
					// 从list将报警人的电话、经纬度通知给警员
					for (int i = 0; i < pbList.size(); i++) {
						response += pbList.get(i).getTelno();
						response += "&";
						response += pbList.get(i).getLongtitude();
						response += "&";
						response += pbList.get(i).getLatitude();
					}
				} else {
					response = "";
				}

			} else if (head.equals("passerbyreport")) {
				// 路人上报案件，要将路人的手机号和地理位置信息存入数据库，另外也需要将案件的种类存入数据库
				PasserBy pb = new PasserBy();
				CalDistance cal = new CalDistance();
				// 此报文会重复上报，为了重复用户插入数据库中进行查询
				boolean bRet = false;
				bRet = DBHelper.queryPasserTable(content[0].split("=")[1].toString());
				if (!bRet)
					pb.insertPBTable(content[0].split("=")[1].toString(), content[1].split("=")[1].toString(),
							content[2].split("=")[1].toString(),
							Integer.parseInt(content[3].split("=")[1].toString(), 16));
				else
					pb.updatePBTable(content[0].split("=")[1].toString(),
							Integer.parseInt(content[3].split("=")[1].toString()));
				// 将用户上报的地理位置信息与数据库中警员的地理位置进行计算，将离最近的警员距离长度通知给用户
				Map<String, Double> onPolice = new HashMap<String, Double>();
				bRet = cal.findPolice(content[0].split("=")[1].toString(), content[1].split("=")[1].toString(),
						content[2].split("=")[1].toString(), onPolice);
				// 将map中的警员的电话号码和与报警人的距离回送给报警人
				if (bRet) {
					for (String s : onPolice.keySet()) {
						response += s;
					}
					response += "&";
					for (Double d : onPolice.values()) {

						response += d;
					}
				} else
					response = "";
				// response +=
				// SelectPolice.getInstance().getSelectPoliceName()+"&"+distance;
			} else if (head.equals("passerbyupdate")) {
				// 如果案件已经处理完成，路人将案件的状态上报完成状态
				PasserBy pb = new PasserBy();
				PoliceHelper pd=new PoliceHelper();
				pb.updatePBTable(content[0].split("=")[1].toString(),
						Integer.parseInt(content[1].split("=")[1].toString(), 16));
				pd.updatePD(1, content[0].split("=")[1].toString());
				response += Res.OK;
			} else {
				
			}
			
			System.out.println(response);
			write.write(response);
			write.flush();			
			
			read.close();
			client.close();
			write.close();

		}
	}
}