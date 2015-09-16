import java.util.Random;
import java.util.regex.Pattern;


public class IPAddress {

	int numIPaddress24prefix = 25;
	int numIPaddressOtherprefix = 250;
	String[] IPaddress = new String[500];
	String[][] IPaddress_Subnet = new String[500][2];
	int counter=0;
	
	String splitIPAddress(String ipaddr) {
		if(ipaddr != null) {
			//System.out.println("IP address received : " +ipaddr);
			String[] temp = ipaddr.split(Pattern.quote("."));
			String returnString = temp[0]+temp[1]+temp[2]+temp[3];
			//System.out.println("Return String = " + returnString);
			return returnString;
		} 
		return "0";
	}
	void generateIPaddresses(int count) {
		Random r = new Random();
		int Low1 = 192;
		int High1 = 223;
		int Low2 = 0;
		int High2 = 255;
		int Low3 = 0;
		int High3 = 255;
		int Low4 = 0;
		int High4 = 255;
		int R=0;
		String ipaddress;

		for(int i=0; i<count; i++) {
			R = r.nextInt(High1-Low1) + Low1;
			ipaddress = String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High2-Low2) + Low2;
			ipaddress = ipaddress + String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High3-Low3) + Low3;
			ipaddress = ipaddress + String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High4-Low4) + Low3;
			ipaddress = ipaddress + String.valueOf(R);
			IPaddress[i] = ipaddress;
			System.out.println("IPaddress " + (i+1) + ": " +IPaddress[i]);
		}
	}

	void generateIPaddressesSubnet24bit(int count) {
		Random r = new Random();
		int Low1 = 192;
		int High1 = 223;
		int Low2 = 0;
		int High2 = 255;
		int Low3 = 0;
		int High3 = 255;
		int Low4 = 0;
		int High4 = 255;
		int R=0;
		String ipaddress;
		this.counter += count; 
		
		for(int i=0; i<count; i++) {
			R = r.nextInt(High1-Low1) + Low1;
			ipaddress = String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High2-Low2) + Low2;
			ipaddress = ipaddress + String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High3-Low3) + Low3;
			ipaddress = ipaddress + String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High4-Low4) + Low3;
			ipaddress = ipaddress + String.valueOf(R);
			IPaddress_Subnet[i][0] = ipaddress;
			IPaddress_Subnet[i][1] = "255.255.255.0";
			System.out.println("IPaddress " + (i+1) + ": " +IPaddress[i]);
		}
	}

	String generate_subnetmask(int prefixlength) {

		int numof255 = prefixlength / 8;
		int numbertobeconverted = prefixlength % 8;
		String subnetmask = "";
		int remaindertoInt = 0;
		
		for(int i=0; i<numof255;i++) {
			subnetmask += "255.";
		}

			
		for(int i=0; i<numbertobeconverted; i++) {
			remaindertoInt += Math.pow(2, 7-i);
		}

		if(numbertobeconverted == 0) {
			if(numof255 == 2) {
				subnetmask = subnetmask + "0" + ".0";
			} else {	
				subnetmask = subnetmask + "0" + ".0" + ".0" ;
			}	
		} else {
			if(prefixlength>16 && prefixlength <24 ) {
				subnetmask = subnetmask + String.valueOf(remaindertoInt) + ".0";
			} else if(prefixlength>8 && prefixlength <16 ) {
				subnetmask = subnetmask + String.valueOf(remaindertoInt) + ".0" + ".0" ;
			}
		}
			
		
		 
		return subnetmask;
	}

	void generateIPaddressesSubnet(int count) {
		Random r = new Random();
		int Low1 = 10;
		int High1 = 192;
		int Low2 = 0;
		int High2 = 255;
		int Low3 = 0;
		int High3 = 255;
		int Low4 = 0;
		int High4 = 255;
		int iplength_low = 8;
		int iplength_high = 23;
		int R=0;
		String ipaddress;
		
		System.out.println("Counter Value " + this.counter);

		for(int i= this.counter; i<this.counter+count; i++) {
			R = r.nextInt(High1-Low1) + Low1;
			ipaddress = String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High2-Low2) + Low2;
			ipaddress = ipaddress + String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High3-Low3) + Low3;
			ipaddress = ipaddress + String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High4-Low4) + Low3;
			ipaddress = ipaddress + String.valueOf(R);
			IPaddress_Subnet[i][0] = ipaddress;
			R= r.nextInt(iplength_high-iplength_low) + iplength_low;
			IPaddress_Subnet[i][1] = generate_subnetmask(R);
			System.out.println("IPaddress " + (i+1) + ": " +IPaddress[i]);
		}
		
		this.counter += count;
	}

	void printIpSubnetTable() {
		
		System.out.println("SR.NO \t IPADDRESS \t SUBNET");
		for(int i=0; i<counter;i++) {
			System.out.println(i+1 + ". \t" + IPaddress_Subnet[i][0] + "\t" + IPaddress_Subnet[i][1]);
		}
	}
}
