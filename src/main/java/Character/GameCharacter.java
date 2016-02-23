package madelinecameron.clickengine.Character;

import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import madelinecameron.clickengine.GameState.GameEvent;
import madelinecameron.clickengine.GameState.GameEventType;
import madelinecameron.clickengine.GameState.GameState;
import madelinecameron.clickengine.GameState.Item;
import madelinecameron.clickengine.Misc.Utilities;
import madelinecameron.clickengine.config;

/**
 * Created by madel on 8/30/2015.
 */
public class GameCharacter {
    private HashMap<String, Object> attributes = new HashMap<>();
    private HashMap<String, Object> attributeLimits = new HashMap<>();
    private HashMap<String, Number> heartbeatModify = new HashMap<>();
    private HashMap<Integer, Integer> ownedItems = new HashMap<>();
    private HashMap<String, Integer> skillMap = new HashMap<>();
    private HashMap<String, Float> progressMap = new HashMap<>();

    public GameCharacter() {
        attributes.put("Money", config.STARTING_MONEY);
        attributes.put("PassiveIncome",
          config.STARTING_PASSIVE_INCOME ? config.STARTING_PASSIVE_INCOME : 0);

        loadAttributesFromConfig();
    }

    private void loadAttributesFromConfig() {
        Iterator<JSONObject> attributeIterator = config.CHAR_ATTRIBUTES.iterator()
        while(attributeIterator.hasNext()) {
          JSONObject currentAttributeObj = attributeIterator.next()
          attributes.put(currentAttributeObj.getString("name"), currentAttributeObj.getInt("startVal"));
          if(currentAttributeObj.getInt("limit") != Null) {
            attributeLimits.put(currentAttributeObj.getString("name"), currentAttributeObj.getInt("limit"))
          }
        }

        Iterator<JSONObject> heartbeatModifyIterator = config.HEARTBEAT_MODIFY.iterator()
        while(heartbeatModifyIterator.hasNext()) {
          JSONObject currentHeartbeatModifyObj = heartbeatModifyIterator.next()
          heartbeatModify.put(currentHeartbeatModifyObj.getString("name"), currentHeartbeatModifyObj.getInt("modifyVal"));
        }
    }

    public HashMap<String, Object> heartbeat() {
        Integer money = (int)attributes.get("Money");
        attributes.put("Money", money + (int)attributes.get("PassiveIncome"));

        Iterator heartbeatModifyIterator = heartbeatModify.entrySet().iterator()
        while(heartbeatModifyIterator.hasNext()) {
          Map.Entry modifyObj = (Map.Entry)heartbeatModifyIterator.next()
          attributes.put(modifyObj.getKey(), attributes.get(modifyObj.getKey()) + modifyObj.getValue())
        }

        return attributes;
    }

    public String getStatus() {
        Iterator<Integer> itemIterator = ownedItems.keySet().iterator();
        String currentStatusName = config.DEFAULT_STATUS;
        while(itemIterator.hasNext()) {
            Item currentItem = GameState.getItem(itemIterator.next());
            if(currentItem.type.toUpperCase() == "STATUS") {
                currentStatusName = currentItem.name;
            }
        }

        return currentStatusNameOIhISS;
    }
    public Set<Integer> getOwnedItems() { return ownedItems.keySet(); }
    public Set<String> getSkills() { return skillMap.keySet(); }
    public boolean hasSkill(String skillName) { return skillMap.keySet().contains(skillName); }
    public boolean isAttribute(String name) { return attributes.containsKey(name); }
    public Boolean ownsItem(Integer itemID) { return ownedItems.containsKey(itemID); }
    public Integer getOwnedItemQty(Integer itemID) { return ownedItems.get(itemID); }
    public Integer getSkillLevel(String skillName) { return skillMap.get(skillName); }
    public Object getAttr(String attrName) { return attributes.get(attrName); }
    public Object getAttrLimit(String attrName) { return attributeLimits.get(attrName); }
    public Float getProgression(String name) { return progressMap.get(name); }

    public void modifyAttrOrSkill(String name, Integer updateValue) {
        if(isAttribute(name)) {
            if(Integer.valueOf(attributes.get(name).toString()) + updateValue < 0) {
                attributes.put(name, 0);
            }
            else {
                if (attributeLimits.containsKey(name)) {  // Check for upper limit
                    if (Integer.valueOf(attributes.get(name).toString()) + updateValue <= Integer.valueOf(attributeLimits.get(name).toString())) {
                        attributes.put(name, Integer.valueOf(attributes.get(name).toString()) + updateValue);
                    } else {
                        attributes.put(name, Integer.valueOf(attributeLimits.get(name).toString())); //Set to max
                    }
                }
                else {
                    attributes.put(name, Integer.valueOf(attributes.get(name).toString()) + updateValue);
                }
            }
        }
        else {
            skillMap.put(name, skillMap.get(name) + updateValue);
        }
    }
    public void addSkill(String skillName) { skillMap.put(skillName, 0); }
    public void addSkill(String skillName, Integer defaultValue) { skillMap.put(skillName, defaultValue); }
    public void addItem(Integer itemID) {
        Log.d("DreamLife", "Adding item...");
        if(ownsItem(itemID)) {
            ownedItems.put(itemID, ownedItems.get(itemID) + 1);
        }
        else {
            ownedItems.put(itemID, 1);
        }

        String message = Utilities.getItemRecievedMessage(itemID);
        Log.d("DreamLife", "message: " + message);
        if(message != null) {
            Log.d("DreamLife", "Non-null message");
            GameState.addGameEvent(new GameEvent(message, GameEventType.ITEM_ADDED));
        }
        else {
            Item item = GameState.getItem(itemID);
            Log.d("DreamLife", "Null message");
            if(item.shouldDisplay) {
                GameState.addGameEvent(new GameEvent(String.format("Received %s", item.name), GameEventType.ITEM_ADDED));
            }
        }
    }
    public void addItem(Integer itemID, Integer qty) {
        Log.d("DreamLife", "Adding item...");
        if(ownsItem(itemID)) {
            ownedItems.put(itemID, ownedItems.get(itemID) + 1);
        }
        else {
            ownedItems.put(itemID, 1);
        }

        String message = Utilities.getItemRecievedMessage(itemID);
        Log.d("DreamLife", "message: " + message);
        if(message != null) {
            Log.d("DreamLife", "Non-null message");
            GameState.addGameEvent(new GameEvent(message, GameEventType.ITEM_ADDED));
        }
        else {
            Item item = GameState.getItem(itemID);
            Log.d("DreamLife", "Null message");
            if(item.shouldDisplay) {
                GameState.addGameEvent(new GameEvent(String.format("Received %s", item.name), GameEventType.ITEM_ADDED));
            }
        }
    }
    public void updateProgress(String name, Float progression) {
        if(progressMap.containsKey(name)) {
            progressMap.put(name, progressMap.get(name) + progression);
        }
        else {
            progressMap.put(name, progression);
        }

        GameState.addGameEvent(new GameEvent(String.format("%s% progress in %s", progression.toString(), name), GameEventType.PROGRESS));
    }

    public boolean buyItem(Integer itemId) {
        try {
            Item item = GameState.getItem(itemId);
            Utilities.causeEffects(item.results, this);
            modifyAttrOrSkill("Money", (int)getAttrLevel("Money") - item.cost);

            if(ownedItems.containsKey(item.id)) {
                ownedItems.put(item.id, ownedItems.get(item.id));
            }
            else {
                ownedItems.put(item.id, 1);
            }

            GameState.addGameEvent(new GameEvent(String.format("%s bought", item.name), GameEventType.ITEM_ADDED));

            return true;
        }
        catch(Exception e) {
            Log.e("DreamLife", e.toString());

            return false;
        }
    }

    public boolean sellItem(Integer itemId) {
        try {
            Item item = GameState.getItem(itemId);
            Utilities.removeEffects(item.results, this);
            modifyAttrOrSkill("Money", (int)getAttrLevel("Money") + item.getSellCost());

            ownedItems.put(item.id, ownedItems.get(item.id) - 1);

            GameState.addGameEvent(new GameEvent(String.format("%s sold", item.name), GameEventType.ITEM_LOST));
            return true;
        }
        catch(Exception e) {
            Log.e("DreamLife", e.toString());

            return false;
        }
    }
}
