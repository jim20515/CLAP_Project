public class OverwriteToString {
	private String companyName;
	private String companyAddress;
	public OverwriteToString(String companyName, String companyAddress) {
		this.companyName = companyName;
		this.companyAddress = companyAddress;
	}
	public static void main(String[] args) {
		OverwriteToString test = new OverwriteToString("ABC private Ltd","10, yy Street, CC Town");
		System.out.println(test);
	}
 
	public String toString() {
		return ("Company Name: " + companyName + "n" +
		"Company Address: " + companyAddress);
	}
}