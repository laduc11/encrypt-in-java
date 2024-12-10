import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Lab5_3 {
    private static final String KEY_PATH = "data/key/";

    // MESSAGE FILE SAVE IN PLAINTEXT_PATH
    // SIGNATURE FILE SAVE IN CIPHER_PATH
    // KEY FILE SAVE IN KEY_PATH

    static public void sign(String messageFile, PrivateKey pvt)
    throws Exception{
        byte[] data = Files.readAllBytes(Paths.get(Lab5_2.PLAINTEXT_PATH + messageFile));
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(pvt);
        signature.update(data);
        byte[] digitalSignature = signature.sign();
        System.out.println("Signature length: " + digitalSignature.length);
        try (FileOutputStream out = new FileOutputStream(Lab5_2.CIPHER_PATH + messageFile.replace("txt", "dat"))) {
            out.write(digitalSignature);
            out.write(data);
        }
        System.out.println("Digital signature saved to file: " + Lab5_2.CIPHER_PATH + messageFile.replace("txt", "dat"));
    }
    
    static public void verifySignature(PublicKey pub) throws Exception{
        // Get digital signature file
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter name of digital signature file:");
        String dsFile = Lab5_2.CIPHER_PATH + scanner.nextLine();
        // Load Signature
        byte[] loadedSignature = new byte[Lab5_2.RSA_KEY_SIZE/8];
        // Read digital sign from file
        try (FileInputStream in = new FileInputStream(dsFile)) {
            in.read(loadedSignature);
            byte[] msg = in.readAllBytes();

            // Verify digital signature
            Signature verifier = Signature.getInstance("SHA1withRSA");
            verifier.initVerify(pub);
            verifier.update(msg);
            boolean verify = verifier.verify(loadedSignature); // verify sign
            System.out.println("The signature verified result:\t" + verify);
            scanner.close();
        }
    }
    
    
    static public void signAndEncrypt() 
    throws Exception {
        Scanner scanner = new Scanner(System.in);
        // Gain msg file
        System.out.println("Enter name of Message file:");
        String msgFile = scanner.nextLine();
        // Gain Private Key
        System.out.println("Enter name of PrivateKey File:");
        PrivateKey pvt = Lab5_2.getPrivateKey(scanner.nextLine());
        sign(msgFile, pvt);
        // Gain SecretKey
        System.out.println("Enter name of ShareKey File:");
        String secretKeyFile = scanner.nextLine();
        byte[] bytes = new byte[8];
        try (FileInputStream fileInputStream = new FileInputStream(Lab5_2.KEY_PATH + secretKeyFile)) {
            bytes = fileInputStream.readNBytes(8);
        } catch(IOException e) {
            e.printStackTrace();
            System.err.println("Error in get first 64 bit of SecretKey file");
        }
        SecretKeySpec secretKey = new SecretKeySpec(bytes,"DES");
        // Encrypt the signature using DES
        Cipher ci = Cipher.getInstance("DES/ECB/PKCS5Padding");
        ci.init(Cipher.ENCRYPT_MODE, secretKey);
        String inFile = msgFile.replace("txt", "dat");
        String outFile = msgFile.replace("txt", "enc");
        Lab5_2.processFile(ci, Lab5_2.CIPHER_PATH + inFile, Lab5_2.CIPHER_PATH + outFile);
        scanner.close();
    }

    static public void verifyAndDecrypt() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter name of Encrypt msg file:");
        String encFile = scanner.nextLine();
        // Gain Private Key
        System.out.println("Enter name of PublicKey File:");
        PublicKey pub = Lab5_2.getPublicKey(scanner.nextLine());
        // Decrypt DES
        System.out.println("Enter name of ShareKey File:");
        String secretKeyFile = scanner.nextLine();
        byte[] bytes = new byte[8];
        try (FileInputStream fileInputStream = new FileInputStream(Lab5_2.KEY_PATH + secretKeyFile)) {
            bytes = fileInputStream.readNBytes(8);
        } catch(IOException e) {
            System.err.println("Error in get first 64 bytes of SecretKey file");
        };
        SecretKeySpec secretKey = new SecretKeySpec(bytes,"DES");
        // Encrypt the signature using DES
        Cipher ci = Cipher.getInstance("DES/ECB/PKCS5Padding");
        ci.init(Cipher.DECRYPT_MODE, secretKey);
        // try (   FileInputStream in = new FileInputStream(Lab5_2.CIPHER_PATH + encFile);
        //         FileOutputStream out = new FileOutputStream(Lab5_2.CIPHER_PATH + encFile.replace("enc", "dec"));) {
        //             Lab5_2.processFile(ci, in, out);
        //         }
        Lab5_2.processFile(ci, Lab5_2.CIPHER_PATH + encFile, Lab5_2.DECODE_PATH + encFile.replace("enc", "vrfdec"));
        verifySignature(pub);
        scanner.close();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, Exception {
        // check key in key directory
        Scanner scanner = new  Scanner(System.in);
        System.out.println("This is a program encrypt/decrypt with SHA1withRSA algorithm.");
        System.out.println("Scanning file Lab05_2.key and Lab05_2.pub at directory ...");
        String pvtFilename = KEY_PATH + "Lab5_2.key", pubFilename = KEY_PATH + "Lab5_2.pub", secretKey = KEY_PATH + "des.key";
        File    pvtFile = new File(pvtFilename),
                scrFile = new File(secretKey),
                pubFile = new File(pubFilename);
        if (scrFile.exists()) {
            System.out.println("DES key exist");
        }
        else {
            KeyGenerator kg = KeyGenerator.getInstance("DES");
            kg.init(56);
            SecretKey scrKey = kg.generateKey();
            FileOutputStream out = new FileOutputStream(Lab5_2.KEY_PATH + "des.key");
            out.write(scrKey.getEncoded());
            out.close();
        } 
        if (pvtFile.exists() && pubFile.exists()) {
            System.out.println("Lab05_2.key and Lab05_2.pub exist");
        }
        else {
            System.out.println("Lab05_2.key and Lab05_2.pub do not exist\nGenerating Key Pair");
            Lab5_2.doGenkey();
        }
        // pub = Lab5_2.getPublicKey(pubFilename);
        String fun = args[0];
        switch (fun) {
            case "encrypt":
                signAndEncrypt();
                break;
            case "decrypt":
                verifyAndDecrypt();
                break;
            default:
                System.out.println("Invalid command, choose encrypt or decrypt.");
                break;
        }
        scanner.close();
    }
}
