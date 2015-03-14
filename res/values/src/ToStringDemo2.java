class Person {
    public String name;
    public Address address;
   

    public String toString() {
        return String.format("Person[name: %s, %s]", name, address);
    }
}

class Address {
	public String street;
	public String city;
   
    public String toString() {
        return String.format("Address[street: %s, city: %s]", street, city);
    }
}

public class ToStringDemo2 {

	public static void main(String args[]) {
		Person person = new Person();
		person.name = "test";
		
		
		Address addr = new Address();
		person.address = addr;
		
		
		addr.street = "test";
		addr.city = "TP";
		
		
		String personString = person.toString();

		System.out.println(person);
		System.out.println(personString);



	}
}
