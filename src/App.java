import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("This is a program encrypt with DES algorithm");

        System.out.print("Enter filename contain plaintext: ");
        String plaintext = scanner.nextLine();
        
        System.out.print("Enter filename contain key: ");
        String key = scanner.nextLine();

        System.out.println("Modes of cryptographic operation");
        System.out.println("1. Electronic Code Block");
        System.out.println("2. Cipher Block Chaining");
        System.out.print("Enter choosen mode: ");
        int mode = scanner.nextInt();
        
        System.out.println("Padding algorithms:");
        System.out.println("1. PKCS5 Padding");
        System.out.println("2. No Padding");
        System.out.print("Enter choosen algorithm: ");
        int padding_algo = scanner.nextInt();
        
        DES des = new DES(plaintext, key, mode, padding_algo);
        des.encrypt();
        des.decrypt();

        scanner.close();
    }
}
