package com.example.locactivity;

/*Class: ShowHistoryMap
 * 
 * The class is for displaying map with the user location.
 * showHistoryMap class is loaded when user clicks a particular item from the history list. 
 * showHistoryMap class is started from onListItemClick function of LocationHistoryFragment class. 
 * latitude and longitude values are bundled when ShowHistoryMap is invoked.
 * shoHistoryMap process the lat/long values using GeoCoder and map is displayed
 * 
 * Map is shown in activity_loc layout(same layout used to show current user location).
 * 
 * This class is similar to MainMapActivity class except that listener is not implemented(UpdateUILocation renamed to showMap). 
 * Please refer MainActicity class for details on functions. 
 * 
 *
 *NOTE:- A bug was found when this class was implemented in AsyncTask. User needs to tap the screen once to display 
 *the location marker. This bug is not found when map is displayed from onCreate function. I have commented out the AsynTask.
 * 
 * 
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class ShowHistoryMap<E> extends MapActivity{

	double latitude;
	double longitude;
	List<Address> addresses = null;
	private ArrayList<ListItem> list = null;
	MapView mapView;
	private ProgressDialog progressDialog; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc);
        Bundle extras = getIntent().getExtras();
        
        if (extras !=null){
        	
        	latitude = extras.getDouble("latitude");
        	longitude = extras.getDouble("longitude");
        }
        
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        try {
			addresses = gcd.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		String address = addresses.get(0).getAddressLine(0);
		String city = addresses.get(0).getAddressLine(1);
		String country = addresses.get(0).getAddressLine(2);
		String countryCode = addresses.get(0).getCountryCode();
		Log.i("ShowHistoryMap", "onCreate: City: "+city);
		Log.i("ShowHistoryMap", "onCreate: Country: "+country);
		Log.i("ShowHistoryMap", "onCreate: Address: "+address);
		Log.i("ShowHistoryMap", "onCreate: Country Code: "+countryCode);
		
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setVisibility(View.VISIBLE);
	    mapView.setBuiltInZoomControls(true);
	    
	    
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    mapOverlays.clear();
	    Drawable drawable = getResources().getDrawable(R.drawable.pegman_launcher);

	    ItemsOverlay itemsoverlay = new ItemsOverlay(drawable, this);
	    
	    GeoPoint point = new GeoPoint((int)(latitude*1e6),(int)(longitude*1e6));
	    OverlayItem overlayitem = new OverlayItem(point, address, city+", "+countryCode);
	    MapController mapController = mapView.getController();
	    mapController.setCenter(point);
	    itemsoverlay.addOverlay(overlayitem);
	    
	    //show marker for all entries in db
	    getLocList();
	    ListItem l;
	    Iterator<ListItem> it = list.iterator();
	    while(it.hasNext()){
	    	l = it.next();
	    	point = new GeoPoint((int)(Double.valueOf(l.getLatitude().trim()).doubleValue()*1e6),(int)(Double.valueOf(l.getLongitude().trim()).doubleValue()*1e6));
	    	
	    	try {
				addresses = gcd.getFromLocation(Double.valueOf(l.getLatitude().trim()).doubleValue(),Double.valueOf(l.getLongitude().trim()).doubleValue(), 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			address = addresses.get(0).getAddressLine(0);
			city = addresses.get(0).getAddressLine(1);
			country = addresses.get(0).getAddressLine(2);
			countryCode = addresses.get(0).getCountryCode();
			
			overlayitem = new OverlayItem(point, address, city+", "+countryCode);
		    overlayitem.setMarker(drawable);
		    itemsoverlay.addOverlay(overlayitem);
		    Log.i("ShowHistoryMap", "showMap "+list.size());
		    Log.i("ShowHistoryMap", "onCreate "+countryCode);
	    }
	    
	    
	    mapOverlays.add(itemsoverlay);
	    
/*	    ShowMapAsyncTask asyncTask = new ShowMapAsyncTask(this);
	    asyncTask.execute();*/
    }
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.i("ShowHistoryMap","onPause:");
	}

	@Override 
	public void onResume(){
		super.onResume();
		Log.i("ShowHistoryMap","onResume:");
	}
	
	@Override 
	public void onDestroy(){
		super.onDestroy();
		Log.i("ShowHistoryMap","onDestroy:");
	}
	
	
/*private class ShowMapAsyncTask extends AsyncTask<Void, Void, Void>{
	Context asyncContext;
	
	public ShowMapAsyncTask(Context context){
		asyncContext = context;
	}
	
	@Override
	    protected void onPreExecute() {
		progressDialog = ProgressDialog.show(asyncContext,"Please wait...", "Showing map...", true);
	    }

	@Override
	protected Void doInBackground(Void... params) {
		Geocoder gcd = new Geocoder(asyncContext, Locale.getDefault());
        try {
			addresses = gcd.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result){
		showMap();
		 progressDialog.dismiss();
	}
	
	private void showMap(){
		String address = addresses.get(0).getAddressLine(0);
		String city = addresses.get(0).getAddressLine(1);
		String country = addresses.get(0).getAddressLine(2);
		String countryCode = addresses.get(0).getCountryCode();
		Log.i("ShowHistoryMap", "showMap: City: "+city);
		Log.i("ShowHistoryMap", "showMap: Country: "+country);
		Log.i("ShowHistoryMap", "showMap: Address: "+address);
		Log.i("ShowHistoryMap", "showMap: Country Code: "+countryCode);
		
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setVisibility(View.VISIBLE);
	    mapView.setBuiltInZoomControls(true);
	    
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    mapOverlays.clear();
	    Drawable drawable = asyncContext.getResources().getDrawable(R.drawable.pegman_launcher);

	    ItemsOverlay itemsoverlay = new ItemsOverlay(drawable, asyncContext);
	    
	    GeoPoint point = new GeoPoint((int)(latitude*1e6),(int)(longitude*1e6));
	    OverlayItem overlayitem = new OverlayItem(point, address, city+", "+countryCode);
	    overlayitem.setMarker(drawable);
		MapController mapController = mapView.getController();
		mapController.setCenter(point);
		itemsoverlay.addOverlay(overlayitem);
		Log.i("ShowHistoryMap", "showMap ");
	    getLocList();
	    ListItem l;
	    Log.i("ShowHistoryMap", "showMap "+list.size());
	    while(list.iterator().hasNext()){
	    	l = list.iterator().next();
	    	point = new GeoPoint((int)(Double.valueOf(l.getLatitude().trim()).doubleValue()*1e6),(int)(Double.valueOf(l.getLongitude().trim()).doubleValue()*1e6));
		    overlayitem = new OverlayItem(point, l.getCity(), countryCode);
		    overlayitem.setMarker(drawable);
		    itemsoverlay.addOverlay(overlayitem);
		    Log.i("ShowHistoryMap", "showMap "+countryCode);
	    }
	    
	    
	    mapOverlays.add(itemsoverlay);
	}
  
	
}*/

/*Function: getLocHist 
 * Reads the location details from db "GeoTrack"  and sets value in the ListItem list.
 * Inorder to preserve the cronological order while viewing the list, a list reversal is done. 
 * Finally, a thread is started to check for any change in the list. 
 */
private void getLocList(){
	
	list = new ArrayList<ListItem>();
	ListItem l;
	
    try {
		//read GeoTracker database
    	DatabaseHandler db = new DatabaseHandler(this, "GeoTrack", null, 9);
    	Cursor cursor = db.readDB();
    	Log.i("ShowHistoryMap","getLocHist:");
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	l= new ListItem();
            	l.setCity(cursor.getString(3)+","+cursor.getString(4));
            	l.setDate(cursor.getString(5));
            	l.setLatitude(String.valueOf(cursor.getDouble(1)));
            	l.setLongitude(String.valueOf(cursor.getDouble(2)));
            	list.add(l);
            	Log.i("ShowHistoryMap","getLocHist:"+cursor.getInt(0));
            	Log.i("ShowHistoryMap","getLocList: Retreiving History: "+l.getCity()+", "+l.getLatitude()+", "+l.getLongitude()+", "+l.getDate());
            } while (cursor.moveToNext());
        }
    	db.close();
    }catch (Exception e) {
    	e.printStackTrace();
    } 
    Collections.reverse(list);
    
}


}


