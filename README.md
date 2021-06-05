# FGO-Tracker
## Introduction
Have you ever wanted to keep better track of your upgrade materials, and what your servants will need, so you don't end up wasting them on [_insert hated servant here_], only to find out that [_insert waifu/husbando/farming machine here_] really needed it, and now you've condemned yourself to waiting for months or doing mindless farming so you can finally get that sweet sweet final ascension jpeg? Have you been using FGO Manager already, but didn't like the fact that half the time Google Sheets decides to poop itself and only load half of the images and/or data, and only do that very slowly too?

If the answer is yes to either of those, than this app is for you! That last part was my personal experience with FGO Manager (as much as I love it), and after trying to seek help with it on the sub, I half jokingly mentioned that I might just make a new version myself. And so I did. Kinda. This is not a complete adaptation of FGO Manager (at least not yet), but I do want to implement as many of its features as I possibly can, and I started with what I consider to be the most important feature of FGO Manager: the roster and planner (including inventory). With those done, I believe this is ready to be released and used, while I continue working on it, potentially with help from helpful peoples.

## Quickstart Guide
1. Download the latest release you want and extract
2. Run the exe
3. Add your servants to the roster manually or import from FGO Manager
4. Update your inventory in either of the planner tabs manually, or import from FGO Manager
5. Add servants from your roster to the regular planner, or import from FGO Manager

## Full Usage
### General info
Download whichever release you want from the releases (stable release is the less frequent, more stable version that only gets updated when a full functionality has been tested enough to seem mostly bug free, while the beta version is the new stuff that works _in theory_) and extract it, preferably to its own folder. You will see an exe file and a "jre" folder. To start the app, just run the exe.

When the app starts it will check if there's a newer version of the data from the [Atlas DB](https://apps.atlasacademy.io/db/) than what's currently saved, and if so will download it and save it. On subsequent starts, if the saved data is up to date, it will load it from the file, significantly speeding up the startup time (in other words, the first time you start will be kinda slow, but otherwise the app starts up fast, except for the occasions when the DB had an update). This includes the images for the material icons, and anything else that might use images later.

The app also saves your user data (the servants in your roster, your inventory, etc.), and loads it from those files, so if you want a fresh start, or want to back your data up, you can do that very easily, just like you'd do it with any normal folder. Both the user data, and the DB cache are saved in a "data" folder the app creates next to itself, and all of them are saved in plain text json files (since there's really no info here that needs to be hidden or encrypted), which also allows easy editing if you feel like it (though be aware that messing with the structure of the files will, of course, lead to the app failing, in those cases you can just delete or remove the data folder, and let the app recreate it).
### Tables
The tables on all current tabs work basically the same, they are fairly straightforwad to use, but here's a how-to anyway.
### Roster Tab
The core of the current feature set is the roster table. As you might expect, it's just a big table for you to record your servants, their levels, skill levels, etc., both for you to keep track of it, as well as for the rest of the app to use it.

![image of roster tab](https://i.imgur.com/9DVvefj.png)

One slight problem with how the GUI works is that you can only use rows that have been "initialized". By default, the app will always add enough valid rows to fill one full page of the table (at least on my 1080p monitor), but if you want to add or remove rows, you can use the context menu (right click). I also add a black line to separate the valid and invalid rows, because for some inexplicable reason, they look the exact same.
### LT Planner and Planner Tabs
The second part of the initial features, the planner tabs. The Long Term Planner might be surprising at first, because it's not just a copy of the L-T sheet in FGO Manager (the regular Planner _is_ basically that though), and instead what I considered "long term" is just maxing out the skills and levels of all of the servants you have. What this means is that the LT Planner will always have all of your servants in your roster, with their desired levels all set to max.
![image of planner tab](https://i.imgur.com/x95ng6d.png)

In contrast, the regular Planner tab is where you'll want to add your servants you want to plan with, and see what mats you'll need for the level and skill levels you intend to get for them, as well as how much you'll have of each material after you do those.
### Adding New Servants
To add new servants, double click in a cell in the name column (in a valid row) and start typing a servant's name in the textbox that appears. Once you see the servant you want in the autocomplete, just click on their name (another weird issue is that not the whole line of the autocomplete menu works, you have to click specifically on the area where the name is), and the row will be filled up with data, all starting at their default level on the roster tab, and at the current level from their roster when adding to the planner tab. After that, just edit each value you'd like (level, bond, desired level etc.) and you're done. Note: for simplicity, currently the NP damage uses the highest rank NP your servant can have, i.e. it assumes you've done all rank-ups and interludes for them (even if they're level 1).
### Removing Servants
To remove servants, you have a few options.
* Edit their name and just delete it
* Click "clear row" in the context menu
* Click "delete row" in the context menu. This also removes the row itself, meaning the rest of the table will move up
### Importing from FGO Manager
I assume many people, like me, who'd want to use the app have already been using FGO Manager, so for ease of use, I've added a feature to import your roster (as well as your inventory and your entries in the L-T Planner) from FGO Manager.

To import your roster do the following:
1. Open FGO Manager and the Roster/L-T sheet on it (depending on whether you want to import your roster, or something else)
2. Click File -> Download -> Comma-separated values
3. In FGO Tracker, right click on the table you want to import to and choose import
4. Choose the .csv file you just downloaded

If for some reason, the app couldn't identify some of your servants or materials (most likely because the app is set to NA, and you tried importing JP-only content), you will get a warning message, listing all the names of the servants it couldn't find automatically. Be warned, the import **_replaces_** the current data in the table, because I'm going to assume that if you've been using FGO Manager, you're going to import your servants from there first, instead of adding some here, and then trying to add the ones from the Manager on top of them.
## Issues and pull requests
As someone who already has more hobbies than time even without developing this, I'll gladly accept people chipping in to help, however, ultimately I still consider this my baby, so I mostly want to code the actual features myself, so if you want to help, please prioritize the issues labeled "help wanted", before trying to implement full new features (if you have an idea that I don't already have in my issues, probably with the "epic" label somewhere, please talk to me about it first, before starting to code, by making a new issue with the "suggestion" label).

As other developers might notice, the code of this app is probably pretty atrocious already, so I don't _really_ have code style rules and other stuff that I'd require from pull requests, but I do want you to test whatever you submit **thoroughly**. As I said, I don't exactly have a whole lot of time, and I definitely don't have enough to test other people's code.
## Credits and Support
The app is built on the database from [Atlas DB](https://apps.atlasacademy.io/db/), which, in theory, is an absolutely up-to-date database of the game data, and this app was _very_ heavily inspired by [FGO Manager by Zuth](https://www.reddit.com/r/grandorder/comments/cz3hak/tool_fgo_manager_v10/). Without them, I never would've thought of making this, and this app wouldn't work in the first place.

While the app is completely free and open-source (because honestly, I wouldn't pay for just a tool like this, so I don't expect others to pay either), if you decide that you appreciate my sleepless nights I spent on this (being too excited about my ideas to sleep), you can drop a few SQ my way here (so maybe next time Jalter comes around with a banner, she won't abandon me in the dust again):

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/E1E64VBGR)
