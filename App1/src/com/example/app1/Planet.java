/**
* Kevin Keitel, Connor Burke, Zach Pearson; CISC181-012 
* 5/19/2013, Project 
* 
* data-type definition for planets
*/


package com.example.app1;

public class Planet implements Comparable<Planet>{
		Double size; //in Earth Mass'
		Boolean rings;
		Integer moons;
		String name;
		String solarSystem;
		Double averageTemperature;
		String primaryCompositionElement; //i.e., rock or gas
		Double distanceFromSun; //in MegaMeters
		
		public Double getSize() {
			return size;
		}

		public void setSize(Double size) {
			this.size = size;
		}

		public Boolean hasRings() {
			return rings;
		}

		public void setRings(Boolean rings) {
			this.rings = rings;
		}

		public Integer getMoons() {
			return moons;
		}

		public void setMoons(Integer moons) {
			this.moons = moons;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSolarSystem() {
			return solarSystem;
		}

		public void setSolarSystem(String solarSystem) {
			this.solarSystem = solarSystem;
		}

		public Double getAverageTemperature() {
			return averageTemperature;
		}

		public void setAverageTemperature(Double averageTemperature) {
			this.averageTemperature = averageTemperature;
		}


		public String getPrimaryCompositionElement() {
			return primaryCompositionElement;
		}

		public void setPrimaryCompositionElement(String primaryCompositionElement) {
			this.primaryCompositionElement = primaryCompositionElement;
		}

		public Double getDistanceFromSun() {
			return distanceFromSun;
		}

		public void setDistanceFromSun(Double distanceFromSun) {
			this.distanceFromSun = distanceFromSun;
		}

		public Planet(Double size, Boolean rings, Integer moons, String name, String solarSystem, 
				Double averageTemperature, Double distanceFromSun, 
				String primaryCompositionElement) {
			
			this.size = size; //in Earth Mass
			this.rings = rings; 
			this.moons = moons; 
			this.name = name;
			this.solarSystem = solarSystem;
			this.averageTemperature = averageTemperature;
			this.primaryCompositionElement = primaryCompositionElement;
			this.distanceFromSun = distanceFromSun; //in MegaMeters
			
		}
		
		//same as above constructor, only our boolean rings takes an int now for storing in sqlite
		public Planet(Double size, Integer ringsInt, Integer moons, String name, String solarSystem, 
				Double averageTemperature, Double distanceFromSun, 
				String primaryCompositionElement) {
			
			this.size = size;
			this.ringsInt = ringsInt; //make more info?
			this.moons = moons; //make Integer into an arraylist
			this.name = name;
			this.solarSystem = solarSystem;
			this.averageTemperature = averageTemperature;
			this.primaryCompositionElement = primaryCompositionElement;
			this.distanceFromSun = distanceFromSun;
			
		}		
				
		public Planet(String name) {
			this.name = name;
		}
		
		public String toString(){
			return this.getName();
		}
		
		
		//way of storing booleans in DB
		int ringsInt;		
		public int getRingsInt(){
			return this.ringsInt;
		}
		
	
		//"natural ordering" by distance from sun
	@Override
	public int compareTo(Planet p) {
		if (this.getDistanceFromSun() < p.getDistanceFromSun())
			return -1;
		else if (this.getDistanceFromSun() == p.getDistanceFromSun())
			return 0;
		else return 1;
	}
	
	
	
}
