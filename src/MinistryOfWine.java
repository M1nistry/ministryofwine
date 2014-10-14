import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Random;
import org.powerbot.script.Script;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.TilePath;

@Script.Manifest(name = "MinistryOfWine", description = "Drinks Jugs of Wine and then fills them with water for a profit", properties = "client=6; topic=0;")

public class MinistryOfWine extends PollingScript<ClientContext>
{
	private static State currentstate = State.FindState;
    private int bankIds[] = {3416, 3293};
    private int jugId = 1935, wineId = 1993, waterJugId = 1937, fountainId = 2771;
    
    private final GameObject fountain = ctx.objects.select().id(fountainId).nearest().poll();
    private final GameObject bankBooth = ctx.objects.select().id(bankIds).nearest().poll();   
    
    private final TilePath toFountain = new TilePath(ctx, new Tile[] { new Tile(3160, 3484, 0), new Tile(3163, 3489, 0) });
    private final TilePath toBank = new TilePath(ctx, new Tile[] {new Tile(3160, 3484, 0), new Tile(3150, 3481) });

    @Override
    public void poll()
    {
        switch (currentstate)
        {
            case WalkToBank:
            	WalkToBank();
            	break;
            case Banking:
            	Banking();
            	break;
            case Drinking:
            	Drinking();
            	break;
            case WalkToFountain:
            	WalkToFountain();
            	break;
            case Filling:
            	Filling();
            	break;
        	default:
        		FindState();
        		break;
        }
    }
    
    private void FindState()
    {
    	//if Inventory Empty?
    	currentstate = State.WalkToBank;
    	
    	//if Inventory Full of Wine?
    	//currentstate = State.Drinking;
    	
    	//if Inventory Full of Empty Jugs?
    	//currentstate = State.WalkToFountain;
    	
    	//if Inventory Full of Water Jugs?
    	//currentstate = State.Filling;
    	
    	//if Unsure of Current State Assume Start
    	//currentstate = State.WalkToBank;
    }
    
    private void WalkToBank()
    {
    	if(ctx.players.local().tile().distanceTo(toBank.end()) < 2)
    	{
    		currentstate = State.Banking; //Arrived at Bank
    	}
    	else
    	{
    		toBank.traverse();
    	}
    }
    
    private void Banking()
    {
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
    }
    
    private void Drinking()
    {
    	while (ctx.backpack.select().id(wineId).count() != 0) {
    		while (ctx.players.local().animation() == 829) Condition.sleep(100);
    		if (ctx.players.local().animation() != 829) ctx.backpack.select().id(wineId).poll().interact("Drink");
    		Condition.sleep(170);
    		ctx.input.move(ctx.backpack.select().id(wineId).poll().nextPoint());
    	}
		if (ctx.backpack.select().id(wineId).count() == 0) currentstate = State.WalkToFountain; //Done Drinking
    }
    
    private void WalkToFountain()
    {
    	if (!ctx.players.local().inMotion()) toFountain.traverse();
    	
    	if(ctx.players.local().tile().distanceTo(toFountain.end()) < 2)
    	{
    		currentstate = State.Filling; //Arrived at Fountain
    	}
    }
    
    private void Filling()
    {
    	final Component FILL_JUGS = ctx.widgets.component(1370, 38);
    	final Component CANCEL_FILL = ctx.widgets.component(1251, 49);
    	ctx.backpack.select().poll().interact("Use");
		Condition.sleep(Random.nextInt(100, 200));
		fountain.interact("Use", "Jug -> Fountain");
		Condition.sleep(1500);
		if (FILL_JUGS.valid()) FILL_JUGS.click();
		while (ctx.players.local().animation() == 829) Condition.sleep(Random.nextInt(800, 1200));
		while (CANCEL_FILL.valid()) Condition.sleep(500);
		if (ctx.backpack.select().id(jugId).count() == 0) {
			currentstate = State.WalkToBank;
			return;
		}
    }
    
    private enum State
    { 
    	FindState, WalkToBank, Banking, Drinking, WalkToFountain, Filling
    }
}

