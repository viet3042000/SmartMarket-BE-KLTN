package com.smartmarket.code.util;

import com.google.gson.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EJson {
	private JsonElement json;
	private Dictionary<String, String> errors;

	public EJson() {
		super();
		this.json = new JsonObject();
		this.errors = new Hashtable<String, String>();
	}

	public EJson(String source) {
		super();
		JsonParser jsonParser = new JsonParser();
		if (source != null && !source.isEmpty()) {
			this.json = jsonParser.parse(source);
		}
		this.errors = new Hashtable<String, String>();
	}

	public EJson(JsonObject source) {
		super();
		this.json = source;
		this.errors = new Hashtable<String, String>();
	}


	public void put(String key, Object value) {
		this.setValue(key, value);
	}

	public JsonObject jsonObject() {
		return json != null ? json.getAsJsonObject() : null;
	}

	public JsonArray jsonArray() {
		return json != null ? json.getAsJsonArray() : null;
	}

	public List<EJson> toArray() {
		List<EJson> result = new ArrayList<EJson>();
		for (int i = 0; i < json.getAsJsonArray().size(); i++) {
			result.add(new EJson(json.getAsJsonArray().get(i).getAsJsonObject()));
		}
		return result;
	}

	public void addError(String key, String message) {
		errors.put(key, message);
	}


	public boolean hasValue(String key) {
		if (json.getAsJsonObject().get(key) == null) {
			return false;
		}
		if (json.getAsJsonObject().get(key).isJsonNull()) {
			return false;
		}
		if (json.getAsJsonObject().get(key).toString().equals("")) {
			return false;
		}
		return true;
	}

	public BigDecimal BigDecimal(String key) {
		return BigDecimal.valueOf(hasValue(key) ? json.getAsJsonObject().get(key).getAsBoolean() == true ? 1 : 0 : 0);
	}

	public String jsonToString() {
		return json.toString();
	}

	public String getString(String key) {
		return hasValue(key) ? json.getAsJsonObject().get(key).getAsString() : null;
	}

	public Long getLong(String key) {
		return hasValue(key) ? json.getAsJsonObject().get(key).getAsLong() : null;
	}
	public Integer getInt(String key) {
		return hasValue(key) ? json.getAsJsonObject().get(key).getAsInt() : null;
	}

	public BigDecimal getBigDecimal(String key) {
		return hasValue(key) ? json.getAsJsonObject().get(key).getAsBigDecimal() : null;
	}

	public Number getNumber(String key) {
		return hasValue(key) ? json.getAsJsonObject().get(key).getAsNumber() : null;
	}

	public Boolean getBoolean(String key) {
		return hasValue(key) ? json.getAsJsonObject().get(key).getAsBoolean() : null;
	}

	public BigDecimal convertBooleanToBigDecimal(String key) {
		return BigDecimal.valueOf(hasValue(key) ? (json.getAsJsonObject().get(key).getAsBoolean() ? 1 : 0) : 0);
	}

	public Number convertBooleanToNumber(String key) {
		return hasValue(key) ? (json.getAsJsonObject().get(key).getAsBoolean() ? 1 : 0) : 0;
	}

	public Date getDate(String key) throws ParseException {
		if (!hasValue(key)) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.parse(getString(key));
	}

	public String getDateString(String key) throws ParseException {
		if (!hasValue(key)) {
			return null;
		}
		String strdate = getString(key);
		String result = "";
		if (!strdate.equals("") && strdate.length() == 10) {
			String[] arr = strdate.split("/");
			result = arr[2] + "" + arr[1] + "" + arr[0];
		}
		if(!strdate.equals("") && strdate.length() == 7){
			String[] arr = strdate.split("/");
			result = arr[1] + "" + arr[0] ;
		}
		return result;
	}

	public EJson getJSONObject(String key) {
		return hasValue(key) ? new EJson(json.getAsJsonObject().get(key).getAsJsonObject()) : null;
	}

	public List<EJson> getJSONArray(String key) {
		List<EJson> data = new ArrayList<EJson>();
		if (hasValue(key)) {
			JsonParser jsonParser = new JsonParser();
			JsonArray items = new JsonArray();
			JsonElement value = json.getAsJsonObject().get(key);
			if (value.isJsonArray()) {
				items = value.getAsJsonArray();
			} else {
				items = jsonParser.parse(value.getAsString()).getAsJsonArray();
			}

			for (int i = 0; i < items.size(); i++) {
				JsonObject item = items.get(i).getAsJsonObject();
				data.add(new EJson(item));
			}
		}
		return data;
	}

	private void setValue(String key, Object value) {
		Gson gson = new GsonBuilder().create();
		JsonElement element = gson.toJsonTree(value);
		json.getAsJsonObject().add(key, element);
	}



}