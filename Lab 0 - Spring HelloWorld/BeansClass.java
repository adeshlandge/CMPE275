package com.adesh.springdemo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeansClass implements Greeter {

	private String name;
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getGreeting() {
		return "Hello world from "+this.name+"!";
	}
	
	public static void main(String[] args) {
		
		ClassPathXmlApplicationContext context = new
				ClassPathXmlApplicationContext("beans.xml");
		// retrieve bean 
		Greeter greet = context.getBean("greeter", Greeter.class);
		
		
		System.out.println(greet.getGreeting());
		
		
		context.close();

	}

}
