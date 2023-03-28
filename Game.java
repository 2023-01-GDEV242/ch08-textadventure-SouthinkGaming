/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */

public class Game {
    private Parser parser;
    private Room currentRoom;
    private Room prevRoom;
    private Room roomStack[];
    private int top;

    /**
     * Create the game and initialise its internal map.
     */
    public Game() {
        createRooms();
        parser = new Parser();
        roomStack = new Room[500];
        top = -1;
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms() {
        Room outside, theater, pub, lab, office, welcome_center, event_center, warehouse,
        gate, dorm, gym, pool, garden, food_court, stadium;

        // declare Item referance variables
        Item bed, drinksAndFood, laptop, keys;
        
        // define each item object
        bed = new Item("Relaz and sleep for a little bit ", 100);
        drinksAndFood = new Item("Soft drinks, wings, and burgers to eat and drink.", 100);
        laptop = new Item("Laptop you access to make a fake id so can get into the office.", 800);
        keys = new Item("Key stand: Contains keys for other rooms ", 400);

        // create the rooms by addint the corresponding Items to each room
        outside = new Room("Outside the main entrance of the university, you see a butiful campus green.");
        theater = new Room("In a lecture theater, there are several classrooms that students can go into.");
        pub = new Room("In the campus pub, there are " + drinksAndFood);
        lab = new Room("In a computing lab, there is a " + laptop);
        office = new Room("In the computing admin office" + keys);
        welcome_center = new Room("Helps transfer/new students with adjusting to the " +
        "campus.");
        event_center = new Room ("Plans events for faculty, staff, and students.");
        warehouse = new Room ("Used as storage for the campus.");
        gate = new Room ("Allows visitors in when the campus is open.");
        dorm = new Room ("Housing for students who can't commute to campus.");
        gym = new Room ("Allows students to exsersice on campus and not pay for a gym " +
        "membership.");
        pool = new Room ("Allows students to swim or other pool related activities.");
        garden = new Room ("Used to grow fresh vegitables for students to eat during a "
        + "meal.");
        food_court = new Room ("Serves food of people.");
        stadium = new Room ("Used for competative and casual sports like football"
        + "or soccer");
        
        // initialise room exits
        outside.setExit("north", gate);
        outside.setExit("south", welcome_center);
        outside.setExit("east", dorm);
        outside.setExit("west", pub);
        
        gate.setExit("south", outside);
        
        dorm.setExit("north", gym);
        dorm.setExit("south", warehouse);
        dorm.setExit("east", food_court);
        dorm.setExit("west", outside);
        
        food_court.setExit("south", garden);
        food_court.setExit("west", dorm);
        
        warehouse.setExit("north", dorm);
        warehouse.setExit("south", event_center);
        warehouse.setExit("east", garden);
        warehouse.setExit("west", welcome_center);
        
        gym.setExit("south", dorm);
        gym.setExit("east", pool);
        
        pool.setExit("west", gym);
        
        welcome_center.setExit("north", outside);
        welcome_center.setExit("south", office);
        welcome_center.setExit("east", warehouse);
        welcome_center.setExit("west", stadium);
        
        office.setExit("north", welcome_center);
        office.setExit("east", event_center);
        
        event_center.setExit("north", warehouse);
        event_center.setExit("west", office);

        garden.setExit("north", food_court);
        garden.setExit("west", warehouse);
        
        pub.setExit("north", lab);
        pub.setExit("south", stadium);
        pub.setExit("east", outside);
        pub.setExit("west", theater);
        
        theater.setExit("east", pub);

        stadium.setExit("north", pub);
        stadium.setExit("east", welcome_center);
        
        lab.setExit("south", pub);

        currentRoom = outside;  // start game outside
        
        prevRoom = null;
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("Type '" + CommandWord.HELP + "' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) {
        boolean wantToQuit = false;

        CommandWord commandWord = command.getCommandWord();

        switch (commandWord) {
            case LOOK:
                look();
                break;

            case HELP:
                printHelp();
                break;

            case GO:
                goRoom(command);
                break;

            case QUIT:
                wantToQuit = quit(command);
                break;

            case EAT:
                eat();
                break;

            case BACK:
                back();
                break;
        }
        return wantToQuit;
    }

    // implementations of user commands:
    private void eat(){
        System.out.println("You finished eating and you aren't hungary any more.");
    }

    private void look(){
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() {
        System.out.println("You are lost. You are alone. You wander.");
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /** 
     * Try to go in one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command) {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        } else {
            push(currentRoom);
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
        }
    }
    
    private void back() {
        currentRoom = pop();
        if(currentRoom != null) {
            System.out.println(currentRoom.getLongDescription());
        }
    }
    
    private void push(Room room) {
        if (top == roomStack.length-1){
            System.out.println("Room stack is full");
        } else {
            roomStack[++top] = room;
        }
    }
    
    private Room pop() {
        if (top < 0) {
            System.out.println("Sorry, you are outside on the campus green and there is no previous room to go to.");
            return null;
        } else {
            return roomStack[top--];
        }
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        } else {
            return true;  // signal that we want to quit
        }
    }
}