## Discord MarketPlace Bot
### Backgound & Purpose
In Foundations of Software Engineering, we were assigned the project of creating a Discord Chat Bot that serves some purpose. After conducting research and discussion, we identified a need for a bot to facilitate items' trading, buying, and selling. Our bot will specifically target keyboard trading servers, computer part trading servers, and tech device trading servers. As a result, we have decided to create a Marketplace Discord Bot that can be deployed to any of these targeted groups, allowing users to come together and conduct business safely and conveniently within their respective communities.

### The Issue
// need to talk about the current issue and how since our bot does not yet exist in servers, users post thread/like listings that are all in different formats, hard to read, may exclude important info, might include too much info, etc

### Our Bot
As mentioned in the previous section, our MarketPlace Bot targets servers where users wish to buy & sell keyboard and computer parts. What makes our bot unique and special is that it is one of the few bots like it. That being said, our group did not have examples to based our work on, and it is completely our own envisionment of what a Discord trading bot should and should not entail.

Our bot allows users to create listings of the items they wish to sell. Specifically, the bot formats the information the user provides the same for each post they make. This so reviewing what's being sold is now

#### Bot Documentation
To view all documentation related to our bot, please use the following links:

[Group Bear Shared Google Drive](https://drive.google.com/drive/folders/1Sn4PxEXHuTDNMWvR-0BYmb5M2VSLNGK6?usp=sharing)

[Group Bear Sprint Board](https://github.com/orgs/CS5500-S-2023/projects/65?query=is%3Aopen+sort%3Aupdated-desc)

[Group Bear GitHub Repository](https://github.com/CS5500-S-2023/team-bear)

### How The Bot Works
// text

#### Join Our Production Server
Use the following invite link to join our Production Discord server: [Invite Link To Our Server](https://discord.gg/sFcdK3xj)

#### Adding The Bot To A Server
Use the following invite link to add the bot to a server: [Bot Invite Link](https://discord.com/api/oauth2/authorize?client_id=1093746738361270373&permissions=8&scope=bot)

Upon adding the bot to a server, two things immdediately occur. First, the server owner will receive the following DM from the bot:

![Screenshot (50)](https://user-images.githubusercontent.com/78775944/232926423-d039fda7-286d-4cd2-b9b9-d978e2218a4b.png)

If the owner selects "Bot Can Create The Channel", the bot will attempt to create a new text channel named "trading-channel". If a channel with that name already exists OR if "I'll Create The Channel" is selected, the server owner MUST call the command /createtradingchannel to create the trading channel. The owner MUST input a custom, unique name for the channel when calling the command. If they do not and they enter a name that already is present in the server, they will have to call the command again until a unique name is given.

Second, each member of the server, including the owner, will receive a DM from the bot asking the member to select the State and City they are located in from drop-down menus. An exact location is not necessary. The bot tracks this information to later attach it to listings created by members. (include image later)

#### New Member Joining A Server The Bot Is In

#### Creating A Listing

#### Viewing Your Own Listings

#### Updating Your Location

#### Creating A New Trading Channel
