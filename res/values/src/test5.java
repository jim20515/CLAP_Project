class Shape2D { // define super class
    public double area() { // all Shape2D have their own area
        return 0;
    }
}
class Rectangle extends Shape2D {
    private double length, width;
    public Rectangle(double l, double w) { // define constructor
        length = l;
        width = w;
    }
    public double area() { // Override
        return length * width;
    }
}
class Circle extends Shape2D {
    private double radius;
    public Circle(double r) {
        radius = r;
    }
    public double area() { // Override
        return 3.141592654 * radius * radius;
    }
}
class Parallelogram extends Shape2D {
    private double top, bottom, height;
    public Parallelogram(double t, double b, double h) {
        top = t;
        bottom = b;
        height = h;
    }
    public double area() { // Override
        return (top + bottom) * height / 2.0;
    }
}
public class test5 {
    public static double sum(Shape2D[] shapes) {
        double total = 0;
        for (int i = 0; i < shapes.length; i++) {
            total += shapes[i].area(); // use Virtual Function to calculate area of Shape2D
                                       // Without Virtual Function, value of Shape2D.area() will be 0
        }
        return total;
    }
    public static void main(String[] argv) {
        Shape2D[] data; // array of reference to Shape2D
        data = new Shape2D[5]; // create array object
        data[0] = new Rectangle(2.4, 3.8); // Polymorphism
        data[1] = new Circle(3.9);
        data[2] = new Parallelogram(3.5, 6.7, 10.2);
        data[3] = new Rectangle(5.3, 7.2);
        data[4] = new Circle(4.6);
        System.out.println("Sum of all Shape2D is "+sum(data));
     }
}