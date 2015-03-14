
class p {
	
	String author;
	String nameOfBook;

	p(String a, String b){
		author = a;
		nameOfBook = b;
	}
	
	@Override
	public int hashCode() {
		System.out.println("hash=" + author.hashCode());
	    final int prime = 31;
	    int result = 1;
	   
	    result = prime * result + ((author == null) ? 0 : author.hashCode());
	    result = prime * result + ((nameOfBook == null) ? 0 : nameOfBook.hashCode());
	    return result;
	        
	    //builder.append(this.author);
	    //builder.append(this.nameOfBook);
	    //return builder.toHashCode();
	}
}
public class override1 {


	public static void main(String[] args) {
		
		p p1 = new p("a","b");
		p p2 = new p("a","b");
		p p3;
		p3 = p1;
		
		System.out.println(p2 == p3);
		
		System.out.println(p2.hashCode());
		System.out.println(p3.hashCode());
		System.out.println(p2);
	
	}
}
