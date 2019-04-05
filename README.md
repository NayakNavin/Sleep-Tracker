**Sleep tracker** 

we shouldn't track sleep according to assumption that user would always
sleep at specific time. 

 The reason is 
> - some users sleep at night
  >- some may sleep during morning (like people who goes for night shift)
  >- some may sleep during afternoon (Like old aged persons)
  >- some may take small naps either morning or at the noon.

So i have created the app which records `Sleep Start Time` And `Sleep
End Time` without user's assumption and ofcourse no user input.

but still if you are curious or eager to track sleep based on
assumption, i've implemented the code for that too. 

for that you will 
have to *comment* and *uncomment* few lines of code in order to work

Step 1: Goto `SleepService.java` file. 

Step 2: *Comment* Codes at these line numbers
> 69, 91

Step 3: *Uncomment* Codes at these line numbers
> 66, 70, 72, 73, 76, 77

i've pre-set user sleep time assumption as 9pm. and Minimum Sleep Time
as 1 hour. 

Your are free to change the assumed time and minimum sleep time. 

Note:
 
User sleep assumption is given in 24hrs format and please don't add `0`
before single digits. ie; if you want given the Sleep assumption time as
`5am`, give it like just `"5"`.

Minimum Sleep time is given in milliseconds. 

1 hour = 3600000 milliseconds 

1 minute = 60000 milliseconds

1 second = 1000 milliseconds

i've pre-set the mininum sleep time as 1 hour which is 3600000
milliseconds.

you are free to change these values but make sure you give it in
milliseconds. i would suggest to you give mininum sleep time as 10
seconds which is 10000 milliseconds. so that you don't have to wait for
1 hour to get the sleep start and end times constantly for 3 days for 3
days data.

there might be some bugs when tested on versions Android Oreo and above
as due to some background limitations implemented by the Android team at
Google. and also i'm restricted from using other apis.

NOTE: This is a POC app to record the Sleep Start Time and Sleep End
Time without using any APIs also smart watches and other fitness
devices.


