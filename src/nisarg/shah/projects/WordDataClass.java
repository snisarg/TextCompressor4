package nisarg.shah.projects;

public class WordDataClass {
	boolean toUse;
	String key, value;
	
	public WordDataClass(boolean toUse, String key, String value) {
		super();
		this.toUse = toUse;
		this.key = key;
		this.value = value;
		System.out.println("WordDataClass: "+toUse+" "+key+" "+value);
	}
}
