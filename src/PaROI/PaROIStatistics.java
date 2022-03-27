
package PaROI;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @author Andrey Dashkov
 * The class to calculate the ROI statistics. Can calculate average and median values, overlapping, creates the map of ROI positions.
 * It operates on the base of ROI file, a ROI file is source of data for this class.
 */
public class PaROIStatistics {

	/**
	 * 
	 */
	public PaROIStatistics() {}
	
	
	public class OverlapData {
		
		public Rectangle rec1;
		
		public Rectangle rec2;
		
		public float overlap;//percent
		
	}
	

	
	/**
	 * Statistic data for a single type of ROI
	 */
	public class TypesStatData {
		
		public int number = 0;
		
		public double averaged_aspect_ratio = 0.0;
		
		public Dimension averaged_size = null;
		
		public Dimension max_size = null;
		
		public Dimension min_size = null;
		
		public ArrayList<Double> ratio_list = new ArrayList<Double>();
		
		public ArrayList<Integer> width_list = new ArrayList<Integer>();
		
		public ArrayList<Integer> height_list = new ArrayList<Integer>();
		
		public double median_aspect_ratio = 0.0;
		
		public Dimension median_size = null;
		
		
		//first list contains arrays appropriative for every type ; size of list == number of types
		//second inner list contains all overlap cases for definite type
		public ArrayList<ArrayList<OverlapData> > overlap_data_list = null;  
		
		//results for overlaping
		public ArrayList<Double> result_averaged_overlap_data_list = null;
		
		public ArrayList<Double> result_median_overlap_data_list = null;
		
		//key - the number of horizontal and vertical cells into an image, value - number of ROIs which intersect with this sector
		public HashMap<Dimension,Integer> position_stat_inside_image_list =null;
		
	}
	
	
	public  TypesStatData createNewTypesData(Dimension grid, int typesNumber)
	{
		
		TypesStatData t = new TypesStatData();
		
		t.averaged_size = new Dimension(0,0);
		
		t.min_size = new Dimension(10000000, 10000000);
		
		t.max_size = new Dimension(0, 0);
		
		t.overlap_data_list = new ArrayList<ArrayList<OverlapData>>();
		
		t.result_averaged_overlap_data_list = new ArrayList<Double>();
		
		t.result_median_overlap_data_list = new ArrayList<Double>();
		
		for(int i = 0; i < typesNumber; i++) {
			
			t.overlap_data_list.add(new ArrayList<OverlapData>());
			
			t.result_averaged_overlap_data_list.add(0.0);
			
			t.result_median_overlap_data_list.add(0.0);
		}
		
		t.position_stat_inside_image_list = new HashMap<Dimension,Integer>();
		
		for(int x = 0; x < grid.width; x++) {
			
			for(int y = 0; y < grid.height; y++) {
			
				t.position_stat_inside_image_list.put(new Dimension(x,y), 0);
			}
		}
		
		return t;
	}
	
	
	public ArrayList<TypesStatData> calculateStatistics(HashMap<String,ArrayList<PaRectangle>> roi_map, 
			ArrayList<String> typesList, Dimension grid, HashMap<String,Dimension> images_sizes_map)
	{
		
		ArrayList<TypesStatData> statList = new ArrayList<TypesStatData>();
		
		//extra type for "all types"
		int typesNumber = typesList.size() + 1;
		
		for(int i = 0; i < typesNumber; i++) {
			
			statList.add( createNewTypesData(grid, typesNumber) );
		}
		
		
		for (Entry<String, ArrayList<PaRectangle>> entry : roi_map.entrySet()) {
			
			ArrayList<PaRectangle> list =  entry.getValue();
			
			Dimension image_size = images_sizes_map.get(entry.getKey());
			
			if(image_size != null) {
			
				for(int i =0; i < list.size(); ++i) {
					
					PaRectangle rec = list.get(i);
					
					double ratio = rec.w/(double)rec.h;
				
					
					int index = typesList.indexOf(rec.type); 
					
					if(index != -1) {
						
						TypesStatData dt = statList.get(index + 1); 
						
						 dt.number += 1;
						 
						 dt.averaged_aspect_ratio += ratio; 
						 
						 dt.averaged_size.width += rec.w;
						 
						 dt.averaged_size.height += rec.h;
						 
						 dt.ratio_list.add(ratio);
						 
						 dt.width_list.add(rec.w);
						 
						 dt.height_list.add(rec.h);
						 
						 if(dt.max_size.width < rec.w ) { dt.max_size.width = rec.w; }
						 
						 if(dt.max_size.height < rec.h ) { dt.max_size.height = rec.h; }
						 
						 if(dt.min_size.width > rec.w ) { dt.min_size.width = rec.w; }
						 
						 if(dt.min_size.height > rec.h ) { dt.min_size.height = rec.h; }
						 
						 markPositionForROI(rec , image_size, grid, dt);
						 
					}
					
					TypesStatData dt0 = statList.get(0); 
					
					 dt0.number += 1;
					 
					 dt0.averaged_aspect_ratio += ratio; 
					 
					 dt0.averaged_size.width += rec.w;
					 
					 dt0.averaged_size.height += rec.h;
					 
					 dt0.ratio_list.add(ratio);
					 
					 dt0.width_list.add(rec.w);
					 
					 dt0.height_list.add(rec.h);
					 
					 if(dt0.max_size.width < rec.w ) { dt0.max_size.width = rec.w; }
					 
					 if(dt0.max_size.height < rec.h ) { dt0.max_size.height = rec.h; }
					 
					 if(dt0.min_size.width > rec.w ) { dt0.min_size.width = rec.w; }
					 
					 if(dt0.min_size.height > rec.h ) { dt0.min_size.height = rec.h; }
					 
					 markPositionForROI(rec , image_size, grid, dt0);
					 
					 collectOverlapData(rec, list,  typesList, statList);
					
				
				}  
			}
		}
		
		
		
		//zero index here is for "all types"
		for(int i = 0; i < (typesList.size() + 1); i++) {
			
			 TypesStatData dt = statList.get(i);
			 
			 //fix for the case when there are now ROIs for the type
			 if(dt.min_size.width == 10000000) {
				 
				 dt.min_size.width = 0;
				 
				 dt.min_size.height = 0;
				 
			 }

			 if(dt.number != 0) {
				 
				 dt.averaged_aspect_ratio /=  dt.number;
				 
				 dt.averaged_aspect_ratio = ((int)(dt.averaged_aspect_ratio*10000))/10000d;
			
				 
				 dt.averaged_size.width = dt.averaged_size.width/dt.number;
				 
				 dt.averaged_size.height = dt.averaged_size.height/dt.number;
			 }
			 	 
			 dt.median_aspect_ratio = 0.0;
			 
			 if(!dt.ratio_list.isEmpty()) {
				 
				 Collections.sort(dt.ratio_list);
				 
				 dt.median_aspect_ratio = dt.ratio_list.get(dt.ratio_list.size()/2);
				 
				 dt.median_aspect_ratio = ((int)(dt.median_aspect_ratio*10000))/10000d;
			 }
			 
			 dt.median_size = new Dimension(0,0);
			 
			 if(!dt.width_list.isEmpty() && !dt.height_list.isEmpty()) {
				 
				 Collections.sort(dt.width_list);
				 
				 Collections.sort(dt.height_list);
				 
				 int median_width = dt.width_list.get(dt.width_list.size()/2);
				 
				 int median_height = dt.height_list.get(dt.height_list.size()/2);
				 
				 dt.median_size = new Dimension(median_width, median_height);
			 }
			 
			 int type_size_for_calc = 1;
			 
			 if(i != 0) {
				 type_size_for_calc = dt.result_averaged_overlap_data_list.size();
			 }
		
			 
			 for(int j = 0; j < type_size_for_calc; ++j) {
				 
				 dt.result_averaged_overlap_data_list.set(j, 0.0);
				
				 dt.result_median_overlap_data_list.set(j, 0.0);
				 
				 ArrayList<OverlapData> overlap_data = dt.overlap_data_list.get(j);
				 
				 ArrayList<Double> tmp = new ArrayList<Double>();
				 
				 double sum_overlap = 0.0;
				 
				 for(int k =0; k < overlap_data.size(); ++k) {
					 
					 double overlap = (double) overlap_data.get(k).overlap;
					 
					 tmp.add(overlap);
					 
					 sum_overlap += overlap;
				 }
				 
				 if(tmp.size() != 0) {
					 
					 double v = ((int)((sum_overlap/tmp.size())*10000))/10000d;
				 
					 dt.result_averaged_overlap_data_list.set(j, v);
					 
					 Collections.sort(tmp);
					 
					 v = ((int)((tmp.get(tmp.size()/2))*10000))/10000d;
					 
					 dt.result_median_overlap_data_list.set(j, v);
				 }
			 }
		}
		
		return statList;
	}
	
	
	/**
	 * Collects the overlap information for all types, including mutual overlapping.
	 * @param input_rec - the input ROI to find the overlap with
	 * @param list - list of other ROIs in the same image
	 * @param typesList - list of all ROI types
	 * @param statList - the main container to collect the overlap information
	 * Explanation of the overlap data structure:
	 * the main list is ArrayList<TypesStatData> statList; it contains the number of items == (number of types + 1); extra
	 * type with index == 0 is reserved for "ALL types".
	 * The type TypesStatData contains the container ArrayList<ArrayList<OverlapData> > overlap_data_list which is also
	 * has the size  == (number of types + 1) So such structure helps to keep the overlap information mutual for all types
	 */
	 private void collectOverlapData(PaRectangle input_rec, ArrayList<PaRectangle> list, ArrayList<String> typesList,
			 ArrayList<TypesStatData> statList)
	 {
		
		 ArrayList<OverlapData> overlap_ALL_list = statList.get(0).overlap_data_list.get(0);
		 
		 Rectangle rec = new Rectangle(input_rec.x, input_rec.y, input_rec.w,input_rec.h);
		 
		 for(int i = 0; i < list.size(); ++i) {
			 
			 PaRectangle in_rec1 = list.get(i);
			 
			 Rectangle rec1 = new Rectangle(in_rec1.x, in_rec1.y, in_rec1.w, in_rec1.h);
			 
			 if( !rec1.equals(rec) ) {
				 
				 Rectangle ints = rec.intersection(rec1);
				 
				 if(!ints.isEmpty()) {
					 
					 float d = ((float)(ints.width*ints.height))/(rec1.width*(float)rec1.height + rec.width*rec.height - ints.width*ints.height);
				 
					 OverlapData dt = new OverlapData();
					 
					 dt.rec1 = rec;
					 
					 dt.rec2 = rec1;
					 
					 dt.overlap = d;
								 
					 if (typesList.size() > 0) {
						 
						int index  = typesList.indexOf(input_rec.type) + 1;//because 0 index is "all types"
						
						if (index != - 1) {
							
							int inner_index  = typesList.indexOf(in_rec1.type) + 1;//because 0 index is "all types"
							
							if (inner_index != -1) {
								
								ArrayList<OverlapData> overlap_FOR_SPEC_TYPE_list  = 
										statList.get(index).overlap_data_list.get(inner_index);
								
								overlap_FOR_SPEC_TYPE_list.add(dt);
							}
							
							//overlap of type with 'index' with all other types
							statList.get(index).overlap_data_list.get(0).add(dt);
						}
						
						
					 }
					 
					 //all intersections, all types with all types
					 overlap_ALL_list.add(dt);
					 
				 }
			 }
			 
		 }
		 
	 }
	
	/**
	 * @param grid - contains number of cell in horizontal direction (grid.width) and vertical direction (grid.height)
	 */
	 private void markPositionForROI(PaRectangle rec, Dimension  image_size, Dimension grid, TypesStatData dt )
	 {
		 int gridWidth = (int) Math.ceil(image_size.width/(float)grid.width);
		 
		 int gridHeight = (int) Math.ceil(image_size.height/(float)grid.height);
		 
		 int xCellStartIndex = rec.x/gridWidth;
		 
		 int xCellEndIndex = (rec.x + rec.w)/gridWidth;
		 
		 int yCellStartIndex = rec.y/gridHeight;
		 
		 int yCellEndIndex = (rec.y + rec.h)/gridHeight;
		 
		 if(xCellEndIndex >= grid.width) { xCellEndIndex = grid.width - 1; }
		 
		 if(yCellEndIndex >= grid.height) { yCellEndIndex = grid.height - 1; }
		 
		 if(xCellStartIndex < 0) { xCellStartIndex = 0; }
		 
		 if(yCellStartIndex < 0) { yCellStartIndex = 0; }
		 
		 for(int x = xCellStartIndex; x <= xCellEndIndex; ++x) {
			 
			 for(int y = yCellStartIndex; y <= yCellEndIndex; ++y) {
				 
				// try {	 
				 Dimension key = new Dimension(x, y);
				 
				 int num = dt.position_stat_inside_image_list.get(key) + 1;
					 
				 dt.position_stat_inside_image_list.put(key, num);
					 		
			 }
			  
		 }
	 }
}
