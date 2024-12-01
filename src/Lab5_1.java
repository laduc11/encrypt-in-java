import java.util.Scanner;
import java.io.File;

public class Lab5_1 {
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
        
        if (plaintext == "") {
            File folder = new File("data/plaintext");

            // Check if the folder exists and is a directory
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();

                // Print all files and directories
                for (File file : files) {
                    if (file.isFile()) {
                        System.out.println("File: " + file.getName());
                        DES des = new DES(file.getName(), key, mode, padding_algo, file.getName());
                        long start_time, end_time, elapsed_time;

                        start_time = System.nanoTime();
                        des.encrypt();
                        end_time = System.nanoTime();
                        elapsed_time = end_time - start_time;
                        System.out.println("Encryt time: " + (elapsed_time / 1000000.0) + " ms");
                        
                        start_time = System.nanoTime();
                        des.decrypt();
                        end_time = System.nanoTime();
                        elapsed_time = end_time - start_time;
                        System.out.println("Decryt time: " + (elapsed_time / 1000000.0) + " ms");

                    }
                }
            } else {
                System.out.println("The specified path is not a directory.");
            }
        } else {
            DES des = new DES(plaintext, key, mode, padding_algo);
            long start_time, end_time, elapsed_time;
            
            start_time = System.nanoTime();
            des.encrypt();
            end_time = System.nanoTime();
            elapsed_time = end_time - start_time;
            System.out.println("Encryt time: " + (elapsed_time / 1000000.0) + " ms");
            
            start_time = System.nanoTime();
            des.decrypt();
            end_time = System.nanoTime();
            elapsed_time = end_time - start_time;
            System.out.println("Decryt time: " + (elapsed_time / 1000000.0) + " ms");
        }

        scanner.close();
    }
}
