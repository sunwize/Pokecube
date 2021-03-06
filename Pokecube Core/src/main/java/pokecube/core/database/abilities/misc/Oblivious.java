package pokecube.core.database.abilities.misc;

import net.minecraft.entity.EntityLivingBase;
import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.IPokemob.MovePacket;

public class Oblivious extends Ability
{

    @Override
    public void onUpdate(IPokemob mob)
    {
        mob.getMoveStats().infatuateTarget = null;
    }

    @Override
    public void onMoveUse(IPokemob mob, MovePacket move)
    {
        mob.getMoveStats().infatuateTarget = null;
        move.infatuate[0] = false;
    }

    @Override
    public void onAgress(IPokemob mob, EntityLivingBase target)
    {
    }

}
