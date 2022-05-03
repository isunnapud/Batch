package com.ups.ops.oms.batch.mdc.mainframe;

public class PackedDecimal {
	    private static final int PlusSign = 0x0C; // Plus sign
	    private static final int MinusSign = 0x0D; // Minus
	    private static final int NoSign = 0x0F; // Unsigned
	    private static final int DropHO = 0xFF; // AND mask to drop HO sign bits
	    private static final int GetLO = 0x0F; // Get only LO digit

	    /**
	     * This method converts the byte array with comp3 formatted value into long
	     * @param pdIn
	     * @return
	     * @throws Exception
	     */
	    public static long parse(byte[] pdIn) throws Exception {
	        long val = 0; // Value to return

	        for (int i = 0; i < pdIn.length; i++) {
	            int aByte = pdIn[i] & DropHO; // Get next 2 digits & drop sign bits
	            if (i == pdIn.length - 1) { // last digit?
	                int digit = aByte >> 4; // First get digit
	                val = val * 10 + digit;

	                int sign = aByte & GetLO; // now get sign
	                if (sign == MinusSign)
	                    val = -val;
	                else {
	                    // Do we care if there is an invalid sign?
	                    if (sign != PlusSign && sign != NoSign) {
	                        for (int x = 0; x < pdIn.length; x++) {
	                            System.out.print(Integer.toString(pdIn[x] & 0x000000ff, 16));
	                        }
	                       
	                        throw new Exception("OC7");
	                    }
	                }
	            } else {
	                int digit = aByte >> 4; // HO first
	                val = val * 10 + digit;

	                digit = aByte & GetLO; // now LO
	                val = val * 10 + digit;

	            }
	        }
	        return val;
	    }
	    
	    

	    /**
	     * This method converts the input string which holds a long value to comp3 format byte array.
	     * 
	     * @param value
	     * @param byteCount
	     * @return
	     * @throws UnsupportedEncodingException
	     */
	    public static byte[] format(String value, int byteCount) {
	    	long longValue = Long.parseLong(value);
	    	byte[] comp3Bytes = format(longValue,byteCount);
	    	//switch  NL(x15) to LF(x25) and vice versa so that they will be mapped to the correct utf-8 char when converted.
	    	switchLF_NL(comp3Bytes);
	    	
	    	//convert the comp3 format byte array to string by explicitly specifying the EBCDIC character set. This will convert the 
	    	//comp3 characters to the corresponding unicode characters. Then get the byte out of the converted string without specifying
	    	// any char set (will use the system default char set) so that the underlying characters
	    	//are retained when converted back to string again.
	    	//String comp3String = new String(comp3Bytes, DataConstants.Keys._EBCDIC);
	    	/*
	    	if(AppUtil.shouldLogDebug()){
		    	AppUtil.debug("Before comp3 conversion: " + value);
		    	AppUtil.debug("After comp3 conversion: " + comp3String);
	    	}
	    	*/
	    	
	    	//return comp3String.getBytes();
	    	return comp3Bytes;
	    }
	    
	    /**
	     * This method returns the byte array with comp3 converted value of the input number.
	     * 
	     * @param number
	     * @param byteCount
	     * @return
	     */
	    public static byte[] format(long number, int byteCount) {
	 
	    	byte[] bytes = new byte[byteCount];

	        String data = Long.toString(number);
	        int length = data.length();
	        boolean isNegative = false;

	        if (data.charAt(0) == '-') {
	            isNegative = true;
	            data = data.substring(1);
	            length--;
	        }

	        if (length % 2 == 0) {
	            data = "0" + data;
	            length++;
	        }

	        int neededBytes = (int) (((length + 1) / 2f) + 0.5f);

	        int extraBytes = neededBytes - byteCount;
	        if (extraBytes < 0) {
	            // Pad extra byte positions with zero
	            for (int i = 0; i < -extraBytes; i++) {
	                bytes[i] = 0x00;
	            }
	        } else if (extraBytes > 0) {
	            // Truncate the high order digits of the number to fit
	            data = data.substring(extraBytes);
	            length -= extraBytes;
	            extraBytes = 0;
	        }

	        // Translate the string digits into bytes
	        for (int pos = 0; pos <= length - 1; pos++) {
	            String digit = data.substring(pos, pos + 1);
	            int now = (pos / 2) - extraBytes;

	            if (pos % 2 == 0) { // High
	                bytes[now] = (byte) (Byte.valueOf(digit) << 4);
	            } else { // Low
	                bytes[now] = (byte) (bytes[now] | (Byte.valueOf(digit) & 0x0f));
	            }
	        }

	        // Add the sign byte
	        if (isNegative) {
	            bytes[byteCount - 1] = (byte) (bytes[byteCount - 1] | MinusSign);
	        } else {
	            bytes[byteCount - 1] = (byte) (bytes[byteCount - 1] | PlusSign);
	        }
	        return bytes;
	    }

	 
	    public static void main(String[] args) throws Exception {
	        long price;
	        byte[] format;

	        price = -123456789;
	        format = PackedDecimal.format(price, 5);
	       
	        String s1 = new String(format);  //represent Byte as ASCII String
	        
	    }

	    /**
	     * This method converts the comp3 byte array into a string form for logging
	     * @param b
	     * @return
	     */
	    public static String byteToString(byte[] b) {
	        StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < b.length; i++) {
	            int curByte = b[i] & 0xFF;
	            sb.append("0x");
	            if (curByte <= 0x0F) {
	                sb.append("0");
	            }
	            sb.append(Integer.toString(curByte, 16));
	            sb.append(" ");
	        }
	        return sb.toString().trim();
	    }
	    
	    /**
	     * This method switches the x15 and x25 hex values in the input byte array.
	     * This is will ensure that java maps these to the right utf-8 characters when converting from ebcdic to utf-8 
	     * @param packed
	     * @return
	     */
	    private static void switchLF_NL(byte[] packed) {
	    	int size= packed.length;
	  		for(int i=0;i<size;i++){
				if(packed[i]== 0x15 ){
					packed[i] = 0x25;
				}
				else if(packed[i]== 0x25 ){
					packed[i] = 0x15;
				}
			}
		}
}
