/**
* Kevin Keitel, Connor Burke, Zach Pearson; CISC181-012 
* 5/19/2013, Project 
* 
* graphing either planet data across list or for a single planet
*/

package com.example.app1;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class PlanetGraphView extends View {

		// the width and height of the current game view
		Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int width = display.getWidth();		
		int height = display.getHeight();

		//instance for "getting" the data to graph (graphList is static)
		PlanetActivity mainActivityInstance = new PlanetActivity();
		
		//data to graph (either a single planet or the entire list)
		ArrayList<Planet> graphList = mainActivityInstance.getList();
		
		//whole list for finding maximum values as well as attribute to display
		//if graphing across whole list
		ArrayList<Planet> mainList = mainActivityInstance.getMain();
		String attributeKey = mainActivityInstance.getAttribute();
	
		public PlanetGraphView(Context context) {
			super(context);
		}
		
		//these next four methods used for scaling our bar graphs based on max value for attributes 
		public Double getMaxDistance (ArrayList<Planet> planetList) {
			Double maxDist = planetList.get(0).getDistanceFromSun();
			for (Planet p: planetList) {
				if (p.getDistanceFromSun() > maxDist)
					maxDist = p.getDistanceFromSun();
			}
			return maxDist;			
		}
		
		public Double getMaxSize (ArrayList<Planet> planetList) {
			Double maxSize = planetList.get(0).getSize();
			for (Planet p: planetList) {
				if (p.getSize() > maxSize)
					maxSize = p.getSize();
			}
			return maxSize;			
		}
		
		public Double getMaxTemp (ArrayList<Planet> planetList) {
			Double maxTemp = planetList.get(0).getAverageTemperature();
			for (Planet p: planetList) {
				if (p.getAverageTemperature() > maxTemp)
					maxTemp = p.getAverageTemperature();
			}
			return maxTemp;			
		}
		
		public Integer getMaxMoons (ArrayList<Planet> planetList) {
			Integer maxMoons = planetList.get(0).getMoons();
			for (Planet p: planetList) {
				if (p.getMoons() > maxMoons)
					maxMoons = p.getMoons();
			}
			return maxMoons;			
		}
		//
		
		
		public void drawAttributes(Canvas canvas) {
				
				//take maximums for scaling (always use full list even if only single planet passed)
				Double maxTemp = getMaxTemp(mainList);
				Double maxDist = getMaxDistance(mainList);
				Double maxSize = getMaxSize(mainList);
				Double maxMoons = getMaxMoons(mainList).doubleValue();
				
				//******************************************************
				if ((this.graphList!=null) && (this.graphList.size()==1)){ //graphing all numerics of a single planet
					Paint rectPaint = new Paint();
					rectPaint.setColor(Color.GREEN);
					rectPaint.setStrokeWidth(12);
					rectPaint.setStyle(Style.FILL_AND_STROKE);
					
					//circle for graphing rings (boolean) 
					Paint circlePaint = new Paint();
					circlePaint.setColor(Color.BLUE); //blue cirlce --> rings = true (change color depending on value)
					circlePaint.setStrokeWidth(4);
					circlePaint.setStyle(Style.FILL_AND_STROKE);	
					circlePaint.setAntiAlias(true);
					
					//variables to enter: (for this single planet in the list)                
					Double scaledDistance = (graphList.get(0).getDistanceFromSun()/maxDist) * (height/6);
					Double scaledTemp = (graphList.get(0).getAverageTemperature()/maxTemp) * (height/6);
					Double scaledSize = (graphList.get(0).getSize()/maxSize) * (height/6);
					Double scaledMoons = (graphList.get(0).getMoons().doubleValue()/maxMoons) * (height/6);
					
					//red circle for false
					if (graphList.get(0).hasRings()==false) circlePaint.setColor(Color.RED);
					
					RectF rf1 = new RectF((width/18), 0, 
							width/10, scaledDistance.intValue());				
					
					RectF rf2 = new RectF((width/18)+width/5, 0, 
							(width/5)+width/10, scaledSize.intValue());
					
					RectF rf3 = new RectF((width/18)+(width*2)/5, 0, 
							((width*2)/5)+width/10, scaledTemp.intValue());	
					
					RectF rf4 = new RectF((width/18)+(width*3)/5, 0, 
							((width*3)/5)+width/10, scaledMoons.intValue());	
					
					canvas.drawRect(rf1, rectPaint);
					canvas.drawRect(rf2, rectPaint);
					canvas.drawRect(rf3, rectPaint);
					canvas.drawRect(rf4, rectPaint);
					canvas.drawCircle((float) ((width*4)/4.75), 
							(float) (height/13), 
							width/20, 
							circlePaint);							
					
					Paint textPaint = new Paint();
					textPaint.setColor(Color.WHITE);
					textPaint.setTypeface(Typeface.SANS_SERIF);
					textPaint.setAntiAlias(true);
					textPaint.setTextSize(20);
					
					//labels (using height and width to draw below in relation to graph)					
					canvas.drawText(""+graphList.get(0).getDistanceFromSun(),(width/18)-(width/80), 
							scaledDistance.intValue()+(height/10), 
							 textPaint);
					
					canvas.drawText(""+graphList.get(0).getSize(), 
							(width/18)+width/5-(width/80), 
							scaledSize.intValue()+height/10, textPaint 
							);
						
					canvas.drawText(""+graphList.get(0).getAverageTemperature(), 
							(width/18)+(width*2)/5-(width/80), 
							scaledTemp.intValue()+height/10, textPaint 
							);
					
					canvas.drawText(""+graphList.get(0).getMoons(), 
							(width/18)+(width*3)/5, 
							scaledMoons.intValue()+height/10, textPaint 
							);
					
					canvas.drawText("Rings: "+graphList.get(0).hasRings(), 
							(float) ((width*4)/5), 
							(height/15)+(width/10), textPaint 
							);
				
				}
				
				//********************************************************************************
				//drawing the attribute across the list since multiple planets were passed to graph
				else if ((this.graphList!=null) && (this.graphList.size()>1)){				
					
					RectF rf1;
					
					Paint rectPaint = new Paint();
					rectPaint.setColor(Color.GREEN);
					rectPaint.setStrokeWidth(12);
					rectPaint.setStyle(Style.FILL_AND_STROKE);
					
					Paint textPaint = new Paint();
					textPaint.setColor(Color.WHITE);
					textPaint.setTypeface(Typeface.SANS_SERIF);
					textPaint.setAntiAlias(true);
					textPaint.setTextSize(20);
					
					//scale is the size of each individual bar given the width of the view and the #planets
					int scale = (width/(graphList.size()*3));
					
					//the variable holding the desired attribute
					Double graphVar = null;
					
					//make the number of bars correspond to planets in our list
					for (int i=0; i<graphList.size(); i++) {
						Double scaledDistance = (graphList.get(i).getDistanceFromSun()/maxDist) * (height/6);
						Double scaledTemp = (graphList.get(i).getAverageTemperature()/maxTemp) * (height/6);
						Double scaledSize = (graphList.get(i).getSize()/maxSize) * (height/6);
						Double scaledMoons = (graphList.get(i).getMoons().doubleValue()/maxMoons) * (height/6);
						
						if (attributeKey.equals("DISTANCE")) graphVar = scaledDistance;
						else if (attributeKey.equals("MOONS")) graphVar = scaledMoons;
						else if (attributeKey.equals("SIZE")) graphVar = scaledSize;
						else graphVar = scaledTemp;
						
							rf1 = new RectF(scale+ i*(width/graphList.size()), 0, 
									2*scale+ i*(width/graphList.size()), graphVar.intValue());							
											
						canvas.drawRect(rf1, rectPaint);
						
						//label the bar by order and have it correspond in list
						canvas.drawText(""+(i+1),scale+ i*(width/graphList.size()) - width/160, 
								graphVar.intValue()+(height/10), 
								 textPaint);
					}				
				}			
			}
			@Override
			/**
			 *  This view has 3 main components:
			 *   - circles
			 *   - messages
			 */
			protected void onDraw(Canvas canvas) {
				//get whatever we are graphing (could be a single planet, 
				//in which case every attribute is shown; alternatively could be 
				//any one of the three numeric attributes graphed across the list
				graphList = mainActivityInstance.getList();
				
				super.onDraw(canvas);
				drawAttributes(canvas);	
				

			}
			
			/**
			 * This method is called by the Android platform when the app window size changes.
			 * We store the initial setting of these so that we can compute the exact locations
			 * to draw the components of our View.
			 */
			/*
			protected void onSizeChanged(int w, int h, int oldw, int oldh) {
				super.onSizeChanged(w, h, oldw, oldh);

				width = w;
				height = h;
			}
*/
}
