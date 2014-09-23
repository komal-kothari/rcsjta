/*******************************************************************************
 * Software Name : RCS IMS Stack
 *
 * Copyright (C) 2010 France Telecom S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.orangelabs.rcs.ri.capabilities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gsma.services.rcs.JoynServiceException;
import com.gsma.services.rcs.JoynServiceNotAvailableException;
import com.gsma.services.rcs.capability.Capabilities;
import com.orangelabs.rcs.ri.ApiConnectionManager;
import com.orangelabs.rcs.ri.ApiConnectionManager.RcsService;
import com.orangelabs.rcs.ri.R;
import com.orangelabs.rcs.ri.utils.LockAccess;
import com.orangelabs.rcs.ri.utils.Utils;

/**
 * My capabilities
 */
public class MyCapabilities extends Activity {

  	/**
	 * API connection manager
	 */
	private ApiConnectionManager connectionManager;
	
	/**
   	 * A locker to exit only once
   	 */
   	private LockAccess exitOnce = new LockAccess();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set layout
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.capabilities_mine);
        
        // Set title
        setTitle(R.string.menu_my_capabilities);
        
		// Register to API connection manager
		connectionManager = ApiConnectionManager.getInstance(this);
		if (connectionManager == null || !connectionManager.isServiceConnected(RcsService.CAPABILITY)) {
			Utils.showMessageAndExit(this, getString(R.string.label_service_not_available), exitOnce);
			return;
		}
		connectionManager.startMonitorServices(this, null, RcsService.CAPABILITY);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	if (connectionManager != null) {
			connectionManager.stopMonitorServices(this);
    	}
    }
    
    @Override
	protected void onResume() {
    	super.onResume();
    	try {
    		// Get the current capabilities from the RCS contacts API
        	Capabilities capabilities = connectionManager.getCapabilityApi().getMyCapabilities();
	    	
	    	// Set capabilities
	        CheckBox imageCSh = (CheckBox)findViewById(R.id.image_sharing);
	        imageCSh.setChecked(capabilities.isImageSharingSupported());
	        CheckBox videoCSh = (CheckBox)findViewById(R.id.video_sharing);
	        videoCSh.setChecked(capabilities.isVideoSharingSupported());
	        CheckBox ft = (CheckBox)findViewById(R.id.file_transfer);
	        ft.setChecked(capabilities.isFileTransferSupported());
	        CheckBox im = (CheckBox)findViewById(R.id.im);
	        im.setChecked(capabilities.isImSessionSupported());
	        CheckBox geolocationPush = (CheckBox)findViewById(R.id.geoloc_push);
	        geolocationPush.setChecked(capabilities.isGeolocPushSupported());
	        CheckBox ipVoiceCall = (CheckBox)findViewById(R.id.ip_voice_call);
	        ipVoiceCall.setChecked(capabilities.isIPVoiceCallSupported());
	        CheckBox ipVideoCall = (CheckBox)findViewById(R.id.ip_video_call);
	        ipVideoCall.setChecked(capabilities.isIPVideoCallSupported());
	        
	        // Set extensions
	        TextView extensions = (TextView)findViewById(R.id.extensions);
	        extensions.setText(RequestCapabilities.getExtensions(capabilities));
	        
	        // Set automata
	        CheckBox automata = (CheckBox)findViewById(R.id.automata);
	        automata.setChecked(capabilities.isAutomata());
	    } catch(JoynServiceNotAvailableException e) {
			Utils.showMessageAndExit(this, getString(R.string.label_api_disabled), exitOnce);
	    } catch(JoynServiceException e) {
			Utils.showMessageAndExit(this, getString(R.string.label_api_failed), exitOnce);
	    }
    }

    
    
}
