// defines pin numbers
const byte avoidancePin = 2;
const byte motionPin = 3;
const byte ultraTrigPin = 4;
const byte ultraEchoPin = 5;

// initializes variables
bool avoidanceDetected = false;
bool motionDetected = false;
long ultraDuration;
int ultraDistance;
int startMotionTime;
int timeElapsed;


// built-in Arduino setup function to initialize and set things up
void setup()
{
  // setting up pins
  pinMode(motionPin, INPUT);
  pinMode(ultraTrigPin, OUTPUT);
  pinMode(ultraEchoPin, INPUT);

  // sets up serial monitor
  Serial.begin(9600);
}



// built-in Arduino loop function that is constantly called/looped through
void loop()
{
  //avoidance sensor
  
  int avoidanceReading = !digitalRead(avoidancePin);
  // if there is a reading on the avoidance detector
  if(avoidanceReading)
  {
    // if we aren't already detecting something with the avoidance detector, then do stuff
    if (avoidanceDetected == false)
    {
      avoidanceDetected = true;
      Serial.print("WARNING: Object detected on the right | ");
    }
    else
    {
      //this is for while there is something in front of the sensor
      Serial.print("WARNING: Object detected on the right | ");
    }
  }
  else
  {
    // if there is no reading on the avoidance sensor when there previously was, set avoidanceDetected to false
    if(avoidanceDetected)
    {
      avoidanceDetected = false;
      //Serial.print("WARNING: Object detected on the left|");
    }
    Serial.print("                                      | ");
  }

 

  /* 
   *  ultrasonic sensor stuff
   */
  // Clears the trigPin
  digitalWrite(ultraTrigPin, LOW);
  delayMicroseconds(2);

  // Sets the trigPin on HIGH state for 10 micro seconds
  digitalWrite(ultraTrigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(ultraTrigPin, LOW);

  // Reads the echoPin, returns the sound wave travel time in microseconds
  ultraDuration = pulseIn(ultraEchoPin, HIGH);

  // Calculating the distance
  ultraDistance = ultraDuration * 0.034 / 2;

  // Prints the distance on the Serial Monitor
  if (ultraDistance < 10)
  {
    Serial.print("WARNING: Object Nearby ");
    Serial.print(ultraDistance);
    Serial.print( " cm away. | ");
  }
  else
  {
    Serial.print("                                  | ");
  }



     /*
   * motion sensor stuff
   */
  int motionReading = digitalRead(motionPin);
  // if there is a reading on the motion sensor
  if (motionReading == HIGH) {
    // if we aren't already detecting motion, then set motionDetected to be true and start timing the amount of time in motion
    if (motionDetected == false) 
    {
      Serial.print("WARNING: Motion detected on the left");
      startMotionTime = millis();
      motionDetected = true;
    }
  }
  else 
  {
    // Change the motion state to false (no motion):
    if (motionDetected == true) 
    {
      timeElapsed = millis() - startMotionTime;
      //Serial.println("Motion ended! Milliseconds elapsed:");
      //Serial.println(timeElapsed);
      motionDetected = false;
    }
  }

  

  //Serial.println();
  //Serial.println();
  //Serial.println();
  Serial.println();
  
  delay(100);
} 
