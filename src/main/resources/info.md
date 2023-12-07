# Welcome!

First off, thank you for purchasing this plugin, if you didn't then please consider doing so if this plugin benefits your server significantly.

This plugin allows your players to give items to the "Hoarder" during Hoarder events.

You can specify which items you want the Hoarder to pick from and if you want the Hoarder to give money to the player in exchange for the item.

The top players at the end of each Hoarder event will be rewarded with treasures (items)!

## Written with ❤️ by @Jsinco
## Support discord:
## Spigot page:




The goal of this plugin was to allow players to compete in hoarding competitions and earn prizes all within
the Hoarder plugin itself.

This is my 3rd re-iteration of the "Hoarder" plugin and the most customizable one yet.

# GUIS

There are 5 types of GUIs
MAIN, TREASURE, STATS, TREASURE_CLAIM, OTHER

Each GUI has an associated pool of dynamic items with it.

You can create your own GUIs by adding files to the /guis/ folder.

All GUIs must have, a title, a size, a type, and at least 1 item.

Within guis, items can be defined. Here is the declaration for a full item:

# GUI Items

```yaml
items:
  my_item:
    slot: 0 # The slot in the GUI *Necessary*
    material: GRASS_BLOCK # The material of the item *Necessary*
    name: 'A name' # The name of the item *Necessary*
    lore: [ 'A lore', 'A second line' ] # The lore of the item *Necessary, but can be empty "[]"*
    enchanted: false # Whether or not the item has enchant glint
    data: 0 # Custom model data or string for player head name
```

## Dynamic Items

Dynamic items are special items that go into their respective GUI type.
Example: The TREASURE gui type has the dynamic item called "treasure" which represents all the treasure items Hoarder has.

Not every part of a dynamic item is customizable. Dynamic items that are customizable can be modified in the
dynamicitems.yml file.

### Actions

Actions are special actions that can be performed when a player clicks on an item in a GUI.
A list of all the actions:

'[OPEN] guis/<gui_name>.yml' ; Opens a GUI
'[COMMAND] mycommand' ; Runs a command use -p to have the command be run as the player clicking and %player% to have the player's name be inserted into the command
'[CLOSE]' ; Closes the GUI
'[MESSAGE] &aHello' ; Sends a message to the player
'[BACK_PAGE]' ; Goes back a page in the GUI. Will only work on paginated GUI types (TREASURE, TREASURE_CLAIM, STATS)
'[NEXT_PAGE]' ; Goes forward a page in the GUI. Will only work on paginated GUI types (TREASURE, TREASURE_CLAIM, STATS)
'[SELL]' ; Sells to the Hoarder
'[CLAIM]' ; Claims a treasure
