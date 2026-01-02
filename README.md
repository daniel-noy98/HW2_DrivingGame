{\rtf1\ansi\ansicpg1252\cocoartf2867
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fswiss\fcharset0 Helvetica-Bold;\f1\fswiss\fcharset0 Helvetica;}
{\colortbl;\red255\green255\blue255;}
{\*\expandedcolortbl;;}
\paperw11900\paperh16840\margl1440\margr1440\vieww11520\viewh8400\viewkind0
\pard\tqr\tx720\tqr\tx1440\tqr\tx2160\tqr\tx2880\tqr\tx3600\tqr\tx4320\tqr\tx5040\tqr\tx5760\tqr\tx6480\tqr\tx7200\tqr\tx7920\tqr\tx8640\pardirnatural\partightenfactor0

\f0\b\fs24 \cf0 HW1 \uc0\u8235 -\uc0\u8236  Driving Game
\f1\b0 \
\

\f0\b Description  
\f1\b0 \
This project is an Android driving game developed as part of HW2.  \
The player controls a car driving on a five-lane road, avoids obstacles, and collects coins.  \
The game ends when all lives are lost, after which the player can save their score and view it in the high scores screen.\
\

\f0\b Gameplay  
\f1\b0 \
The player controls the car either using on-screen buttons or by tilting the device using sensors.  \
Colliding with obstacles reduces player lives and plays a crash sound effect.  \
Collecting coins increases the score and plays a coin collection sound.  \
The game speed gradually increases over time.  \
The game can be paused at any time and the player can return to the main menu.\
\

\f0\b Game Modes  
\f1\b0 \
Control mode: Buttons or Sensors.  \
Speed mode: Slow or Fast.\
\

\f0\b High Scores  
\f1\b0 \
The game stores the top 10 high scores locally on the device using SharedPreferences.  \
Each high score includes the player name, score, distance traveled, and a location value.  \
The high scores screen displays the scores in a list and allows interaction with a map view.\

\f0\b \
Map Visualization  
\f1\b0 \
The high scores screen includes a custom local map implementation.  \
Each high score is associated with a location that is visualized on the map.  \
Selecting a score from the list updates the marker position on the map.  \
The map was implemented without using external map services such as Google Maps.\
\

\f0\b Screens  
\f1\b0 \
Main menu screen.  \
Game screen.  \
Pause menu during gameplay.  \
Game over screen.  \
High scores screen with a list and a map.\
\

\f0\b Technologies Used  
\f1\b0 \
Kotlin.  \
Android SDK.  \
Custom View for rendering the game.  \
SoundPool for sound effects.  \
SharedPreferences for saving high scores.  \
Fragments and RecyclerView.\
\

\f0\b How to Run  
\f1\b0 \
Open the project in Android Studio.  \
Run the application on an emulator or physical device.  \
Select control and speed modes from the menu.  \
Start the game and play.\
\

\f0\b Demo Video  
\f1\b0 \
A demo video showing gameplay, sound effects, pause functionality, high score saving, and map interaction is included in the submission.\
\
}