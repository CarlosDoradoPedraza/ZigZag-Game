import java.util.Scanner;
import swiftbot.*;
public class Buttons {
	static Scanner scanner = new Scanner(System.in);
	static screenClear cleanUI = new screenClear();
	public static void terminate(SwiftBotAPI swiftBot) {
		try{ 
            System.out.println("Terminating the program...");
            swiftBot.disableUnderlights();
            swiftBot.stopMove();
            swiftBot.disableButton(Button.B);
            swiftBot.disableAllButtons(); // Turns off all buttons now that it's been 10 seconds.
            System.out.println("All buttons are now off.");
            System.exit(0);
        }catch(Exception e){
            System.out.println("ERROR occurred when setting up buttons.");
            e.printStackTrace();
            System.exit(5);
        }
	}
	
	public static void restart(SwiftBotAPI swiftBot) {
	    try {
	        System.out.println("Restarting the program...");
	        swiftBot.stopMove();
	        swiftBot.disableUnderlights();
	        swiftBot.disableButton(Button.Y);
	        swiftBot.disableAllButtons();
	        System.out.println("All buttons are now off.");
	    } catch (Exception e) {
	        System.out.println("Unknown error occurred when restarting the program:");
	        e.printStackTrace();
	        // Handle other exceptions gracefully (e.g., log the error and continue)
	    }
	}
}