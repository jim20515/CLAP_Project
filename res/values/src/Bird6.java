class Animal6 {
    int aMask = 0x00FF;
    public Animal6() {
    }
    public Animal6(int mask) {
        aMask = mask;
    }
}
public class Bird6 extends Animal6 {
    int bMask = 0xFF00;
    int fullMask;
    public Bird6() {
        // Compiler add super() here
        fullMask = bMask | aMask;
    }
    public Bird6(int mask) {
        /* 若有super,則必須放在第一行,連變數宣告也不能擺在super前面 */
        super(mask);
        fullMask = bMask | aMask;
    }
    public static void main(String[] argv) {
        Bird6 b = new Bird6();
        System.out.println(b.fullMask);
        b = new Bird6(0x0011);
        System.out.println(b.fullMask);
    }
}