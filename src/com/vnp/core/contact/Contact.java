package com.vnp.core.contact;

import java.util.ArrayList;
import java.util.List;

public class Contact {
	private String id;
	private String name;
	private List<Phone> lPhones = new ArrayList<Phone>();
	private List<String> lEmail = new ArrayList<String>();
	
	public List<String> getlEmail() {
		return lEmail;
	}

	public void setlEmail(List<String> lEmail) {
		this.lEmail = lEmail;
	}

	public List<Phone> getlPhones() {
		return lPhones;
	}

	public void setlPhones(List<Phone> lPhones) {
		this.lPhones = lPhones;
	}

	public Contact(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return id + " " + name;
	}

}
