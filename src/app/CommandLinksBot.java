package app;

import commands.*;
import errorHandling.BotError;
import utilities.interfaces.Commands;
import vendor.abstracts.CommandsLinker;
import vendor.interfaces.LinkableCommand;
import vendor.objects.CommandLinksContainer;
import vendor.objects.Link;
import vendor.objects.LinkParams;

public class CommandLinksBot extends CommandsLinker implements Commands {
	
	@Override
	public CommandLinksContainer createLinksContainer(){
		
		return new CommandLinksContainer(new Link[]
		{
			new Link(CommandHello.class),
			
			new Link(CommandHelp.class),
			
			new LinkParams(new Link(CommandMusic.class, MUSIC_PLAY),
					CommandMusic.CommandType.PLAY),
			
			new LinkParams(new Link(CommandMusic.class, MUSIC_PAUSE),
					CommandMusic.CommandType.PAUSE),
			
			new LinkParams(new Link(CommandMusic.class, MUSIC_SKIP),
					CommandMusic.CommandType.SKIP),
			
			new LinkParams(new Link(CommandMusic.class, MUSIC_SKIP_ALL1,
					MUSIC_SKIP_ALL2, MUSIC_SKIP_ALL3),
					CommandMusic.CommandType.SKIP_ALL),
			
			new LinkParams(new Link(CommandMusic.class, MUSIC_DISCONNECT),
					CommandMusic.CommandType.DISCONNECT),
			
			new LinkParams(new Link(CommandMusic.class, MUSIC_VOLUME),
					CommandMusic.CommandType.VOLUME),
			
			new LinkParams(new Link(CommandMusic.class, MUSIC_LIST),
					CommandMusic.CommandType.LIST),
			
			new Link(CommandClear.class),
			
			new Link(CommandSpam.class),
			
			new Link(CommandTerminate.class),
			
			new Link(CommandStop.class),
			
			new LinkParams(new Link(GameInteractionCommand.class, GAME),
					GameInteractionCommand.CommandType.INITIAL),
			
			new LinkParams(new Link(GameInteractionCommand.class, GAME_ADD),
					GameInteractionCommand.CommandType.ADD),
			
			new LinkParams(new Link(GameInteractionCommand.class, GAME_REMOVE),
					GameInteractionCommand.CommandType.REMOVE),
			
			new LinkParams(new Link(GameInteractionCommand.class, GAME_ROLL,
					GAME_ROLL_ALT), GameInteractionCommand.CommandType.ROLL),
			
			new LinkParams(new Link(GameInteractionCommand.class, GAME_LIST),
					GameInteractionCommand.CommandType.LIST),
			
			new Link(CommandTimer.class),
			
			new Link(CommandLanguage.class),
		
		}){
			
			@Override
			public LinkableCommand whenCommandNotFound(String commandName){
				
				return new BotError(lang("NoActionForCommand",
						buildVCommand(commandName)), false);
				
			}
			
		};
		
	}
	
	@Override
	public String formatCommand(String command){
		return buildVCommand(command);
	}
}
