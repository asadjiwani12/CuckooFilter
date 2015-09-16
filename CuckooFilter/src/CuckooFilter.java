import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;

public class CuckooFilter {

	final int max_attempts = 500;
	double elements_count ;
	double row_size = 25;

	CuckooFilter() {
		elements_count = 0;
	}

	private Object hash_table[] = new Object[(int) row_size];

	String hexToString(byte[] output) {
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
	int useHash(String input, String functionName) {

		try {
			MessageDigest digest = MessageDigest.getInstance(functionName);
			digest.update(input.getBytes());
			byte[] out = digest.digest() ;
			String hex = hexToString(out);
			BigInteger val = new BigInteger(hex,16);
			//System.out.println("Value" + val);
			BigDecimal k = BigDecimal.valueOf(row_size);
			BigInteger b = k.toBigInteger();
			int mappedValue = val.mod(b).intValue();
			return mappedValue;
		}catch(Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
	
	
	
	// Returns the hashvalue which is fingerPrint
	int get_hashValue(Object o) {
		int gen_code =  o.hashCode();
		if(gen_code < 0) {
			gen_code = Math.abs(gen_code);
		}	
		return gen_code;
	}

	int get_hashValue2(int pos1, int hashvalue) {
		
		int position2 = this.useHash(String.valueOf(hashvalue),"SHA1");
		position2 = (position2 ^ pos1) % (int)this.row_size;
		//System.out.println("Hashcode2 generated : " + position2 );
		return position2;
	}
	

	/*
	 * This method clears the HashTable.
	 */
	void clear() {
		for(int row_cnt = 0 ; row_cnt < row_size ; row_cnt++ ) {
			this.hash_table[row_cnt] = null ;
		}

		elements_count = 0;
		row_size = 3;
		hash_table = new Object[(int) row_size];
	}	



	/*
	 * This method gives a shallow copy of the object 
	 * @param: A 1-dimensional object array
	 */
	void copy(Object a[]) {
		for(int row_no = 0 ; row_no < row_size; row_no++) {
			if(hash_table[row_no] != null ) {
				a[row_no] = hash_table[row_no];	
			}
		}
	}	

	/*
	 * This method checks if the parameter is already there in the hashtable.
	 *@param : Object to be checked if present
	 */
	boolean contains(Object o) {
		int flag = 0;

		int row_pos1;
		int row_pos2;
		int valuetobestored;
		
		// Checks the size of the hash_table
		//check(e);
		valuetobestored = get_hashValue((String) o);
		row_pos1 = this.useHash((String) o,"SHA1");
		row_pos2 = get_hashValue2(valuetobestored, row_pos1);
		System.out.println("Row Position1 : " + row_pos1);
		System.out.println("Row Position2 : " + row_pos2);
		if(hash_table[row_pos1] != null || hash_table[row_pos2] !=null) {
			if( (int)hash_table[row_pos1] == valuetobestored || (int)hash_table[row_pos2] == valuetobestored) {
				flag = 1;
			}
		}	

		if(flag == 1) {
			return true;
		} else{
			return false;
		}
	}

	/*
	 * This method checks if the size is sufficient for an object to be added
	 * @param : The object to be added
	 */
	void check(Object o) {
		if(elements_count > (0.5 * row_size)) {
			Object new_hashtable[]  = new Object[(int)row_size*2];
			copy(new_hashtable); 	
			this.hash_table = new_hashtable;
			row_size *= 2;
		}
	}

	boolean delete(Object o) {
		if(contains(o)) {		
			int row_pos1;
			int row_pos2;
			int valuetobestored;
		
		// Checks the size of the hash_table
		//check(e);
			valuetobestored = get_hashValue((String) o);
			row_pos1 = this.useHash((String) o,"SHA1");
			row_pos2 = get_hashValue2(valuetobestored, row_pos1);
 
			if( ((int)hash_table[row_pos1] == valuetobestored ) || ( ((int)hash_table[row_pos1] == valuetobestored))) {
				 hash_table[row_pos1] = null;
			} else {
				hash_table[row_pos2] = null;		
			}
			this.elements_count--;
			return true;
		} else {
			return false;
		}
	}
	
	boolean insert(Object e) {
		int row_pos1;
		int row_pos2;
		
		int valuetobestored;
		
		// Checks the size of the hash_table
		//check(e);
		valuetobestored = get_hashValue((String) e);
		row_pos1 = this.useHash((String) e,"SHA1");
		row_pos2 = get_hashValue2(valuetobestored, row_pos1);

		System.out.println("Row Position 1 : " + row_pos1);
		System.out.println("Row Position 2 : " + row_pos2);
				
		if(this.hash_table[row_pos1] == null) {
			this.hash_table[row_pos1] = valuetobestored;
			this.elements_count++;
			return true;	
		} else if(this.hash_table[row_pos2] == null) {
			// if storing at position 2 then too store hashcode1
			this.hash_table[row_pos2] = valuetobestored;
			this.elements_count++;
			return true;	
		} else {	
			// i1 and i2  both are not null...
			// We will replace i1 and find alternate place for the i1.
			// Find an empty spot for the previous one in the slot and reloacte that to the new spot
			for(int i = 0; i < max_attempts; i++) {
				// prev_element_value
				int prev_stored_value = (int)this.hash_table[row_pos1];
				// insert new element
				this.hash_table[row_pos1] = valuetobestored;
				// find new place for the previous
				int alt_row_pos = get_hashValue2(prev_stored_value, row_pos1);
				if(this.hash_table[alt_row_pos] == null ) {
					this.hash_table[alt_row_pos] = prev_stored_value;
					this.elements_count++;
					return true;	
				} else {
					row_pos1 = alt_row_pos; 
					valuetobestored= (int)this.hash_table[alt_row_pos];
				}
			}
		}
		return false;
	}

	void printTable(String[] ipaddress) {
		System.out.println("Element \t HashValue1 \t HashValue2 \t ");
		int row_pos1;
		int row_pos2;
		int valuetobestored;
		
		for(int i=0; i<this.elements_count; i++) {
			valuetobestored = get_hashValue((String) ipaddress[i]);
			row_pos1 = this.useHash((String) ipaddress[i],"SHA1");
			row_pos2 = get_hashValue2(valuetobestored, row_pos1);
			System.out.println(ipaddress[i] + "\t" + row_pos1 + "\t" + row_pos2);
		}
		
		System.out.println("\n \n Index \t Value");
		
		for(int i=0; i<this.row_size; i++) {
			System.out.println(i + "\t" + this.hash_table[i]);
		}
	}

	public static void main(String args[]) {

		IPAddress obj1 = new IPAddress();
		CuckooFilter obj = new CuckooFilter();
		
		
		//String formattedIPAddress = obj1.splitIPAddress("129.28.34.123");
		//System.out.println("Formatted IP Address = " + formattedIPAddress);

		obj1.generateIPaddresses(5);
	
		for(int i=0; i<obj1.numIPaddress24prefix;i++) {
			//System.out.println(obj1.IPaddress[i]);
			obj1.IPaddress[i] = obj1.splitIPAddress(obj1.IPaddress[i]);
			//System.out.println(obj1.IPaddress[i]);
		}
		
		for(int i=0; i<obj1.numIPaddress24prefix; i++) {
			System.out.println("IP address : " + obj1.IPaddress[i] + "  : " + obj.insert(obj1.IPaddress[i]));
		}

		//System.out.println("Hello World !");
		for(int i=0; i<obj1.numIPaddress24prefix; i++) {
			System.out.println( i+1 + ".  IS PRESENT : " + " :  " + obj.contains(obj1.IPaddress[i]));
		}

		
		System.out.println(" IS PRESENT : " + 10 + " :  " + obj.contains("10"));
		/*
		//System.out.println(" Delete : " + 10 + " :  " + obj.delete(10));
		System.out.println(" Size : " + obj.elements_count);
		System.out.println(" Size : " + obj.row_size);	
	
		obj.printTable(obj1.IPaddress);
		
*/	}
}
