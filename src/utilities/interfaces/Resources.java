package utilities.interfaces;

import vendor.objects.Language;
import vendor.utilities.settings.*;

/**
 * Contient les constantes du bot
 * 
 * @author Stephano
 */
public interface Resources {
	
	String PREFIX = "!!";
	char PARAMETER_PREFIX = '-';
	
	String BUFFER_GAMEPOOL = "GAMEPOOL";
	String BUFFER_SPAM = "SPAM_CONDITION";
	String BUFFER_LANG = "LANGUAGE";
	String BUFFER_SETTINGS = "SETTINGS";
	
	Language[] languages =
	{
		new Language("en", "US", "anglais", "eng", "english", "en"),
		new Language("fr", "CA", "francais", "français", "fra", "french", "fr"),
	};
	
	SettingField[] SETTINGS =
	{
		new TextNotEmptyField("prefix", "PREFIX", PREFIX),
		new CharField("param_prefix", "PARAMETER_PREFIX", PARAMETER_PREFIX),
		new TextNotEmptyField("nickname", "NICKNAME", "Vhoyon"),
		new BooleanField("confirm_stop", "SHOULD_CONFIRM_STOP", true),
		new IntegerField("volume", "DEFAULT_VOLUME", 60, 0, 100)
	};
	
}
