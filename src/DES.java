import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DES {
    private String plaintext;
    private String key_filename;
    private String output_filename;
    private String transformation;
    private int mode;
    private int padding_algo;

    public DES(String plaintext, String key_filename, int mode, int padding_algo, String output) {
        this.plaintext = plaintext;
        this.key_filename = key_filename;
        this.output_filename = output;
        this.mode = mode;
        this.padding_algo = padding_algo;

        // Get transformation
        this.transformation = "DES/";
        if (mode == Common.ECB) {
            transformation += "ECB/";
        } else if (mode == Common.CBC) {
            transformation += "CBC/";
        } else {
            throw new ExceptionInInitializerError("Mode invalid");
        }

        if (padding_algo == Common.PKCS5Padding) {
            transformation += "PKCS5Padding";
        } else if (padding_algo == Common.NoPadding) {
            transformation += "NoPadding";
        } else {
            throw new ExceptionInInitializerError("Padding algorithm invalid");
        }
    }

    public DES(String plaintext, String key_filename, int mode, int padding_algo) {
        this(plaintext, key_filename, mode, padding_algo, "output");
    }

    public void encrypt() {
        String basename;
        // Get basename without file extension
        if (this.plaintext.contains(".")) {
            basename = this.plaintext.substring(0, this.plaintext.lastIndexOf('.'));
        } else {
            basename = this.plaintext;
        }
        System.out.printf("basename: %s\n", basename);
        String path2file = "data/plaintext/" + basename + ".txt";
        String ouput_file = "data/ciphertext/" + this.output_filename + ".enc";

        try {
            File desFile = new File(ouput_file);
            FileInputStream fis;
            FileOutputStream fos;
            FileInputStream key_file;
            CipherInputStream cis;

            // Open the Plaintext file
            try {
                fis = new FileInputStream(path2file);
                
                // Define an Initialization Vector (IV)
                byte[] iv = new byte[Common.DES_LENGTH];
                IvParameterSpec ivSpec = new IvParameterSpec(iv);

                // Create a Secret key
                key_file = new FileInputStream("data/key/" + this.key_filename);
                byte key[] = key_file.readNBytes(Common.DES_LENGTH);
                SecretKeySpec secretKey = new SecretKeySpec(key, "DES");

                // Create Cipher object
                Cipher encrypt = Cipher.getInstance(this.transformation);
                if (this.mode == Common.ECB) {
                    encrypt.init(Cipher.ENCRYPT_MODE, secretKey);
                } else if (this.mode == Common.CBC) {
                    encrypt.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
                }
                
                cis = new CipherInputStream(fis, encrypt);
                
                // Write to the Encrypted file
                fos = new FileOutputStream(desFile);
                byte[] b = new byte[8];
                int i = cis.read(b);
                while (i != -1) {
                    fos.write(b, 0, i);
                    i = cis.read(b);
                }
                fos.flush();
                fos.close();
                cis.close();
                fis.close();
                key_file.close();
            } catch (IOException err) {
                System.out.println("Cannot open file!");
                System.exit(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void decrypt() {
        try {
            File desFile = new File("data/ciphertext/" + this.output_filename + ".enc");
            File desFileBis = new File("data/decoded/" + this.output_filename + ".dec");
            FileInputStream key_file;
            FileInputStream fis;
            FileOutputStream fos;
            CipherInputStream cis;
            
            // Define an Initialization Vector (IV)
            byte[] iv = new byte[Common.DES_LENGTH];
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Create a Secret key
            key_file = new FileInputStream("data/key/" + this.key_filename);
            byte key[] = key_file.readNBytes(Common.DES_LENGTH);
            SecretKeySpec secretKey = new SecretKeySpec(key, "DES");
            
            // Create Cipher object
            Cipher decrypt = Cipher.getInstance(this.transformation);
            if (this.mode == Common.ECB) {
                decrypt.init(Cipher.DECRYPT_MODE, secretKey);
            } else if (this.mode == Common.CBC) {
                decrypt.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            }
            
            // Open the Encrypted file
            fis = new FileInputStream(desFile);
            cis = new CipherInputStream(fis, decrypt);
            
            // Write to the Decrypted file
            fos = new FileOutputStream(desFileBis);
            byte[] b = new byte[8];
            int i = cis.read(b);
            while (i != -1) {
                 fos.write(b, 0, i);
                 i = cis.read(b);
            }
            fos.flush();
            fos.close();
            cis.close();
            fis.close();
            key_file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
