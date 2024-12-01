import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class EncryptFile {
	public static void encrypt(String plaintext, String key_filename, int mode, int padding_algo) {
		String basename;
		// Get basename without file extension
		if (plaintext.contains(".")) {
            basename = plaintext.substring(0, plaintext.lastIndexOf('.'));
		} else {
			basename = plaintext;
		}
		System.out.printf("basename: %s\n", basename);
		String path2file = "data/plaintext/" + basename + ".txt";
		String ouput_file = "data/ciphertext/output.enc";
		// String ouput_file = "data/ciphertext/encrypt_" + basename + ".enc";


		try {
			File desFile = new File(ouput_file);
			FileInputStream fis;
			FileOutputStream fos;
			FileInputStream key_file;
			CipherInputStream cis;
			
			
			
			// Open the Plaintext file
			try {
				fis = new FileInputStream(path2file);
				key_file = new FileInputStream("data/key/" + key_filename);

				// Create a Secret key
				byte key[] = key_file.readNBytes(Common.DES_LENGTH);
				SecretKeySpec secretKey = new SecretKeySpec(key, "DES");

				// Get transformation
				String transformation = "DES/";
				if (mode == Common.ECB) {
					transformation += "ECB/";
				} else if (mode == Common.CBC) {
					transformation += "CBC/";
				} else {
					System.out.println("Mode invalid");
					fis.close();
					key_file.close();
					return;
				}
				
				if (padding_algo == Common.PKCS5Padding) {
					transformation += "PKCS5Padding";
				} else if (padding_algo == Common.NoPadding) {
					transformation += "NoPadding";
				} else {
					System.out.println("Padding algorithm invalid");
					fis.close();
					key_file.close();
					return;
				}
				
				// Create Cipher object
				Cipher encrypt = Cipher.getInstance(transformation);
				encrypt.init(Cipher.ENCRYPT_MODE, secretKey);
				
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
}
