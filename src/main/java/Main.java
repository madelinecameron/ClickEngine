/**
 * Copyright (C) 2016 Madeline Cameron.
 */

/**
 * Main class.
 *
 * @version 1.0.0
 */

public class Main {
  Log.d("ClickEngine", "Initializing game loop");
  GameLoop gameLoopObj = new GameLoop();
  Thread gameLoopThread = new Thread(gameLoopObj);
  gameLoopThread.start();
}
