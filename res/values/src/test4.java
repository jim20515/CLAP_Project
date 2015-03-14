class Animal {
    public String moveMethod() {
        return "Unspecified";
    }
}
class Bird extends Animal {
    public String moveMethod() {
        return "Fly";
    }
}
class Dog extends Animal {
    public String moveMethod() {
        return "run";
    }
}
class Fish extends Animal {
    public String moveMethod() {
        return "swim";
    }
}
public class test4 {

	public static void main(String[] args) {
		Animal a1, a2, a3, a4;
	    Bird b;
	    Dog d;
	    Fish f;
	    
	    Animal a5 = new Bird();  //¦h«¬
	    //System.out.println(a5.moveMethod()); fly
	    
	    
	    a2 = a1 = new Animal();
	    b = new Bird();
	    d = new Dog();
	    f = new Fish();
	   //System.out.println(a1.moveMethod());
	   //System.out.println(b.moveMethod());
	   //System.out.println(d.moveMethod());
	   //System.out.println(f.moveMethod());
	    
	    //System.out.println(a5.moveMethod()); //fly
	    //System.out.println(a1.moveMethod()); //unspecified
	    
	    a1 = b; // Correct, we call this upcasting
	    //System.out.println(a1.moveMethod()); //fly
	    
	    //b = a1; // Compile Error, type not compatible
	    b = (Bird)a1; // downcasting, Compile Correct
	    //System.out.println(b.moveMethod());  //fly
	    
	    a2 = b; // Correct,we call this upcasting
	    //System.out.println(a2.moveMethod()); //fly
	    
	    //d = a2; // Compile Error, type not compatible,  Bird cannot be cast to Dog
	    //d = (Dog)a2; // Compile Correct, but runtime error
	    
	    //System.out.println(a1 instanceof Bird);
	}

}
