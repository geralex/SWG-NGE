package script.space.crafting;

import script.library.space_crafting;
import script.library.space_transition;
import script.library.utils;
import script.menu_info;
import script.menu_info_types;
import script.obj_id;
import script.string_id;

public class chassis_deed extends script.base_script
{
    public chassis_deed()
    {
    }
    public static final string_id MNU_CREATE_VEHICLE = new string_id("sui", "create_vehicle");
    public static final String STF = "chassis_npc";
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        return SCRIPT_CONTINUE;
    }
    public int OnGetAttributes(obj_id self, obj_id player, String[] names, String[] attribs) throws InterruptedException
    {
        int idx = utils.getValidAttributeIndex(names);
        if (idx == -1)
        {
            return SCRIPT_CONTINUE;
        }
        float hp = getFloatObjVar(self, "ship_chassis.hp");
        if (hp < 0)
        {
            return SCRIPT_CONTINUE;
        }
        float mass = getFloatObjVar(self, "ship_chassis.mass");
        if (hp < 0)
        {
            return SCRIPT_CONTINUE;
        }
        float currentHp = hp;
        if (hasObjVar(self, "ship_chassis.currentHp"))
        {
            float newHp = getFloatObjVar(self, "ship_chassis.currentHp");
            if (newHp > 0)
            {
                currentHp = newHp;
            }
        }
        String type = getStringObjVar(self, "ship_chassis.type");
        if (type == null || type.equals(""))
        {
            return SCRIPT_CONTINUE;
        }
        if (hasObjVar(self, "ship_chassis.multifactional") && getBooleanObjVar(self, "ship_chassis.multifactional") == true)
        {
            if (type.equals("vwing"))
            {
                names[idx] = "reb_pilot_cert_required";
                attribs[idx] = utils.packStringId(new string_id("skl_n", "pilot_rebel_navy_starships_02"));
                idx++;
                names[idx] = "imp_pilot_cert_required";
                attribs[idx] = utils.packStringId(new string_id("skl_n", "pilot_imperial_navy_starships_02"));
                idx++;
                names[idx] = "neu_pilot_cert_required";
                attribs[idx] = utils.packStringId(new string_id("skl_n", "pilot_neutral_starships_02"));
                idx++;
            }
        }
        else 
        {
            names[idx] = "pilotSkillRequired";
            attribs[idx] = getSkillRequiredForShip(self, type);
            idx++;
        }
        names[idx] = "chassisHitpoints";
        attribs[idx] = Float.toString(currentHp) + "/" + Float.toString(hp);
        idx++;
        names[idx] = "chassisMass";
        attribs[idx] = Float.toString(mass);
        idx++;
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        if (!utils.isNestedWithin(self, player))
        {
            return SCRIPT_CONTINUE;
        }
        int mnuColor = mi.addRootMenu(menu_info_types.SERVER_MENU1, MNU_CREATE_VEHICLE);
        if (mnuColor > -1 && ((getContainedBy(self) != getOwner(self)) || isGod(player)))
        {
            String template = utils.getTemplateFilenameNoPath(self);
        }
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (!utils.isNestedWithin(self, player))
        {
            return SCRIPT_CONTINUE;
        }
        String template = utils.getTemplateFilenameNoPath(self);
        if (item == menu_info_types.SERVER_MENU1)
        {
            obj_id datapad = utils.getDatapad(player);
            if (utils.getIntScriptVar(self, "chassis_deed.inUse") == 1)
            {
                return SCRIPT_CONTINUE;
            }
            if (isIdValid(datapad))
            {
                utils.setScriptVar(self, "chassis_deed.inUse", 1);
                String type = getStringObjVar(self, "ship_chassis.type");
                float hp = getFloatObjVar(self, "ship_chassis.hp");
                float currentHp = hp;
                if (hasObjVar(self, "ship_chassis.currentHp"))
                {
                    currentHp = getFloatObjVar(self, "ship_chassis.currentHp");
                }
                float mass = getFloatObjVar(self, "ship_chassis.mass");
                obj_id newShip = space_crafting.createChassisFromDeed(player, self, hp, currentHp, mass, type);
                if (!isIdValid(newShip))
                {
                    CustomerServiceLog("ship_deed", "PLAYER: " + player + "(" + getPlayerName(player) + ") attempted and FAILED to create a ship from DEED:" + self + " which provided the Ship Type: " + type + " MASS: " + mass + " MAX CHASSIS HP: " + hp + " and CURRENT CHASSIS HP: " + currentHp);
                    utils.removeScriptVar(self, "chassis_deed.inUse");
                    return SCRIPT_CONTINUE;
                }
                CustomerServiceLog("ship_deed", "PLAYER: " + player + "(" + getPlayerName(player) + ") created SHIP: " + newShip + " from DEED:" + self + " which provided the Ship Type: " + type + " MASS: " + mass + " MAX CHASSIS HP: " + hp + " and CURRENT CHASSIS HP: " + currentHp);
                return SCRIPT_CONTINUE;
            }
            else 
            {
                utils.removeScriptVar(self, "chassis_deed.inUse");
            }
            space_transition.handlePotentialSceneChange(player);
        }
        return SCRIPT_CONTINUE;
    }
    public String getSkillRequiredForShip(obj_id deed, String type) throws InterruptedException
    {
        if (type == null || type.equals("") || !isValidId(deed) || !exists(deed))
        {
            return "";
        }
        switch (type) {
            case "firespray":
                return "@space_crafting_n:all_master";
            case "hutt_light_s01":
            case "hutt_light_s02":
                return "@skl_n:pilot_neutral_novice";
            case "hutt_medium_s01":
            case "hutt_medium_s02":
                return "@skl_n:pilot_neutral_starships_01";
            case "blacksun_light_s01":
            case "blacksun_light_s02":
            case "blacksun_light_s03":
            case "blacksun_light_s04":
            case "hutt_heavy_s01":
            case "hutt_heavy_s02":
                return "@skl_n:pilot_neutral_starships_02";
            case "blacksun_medium_s01":
            case "blacksun_medium_s02":
            case "blacksun_medium_s03":
            case "blacksun_medium_s04":
                return "@skl_n:pilot_neutral_starships_03";
            case "blacksun_heavy_s01":
            case "blacksun_heavy_s02":
            case "blacksun_heavy_s03":
            case "blacksun_heavy_s04":
            case "havoc":
                return "@skl_n:pilot_neutral_starships_04";
            case "hutt_turret_ship":
            case "gunship_neutral":
            case "yt1300":
                return "@skl_n:pilot_neutral_master";
            case "tie_light_duty":
                return "@skl_n:pilot_imperial_navy_novice";
            case "tiefighter":
                return "@skl_n:pilot_imperial_navy_starships_01";
            case "tie_in":
                return "@skl_n:pilot_imperial_navy_starships_02";
            case "tiebomber":
            case "tieinterceptor":
                return "@skl_n:pilot_imperial_navy_starships_03";
            case "tieadvanced":
            case "tieaggressor":
                return "@skl_n:pilot_imperial_navy_starships_04";
            case "tieoppressor":
            case "tiedefender":
            case "decimator":
            case "gunship_imperial":
                return "@skl_n:pilot_imperial_navy_master";
            case "z95":
                return "@skl_n:pilot_rebel_navy_novice";
            case "ywing":
                return "@skl_n:pilot_rebel_navy_starships_01";
            case "ywing_longprobe":
                return "@skl_n:pilot_rebel_navy_starships_02";
            case "xwing":
                return "@skl_n:pilot_rebel_navy_starships_03";
            case "awing":
            case "twing":
                return "@skl_n:pilot_rebel_navy_starships_04";
            case "bwing":
            case "ykl37r":
            case "gunship_rebel":
                return "@skl_n:pilot_rebel_navy_master";
            default:
                return "";
        }
    }
}
