/**
* Kevin Keitel, Connor Burke, Zach Pearson; CISC181-012 
* 5/19/2013, Project 
* 
*
*/


package com.example.app1;

import java.util.Comparator;

public class CompareSize implements Comparator<Planet> {
	@Override
	public int compare(Planet one, Planet two) {
		if (one.getSize() > two.getSize())
			return 1;
		else if (one.getSize() == two.getSize())
			return 0;
		else 
			return -1; 
	}

}
