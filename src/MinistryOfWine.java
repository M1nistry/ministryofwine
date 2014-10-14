import org.powerbot.script.rt6.*;
import org.powerbot.script.rt6.Widgets;
import org.powerbot.script.Script;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Condition;

@Script.Manifest(name = "MinistryOfWine", description = "Drinks Jugs of Wine and then fills them with water for a profit", properties = "client=6; topic=0;")

public class MinistryOfWine extends PollingScript<ClientContext>
{
	private static State currentstate = State.WalkToBank;
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
        		WalkToBank();
        		break;
        }
    }

    private enum State
    { 
    	FindState, WalkToBank, Banking, Drinking, WalkToFountain, Filling
    }
    
    private void FindState()
    {
    	//Will Replace Start
    	
    	//Inventory Empty?
    	//-Walk to bank
    	
    	//Inventory Full of Wine?
    	//-Drink Wine
    	
    	//Inventory Full of Empty Jugs?
    	//-Walk to Fountain
    	
    	//Inventory Full of Water Jugs?
    	//-Walk to Bank
    }
    private void WalkToBank()
    {
    	//-WalkToBank
    	System.out.println("State: Start");
    	//if at bank:
    	if(ctx.players.local().tile().distanceTo(toBank.end()) < 2)
    	{
    		//Open Bank
    		currentstate = State.Banking;
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
    	System.out.println("State: Drinking");
    	while (ctx.backpack.select().id(wineId).count() != 0) {
    		while (ctx.players.local().animation() == 829) Condition.sleep(100);
    		if (ctx.players.local().animation() != 829) ctx.backpack.select().id(wineId).poll().interact("Drink");
    		Condition.sleep(170);
    		ctx.input.move(ctx.backpack.select().id(wineId).poll().nextPoint());
    	}
		if (ctx.backpack.select().id(wineId).count() == 0) currentstate = State.WalkToFountain;
    	
    }
    
    private void WalkToFountain()
    {
    	System.out.println("State: Fountain");
    	if (!ctx.players.local().inMotion())toFountain.traverse();
    	
    	//if at fountain:
    	if(ctx.players.local().tile().distanceTo(toFountain.end()) < 2)
    	{
    		//Fill Jugs
    		currentstate = State.Filling;
    	}
    }
    
    private void Filling()
    {
        //-FillJugsWithWater
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
}

