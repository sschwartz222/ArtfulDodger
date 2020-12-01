# MobileSoftwareDevFinalGame

My final project for Mobile Software Development is a self-proposed game project titled Artful Dodger. 
The basic premise of the game is that the player object (represented by a smiley face) must avoid colliding with any of the increasing number of bouncing projectiles generated on the screen. The player must also not lift their finger (or mouse click if using an emulator) after starting the game. Score is based on the number of projectiles on the screen when defeat occurs, tracked by both a current score text and high score text.

## Goals

### Initial Proposal
    
    The game will be a simple one where the player needs to: 
    1) keep their finger touching the screen at all times, and 
    2) dodge projectiles that continuously bounce around the screen, with an additional projectile added every X seconds
    
    Score will be based on how many seconds the player survives for

    Learning goals for this project:

    Track player touch input
    Instantiate enemy objects (projectiles) that have properties like speed, direction, etc.
    Objects move fluidly and are represented by sprites
    Handle collision resolution between player and enemy object
    Track and display high score (until app is uninstalled)
    
With the exception of the score mechanic changing from time to number of projectiles, I believe all of these criteria have been fulfilled.

### Other Goals and Lessons Learned

Broadly, my motivation for proposing a game project is due to my love of video games. The fields of video game design/programming are ones that I hope to pursue in the future in my career. While this game may be simple, I think it provided a great introduction to the basic considerations needed in a mobile game.

My primary takeaways from this project include:
* The general life cycle of a mobile game, insofar as the creation of a view and drawing surface, the starting of a running thread that can be paused by changing an internal bool, and the methods needed for ending and reinitializing a game
* The methods used for updating and (re)drawing sprites
* Collision resolution between player and enemy sprites (and between enemy sprites and the boundaries of the play field)
* Dealing with UI elements outside of the scope of the class through the use of handlers

## Code

The code is split into five components:
* MainActivity sets up the basic view with the toolbar, info button, score texts, and playfield
* GameThread constantly refreshes per the set target FPS (frames per second) and can be paused by setting 'is_running' to false
* GameView handles all game logic:
    1. it starts off by establishing the drawing surface and grabbing the score TextViews for later updating
    2. the 
 

## Looking Forward

## References

