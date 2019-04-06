# **Sleep tracker**


### Intro:


This android application records Sleep Start Time And Sleep End Time with no user input provided.



Steps for trying out this sample:
- Compile and install the mobile app onto your mobile device or emulator.

- Turn off the screen and make sure device is locked with anyone of
  these (pin, pattern or password).

- Unlock the device after few minutes. you can use anyone of these (pin,
  pattern, password or fingerprints) to unlock.
  
- Open the app, you would see the Sleep Start and End times. 



    <img src="https://user-images.githubusercontent.com/37023798/55673459-fee91300-58c5-11e9-9cf1-a4e89c134345.png" width="150" hheight="150" >



### Additional features provided



Additional feature for recording the sleep of the user only at a particular time of the day is also implemented in this project.



Follow the steps below to accomplish it:



Step 1: Goto SleepService.java file.

Step 2: Comment Codes at these line numbers 69 and 91

Step 3: Uncomment Codes at these line numbers 66, 70, 72, 73, 76 and 77

The user sleep start time is assumed as 9pm and minimum Sleep Time as 1 hour.

Feel free to change the assumed time and minimum sleep time.



User’s sleep assumption is given in 24hrs format and we don't need to
add 0 before single digits, ie, if you want to give the Sleep assumption
time as 5am, give it like just "5".



Minimum Sleep time is given in milliseconds and it is set to 1 hour which is 3600000 milliseconds. So any modifications further should also be in milliseconds.



### Disclaimer:

There might be some bugs when tested on versions Android Oreo and above as due to some background limitations on Oreo and further android versions (as I am not supposed to use other apis as well, as per the requirement of this project).



#### NOTE: 
This is a POC app to record the Sleep Start Time and Sleep End
Time without using any APIs or any form of user input.
