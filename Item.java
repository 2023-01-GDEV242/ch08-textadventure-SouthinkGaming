/**
 * Item class that holds informatio about the item description & weight of the item
 */
public class Item {
   // instance variables
   private String itemDescription;
   private int itemWeight;
        
   /**
   * Constructor for objects of class Item
   */
   public Item(){
       // initialize instance variables
       itemDescription = "";
       itemWeight = 0;
   }
        
   /**
   * Constructor for objects of class Item which sets the
   * instance variables with parameter values
   */
   public Item(String description, int weight){
       // initialize instance variables
       itemDescription = description;
       itemWeight = weight;
   }

   /**
    * Returns a description of the items contained in a room
    * @return A description of the item with weight
    */
    public String getItemDescription(){
        String itemString = "Item Description: ";
        itemString += this.itemDescription + "\n";
        itemString += "Item Weight: " + this.itemWeight;
        return itemString;
    }
}