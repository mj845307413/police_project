
//警员登录，也就是使用警员的手机号在后台数据库进行查询，如果可以查询到那么就是成功，否则就是失败
public interface LoginHelper {
	public int login(String telNo,String password);
	
}
