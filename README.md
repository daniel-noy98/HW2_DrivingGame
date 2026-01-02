HW1 - Driving Game

Description  
This project is an Android driving game developed as part of HW2.  
The player controls a car driving on a five-lane road, avoids obstacles, and collects coins.  
The game ends when all lives are lost, after which the player can save their score and view it in the high scores screen.

Gameplay  
The player controls the car either using on-screen buttons or by tilting the device using sensors.  
Colliding with obstacles reduces player lives and plays a crash sound effect.  
Collecting coins increases the score and plays a coin collection sound.  
The game speed gradually increases over time.  
The game can be paused at any time and the player can return to the main menu.

Game Modes  
Control mode: Buttons or Sensors.  
Speed mode: Slow or Fast.

High Scores  
The game stores the top 10 high scores locally on the device using SharedPreferences.  
Each high score includes the player name, score, distance traveled, and a location value.  
The high scores screen displays the scores in a list and allows interaction with a map view.

Map Visualization  
The high scores screen includes a custom local map implementation.  
Each high score is associated with a location that is visualized on the map.  
Selecting a score from the list updates the marker position on the map.  
The map was implemented without using external map services such as Google Maps.

Screens  
Main menu screen.  
Game screen.  
Pause menu during gameplay.  
Game over screen.  
High scores screen with a list and a map.

Technologies Used  
Kotlin.  
Android SDK.  
Custom View for rendering the game.  
SoundPool for sound effects.  
SharedPreferences for saving high scores.  
Fragments and RecyclerView.

How to Run  
Open the project in Android Studio.  
Run the application on an emulator or physical device.  
Select control and speed modes from the menu.  
Start the game and play.

Demo Video  
A demo video showing gameplay, sound effects, pause functionality, high score saving, and map interaction is included in the submission.

