/**
* Kevin Keitel, Connor Burke, Zach Pearson; CISC181-012 
* 5/19/2013, Project 
* 
*
*/


package com.example.app1;

import java.util.Comparator;


public class CompareMoons implements Comparator<Planet>{
	@Override
	public int compare(Planet one, Planet two) {
		return (one.getMoons() - two.getMoons());
	}
	
}
