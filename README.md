# GameAI
Neural network trained evolutionarily to play a game.  
Made with Java and Processing.

## Demo
![demo](demo.gif)

This is a demo of the AI playing one game.

## Quickstart
1. Clone repository
2. Run `./gradlew build && ./gradlew run`
3. You will see the AI play a random game. Press `space` to restart a new game once a game ends.

## Playing & Training
1. Open `src/main/java/gameai`
2. In the first line of main, there should be a string `option`
3. Set that to `play` to play the game as a person, or to `train` to train
  - To play, use left, right, and down arrow keys 
  - In training, press `space` to view the next generation play, and then `space` again to go back to silent training

## Game & AI info
### Game:
- Move left, right
- Stomp opponenets
- Death if touches opponents
- Score increases every second and stomping opponents
  - Stomping increases by a "streak" mechanic: more stomps without touching ground = higher score
  - Stomping without hitting opponents loses a few points
### AI:
- Recurrent Neural Network
  - Fully implemented in pure Java all the way down because I couldn't find any well-documented matrix libraries
  - Two hidden layers (sizes of 64 & 16)
  - Recurrent layer of size 10
  - Input:
    - Closest 6 enemies: x, y, m speed
    - Player: x, y, m speed, isStomping
  - Output:
    - Left, right, down
