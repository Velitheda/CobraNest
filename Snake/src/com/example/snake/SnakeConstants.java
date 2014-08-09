//Written by Jasmine Vickery
//Constants that can be altered here to tweak some of the settings of the game

package com.example.snake;

public class SnakeConstants { 
	
	public static final int SIZE = 12;

	//these integers are simply an easy way of representing directions
    public static final int LEFT = 0;
    public static final int RIGHT = LEFT + 1;
    public static final int UP = RIGHT + 1;
    public static final int DOWN = UP + 1;
    public static final int STOPPED = DOWN + 1;

    //The option to hard-code a consistent starting point for the snake
    public static final int SNAKE_START_X = 0;
    public static final int SNAKE_START_Y = 0;

    //The starting speed of the timer
    public static final int TIMER_START = 150;
    //The amount things speed up by each time a new level is reached
    public static final int TIMER_CHANGE = 10;

    public static final int MAX_LEVEL = 10;

    //The number of segments in the barriers
    public static final int BARRIER_LENGTH = 20;
    //The rough number of times a barrier will change direction when it is being generated
	public static final int BARRIER_CHANGE = BARRIER_LENGTH / 10;

	//The speed at which strawberries disappear and instantly reappear somewhere else,
	//partly to resolve the possible problem of it spawning inside an unaccessible hole created by the barriers
	public static final int STRAWBERRY_MOVE_RATE = 1;

}