package common;


/**
 * This is a simple output class
 * The idea is that it encapsulates system output to make it easier to format text and turn off output via the verbose flag
 * @author Kim
 *
 */
public class Out {

	public static void print(String output){
		if(Settings.VERBOSE) {
			System.out.println("**" + output);
		}
	}
	
	public static void printHeading(String output){
		if(Settings.VERBOSE) {
			System.out.println("******************************");
			System.out.println("**" + output + "**");
			System.out.println("******************************");
		}		
	}
	
	/**
	 * Error messages are always printed, regardless of the verbose flag
	 */
	public static void error(String output) {
		System.out.println("**ERROR: " + output);
	}
}
