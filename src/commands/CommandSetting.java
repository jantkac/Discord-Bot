package commands;

import utilities.BotCommand;
import errorHandling.BotError;
import vendor.objects.ParametersHelp;

import java.util.function.Consumer;

public class CommandSetting extends BotCommand {
	
	@Override
	public void action(){
		
		tryAndChangeSetting("prefix", "prefix", (value) -> {
			sendMessage("You switched the prefix to `" + value + "`!");
		});
		
		tryAndChangeSetting("nickname", "nickname", (value) -> {
			setSelfNickname(value.toString());
			
			sendMessage("The nickname of the bot is now set to `" + value + "`!");
		});
		
	}
	
	public void tryAndChangeSetting(String settingName, String parameterName,
			Consumer<Object> onSuccess){
		
		if(hasParameter(parameterName)){
			
			String parameterContent = getParameter(parameterName).getParameterContent();
			
			if(parameterContent == null){
				
				Object defaultSettingValue = getSettings().getField(settingName).getDefaultValue();
				
				sendMessage("The default value for the setting " + code(settingName)
					+ " is : " + code(defaultSettingValue.toString()) + ".");
				
			}
			else{
				
				try{
					setSetting(settingName, parameterContent, onSuccess);
				}
				catch(IllegalArgumentException e){
					new BotError(this, e.getMessage());
				}
				
			}
			
		}
		
	}
	
	@Override
	public Object getCalls(){
		return new String[]
		{
			"setting", "settings"
		};
	}
	
	@Override
	public String getCommandDescription(){
		return "This command changes settings for the bot. Use the parameters below to change what you want to change!";
	}
	
	@Override
	public ParametersHelp[] getParametersDescriptions(){
		return new ParametersHelp[]
		{
			new ParametersHelp(
					"Changes the prefix used for each command. Default is `" + getSettings().getField("prefix").getDefaultValue() + "`.",
					"prefix"),
			new ParametersHelp(
					"Changes the bot's nickname. His default name is `" + getSettings().getField("nickname").getDefaultValue() + "`.",
					"nickname"),
		};
	}
	
}
