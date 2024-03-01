import java.awt.image.BufferedImage;
import java.io.File;
import swiftbot.*;
import javax.imageio.ImageIO;

public class Journeypicture {
	public static void photo(SwiftBotAPI swiftBot) {
		try {
			 BufferedImage image = swiftBot.takeStill(ImageSize.SQUARE_720x720);
			 if(image == null){
	                System.out.println("ERROR: Image is null");
	                System.exit(5);
	            }
	            else{
	            	String fileName = "/home/pi/colourImage_" + System.currentTimeMillis() + ".png";
	                File outputFile = new File(fileName);
	                ImageIO.write(image, "png", outputFile);
	                System.out.println("SUCCESS: Image saved!");
	                Thread.sleep(1000);
	            }
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	}
}
