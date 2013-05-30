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

package com.orangelabs.rcs.service.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gsma.joyn.capability.Capabilities;
import org.gsma.joyn.contacts.IContactsService;
import org.gsma.joyn.contacts.JoynContact;

import com.orangelabs.rcs.provider.eab.ContactsManager;
import com.orangelabs.rcs.service.api.client.contacts.ContactInfo;
import com.orangelabs.rcs.service.api.server.ServerApiException;
import com.orangelabs.rcs.utils.logger.Logger;

/**
 * Contacts service API implementation
 * 
 * @author Jean-Marc AUFFRET
 */
public class ContactsServiceImpl extends IContactsService.Stub {
    /**
	 * The logger
	 */
	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Constructor
	 */
	public ContactsServiceImpl() {
		if (logger.isActivated()) {
			logger.info("Contacts service API is loaded");
		}
	}

	/**
	 * Close API
	 */
	public void close() {
	}
    
    /**
     * Returns the list of joyn contacts
     * 
     * @return List of contacts
     * @throws ServerApiException
     */
    public List<JoynContact> getJoynContacts() throws ServerApiException {
		if (logger.isActivated()) {
			logger.info("Get joyn contacts");
		}

		// Read capabilities in the local database
		List<String> contacts = ContactsManager.getInstance().getRcsContacts();
		ArrayList<JoynContact> result = new ArrayList<JoynContact>(contacts.size());
		for(int i =0; i < contacts.size(); i++) {
			String contact = contacts.get(i);
			ContactInfo contactInfo = ContactsManager.getInstance().getContactInfo(contact);
			com.orangelabs.rcs.core.ims.service.capability.Capabilities capabilities = contactInfo.getCapabilities();
			Capabilities capaApi = null;
			if (capabilities != null) {
	    		Set<String> exts = new HashSet<String>(capabilities.getSupportedExtensions());
				capaApi = new Capabilities(
	    				capabilities.isImageSharingSupported(),
	    				capabilities.isVideoSharingSupported(),
	    				capabilities.isImSessionSupported(),
	    				capabilities.isFileTransferSupported(),
	    				exts); 
			}
			boolean registered = (contactInfo.getRegistrationState() == ContactInfo.REGISTRATION_STATUS_ONLINE);
			result.add(new JoynContact(contact, registered, capaApi));
		}
		
		return result;
	}

    /**
     * Returns the list of online contacts (i.e. registered)
     * 
     * @return List of contacts
     * @throws ServerApiException
     */
    public List<JoynContact> getJoynContactsOnline() throws ServerApiException {
		if (logger.isActivated()) {
			logger.info("Get registered joyn contacts");
		}

		// Read capabilities in the local database
		List<String> contacts = ContactsManager.getInstance().getRcsContacts();
		ArrayList<JoynContact> result = new ArrayList<JoynContact>(contacts.size());
		for(int i =0; i < contacts.size(); i++) {
			String contact = contacts.get(i);
			ContactInfo contactInfo = ContactsManager.getInstance().getContactInfo(contact);
			com.orangelabs.rcs.core.ims.service.capability.Capabilities capabilities = contactInfo.getCapabilities();
			if (contactInfo.getRegistrationState() == ContactInfo.REGISTRATION_STATUS_ONLINE) {			
				Capabilities capaApi = null;
				if (capabilities != null) {
		    		Set<String> exts = new HashSet<String>(capabilities.getSupportedExtensions());
					capaApi = new Capabilities(
		    				capabilities.isImageSharingSupported(),
		    				capabilities.isVideoSharingSupported(),
		    				capabilities.isImSessionSupported(),
		    				capabilities.isFileTransferSupported(),
		    				exts); 
				}
				result.add(new JoynContact(contact, true, capaApi));
			}
		}
		
		return result;
	}
    
    /**
     * Returns the list of contacts supporting a given extension (i.e. feature tag)
     * 
     * @return List of contacts
     * @throws ServerApiException
     */
    public List<JoynContact> getJoynContactsSupporting(String tag) throws ServerApiException {
		if (logger.isActivated()) {
			logger.info("Get registered joyn contacts");
		}

		// Read capabilities in the local database
		List<String> contacts = ContactsManager.getInstance().getRcsContacts();
		ArrayList<JoynContact> result = new ArrayList<JoynContact>(contacts.size());
		for(int i =0; i < contacts.size(); i++) {
			String contact = contacts.get(i);
			ContactInfo contactInfo = ContactsManager.getInstance().getContactInfo(contact);
			com.orangelabs.rcs.core.ims.service.capability.Capabilities capabilities = contactInfo.getCapabilities();
			Capabilities capaApi = null;
			if (capabilities != null) {
				if (capabilities.getSupportedExtensions().contains(tag)) {
		    		Set<String> exts = new HashSet<String>(capabilities.getSupportedExtensions());
					capaApi = new Capabilities(
		    				capabilities.isImageSharingSupported(),
		    				capabilities.isVideoSharingSupported(),
		    				capabilities.isImSessionSupported(),
		    				capabilities.isFileTransferSupported(),
		    				exts); 
					result.add(new JoynContact(contact, true, capaApi));
				}
			}
		}
		
		return result;
    }    
}