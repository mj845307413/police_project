
public class SelectPolice {
	private SelectPolice (){}
	private static SelectPolice instance;
	public static SelectPolice getInstance() {
		if (instance==null) {
			instance=new SelectPolice();
		}
		return instance;
	}
	private String selectPoliceName;
	public String getSelectPoliceName() {
		return selectPoliceName;
	}
	public void setSelectPoliceName(String selectPoliceName) {
		this.selectPoliceName = selectPoliceName;
	}
}
