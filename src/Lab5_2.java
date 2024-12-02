import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Scanner;
import javax.crypto.Cipher;

public class Lab5_2 {
    static private void processFile(Cipher ci,InputStream in,OutputStream out)
    throws javax.crypto.IllegalBlockSizeException,
           javax.crypto.BadPaddingException,
           java.io.IOException
{
    byte[] ibuf = new byte[1024];
    int len;
    while ((len = in.read(ibuf)) != -1) {
        byte[] obuf = ci.update(ibuf, 0, len);
        if ( obuf != null ) out.write(obuf);
    }
    byte[] obuf = ci.doFinal();
    if ( obuf != null ) out.write(obuf);
}

    static public void processFile(Cipher ci, String inFile, String outFile)
    throws javax.crypto.IllegalBlockSizeException, 
    javax.crypto.BadPaddingException, 
    java.io.IOException 
    {
        FileInputStream in = new FileInputStream(inFile);
        FileOutputStream out = new FileOutputStream(outFile);
        processFile(ci, in, out);
    }
    static public PublicKey getPublicKey(String filename)
    throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
        // String keyString = new String(keyBytes);

        // Remove 1st line and last line
        // keyString = keyString.replace("-----BEGIN PUBLIC KEY-----", "");
        // keyString = keyString.replace("-----END PUBLIC KEY-----", "");
        // keyString = keyString.replace("\\s", "");
        X509EncodedKeySpec ks = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return  kf.generatePublic(ks);
    }

    static public PrivateKey getPrivateKey(String filename)
    throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

        // System.out.println(Paths.get(filename).toString());
        
        // String keyString = new String(keyBytes);

        // Remove 1st line and last line
        // keyString = keyString.replace("-----BEGIN PUBLIC KEY-----", "");
        // keyString = keyString.replace("-----END PUBLIC KEY-----", "");
        // keyString = keyString.replace("\\s", "");
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return  kf.generatePrivate(ks);
    }
    static public void doGenkey() throws java.security.NoSuchAlgorithmException, java.io.IOException {
        // Create RSA key pair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        // Save RSA key to file
        // Save private key
        FileOutputStream out1 = new FileOutputStream("data/key/Lab5_2.key");
        out1.write(kp.getPrivate().getEncoded());
        
        // Save public key
        FileOutputStream out2 = new FileOutputStream("data/key/Lab5_2.pub");
        out2.write(kp.getPublic().getEncoded());
        out1.close();
        out2.close();
    }
    
    static public void encryptRSA() throws Exception {
        Scanner scanner = new  Scanner(System.in);
        System.out.println("This is a program encrypt with RSA algorithm.");
        System.out.println("Scanning file Lab05_2.key and Lab05_2.pub at directory ...");
        String pvtFilename = "data/key/Lab5_2.key", pubFilename = "data/key/Lab5_2.pub";
        File    pvtFile = new File(pvtFilename),
                pubFile = new File(pubFilename);
        PublicKey pub;
        // PrivateKey pvt;
        if (pvtFile.exists() && pubFile.exists()) {
            System.out.println("Lab05_2.key and Lab05_2.pub exist");
        }
        else {
            System.out.println("Lab05_2.key and Lab05_2.pub do not exist\nGenerating Key Pair");
            doGenkey();
        }
        // get Key
        pub = getPublicKey(pubFilename);
        // pvt = getPrivateKey(pvtFilename);

        // Encrypt a file using a RSA key
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pub);
        // input file contain plaintext
        System.out.print("Enter filename of plaintext: ");
        String plaintext = scanner.nextLine();
        FileInputStream in = new FileInputStream("data/plaintext/" + plaintext);

        // write to output file
        System.out.print("Enter filename of ciphertext: ");
        String ciphertext = scanner.nextLine();
        FileOutputStream out = new FileOutputStream("data/ciphertext/" + ciphertext);

        // do Final
        processFile(cipher, in, out);
        scanner.close();
        out.close();
    }
    static public void decryptRSA(String[] args)
    throws java.security.NoSuchAlgorithmException,
    java.security.spec.InvalidKeySpecException,
    javax.crypto.NoSuchPaddingException,
    javax.crypto.BadPaddingException,
    java.security.InvalidKeyException,
    javax.crypto.IllegalBlockSizeException,
    java.io.IOException,
            Exception {
        if (args.length != 2) {
            System.err.println("dec Lab05_2.key codetext.");
            System.exit(1);
        }
        int idx = 0;
        String privateKeyFile = args[idx++];
        String codeFile = args[idx];
        PrivateKey pvt = getPrivateKey(privateKeyFile);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, pvt);
        System.out.println(codeFile);
        processFile(cipher, codeFile, "data/decoded/duc.dec");
    }
    
    public static void main(String[] args)
    throws Exception {
        if ( args.length == 0 ) {
	    System.err.print("usage: java RSA command params..\n");
	    System.exit(1);
		}

	int index = 0;
	String command = args[index++];
	String[] params = Arrays.copyOfRange(args, index, args.length);
    if ( command.equals("encryptRSA")) encryptRSA();
    else if (command.equals("decryptRSA")) decryptRSA(params);
    else throw new Exception("Unknown command: " + command);
    }
}
