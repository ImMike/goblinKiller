package scripts;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.Npc;

import java.util.concurrent.Callable;

@Script.Manifest(name="Hello World!", description = "How's it going!", properties = "author=Lyric; topic=999; client=4;")

public class HelloWorld extends PollingScript<ClientContext> {

    final static int GOBLIN_IDS[] = { 3017, 3029, 3034, 3033, 3032, 3031, 3030 };
    final static int FOOD_ID = 2140;

    @Override
    public void start() {
        System.out.println("Started");
    }

    @Override
    public void stop() {
        System.out.println("Stopped");
    }

    @Override
    public void poll() {
        //constant loop
        if(hasFood()){
            if(needsHeal()){
                heal();
            } else if(shouldAttack()){
                attack();
            }
        }
    }

    public boolean needsHeal(){
        return ctx.combat.health() < 6;
    }

    public boolean shouldAttack(){
        return !ctx.players.local().inCombat();
    }

    public boolean hasFood(){
        return ctx.inventory.select().id(FOOD_ID).count() > 0;
    }

    public void attack(){
        final Npc goblinToAttack = ctx.npcs.select().id(GOBLIN_IDS).select(new Filter<Npc>(){

            @Override
            public boolean accept(Npc npc) {
                return !npc.inCombat();
            }
        }).nearest().poll();

        goblinToAttack.interact("Attack");


        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().inCombat();
            }
        }, 200, 20);
    }


    public void heal(){
        Item foodToEat = ctx.inventory.select().id(FOOD_ID).poll();
        foodToEat.interact("Eat");

        final int startingHealth = ctx.combat.health();

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                final int currentHealth = ctx.combat.health();
                return currentHealth != startingHealth;
            }
        }, 150, 20);

    }

}
