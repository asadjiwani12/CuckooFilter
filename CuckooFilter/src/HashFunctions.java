import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;

public class HashFunctions {

	public static int row_size = 2;
	public static String hexToString(byte[] output) {
		char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };

		StringBuffer buf = new StringBuffer();

		for (int j = 0; j < output.length; j++) {
			buf.append(hexDigit[(output[j] >> 4) & 0x0f]);
			buf.append(hexDigit[output[j] & 0x0f]);
		}
		return buf.toString();
	}

	//use the hash function passed as input value to produce an integer
	//output. The output is then mapped onto one of the servers for
	//file replication.
	public static int useHash(String input, String functionName) {

		try {
			MessageDigest digest = MessageDigest.getInstance(functionName);
			digest.update(input.getBytes());
			byte[] out = digest.digest() ;
			String hex = hexToString(out);
			BigInteger val = new BigInteger(hex,16);
			System.out.println("Value" + val);
			BigDecimal k = BigDecimal.valueOf(row_size);
			BigInteger b = k.toBigInteger();
			int mappedValue = val.mod(b).intValue();
			//int mappedValue = val.mod(new BigInteger("3")).intValue();
			return mappedValue;

		}catch(Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public static void main(String args[]) {
		System.out.println("MappedValue : " + useHash("1234567891011","SHA1"));
		System.out.println("MappedValue : " + useHash("1234567891011","MD5"));
	}
}
