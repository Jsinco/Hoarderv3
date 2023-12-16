# Hoarder v1.0.0 (Copied from info.md in resources)
This plugin allows your players to sell a specified item for Hoarder points and (optionally) money!

You can specify which items you want the Hoarder to pick from and if you want the Hoarder to give money to the player in exchange for the item.

The top players at the end of each Hoarder event will be rewarded with treasures (items)!

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
    enchanted: false # Whether the item has the enchant glint
    data: 0 # Custom model data int or string for player head name
```

## Dynamic Items
Dynamic items are special items that go into their respective GUI type.
Example: The TREASURE gui type has the dynamic item called "treasure" which represents all the treasure items Hoarder has.

Not every part of a dynamic item is customizable. Dynamic items that are customizable can be modified in the
dynamicitems.yml file.

## Actions
Actions are special actions that can be performed when a player clicks on an item in a GUI.
These actions are also used for the default /hoarder command action ('default-command-action' in config.yml)
A list of all the actions:

- '[OPEN] guis/<gui_name>.yml' ; Opens a GUI
- '[COMMAND] mycommand' ; Runs a command use -p to have the command be run as the player clicking and %player% to have the player's name be inserted into the command
- '[CLOSE]' ; Closes the GUI
- '[MESSAGE] &aHello' ; Sends a message to the player
- '[BACK_PAGE]' ; Goes back a page in the GUI. Will only work on paginated GUI types (TREASURE, TREASURE_CLAIM, STATS)
- '[NEXT_PAGE]' ; Goes forward a page in the GUI. Will only work on paginated GUI types (TREASURE, TREASURE_CLAIM, STATS)
- '[SELL]' ; Sells to the Hoarder
- '[CLAIM]' ; Claims a treasure


# Placeholders
Hoarder comes with some placeholders that are automatically registered as long as you have PlaceholderAPI installed.
Placeholders:
- %hoarder_top<num>% ; Returns the specified positions name and points "Jsinco - 100"
- %hoarder_top_<num>_name% ; Returns the specified positions name "Jsinco"
- %hoarder_top_<num>_points% ; Returns the specified positions points "100"
- %hoarder_top_<num>_claimable% ; Returns the specified positions amount of claimable treasures "5"
- %hoarder_top_<num>_uuid% ; Returns the specified positions player uuid "144ce39d-301b-40a9-9788-0ca8cb23daf4"

- %hoarder_me_points% ; Returns the players points "100"
- %hoarder_me_claimable% ; Returns the players claimable treasures "5"

- %hoarder_active_material% ; Returns the active material "GRASS_BLOCK"
- %hoarder_active_materialformatted% ; Returns the active material formatted "Grass Block"
- %hoarder_active_sellprice% ; Returns the active sell price "4.72"
- %hoarder_active_time% ; Returns the amount of time remaining in the event "1h 2m 3s"

# Commands
! = Required
? = Optional

- /hoarder ; default command action
- /hoarder help ; shows help menu
- /hoarder reload ; reloads the plugin
- /hoarder claim ; claims a treasure
- /hoarder gui <gui!> ; opens a gui
- /hoarder event
    - restart ; restarts the event
    - price <price!> ; sets the price of the event material
    - time <mins!> ; sets the time remaining of the event
    - material <material!> ; sets the material of the event
- /hoarder treasure
    - add <weight!>  <identifier?> ; adds the item in your hand to the treasure pool
    - edit <identifier!> <weight!> <new-identifier!> ; edits the specified treasure item's weight and identifier
    - delete <identifier!> ; deletes the specified treasure item from the treasure pool
- /hoarder sell
    - container ; Attempts sells all items in the container you are looking at, if event is cancelled by another plugin nothing will happen. Example: LWC locked chest
    - inventory ; Attempts sells all items in your inventory

# Features

## Hoarder Events
At the start of a Hoarder event, Hoarder will pick a random item from the 'materials' section of your config.yml (or a random material from minecraft if you're using blacklist instead of whitelist).
Players can sell this picked item for 1 point and (optionally) money. The top players at the end of the event will be rewarded with treasures (items)!

## Economy
Hoarder supports economy by giving money to players whenever they sell items to the Hoarder.
Specify the sell price of an item when defining it in the 'materials' section of your config.yml OR you can use 'random-pricing' to
have prices be picked at random from a range. See config economy section for more info/usage.

Currently, the 2 supported economy providers are Vault and PlayerPoints. When using an economy provider that does not support decimals (cents),
such as PlayerPoints, the sell price will be rounded to the nearest whole number. Ex: $2.32 -> $2

## Treasure
Treasures are items that are claimable by players for being in a winning spot in a Hoarder event.
The treasure system works similar to a crate. You must add treasure items in game by using /hoarder treasure add <weight> while holding the item.
Weights are used to determine the chance of getting a certain item. The higher the weight, the higher the chance of getting that item.

See the commands section for command usage on adding, editing, and deleting treasures.

## Customization
Customize just about anything you'd like. GUIs, messages, items, winners & rewards, and treasures are all among the customizable features of Hoarder.

## Lang
Hoarder comes with 5 different pre-translated languages. These language files are fully customizable and can be found in the /lang/ folder.
Want to add your own lang file? Just create a new yml file in the lang folder and follow the format of all the other lang files!

...

### Written with ❤️ by Jsinco
### Discord:
### Spigot page: