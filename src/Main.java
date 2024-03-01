import java.util.InputMismatchException;
import java.util.Random;
import swiftbot.*;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main {
	//Variables declared and grouped by data type.
	static Random rand = new Random();
	static screenClear cleanUI = new screenClear();
	static SwiftBotAPI swiftBot;
	static int sections, length, speed, leftVelocity, rightVelocity,sectionCount, sectionTime,totalSections = 0, roundedTimeToMove; //Variables to input sections and length, and variables to assign speed to each wheels of the SwiftBot. || SectionTime is used to calculate the time the robot will move at "x" speed.
	static boolean zigzagCompensation = false, takePictureYN = false, decodedSuccessfully ;//Variables to reverse Zigzag, terminate the program and enable taking a picture.
	static double straightDistance, distance, time; //Variables to perform calculations to store into a file.
	static long startTime, stopTime; //Variables to control the time the robot takes to complete the task.
	static String cyanColor = "\u001B[36m"; 
	static String whiteColor = "\u001B[37m";
	static String yellowColor = "\u001B[33m";
	static String greenColor = "\u001B[32m";
	static String redColor = "\u001B[31m";
	static String magentaColor = "\u001B[35m";
	// Constructor
	public Main(SwiftBotAPI swiftBot) {
		Main.swiftBot = swiftBot;
	}
	
	public static void main(String[] args) {
	    try {
	    	swiftBot = new SwiftBotAPI();
	    	cleanUI.clearScreen();
	    	startProgram();
	    } catch (Exception e) {
	        System.out.println("An error occurred during program execution:");
	        e.printStackTrace();
	    }
	}
	public static void startProgram() {
    	try {
	    	//Create instance of the Buttons class, to assign determinate actions to the buttons enabled.
	        Buttons button = new Buttons();
	        Scanner scanner = new Scanner(System.in);
	        
	        //Introduction and game instructions.
	        System.out.println("");
	        System.out.println(cyanColor + "		********************************************		" + whiteColor);
	        System.out.println(cyanColor + "		*         ZIGZAG JOURNEY WITH SWIFTBOT     *		" + whiteColor);
	        System.out.println(cyanColor + "		********************************************		" + whiteColor);
	        System.out.println("");
	        System.out.println(yellowColor + "Welcome to Zigzag SwiftBot System!" + whiteColor);
	        System.out.println("");
	        System.out.println(greenColor + "User Instructions:" + whiteColor);
	        System.out.println("");
	        System.out.println("- The robot will scan a QR code using the main camera.");
	        System.out.println("- The robot only accepts two values, first one is number of sections and second one the length of sections.");
	        System.out.println("- The values must be separated by a ','");
	        System.out.println("- The Number of sections must be even and no more than 12.");
	        System.out.println("- The length of the section should be between 15 and 85.");
	        System.out.println("- Use button ‘X’ to terminate the program.");
	        System.out.println("- Use button ‘Y’ to restart the program.");
	        System.out.println("");
	        //Enable button X to perform terminate 
	        swiftBot.enableButton(Button.X, () -> {
		             System.out.println("User has completed " + (totalSections) + " zigzag sections in total. Goodbye!");
		             button.terminate(swiftBot);
	        });
	        
	        //Enable button Y to perform restart.
	        swiftBot.enableButton(Button.Y, () -> {
	                	//Reset input values to 0
	      	        	sections = 0;
	                    length = 0;
	                    //Call restart action from class Buttons.
	    	            button.restart(swiftBot);
	    	            startProgram();
	        });
	        
	        //Call the scanQrCode method.
	        scanQrCode();
	    } catch (Exception e) {
	        System.out.println(redColor + "An error occurred during program execution:" + whiteColor);
	        e.printStackTrace();
	    }
	}
	public static void scanQrCode(){
		 try {
		    	Scanner scanner = new Scanner(System.in);
		        int qrInput; //Variable too store user input
		        do {
		            System.out.println(greenColor + "Please input the number 1 to start:" + whiteColor); //Ask user to input number 1.
		            try {
		                qrInput = scanner.nextInt(); //Read an integer from the user input
		                scanner.nextLine();
		                if (qrInput != 1) { //Validate if the input is 1, if not ask to input again.
		                    System.out.println(redColor + "Invalid input. Please input the number 1." + whiteColor);
		                }
		            } catch (InputMismatchException e) {
		                System.out.println(redColor + "Invalid input. Please input a valid integer." + whiteColor); //Validates correct data type input.
		                scanner.nextLine(); // Clear the invalid input.
		                qrInput = 0; // Reset qrInput to 0 to accept a new input.
		            }
		        } while (qrInput != 1); //Repeat the loop until the input is 1.

		        System.out.println(yellowColor + "Taking a capture in 5 seconds.." + whiteColor);
		        Thread.sleep(5000); //Wait for 5 seconds.
		        //Get the QR code image and decode it into a string.
		        BufferedImage img = swiftBot.getQRImage();
		        String decodedMessage = swiftBot.decodeQRImage(img);
		        
		        //Validate if the coded message has any data, if not scanQrCode again. 
		        if (decodedMessage.isEmpty()) {
		            System.out.println(redColor + "The QR code doesn't have any data" + whiteColor);
		            scanQrCode();
		            return;
		        } else {
		            System.out.println(greenColor + "SUCCESS: QR code found!" + whiteColor);
		            System.out.println(yellowColor + "Decoded message: " + decodedMessage + whiteColor);
		        }
		        //Validate if the decoded message has a comer and split the information into an array separated by the ",".
		        if (decodedMessage.contains(",")) {
		            String[] numbers = decodedMessage.split(",");
		            if (numbers.length == 2) { 
		            	try {
		                sections = Integer.parseInt(numbers[0]); 
		                length = Integer.parseInt(numbers[1]); 
		            	}catch (NumberFormatException ex) {
		                    System.out.println(redColor + "Error! Invalid input format for numbers." + whiteColor);
		                    scanQrCode();
		                    return;
		                }
		                if (sections % 2 == 0 && sections <= 12 && sections > 0) {
		                    if (length >= 15 && length <= 85) {
		                        System.out.println(yellowColor + "QR code is valid, zigzag is about to start in 5 seconds..." + whiteColor);
		                        //If inputs are correct wait for 5 seconds and call generateRandomSpeed method to generate a number between 20 and 40. 
		                        Thread.sleep(5000);
		                        speed = generateRandomSpeed(60, 100);
		                        timeToTravel();
		                        //Assign the value generated in speed to the each of the robot wheels.
		                        leftVelocity = speed;
		                        rightVelocity = speed;
		                        //check if the user want to  take picture (Default is N)
		                        while (true) {
		                        	System.out.println("");
		                        	System.out.println(greenColor + "Do you want to take a picture during zigzag movement?(Y/N)" + whiteColor);
		                            String userInput = scanner.nextLine();
		                            if(takePictureYN = userInput.equalsIgnoreCase("Y")) {
		                            	takePictureYN = true;
		                            	System.out.println("");
		                            	System.out.println(yellowColor + "Images will be saved on /home/pi/ !" + whiteColor);
		                            	System.out.println("");
		                            	break;
		                            } else if (takePictureYN = userInput.equalsIgnoreCase("N")) {
		                            	takePictureYN = false;
		                            	System.out.println("");
		                            	break;
		                            } else {
		                            	System.out.println(redColor + "Invaild answer!" + whiteColor);	                            	
		                            }
		                        }
		                        zigzagJourney();
		                    } else {
		                        System.out.println(redColor + "Error! The number of section length should be between 15 to 85." + whiteColor);
		                        scanQrCode();
		                        return;
		                    }
		                } else {
		                    System.out.println(redColor + "Error! The number of sections should be even and shouldn’t exceed 12." + whiteColor);
		                    scanQrCode();
		                    return;
		                }
		            } else {
		                System.out.println(redColor + "Only two values are allowed" + whiteColor);
		                scanQrCode();
		                return;
		            }
		        } else {
		            System.out.println(redColor + "The data must be separated by a ','" + whiteColor);
		            scanQrCode();
		            return;
		        }
		    } catch (Exception e) {
		        System.out.println(redColor + "ERROR: Unable to scan for code." + whiteColor);
		        e.printStackTrace();
		    }
		}
	
	//Method to generate a random number between a certain range.
	public static int generateRandomSpeed(int min, int max) {
        return rand.nextInt(max - min + 1) + min;
    }
	

		
	public static void zigzagJourney() {
	try {	
		Scanner scanner = new Scanner(System.in);
		Ultrasound detectObject = new Ultrasound();
		Journeypicture takePhoto = new Journeypicture();
		//Array declaration to call the different lights of the robot.
		Underlight[] underlights = new Underlight[] {
				Underlight.FRONT_LEFT,    // Front left light
				Underlight.BACK_LEFT,     // Back left light
				Underlight.FRONT_RIGHT,   // Front right light
				Underlight.BACK_RIGHT,    // Back right light
				Underlight.MIDDLE_RIGHT,  // Middle right light
				Underlight.MIDDLE_LEFT    // Middle left light
			};
	
		final int[] blue = { 0, 255, 0 }; //(Red, Green, Blue).
		final int[] green = { 0, 0, 255 }; //(Red, Green, Blue).
		
		startTime = System.currentTimeMillis(); // Captures the current time in milliseconds in the variable startTime, to be able to be used later.
		
	            // Start object detection thread
		 Thread objectDetectionThread = new Thread(() -> {
	            detectObject.objectDetector(swiftBot);
	        });
	        objectDetectionThread.start();

		//Start loop for the zigzag
        for (sectionCount = 1; sectionCount <= sections;) { 
            if (sectionCount % 2 != 0) { 
            	if(takePictureYN) {
            		takePhoto.photo(swiftBot);
            	}
                for (Underlight underlight : underlights) {
                    swiftBot.setUnderlight(underlight, green); 
                }
                //Turn orthogonally left.
                swiftBot.move(-50,100,475); 
                swiftBot.move(leftVelocity,rightVelocity,roundedTimeToMove); 
                swiftBot.disableUnderlights(); 
                System.out.println("Section count: " + sectionCount); 
            } else { 
            	if(takePictureYN) {
            		takePhoto.photo(swiftBot);
            	}
                for (Underlight underlight : underlights) {
                    swiftBot.setUnderlight(underlight, blue); 
                }
                if(!(sectionCount == sections && zigzagCompensation == true)) {
                	swiftBot.move(100,-50,475);   //Turn orthogonally right
                }
                swiftBot.move(leftVelocity,rightVelocity,roundedTimeToMove); 
                swiftBot.disableUnderlights(); //Disable lights.
                System.out.println("Section count: " + sectionCount); 
            }
            totalSections++;
		    if (sectionCount == sections) { 
		    	if(zigzagCompensation == false) {
		    		sectionCount++;
		    		swiftBot.stopMove(); 
			    	swiftBot.move(-55,100,950); //180 rotation.
			        zigzagCompensation = true; 
			        stopTime = System.currentTimeMillis(); 
			        System.out.println("zigzagCompensation: " + zigzagCompensation); 
		    	}
		    }
		    if (zigzagCompensation) { 
                if (sectionCount == 1) { 
                    calculateUserProgress(); 
                    System.exit(0); //Terminate the program.
                } else {
                		sectionCount--; //if zigzagCompensation is true, Decrement sectionCount after reaching the number of sections.
                }
                
		    } else {
                sectionCount++; // if zigzagCompensation is false, Increment sectionCount normally
            }
				try {
		            Thread.sleep(1000); //stops for one second
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
			}
        objectDetectionThread.join();

	    }catch (Exception e) {
	        System.out.println(redColor + "ERROR: An error occurred during the zigzag journey." + whiteColor);
	        e.printStackTrace();
	    }
	}
	
	public static void calculateUserProgress() {
		distance = length * sections;
		time = (stopTime - startTime) / 1000.0;
		// Calculate straightDistance
        straightDistance = Math.sqrt(Math.pow(length, 2) + Math.pow(length, 2)) * (sections / 2);
     // Write to file
        writeToFile(sections, length, speed, distance, time, straightDistance);
	}
	private static void writeToFile(int sections, int length, int speed, double distance, double time, double straightDistance) {
        String filename = "/home/pi/RobotData/UserProgress.txt"; // File path
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write variables to the file
            writer.println("Sections: " + sections);
            writer.println("Length: " + length + " cm");
            writer.println("Speed: " + speed);
            writer.println("Distance: " + distance + " cm");
            writer.println("Time: " + time + " seconds");
            writer.println("StraightDistance: " + straightDistance + " cm");
            writer.println("ZigZag Successfully completed!");

            // Display status
            System.out.println(greenColor + "ZigZag Successfully completed!" + whiteColor);
	        System.out.println("");
	        System.out.println(cyanColor + "		********************************************		" + whiteColor);
	        System.out.println(cyanColor + "		*         JOURNEY STATS WITH SWIFTBOT     *		" + whiteColor);
	        System.out.println(cyanColor + "		********************************************		" + whiteColor);
	        System.out.println("");
            System.out.println("Sections: " + sections);
            System.out.println("Length: " + length + " cm");
            System.out.println("Speed: " + speed);
            System.out.println("Distance: " + distance + " cm");
            System.out.println("Time: " + time + " seconds");
            System.out.println("StraightDistance: " + straightDistance + " cm");
            System.out.println("");
            System.out.println(yellowColor + "You can view the data captured in the following route: " + filename + whiteColor);
            System.out.println(magentaColor + "Thanks for taking this journey with me ^^" + whiteColor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public static void timeToTravel(){
		try {
			int initialSpeed = 50;
			int initialDuration = 1000;
			int initialDistance = 20;
			double distanceCalc = (double) length / initialDistance;
			double timeCalc = (double) initialDuration * distanceCalc;
			double speedCalc = (double) initialSpeed / speed;
			double timeToMove =(double) timeCalc * speedCalc;
			roundedTimeToMove = (int) Math.round(timeToMove);
			//System.out.println("DistanceCalc: " + distanceCalc + "timeCalc: " + timeCalc + " speedCalc: " + speedCalc + " timeToMove: " + timeToMove);
			System.out.println("SwiftBot will move at " + speed + " speed during " + roundedTimeToMove + " miliseconds at the length of " + length + " cm.");
		}catch (ArithmeticException e) {
	        System.out.println("ArithmeticException occurred during calculation: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
}
