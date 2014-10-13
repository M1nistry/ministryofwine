import org.powerbot.script.rt6.*;
import org.powerbot.script.rt6.Widgets;
import org.powerbot.script.Script;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Condition;
import org.powerbot.script.Input;

@Script.Manifest(name = "MinistryOfWine", description = "Drinks Jugs of Wine and then fills them with water for a profit", properties = "client=6; topic=0;")

public class MinistryOfWine extends PollingScript<ClientContext>
{
	private static State currentstate = State.Start;
    private int bankIds[] = {3416, 3293};
    private int jugId = 1935, wineId = 1993, waterJugId = 1937, fountainId = 2771;
    private final GameObject fountain = ctx.objects.select().id(fountainId).nearest().poll();
    private final GameObject bankBooth = ctx.objects.select().id(bankIds).nearest().poll();   
    
    @Override
    public void poll()
    {
    	final TilePath toFountain = new TilePath(ctx, new Tile[] { new Tile(3160, 3484, 0), new Tile(3163, 3489, 0) });
    	final TilePath toBank = new TilePath(ctx, new Tile[] {new Tile(3160, 3484, 0), new Tile(3150, 3481) });

    	
        switch (state())
        {
            case Start:
            	//-WalkToBank
            	System.out.println("State: Start");
            	//if at bank:
            	if(ctx.players.local().tile().distanceTo(toBank.end()) < 1.1)
            	{
            		//Open Bank
            		currentstate = State.Banking;
            	}
            	else
            	{
            		toBank.traverse();
            	}
            	break;
        	
            case Banking:
            	System.out.println("State: Banking");
            	ctx.bank.open();
                ctx.bank.depositInventory();
                if (ctx.backpack.select().count() == 0)
                {
                	ctx.bank.withdraw(wineId, 28);
                }
                if (ctx.backpack.select().id(wineId).count() > 0) 
            	{
            		ctx.bank.close();
            		currentstate = State.Drinking;
            	}
            	break;
        	
            case Drinking:
            	System.out.println("State: Drinking");
            	
        		while (ctx.players.local().animation() == 829) Condition.sleep(100);
        		if (ctx.players.local().animation() != 829) ctx.backpack.select().id(wineId).poll().interact("Drink");
        		Condition.sleep(200);
        		ctx.input.move(ctx.backpack.select().id(wineId).poll().nextPoint());
        		//Condition.sleep(Random.nextInt(980, 1020));
        		if (ctx.backpack.select().id(wineId).count() == 0) currentstate = State.WalkToFountain;
            	
            	break;
        	
            case WalkToFountain:
            	System.out.println("State: Fountain");
            	toFountain.traverse();
            	
            	//if at fountain:
            	System.out.println(ctx.players.local().tile().distanceTo(toFountain.end()));
            	if(ctx.players.local().tile().distanceTo(toFountain.end()) < 2)
            	{
            		//Fill Jugs
            		currentstate = State.Filling;
            	}
            	break;
        	
            case Filling:
                //-FillJugsWithWater
            	final Widgets widgets = ctx.widgets;
            	final Component FILL_JUGS = ;
            	for (final Item item : ctx.backpack.select().id(jugId))
            	{
            		item.click();
            		Condition.sleep(Random.nextInt(100, 200));
        			fountain.interact("Use", "Jug -> Fountain");
        			
            	}
            	
            	//Set state to start
            	break;
        	default:
        	
        		break;
        }
    }

    private State state()
    {
        //Work Flow:
        //Start
    	//Auto Starts on this State. Nothing to see here.
    	
    	//Banking
    	if(currentstate == State.Banking)
    	{
    		return State.Banking; 
    	}
    	
        //Drinking
    	if(currentstate == State.Drinking)
    	{
    		return State.Drinking; 
    	}
    	
        //WalkToFountain
    	if(currentstate == State.WalkToFountain)
    	{
    		return State.WalkToFountain; 
    	}
    	
        //Filling
    	if(currentstate == State.Filling)
    	{
    		return State.Filling; 
    	}

    	//Default:
    	return State.Start;
    }

    private enum State
    { 
    	Start, Banking, Drinking, WalkToFountain, Filling
    }
}

