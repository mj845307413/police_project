
public class StringUtil {
	public static void split(final String url, String head, String[] content) {
		String right = "";
		int k = url.length();
		for (int i = 0; i < url.length(); i++) {
			if (url.substring(i, i + 1).equals("@")) {
				head = url.substring(0, i).trim();
				right = url.substring(i + 1, k).trim();
			}
		}
		content = right.split("&");
		System.out.println(head+content);
	}
	
}
