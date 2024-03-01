import swiftbot.*;
public class Ultrasound {
	public static void objectDetector(SwiftBotAPI swiftBot) {
	    try {
	        while (true) { // Run indefinitely
	            double distance = swiftBot.useUltrasound();
	            // Check if object is within 20 cm
	            if (distance <= 20.0) {
	                // Do something when object is detected within 20 cm
	                System.out.println("Object detected within 20 cm!");
	                swiftBot.stopMove();
	                swiftBot.move(100,-50,475);
	                swiftBot.useUltrasound();
	            }
	            Thread.sleep(1000); // Delay for stability (optional)
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.out.println("ERROR: Ultrasound Unsuccessful");
	        System.exit(5);
	    }
	}
}