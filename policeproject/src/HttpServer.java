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
			//��Ա���Ƚ��е�¼���ն˽���Ա���ֻ��ŷ��͵��������������ѯ���ݿ����У����Ƿ��سɹ�������ʧ��
			if (head.equals("policelogin")) {
				PoliceHelper police = new PoliceHelper();
				System.out.println(content[0].toString());
				String[] login = content[0].split("=");
				System.out.println(login[1].toString());				
				ret = police.login(login[1].toString(),"123456");
				if(ret == Res.LOGIN_SUCCESS)
					response += "success";
				
			} else if (head.equals("policeheartbt")) {
				// ��Ա�������������������λ����Ϣ
				PoliceHelper police = new PoliceHelper();
				// ����֮ǰ����Ҫ��ѯ���ݿ⣬��ʱ�Ƿ���Ҫ������Ҳ���ǽ��û��ĺ��뷢�͸���Ա����
				police.updatePD(content[0].split("=")[1].toString(), content[1].split("=")[1].toString(),
						content[2].split("=")[1].toString(), Integer.parseInt(content[3].split("=")[1].toString(), 16),
						null);
				// ����������Ӧ����Ҫ�������˵ĵ绰�;�γ�ȸ�֪������
				List<PassbyBean> pbList = new ArrayList<PassbyBean>();
				if (DBHelper.joinQuery(pbList)) {
					// ��list�������˵ĵ绰����γ��֪ͨ����Ա
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
				// ·���ϱ�������Ҫ��·�˵��ֻ��ź͵���λ����Ϣ�������ݿ⣬����Ҳ��Ҫ������������������ݿ�
				PasserBy pb = new PasserBy();
				CalDistance cal = new CalDistance();
				// �˱��Ļ��ظ��ϱ���Ϊ���ظ��û��������ݿ��н��в�ѯ
				boolean bRet = false;
				bRet = DBHelper.queryPasserTable(content[0].split("=")[1].toString());
				if (!bRet)
					pb.insertPBTable(content[0].split("=")[1].toString(), content[1].split("=")[1].toString(),
							content[2].split("=")[1].toString(),
							Integer.parseInt(content[3].split("=")[1].toString(), 16));
				else
					pb.updatePBTable(content[0].split("=")[1].toString(),
							Integer.parseInt(content[3].split("=")[1].toString()));
				// ���û��ϱ��ĵ���λ����Ϣ�����ݿ��о�Ա�ĵ���λ�ý��м��㣬��������ľ�Ա���볤��֪ͨ���û�
				Map<String, Double> onPolice = new HashMap<String, Double>();
				bRet = cal.findPolice(content[0].split("=")[1].toString(), content[1].split("=")[1].toString(),
						content[2].split("=")[1].toString(), onPolice);
				// ��map�еľ�Ա�ĵ绰������뱨���˵ľ�����͸�������
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
				// ��������Ѿ�������ɣ�·�˽�������״̬�ϱ����״̬
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