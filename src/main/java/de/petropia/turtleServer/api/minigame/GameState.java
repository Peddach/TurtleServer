package de.petropia.turtleServer.api.minigame;

public enum GameState {
    WAITING,    //No players in arena
    STARTING,  //First player joined and countdown is started
    INGAME,    //All players are playing the game
    ENDING,   //Game is finished
}
