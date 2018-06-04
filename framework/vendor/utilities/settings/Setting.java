package vendor.utilities.settings;

import java.util.HashMap;

public class Setting {
	
	private HashMap<String, SettingField> fields;
	
	public Setting(SettingField... fields){
		
		this.fields = new HashMap<>();
		
		for(SettingField field : fields){
			this.fields.put(field.getName(), field);
		}
		
	}
	
	public boolean save(String settingName, Object value, Object context)
			throws IllegalArgumentException{
		
		if(!hasField(settingName)){
			return false;
		}
		
		SettingField field = getField(settingName);
		
		field.setValue(value, context);
		
		return true;
		
	}
	
	public boolean hasField(String name){
		return fields.containsKey(name);
	}
	
	public SettingField getField(String name){
		return fields.get(name);
	}
	
}
