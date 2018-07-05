package vendor;

import vendor.modules.Logger;
import vendor.modules.Logger.LogType;
import vendor.modules.Metrics;
import vendor.interfaces.Console;
import vendor.abstracts.AbstractMessageListener;
import vendor.utilities.CommandsThreadManager;
import vendor.objects.Buffer;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

public class FrameworkTemplate {
	
	public static JDA jda;
	public static String botToken;
	
	public static void startBot(Console console, AbstractMessageListener messageListener) throws Exception{
		
		boolean success = false;
		
		do{
			
			Logger.log("Starting the bot...", LogType.INFO);
			
			try{
				
				jda = new JDABuilder(AccountType.BOT).setToken(botToken)
						.buildBlocking();
				jda.addEventListener(messageListener);
				jda.setAutoReconnect(true);
				
				Metrics.startClock();
				Metrics.setJDA(jda);
				
				Logger.log("Bot started!", LogType.INFO);
				
				success = true;
				
			}
			catch(LoginException e){
				
				botToken = console
						.getInput("The bot token provided is invalid. Please enter a valid token here :");
				
				if(botToken == null || botToken.length() == 0)
					throw e;
				
				Logger.log("Application's Bot Token has been set to : "
						+ botToken, LogType.INFO);
				
			}
			
		}while(!success);
		
	}
	
	public static void stopBot(Console console) throws Exception{
		
		if(jda != null){
			
			Logger.log("Shutting down the bot...", LogType.INFO);
			
			boolean canStopBot = true;
			
			if(CommandsThreadManager.hasRunningCommands()){
				
				int conf = console
						.getConfirmation(
								"There are running commands, are you sure you want to stop the bot?",
								Console.QuestionType.YES_NO);
				
				if(conf == Console.NO){
					canStopBot = false;
				}
				else{
					
					int numberOfStoppedCommands = CommandsThreadManager
							.stopAllCommands();
					
					Logger.log("Stopped " + numberOfStoppedCommands
							+ " running commands before stopping the bot.",
							LogType.INFO);
					
				}
				
			}
			
			if(!canStopBot){
				
				Logger.log("Bot not stopped.", LogType.INFO);
				
			}
			else{
				
				jda.shutdownNow();
				
				jda = null;
				
				Buffer.get().emptyMemory();
				
				Logger.log("Bot has been shutdown!", LogType.INFO);
				
				Metrics.stopClock();
				Metrics.setJDA(null);
				
			}
			
		}
		else{
			Logger.log("The JDA was already closed, no action was taken.",
					LogType.INFO);
		}
		
	}
	
}
