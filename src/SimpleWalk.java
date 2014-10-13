import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.TilePath;
import org.powerbot.script.rt6.*;
import org.powerbot.script.Script;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Condition;


@Script.Manifest(name = "SimpleWalk", description = "Baby Steps!", properties = "client=6; topic=0;")

public class SimpleWalk extends PollingScript<ClientContext>
{
	private int bankIds[] = {3416, 3293};
	private int jugId = 1935, wineId = 1993, waterJugId = 1937, fountainId = 2771;
    private final GameObject fountain = ctx.objects.select().id(fountainId).nearest().poll();
    private final GameObject bankBooth = ctx.objects.select().id(bankIds).nearest().poll();

//Position Bank: 3150, 3481
//Position Halfway: 3160, 3484
//Position Fountain: 3163, 3489
    
    @Override
    public void poll()
    {
    	final TilePath toFountain = new TilePath(ctx, new Tile[] { new Tile(3160, 3484, 0), new Tile(3163, 3489, 0) });
    	final TilePath toBank = new TilePath(ctx, new Tile[] {new Tile(3160, 3484, 0), new Tile(3150, 3481) });
    	
        switch (state())
        {
	        //Work Flow:
	        //Start
	        //-WalkToBank
	        //Banking
	        //-EmptyInventory
	        //-FillInventory
	        //Drinking
	        //-DrinkWine
	        //WalkToFountain
	        //Filling
	        //-FillJugsWithWater
            	
            case Start:
            	//-WalkToBank
            	System.out.println("To Bank!");
            	toBank.traverse();
            	break;
        	
            case Banking:
                //-EmptyInventory
                //-FillInventory
            	break;
        	
            case Drinking:
                //-DrinkWine
            	break;
        	
            case WalkToFountain:
            	System.out.println("To Fountain!");
            	toFountain.traverse();
            	break;
        	
            case Filling:
                //-FillJugsWithWater
            	break;
        	
        }
    }

    private State state()
    {
    	if (ctx.backpack.select().id()
    }

    private enum State
    { 
    	Start, Banking, Drinking, WalkToFountain, Filling
    }
    
    private boolean CanSee(GameObject go)
    {
    	if (ctx.players.nearest(go).viewable().equals(true))
    	{
    		System.out.println("Can See: " + go.toString());
    		return true;
    	}
    	return false;
    }
    
    //For Jugs:
    //Start
    //-WalkToBank
    //Banking
    //-EmptyInventory
    //-FillInventory
    //Drinking
    //-DrinkWine
    //WalkToFountain
    //Filling
    //-FillJugsWithWater
    
    //For Vials;
    //Walk at bank
    //Empty Inventory
    //Fill Inventory with Empty Vials
    //Walk to fountain
    //Fill Vials with water
}
