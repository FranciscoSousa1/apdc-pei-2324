package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Value;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
public class CreateStringListUsersOutput {
	Gson g = new Gson();  
	public CreateStringListUsersOutput()
	{
		
	}
	public static String createString(List<Entity> entities, boolean isUser)
	{
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    Iterator<Entity> iterator = entities.iterator();
	    String res = "";
	    while (iterator.hasNext())
	    {
	    	Entity entity = iterator.next();
	    	Map<String, Value<?>> map = entity.getProperties();
	    	Iterator<Map.Entry<String, Value<?>>> entryIterator = map.entrySet().iterator();
	    	res = res + "(";
	    	while (entryIterator.hasNext())
	    	{
	    		Entry<String, Value<?>> entry = entryIterator.next();
	    		if (!isUser || (isUser && entry.getKey().equals("username")) || (isUser && entry.getKey().equals("email")) || (isUser && entry.getKey().equals("name")))
	    		{
		    		res = res + entry.getKey() + " : " + entry.getValue().get();
		    		if (entryIterator.hasNext())
		    		{
		    			res = res + ",<br>";
		    		}
		    		else {
		    			res = res + "),<br>";
		    		}
	    		}
	    	}
	    	if (!iterator.hasNext())
	    	{
	    		res = res + ")";
	    	}
	    }
	    return res;
	}

}
